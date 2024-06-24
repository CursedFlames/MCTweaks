package cursedflames.pale;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class StatusEffectPale extends MobEffect {
	public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath("pale", "pale");

	public StatusEffectPale(MobEffectCategory type, int color) {
		super(type, color);
	}

	@Override
	public boolean applyEffectTick(LivingEntity entity, int amplifier) {
		if (entity.level().isDay() && !entity.fireImmune() && !entity.isInWaterRainOrBubble() && !entity.isInPowderSnow) {
			BlockPos pos = BlockPos.containing(entity.getX(), entity.getEyeY(), entity.getZ());
			if (entity.level().canSeeSky(pos)) {
				var biome = entity.level().getBiome(pos);
				float damage = (biome.isBound() && !biome.value().hasPrecipitation()) ? 2 : 1;
				entity.hurt(entity.damageSources().onFire(), damage);
			}
		}
		return true;
	}

	@Override
	public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
		return (duration % (40 * Math.max(1, 4-amplifier))) == 0;
	}
}
