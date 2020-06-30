package de.shyrik.modularitemframe.api.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.util.ResourceLocation;

import java.util.function.Function;

public class RandomUtils {
    private static Function<ResourceLocation, TextureAtlasSprite> func;
    public static TextureAtlasSprite getSprite(ResourceLocation textureLocation) {
        if (func == null)
            func = Minecraft.getInstance().getAtlasSpriteGetter(PlayerContainer.LOCATION_BLOCKS_TEXTURE);

        return func.apply(textureLocation);
    }
}
