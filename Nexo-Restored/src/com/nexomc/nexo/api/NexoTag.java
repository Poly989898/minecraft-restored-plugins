package com.nexomc.nexo.api;

import com.nexomc.nexo.items.ItemBuilder;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.JvmStatic;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010 \n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\bÆ\u0002\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u001a\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u00052\u0006\u0010\u0007\u001a\u00020\u0006H\u0007b\u0002\b\bJ\u0012\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u00060\nH\u0007b\u0002\b\b¨\u0006\u000b"},
   d2 = {"Lcom/nexomc/nexo/api/NexoTag;", "", "<init>", "()V", "itemIds", "", "", "tag", "Lkotlin/jvm/JvmStatic;", "tags", "Lit/unimi/dsi/fastutil/objects/ObjectLinkedOpenHashSet;", "com.nexomc:core"}
)
public final class NexoTag {
   @NotNull
   public static final NexoTag INSTANCE = new NexoTag();

   private NexoTag() {
   }

   @JvmStatic
   @NotNull
   public static final List<String> itemIds(@NotNull String tag) {
      Iterable entrySet = (Iterable)NexoItems.entries().entrySet();
      Collection destination = (Collection)(new ArrayList());

      for(Object element : entrySet) {
         Map.Entry it = (Map.Entry)element;
         int value = 0;
         if (((ItemBuilder)it.getValue()).hasNexoTag(tag)) {
            destination.add(element);
         }
      }

      entrySet = (Iterable)((List)destination);
      destination = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault(entrySet, 10)));

      for(Object item : entrySet) {
         Map.Entry it = (Map.Entry)item;
         int value2 = 0;
         destination.add((String)it.getKey());
      }

      return (List)destination;
   }

   @JvmStatic
   @NotNull
   public static final ObjectLinkedOpenHashSet<String> tags() {
      Iterable items = (Iterable)NexoItems.items();
      Collection destination = (Collection)(new ObjectLinkedOpenHashSet());

      for(Object element : items) {
         ItemBuilder it = (ItemBuilder)element;
         int value = 0;
         Iterable list = (Iterable)it.getNexoTags();
         CollectionsKt.addAll(destination, list);
      }

      return (ObjectLinkedOpenHashSet)destination;
   }
}
