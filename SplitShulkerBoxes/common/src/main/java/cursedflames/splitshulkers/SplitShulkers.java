package cursedflames.splitshulkers;

import it.unimi.dsi.fastutil.Pair;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class SplitShulkers {
	static ItemStack iconStack;

	static {
		iconStack = new ItemStack(Blocks.BLACK_SHULKER_BOX);
		iconStack.getOrCreateTag().putString("secondaryColor", "white");
	}

	public static List<Pair<DyeColor, DyeColor>> allColorPairs;

	static {
		allColorPairs = new ArrayList<>(17 * 17);
		// This is a mess but it works
		var colors = DyeColor.values();
		int i = -1;
		DyeColor color1 = null;
		while (true) {
			int j = -1;
			DyeColor color2 = null;
			while (true) {
				allColorPairs.add(Pair.of(color1, color2));
				j++;
				if (j >= colors.length) break;
				color2 = colors[j];
			}
			i++;
			if (i >= colors.length) break;
			color1 = colors[i];
		}
	}

	public static String nullableColorToString(@Nullable DyeColor color) {
		return color == null ? "plain" : color.getName();
	}

	public static @Nullable DyeColor nullableColorFromString(@Nullable String string, @Nullable DyeColor defaultColor) {
		if (string == null) return defaultColor;
		if (string.equals("plain")) return null;
		return DyeColor.byName(string, defaultColor);
	}

	public static @Nullable DyeColor secondaryColorFromTag(@Nullable CompoundTag tag, @Nullable DyeColor defaultColor) {
		if (tag == null || !tag.contains("secondaryColor")) return defaultColor;
		return nullableColorFromString(tag.getString("secondaryColor"), defaultColor);
	}

	public static void secondaryColorToTag(@Nullable DyeColor color, CompoundTag tag) {
		tag.putString("secondaryColor", nullableColorToString(color));
	}

	static List<ItemStack> getAllShulkerBoxes() {
		var stacks = new ArrayList<ItemStack>(17 * 17);
		for (var pair : allColorPairs) {
			var color1 = pair.left();
			var color2 = pair.right();
			var stack = color1 == null ? new ItemStack(Blocks.SHULKER_BOX) : ShulkerBoxBlock.getColoredItemStack(color1);
			if (color1 != color2) {
				var blockEntityTag = new CompoundTag();
				secondaryColorToTag(color2, blockEntityTag);
				BlockItem.setBlockEntityData(stack, BlockEntityType.SHULKER_BOX, blockEntityTag);
			}
			stacks.add(stack);
		}
		return stacks;
	}
}
