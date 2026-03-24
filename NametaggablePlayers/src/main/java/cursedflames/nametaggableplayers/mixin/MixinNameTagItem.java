package cursedflames.nametaggableplayers.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import cursedflames.nametaggableplayers.config.NametaggablePlayersConfig;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.NameTagItem;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(NameTagItem.class)
public class MixinNameTagItem extends MixinItem {
	@WrapOperation(method = "interactLivingEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/EntityType;canSerialize()Z"))
	private boolean on_interactLivingEntity_canSerialize(EntityType<?> instance, Operation<Boolean> original) {
		return instance == EntityType.PLAYER && NametaggablePlayersConfig.configInstance.nametagsUsedOn.get().usableOnOthers()
				|| original.call(instance);
	}

	@Override
	protected void onUse(Level level, Player player, InteractionHand interactionHand, CallbackInfoReturnable<InteractionResult> cir) {
		if (!NametaggablePlayersConfig.configInstance.nametagsUsedOn.get().usableOnSelf()) return;
		if (!player.isCrouching()) return;
		var stack = player.getItemInHand(interactionHand);
		var customNameComponent = stack.get(DataComponents.CUSTOM_NAME);
		if (customNameComponent != null) {
			if (!player.level().isClientSide() && player.isAlive()) {
				player.setCustomName(customNameComponent);
				if (!player.hasInfiniteMaterials()) {
					stack.shrink(1);
				}
			}

			cir.setReturnValue(InteractionResult.SUCCESS);
		}
	}
}
