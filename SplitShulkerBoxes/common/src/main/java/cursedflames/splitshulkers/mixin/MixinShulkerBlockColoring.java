package cursedflames.splitshulkers.mixin;

import cursedflames.splitshulkers.SplitShulkers;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.ShulkerBoxColoring;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import static cursedflames.splitshulkers.SplitShulkers.getItemBlockEntityTagUnsafe;

@Mixin(ShulkerBoxColoring.class)
public class MixinShulkerBlockColoring {
	/**
	 * @author CursedFlames
	 * @reason Change shulker box dyeing logic to allow for dyeing individual halves
	 */
	@Overwrite
	public ItemStack assemble(CraftingInput craftingInput, HolderLookup.Provider provider) {
		ItemStack shulkerStack = ItemStack.EMPTY;
		DyeItem dyeItem = (DyeItem) Items.WHITE_DYE;
		int shulkerPos = 0;
		int dyeItemPos = 0;
		for (int i = 0; i < craftingInput.size(); ++i) {
			ItemStack itemStack2 = craftingInput.getItem(i);
			if (itemStack2.isEmpty()) continue;
			Item item = itemStack2.getItem();
			if (Block.byItem(item) instanceof ShulkerBoxBlock) {
				shulkerStack = itemStack2;
				shulkerPos = i / craftingInput.width();
			} else if (item instanceof DyeItem) {
				dyeItem = (DyeItem)item;
				dyeItemPos = i / craftingInput.width();
			}
		}
		DyeColor primaryColor = ShulkerBoxBlock.getColorFromItem(shulkerStack.getItem());
		DyeColor secondaryColor = SplitShulkers.secondaryColorFromTag(getItemBlockEntityTagUnsafe(shulkerStack), primaryColor);
		if (dyeItemPos <= shulkerPos) primaryColor = dyeItem.getDyeColor();
		if (dyeItemPos >= shulkerPos) secondaryColor = dyeItem.getDyeColor();

		Block block = ShulkerBoxBlock.getBlockByColor(primaryColor);
		var outputStack = shulkerStack.transmuteCopy(block, 1);

		var blockData = getItemBlockEntityTagUnsafe(outputStack);
		if (blockData != null) blockData = blockData.copy();
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
