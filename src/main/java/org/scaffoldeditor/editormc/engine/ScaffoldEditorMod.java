package org.scaffoldeditor.editormc.engine;

import java.awt.Dimension;

import org.scaffoldeditor.editormc.Config;
import org.scaffoldeditor.editormc.ScaffoldEditor;
import org.scaffoldeditor.editormc.engine.entity.ModelEntityRenderer;
import org.scaffoldeditor.editormc.engine.mixins.MainWindowAccessor;
//import org.scaffoldeditor.editormc.engine.mixins.ScreenAccessor;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;

public class ScaffoldEditorMod implements ClientModInitializer {
	
	private static ScaffoldEditorMod instance;
	public boolean isInEditor = false;
	private MinecraftClient client;
	protected ScaffoldEditor editor;

	public void onInitializeClient() {
		client = MinecraftClient.getInstance();
		
		Config.init();
		
		ModelEntityRenderer.register();
		
		ScaffoldEditorMod.instance = this;
		
		ClientTickEvents.START_CLIENT_TICK.register(e -> {
			if (isInEditor) {
				try {
					Dimension res = editor.getUI().getViewport().getDesiredResolution();
					Framebuffer fb = client.getFramebuffer();
					if (fb.viewportWidth != res.width || fb.viewportHeight != res.height) {
						fb.resize(res.width, res.height, false);
						
						MainWindowAccessor window = (MainWindowAccessor) (Object) client.getWindow();
						window.setFramebufferWidth(res.width);
						window.setFramebufferHeight(res.height);
						
						client.gameRenderer.onResized(res.width, res.height);
					}
				} catch (Exception ex) {}			
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
		
		if (editor == null) editor = new ScaffoldEditor();
		editor.start(null);
		
	}
	
	public ScaffoldEditor getEditor() {
		return editor;
	}
	
	public static ScaffoldEditorMod getInstance() {
		return instance;
	}
}
