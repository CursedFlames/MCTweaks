package cursedflames.pale;

import net.fabricmc.api.ModInitializer;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;

public class Pale implements ModInitializer {
	@Override
	public void onInitialize() {
		Registry.register(BuiltInRegistries.MOB_EFFECT, StatusEffectPale.ID, StatusEffectPale.PALE);
	}
}
