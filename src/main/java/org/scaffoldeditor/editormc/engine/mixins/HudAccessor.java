package org.scaffoldeditor.editormc.engine.mixins;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.gui.ClientChatListener;
import net.minecraft.client.gui.hud.InGameHud;

@Mixin(InGameHud.class)
public interface HudAccessor {
	@Accessor("listeners")
	List<ClientChatListener> getChatListeners();
	
}
