package org.scaffoldeditor.editormc.tools;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

import org.apache.logging.log4j.LogManager;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.scaffoldeditor.editormc.EditorOperationManager;
import org.scaffoldeditor.editormc.ScaffoldEditor;
import org.scaffoldeditor.editormc.ui.ScaffoldUI;
import org.scaffoldeditor.editormc.ui.Viewport;
import org.scaffoldeditor.editormc.ui.controllers.EntityToolPropertiesController;
import org.scaffoldeditor.editormc.ui.controllers.CompileProgressUI;
import org.scaffoldeditor.editormc.util.RaycastUtils;
import org.scaffoldeditor.scaffold.level.Level;
import org.scaffoldeditor.scaffold.level.entity.Entity;
import org.scaffoldeditor.scaffold.level.entity.EntityRegistry;
import org.scaffoldeditor.scaffold.operation.AddEntityOperation;

import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;

/**
 * Creates an entity at the crosshair when clicked.
 * @author Igrium
 */
public class EntityTool implements ViewportTool {
	
	public final Image icon;
	
	private EntityToolPropertiesController uiController;
	private Parent root;
	
	public EntityTool() {
		icon = new Image(getClass().getResourceAsStream("/assets/scaffold/tools/entity.png"));
		try {
			loadUI();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	protected void loadUI() throws IOException {
		FXMLLoader loader = new FXMLLoader(CompileProgressUI.class.getResource("/assets/scaffold/ui/tool_properties/entity_tool.fxml"));
		root = loader.load();
		uiController = loader.getController();
		uiController.setParent(this);
	}

	@Override
	public void onActivate() {
		uiController.setWarningText("");
	}

	@Override
	public void onMouseClicked(MouseEvent event) {
		if (event.getButton() == MouseButton.PRIMARY) {
			Viewport viewport = ScaffoldUI.getInstance().getViewport();
			int width = (int) viewport.getParent().getWidth();
			int height = (int) viewport.getParent().getHeight();
			
			int x = (int) event.getX();
			int y = (int) event.getY();
			
			HitResult hitResult = RaycastUtils.raycastPixel(x, y, width, height, 100);		
			if (hitResult.getType() == HitResult.Type.MISS) return;		
			Vec3d pos = hitResult.getPos();
			spawn(new Vector3d(pos.x, pos.y, pos.z), uiController.shouldSnapToBlock())
				.thenAccept(ent -> ScaffoldUI.getInstance().getToolbar().setTool("select"));
			
		}
	}
	
	/**
	 * Spawn the selected entity.
	 * @param position Position to spawn at.
	 * @param snapToBlock Snap to a grid increment (always rounds down).
	 * @return Success.
	 */
	public CompletableFuture<Entity> spawn(Vector3dc position, boolean snapToBlock) {
		if (uiController.getEnteredClass().length() == 0) {
			// This is a VERY high-level class, so we don't want to be throwing exceptions here.
			uiController.setWarningText("Improper name.");
			return CompletableFuture.failedFuture(new IllegalArgumentException("Improper name."));
		}
		
		String registryName = uiController.getEnteredClass();
		if (!EntityRegistry.registry.containsKey(registryName)) {
			uiController.setWarningText("Unknown entity type: "+registryName);
			return CompletableFuture.failedFuture(new IllegalArgumentException("Unknown entity type: "+registryName));
		}
		String name = uiController.getEnteredName();
		if (name.length() == 0) {
			name = registryName;
		}
		if (snapToBlock) {
			position = position.floor(new Vector3d());
		}
		
		Level level = ScaffoldEditor.getInstance().getLevel();
		CompletableFuture<Entity> future = EditorOperationManager.getInstance().runOperation(new AddEntityOperation(level, registryName, name, position));
		
		future.whenComplete((ent, e) -> {
			if (e != null) {
				uiController.setWarningText("Unable to spawn entity. " + e.getMessage() + " See console for details.");
				LogManager.getLogger().error("Unable to spawn entity.", e);
			}
		});


		return future;
	}

	@Override
	public Image getIcon() {
		return icon;
	}

	@Override
	public Cursor getCursor() {
		return Cursor.CROSSHAIR;
	}
	
	@Override
	public boolean overrideCursor() {
		return true;
	}
	
	@Override
	public String getName() {
		return "Entity Tool";
	}
	
	@Override
	public Node getPropertiesPane() {
		if (root != null ) {
			return root;
		} else {
			return ViewportTool.super.getPropertiesPane();
		}
	}
}
