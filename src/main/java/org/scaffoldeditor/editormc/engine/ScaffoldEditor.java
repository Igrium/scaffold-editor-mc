package org.scaffoldeditor.editormc.engine;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.TranslatableText;

public class ScaffoldEditor implements ModInitializer {
	
	private static ScaffoldEditor instance;
	public boolean isInEditor = false;
	private MinecraftClient client;

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
		
		ScaffoldEditor.instance = this;
	}
	
	
	/**
	 * Launch the Scaffold editor.
	 */
	public void launchEditor() {
		if (client.world != null) {
			return;
		}
		
		client.startIntegratedServer("");
	}
	
	public static ScaffoldEditor getInstance() {
		return instance;
	}


}
