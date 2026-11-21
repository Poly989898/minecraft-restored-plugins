package team.unnamed.creative.model;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.key.Keyed;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;
import org.jetbrains.annotations.ApiStatus.NonExtendable;
import org.jetbrains.annotations.ApiStatus.ScheduledForRemoval;
import team.unnamed.creative.overlay.ResourceContainer;
import team.unnamed.creative.part.ResourcePackPart;

@NonExtendable
public interface Model extends ResourcePackPart, Keyed {
   Key ITEM_GENERATED = Key.key("item/generated");
   Key ITEM_HANDHELD = Key.key("item/handheld");
   Key BUILT_IN_ENTITY = Key.key("builtin/entity");
   Key BUILT_IN_GENERATED = Key.key("builtin/generated");
   boolean DEFAULT_AMBIENT_OCCLUSION = true;

   @Contract("-> new")
   static @NotNull Builder model() {
      return new ModelImpl.BuilderImpl();
   }

   /** @deprecated */
   @Deprecated
   @ScheduledForRemoval(
      inVersion = "2.0.0"
   )
   @Contract("-> new")
   static @NotNull Builder builder() {
      return model();
   }

   @NotNull Key key();

   @Nullable Key parent();

   boolean ambientOcclusion();

   @NotNull @Unmodifiable Map<ItemTransform.Type, ItemTransform> display();

   @NotNull ModelTextures textures();

   @Nullable GuiLight guiLight();

   @NotNull @Unmodifiable List<Element> elements();

   /** @deprecated */
   @Deprecated
   @NotNull List<ItemOverride> overrides();

   @Contract("-> new")
   default @NotNull Builder toBuilder() {
      return model().key(this.key()).parent(this.parent()).ambientOcclusion(this.ambientOcclusion()).display(this.display()).textures(this.textures()).guiLight(this.guiLight()).elements(this.elements()).overrides(this.overrides());
   }

   default void addTo(@NotNull ResourceContainer resourceContainer) {
      resourceContainer.model(this);
   }

   public static enum GuiLight {
      FRONT,
      SIDE;
   }

   public interface Builder {
      @Contract("_ -> this")
      @NotNull Builder key(@NotNull Key var1);

      @Contract("_ -> this")
      @NotNull Builder parent(@Nullable Key var1);

      @Contract("_ -> this")
      @NotNull Builder ambientOcclusion(boolean var1);

      @Contract("_ -> this")
      @NotNull Builder display(@NotNull Map<ItemTransform.Type, ItemTransform> var1);

      @Contract("_ -> this")
      @NotNull Builder textures(@NotNull ModelTextures var1);

      @Contract("_ -> this")
      @NotNull Builder guiLight(@Nullable GuiLight var1);

      @Contract("_ -> this")
      @NotNull Builder elements(@NotNull List<Element> var1);

      @Contract("_ -> this")
      default @NotNull Builder elements(@NotNull Element... elements) {
         Objects.requireNonNull(elements, "elements");
         return this.elements(Arrays.asList(elements));
      }

      @Contract("_ -> this")
      @NotNull Builder addElement(@NotNull Element var1);

      /** @deprecated */
      @Deprecated
      @Contract("_ -> this")
      @NotNull Builder overrides(@NotNull List<ItemOverride> var1);

      /** @deprecated */
      @Deprecated
      @Contract("_ -> this")
      default @NotNull Builder overrides(@NotNull ItemOverride... overrides) {
         Objects.requireNonNull(overrides, "overrides");
         return this.overrides(Arrays.asList(overrides));
      }

      /** @deprecated */
      @Deprecated
      @Contract("_ -> this")
      @NotNull Builder addOverride(@NotNull ItemOverride var1);

      @Contract("-> new")
      @NotNull Model build();
   }
}
