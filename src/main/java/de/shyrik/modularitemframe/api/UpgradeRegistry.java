package de.shyrik.modularitemframe.api;

import de.shyrik.modularitemframe.ModularItemFrame;
import de.shyrik.modularitemframe.common.item.ItemUpgrade;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class UpgradeRegistry {
    private static Map<ResourceLocation, Class<? extends UpgradeBase>> upgrades = new HashMap<>();

    /**
     * registers a upgrade class
     * @param id unique id
     * @param upgradeClass upgrade to register
     *
     * @throws IllegalArgumentException when id is duplicated
     */
    public static void register(ResourceLocation id, Class<? extends UpgradeBase> upgradeClass) {
        if (get(id).isPresent())
            throw new IllegalArgumentException("[ModularItemFrame] upgrade key already exists!");
        upgrades.put(id, upgradeClass);
    }

    /**
     * Registers the upgrade class and creates a new {@link ItemUpgrade} instance
     * @param id unique id
     * @param upgradeClass upgrade to register
     * @return new {@link ItemUpgrade} instance
     */
    public static ItemUpgrade registerCreate(ResourceLocation id, Class<? extends UpgradeBase> upgradeClass) {
        register(id, upgradeClass);
        return new ItemUpgrade(id);
    }

    /**
     * creates an instance of a upgrade
     * @param id the id of the upgrade
     * @return created instance
     */
    @Nullable
    public static UpgradeBase createUpgradeInstance(ResourceLocation id) {
        Optional<Class<? extends UpgradeBase>> up = get(id);
        if(up.isPresent()) {
            try {
                return up.get().newInstance();
            } catch (Exception ex) {
                return null;
            }
        }
        return null;
    }

    /**
     * Gets the upgrade id of a specific upgrade
     * @param upgradeClass upgrade to look up
     * @return unique upgrade id
     */
    public static ResourceLocation getUpgradeId(Class<? extends UpgradeBase> upgradeClass) {
        for (Map.Entry<ResourceLocation, Class<? extends UpgradeBase>> entry : upgrades.entrySet())
            if (entry.getValue() == upgradeClass) return entry.getKey();
        return null;
    }

    private static Optional<Class<? extends UpgradeBase>> get(ResourceLocation id) {
        return upgrades.keySet().stream().filter(r -> r.toString().equals(id.toString())).findAny().map(upgrades::get);
    }
}
