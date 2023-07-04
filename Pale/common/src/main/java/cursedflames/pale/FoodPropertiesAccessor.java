package cursedflames.pale;

import com.mojang.datafixers.util.Pair;
import net.minecraft.world.effect.MobEffectInstance;

import java.util.List;

public interface FoodPropertiesAccessor {
	// Necessary because getEffects() on forge returns a clone, preventing us from modifying it
	List<Pair<MobEffectInstance, Float>> getEffectsRaw();
}
