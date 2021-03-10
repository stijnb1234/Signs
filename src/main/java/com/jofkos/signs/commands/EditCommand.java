package com.jofkos.signs.commands;

import com.jofkos.signs.utils.API;
import com.jofkos.signs.utils.i18n.I18n;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.block.SignChangeEvent;

public class EditCommand implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
		if (!(cs instanceof Player)) {
			cs.sendMessage(I18n.__("cmd.playeronly"));
			return true;
		}
		
		if (args.length < 2) {
			return false;
		}
		int line;
		try {
			line = Integer.parseInt(args[0]);
			if (line > 4 || line < 1)
				return false;
		} catch (Exception e) {
			return false;
		}
		
		Player player = (Player) cs;
		
		Block block = player.getTargetBlock(null, 100);
		if (!(block.getState() instanceof Sign)) {
			player.sendMessage(I18n.__("cmd.edit.signonly"));
			return true;
		}
		
		if (!API.canBuild(player, block)) {
			player.sendMessage(cmd.getPermissionMessage());
			return true;
		}
		
		Sign sign = (Sign) block.getState();
		
		SignChangeEvent event = new SignChangeEvent(block, player, sign.getLines());
		event.setLine(line-1, StringUtils.join(ArrayUtils.subarray(args, 1, args.length), " "));
		
		Bukkit.getPluginManager().callEvent(event);
		
		for (int i = 0; i < 4; i++) {
			sign.setLine(i, event.getLine(i));
		}
		sign.update();
		
		
		player.sendMessage(I18n.__("cmd.edit.success", line, event.getLine(line-1)));
		
		return true;
	}
}
