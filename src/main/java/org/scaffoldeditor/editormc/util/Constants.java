package org.scaffoldeditor.editormc.util;

import java.util.HashMap;
import java.util.Map;

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

public class Constants {
	private Constants() {};
	
	@SuppressWarnings("rawtypes")
	public static final Map<String, Class<? extends Tag>> TAG_NAMES;
	
	static {
		TAG_NAMES = new HashMap<>();
		
		TAG_NAMES.put("Byte", ByteTag.class);
		TAG_NAMES.put("Short", ShortTag.class);
		TAG_NAMES.put("Int", IntTag.class);
		TAG_NAMES.put("Long", LongTag.class);
		TAG_NAMES.put("Float", FloatTag.class);
		TAG_NAMES.put("Double", DoubleTag.class);
		TAG_NAMES.put("String", StringTag.class);
		TAG_NAMES.put("List", ListTag.class);
		TAG_NAMES.put("Compound", CompoundTag.class);
		TAG_NAMES.put("ByteArray", ByteArrayTag.class);
		TAG_NAMES.put("IntArray", IntArrayTag.class);
		TAG_NAMES.put("LongArray", LongArrayTag.class);
	}
}
