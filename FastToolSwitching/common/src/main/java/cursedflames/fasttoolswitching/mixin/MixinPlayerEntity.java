package cursedflames.fasttoolswitching.mixin;

import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public class MixinPlayerEntity {
	// Janky hack to block `resetLastAttackedTicks` calls from `tick()` without using redirects
	private boolean fts_isInTick = false;

	@Inject(method = "tick",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/entity/player/Player;resetAttackStrengthTicker()V"
			))
	private void beforeResetLastAttackedTicks(CallbackInfo ci) {
		fts_isInTick = true;
	}

	@Inject(method = "resetAttackStrengthTicker",
			at = @At("HEAD"),
			cancellable = true)
	private void onResetLastAttackedTicks(CallbackInfo ci) {
		if (fts_isInTick) {
			ci.cancel();
			// Shouldn't be necessary here as well, but just in case
			fts_isInTick = false;
		}
	}

	@Inject(method = "tick",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/entity/player/Player;resetAttackStrengthTicker()V",
					shift = At.Shift.AFTER
			))
	private void afterResetLastAttackedTicks(CallbackInfo ci) {
		fts_isInTick = false;
	}
}