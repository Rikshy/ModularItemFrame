package de.shyrik.modularitemframe.api;

import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ItemModule extends Item {
    private ResourceLocation moduleId;

    private static Map<ResourceLocation, Tuple<ItemModule, Class<? extends ModuleBase>>> MODULES = new HashMap<>();

    public ItemModule(Class<? extends ModuleBase> moduleClass, ResourceLocation moduleId) {
        super(new Properties());
        this.moduleId = moduleId;
        MODULES.put(moduleId, new Tuple<>(this, moduleClass));
    }

    public ModuleBase createModule() {
        return createModule(moduleId);
    }

    public static ModuleBase createModule(ResourceLocation id) {
        try {
            Optional<Tuple<ItemModule, Class<? extends ModuleBase>>> set = MODULES.keySet().stream().filter(r -> r.toString().equals(id.toString())).findAny().map(MODULES::get);
            if (set.isPresent()) {
                ModuleBase module = set.get().getB().newInstance();
                module.parent = set.get().getA();
                return module;
            }
        } catch (InstantiationException | IllegalAccessException e) {
            return null;
        }

        return null;
    }
}
