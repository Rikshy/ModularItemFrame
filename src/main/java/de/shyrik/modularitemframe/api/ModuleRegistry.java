package de.shyrik.modularitemframe.api;

import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class ModuleRegistry {

	private static Map<String, Class<? extends ModuleFrameBase>> modules = new HashMap<>();

	/**
	 * registers a module class
	 * @param id unique id
	 * @param moduleClass module to register
	 *
	 * @throws IllegalArgumentException when id is duplicated
	 */
	public static void register(String id, Class<? extends ModuleFrameBase> moduleClass) {
		if(modules.containsKey(id))
			throw new IllegalArgumentException("[ModularItemFrame] module key already exists!");
		modules.put(id, moduleClass);
	}

	/**
	 * Registers the module class and creates a new {@link ItemModule} instance
	 * @param id unique id
	 * @param moduleClass module to register
	 * @return new {@link ItemModule} instance
	 */
	public static ItemModule registerCreate(String id, Class<? extends ModuleFrameBase> moduleClass) {
		register(id, moduleClass);
		return new ItemModule(id);
	}

	/**
	 * creates an instance of a module
	 * @param id the id of the module
	 * @return created instance
	 */
	@Nullable
	public static ModuleFrameBase createModuleInstance(String id) {
		try {
			return modules.get(id).newInstance();
		} catch (Exception ex) {
			return null;
		}
	}

	/**
	 * Gets the module id of a specific module
	 * @param moduleClass module to look up
	 * @return unique module id
	 */
	public static String getModuleId(Class<? extends ModuleFrameBase> moduleClass) {
		for (Map.Entry<String, Class<? extends ModuleFrameBase>> entry : modules.entrySet())
			if (entry.getValue() == moduleClass) return entry.getKey();
		return "";
	}
}
