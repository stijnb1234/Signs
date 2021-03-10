package com.jofkos.signs.listeners;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

import com.jofkos.signs.utils.Config;
import com.jofkos.signs.utils.Utils;

public class ColorListener implements Listener {
	@EventHandler
	public void onSignChangeColor(SignChangeEvent e) {
		Utils.clearLines(e);
		if (!Config.COLORS) return; 
		if (!e.getPlayer().hasPermission("signs.signcolors")) return;
		
		for (int i = 0; i < 4; i++) {
			if (e.getLine(i).isEmpty()) continue;
			e.setLine(i, ChatColor.translateAlternateColorCodes('&', e.getLine(i)));
		}
	}
}
