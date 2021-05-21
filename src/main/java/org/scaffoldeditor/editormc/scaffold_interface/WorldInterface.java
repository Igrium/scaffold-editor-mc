package org.scaffoldeditor.editormc.scaffold_interface;

import java.util.Map;

import org.scaffoldeditor.editormc.engine.EditorServerWorld;
import org.scaffoldeditor.nbt.block.Block;
import org.scaffoldeditor.nbt.block.BlockWorld;
import org.scaffoldeditor.nbt.block.Chunk;
import org.scaffoldeditor.nbt.block.Chunk.SectionCoordinate;
import org.scaffoldeditor.nbt.block.Section;
import org.scaffoldeditor.nbt.block.BlockWorld.ChunkCoordinate;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;

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
	public static void loadScaffoldChunk(Chunk chunk, EditorServerWorld world, ChunkCoordinate offset) {
		ChunkPos chunkPos = new ChunkPos(offset.x(), offset.z());
		if (world.occupiedChunks.contains(chunkPos)) {
			world.clearChunk(chunkPos);
		}
		
		for (int y = 0; y < Chunk.HEIGHT; y++) {
			for (int z = 0; z < Chunk.LENGTH; z++) {
				for (int x = 0; x < Chunk.WIDTH; x++) {
					loadBlock(chunk.blockAt(x, y, z), x, y, z, world,
							new BlockPos(offset.x() * Chunk.WIDTH, 0, offset.z() * Chunk.LENGTH));
				}
			}
		}
	}
	
	/**
	 * Load a Scaffold section into the world.
	 * @param section Scaffold section.
	 * @param world World to load into.
	 * @param offset Coordinates of the section.
	 */
	public static void loadScaffoldSection(Section section, EditorServerWorld world, SectionCoordinate offset) {
		if (world.occupiedSections.contains(offset)) {
			world.clearSection(offset);
		}
		
		for (int y = 0; y < Section.HEIGHT; y++) {
			for (int z = 0; z < Chunk.LENGTH; z++) {
				for (int x = 0; x < Chunk.WIDTH; x++) {
					loadBlock(section.blockAt(x, y, z), x, y, z, world,
							new BlockPos(offset.x * Chunk.WIDTH, offset.y * Section.HEIGHT, offset.z * Chunk.LENGTH));
				}
			}
		}
		world.occupiedSections.add(offset);
	}
	
	private static void loadBlock(Block block, int x, int y, int z, EditorServerWorld world, BlockPos offset) {
		if (block == null) {
			return;
		}
		
		world.forceBlockState(new BlockPos(offset.getX() + x, offset.getY() + y, offset.getZ() + z), BlockConverter.scaffoldToMinecraft(block));
	}
	
	public static void loadScaffoldWorld(BlockWorld in, EditorServerWorld world) {
		Map<ChunkCoordinate, Chunk> chunksMap = in.getChunks();
		
		for (ChunkCoordinate chunk : chunksMap.keySet()) {
			loadScaffoldChunk(chunksMap.get(chunk), world, chunk);
		}
	}

}
