package com.jofkos.signs.commands;

import com.jofkos.signs.utils.Config;
import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ColorCodesCommand implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
		
		for (ChatColor color : ChatColor.values()) {
			StringBuilder message = new StringBuilder();
			
			if (color != ChatColor.MAGIC) {
				message.append(color);
			}
			
			if (Config.PER_COLOR_PERMISSIONS && !cs.hasPermission("signs.signcolors." + color.name().toLowerCase())) {
				message.append(ChatColor.ITALIC);
			}
			
			message.append("&").append(color.getChar()).append(" ").append(WordUtils.capitalizeFully(color.name()).replace("_", " "));
			
			if (color == ChatColor.MAGIC) {
				message.append(" ").append(color).append(WordUtils.capitalizeFully(color.name()).replace("_", " "));
			}
			
			cs.sendMessage(message.toString());
		}
		return true;
	}
}
