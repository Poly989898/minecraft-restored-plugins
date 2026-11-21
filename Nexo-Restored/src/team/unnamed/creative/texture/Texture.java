package team.unnamed.creative.texture;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.key.Keyed;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.NonExtendable;
import org.jetbrains.annotations.ApiStatus.ScheduledForRemoval;
import team.unnamed.creative.base.Writable;
import team.unnamed.creative.metadata.Metadata;
import team.unnamed.creative.metadata.Metadatable;
import team.unnamed.creative.metadata.animation.AnimationMeta;
import team.unnamed.creative.metadata.texture.TextureMeta;
import team.unnamed.creative.metadata.villager.VillagerMeta;
import team.unnamed.creative.overlay.ResourceContainer;
import team.unnamed.creative.part.ResourcePackPart;

@NonExtendable
public interface Texture extends ResourcePackPart, Keyed, Metadatable {
   static @NotNull Texture texture(@NotNull Key key, @NotNull Writable data) {
      return texture(key, data, Metadata.empty());
   }

   static @NotNull Texture texture(@NotNull Key key, @NotNull Writable data, @NotNull Metadata meta) {
      return new TextureImpl(key, data, meta);
   }

   static @NotNull Builder texture() {
      return new TextureImpl.BuilderImpl();
   }

   /** @deprecated */
   @Deprecated
   @ScheduledForRemoval(
      inVersion = "2.0.0"
   )
   static @NotNull Texture of(@NotNull Key key, @NotNull Writable data) {
      return of(key, data, Metadata.empty());
   }

   /** @deprecated */
   @Deprecated
   @ScheduledForRemoval(
      inVersion = "2.0.0"
   )
   static @NotNull Texture of(@NotNull Key key, @NotNull Writable data, @NotNull Metadata meta) {
      return new TextureImpl(key, data, meta);
   }

   /** @deprecated */
   @Deprecated
   @ScheduledForRemoval(
      inVersion = "2.0.0"
   )
   static @NotNull Builder builder() {
      return new TextureImpl.BuilderImpl();
   }

   @NotNull Key key();

   @Contract(
      value = "_ -> new",
      pure = true
   )
   default @NotNull Texture key(@NotNull Key key) {
      return this.toBuilder().key(key).build();
   }

   @NotNull Writable data();

   @Contract(
      value = "_ -> new",
      pure = true
   )
   default @NotNull Texture data(@NotNull Writable data) {
      return this.toBuilder().data(data).build();
   }

   @NotNull Metadata meta();

   @Contract(
      value = "_ -> new",
      pure = true
   )
   default @NotNull Texture meta(@NotNull Metadata meta) {
      return this.toBuilder().meta(meta).build();
   }

   @Contract("-> new")
   default @NotNull Builder toBuilder() {
      return builder().key(this.key()).data(this.data()).meta(this.meta());
   }

   default void addTo(@NotNull ResourceContainer resourceContainer) {
      resourceContainer.texture(this);
   }

   public interface Builder {
      @Contract("_ -> this")
      @NotNull Builder key(@NotNull Key var1);

      @Nullable Key key();

      @Contract("_ -> this")
      @NotNull Builder data(@NotNull Writable var1);

      @Nullable Writable data();

      @Contract("_ -> this")
      @NotNull Builder meta(@NotNull Metadata var1);

      @NotNull Metadata meta();

      @Contract("_ -> this")
      default @NotNull Builder animationMeta(@NotNull AnimationMeta animationMeta) {
         return this.meta(this.meta().toBuilder().addPart(animationMeta).build());
      }

      @Contract("_ -> this")
      default @NotNull Builder villagerMeta(@NotNull VillagerMeta villagerMeta) {
         return this.meta(this.meta().toBuilder().addPart(villagerMeta).build());
      }

      default @NotNull Builder textureMeta(@NotNull TextureMeta textureMeta) {
         return this.meta(this.meta().toBuilder().addPart(textureMeta).build());
      }

      @NotNull Texture build();
   }
}
