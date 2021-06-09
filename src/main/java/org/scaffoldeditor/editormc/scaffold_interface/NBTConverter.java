package org.scaffoldeditor.editormc.scaffold_interface;

import net.querz.nbt.tag.ByteArrayTag;
import net.querz.nbt.tag.ByteTag;
import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.DoubleTag;
import net.querz.nbt.tag.FloatTag;
import net.querz.nbt.tag.IntArrayTag;
import net.querz.nbt.tag.IntTag;
import net.querz.nbt.tag.ListTag;
import net.querz.nbt.tag.LongArrayTag;
import net.querz.nbt.tag.LongTag;
import net.querz.nbt.tag.ShortTag;
import net.querz.nbt.tag.StringTag;
import net.querz.nbt.tag.Tag;


/**
 * Converts between Scaffold NBT tags and Minecraft NBT tags.
 * @author Igrium
 */
public final class NBTConverter {
	
	/**
	 * Convert a Scaffold tag to a Minecraft tag.
	 * @param in Scaffold tag.
	 * @return Minecraft tag.
	 */
	public static net.minecraft.nbt.NbtElement scaffoldToMinecraft(Tag<?> in) {
		if (in instanceof ByteTag) {
			return scaffoldByteToMinecraft((ByteTag) in);
		} else if (in instanceof ShortTag) {
			return scaffoldShortToMinecraft((ShortTag) in);
		} else if (in instanceof IntTag) {
			return scaffoldIntToMinecraft((IntTag) in);
		} else if (in instanceof LongTag) {
			return scaffoldLongToMinecraft((LongTag) in);
		} else if (in instanceof FloatTag) {
			return scaffoldFloatToMinecraft((FloatTag) in);
		} else if (in instanceof DoubleTag) {
			return scaffoldDoubleToMinecraft((DoubleTag) in);
		} else if (in instanceof ByteArrayTag) {
			return scaffoldByteArrayToMinecraft((ByteArrayTag) in);
		} else if (in instanceof StringTag) {
			return scaffoldStringToMinecraft((StringTag) in);
		} else if (in instanceof ListTag<?>) {
			return scaffoldListToMinecraft((ListTag<?>) in);
		} else if (in instanceof CompoundTag) {
			return scaffoldCompoundToMinecraft((CompoundTag) in);
		} else if (in instanceof IntArrayTag) {
			return scaffoldIntArrayToMinecraft((IntArrayTag) in);
		} else if (in instanceof LongArrayTag) {
			return scaffoldLongArrayToMinecraft((LongArrayTag) in);
		}
		throw new IllegalArgumentException("Unknown tag type: "+in.getClass().getName());
	}
	
	public static net.minecraft.nbt.NbtByte scaffoldByteToMinecraft(ByteTag in) {
		return net.minecraft.nbt.NbtByte.of(in.asByte());
	}
	
	public static net.minecraft.nbt.NbtShort scaffoldShortToMinecraft(ShortTag in) {
		return net.minecraft.nbt.NbtShort.of(in.asShort());
	}

	public static net.minecraft.nbt.NbtInt scaffoldIntToMinecraft(IntTag in) {
		return net.minecraft.nbt.NbtInt.of(in.asInt());
	}
	
	public static net.minecraft.nbt.NbtLong scaffoldLongToMinecraft(LongTag in) {
		return net.minecraft.nbt.NbtLong.of(in.asLong());
	}
	
	public static net.minecraft.nbt.NbtFloat scaffoldFloatToMinecraft(FloatTag in) {
		return net.minecraft.nbt.NbtFloat.of(in.asFloat());
	}
	
	public static net.minecraft.nbt.NbtDouble scaffoldDoubleToMinecraft(DoubleTag in) {
		return net.minecraft.nbt.NbtDouble.of(in.asDouble());
	}
	
	public static net.minecraft.nbt.NbtByteArray scaffoldByteArrayToMinecraft(ByteArrayTag in) {
		return new net.minecraft.nbt.NbtByteArray(in.getValue());
	}
	
	public static net.minecraft.nbt.NbtString scaffoldStringToMinecraft(StringTag in) {
		return net.minecraft.nbt.NbtString.of(in.getValue());
	}
	
	public static net.minecraft.nbt.NbtList scaffoldListToMinecraft(ListTag<?> in) {
		net.minecraft.nbt.NbtList list = new net.minecraft.nbt.NbtList();
		for (Tag<?> tag : in) {
			list.add(scaffoldToMinecraft(tag));
		}
		
		return list;
	}
	
	public static net.minecraft.nbt.NbtCompound scaffoldCompoundToMinecraft(CompoundTag in) {
		net.minecraft.nbt.NbtCompound tag = new net.minecraft.nbt.NbtCompound();
		for (String key : in.keySet()) {
			tag.put(key, scaffoldToMinecraft(in.get(key)));
		}
		return tag;
	}
	
	public static net.minecraft.nbt.NbtIntArray scaffoldIntArrayToMinecraft(IntArrayTag in) {
		return new net.minecraft.nbt.NbtIntArray(in.getValue());
	}
	
	public static net.minecraft.nbt.NbtLongArray scaffoldLongArrayToMinecraft(LongArrayTag in) {
		return new net.minecraft.nbt.NbtLongArray(in.getValue());
	}
	
}
