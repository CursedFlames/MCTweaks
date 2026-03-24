package cursedflames.nametaggableplayers.mixin;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public abstract class MixinPlayer extends LivingEntity {
	protected MixinPlayer(EntityType<? extends LivingEntity> entityType, Level level) {
		super(entityType, level);
	}

	// high order so that nametags should hopefully take priority over anything else that changes the player's name
	@Inject(method = "getName", at = @At("HEAD"), order = 9000, cancellable = true)
	private void on_getName(CallbackInfoReturnable<Component> cir) {
		if (this.hasCustomName()) {
			cir.setReturnValue(EntityAccess.invokeRemoveAction(this.getCustomName()));
		}
	}
}
