package dev.ichinomiya.fastlang;

import net.minecraftforge.fml.common.DummyModContainer;
import net.minecraftforge.fml.common.ModMetadata;

public class Container extends DummyModContainer {
    public Container() {
        super(new ModMetadata());
        ModMetadata meta = this.getMetadata();
        meta.modId = "fastlang";
        meta.name = "FastLang";
        meta.version = "1.0";
        meta.authorList.add("Ichinomiya Shirona");
    }
}
