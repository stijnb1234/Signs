package com.jofkos.signs;

import com.jofkos.signs.listeners.ColorListener;
import com.jofkos.signs.listeners.CopyListener;
import com.jofkos.signs.listeners.EditListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import com.jofkos.signs.commands.ColorCodesCommand;
import com.jofkos.signs.commands.EditCommand;
import com.jofkos.signs.commands.ReloadCommand;
import com.jofkos.signs.utils.API;
import com.jofkos.signs.utils.Config;
import com.jofkos.signs.utils.i18n.I18n;

public class Signs extends JavaPlugin {
	private static Signs plugin;
	
	@Override
	public void onEnable() {
		plugin = this;
		Config.load();
		
		I18n.load();
		API.load();

		Bukkit.getPluginManager().registerEvents(new EditListener(), this);
		Bukkit.getPluginManager().registerEvents(new CopyListener(), this);
		Bukkit.getPluginManager().registerEvents(new ColorListener(), this);

		getCommand("signs").setExecutor(new ReloadCommand());
		getCommand("colorcodes").setExecutor(new ColorCodesCommand());
		getCommand("edit").setExecutor(new EditCommand());
		
		this.saveConfig();
	}
	
	public static Signs getInstance() {
		return plugin;
	}
	
	public static void log(String... msgs) {
		for (String msg : msgs) {
			log(msg);
		}
	}
	
	public static void log(String msg) {
		Bukkit.getConsoleSender().sendMessage("[Signs] " + msg);
	}
}
