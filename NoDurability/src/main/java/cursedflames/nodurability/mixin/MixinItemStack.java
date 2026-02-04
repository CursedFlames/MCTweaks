package cursedflames.nodurability.mixin;

import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public abstract class MixinItemStack {
	@Inject(method = "isDamageableItem", at = @At("HEAD"), cancellable = true)
	public void isDamageableItem(CallbackInfoReturnable<Boolean> cir) {
		cir.setReturnValue(false);
	}
}
