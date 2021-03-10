package com.jofkos.signs.utils;

import java.util.Locale;

import org.apache.commons.lang.LocaleUtils;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import com.jofkos.signs.Signs;
import com.jofkos.signs.utils.i18n.I18n;

public class Config {
	private static Signs plugin;
	private static FileConfiguration config;
	
	public static Material EDIT_MAT = Material.FEATHER;
	
	public static Locale LOCALE = Locale.getDefault();
	public static Action ACTION = null;
	public static boolean COSTS = false;
	public static boolean COLORS = true;
	public static boolean PER_COLOR_PERMISSIONS = false;
	public static boolean ONLY_OWNED = false;
	public static boolean NOTIFY_UPDATES = true;
	public static boolean AUTO_UPDATE = true;
	
	public static Material COPY_MAT = Material.PAPER;
	public static Material INK = Material.INK_SAC;
	public static boolean COPY_COSTS = false;
	public static boolean PASTE_COSTS = false;
	
	public static void load() {
		plugin = Signs.getInstance();
		config = plugin.getConfig();
		setupConfig();
		loadConfig();
	}
	
	public static void reload() {
		loadConfig();
	}
	
	private static void setupConfig() {
		
		if (config.contains("EditMaterial")) {
			config.set("Action", config.get("EditMaterial"));
			config.set("EditMaterial", null);
			saveConfig();
		}
		
		if (config.contains("EditCost")) {
			config.set("EditCosts", config.get("EditCost"));
			config.set("EditCost", null);
			saveConfig();
		}
		
		config.addDefault("Locale", LOCALE.toString());
		config.addDefault("Action", "INK_SACK:0");
		config.addDefault("EditCosts", false);
		config.addDefault("SignColors", true);
		config.addDefault("PerColorPermissions", false);
		config.addDefault("OnlyInOwnedRegion", false);
		config.addDefault("NotifyUpdates", true);
		config.addDefault("AutoUpdate", true);
		
		config.addDefault("Copy.Item", Material.PAPER.name());
		config.addDefault("Copy.Ink", Material.INK_SAC.name());
		config.addDefault("Copy.CopyCosts", false);
		config.addDefault("Copy.PasteCosts", false);
		
		config.options().copyDefaults(true);
		saveConfig();
	}
	
	private static void loadConfig() {
		reloadConfig();
		
		LOCALE = LocaleUtils.toLocale(config.getString("Locale"));
		ACTION = Action.fromString(config.getString("Action"));
		COSTS = config.getBoolean("EditCosts");
		COLORS = config.getBoolean("SignColors");
		PER_COLOR_PERMISSIONS = config.getBoolean("PerColorPermissions");
		ONLY_OWNED = config.getBoolean("OnlyInOwnedRegion");
		NOTIFY_UPDATES = config.getBoolean("NotifyUpdates");
		AUTO_UPDATE = config.getBoolean("AutoUpdate");
		
		COPY_MAT = Material.matchMaterial(config.getString("Copy.Item"));
		INK = Material.matchMaterial(config.getString("Copy.Ink"));
		COPY_COSTS = config.getBoolean("Copy.CopyCosts");
		PASTE_COSTS = config.getBoolean("Copy.PasteCosts");
		
		if (ACTION == null) {
			reset("Action", ACTION = Action.fromString(Material.INK_SAC.name()), I18n.__("config.edit.invalid"), I18n.__("config.edit.resetted", "(INK_SAC)"));
		}

		if (COPY_MAT == null) {
			reset("Copy.Item", COPY_MAT = Material.PAPER, I18n.__("config.copy.invalid"), I18n.__("config.copy.resetted", "(PAPER)"));
		}

		if (INK == null) {
			reset("Copy.Ink", INK = Material.INK_SAC, I18n.__("config.ink.invalid"), I18n.__("config.ink.resetted", "(INK_SAC)"));
		}
	}
	
	private static void reset(String path, Object obj, String... messages) {
		Signs.log(messages);
		config.set(path, obj);
		saveConfig();
	}
	
	private static void saveConfig() {
		plugin.saveConfig();
	}
	
	public static void reloadConfig() {
		plugin.reloadConfig();
		config = plugin.getConfig();
	}
	
	public enum Action {
		HAND, ITEM, SNEAK;
		
		public static Action fromString(String action) {
			action = action.trim();
			if (action.equalsIgnoreCase("hand")) {
				return Action.HAND;
			} else if (action.equalsIgnoreCase("sneak")) {
				return Action.SNEAK;
			} else {
				return (EDIT_MAT = Material.matchMaterial(action)) != null ? Action.ITEM : null;
			}
		}
		
		public String toString() {
			return this == ITEM ? EDIT_MAT.toString() : name();
		}
	}
}