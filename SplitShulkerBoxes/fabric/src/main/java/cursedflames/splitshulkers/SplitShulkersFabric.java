package cursedflames.splitshulkers;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class SplitShulkersFabric extends SplitShulkers implements ModInitializer {
	@Override
	public void onInitialize() {
		var iconStack = new ItemStack(Blocks.BLACK_SHULKER_BOX);
		var tag = new CompoundTag();
		secondaryColorToTag(DyeColor.WHITE, tag);
		BlockItem.setBlockEntityData(iconStack, BlockEntityType.SHULKER_BOX, tag);
		var itemGroup = FabricItemGroup
				.builder()
				.title(Component.translatable("splitshulkers.category"))
				.icon(() -> iconStack)
				.displayItems((params, output) -> output.acceptAll(getAllShulkerBoxes()))
				.build();
		Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, new ResourceLocation("splitshulkers", "boxes"), itemGroup);
	}
}
