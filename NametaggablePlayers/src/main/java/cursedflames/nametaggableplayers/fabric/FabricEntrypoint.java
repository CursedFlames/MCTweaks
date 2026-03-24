package cursedflames.nametaggableplayers.fabric;

//? fabric {
import dev.kikugie.fletching_table.annotation.fabric.Entrypoint;
import cursedflames.nametaggableplayers.NametaggablePlayers;
import net.fabricmc.api.ModInitializer;

@Entrypoint
public class FabricEntrypoint implements ModInitializer {

    @Override
    public void onInitialize() {
        NametaggablePlayers.init();
    }

}
//?}
