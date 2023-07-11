package cursedflames.splitshulkers;

import net.minecraft.world.item.DyeColor;

import javax.annotation.Nullable;

public interface SplitShulkerBoxBlockEntity {
	@Nullable DyeColor splitshulkers_getSecondaryColor();
	void splitshulkers_setSecondaryColor(@Nullable DyeColor color);

	@Nullable DyeColor getColor();
}
