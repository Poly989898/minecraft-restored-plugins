package com.nexomc.nexo.pack;

import java.util.List;
import kotlin.Lazy;
import kotlin.LazyKt;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.enums.EnumEntries;
import org.jetbrains.annotations.NotNull;
import team.unnamed.creative.ResourcePack;
import team.unnamed.creative.metadata.overlays.OverlayEntry;
import team.unnamed.creative.metadata.pack.FormatVersion;
import team.unnamed.creative.metadata.pack.PackFormat;
import team.unnamed.creative.overlay.Overlay;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u00004\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\b\n\u0002\b\u000f\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\b\u0086\u0081\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\u0019\b\u0002\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005¢\u0006\u0004\b\u0006\u0010\u0007J\u0013\u0010\u0014\u001a\u00070\u0015¢\u0006\u0002\b\u00182\u0006\u0010\u0019\u001a\u00020\u001aR\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000bR\u0011\u0010\u0014\u001a\u00020\u0015¢\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\u0017R\u001b\u0010\u001b\u001a\u00020\u001c8FX\u0086\u0084\u0002¢\u0006\f\n\u0004\b\u001f\u0010 \u001a\u0004\b\u001d\u0010\u001ej\u0002\b\fj\u0002\b\rj\u0002\b\u000ej\u0002\b\u000fj\u0002\b\u0010j\u0002\b\u0011j\u0002\b\u0012j\u0002\b\u0013¨\u0006!"},
   d2 = {"Lcom/nexomc/nexo/pack/NexoOverlay;", "", "id", "", "format", "", "<init>", "(Ljava/lang/String;ILjava/lang/String;I)V", "getId", "()Ljava/lang/String;", "getFormat", "()I", "V1_21_1", "V1_21_3", "V1_21_4", "V1_21_5", "V1_21_6", "V1_21_11", "V26_1", "V26_2", "overlay", "Lteam/unnamed/creative/overlay/Overlay;", "getOverlay", "()Lteam/unnamed/creative/overlay/Overlay;", "Lorg/jetbrains/annotations/Nullable;", "resourcepack", "Lteam/unnamed/creative/ResourcePack;", "entry", "Lteam/unnamed/creative/metadata/overlays/OverlayEntry;", "getEntry", "()Lteam/unnamed/creative/metadata/overlays/OverlayEntry;", "entry$delegate", "Lkotlin/Lazy;", "com.nexomc:core"}
)
public enum NexoOverlay {
   V1_21_1("nexo_1_21_1", 34),
   V1_21_3("nexo_1_21_3", 42),
   V1_21_4("nexo_1_21_4", 46),
   V1_21_5("nexo_1_21_5", 55),
   V1_21_6("nexo_1_21_6", 63),
   V1_21_11("nexo_1_21_11", 75),
   V26_1("nexo_26_1", 84),
   V26_2("nexo_26_2", 88);

   @NotNull
   private final String id;
   private final int format;
   @NotNull
   private final Overlay overlay;
   @NotNull
   private final Lazy entry$delegate;

   private NexoOverlay(String id, int format) {
      this.id = id;
      this.format = format;
      Overlay overlay2 = Overlay.overlay(this.id);
      this.overlay = overlay2;
      this.entry$delegate = LazyKt.lazy(() -> entry_delegate$lambda$0(this));
   }

   @NotNull
   public final String getId() {
      return this.id;
   }

   public final int getFormat() {
      return this.format;
   }

   @NotNull
   public final Overlay getOverlay() {
      return this.overlay;
   }

   @NotNull
   public final Overlay overlay(@NotNull ResourcePack resourcepack) {
      Overlay overlay2 = resourcepack.overlay(this.id);
      if (overlay2 == null) {
         overlay2 = this.overlay;
      }

      return overlay2;
   }

   @NotNull
   public final OverlayEntry getEntry() {
      Lazy lazy = this.entry$delegate;
      Object value = lazy.getValue();
      return (OverlayEntry)value;
   }


   private static final OverlayEntry entry_delegate$lambda$0(NexoOverlay nexoOverlay) {
      NexoOverlay orNull = CollectionsKt.getOrNull(java.util.Arrays.asList(values()), java.util.Arrays.asList(values()).indexOf(nexoOverlay) + 1);
      int maxFormat = orNull != null ? orNull.format + -1 : 199;
      return OverlayEntry.of(PackFormat.format(FormatVersion.of(nexoOverlay.format), FormatVersion.of(maxFormat)), nexoOverlay.id);
   }
}
