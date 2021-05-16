package org.scaffoldeditor.editormc.engine;

import java.awt.Dimension;

import org.scaffoldeditor.editormc.ScaffoldEditor;
import org.scaffoldeditor.editormc.engine.mixins.MainWindowAccessor;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.TranslatableText;

public class ScaffoldEditorMod implements ModInitializer {
	
	private static ScaffoldEditorMod instance;
	public boolean isInEditor = false;
	private MinecraftClient client;
	protected ScaffoldEditor editor;

	public void onInitialize() {
		client = MinecraftClient.getInstance();

		ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
			client = MinecraftClient.getInstance();
			if (screen instanceof TitleScreen) {
				Screens.getButtons(screen).add(
						new ButtonWidget(0, 0, 20, 20, new TranslatableText("menu.scaffoldeditor"), (buttonWidget) -> {
							System.out.println("Launching editor!");
							launchEditor();
						}));
			}
		});
		
		ScaffoldEditorMod.instance = this;
		
		ClientTickEvents.START_WORLD_TICK.register(e -> {
			if (isInEditor) {
				Dimension res = editor.getUI().getViewport().getDesiredResolution();
				Framebuffer fb = client.getFramebuffer();
				if (fb.viewportWidth != res.width || fb.viewportHeight != res.height) {
					fb.resize(res.width, res.height, false);
					
					MainWindowAccessor window = (MainWindowAccessor) (Object) client.getWindow();
					window.setFramebufferWidth(res.width);
					window.setFramebufferHeight(res.height);
					
					client.gameRenderer.onResized(res.width, res.height);
				}
				
			}
		});
		
	}
	
	
	/**
	 * Launch the Scaffold editor.
	 */
	public void launchEditor() {
		if (client.world != null) {
			return;
		}
		
		editor = ScaffoldEditor.startWithTestProject();
		
		
		
	}
	
	public ScaffoldEditor getEditor() {
		return editor;
	}
	
	public static ScaffoldEditorMod getInstance() {
		return instance;
	}


}
