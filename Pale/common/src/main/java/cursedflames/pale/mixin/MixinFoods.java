package cursedflames.pale.mixin;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.food.Foods;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(Foods.class)
public class MixinFoods {
	@Redirect(method = "<clinit>",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/world/food/FoodProperties$Builder;effect(Lnet/minecraft/world/effect/MobEffectInstance;F)Lnet/minecraft/world/food/FoodProperties$Builder;"),
			slice = @Slice(
					from = @At(value = "FIELD", shift = At.Shift.BY, by = -4, target = "Lnet/minecraft/world/food/Foods;ROTTEN_FLESH:Lnet/minecraft/world/food/FoodProperties;"),
					to = @At(value = "FIELD", target = "Lnet/minecraft/world/food/Foods;ROTTEN_FLESH:Lnet/minecraft/world/food/FoodProperties;")
			),
			require = 1, allow = 1
			)
	private static FoodProperties.Builder onRottenFleshEffect(FoodProperties.Builder instance, MobEffectInstance $$0, float $$1) {
		// Skip the call to builder.effect by redirecting the call and just returning the original value
		return instance;
	}
}
