package org.scaffoldeditor.editormc.scaffold_interface;

import com.github.mryurihi.tbnbt.TagType;
import com.github.mryurihi.tbnbt.tag.NBTTag;
import com.github.mryurihi.tbnbt.tag.NBTTagByte;
import com.github.mryurihi.tbnbt.tag.NBTTagByteArray;
import com.github.mryurihi.tbnbt.tag.NBTTagCompound;
import com.github.mryurihi.tbnbt.tag.NBTTagDouble;
import com.github.mryurihi.tbnbt.tag.NBTTagFloat;
import com.github.mryurihi.tbnbt.tag.NBTTagInt;
import com.github.mryurihi.tbnbt.tag.NBTTagIntArray;
import com.github.mryurihi.tbnbt.tag.NBTTagList;
import com.github.mryurihi.tbnbt.tag.NBTTagLong;
import com.github.mryurihi.tbnbt.tag.NBTTagLongArray;
import com.github.mryurihi.tbnbt.tag.NBTTagShort;
import com.github.mryurihi.tbnbt.tag.NBTTagString;

import net.minecraft.nbt.ByteArrayTag;
import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.LongArrayTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.ShortTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;

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
	public static Tag scaffoldToMinecraft(NBTTag in) {
		if (in.getTagType() == TagType.BYTE) {
			return scaffoldByteToMinecraft(in.getAsTagByte());
		} else if (in.getTagType() == TagType.SHORT) {
			return scaffoldShortToMinecraft(in.getAsTagShort());
		} else if (in.getTagType() == TagType.INT) {
			return scaffoldIntToMinecraft(in.getAsTagInt());
		} else if (in.getTagType() == TagType.LONG) {
			return scaffoldLongToMinecraft(in.getAsTagLong());
		} else if (in.getTagType() == TagType.FLOAT) {
			return scaffoldFloatToMinecraft(in.getAsTagFloat());
		} else if (in.getTagType() == TagType.DOUBLE) {
			return scaffoldDoubleToMinecraft(in.getAsTagDouble());
		} else if (in.getTagType() == TagType.BYTE_ARRAY) {
			return scaffoldByteArrayToMinecraft(in.getAsTagByteArray());
		} else if (in.getTagType() == TagType.STRING) {
			return scaffoldStringToMinecraft(in.getAsTagString());
		} else if (in.getTagType() == TagType.LIST) {
			return scaffoldListToMinecraft(in.getAsTagList());
		} else if (in.getTagType() == TagType.COMPOUND) {
			return scaffoldCompoundToMinecraft(in.getAsTagCompound());
		} else if (in.getTagType() == TagType.INT_ARRAY) {
			return scaffoldIntArrayToMinecraft(in.getAsTagIntArray());
		} else if (in.getTagType() == TagType.LONG_ARRAY) {
			return scaffoldLongArrayToMinecraft(in.getAsTagLongArray());
		} else {
			throw new IllegalArgumentException("Unknown tag type: "+in.getTagType().toString());
		}
	}
	
	public static ByteTag scaffoldByteToMinecraft(NBTTagByte in) {
		return ByteTag.of(in.getValue());
	}
	
	public static ShortTag scaffoldShortToMinecraft(NBTTagShort in) {
		return ShortTag.of(in.getValue());
	}

	public static IntTag scaffoldIntToMinecraft(NBTTagInt in) {
		return IntTag.of(in.getValue());
	}
	
	public static LongTag scaffoldLongToMinecraft(NBTTagLong in) {
		return LongTag.of(in.getValue());
	}
	
	public static FloatTag scaffoldFloatToMinecraft(NBTTagFloat in) {
		return FloatTag.of(in.getValue());
	}
	
	public static DoubleTag scaffoldDoubleToMinecraft(NBTTagDouble in) {
		return DoubleTag.of(in.getValue());
	}
	
	public static ByteArrayTag scaffoldByteArrayToMinecraft(NBTTagByteArray in) {
		return new ByteArrayTag(in.getValue());
	}
	
	public static StringTag scaffoldStringToMinecraft(NBTTagString in) {
		return StringTag.of(in.getValue());
	}
	
	public static ListTag scaffoldListToMinecraft(NBTTagList in) {
		ListTag list = new ListTag();
		for (NBTTag tag : in.getValue()) {
			list.add(scaffoldToMinecraft(tag));
		}
		
		return list;
	}
	
	public static CompoundTag scaffoldCompoundToMinecraft(NBTTagCompound in) {
		CompoundTag tag = new CompoundTag();
		for (String key : in.getValue().keySet()) {
			tag.put(key, scaffoldToMinecraft(in.get(key)));
		}
		return tag;
	}
	
	public static IntArrayTag scaffoldIntArrayToMinecraft(NBTTagIntArray in) {
		return new IntArrayTag(in.getValue());
	}
	
	public static LongArrayTag scaffoldLongArrayToMinecraft(NBTTagLongArray in) {
		return new LongArrayTag(in.getValue());
	}
	
}
