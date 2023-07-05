package cursedflames.noincreasingrepaircost.mixin;

import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(ItemStack.class)
public class MixinItemStack {
	/**
	 * @author CursedFlames
	 * @reason Disable increasing repair costs (note: identical to inject-at-head + unconditional cancel)
	 */
	@Overwrite
	public int getBaseRepairCost() {
		return 0;
	}

	/**
	 * @author CursedFlames
	 * @reason Disable increasing repair costs (note: identical to inject-at-head + unconditional cancel)
	 */
	@Overwrite
	public void setRepairCost(int cost) {
		// intentionally empty
	}
}
