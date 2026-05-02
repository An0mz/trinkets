package dev.emi.trinkets.mixin;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import dev.emi.trinkets.Point;
import dev.emi.trinkets.TrinketPlayerScreenHandler;
import dev.emi.trinkets.TrinketScreen;
import dev.emi.trinkets.TrinketScreenManager;
import dev.emi.trinkets.api.SlotGroup;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.screen.recipebook.RecipeBookProvider;
import net.minecraft.client.util.math.Rect2i;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.slot.Slot;

/**
 * Delegates drawing and slot group selection logic
 * 
 * @author Emi
 */
@Mixin(InventoryScreen.class)
public abstract class InventoryScreenMixin extends HandledScreen<PlayerScreenHandler> implements RecipeBookProvider, TrinketScreen {

	private InventoryScreenMixin() { super(null, null, null); }

	@Inject(at = @At("HEAD"), method = "init")
	private void init(CallbackInfo info) {
		TrinketScreenManager.init(this);
	}

	@Inject(at = @At("TAIL"), method = "handledScreenTick")
	private void tick(CallbackInfo info) {
		TrinketScreenManager.tick();
	}

	@Inject(at = @At("HEAD"), method = "render")
	private void render(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo info) {
		TrinketScreenManager.update(mouseX, mouseY);
	}

	/**
	 * Clip the entity preview to its character viewer bounds so the player model
	 * (arms, equipment, etc.) cannot bleed into the Trinkets slot panels in MC 1.21.2.
	 * By redirecting the drawEntity call, we receive the exact bounds vanilla passes
	 * (x1, y1, x2, y2) and apply an outer scissor using those same coordinates.
	 */
	@Redirect(method = "drawBackground", at = @At(value = "INVOKE",
		target = "Lnet/minecraft/client/gui/screen/ingame/InventoryScreen;drawEntity(Lnet/minecraft/client/gui/DrawContext;IIIIIFFFLnet/minecraft/entity/LivingEntity;)V"))
	private void drawEntityWithScissor(DrawContext context, int x1, int y1, int x2, int y2,
			int size, float f, float mouseX, float mouseY, LivingEntity entity) {
		context.enableScissor(x1, y1, x2, y2);
		InventoryScreen.drawEntity(context, x1, y1, x2, y2, size, f, mouseX, mouseY, entity);
		context.draw();
		context.disableScissor();
	}

	@Inject(at = @At("RETURN"), method = "drawBackground")
	private void drawBackground(DrawContext context, float delta, int mouseX, int mouseY, CallbackInfo info) {
		TrinketScreenManager.drawExtraGroups(context);
	}

	@Inject(at = @At("TAIL"), method = "drawForeground")
	private void drawForeground(DrawContext context, int mouseX, int mouseY, CallbackInfo info) {
		TrinketScreenManager.drawActiveGroup(context);
	}
	

	@Override
	public TrinketPlayerScreenHandler trinkets$getHandler() {
		return (TrinketPlayerScreenHandler) this.handler;
	}
	
	@Override
	public Rect2i trinkets$getGroupRect(SlotGroup group) {
		Point pos = ((TrinketPlayerScreenHandler) handler).trinkets$getGroupPos(group);
		if (pos != null) {
			return new Rect2i(pos.x() - 1, pos.y() - 1, 17, 17);
		}
		return new Rect2i(0, 0, 0, 0);
	}

	@Override
	public Slot trinkets$getFocusedSlot() {
		return this.focusedSlot;
	}

	@Override
	public int trinkets$getX() {
		return this.x;
	}

	@Override
	public int trinkets$getY() {
		return this.y;
	}

	@Override
	public boolean trinkets$isRecipeBookOpen() {
		return false; // TODO: check recipe book open state in 1.21.2 (API changed)
	}
}
