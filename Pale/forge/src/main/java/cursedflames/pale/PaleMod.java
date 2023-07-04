package cursedflames.pale;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;

@Mod("pale")
public class PaleMod {
	public PaleMod() {
		FMLJavaModLoadingContext.get().getModEventBus().register(this);
	}

	@SubscribeEvent
	public void register(RegisterEvent event) {
		event.register(ForgeRegistries.MOB_EFFECTS.getRegistryKey(), helper -> {
			helper.register(StatusEffectPale.ID, StatusEffectPale.PALE);
		});
	}
}
