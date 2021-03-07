package modularitemframe.api;

import net.minecraft.util.ResourceLocation;

public enum ModuleTier {
    T1(new ResourceLocation("modularitemframe", "block/default_inner")),
    T2(new ResourceLocation("modularitemframe", "block/hard_inner")),
    T3(new ResourceLocation("modularitemframe", "block/hardest_inner"));

    public final ResourceLocation innerTex;

    ModuleTier(ResourceLocation innerTex) {
        this.innerTex = innerTex;
    }
}
