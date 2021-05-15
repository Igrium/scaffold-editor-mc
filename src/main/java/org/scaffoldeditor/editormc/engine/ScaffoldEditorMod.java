package org.scaffoldeditor.editormc.engine;

import org.scaffoldeditor.editormc.ScaffoldEditor;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.minecraft.client.MinecraftClient;
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
				System.out.println("test");
				Screens.getButtons(screen).add(
						new ButtonWidget(0, 0, 20, 20, new TranslatableText("menu.scaffoldeditor"), (buttonWidget) -> {
							System.out.println("Launching editor!");
							launchEditor();
						}));
			}
		});
		
		ScaffoldEditorMod.instance = this;
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
