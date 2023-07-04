package cursedflames.pale.mixin;

import cursedflames.pale.FoodPropertiesAccessor;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.food.Foods;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(Foods.class)
public class MixinFoodComponents {
	@Shadow @Final public static FoodProperties ROTTEN_FLESH;

	// Remove the vanilla hunger status effect from rotten flesh
	@Inject(method = "<clinit>", at = @At("RETURN"))
	private static void onInit(CallbackInfo ci) {
		((FoodPropertiesAccessor) ROTTEN_FLESH).getEffectsRaw().remove(0);
	}
}
