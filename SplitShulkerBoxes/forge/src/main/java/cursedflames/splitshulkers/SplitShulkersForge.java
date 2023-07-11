package cursedflames.splitshulkers;

import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.RegisterEvent;

@Mod("splitshulkers")
public class SplitShulkersForge extends SplitShulkers {
	public SplitShulkersForge() {
		FMLJavaModLoadingContext.get().getModEventBus().register(this);
	}

	@SubscribeEvent
	public void register(RegisterEvent event) {
		event.register(Registries.CREATIVE_MODE_TAB, helper -> {
			var iconStack = new ItemStack(Blocks.BLACK_SHULKER_BOX);
			var tag = new CompoundTag();
			secondaryColorToTag(DyeColor.WHITE, tag);
			BlockItem.setBlockEntityData(iconStack, BlockEntityType.SHULKER_BOX, tag);
			var itemGroup = CreativeModeTab
					.builder()
					.withTabsBefore(CreativeModeTabs.SPAWN_EGGS)
					.title(Component.translatable("splitshulkers.category"))
					.icon(() -> iconStack)
					.displayItems((params, output) -> output.acceptAll(getAllShulkerBoxes()))
					.build();
			helper.register(new ResourceLocation("splitshulkers", "boxes"), itemGroup);
		});
	}
}
