package modularitemframe.api;

import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class ModuleItem extends Item {
    private final ResourceLocation moduleId;

    private static final Map<ResourceLocation, Tuple<ModuleItem, Class<? extends ModuleBase>>> MODULES = new HashMap<>();

    public ModuleItem(Properties props, Class<? extends ModuleBase> moduleClass, @NotNull ResourceLocation moduleId) {
        super(props);
        this.moduleId = moduleId;
        MODULES.put(moduleId, new Tuple<>(this, moduleClass));
    }

    public ModuleBase createModule() {
        return createModule(moduleId);
    }

    public static ModuleBase createModule(ResourceLocation id) {
        try {
            Optional<Tuple<ModuleItem, Class<? extends ModuleBase>>> set = MODULES.keySet().stream().filter(r -> r.toString().equals(id.toString())).findAny().map(MODULES::get);
            if (set.isPresent()) {
                ModuleBase module = set.get().getB().newInstance();
                module.item = set.get().getA();
                return module;
            }
        } catch (InstantiationException | IllegalAccessException e) {
            return null;
        }

        return null;
    }

    public static Set<ResourceLocation> getModuleIds() {
        return MODULES.keySet();
    }
}
