package cursedflames.splitshulkers.neoforge;

//? neoforge {
/*import cursedflames.splitshulkers.ShulkerBoxColoring;
import cursedflames.splitshulkers.SplitShulkerBoxes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.registries.RegisterEvent;

import static cursedflames.splitshulkers.SplitShulkerBoxes.CREATIVE_TAB_ID;
import static cursedflames.splitshulkers.SplitShulkerBoxes.getAllShulkerBoxes;
import static cursedflames.splitshulkers.SplitShulkerBoxes.secondaryColorToTag;

@Mod(SplitShulkerBoxes.MOD_ID)
public class NeoforgeEntrypoint {
    public NeoforgeEntrypoint(IEventBus eventBus) {
		eventBus.addListener(this::register);
	}

	public void register(RegisterEvent event) {
		event.register(Registries.CREATIVE_MODE_TAB, helper -> {
			var iconStack = new ItemStack(Blocks.BLACK_SHULKER_BOX);
			var tag = new CompoundTag();
			secondaryColorToTag(DyeColor.WHITE, tag);
			BlockItem.setBlockEntityData(iconStack, BlockEntityType.SHULKER_BOX, tag);
			var itemGroup = CreativeModeTab
					.builder()
					.withTabsBefore(CreativeModeTabs.SPAWN_EGGS)
					.title(Component.translatable("splitshulkers.category"))
					.icon(() -> iconStack)
					.displayItems((params, output) -> output.acceptAll(getAllShulkerBoxes()))
					.build();
			helper.register(CREATIVE_TAB_ID, itemGroup);
		});
		event.register(Registries.RECIPE_SERIALIZER, helper -> {
			var serializer = new CustomRecipe.Serializer<>(ShulkerBoxColoring::new);
			helper.register(ResourceLocation.fromNamespaceAndPath("splitshulkers", "crafting_special_shulkerboxcoloring"), serializer);
			ShulkerBoxColoring.setRecipeSerializer(serializer);
		});
	}

}
*///?}
