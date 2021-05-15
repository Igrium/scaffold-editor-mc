package org.scaffoldeditor.editormc.scaffold_interface;


import java.util.HashMap;
import java.util.Map;

import org.scaffoldeditor.nbt.block.Block;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public final class BlockConverter {
	
	private static Map<Block, BlockState> cache = new HashMap<Block, BlockState>();
	
	/**
	 * Convert a Scaffold block to a Minecraft blockstate.
	 * @param in Scaffold block.
	 * @return Minecraft block state.
	 */
	public static BlockState scaffoldToMinecraft(Block in) {
		if (cache.containsKey(in)) {
			return cache.get(in);
		}
		
		BlockState state;
		if (in.getProperties() == null) {
			state =  Registry.BLOCK.get(new Identifier(in.getName())).getDefaultState();
		} else {
			CompoundTag start = NBTConverter.scaffoldCompoundToMinecraft(in.toPaletteEntry());
			state = NbtHelper.toBlockState(start);
		}
			
		cache.put(in, state);
		return state;
	}
	
	
}
