package dev.ichinomiya.fastlang.transformer;

import net.minecraft.client.Minecraft;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import net.minecraft.launchwrapper.IClassTransformer;

public class GuiLanguageTransformer implements IClassTransformer {
    private final static Logger logger = LogManager.getLogger(GuiLanguageTransformer.class);

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (transformedName.equals("net.minecraft.client.gui.GuiLanguage$List")) {
            ClassNode classNode = new ClassNode();
            ClassReader classReader = new ClassReader(basicClass);
            classReader.accept(classNode, 0);

            for (MethodNode method : classNode.methods) {
                // Locate the method with the signature
                if (method.desc.equals("(IZII)V")) {
                    for (int i = 0; i < method.instructions.size(); i++) {
                        AbstractInsnNode instruction = method.instructions.get(i);

                        if (instruction instanceof MethodInsnNode) {
                            MethodInsnNode methodInsnNode = (MethodInsnNode) instruction;
                            // Remove Forge resource reload
                            if (methodInsnNode.owner.equals("net/minecraftforge/fml/client/FMLClientHandler")) {
                                method.instructions.remove(instruction);
                            }

                            // Replace Minecraft resource reload
                            if (instruction.getOpcode() == Opcodes.INVOKEVIRTUAL) {
                                if (methodInsnNode.desc.equals("()V")) {
                                    method.instructions.insertBefore(instruction, new InsnNode(Opcodes.POP));
                                    method.instructions.insertBefore(instruction, new MethodInsnNode(Opcodes.INVOKESTATIC, this.getClass().getName().replace(".", "/"), "reloadLanguageManager", "()V", false));
                                    method.instructions.remove(instruction);
                                }
                            }
                        }
                    }
                }
            }

            ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
            classNode.accept(classWriter);
            return classWriter.toByteArray();
        }
        return basicClass;
    }

    // Latest Optifine is using net.optifine.Lang, but older versions are using Lang
    private final static String[] optifineLangClassNames = {
            "net.optifine.Lang", 
            "Lang"};

    @SuppressWarnings("unused")
    public static void reloadLanguageManager() {
        Minecraft mc = Minecraft.getMinecraft();
        mc.getLanguageManager().onResourceManagerReload(mc.getResourceManager());

        // Optifine
        try {
            Class<?> optifineLangClass = null;
            
            for (String optifineLangClassName : optifineLangClassNames) {
                try {
                    optifineLangClass = Class.forName(optifineLangClassName);
                    break;
                } catch (ClassNotFoundException ignored) {
                }
            }

            if (optifineLangClass == null) {
                return;
            }

            optifineLangClass.getMethod("resourcesReloaded").invoke(null);
        } catch (Exception e) {
            logger.error("Failed to reload Optifine!", e);
        }
    }
}
