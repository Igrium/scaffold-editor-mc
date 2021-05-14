package org.scaffoldeditor.editormc.scaffold_interface;

import java.util.Map;

import org.scaffoldeditor.nbt.block.Block;
import org.scaffoldeditor.nbt.block.BlockWorld;
import org.scaffoldeditor.nbt.block.Chunk;
import org.scaffoldeditor.nbt.block.BlockWorld.ChunkCoordinate;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Handles the parsing of Scaffold BlockWorlds.
 * @author Igrium
 */
public final class WorldInterface {
	
	/**
	 * Load a Scaffold chunk into a Minecraft world.
	 * @param chunk Scaffold chunk.
	 * @param world World to load into.
	 */
	public static void loadScaffoldChunk(Chunk chunk, World world, ChunkCoordinate offset) {
		for (int y = 0; y < Chunk.HEIGHT; y++) {
			for (int z = 0; z < Chunk.LENGTH; z++) {
				for (int x = 0; x < Chunk.WIDTH; x++) {
					loadBlock(chunk.blockAt(x, y, z), x, y, z, world, offset);
				}
			}
		}
	}
	
	private static void loadBlock(Block block, int x, int y, int z, World world, ChunkCoordinate offset) {
		int x_final = x + offset.x() * Chunk.WIDTH;
		int z_final = z + offset.z() * Chunk.LENGTH;
		world.setBlockState(new BlockPos(x_final, y, z_final), BlockConverter.scaffoldToMinecraft(block), 2);
	}
	
	public static void loadScaffoldWorld(BlockWorld in, World world) {
		Map<ChunkCoordinate, Chunk> chunksMap = in.getChunks();
		
		for (ChunkCoordinate chunk : chunksMap.keySet()) {
			loadScaffoldChunk(chunksMap.get(chunk), world, chunk);
		}
	}
}
