package com.nexomc.nexo.dialog;

import com.nexomc.nexo.decomp.ResultShim;
import com.nexomc.nexo.api.NexoItems;
import com.nexomc.nexo.items.ItemBuilder;
import com.nexomc.nexo.utils.AdventureUtils;
import com.nexomc.nexo.utils.NexoYamlKt;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemLore;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import kotlin.Metadata;
import kotlin.Result;
import kotlin.ResultKt;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u00002\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\b\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\u0006\u0010\b\u001a\u00020\tJ\u0010\u0010\n\u001a\u00020\t2\u0006\u0010\u000b\u001a\u00020\fH\u0002J\t\u0010\r\u001a\u00020\u0003HÆ\u0003J\u0013\u0010\u000e\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u0003HÆ\u0001J\u0014\u0010\u000f\u001a\u00020\u00102\b\u0010\u0011\u001a\u0004\u0018\u00010\u0001HÖ\u0083\u0004J\n\u0010\u0012\u001a\u00020\u0013HÖ\u0081\u0004J\n\u0010\u0014\u001a\u00020\fHÖ\u0081\u0004R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007¨\u0006\u0015"},
   d2 = {"Lcom/nexomc/nexo/dialog/NexoDialogItem;", "", "bodySection", "Lorg/bukkit/configuration/ConfigurationSection;", "<init>", "(Lorg/bukkit/configuration/ConfigurationSection;)V", "getBodySection", "()Lorg/bukkit/configuration/ConfigurationSection;", "buildItem", "Lorg/bukkit/inventory/ItemStack;", "nexoItem", "itemId", "", "component1", "copy", "equals", "", "other", "hashCode", "", "toString", "com.nexomc:core"}
)
public final class NexoDialogItem {
   @NotNull
   private final ConfigurationSection bodySection;

   public NexoDialogItem(@NotNull ConfigurationSection bodySection) {
      super();
      this.bodySection = bodySection;
   }

   @NotNull
   public final ConfigurationSection getBodySection() {
      return this.bodySection;
   }

   @NotNull
   public final ItemStack buildItem() {
      ItemStack itemStack;
      if (this.bodySection.isItemStack("item")) {
         itemStack = this.bodySection.getItemStack("item");
         if (itemStack == null) {
            itemStack = ItemStack.of(Material.PAPER);
         }
      } else if (this.bodySection.isString("nexoItem")) {
         String string = this.bodySection.getString("nexoItem");
         itemStack = this.nexoItem(string);
      } else {
         itemStack = ItemStack.of(Material.PAPER);
      }

      ItemStack itemStack2 = itemStack;
      boolean condition = this.bodySection.contains("itemModel");
      if (condition) {
         int value = 0;
         Key key = NexoYamlKt.key(this.bodySection, "itemModel");
         if (key != null) {
            Key it = key;
            int value2 = 0;
            itemStack2.setData(DataComponentTypes.ITEM_MODEL, it);
         }
      }

      condition = this.bodySection.contains("lore");
      if (condition) {
         int value3 = 0;
         Iterable iterable = (Iterable)NexoYamlKt.stringListOrSingle(this.bodySection, "lore", (String)null);
         MiniMessage minI_MESSAGE = AdventureUtils.INSTANCE.getMINI_MESSAGE();
         Collection destination = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault(iterable, 10)));

         for(Object item : iterable) {
            String p0 = (String)item;
            int value4 = 0;
            destination.add(minI_MESSAGE.deserialize(p0));
         }

         List lore = (List)destination;
         iterable = (Iterable)lore;
         if (CollectionsKt.count(iterable) > 0) {
            List it = (List)iterable;
            int value5 = 0;
            itemStack2.setData(DataComponentTypes.LORE, ItemLore.lore(it));
         }
      }

      return itemStack2;
   }

   private final ItemStack nexoItem(String itemId) {
      NexoDialogItem nexoDialogItem = this;

      Object obj;
      try {
         NexoDialogItem nexoDialogItem2 = nexoDialogItem;
         int value = 0;
         obj = ResultShim.ok(NexoItems.itemFromId(itemId));
      } catch (Throwable e) {
         obj = ResultShim.ok(ResultKt.createFailure(e));
      }

      ItemBuilder builder = (ItemBuilder)(ResultShim.isFailure(obj) ? null : obj);
      if (builder == null) {
         NexoDialogs.INSTANCE.markNeedsReload();
      }

      ItemStack itemStack;
      if (builder != null) {
         itemStack = builder.build();
         if (itemStack != null) {
            return itemStack;
         }
      }

      itemStack = ItemStack.of(Material.PAPER);
      return itemStack;
   }

   @NotNull
   public final ConfigurationSection component1() {
      return this.bodySection;
   }

   @NotNull
   public final NexoDialogItem copy(@NotNull ConfigurationSection bodySection) {
      return new NexoDialogItem(bodySection);
   }

   @NotNull
   public String toString() {
      return "NexoDialogItem(bodySection=" + this.bodySection + ")";
   }

   public int hashCode() {
      return this.bodySection.hashCode();
   }

   public boolean equals(@Nullable Object other) {
      if (this == other) {
         return true;
      } else if (!(other instanceof NexoDialogItem)) {
         return false;
      } else {
         NexoDialogItem nexoDialogItem = (NexoDialogItem)other;
         return Intrinsics.areEqual(this.bodySection, nexoDialogItem.bodySection);
      }
   }
}
