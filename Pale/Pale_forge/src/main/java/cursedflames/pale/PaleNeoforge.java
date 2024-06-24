package cursedflames.pale;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffectCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.registries.RegisterEvent;

@Mod("pale")
public class PaleNeoforge extends Pale {
	public PaleNeoforge(IEventBus modEventBus) {
		modEventBus.addListener(PaleNeoforge::register);
	}

	public static void register(RegisterEvent event) {
		if (event.getRegistryKey() != BuiltInRegistries.MOB_EFFECT.key()) return;
		PALE_EFFECT = Registry.registerForHolder(BuiltInRegistries.MOB_EFFECT, StatusEffectPale.ID, new StatusEffectPale(MobEffectCategory.HARMFUL, 0xffb340));
	}
}
