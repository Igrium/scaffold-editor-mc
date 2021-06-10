package org.scaffoldeditor.editormc.engine.mixins;

import org.scaffoldeditor.editormc.engine.ScaffoldEditorMod;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

@Mixin(TitleScreen.class)
public class MixinTitleScreen extends Screen {
	
	protected MixinTitleScreen(Text title) {
		super(title);
	}

	@Inject(method = "init()V", at = @At("RETURN"))
	protected void init(CallbackInfo ci) { 
		addDrawableChild(new ButtonWidget(0, 0, 98, 20, new TranslatableText("menu.scaffoldeditor"), (button) -> {
			ScaffoldEditorMod.getInstance().launchEditor();
		}));
	}
}
