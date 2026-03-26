package cursedflames.nametaggableplayers.mixin;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public abstract class MixinPlayer extends MixinEntity {
	// high order so that nametags should hopefully take priority over anything else that changes the player's name
	@Inject(method = "getName", at = @At("HEAD"), order = 9000, cancellable = true)
	private void on_getName(CallbackInfoReturnable<Component> cir) {
		if (this.hasCustomName()) {
			cir.setReturnValue(EntityAccess.invokeRemoveAction(this.getCustomName()));
		}
	}

	// neoforge 26.1+ caches the player display name, so we need to manually refresh it
	//? if neoforge && >= 26 {
	/*@Shadow public abstract void refreshDisplayName();

	@Override
	protected void onSetCustomName(Component component, CallbackInfo ci) {
		refreshDisplayName();
	}
	*///? }
}
