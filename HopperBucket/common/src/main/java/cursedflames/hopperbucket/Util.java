package cursedflames.hopperbucket;

import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.function.Predicate;

public class Util {
	public static int getFirstEmptySlot(Container container) {
		int length = container.getContainerSize();
		for (int i = 0; i < length; i++) {
			var stack = container.getItem(i);
			if (stack.isEmpty()) return i;
		}
		return -1;
	}
	public static int getFirstStackIndex(Container container, Item item) {
		int length = container.getContainerSize();
		for (int i = 0; i < length; i++) {
			var stack = container.getItem(i);
			if (stack.is(item)) return i;
		}
		return -1;
	}
	public static int getFirstMatching(Container container, Predicate<ItemStack> predicate) {
		int length = container.getContainerSize();
		for (int i = 0; i < length; i++) {
			ItemStack itemStack = container.getItem(i);
			if (predicate.test(itemStack)) return i;
		}
		return -1;
	}
}
