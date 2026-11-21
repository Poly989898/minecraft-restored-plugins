package com.nexomc.nexo.items;

import com.nexomc.nexo.api.NexoItems;
import java.util.ArrayList;
import java.util.List;
import kotlin.Metadata;
import org.jetbrains.annotations.NotNull;
import team.unnamed.creative.ResourcePack;
import team.unnamed.creative.item.Item;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000$\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010!\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\bÆ\u0002\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u000e\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\fR\u001a\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005X\u0080\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\b¨\u0006\r"},
   d2 = {"Lcom/nexomc/nexo/items/ItemModelParser;", "", "<init>", "()V", "extraItems", "", "Lcom/nexomc/nexo/items/ItemModelBuilder;", "getExtraItems$com_nexomc_core", "()Ljava/util/List;", "startParsing", "", "resourcePack", "Lteam/unnamed/creative/ResourcePack;", "com.nexomc:core"}
)
public final class ItemModelParser {
   @NotNull
   public static final ItemModelParser INSTANCE = new ItemModelParser();
   @NotNull
   private static final List<ItemModelBuilder> extraItems = (List)(new ArrayList());

   private ItemModelParser() {
   }

   @NotNull
   public final List<ItemModelBuilder> getExtraItems$com_nexomc_core() {
      return extraItems;
   }

   public final void startParsing(@NotNull ResourcePack resourcePack) {
      Iterable items$com_nexomc_core = (Iterable)NexoItems.INSTANCE.getItems$com_nexomc_core();

      for(Object element : items$com_nexomc_core) {
         ItemBuilder item = (ItemBuilder)element;
         int value = 0;
         ItemModelBuilder itemModel = item.getNexoMeta().getItemModel();
         if (itemModel != null) {
            Item parse = itemModel.parse();
            if (parse != null) {
               parse.addTo(resourcePack);
            }
         }
      }

      items$com_nexomc_core = (Iterable)extraItems;

      for(Object obj : items$com_nexomc_core) {
         ItemModelBuilder it = (ItemModelBuilder)obj;
         int value2 = 0;
         Item item2 = it.parse();
         if (item2 != null) {
            item2.addTo(resourcePack);
         }
      }

   }
}
