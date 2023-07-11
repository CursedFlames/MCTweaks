package cursedflames.splitshulkers.mixin.client;

import cursedflames.splitshulkers.client.ShulkerModelGetter;
import net.minecraft.client.model.ShulkerModel;
import net.minecraft.client.model.geom.ModelPart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ShulkerModel.class)
public abstract class ShulkerModelAccess implements ShulkerModelGetter {
	@Accessor
	public abstract ModelPart getBase();

	@Accessor
	public abstract ModelPart getLid();
}
