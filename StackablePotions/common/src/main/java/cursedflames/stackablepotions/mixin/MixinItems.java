package cursedflames.stackablepotions.mixin;

import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(Items.class)
public abstract class MixinItems {
	@ModifyArg(method = "<clinit>",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/Item$Properties;stacksTo(I)Lnet/minecraft/world/item/Item$Properties;", ordinal = 0),
			slice = @Slice( from = @At(value = "NEW", target = "Lnet/minecraft/world/item/PotionItem;")))
	private static int onPotion(int old) {
		return 16;
	}
	@ModifyArg(method = "<clinit>",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/Item$Properties;stacksTo(I)Lnet/minecraft/world/item/Item$Properties;", ordinal = 0),
			slice = @Slice( from = @At(value = "NEW", target = "Lnet/minecraft/world/item/SplashPotionItem;")))
	private static int onSplashPotion(int old) {
		return 16;
	}
	@ModifyArg(method = "<clinit>",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/Item$Properties;stacksTo(I)Lnet/minecraft/world/item/Item$Properties;", ordinal = 0),
			slice = @Slice( from = @At(value = "NEW", target = "Lnet/minecraft/world/item/LingeringPotionItem;")))
	private static int onLingeringPotion(int old) {
		return 16;
	}
}
