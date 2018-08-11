package de.shyrik.modularitemframe.api;

import de.shyrik.modularitemframe.common.item.ItemModule;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class ModuleRegistry {

	private static Map<String, Class<? extends ModuleFrameBase>> modules = new HashMap<>();

	public static void register(String id, Class<? extends ModuleFrameBase> moduleClass) {
		modules.put(id, moduleClass);
	}

	public static ItemModule registerCreate(String id, Class<? extends ModuleFrameBase> moduleClass) {
		register(id, moduleClass);
		return new ItemModule(id);
	}

	@Nullable
	public static ModuleFrameBase createModuleInstance(String id) {
		try {
			return modules.get(id).newInstance();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public static String getModuleId(Class<? extends ModuleFrameBase> moduleClass) {
		for (Map.Entry<String, Class<? extends ModuleFrameBase>> entry : modules.entrySet())
			if (entry.getValue() == moduleClass) return entry.getKey();
		return "";
	}
}
