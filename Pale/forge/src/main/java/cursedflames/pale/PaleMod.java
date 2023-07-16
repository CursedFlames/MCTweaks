package cursedflames.pale;

import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod("pale")
public class PaleMod {
	public PaleMod() {
		FMLJavaModLoadingContext.get().getModEventBus().register(this);
	}

	@SubscribeEvent
	public void register(RegistryEvent.Register<MobEffect> event) {
		event.getRegistry().register(StatusEffectPale.PALE.setRegistryName(StatusEffectPale.ID));
	}
}
