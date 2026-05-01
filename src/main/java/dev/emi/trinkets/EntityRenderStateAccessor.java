package dev.emi.trinkets;

/**
 * Interface added to EntityRenderState (via mixin) to carry the entity's
 * network ID for use in feature renderers (e.g. TrinketFeatureRenderer).
 */
public interface EntityRenderStateAccessor {
	int trinkets$getId();
	void trinkets$setId(int id);
}

