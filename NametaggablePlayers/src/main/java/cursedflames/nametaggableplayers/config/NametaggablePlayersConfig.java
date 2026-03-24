package cursedflames.nametaggableplayers.config;

import cursedflames.nametaggableplayers.NametaggablePlayers;
import me.fzzyhmstrs.fzzy_config.api.ConfigApiJava;
import me.fzzyhmstrs.fzzy_config.api.SaveType;
import me.fzzyhmstrs.fzzy_config.config.Config;
import me.fzzyhmstrs.fzzy_config.util.EnumTranslatable;
import me.fzzyhmstrs.fzzy_config.validation.misc.ValidatedEnum;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class NametaggablePlayersConfig extends Config {
	public static NametaggablePlayersConfig configInstance = ConfigApiJava.registerAndLoadConfig(NametaggablePlayersConfig::new);

	/** dummy function to make <clinit> run */
	public static void init() {}

	public NametaggablePlayersConfig() {
		super(ResourceLocation.fromNamespaceAndPath(NametaggablePlayers.MOD_ID, "config"));
	}

	public enum NametagsUsedOn implements EnumTranslatable {
		SELF,
		OTHERS,
		BOTH;

		public boolean usableOnSelf() {
			return this == SELF || this == BOTH;
		}

		public boolean usableOnOthers() {
			return this == OTHERS || this == BOTH;
		}

		@NotNull @Override public String prefix() {
			return NametaggablePlayers.MOD_ID + ".nametags_used_on";
		}
	}

	public ValidatedEnum<NametagsUsedOn> nametagsUsedOn = new ValidatedEnum<>(NametagsUsedOn.BOTH, ValidatedEnum.WidgetType.POPUP);
	public boolean resetNamesOnDeath = false;

	@Override
	public @NotNull SaveType saveType() {
		return SaveType.SEPARATE;
	}
}
