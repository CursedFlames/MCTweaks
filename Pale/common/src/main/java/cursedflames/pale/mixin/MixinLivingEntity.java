package cursedflames.pale.mixin;

import cursedflames.pale.StatusEffectPale;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.Foods;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity extends Entity {
	@Shadow public abstract boolean addEffect(MobEffectInstance effect);

	@Shadow public abstract MobEffectInstance getEffect(MobEffect effect);

	private MixinLivingEntity(EntityType<?> type, Level world) {
		super(type, world);
	}

	// Target after vanilla effects are applied, but before the stack size is decremented.
	@Inject(method = "eat",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/entity/LivingEntity;addEatEffect(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/LivingEntity;)V",
					ordinal = 0,
					shift = At.Shift.AFTER))
	private void onEatFood(Level world, ItemStack stack, CallbackInfoReturnable<ItemStack> cir) {
		if (stack.isEmpty() || stack.getItem().getFoodProperties() != Foods.ROTTEN_FLESH) {
			return;
		}

		int baseDuration = 3*60*20;
		int durationIncrement = 10*20;

		MobEffectInstance currentEffect = this.getEffect(StatusEffectPale.PALE);
		if (currentEffect == null) {
			this.addEffect(new MobEffectInstance(StatusEffectPale.PALE, baseDuration, 0, false, false, true));
			return;
		}
		// Extend current effect, and amplify effect if below max level
		int level = currentEffect.getAmplifier()+1;
		if (level > 3) {
			level = 3;
			this.playSound(SoundEvents.ZOMBIE_AMBIENT, 0.5f, 0.8f);
		}
		int time = Math.max(currentEffect.getDuration() + durationIncrement, baseDuration);
		currentEffect.update(new MobEffectInstance(StatusEffectPale.PALE, time, level, false, false, true));
	}
}
