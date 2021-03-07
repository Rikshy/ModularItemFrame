package de.shyrik.modularitemframe.api;

import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class UpgradeItem extends Item {
    private final ResourceLocation upgradeId;

    private static final Map<ResourceLocation, Tuple<UpgradeItem, Class<? extends UpgradeBase>>> UPGRADES = new HashMap<>();


    public UpgradeItem(Properties prop, Class<? extends UpgradeBase> upgradeClass, ResourceLocation upgradeId) {
        super(prop);
        this.upgradeId = upgradeId;
        UPGRADES.put(upgradeId, new Tuple<>(this, upgradeClass));
    }

    public UpgradeBase createUpgrade() {
        return createUpgrade(upgradeId);
    }

    public static UpgradeBase createUpgrade(ResourceLocation id) {
        try {
            Optional<Tuple<UpgradeItem, Class<? extends UpgradeBase>>> set = UPGRADES.keySet().stream().filter(r -> r.toString().equals(id.toString())).findAny().map(UPGRADES::get);
            if (set.isPresent()) {
                UpgradeBase upgrade = set.get().getB().newInstance();
                upgrade.item = set.get().getA();
                return upgrade;
            }
        } catch (InstantiationException | IllegalAccessException e) {
            return null;
        }

        return null;
    }

    public static Set<ResourceLocation> getUpgradeIds() {
        return UPGRADES.keySet();
    }
}
