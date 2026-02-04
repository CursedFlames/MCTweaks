package cursedflames.splitshulkers;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;

import static cursedflames.splitshulkers.SplitShulkerBoxes.getItemBlockEntityTagUnsafe;

// TODO the split shulkers crafting recipe broke at some point and I'm not sure why
public class ShulkerBoxColoring extends CustomRecipe {
	public ShulkerBoxColoring(CraftingBookCategory craftingbookcategory) {
		super(craftingbookcategory);
	}

	public boolean matches(CraftingInput craftinginput, Level level) {
		int i = 0;
		int j = 0;

		for (int k = 0; k < craftinginput.size(); k++) {
			ItemStack itemstack = craftinginput.getItem(k);
			if (!itemstack.isEmpty()) {
				if (Block.byItem(itemstack.getItem()) instanceof ShulkerBoxBlock) {
					i++;
				} else {
					if (!(itemstack.getItem() instanceof DyeItem)) {
						return false;
					}

					j++;
				}

				if (j > 1 || i > 1) {
					return false;
				}
			}
		}

		return i == 1 && j == 1;
	}

	public ItemStack assemble(CraftingInput craftingInput, HolderLookup.Provider holderlookup$provider) {
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
		DyeColor secondaryColor = SplitShulkerBoxes.secondaryColorFromTag(getItemBlockEntityTagUnsafe(shulkerStack), primaryColor);
		if (dyeItemPos <= shulkerPos) primaryColor = dyeItem.getDyeColor();
		if (dyeItemPos >= shulkerPos) secondaryColor = dyeItem.getDyeColor();

		Block block = ShulkerBoxBlock.getBlockByColor(primaryColor);
		var outputStack = shulkerStack.transmuteCopy(block, 1);

		var blockData = getItemBlockEntityTagUnsafe(outputStack);
		if (blockData != null) blockData = blockData.copy();
		if (blockData == null) blockData = new CompoundTag();
		if (secondaryColor != primaryColor) {
			SplitShulkerBoxes.secondaryColorToTag(secondaryColor, blockData);
		} else {
			blockData.remove("secondaryColor");
		}
		BlockItem.setBlockEntityData(outputStack, BlockEntityType.SHULKER_BOX, blockData);
		return outputStack;
	}

	// FIXME register in proper location
	private static RecipeSerializer<ShulkerBoxColoring> RECIPE_SERIALIZER;

	public static void setRecipeSerializer(RecipeSerializer<ShulkerBoxColoring> r) {
		RECIPE_SERIALIZER = r;
	}

	@Override
	public RecipeSerializer<ShulkerBoxColoring> getSerializer() {
		return RECIPE_SERIALIZER;
	}
}
