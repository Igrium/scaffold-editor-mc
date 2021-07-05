package org.scaffoldeditor.editormc.engine;

import java.awt.Dimension;

import org.scaffoldeditor.editormc.Config;
import org.scaffoldeditor.editormc.ScaffoldEditor;
import org.scaffoldeditor.editormc.engine.entity.BillboardEntityRenderer;
import org.scaffoldeditor.editormc.engine.entity.BrushEntityRenderer;
import org.scaffoldeditor.editormc.engine.entity.ModelEntityRenderer;
import org.scaffoldeditor.editormc.engine.gizmos.GizmoManager;
import org.scaffoldeditor.editormc.engine.world.BlockRenderDispatcher;
import org.scaffoldeditor.editormc.engine.world.ScaffoldRenderEvents;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.util.Window;

public class ScaffoldEditorMod implements ClientModInitializer {
	
	private static ScaffoldEditorMod instance;
	public boolean isInEditor = false;
	private MinecraftClient client;
	private BlockRenderDispatcher blockRenderDispatcher;
	private GizmoManager gizmoManager;
	protected ScaffoldEditor editor;

	public void onInitializeClient() {
		client = MinecraftClient.getInstance();
		
		Config.init();
		
		ModelEntityRenderer.register();
		BillboardEntityRenderer.register();
		BrushEntityRenderer.register();
		
		ScaffoldEditorMod.instance = this;
		
		ClientTickEvents.START_CLIENT_TICK.register(e -> {
			if (isInEditor) {
				try {
					Dimension res = editor.getUI().getViewport().getDesiredResolution();
					Framebuffer fb = client.getFramebuffer();
					if (fb.viewportWidth != res.width || fb.viewportHeight != res.height) {
						fb.resize(res.width, res.height, false);
						
						Window window = client.getWindow();
						window.setFramebufferWidth(res.width);
						window.setFramebufferHeight(res.height);
						
						client.gameRenderer.onResized(res.width, res.height);
					}
				} catch (Exception ex) {}
			}
		});
		
		WorldRenderEvents.LAST.register(context -> {
			ViewportExporter.export();
		});
		
		ScaffoldRenderEvents.register();
		blockRenderDispatcher = new BlockRenderDispatcher(client);
		blockRenderDispatcher.register();
		
		gizmoManager = new GizmoManager();
		gizmoManager.register();
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
	
	public BlockRenderDispatcher getBlockRenderDispatcher() {
		return blockRenderDispatcher;
	}
	
	public GizmoManager getGizmoManager() {
		return gizmoManager;
	}
	
	public static ScaffoldEditorMod getInstance() {
		return instance;
	}
}
