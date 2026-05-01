package dev.emi.trinkets.mixin;

import dev.emi.trinkets.EntityRenderStateAccessor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.state.EntityRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

/**
 * Stores the entity's network ID in the render state so that feature renderers
 * can look up the live entity when needed.
 *
 * @author Trinkets
 */
@Environment(EnvType.CLIENT)
@Mixin(EntityRenderState.class)
public class EntityRenderStateMixin implements EntityRenderStateAccessor {

	@Unique
	private int trinkets$entityId = -1;

	@Override
	public int trinkets$getId() {
		return this.trinkets$entityId;
	}

	@Override
	public void trinkets$setId(int id) {
		this.trinkets$entityId = id;
	}
}


