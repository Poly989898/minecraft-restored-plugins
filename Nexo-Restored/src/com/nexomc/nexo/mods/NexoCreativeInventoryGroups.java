package com.nexomc.nexo.mods;

import com.nexomc.nexo.NexoPlugin;
import com.nexomc.nexo.api.NexoItems;
import com.nexomc.nexo.items.ItemBuilder;
import com.nexomc.nexo.utils.FileUtilsKt;
import com.nexomc.nexo.utils.NexoYamlKt;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.collections.MapsKt;
import kotlin.io.FilesKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import net.kyori.adventure.key.Key;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000:\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u000f\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0003\b\u0086\b\u0018\u0000 \"2\u00020\u0001:\u0001\"B/\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\f\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\b0\u0007\u0012\b\b\u0002\u0010\t\u001a\u00020\b¢\u0006\u0004\b\n\u0010\u000bB\u0011\b\u0016\u0012\u0006\u0010\f\u001a\u00020\r¢\u0006\u0004\b\n\u0010\u000eJ\t\u0010\u0017\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0018\u001a\u00020\u0005HÆ\u0003J\u000f\u0010\u0019\u001a\b\u0012\u0004\u0012\u00020\b0\u0007HÆ\u0003J\t\u0010\u001a\u001a\u00020\bHÆ\u0003J7\u0010\u001b\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\u000e\b\u0002\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\b0\u00072\b\b\u0002\u0010\t\u001a\u00020\bHÆ\u0001J\u0014\u0010\u001c\u001a\u00020\u001d2\b\u0010\u001e\u001a\u0004\u0018\u00010\u0001HÖ\u0083\u0004J\n\u0010\u001f\u001a\u00020 HÖ\u0081\u0004J\n\u0010!\u001a\u00020\u0005HÖ\u0081\u0004R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010R\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u0012R\u0017\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\b0\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0014R\u0011\u0010\t\u001a\u00020\b¢\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u0016¨\u0006#"},
   d2 = {"Lcom/nexomc/nexo/mods/NexoCreativeInventoryGroups;", "", "id", "Lnet/kyori/adventure/key/Key;", "title", "", "items", "", "Lorg/bukkit/inventory/ItemStack;", "icon", "<init>", "(Lnet/kyori/adventure/key/Key;Ljava/lang/String;Ljava/util/List;Lorg/bukkit/inventory/ItemStack;)V", "section", "Lorg/bukkit/configuration/ConfigurationSection;", "(Lorg/bukkit/configuration/ConfigurationSection;)V", "getId", "()Lnet/kyori/adventure/key/Key;", "getTitle", "()Ljava/lang/String;", "getItems", "()Ljava/util/List;", "getIcon", "()Lorg/bukkit/inventory/ItemStack;", "component1", "component2", "component3", "component4", "copy", "equals", "", "other", "hashCode", "", "toString", "Companion", "com.nexomc:core"}
)
public final class NexoCreativeInventoryGroups {
   @NotNull
   public static final Companion Companion = new Companion();
   @NotNull
   private final Key id;
   @NotNull
   private final String title;
   @NotNull
   private final List<ItemStack> items;
   @NotNull
   private final ItemStack icon;

   public NexoCreativeInventoryGroups(@NotNull Key id, @NotNull String title, @NotNull List<ItemStack> items, @NotNull ItemStack icon) {
      super();
      this.id = id;
      this.title = title;
      this.items = items;
      this.icon = icon;
   }

   @NotNull
   public final Key getId() {
      return this.id;
   }

   @NotNull
   public final String getTitle() {
      return this.title;
   }

   @NotNull
   public final List<ItemStack> getItems() {
      return this.items;
   }

   @NotNull
   public final ItemStack getIcon() {
      return this.icon;
   }

