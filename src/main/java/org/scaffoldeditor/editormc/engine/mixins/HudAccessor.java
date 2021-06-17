package org.scaffoldeditor.editormc.engine.mixins;

import java.util.List;
import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.gui.ClientChatListener;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.network.MessageType;

@Mixin(InGameHud.class)
public interface HudAccessor {
	@Accessor("listeners")
	Map<MessageType, List<ClientChatListener>> getChatListeners();
	
}
