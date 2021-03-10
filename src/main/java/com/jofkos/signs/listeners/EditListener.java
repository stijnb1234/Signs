package com.jofkos.signs.listeners;

import com.jofkos.signs.utils.nms.NMSImpl;
import com.jofkos.signs.utils.nms.ReflectionUtil;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.meta.BookMeta;

import com.jofkos.signs.utils.API;
import com.jofkos.signs.utils.Config;
import com.jofkos.signs.utils.Utils;

public class EditListener implements Listener {
	@EventHandler(priority=EventPriority.LOWEST)
	public void onClickEditSign(PlayerInteractEvent e) {
		if (e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
		if (Config.COPY_MAT.equals(e.getItem())) return;
		if (!Utils.isAction(e)) return;
		if (!Utils.isSign(e.getClickedBlock())) return;
		
		Sign sign = (Sign) e.getClickedBlock().getState();
		
		Player p = e.getPlayer();
		if (!API.canBuild(p, sign.getBlock())) return;
		if (!API.isInOwnedRegion(p, sign.getBlock())) return;
		Utils.cost(e);

		for (int i = 0; i < 4; i++) {
			sign.setLine(i, "");
		}
		sign.update();

		ReflectionUtil.sendPacketSync(e.getPlayer(), NMSImpl.getSignEdit(e.getClickedBlock()));
		NMSImpl.sendSignEditor(e.getPlayer(), e.getClickedBlock());
	}
	
	@EventHandler
	public void onClickBookSign(PlayerInteractEvent e) {
		if (e.getAction() != Action.LEFT_CLICK_BLOCK) return;
		if (!e.getPlayer().getInventory().getItemInMainHand().hasItemMeta()) return;
		if (!(e.getPlayer().getInventory().getItemInMainHand().getItemMeta() instanceof BookMeta)) return;
		if (!Utils.isSign(e.getClickedBlock())) return;
		if (e.getPlayer().isSneaking()) return;
		if (!e.getPlayer().hasPermission("signs.signwall")) return;
		e.setCancelled(true);
		
		BookMeta meta = ((BookMeta)e.getPlayer().getInventory().getItemInMainHand().getItemMeta());
		Utils.write(e.getClickedBlock(), e.getBlockFace(), e.getPlayer(), meta.getPages().toArray(new String[meta.getPageCount()]));
	}
}
