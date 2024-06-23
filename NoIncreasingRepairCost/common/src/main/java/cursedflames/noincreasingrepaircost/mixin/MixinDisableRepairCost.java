package cursedflames.noincreasingrepaircost.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.GrindstoneMenu;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({AnvilMenu.class, GrindstoneMenu.class})
public class MixinDisableRepairCost {
	// TODO we need to do require = 0 to target multiple classes when only some of them have a valid target,
	//      but this does mean the mod could silently fail - unlikely? would require significant vanilla changes and we target an exact vanilla version
	/**
	 * Disable reading the value of REPAIR_COST
	 */
	@WrapOperation(require = 0, method = "*", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;getOrDefault(Lnet/minecraft/core/component/DataComponentType;Ljava/lang/Object;)Ljava/lang/Object;"))
	private Object onItemStackGetOrDefault(ItemStack stack, DataComponentType<?> componentType, Object defaultVal, Operation<Object> original) {
		if (componentType == DataComponents.REPAIR_COST) {
			return 0;
		}
		return original.call(stack, componentType, defaultVal);
	}

	/**
	 * When attempting to set REPAIR_COST, set it to 0 instead
	 */
	@WrapOperation(require = 0, method = "*", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;set(Lnet/minecraft/core/component/DataComponentType;Ljava/lang/Object;)Ljava/lang/Object;"))
	private Object onItemStackSet(ItemStack stack, DataComponentType<?> componentType, Object val, Operation<Object> original) {
		if (componentType == DataComponents.REPAIR_COST) {
			stack.set(DataComponents.REPAIR_COST, 0);
			return 0;
		}
		return original.call(stack, componentType, val);
	}
}
