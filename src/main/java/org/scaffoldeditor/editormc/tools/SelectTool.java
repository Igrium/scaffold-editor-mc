package org.scaffoldeditor.editormc.tools;

import org.scaffoldeditor.editormc.ScaffoldEditor;
import org.scaffoldeditor.editormc.ui.ScaffoldUI;
import org.scaffoldeditor.editormc.ui.Viewport;
import org.scaffoldeditor.editormc.util.RaycastUtils;
import org.scaffoldeditor.scaffold.entity.Entity;

import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.BlockPos;

public class SelectTool implements ViewportTool {
	
	public final Image icon;
	protected Viewport viewport;
	
	public SelectTool(Viewport viewport) {
		icon = new Image(getClass().getResourceAsStream("/assets/scaffold/tools/select.png"));
		this.viewport = viewport;
	}

	@Override
	public void onActivate() {
	}

	@Override
	public void onDeactivate() {
	}

	@Override
	public void onMousePressed(MouseEvent e) {	
	}
	
	@Override
	public void onMouseClicked(MouseEvent e) {
		if (e.getButton() != MouseButton.PRIMARY) {
			return;
		}
		
		ScaffoldEditor editor = ScaffoldUI.getInstance().getEditor();
		if (!e.isShiftDown()) { // TODO: test for shift key
			editor.getSelectedEntities().clear();
		}
		
		int width = (int) viewport.getParent().getWidth();
		int height = (int) viewport.getParent().getHeight();
		
		int x = (int) e.getX();
		int y = (int) e.getY();
		
		HitResult hitResult = RaycastUtils.raycastPixel(x, y, width, height, 100);
		
		if (hitResult.getType() == Type.MISS) {
		} else if (hitResult.getType() == Type.BLOCK) {
			BlockHitResult blockHit = (BlockHitResult) hitResult;
			BlockPos pos = blockHit.getBlockPos();
			Object owner = editor.getLevel().getBlockWorld().getBlockOwner(pos.getX(), pos.getY(), pos.getZ());
			if (owner instanceof Entity) {
				editor.getSelectedEntities().add((Entity) owner);
			}
		} else if (hitResult.getType() == Type.ENTITY) {
			EntityHitResult entHit = (EntityHitResult) hitResult;
			net.minecraft.entity.Entity ent = entHit.getEntity();
			Entity owner = editor.getRenderEntityManager().findOwner(ent);
			if (owner != null) {
				editor.getSelectedEntities().add(owner);
			}
		}
		editor.updateSelection();
	}

	@Override
	public void onMouseReleased(MouseEvent e) {
	}

	@Override
	public void onMouseMoved(int x, int y) {
	}

	@Override
	public void onMouseDragged(MouseEvent e) {
	}

	@Override
	public void onKeyPressed(KeyEvent e) {
	}

	@Override
	public void onKeyReleased(KeyEvent e) {
	}

	@Override
	public void onKeyTyped(KeyEvent e) {
	}

	@Override
	public Image getIcon() {
		return icon;
	}

	@Override
	public String getName() {
		return "Selection Tool";
	}
}