   public NexoCreativeInventoryGroups(@NotNull ConfigurationSection section) {
      Key key = Key.key(section.getName());
      String str = NexoYamlKt.stringOrEmpty(section, "title");
      Iterable iterable = (Iterable)NexoYamlKt.sectionListOrSingle(section, "items");
      String str2 = str;
      Key key2 = key;
      Collection destination = (Collection)(new ArrayList());

      for(Object element : iterable) {
         ConfigurationSection it = (ConfigurationSection)element;
         int value = 0;
         Iterable iterable2 = (Iterable)NexoYamlKt.stringListOrSingle(it, "folder", (String)null);
         Collection collection = (Collection)(new ArrayList());

         for(Object obj : iterable2) {
            String folder = (String)obj;
            int value2 = 0;
            File dataFolder = NexoPlugin.Companion.instance().getDataFolder();
            String[] strs = new String[]{"items"};
            Iterable iterable3 = (Iterable)FileUtilsKt.listDirectories(FileUtilsKt.resolve(dataFolder, strs), (a0_ -> NexoCreativeInventoryGroups._init_$lambda$0$0$0(folder, (File)a0_)));
            CollectionsKt.addAll(collection, iterable3);
         }

         List folders = (List)collection;
         Iterable iterable4 = (Iterable)folders;
         Collection collection2 = (Collection)(new ArrayList());

         for(Object obj2 : iterable4) {
            File it_19 = (File)obj2;
            int value3 = 0;
            Iterable iterable5 = (Iterable)FileUtilsKt.listYamlFiles(it_19, true);
            CollectionsKt.addAll(collection2, iterable5);
         }

         iterable4 = (Iterable)((List)collection2);
         collection2 = (Collection)(new ArrayList());

         for(Object obj3 : iterable4) {
            File it_17 = (File)obj3;
            int file = 0;
            Map map = (Map)NexoItems.INSTANCE.getItemMap$com_nexomc_core().get(it_17);
            if (map == null) {
               map = MapsKt.emptyMap();
            }

            Iterable iterable6 = (Iterable)map.values();
            Collection collection3 = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault(iterable6, 10)));

            for(Object obj4 : iterable6) {
               ItemBuilder it_15 = (ItemBuilder)obj4;
               int value4 = 0;
               collection3.add(it_15.build());
            }

            Iterable iterable7 = (Iterable)((List)collection3);
            CollectionsKt.addAll(collection3, iterable7);
         }

         List folderFiles = (List)collection2;
         Iterable iterable8 = (Iterable)NexoYamlKt.stringListOrSingle(it, "file", (String)null);
         Collection collection4 = (Collection)(new ArrayList());

         for(Object obj5 : iterable8) {
            String file = (String)obj5;
            int value5 = 0;
            Map itemMap = NexoItems.itemMap();
            Map map2 = (Map)(new LinkedHashMap());

            for(Map.Entry entry : (java.lang.Iterable<Map.Entry>)(java.lang.Iterable)itemMap.entrySet()) {
               int value6 = 0;
               if (Intrinsics.areEqual(FilesKt.getNameWithoutExtension((File)entry.getKey()), file)) {
                  map2.put(entry.getKey(), entry.getValue());
               }
            }

            Iterable iterable9 = (Iterable)map2.values();
            Collection collection5 = (Collection)(new ArrayList());

            for(Object obj6 : iterable9) {
               Map it_4 = (Map)obj6;
               int value7 = 0;
               Iterable iterable10 = (Iterable)it_4.values();
               Collection collection6 = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault(iterable10, 10)));

               for(Object obj7 : iterable10) {
                  ItemBuilder it_2 = (ItemBuilder)obj7;
                  int value8 = 0;
                  collection6.add(it_2.build());
               }

