package org.scaffoldeditor.editormc.engine;

import java.util.function.Consumer;

import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.resource.ResourcePackProfile.Factory;
import net.minecraft.resource.ResourcePackProvider;

public class ScaffoldResourcePackCreator implements ResourcePackProvider {

	@Override
	public void register(Consumer<ResourcePackProfile> profileAdder, Factory factory) {
	}

}
