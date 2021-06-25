package org.scaffoldeditor.editormc.engine.gizmos;

import org.scaffoldeditor.editormc.engine.RenderUtils;

import dev.monarkhes.myron.api.Myron;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class TranslationGizmo extends Gizmo {
	
	private static final Identifier MODEL = new Identifier("scaffold:models/misc/arrow");
	
	@Override
	public void render(MatrixStack matrices, VertexConsumerProvider vertices) {
		BakedModel model = Myron.getModel(MODEL);
		if (model == null) model = MinecraftClient.getInstance().getBakedModelManager().getMissingModel();
		
		VertexConsumer consumer = vertices.getBuffer(TexturedRenderLayers.getEntityCutout());
		RenderUtils.renderBakedModel(model, matrices, consumer);
	}

}