               Iterable iterable11 = (Iterable)((List)collection6);
               CollectionsKt.addAll(collection6, iterable11);
            }

            Iterable iterable12 = (Iterable)((List)collection5);
            CollectionsKt.addAll(collection5, iterable12);
         }

         List files = (List)collection4;
         ItemStack item = Companion.itemStack(it);
         Iterable iterable13 = (Iterable)CollectionsKt.plus((Collection)CollectionsKt.plus((Collection)folderFiles, (Iterable)files), item);
         CollectionsKt.addAll(collection4, iterable13);
      }

      List list;
      ItemStack itemStack;
      block: {
         list = (List)destination;
         ConfigurationSection configurationSection = NexoYamlKt.section(section, "icon");
         if (configurationSection != null) {
            itemStack = Companion.itemStack(configurationSection);
            if (itemStack != null) {
               break block;
            }
         }

         itemStack = ItemStack.of(Material.PAPER);
      }

      this(key2, str2, list, itemStack);
   }

   @NotNull
   public final Key component1() {
      return this.id;
   }

   @NotNull
   public final String component2() {
      return this.title;
   }

   @NotNull
   public final List<ItemStack> component3() {
      return this.items;
   }

   @NotNull
   public final ItemStack component4() {
      return this.icon;
   }

   @NotNull
   public final NexoCreativeInventoryGroups copy(@NotNull Key id, @NotNull String title, @NotNull List<ItemStack> items, @NotNull ItemStack icon) {
      return new NexoCreativeInventoryGroups(id, title, items, icon);
   }

   @NotNull
   public String toString() {
      return "NexoCreativeInventoryGroups(id=" + this.id + ", title=" + this.title + ", items=" + this.items + ", icon=" + this.icon + ")";
   }

   public int hashCode() {
      int result = this.id.hashCode();
      result = result * 31 + this.title.hashCode();
      result = result * 31 + this.items.hashCode();
      result = result * 31 + this.icon.hashCode();
      return result;
   }

   public boolean equals(@Nullable Object other) {
      if (this == other) {
         return true;
      } else if (!(other instanceof NexoCreativeInventoryGroups)) {
         return false;
      } else {
         NexoCreativeInventoryGroups nexoCreativeInventoryGroups = (NexoCreativeInventoryGroups)other;
         if (!Intrinsics.areEqual(this.id, nexoCreativeInventoryGroups.id)) {
            return false;
         } else if (!Intrinsics.areEqual(this.title, nexoCreativeInventoryGroups.title)) {
            return false;
         } else if (!Intrinsics.areEqual(this.items, nexoCreativeInventoryGroups.items)) {
            return false;
         } else {
            return Intrinsics.areEqual(this.icon, nexoCreativeInventoryGroups.icon);
         }
      }
   }

   private static final boolean _init_$lambda$0$0$0(String folder, File d) {
      return Intrinsics.areEqual(d.getName(), folder);
   }

   @Metadata(
      mv = {2, 4, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000\u0016\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\f\u0010\u0004\u001a\u00020\u0005*\u00020\u0006H\u0002¨\u0006\u0007"},
      d2 = {"Lcom/nexomc/nexo/mods/NexoCreativeInventoryGroups$Companion;", "", "<init>", "()V", "itemStack", "Lorg/bukkit/inventory/ItemStack;", "Lorg/bukkit/configuration/ConfigurationSection;", "com.nexomc:core"}
   )
   public static final class Companion {
      private Companion() {
      }

      private final ItemStack itemStack(ConfigurationSection configurationSection) {
         ItemBuilder itemBuilder = NexoItems.itemFromId(NexoYamlKt.stringOrNull(configurationSection, "nexo_items"));
         ItemStack itemStack;
         if (itemBuilder != null) {
            itemStack = itemBuilder.build();
            if (itemStack != null) {
               return itemStack;
            }
         }

         block: {
            String str = NexoYamlKt.stringOrNull(configurationSection, "minecraft_type");
            if (str != null) {
               String p0 = str;
               int value = 0;
               Material material = Material.matchMaterial(p0);
               if (material != null) {
                  Material p0_1 = material;
                  value = 0;
                  itemStack = ItemStack.of(p0_1);
                  break block;
               }
            }

            itemStack = null;
         }

         if (itemStack == null) {
            itemStack = ItemStack.of(Material.PAPER);
         }

         return itemStack;
      }
   }
}
