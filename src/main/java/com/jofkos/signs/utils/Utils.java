package com.jofkos.signs.utils;

import com.jofkos.signs.utils.Config.Action;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Utils {
	public static boolean isSign(Block b) {
		return b.getType().name().contains("SIGN");
	}

	public static void write(Block clickedBlock, BlockFace clicked, Player writer, String... texts) {
		if (!isSign(clickedBlock)) return;
		
		BlockFace left;
		switch (clicked) {
		case EAST:
			left = BlockFace.SOUTH; break;
		case NORTH:
			left = BlockFace.EAST; break;
		case SOUTH:
			left = BlockFace.WEST; break;
		case WEST:
			left = BlockFace.NORTH; break;
		default:
			return;
		}
		
		StringBuilder text = new StringBuilder();
		
		for (String line : texts) {
			line = line.replace("\n", " ");
			if (!text.toString().endsWith(" ") && !line.startsWith(" ")) {
				text.append(" ");
			}

			text.append(line);
		}
		
		text = new StringBuilder(ChatColor.translateAlternateColorCodes('&', text.toString()).replace("\t", " ").replace("  ", " "));
		
		Block sign = clickedBlock;
		
		while (isSign(sign.getRelative(left))) {
			sign = sign.getRelative(left);
		}

		while (isSign(sign.getRelative(BlockFace.UP))) {
			sign = sign.getRelative(BlockFace.UP);
		}

		clickedBlock = sign;

		while (isSign(clickedBlock)) {
			while (isSign(sign)) {
				if (API.canBuild(writer, sign)) {
					for (int i = 0; i < 4; i++) {
						Sign s = (Sign) sign.getState();
						if (text.length() <= 15) {
							s.setLine(i, text.toString());
							s.update();
							return;
						}
						if (text.toString().startsWith(" ")) {
							text = new StringBuilder(text.substring(1));
						}
						s.setLine(i, text.substring(0, 15));
						text = new StringBuilder(text.substring(15));
						s.update();
					}
				}
				sign = sign.getRelative(left.getOppositeFace());
			}
			sign = clickedBlock = clickedBlock.getRelative(BlockFace.DOWN);
		}
	}

	public static boolean isAction(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		switch (Config.ACTION) {
		case HAND:
			return !p.isSneaking();
		case ITEM:
			return !p.isSneaking() && Config.EDIT_MAT.equals(e.getItem());
		case SNEAK:
			return p.isSneaking();
		}
		return false;
	}
	
	public static void cost(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		if (Config.COSTS && Config.ACTION == Action.ITEM && p.getGameMode() != GameMode.CREATIVE && !p.hasPermission("signs.bypass.editcost") && !p.hasPermission("signs.bypass.*")) {
			if (p.getInventory().getItemInMainHand().getAmount() <= 1) {
				p.getInventory().clear(p.getInventory().getHeldItemSlot());
			} else {
				p.getInventory().getItemInMainHand().setAmount(p.getInventory().getItemInMainHand().getAmount() - 1);
			}
		}
	}
	
//	---------------------------- Item ----------------------------	\\
	
	public static void addGlow(ItemStack i) {
		i.addUnsafeEnchantment(Enchantment.LURE, 0);
		ItemMeta im = i.getItemMeta();
		im.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		i.setItemMeta(im);
	}

	public static void giveItem(Player p, ItemStack i) {
		if (p.getInventory().getItemInMainHand().getType() == Material.AIR) {
			p.getInventory().setItemInMainHand(i);
		} else {
			for (ItemStack itemStack : p.getInventory().addItem(i).values()) {
				p.getWorld().dropItem(p.getLocation(), itemStack);
			}
		}
		p.updateInventory();
	}
	
//	---------------------------- Text ----------------------------	\\
	
	public static String[] colorCodes(String... lines) {
		String[] r = new String[lines.length];
		for (int i = 0; i < lines.length; i++) {
			r[i] = lines[i].replaceAll(ChatColor.COLOR_CHAR + "(?=[a-fk-or0-9])", "&");
			while (r[i].startsWith("&0") || r[i].startsWith("§0")) {
				r[i] = r[i].substring(2);
			}
		}
		return r;
	}
	
	public static void clearLines(SignChangeEvent e) {
		for (int i = 0; i < e.getLines().length; i++) {
			e.setLine(i, clearLine(e.getLine(i)));
		}
	}
	
	public static String clearLine(String line) {
		StringBuilder builder = new StringBuilder();
		for (char c : line.toCharArray()) {
			if (c < 0xF700 || c > 0xF747) {
				builder.append(c);
			}
		}
		return builder.toString();
	}
}

