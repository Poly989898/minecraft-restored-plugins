package team.unnamed.creative;

import java.util.Collection;
import java.util.Objects;
import java.util.function.Consumer;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.NonExtendable;
import org.jetbrains.annotations.ApiStatus.ScheduledForRemoval;
import team.unnamed.creative.base.Writable;
import team.unnamed.creative.metadata.Metadata;
import team.unnamed.creative.metadata.filter.FilterMeta;
import team.unnamed.creative.metadata.language.LanguageMeta;
import team.unnamed.creative.metadata.overlays.OverlayEntry;
import team.unnamed.creative.metadata.overlays.OverlaysMeta;
import team.unnamed.creative.metadata.pack.FormatVersion;
import team.unnamed.creative.metadata.pack.PackFormat;
import team.unnamed.creative.metadata.pack.PackMeta;
import team.unnamed.creative.metadata.sodium.SodiumMeta;
import team.unnamed.creative.overlay.Overlay;
import team.unnamed.creative.overlay.ResourceContainer;

@NonExtendable
public interface ResourcePack extends ResourceContainer {
   static @NotNull ResourcePack resourcePack() {
      return new ResourcePackImpl();
   }

   /** @deprecated */
   @Deprecated
   @ScheduledForRemoval(
      inVersion = "2.0.0"
   )
   static @NotNull ResourcePack create() {
      return resourcePack();
   }

   @Nullable Writable icon();

   void icon(@Nullable Writable var1);

   @NotNull Metadata metadata();

   void metadata(@NotNull Metadata var1);

   default void editMetadata(@NotNull Consumer<Metadata.Builder> editFunction) {
      Objects.requireNonNull(editFunction, "editFunction");
      Metadata.Builder builder = this.metadata().toBuilder();
      editFunction.accept(builder);
      this.metadata(builder.build());
   }

   default @Nullable PackMeta packMeta() {
      return (PackMeta)this.metadata().meta(PackMeta.class);
   }

   default void packMeta(@NotNull PackMeta packMeta) {
      Objects.requireNonNull(packMeta, "packMeta");
      this.editMetadata((metadata) -> metadata.addPart(packMeta));
   }

   default void packMeta(@NotNull PackFormat format, @NotNull Component description) {
      this.packMeta(PackMeta.of(format, description));
   }

   default void packMeta(@NotNull FormatVersion format, @NotNull Component description) {
      this.packMeta(PackMeta.of(PackFormat.format(format), description));
   }

   default @Nullable PackFormat formats() {
      PackMeta meta = this.packMeta();
      return meta == null ? null : meta.formats();
   }

   default @Nullable Component description() {
      PackMeta meta = this.packMeta();
      return meta == null ? null : meta.description();
   }

   default @Nullable LanguageMeta languageMeta() {
      return (LanguageMeta)this.metadata().meta(LanguageMeta.class);
   }

   default void languageMeta(@NotNull LanguageMeta languageMeta) {
      Objects.requireNonNull(languageMeta, "languageMeta");
      this.editMetadata((metadata) -> metadata.addPart(languageMeta));
   }

   default @Nullable FilterMeta filterMeta() {
      return (FilterMeta)this.metadata().meta(FilterMeta.class);
   }

   default void filterMeta(@NotNull FilterMeta filterMeta) {
      Objects.requireNonNull(filterMeta, "filterMeta");
      this.editMetadata((metadata) -> metadata.addPart(filterMeta));
   }

   default @Nullable OverlaysMeta overlaysMeta() {
      return (OverlaysMeta)this.metadata().meta(OverlaysMeta.class);
   }

   default void overlaysMeta(@NotNull OverlaysMeta overlaysMeta) {
      Objects.requireNonNull(overlaysMeta, "overlaysMeta");
      this.editMetadata((metadata) -> metadata.addPart(overlaysMeta));
   }

   void overlay(@NotNull Overlay var1);

   @Nullable Overlay overlay(@OverlayEntry.Directory @NotNull String var1);

   @NotNull Collection<Overlay> overlays();

   default @Nullable SodiumMeta sodiumMeta() {
      return (SodiumMeta)this.metadata().meta(SodiumMeta.class);
   }

   default void sodiumMeta(@NotNull SodiumMeta sodiumMeta) {
      Objects.requireNonNull(sodiumMeta, "sodiumMeta");
      this.editMetadata((metadata) -> metadata.addPart(sodiumMeta));
   }
}
