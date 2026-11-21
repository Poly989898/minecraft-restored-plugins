package com.nexomc.nexo.pack;

import com.nexomc.nexo.configs.Settings;
import com.nexomc.nexo.nms.NMSHandlers;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import team.unnamed.creative.ResourcePack;
import team.unnamed.creative.metadata.overlays.OverlayEntry;
import team.unnamed.creative.metadata.overlays.OverlaysMeta;
import team.unnamed.creative.metadata.pack.PackFormat;
import team.unnamed.creative.metadata.pack.PackMeta;
import team.unnamed.creative.overlay.Overlay;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000$\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\u0018\u0002\n\u0002\b\u0002\bÆ\u0002\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u000e\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0007J\u0014\u0010\b\u001a\u00020\t*\u00020\n2\u0006\u0010\u000b\u001a\u00020\nH\u0002¨\u0006\f"},
   d2 = {"Lcom/nexomc/nexo/pack/Overlays;", "", "<init>", "()V", "addToResourcepack", "", "resourcePack", "Lteam/unnamed/creative/ResourcePack;", "overlaps", "", "Lteam/unnamed/creative/metadata/pack/PackFormat;", "other", "com.nexomc:core"}
)
public final class Overlays {
   @NotNull
   public static final Overlays INSTANCE = new Overlays();

   private Overlays() {
   }

   public final void addToResourcepack(@NotNull ResourcePack resourcePack) {
      java.util.List list;
      boolean skipUnsupported;
      PackFormat packFormat;
      block: {
         skipUnsupported = Settings.PACK_SKIP_UNSUPPORTED_OVERLAYS.toBool();
         PackMeta packMeta = resourcePack.packMeta();
         if (packMeta != null) {
            packFormat = packMeta.formats();
            if (packFormat != null) {
               break block;
            }
         }

         packFormat = PackFormat.format(NMSHandlers.handler().resourcepackFormat());
      }

      PackFormat mainFormats;
      block: {
         mainFormats = packFormat;
         OverlaysMeta overlaysMeta = resourcePack.overlaysMeta();
         if (overlaysMeta != null) {
            List list2 = overlaysMeta.entries();
            if (list2 != null) {
               list = CollectionsKt.toMutableList((Collection)list2);
               if (list != null) {
                  break block;
               }
            }
         }

         list = (List)(new ArrayList());
      }

      List entries = list;
      Iterable iterable2 = (Iterable)kotlin.enums.EnumEntriesKt.enumEntries(NexoOverlay.values());

      for(Object element : iterable2) {
         NexoOverlay it = (NexoOverlay)element;
         int value = 0;
         resourcePack.overlay(it.getOverlay());
         ((Collection)entries).add(it.getEntry());
      }

      if (skipUnsupported) {
         Collection sortedEntries = (Collection)entries;
         Iterable iterable3 = (Iterable)entries;
         Collection destination = (Collection)(new ObjectOpenHashSet());

         for(Object obj : iterable3) {
            OverlayEntry it = (OverlayEntry)obj;
            int value2 = 0;
            Overlays overlays = INSTANCE;
            PackFormat formats = it.formats();
            if (!overlays.overlaps(mainFormats, formats)) {
               destination.add(obj);
            }
         }

         iterable3 = (Iterable)((ObjectOpenHashSet)destination);

         for(Object obj2 : iterable3) {
            OverlayEntry entry = (OverlayEntry)obj2;
            int value3 = 0;
            resourcePack.overlays().removeIf((a0_ -> Overlays.addToResourcepack$lambda$2$1((b0_ -> Overlays.addToResourcepack$lambda$2$0(entry, (Overlay)b0_)), a0_)));
         }

         CollectionsKt.removeAll(sortedEntries, iterable3);
      }

      Iterable iterable4 = (Iterable)entries;
      HashSet hashSet = new HashSet();
      ArrayList arrayList = new ArrayList();

      for(Object obj3 : iterable4) {
         OverlayEntry it = (OverlayEntry)obj3;
         int value4 = 0;
         Object key = it.directory();
         if (hashSet.add(key)) {
            arrayList.add(obj3);
         }
      }

      Iterable iterable = (Iterable)((List)arrayList);
      Comparator comparator = new Overlays$addToResourcepack$$inlined$compareBy$1();
      List sortedEntries = CollectionsKt.sortedWith(iterable, new Overlays$addToResourcepack$$inlined$thenBy$1(comparator));
      resourcePack.overlaysMeta(OverlaysMeta.of(sortedEntries));
   }

   private final boolean overlaps(PackFormat packFormat, PackFormat other) {
      return packFormat.min().compareTo(other.max()) <= 0 && other.min().compareTo(packFormat.max()) <= 0;
   }

   private static final boolean addToResourcepack$lambda$2$0(OverlayEntry entry, Overlay it) {
      return Intrinsics.areEqual(it.directory(), entry.directory());
   }

   private static final boolean addToResourcepack$lambda$2$1(Function1 tmp0, Object p0) {
      return (Boolean)tmp0.invoke(p0);
   }
}
