package dev.emi.trinkets;

import dev.emi.trinkets.api.TrinketsApi;
import dev.emi.trinkets.api.client.TrinketRendererRegistry;
import dev.emi.trinkets.EntityRenderStateAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;

public class TrinketFeatureRenderer<S extends LivingEntityRenderState, M extends EntityModel<S>> extends FeatureRenderer<S, M> {

	public TrinketFeatureRenderer(LivingEntityRenderer<?, ?, ?> context) {
		super((FeatureRendererContext<S, M>) context);
	}

	@Override
	public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, S state, float limbAngle, float limbDistance) {
		MinecraftClient client = MinecraftClient.getInstance();
		if (client.world == null) return;
		Entity rawEntity = client.world.getEntityById(((EntityRenderStateAccessor) state).trinkets$getId());
		if (!(rawEntity instanceof LivingEntity entity)) return;

		TrinketsApi.getTrinketComponent(entity).ifPresent(component ->
				component.forEach((slotReference, stack) ->
						TrinketRendererRegistry.getRenderer(stack.getItem()).ifPresent(renderer -> {
							matrices.push();
							renderer.render(stack, slotReference, this.getContextModel(), matrices, vertexConsumers,
									light, entity, limbAngle, limbDistance, 0, state.age, state.yawDegrees, state.pitch);
							matrices.pop();
						})
				)
		);
	}
}
