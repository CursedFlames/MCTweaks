package cursedflames.pale.neoforge;

//? neoforge {

/*import cursedflames.pale.Pale;
import cursedflames.pale.StatusEffectPale;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffectCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.registries.RegisterEvent;

@Mod(Pale.MOD_ID)
public class NeoforgeEntrypoint {

    public NeoforgeEntrypoint(IEventBus modEventBus) {
        Pale.init();
		modEventBus.addListener(NeoforgeEntrypoint::register);
    }

	public static void register(RegisterEvent event) {
		if (event.getRegistryKey() != BuiltInRegistries.MOB_EFFECT.key()) return;
		Registry.registerForHolder(BuiltInRegistries.MOB_EFFECT, StatusEffectPale.ID, new StatusEffectPale(MobEffectCategory.HARMFUL, 0xffb340));
	}

}
*///?}
