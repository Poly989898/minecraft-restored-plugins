package team.unnamed.creative.atlas;

import java.util.List;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.key.Keyed;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;
import org.jetbrains.annotations.ApiStatus.NonExtendable;
import team.unnamed.creative.overlay.ResourceContainer;
import team.unnamed.creative.part.ResourcePackPart;
import team.unnamed.creative.texture.Texture;

@NonExtendable
public interface Atlas extends ResourcePackPart, Keyed {
   Key BLOCKS = Key.key("blocks");
   Key BANNER_PATTERNS = Key.key("banner_patterns");
   Key CHESTS = Key.key("chests");
   Key SHIELD_PATTERNS = Key.key("shield_patterns");
   Key SHULKER_BOXES = Key.key("shulker_boxes");
   Key MOB_EFFECTS = Key.key("mob_effects");
   Key PAINTINGS = Key.key("paintings");
   Key PARTICLES = Key.key("particles");
   Key ARMOR_TRIMS = Key.key("armor_trims");
   Key DECORATED_POT = Key.key("decorated_pot");
   /** @deprecated */
   @Deprecated
   Key SIGNS = Key.key("signs");
   /** @deprecated */
   @Deprecated
   Key BEDS = Key.key("beds");
   Key GUI = Key.key("gui");
   Key ITEMS = Key.key("items");

   @NotNull Key key();

   @Unmodifiable @NotNull List<AtlasSource> sources();

   @Contract("-> new")
   @NotNull Builder toBuilder();

   default void addTo(@NotNull ResourceContainer resourceContainer) {
      resourceContainer.atlas(this);
   }

   static @NotNull Atlas atlas(@NotNull Key key, @NotNull List<AtlasSource> sources) {
      return new AtlasImpl(key, sources);
   }

   static @NotNull Atlas atlas(@NotNull Key key, @NotNull AtlasSource source) {
      return new AtlasImpl(key, List.of(source));
   }

   static @NotNull Builder atlas() {
      return new AtlasImpl.BuilderImpl();
   }

   boolean contains(@NotNull Texture var1);

   boolean contains(@NotNull Key var1);

   public interface Builder {
      @Nullable Key key();

      @Contract("_ -> this")
      @NotNull Builder key(@NotNull Key var1);

      @Nullable List<AtlasSource> sources();

      @Contract("_ -> this")
      @NotNull Builder sources(@NotNull List<AtlasSource> var1);

      @Contract("_ -> this")
      @NotNull Builder sources(@NotNull AtlasSource... var1);

      @Contract("_ -> this")
      @NotNull Builder addSource(@NotNull AtlasSource var1);

      @Contract("_ -> this")
      @NotNull Builder removeSource(@NotNull AtlasSource var1);

      @Contract("-> new")
      @NotNull Atlas build();
   }
}
