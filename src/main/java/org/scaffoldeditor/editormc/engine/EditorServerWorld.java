package org.scaffoldeditor.editormc.engine;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executor;
import org.scaffoldeditor.nbt.block.Chunk.SectionCoordinate;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WorldGenerationProgressListener;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ProgressListener;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.Spawner;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.level.ServerWorldProperties;
import net.minecraft.world.level.storage.LevelStorage.Session;

public class EditorServerWorld extends ServerWorld {
	
	/**
	 * Keeps track of the chunks that may have blocks in them.
	 */
	public final Set<ChunkPos> occupiedChunks = new HashSet<>();
	
	public final Set<SectionCoordinate> occupiedSections = new HashSet<>();

	@Override
	public void save(ProgressListener progressListener, boolean flush, boolean bl) {
		return;
	}

	@Override
	public void updateNeighbor(BlockPos sourcePos, Block sourceBlock, BlockPos neighborPos) {
		return;
	}
	
	@Override
	public void updateComparators(BlockPos pos, Block block) {
		return;
	}
	
	@Override
	public void tickChunk(WorldChunk chunk, int randomTickSpeed) {
		return;
	}
	
	@Override
	public boolean setBlockState(BlockPos pos, BlockState state, int flags, int maxUpdateDepth) {
		occupiedChunks.add(new ChunkPos(pos.getX() >> 4, pos.getZ() >> 4));
		occupiedSections.add(new SectionCoordinate(pos.getX() >> 4, pos.getY() >> 4, pos.getZ() >> 4));
		return super.setBlockState(pos, state, flags, maxUpdateDepth);
	}

	public void forceBlockState(BlockPos pos, BlockState state) {
		this.setBlockState(pos, state, 50); // 110010
	}

	public void clear() {
		for (ChunkPos c : occupiedChunks) {
			clearChunk(c);
		}
	}

	public void clearChunk(ChunkPos pos) {
		BlockPos pos1 = new BlockPos(pos.getStartX(), 0, pos.getStartZ());
		BlockPos pos2 = new BlockPos(pos.getEndX(), 256, pos.getEndZ());
		
		Iterable<BlockPos> iterator = BlockPos.iterate(pos1, pos2);
		
		for (BlockPos b : iterator) {
			forceBlockState(b, Blocks.AIR.getDefaultState());
		}
		occupiedChunks.remove(pos);
	}
	
	public void clearSection(SectionCoordinate pos) {
		BlockPos pos1 = new BlockPos(pos.getStartX(), pos.getStartY(), pos.getStartZ());
		BlockPos pos2 = new BlockPos(pos.getEndX(), pos.getEndY(), pos.getEndZ());
		
		Iterable<BlockPos> iterator = BlockPos.iterate(pos1, pos2);
		
		for (BlockPos b : iterator) {
			forceBlockState(b, Blocks.AIR.getDefaultState());
		}
		occupiedSections.remove(pos);
	}

	public EditorServerWorld(MinecraftServer server, Executor workerExecutor, Session session,
			ServerWorldProperties properties, RegistryKey<World> registryKey, DimensionType dimensionType,
			WorldGenerationProgressListener worldGenerationProgressListener, ChunkGenerator chunkGenerator,
			boolean debugWorld, long l, List<Spawner> list, boolean bl) {
		super(server, workerExecutor, session, properties, registryKey, dimensionType, worldGenerationProgressListener,
				chunkGenerator, debugWorld, l, list, bl);
	}

}
