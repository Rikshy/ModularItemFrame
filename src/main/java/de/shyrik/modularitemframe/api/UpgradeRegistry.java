package de.shyrik.modularitemframe.api;

import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class UpgradeRegistry {
    private static Map<String, Class<? extends UpgradeBase>> upgrades = new HashMap<>();

    /**
     * registers a upgrade class
     * @param id unique id
     * @param moduleClass upgrade to register
     *
     * @throws IllegalArgumentException when id is duplicated
     */
    public static void register(String id, Class<? extends UpgradeBase> upgradeClass) {
        if(upgrades.containsKey(id))
            throw new IllegalArgumentException("[ModularItemFrame] upgrade key already exists!");
        upgrades.put(id, upgradeClass);
    }

    /**
     * Registers the upgrade class and creates a new {@link ItemUpgrade} instance
     * @param id unique id
     * @param moduleClass upgrade to register
     * @return new {@link ItemUpgrade} instance
     */
    public static ItemUpgrade registerCreate(String id, Class<? extends UpgradeBase> moduleClass) {
        register(id, moduleClass);
        return new ItemUpgrade(id);
    }

    /**
     * creates an instance of a upgrade
     * @param id the id of the upgrade
     * @return created instance
     */
    @Nullable
    public static UpgradeBase createModuleInstance(String id) {
        try {
            return upgrades.get(id).newInstance();
        } catch (Exception ex) {
            return null;
        }
    }

    /**
     * Gets the upgrade id of a specific upgrade
     * @param upgradelass upgrade to look up
     * @return unique upgrade id
     */
    public static String getModuleId(Class<? extends UpgradeBase> upgradeClass) {
        for (Map.Entry<String, Class<? extends UpgradeBase>> entry : upgrades.entrySet())
            if (entry.getValue() == upgradeClass) return entry.getKey();
        return "";
    }
}
