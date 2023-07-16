package cursedflames.splitshulkers;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod("splitshulkers")
public class SplitShulkersForge extends SplitShulkers {
	public SplitShulkersForge() {
		FMLJavaModLoadingContext.get().getModEventBus().register(this);
	}

	static CreativeModeTab boxes = new CreativeModeTab("splitshulkers:boxes") {
		@Override
		public ItemStack makeIcon() {
			return iconStack;
		}

		@Override
		public void fillItemList(NonNullList<ItemStack> stacks) {
			stacks.addAll(getAllShulkerBoxes());
		}
	};
}
