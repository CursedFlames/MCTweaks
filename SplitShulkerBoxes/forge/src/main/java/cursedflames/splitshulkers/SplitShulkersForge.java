package cursedflames.splitshulkers;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod("splitshulkers")
public class SplitShulkersForge extends SplitShulkers {
	public SplitShulkersForge() {
		FMLJavaModLoadingContext.get().getModEventBus().register(this);
	}

	@SubscribeEvent
	public void register(CreativeModeTabEvent.Register event) {
		var iconStack = new ItemStack(Blocks.BLACK_SHULKER_BOX);
		var tag = new CompoundTag();
		secondaryColorToTag(DyeColor.WHITE, tag);
		BlockItem.setBlockEntityData(iconStack, BlockEntityType.SHULKER_BOX, tag);
		event.registerCreativeModeTab(new ResourceLocation("splitshulkers", "boxes"), builder -> builder
					.title(Component.translatable("splitshulkers.category"))
					.icon(() -> iconStack)
					.displayItems((params, output) -> output.acceptAll(getAllShulkerBoxes()))
					.build()
		);
	}
}
