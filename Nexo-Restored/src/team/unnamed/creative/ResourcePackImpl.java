package team.unnamed.creative;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.unnamed.creative.base.Writable;
import team.unnamed.creative.metadata.Metadata;
import team.unnamed.creative.metadata.MetadataPart;
import team.unnamed.creative.metadata.overlays.OverlayEntry;
import team.unnamed.creative.metadata.pack.PackMeta;
import team.unnamed.creative.overlay.Overlay;
import team.unnamed.creative.overlay.ResourceContainer;
import team.unnamed.creative.overlay.ResourceContainerImpl;
import team.unnamed.creative.resources.MergeStrategy;

final class ResourcePackImpl extends ResourceContainerImpl implements ResourcePack {
   private final Map<String, Overlay> overlays = new LinkedHashMap();
   private @Nullable Writable icon;
   private Metadata metadata;

   public @Nullable Writable icon() {
      return this.icon;
   }

   public void icon(@Nullable Writable icon) {
      this.icon = icon;
   }

   public @NotNull Metadata metadata() {
      return this.metadata == null ? Metadata.empty() : this.metadata;
   }

   public void metadata(@NotNull Metadata metadata) {
      Objects.requireNonNull(metadata, "metadata");
      this.metadata = metadata;
   }

   public void overlay(@NotNull Overlay overlay) {
      Objects.requireNonNull(overlay, "overlay");
      this.overlays.put(overlay.directory(), overlay);
   }

   public @Nullable Overlay overlay(@OverlayEntry.Directory @NotNull String directory) {
      Objects.requireNonNull(directory, "directory");
      return (Overlay)this.overlays.get(directory);
   }

   public @NotNull Collection<Overlay> overlays() {
      return this.overlays.values();
   }

   public void merge(@NotNull ResourceContainer other, @NotNull MergeStrategy strategy) {
      super.merge(other, strategy);
      if (other instanceof ResourcePack otherPack) {
         Writable newIcon = otherPack.icon();
         if (strategy == MergeStrategy.override()) {
            if (newIcon != null) {
               this.icon = newIcon;
            }
         } else if (strategy == MergeStrategy.mergeAndFailOnError()) {
            if (newIcon != null && this.icon != null) {
               throw new IllegalStateException("Can't merge resource packs, icons are already set for both packs");
            }

            if (newIcon != null) {
               this.icon = newIcon;
            }
         } else if (strategy == MergeStrategy.mergeAndKeepFirstOnError() && this.icon == null && newIcon != null) {
            this.icon = newIcon;
         }

         Metadata newMetadata = otherPack.metadata();
         if (this.metadata != null && strategy != MergeStrategy.override()) {
            Collection<MetadataPart> oldParts = new HashSet(this.metadata.parts());

            for(MetadataPart part : newMetadata.parts()) {
               if (part.type() == PackMeta.class) {
                  PackMeta oldPackMeta = (PackMeta)this.metadata.meta(PackMeta.class);
                  if (oldPackMeta != null && strategy != MergeStrategy.override()) {
                     PackMeta newPackMeta = (PackMeta)part;
                     oldParts.add(PackMeta.of(oldPackMeta.formats().union(newPackMeta.formats()), oldPackMeta.description()));
                  } else {
                     oldParts.add(part);
                  }
               } else {
                  boolean duplicate = false;

                  for(MetadataPart oldPart : oldParts) {
                     if (oldPart.type() == part.type()) {
                        duplicate = true;
                        break;
                     }
                  }

                  if (duplicate && strategy == MergeStrategy.mergeAndFailOnError()) {
                     throw new IllegalStateException("Can't merge resource packs, metadata part of type " + part.type().getSimpleName() + " is already set for both packs");
                  }

                  oldParts.add(part);
               }
            }

            this.metadata = Metadata.metadata().parts(oldParts).build();
         } else {
            this.metadata = newMetadata;
         }

         for(Overlay overlay : otherPack.overlays()) {
            Overlay existingOverlay = (Overlay)this.overlays.get(overlay.directory());
            if (existingOverlay == null) {
               this.overlays.put(overlay.directory(), overlay);
            } else {
               existingOverlay.merge(overlay, strategy);
            }
         }

      }
   }
}
