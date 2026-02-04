package cursedflames.fasttoolswitching.mixin;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Player.class)
public class MixinPlayerEntity {
	@WrapWithCondition(method = "tick",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/entity/player/Player;resetAttackStrengthTicker()V"
			))
	private boolean cancelResetLastAttackedTicks(Player player) {
		// Unconditionally cancel. Maybe this *should* be a redirect after all so it crashes instead of silently overriding other mods that target this invoke but also. this is the entire point of this mod
		return false;
	}
}