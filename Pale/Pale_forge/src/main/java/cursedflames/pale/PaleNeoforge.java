package cursedflames.pale;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.RegisterEvent;

@Mod("pale")
public class PaleNeoforge extends Pale {
	public static Holder<MobEffect> getPaleEffect() {
		if (PALE_EFFECT == null) {
			PALE_EFFECT = DeferredHolder.create(Registries.MOB_EFFECT, StatusEffectPale.ID);
		}
		return PALE_EFFECT;
	}

	public PaleNeoforge(IEventBus modEventBus) {
		modEventBus.addListener(PaleNeoforge::register);
	}

	public static void register(RegisterEvent event) {
		if (event.getRegistryKey() != BuiltInRegistries.MOB_EFFECT.key()) return;
		Registry.registerForHolder(BuiltInRegistries.MOB_EFFECT, StatusEffectPale.ID, new StatusEffectPale(MobEffectCategory.HARMFUL, 0xffb340));
	}
}
