package cursedflames.pale.mixin;

import cursedflames.pale.Pale;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodProperties;
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

	@Shadow public abstract MobEffectInstance getEffect(Holder<MobEffect> holder);

	private MixinLivingEntity(EntityType<?> type, Level world) {
		super(type, world);
	}

	// Target after vanilla effects are applied, but before the stack size is decremented.
	@Inject(method = "eat(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/food/FoodProperties;)Lnet/minecraft/world/item/ItemStack;",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/entity/LivingEntity;addEatEffect(Lnet/minecraft/world/food/FoodProperties;)V",
					ordinal = 0,
					shift = At.Shift.AFTER))
	private void onEatFood(Level world, ItemStack stack, FoodProperties foodProperties, CallbackInfoReturnable<ItemStack> cir) {
		if (foodProperties != Foods.ROTTEN_FLESH) {
			return;
		}

		int baseDuration = 3*60*20;
		int durationIncrement = 10*20;

		MobEffectInstance currentEffect = this.getEffect(Pale.PALE_EFFECT);
		if (currentEffect == null) {
			this.addEffect(new MobEffectInstance(Pale.PALE_EFFECT, baseDuration, 0, false, false, true));
			return;
		}
		// Extend current effect, and amplify effect if below max level
		int level = currentEffect.getAmplifier()+1;
		if (level > 3) {
			level = 3;
			this.playSound(SoundEvents.ZOMBIE_AMBIENT, 0.5f, 0.8f);
		}
		int time = Math.max(currentEffect.getDuration() + durationIncrement, baseDuration);
		currentEffect.update(new MobEffectInstance(Pale.PALE_EFFECT, time, level, false, false, true));
	}
}
