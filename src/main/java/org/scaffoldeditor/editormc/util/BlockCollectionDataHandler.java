package org.scaffoldeditor.editormc.util;

import org.scaffoldeditor.nbt.block.BlockCollection;

import net.minecraft.entity.data.TrackedDataHandler;
import net.minecraft.network.PacketByteBuf;

public class BlockCollectionDataHandler implements TrackedDataHandler<BlockCollection> {

	@Override
	public void write(PacketByteBuf buf, BlockCollection value) {
	}

	@Override
	public BlockCollection read(PacketByteBuf buf) {
		return null;
	}

	@Override
	public BlockCollection copy(BlockCollection value) {
		return null;
	}

}
