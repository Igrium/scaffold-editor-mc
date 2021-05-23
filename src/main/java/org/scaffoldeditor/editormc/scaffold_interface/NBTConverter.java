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
	public static net.minecraft.nbt.Tag scaffoldToMinecraft(Tag<?> in) {
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
	
	public static net.minecraft.nbt.ByteTag scaffoldByteToMinecraft(ByteTag in) {
		return net.minecraft.nbt.ByteTag.of(in.asByte());
	}
	
	public static net.minecraft.nbt.ShortTag scaffoldShortToMinecraft(ShortTag in) {
		return net.minecraft.nbt.ShortTag.of(in.asShort());
	}

	public static net.minecraft.nbt.IntTag scaffoldIntToMinecraft(IntTag in) {
		return net.minecraft.nbt.IntTag.of(in.asInt());
	}
	
	public static net.minecraft.nbt.LongTag scaffoldLongToMinecraft(LongTag in) {
		return net.minecraft.nbt.LongTag.of(in.asLong());
	}
	
	public static net.minecraft.nbt.FloatTag scaffoldFloatToMinecraft(FloatTag in) {
		return net.minecraft.nbt.FloatTag.of(in.asFloat());
	}
	
	public static net.minecraft.nbt.DoubleTag scaffoldDoubleToMinecraft(DoubleTag in) {
		return net.minecraft.nbt.DoubleTag.of(in.asDouble());
	}
	
	public static net.minecraft.nbt.ByteArrayTag scaffoldByteArrayToMinecraft(ByteArrayTag in) {
		return new net.minecraft.nbt.ByteArrayTag(in.getValue());
	}
	
	public static net.minecraft.nbt.StringTag scaffoldStringToMinecraft(StringTag in) {
		return net.minecraft.nbt.StringTag.of(in.getValue());
	}
	
	public static net.minecraft.nbt.ListTag scaffoldListToMinecraft(ListTag<?> in) {
		net.minecraft.nbt.ListTag list = new net.minecraft.nbt.ListTag();
		for (Tag<?> tag : in) {
			list.add(scaffoldToMinecraft(tag));
		}
		
		return list;
	}
	
	public static net.minecraft.nbt.CompoundTag scaffoldCompoundToMinecraft(CompoundTag in) {
		net.minecraft.nbt.CompoundTag tag = new net.minecraft.nbt.CompoundTag();
		for (String key : in.keySet()) {
			tag.put(key, scaffoldToMinecraft(in.get(key)));
		}
		return tag;
	}
	
	public static net.minecraft.nbt.IntArrayTag scaffoldIntArrayToMinecraft(IntArrayTag in) {
		return new net.minecraft.nbt.IntArrayTag(in.getValue());
	}
	
	public static net.minecraft.nbt.LongArrayTag scaffoldLongArrayToMinecraft(LongArrayTag in) {
		return new net.minecraft.nbt.LongArrayTag(in.getValue());
	}
	
}
