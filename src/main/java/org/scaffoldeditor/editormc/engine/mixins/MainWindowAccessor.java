package org.scaffoldeditor.editormc.engine.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.util.Window;

@Mixin(Window.class)
public interface MainWindowAccessor {
	@Accessor
	int getFramebufferWidth();
	@Accessor
	void setFramebufferWidth(int value);
	@Accessor
	int getFramebufferHeight();
	@Accessor
	void setFramebufferHeight(int value);
}
