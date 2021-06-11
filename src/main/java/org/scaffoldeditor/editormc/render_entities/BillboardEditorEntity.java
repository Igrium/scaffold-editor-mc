package org.scaffoldeditor.editormc.render_entities;

import org.scaffoldeditor.editormc.engine.ScaffoldEditorMod;
import org.scaffoldeditor.editormc.engine.billboard.Billboard;
import org.scaffoldeditor.nbt.math.Vector3f;
import org.scaffoldeditor.scaffold.level.render.BillboardRenderEntity;
import org.scaffoldeditor.scaffold.level.render.RenderEntity;

import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

public class BillboardEditorEntity implements EditorRenderEntity {
	
	private ScaffoldEditorMod mod = ScaffoldEditorMod.getInstance();
	protected Billboard billboard;

	@Override
	public void spawn(RenderEntity in) {
		if (!(in instanceof BillboardRenderEntity)) {
			throw new IllegalArgumentException("Passed RenderEntity is not an instance of BillboardRenderEntity!");
		}
		BillboardRenderEntity ent = (BillboardRenderEntity) in;
		
		Vector3f pos = in.getPosition();
		billboard = new Billboard(new Identifier(ent.getTexture()), new Vec3d(pos.x, pos.y, pos.z),
				ent.getScale(), new Vec2f(0, 0), new Vec2f(1, 1));
		
		mod.getBillboardRenderer().add(billboard);
	}

	@Override
	public void update(RenderEntity in) {
		if (!(in instanceof BillboardRenderEntity)) {
			throw new IllegalArgumentException("Passed RenderEntity is not an instance of BillboardRenderEntity!");
		}
		BillboardRenderEntity ent = (BillboardRenderEntity) in;
		
		Vector3f pos = ent.getPosition();
		billboard.setTexture(new Identifier(ent.getTexture()));
		billboard.setPos(new Vec3d(pos.x, pos.y, pos.z));
		billboard.setScale(ent.getScale());
	}

	@Override
	public void despawn() {
		mod.getBillboardRenderer().remove(billboard);
		billboard = null;
	}

}
