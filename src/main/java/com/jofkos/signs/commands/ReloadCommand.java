package com.jofkos.signs.commands;

import com.jofkos.signs.utils.Config;
import com.jofkos.signs.utils.i18n.I18n;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ReloadCommand implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
		Config.reload();
		I18n.load();
		Command.broadcastCommandMessage(cs, I18n.__("config.reloaded"));
		return true;
	}
}
