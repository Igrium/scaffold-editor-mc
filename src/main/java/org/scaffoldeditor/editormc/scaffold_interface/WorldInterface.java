package org.scaffoldeditor.editormc.scaffold_interface;

import java.util.HashSet;
import java.util.Map;

import org.scaffoldeditor.editormc.engine.world.EditorServerWorld;
import org.scaffoldeditor.nbt.block.Block;
import org.scaffoldeditor.nbt.block.BlockWorld;
import org.scaffoldeditor.nbt.block.Chunk;
import org.scaffoldeditor.nbt.block.Chunk.SectionCoordinate;
import org.scaffoldeditor.nbt.block.Section;
import org.scaffoldeditor.nbt.block.BlockWorld.ChunkCoordinate;

import net.minecraft.util.math.BlockPos;

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
		if (!world.getServer().isOnThread()) {
			world.getServer().execute(() -> loadScaffoldChunk(chunk, world, offset));
			return;
		}
		for (int i = 0; i < Chunk.HEIGHT / Section.HEIGHT; i++) {
			loadScaffoldSection(chunk.sections[i], world, new SectionCoordinate(offset, i));
		}
	}
	
	/**
	 * Load a Scaffold section into the world.
	 * @param section Scaffold section.
	 * @param world World to load into.
	 * @param offset Coordinates of the section.
	 */
	public static void loadScaffoldSection(Section section, EditorServerWorld world, SectionCoordinate offset) {
		if (!world.getServer().isOnThread()) {
			world.getServer().execute(() -> loadScaffoldSection(section, world, offset));
			return;
		}
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
		if (!world.getServer().isOnThread()) {
			world.getServer().execute(() -> loadScaffoldWorld(in, world));
			return;
		}
		
		Map<ChunkCoordinate, Chunk> chunksMap = in.getChunks();
		for (SectionCoordinate section : new HashSet<>(world.occupiedSections)) {
			world.clearSection(section);
		}
		
		for (ChunkCoordinate chunk : chunksMap.keySet()) {
			loadScaffoldChunk(chunksMap.get(chunk), world, chunk);
		}
	}

}
