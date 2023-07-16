package cursedflames.pale;

import net.fabricmc.api.ModInitializer;
import net.minecraft.core.Registry;

public class Pale implements ModInitializer {
	@Override
	public void onInitialize() {
		Registry.register(Registry.MOB_EFFECT, StatusEffectPale.ID, StatusEffectPale.PALE);
	}
}
