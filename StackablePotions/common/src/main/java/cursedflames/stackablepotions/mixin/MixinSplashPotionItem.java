package cursedflames.stackablepotions.mixin;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.SplashPotionItem;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SplashPotionItem.class)
public abstract class MixinSplashPotionItem extends PotionItem {
	private MixinSplashPotionItem(Properties settings) {
		super(settings);
	}

	@Inject(method="use", at=@At("RETURN"))
	private void onUse(Level world, Player user, InteractionHand hand,
					   CallbackInfoReturnable<InteractionResultHolder<ItemStack>> info) {
		// Add a cooldown so that splash damage/health doesn't become absurdly overpowered when stackable
		user.getCooldowns().addCooldown(this, 20);
	}
}
