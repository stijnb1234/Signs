package com.jofkos.signs.plugin;

import com.jofkos.signs.utils.API;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class WorldGuardPlugin extends API.APIPlugin {
	static {
		clazz = "com.sk89q.worldguard.bukkit.WorldGuardPlugin";
	}
		
	@Override
	public boolean canBuild(Player player, Block block) {
		LocalPlayer localPlayer = com.sk89q.worldguard.bukkit.WorldGuardPlugin.inst().wrapPlayer(player);

		RegionQuery query = WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery();
		if (!WorldGuard.getInstance().getPlatform().getSessionManager().hasBypass(localPlayer, BukkitAdapter.adapt(block.getWorld()))) {
			return query.testState(BukkitAdapter.adapt(block.getLocation()), localPlayer, Flags.BUILD);
		} else {
			return true;
		}
	}
	
	public boolean isInOwnedRegion(Player player, Block block) {
		RegionManager rm = WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(block.getWorld()));
		for (ProtectedRegion pr : rm.getApplicableRegions(BukkitAdapter.asBlockVector(block.getLocation()))) {
			if (pr.getMembers().contains(player.getUniqueId())) return true;
		}
		return false;
	}
}