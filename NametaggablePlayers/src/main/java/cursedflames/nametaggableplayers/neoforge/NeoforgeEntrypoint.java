package cursedflames.nametaggableplayers.neoforge;

//? neoforge {
/*import cursedflames.nametaggableplayers.NametaggablePlayers;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

@Mod(NametaggablePlayers.MOD_ID)
public class NeoforgeEntrypoint {

    public NeoforgeEntrypoint() {
        NametaggablePlayers.init();
    }

    @EventBusSubscriber(modid = NametaggablePlayers.MOD_ID, value = Dist.CLIENT)
    public static class ClientEvents {
        @SubscribeEvent
        public static void onClientSetup(final FMLClientSetupEvent event) {
        }
    }

}
*///?}
