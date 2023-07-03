package cursedflames.stackablepotions.mixin;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.BrewingStandMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;


@Mixin(BrewingStandMenu.class)
public abstract class MixinBrewingStandMenu extends AbstractContainerMenu {
	private MixinBrewingStandMenu(MenuType<?> type, int syncId) {
		super(type, syncId);
	}

	@Inject(method = "quickMoveStack",
			at = @At(value = "INVOKE",
					target = "Lnet/minecraft/world/inventory/BrewingStandMenu$PotionSlot;mayPlaceItem(Lnet/minecraft/world/item/ItemStack;)Z"),
			locals = LocalCapture.CAPTURE_FAILSOFT,
			cancellable = true)
	private void onQuickMoveStack(Player player, int index, CallbackInfoReturnable<ItemStack> info,
								  ItemStack itemStack, Slot slot, ItemStack itemStack2) {
		// Replace vanilla shift-click behavior for potions with our own, to prevent getting more than one in a slot
		if (slot.mayPlace(itemStack)) {
			boolean movedItems = false;
			for (int i = 0; i < 3; i++) {
				Slot slot2 = this.getSlot(i);
				if (slot2.getItem().isEmpty() && slot.mayPlace(itemStack2)) {
					if (itemStack2.getCount() > slot2.getMaxStackSize()) {
						slot2.set(itemStack2.split(slot2.getMaxStackSize()));
					} else {
						slot2.set(itemStack2.split(itemStack2.getCount()));
					}
					movedItems = true;
					slot2.setChanged();
					// We loop through all the slots without breaking on a successful transfer,
					// so that you can shift-click into all potion slots at once
					if (itemStack2.isEmpty()) break;
				}
			}
			// returned value sets current slot
			if (movedItems) {
				info.setReturnValue(ItemStack.EMPTY);
			}
		}
	}

	@Redirect(method = "quickMoveStack",
			at = @At(value = "INVOKE",
					target = "Lnet/minecraft/world/inventory/BrewingStandMenu$PotionSlot;mayPlaceItem(Lnet/minecraft/world/item/ItemStack;)Z"))
	private boolean onQuickMoveStackRedirect(ItemStack stack, Player player, int index) {
		// Block the default shift-clicking into potion slots so we can do it ourselves.
		// Unfortunately because we're cancelling the vanilla `if`, the else ifs run, meaning the player can
		// shift-click into potion slots with more than 3 potions and the rest of the stack will also get moved around
		// kinda annoying but whatever, don't know of a clean solution for it.
		return false;
	}
}
