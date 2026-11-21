package team.unnamed.creative.metadata;

import java.util.Collection;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;
import org.jetbrains.annotations.ApiStatus.NonExtendable;
import org.jetbrains.annotations.ApiStatus.ScheduledForRemoval;
import team.unnamed.creative.metadata.animation.AnimationMeta;
import team.unnamed.creative.metadata.filter.FilterMeta;
import team.unnamed.creative.metadata.gui.GuiMeta;
import team.unnamed.creative.metadata.language.LanguageMeta;
import team.unnamed.creative.metadata.overlays.OverlaysMeta;
import team.unnamed.creative.metadata.pack.PackMeta;
import team.unnamed.creative.metadata.sodium.SodiumMeta;
import team.unnamed.creative.metadata.texture.TextureMeta;
import team.unnamed.creative.metadata.villager.VillagerMeta;

@NonExtendable
public interface Metadata {
   /** @deprecated */
   @Deprecated
   @ScheduledForRemoval(
      inVersion = "2.0.0"
   )
   Metadata EMPTY = MetadataImpl.EMPTY;

   @Contract("-> new")
   static @NotNull Builder metadata() {
      return new MetadataImpl.BuilderImpl();
   }

   static @NotNull Metadata empty() {
      return MetadataImpl.EMPTY;
   }

   /** @deprecated */
   @Deprecated
   @Contract("-> new")
   @ScheduledForRemoval(
      inVersion = "2.0.0"
   )
   static @NotNull Builder builder() {
      return metadata();
   }

   @Unmodifiable @NotNull Collection<MetadataPart> parts();

   <T extends MetadataPart> @Nullable T meta(@NotNull Class<T> var1);

   @Contract("-> new")
   @NotNull Builder toBuilder();

   public interface Builder {
      @NotNull Builder parts(@NotNull Collection<MetadataPart> var1);

      @NotNull Builder addPart(@NotNull MetadataPart var1);

      /** @deprecated */
      @Deprecated
      @ScheduledForRemoval(
         inVersion = "2.0.0"
      )
      @Contract("_, _ -> this")
      default <T extends MetadataPart> @NotNull Builder add(@NotNull Class<T> type, @NotNull T part) {
         return this.addPart(part);
      }

      /** @deprecated */
      @Deprecated
      @ScheduledForRemoval(
         inVersion = "2.0.0"
      )
      @Contract("_ -> this")
      default @NotNull Builder add(@NotNull AnimationMeta meta) {
         return this.add(AnimationMeta.class, meta);
      }

      /** @deprecated */
      @Deprecated
      @ScheduledForRemoval(
         inVersion = "2.0.0"
      )
      @Contract("_ -> this")
      default @NotNull Builder add(@NotNull FilterMeta meta) {
         return this.add(FilterMeta.class, meta);
      }

      /** @deprecated */
      @Deprecated
      @ScheduledForRemoval(
         inVersion = "2.0.0"
      )
      @Contract("_ -> this")
      default @NotNull Builder add(@NotNull LanguageMeta meta) {
         return this.add(LanguageMeta.class, meta);
      }

      /** @deprecated */
      @Deprecated
      @ScheduledForRemoval(
         inVersion = "2.0.0"
      )
      @Contract("_ -> this")
      default @NotNull Builder add(@NotNull PackMeta meta) {
         return this.add(PackMeta.class, meta);
      }

      /** @deprecated */
      @Deprecated
      @ScheduledForRemoval(
         inVersion = "2.0.0"
      )
      @Contract("_ -> this")
      default @NotNull Builder add(@NotNull TextureMeta meta) {
         return this.add(TextureMeta.class, meta);
      }

      /** @deprecated */
      @Deprecated
      @ScheduledForRemoval(
         inVersion = "2.0.0"
      )
      @Contract("_ -> this")
      default @NotNull Builder add(@NotNull VillagerMeta meta) {
         return this.add(VillagerMeta.class, meta);
      }

      /** @deprecated */
      @Deprecated
      @ScheduledForRemoval(
         inVersion = "2.0.0"
      )
      @Contract("_ -> this")
      default @NotNull Builder add(@NotNull GuiMeta meta) {
         return this.add(GuiMeta.class, meta);
      }

      /** @deprecated */
      @Deprecated
      @ScheduledForRemoval(
         inVersion = "2.0.0"
      )
      @Contract("_ -> this")
      default @NotNull Builder add(@NotNull OverlaysMeta meta) {
         return this.add(OverlaysMeta.class, meta);
      }

      /** @deprecated */
      @Deprecated
      @ScheduledForRemoval(
         inVersion = "2.0.0"
      )
      @Contract("_ -> this")
      default @NotNull Builder add(@NotNull SodiumMeta meta) {
         return this.add(SodiumMeta.class, meta);
      }

      @Contract("-> new")
      @NotNull Metadata build();
   }
}
