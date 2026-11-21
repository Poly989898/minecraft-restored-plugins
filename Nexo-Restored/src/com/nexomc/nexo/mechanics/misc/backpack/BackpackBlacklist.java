package com.nexomc.nexo.mechanics.misc.backpack;

import com.nexomc.nexo.api.NexoItems;
import com.nexomc.nexo.utils.NexoYamlKt;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.bukkit.Bukkit;
import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000@\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010 \n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\b\u0018\u00002\u00020\u0001B=\u0012\u000e\b\u0002\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003\u0012\u000e\b\u0002\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00060\u0003\u0012\u0014\b\u0002\u0010\u0007\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00060\b0\u0003¢\u0006\u0004\b\t\u0010\nB\u0011\b\u0016\u0012\u0006\u0010\u000b\u001a\u00020\f¢\u0006\u0004\b\t\u0010\rJ\u0011\u0010\u0012\u001a\u00020\u00132\u0006\u0010\u0014\u001a\u00020\u0015H\u0086\u0002J\u000f\u0010\u0016\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003HÆ\u0003J\u000f\u0010\u0017\u001a\b\u0012\u0004\u0012\u00020\u00060\u0003HÆ\u0003J\u0015\u0010\u0018\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00060\b0\u0003HÆ\u0003J?\u0010\u0019\u001a\u00020\u00002\u000e\b\u0002\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\u000e\b\u0002\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00060\u00032\u0014\b\u0002\u0010\u0007\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00060\b0\u0003HÆ\u0001J\u0014\u0010\u001a\u001a\u00020\u00132\b\u0010\u001b\u001a\u0004\u0018\u00010\u0001HÖ\u0083\u0004J\n\u0010\u001c\u001a\u00020\u001dHÖ\u0081\u0004J\n\u0010\u001e\u001a\u00020\u0004HÖ\u0081\u0004R\u0017\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000fR\u0017\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00060\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u000fR\u001d\u0010\u0007\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00060\b0\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u000f¨\u0006\u001f"},
   d2 = {"Lcom/nexomc/nexo/mechanics/misc/backpack/BackpackBlacklist;", "", "nexoItems", "", "", "materials", "Lorg/bukkit/Material;", "tags", "Lorg/bukkit/Tag;", "<init>", "(Ljava/util/List;Ljava/util/List;Ljava/util/List;)V", "section", "Lorg/bukkit/configuration/ConfigurationSection;", "(Lorg/bukkit/configuration/ConfigurationSection;)V", "getNexoItems", "()Ljava/util/List;", "getMaterials", "getTags", "contains", "", "item", "Lorg/bukkit/inventory/ItemStack;", "component1", "component2", "component3", "copy", "equals", "other", "hashCode", "", "toString", "com.nexomc:core"}
)
public final class BackpackBlacklist {
   @NotNull
   private final List<String> nexoItems;
   @NotNull
   private final List<Material> materials;
   @NotNull
   private final List<Tag<Material>> tags;

   public BackpackBlacklist(@NotNull List<String> nexoItems, @NotNull List<Material> materials, @NotNull List<Tag<Material>> tags) {
      super();
      this.nexoItems = nexoItems;
      this.materials = materials;
      this.tags = tags;
   }

   @NotNull
   public final List<String> getNexoItems() {
      return this.nexoItems;
   }

   @NotNull
   public final List<Material> getMaterials() {
      return this.materials;
   }

   @NotNull
   public final List<Tag<Material>> getTags() {
      return this.tags;
   }

   public BackpackBlacklist(@NotNull ConfigurationSection section) {
      List list = NexoYamlKt.stringListOrSingle(section, "nexo_item", (String)null);
      List list2 = NexoYamlKt.enumListOrSingle(section, "material", Material.class, (String)null);
      Iterable iterable = (Iterable)NexoYamlKt.namespacedKeyListOrSingle(section, "tag", (String)null);
      List list3 = list2;
      List list4 = list;
      Collection destination = (Collection)(new ArrayList());

      for(Object element : iterable) {
         int value = 0;
         NamespacedKey it = (NamespacedKey)element;
         int value2 = 0;
         Tag tag = Bukkit.getTag("items", it, Material.class);
         if (tag != null) {
            Object obj = tag;
            int value3 = 0;
            destination.add(obj);
         }
      }

      List list5 = (List)destination;
      this(list4, list3, list5);
   }

   public final boolean contains(@NotNull ItemStack item) {
      boolean flag;
      if (!this.materials.contains(item.getType()) && !CollectionsKt.contains((Iterable)this.nexoItems, NexoItems.idFromItem(item))) {
         Iterable iterable = (Iterable)this.tags;
         if (iterable instanceof Collection && ((Collection)iterable).isEmpty()) {
            flag = false;
         } else {
            Iterator iterator = iterable.iterator();

            while(true) {
               if (!iterator.hasNext()) {
                  flag = false;
                  break;
               }

               Object element = iterator.next();
               Tag it = (Tag)element;
               int value = 0;
               if (it.isTagged(item.getType())) {
                  flag = true;
                  break;
               }
            }
         }

         if (!flag) {
            flag = false;
            return flag;
         }
      }

      flag = true;
      return flag;
   }

   @NotNull
   public final List<String> component1() {
      return this.nexoItems;
   }

   @NotNull
   public final List<Material> component2() {
      return this.materials;
   }

   @NotNull
   public final List<Tag<Material>> component3() {
      return this.tags;
   }

   @NotNull
   public final BackpackBlacklist copy(@NotNull List<String> nexoItems, @NotNull List<Material> materials, @NotNull List<Tag<Material>> tags) {
      return new BackpackBlacklist(nexoItems, materials, tags);
   }

   @NotNull
   public String toString() {
      return "BackpackBlacklist(nexoItems=" + this.nexoItems + ", materials=" + this.materials + ", tags=" + this.tags + ")";
   }

   public int hashCode() {
      int result = this.nexoItems.hashCode();
      result = result * 31 + this.materials.hashCode();
      result = result * 31 + this.tags.hashCode();
      return result;
   }

   public boolean equals(@Nullable Object other) {
      if (this == other) {
         return true;
      } else if (!(other instanceof BackpackBlacklist)) {
         return false;
      } else {
         BackpackBlacklist backpackBlacklist = (BackpackBlacklist)other;
         if (!Intrinsics.areEqual(this.nexoItems, backpackBlacklist.nexoItems)) {
            return false;
         } else if (!Intrinsics.areEqual(this.materials, backpackBlacklist.materials)) {
            return false;
         } else {
            return Intrinsics.areEqual(this.tags, backpackBlacklist.tags);
         }
      }
   }

   public BackpackBlacklist() {
      this(kotlin.collections.CollectionsKt.emptyList(), kotlin.collections.CollectionsKt.emptyList(), kotlin.collections.CollectionsKt.emptyList());
   }
}
