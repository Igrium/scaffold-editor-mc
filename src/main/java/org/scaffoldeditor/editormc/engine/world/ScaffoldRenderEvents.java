package org.scaffoldeditor.editormc.engine.world;

import java.util.ArrayList;
import java.util.List;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents.AfterEntities;

/**
 * The Fabric API doesn't provide methods to unsubscribe from world render
 * events, so this wrapper layer does that.
 * 
 * @author Igrium
 */
public final class ScaffoldRenderEvents {
	private ScaffoldRenderEvents() {}
	
	private static List<AfterEntities> afterEntitiesListeners = new ArrayList<>();
	
	public static void registerAfterEntities(AfterEntities listener) {
		afterEntitiesListeners.add(listener);
	}
	
	public static void removeAfterEntities(AfterEntities listener) {
		afterEntitiesListeners.remove(listener);
	}
	
	public static void register() {
		WorldRenderEvents.AFTER_ENTITIES.register(context -> {
			for (AfterEntities listener : afterEntitiesListeners) {
				listener.afterEntities(context);
			}
		});
	}
}
