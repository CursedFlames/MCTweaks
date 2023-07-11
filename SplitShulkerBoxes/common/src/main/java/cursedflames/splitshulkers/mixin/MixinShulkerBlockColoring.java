package cursedflames.splitshulkers.mixin;

import cursedflames.splitshulkers.SplitShulkers;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.ShulkerBoxColoring;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(ShulkerBoxColoring.class)
public class MixinShulkerBlockColoring {
	/**
	 * @author CursedFlames
	 * @reason Change shulker box dyeing logic to allow for dyeing individual halves
	 */
	@Overwrite
	public ItemStack assemble(CraftingContainer craftingContainer, RegistryAccess registryAccess) {
		ItemStack shulkerStack = ItemStack.EMPTY;
		DyeItem dyeItem = (DyeItem) Items.WHITE_DYE;
		int shulkerPos = 0;
		int dyeItemPos = 0;
		for (int i = 0; i < craftingContainer.getContainerSize(); ++i) {
			ItemStack itemStack2 = craftingContainer.getItem(i);
			if (itemStack2.isEmpty()) continue;
			Item item = itemStack2.getItem();
			if (Block.byItem(item) instanceof ShulkerBoxBlock) {
				shulkerStack = itemStack2;
				shulkerPos = i / craftingContainer.getWidth();
			} else if (item instanceof DyeItem) {
				dyeItem = (DyeItem)item;
				dyeItemPos = i / craftingContainer.getWidth();
			}
		}
		DyeColor primaryColor = ShulkerBoxBlock.getColorFromItem(shulkerStack.getItem());
		DyeColor secondaryColor = SplitShulkers.secondaryColorFromTag(BlockItem.getBlockEntityData(shulkerStack), primaryColor);
		if (dyeItemPos <= shulkerPos) primaryColor = dyeItem.getDyeColor();
		if (dyeItemPos >= shulkerPos) secondaryColor = dyeItem.getDyeColor();

		ItemStack outputStack = primaryColor == null ? new ItemStack(shulkerStack.getItem()) : ShulkerBoxBlock.getColoredItemStack(primaryColor);
		if (shulkerStack.hasTag()) {
			outputStack.setTag(shulkerStack.getTag().copy());
		}
		var blockData = BlockItem.getBlockEntityData(outputStack);
		if (secondaryColor != primaryColor) {
			if (blockData == null) blockData = new CompoundTag();
			SplitShulkers.secondaryColorToTag(secondaryColor, blockData);
			BlockItem.setBlockEntityData(outputStack, BlockEntityType.SHULKER_BOX, blockData);
		} else {
			if (blockData != null) blockData.remove("secondaryColor");
		}
		return outputStack;
	}
}
