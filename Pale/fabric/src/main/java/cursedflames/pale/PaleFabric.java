package cursedflames.pale;

import net.fabricmc.api.ModInitializer;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class PaleFabric extends Pale implements ModInitializer {
	public static Holder<MobEffect> getPaleEffect() {
		if (PALE_EFFECT == null) {
			PALE_EFFECT = Registry.registerForHolder(BuiltInRegistries.MOB_EFFECT, StatusEffectPale.ID, new StatusEffectPale(MobEffectCategory.HARMFUL, 0xffb340));
		}
		return PALE_EFFECT;
	}

	@Override
	public void onInitialize() {
	}
}
