package com.nexomc.nexo.compatibilities.worldedit;

import com.destroystokyo.paper.event.server.AsyncTabCompleteEvent;
import com.nexomc.nexo.api.NexoBlocks;
import com.nexomc.nexo.api.NexoItems;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import kotlin.Lazy;
import kotlin.LazyKt;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.text.StringsKt;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0010\u000e\n\u0002\b\u0005\u0018\u00002\u00020\u0001B\u0007¢\u0006\u0004\b\u0002\u0010\u0003J\u0010\u0010\u0004\u001a\u00020\u0005*\u00020\u0006H\u0007b\u0002\b\u0007R!\u0010\b\u001a\b\u0012\u0004\u0012\u00020\n0\t8BX\u0082\u0084\u0002¢\u0006\f\n\u0004\b\r\u0010\u000e\u001a\u0004\b\u000b\u0010\f¨\u0006\u000f"},
   d2 = {"Lcom/nexomc/nexo/compatibilities/worldedit/WorldEditListener;", "Lorg/bukkit/event/Listener;", "<init>", "()V", "onTabComplete", "", "Lcom/destroystokyo/paper/event/server/AsyncTabCompleteEvent;", "Lorg/bukkit/event/EventHandler;", "nexoBlockIDs", "", "", "getNexoBlockIDs", "()Ljava/util/List;", "nexoBlockIDs$delegate", "Lkotlin/Lazy;", "com.nexomc:core"}
)
public final class WorldEditListener implements Listener {
   @NotNull
   private final Lazy nexoBlockIDs$delegate = LazyKt.lazy(WorldEditListener::nexoBlockIDs_delegate$lambda$0);

   @EventHandler
   public final void onTabComplete(@NotNull AsyncTabCompleteEvent asyncTabCompleteEvent) {
      String it = asyncTabCompleteEvent.getBuffer();
      int value = 0;
      String str = StringsKt.startsWith(it, "//", false) ? it : null;
      if (str != null) {
         str = StringsKt.substringAfterLast(str, " ", str);
         if (str != null) {
            String arg = str;
            Iterable nexoBlockIDs = (Iterable)this.getNexoBlockIDs();
            Collection destination = (Collection)(new ArrayList());

            for(Object element : nexoBlockIDs) {
               String it_2 = (String)element;
               int value2 = 0;
               if (StringsKt.contains((CharSequence)it_2, (CharSequence)arg, false)) {
                  destination.add(element);
               }
            }

            Collection collection = (Collection)((List)destination);
            List completions = asyncTabCompleteEvent.getCompletions();
            asyncTabCompleteEvent.setCompletions(CollectionsKt.plus(collection, (Iterable)completions));
            return;
         }
      }

   }

   private final List<String> getNexoBlockIDs() {
      Lazy lazy = this.nexoBlockIDs$delegate;
      return (List)lazy.getValue();
   }

   private static final List nexoBlockIDs_delegate$lambda$0() {
      String[] unexcludedItemNames = NexoItems.unexcludedItemNames();
      NexoBlocks nexoBlocks = NexoBlocks.INSTANCE;
      String[] strs = unexcludedItemNames;
      Collection destination = (Collection)(new ArrayList());
      int value = 0;

      for(int i = unexcludedItemNames.length; value < i; ++value) {
         Object element = strs[value];
         int value2 = 0;
         if (NexoBlocks.isCustomBlock((String)element)) {
            destination.add(element);
         }
      }

      Iterable iterable = (Iterable)((List)destination);
      Collection collection = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault(iterable, 10)));

      for(Object item : iterable) {
         String str = (String)item;
         int value3 = 0;
         collection.add("nexo:" + str);
      }

      return (List)collection;
   }
}
