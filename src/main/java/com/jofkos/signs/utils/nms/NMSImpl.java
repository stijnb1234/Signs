package com.jofkos.signs.utils.nms;

import net.minecraft.server.v1_16_R3.BlockPosition;
import net.minecraft.server.v1_16_R3.PacketPlayOutOpenSignEditor;
import net.minecraft.server.v1_16_R3.TileEntitySign;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class NMSImpl {
	public static PacketPlayOutOpenSignEditor getSignEdit(Block sign) {
		return new PacketPlayOutOpenSignEditor(new BlockPosition(sign.getX(), sign.getY(), sign.getZ()));
	}

	public static void sendSignEditor(Player p, Block sign) {
		CraftPlayer craftP = (CraftPlayer) p;
		CraftWorld craftW = (CraftWorld) sign.getWorld();
		TileEntitySign tes = (TileEntitySign) craftW.getHandle().getTileEntity(new BlockPosition(sign.getX(), sign.getY(), sign.getZ()));
		tes.isEditable = true;
		craftP.getHandle().openSign(tes);
	}
}
