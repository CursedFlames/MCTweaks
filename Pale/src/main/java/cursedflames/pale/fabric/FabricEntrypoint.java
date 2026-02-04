package cursedflames.pale.fabric;

//? fabric {
import dev.kikugie.fletching_table.annotation.fabric.Entrypoint;
import cursedflames.pale.Pale;
import net.fabricmc.api.ModInitializer;

@Entrypoint
public class FabricEntrypoint implements ModInitializer {

    @Override
    public void onInitialize() {
        Pale.init();
    }

}
//?}
