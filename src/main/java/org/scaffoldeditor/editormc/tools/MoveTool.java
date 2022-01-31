package org.scaffoldeditor.editormc.tools;

import java.util.Set;

import org.joml.Vector3d;
import org.scaffoldeditor.editormc.ScaffoldEditor;
import org.scaffoldeditor.editormc.ScaffoldEditor.UpdateSelectionEvent;
import org.scaffoldeditor.editormc.engine.ScaffoldEditorMod;
import org.scaffoldeditor.editormc.engine.gizmos.TranslationGizmo;
import org.scaffoldeditor.editormc.ui.Viewport;
import org.scaffoldeditor.scaffold.entity.Entity;
import org.scaffoldeditor.scaffold.util.event.EventListener;

import javafx.scene.image.Image;
import net.minecraft.util.math.Vec3d;

public class MoveTool extends SelectTool {
	
	public static Image ICON = new Image(MoveTool.class.getResourceAsStream("/assets/scaffold/tools/move.png"));
	protected ScaffoldEditor editor;
	protected ScaffoldEditorMod mod = ScaffoldEditorMod.getInstance();
	
	protected TranslationGizmo gizmo;
	
	private EventListener<UpdateSelectionEvent> updateListener = event -> updateGizmo(event.newSelection);

	public MoveTool(Viewport viewport, ScaffoldEditor editor) {
		super(viewport);
		this.editor = editor;
	}
	
	@Override
	public void onActivate() {
		super.onActivate();
		editor.onUpdateSelection(updateListener);
		updateGizmo(editor.getSelectedEntities());
	}
	
	@Override
	public void onDeactivate() {
		super.onDeactivate();
		editor.removeOnUpdateSelection(updateListener);
		disableGizmo();
	}
	
	public void updateGizmo(Set<Entity> selection) {
		if (selection.isEmpty()) {
			disableGizmo();
			return;
		}
		
		Vector3d avg = new Vector3d();
		for (Entity ent : selection) {
			avg.add(ent.getPosition());
		}
		avg.div(selection.size());
		
		if (gizmo == null) {
			gizmo = new TranslationGizmo();
			mod.getGizmoManager().gizmos.add(gizmo);
		}
		
		gizmo.setPos(new Vec3d(avg.x, avg.y, avg.z));
	}
	
	public void disableGizmo() {
		if (gizmo != null) {
			mod.getGizmoManager().gizmos.remove(gizmo);
		}
		gizmo = null;
	}
	
	@Override
	public Image getIcon() {
		return ICON;
	}
	
	@Override
	public String getName() {
		return "Move Tool";
	}
}
