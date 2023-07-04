package cursedflames.pale.mixin;

import com.mojang.datafixers.util.Pair;
import cursedflames.pale.FoodPropertiesAccessor;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.food.FoodProperties;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(FoodProperties.class)
public abstract class FoodPropertiesAccess implements FoodPropertiesAccessor {
	@Accessor("effects")
	public abstract List<Pair<MobEffectInstance, Float>> getEffectsRaw();
}
