package dev.ichinomiya.fastlang;

import dev.ichinomiya.fastlang.transformer.GuiLanguageTransformer;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
public class Loader implements IFMLLoadingPlugin {
    @Override
    public String[] getASMTransformerClass() {
        List<String> transformers = new ArrayList<>();
        transformers.add(GuiLanguageTransformer.class.getName());
        return transformers.toArray(new String[0]);
    }

    @Override
    public String getModContainerClass() {
        return Container.class.getName();
    }

    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {

    }

    @Override
    public String getAccessTransformerClass() {
        return null;
    }
}
