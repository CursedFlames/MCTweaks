package cursedflames.nametaggableplayers.mixin;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class MixinEntity {
	@Shadow public abstract boolean hasCustomName();
	@Shadow public abstract Component getCustomName();

	// Overriden in MixinPlayer
	//? if neoforge && >= 26 {
	/*@Inject(method = "setCustomName", at = @At("RETURN"))
	protected void onSetCustomName(Component component, CallbackInfo ci) {}
	*///? }
}
