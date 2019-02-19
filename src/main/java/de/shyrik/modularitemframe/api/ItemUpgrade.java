package de.shyrik.modularitemframe.api;

import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ItemUpgrade  extends Item {
    private ResourceLocation upgradeId;

    private static Map<ResourceLocation, Tuple<ItemUpgrade, Class<? extends UpgradeBase>>> UPGRADES = new HashMap<>();

    public ItemUpgrade(Properties prop, Class<? extends UpgradeBase> upgradeClass, ResourceLocation upgradeId) {
        super(prop);
        this.upgradeId = upgradeId;
        UPGRADES.put(upgradeId, new Tuple<>(this, upgradeClass));
    }

    public UpgradeBase createUpgrade() {
        return createUpgrade(upgradeId);
    }

    public static UpgradeBase createUpgrade(ResourceLocation id) {
        try {
            Optional<Tuple<ItemUpgrade, Class<? extends UpgradeBase>>> set = UPGRADES.keySet().stream().filter(r -> r.toString().equals(id.toString())).findAny().map(UPGRADES::get);
            if (set.isPresent()) {
                UpgradeBase upgrade = set.get().getB().newInstance();
                upgrade.parent = set.get().getA();
                return upgrade;
            }
        } catch (InstantiationException | IllegalAccessException e) {
            return null;
        }

        return null;
    }
}
