package com.nexomc.nexo.utils.drops;

import com.nexomc.nexo.decomp.ResultShim;
import com.nexomc.nexo.api.NexoFurniture;
import com.nexomc.nexo.api.NexoItems;
import com.nexomc.nexo.items.ItemBuilder;
import com.nexomc.nexo.mechanics.furniture.FurnitureMechanic;
import com.nexomc.nexo.mechanics.misc.itemtype.ItemTypeMechanic;
import com.nexomc.nexo.mechanics.misc.itemtype.ItemTypeMechanicFactory;
import com.nexomc.nexo.utils.AdventureUtilsKt;
import com.nexomc.nexo.utils.BlockHelpers;
import com.nexomc.nexo.utils.NexoYamlKt;
import com.nexomc.nexo.utils.RangeUtilsKt;
import io.papermc.paper.datacomponent.DataComponentType;
import io.papermc.paper.datacomponent.DataComponentTypes;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import kotlin.Metadata;
import kotlin.Result;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.random.Random;
import kotlin.ranges.IntRange;
import kotlin.text.StringsKt;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000X\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010 \n\u0002\u0010\u000e\n\u0000\n\u0002\u0010!\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0017\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u000e\b\u0086\b\u0018\u0000 A2\u00020\u0001:\u0001ABU\u0012\u000e\b\u0002\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003\u0012\f\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006\u0012\u0006\u0010\b\u001a\u00020\t\u0012\u0006\u0010\n\u001a\u00020\t\u0012\u0006\u0010\u000b\u001a\u00020\u0004\u0012\n\b\u0002\u0010\f\u001a\u0004\u0018\u00010\u0004\u0012\n\b\u0002\u0010\r\u001a\u0004\u0018\u00010\u0004¢\u0006\u0004\b\u000e\u0010\u000fB/\b\u0016\u0012\f\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006\u0012\u0006\u0010\u0010\u001a\u00020\t\u0012\u0006\u0010\u0011\u001a\u00020\t\u0012\u0006\u0010\u000b\u001a\u00020\u0004¢\u0006\u0004\b\u000e\u0010\u0012J\u0010\u0010\u001f\u001a\u00020\u00042\b\u0010 \u001a\u0004\u0018\u00010!J\u0010\u0010\"\u001a\u00020\t2\b\u0010 \u001a\u0004\u0018\u00010!J\u0010\u0010#\u001a\u00020\t2\b\u0010 \u001a\u0004\u0018\u00010!J\u0010\u0010$\u001a\u00020\t2\b\u0010 \u001a\u0004\u0018\u00010!J\u000e\u0010%\u001a\u00020&2\u0006\u0010'\u001a\u00020!J\u001c\u0010(\u001a\b\u0012\u0004\u0012\u00020)0\u00032\u0006\u0010*\u001a\u00020+2\u0006\u0010 \u001a\u00020!J\u0016\u0010,\u001a\u00020-2\u0006\u0010.\u001a\u00020/2\u0006\u0010 \u001a\u00020!J\u000e\u00100\u001a\u00020&2\u0006\u0010 \u001a\u00020!J*\u00101\u001a\b\u0012\u0004\u0012\u00020)0\u00032\f\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u00032\u0006\u0010*\u001a\u00020+2\u0006\u00100\u001a\u00020&J\u0014\u00102\u001a\b\u0012\u0004\u0012\u00020\u00070\u00032\u0006\u00103\u001a\u000204J\u000f\u00105\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003HÆ\u0003J\u000f\u00106\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006HÆ\u0003J\t\u00107\u001a\u00020\tHÆ\u0003J\t\u00108\u001a\u00020\tHÆ\u0003J\t\u00109\u001a\u00020\u0004HÆ\u0003J\u000b\u0010:\u001a\u0004\u0018\u00010\u0004HÆ\u0003J\u000b\u0010;\u001a\u0004\u0018\u00010\u0004HÆ\u0003J_\u0010<\u001a\u00020\u00002\u000e\b\u0002\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\u000e\b\u0002\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u00062\b\b\u0002\u0010\b\u001a\u00020\t2\b\b\u0002\u0010\n\u001a\u00020\t2\b\b\u0002\u0010\u000b\u001a\u00020\u00042\n\b\u0002\u0010\f\u001a\u0004\u0018\u00010\u00042\n\b\u0002\u0010\r\u001a\u0004\u0018\u00010\u0004HÆ\u0001J\u0014\u0010=\u001a\u00020\t2\b\u0010>\u001a\u0004\u0018\u00010\u0001HÖ\u0083\u0004J\n\u0010?\u001a\u00020&HÖ\u0081\u0004J\n\u0010@\u001a\u00020\u0004HÖ\u0081\u0004R\u0017\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0014R\u0017\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006¢\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u0014R\u0011\u0010\b\u001a\u00020\t¢\u0006\b\n\u0000\u001a\u0004\b\b\u0010\u0016R\u0011\u0010\n\u001a\u00020\t¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u0016R\u0011\u0010\u000b\u001a\u00020\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0017\u0010\u0018R\u0013\u0010\f\u001a\u0004\u0018\u00010\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0019\u0010\u0018R\u0013\u0010\r\u001a\u0004\u0018\u00010\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u001a\u0010\u0018R\u0011\u0010\u001b\u001a\u00020\t8F¢\u0006\u0006\u001a\u0004\b\u001b\u0010\u0016R\u0011\u0010\u001c\u001a\u00020\u00008F¢\u0006\u0006\u001a\u0004\b\u001d\u0010\u001e¨\u0006B"},
   d2 = {"Lcom/nexomc/nexo/utils/drops/Drop;", "", "hierarchy", "", "", "loots", "", "Lcom/nexomc/nexo/utils/drops/Loot;", "isSilktouch", "", "isFortune", "sourceID", "minimalType", "bestTool", "<init>", "(Ljava/util/List;Ljava/util/List;ZZLjava/lang/String;Ljava/lang/String;Ljava/lang/String;)V", "silktouch", "fortune", "(Ljava/util/List;ZZLjava/lang/String;)V", "getHierarchy", "()Ljava/util/List;", "getLoots", "()Z", "getSourceID", "()Ljava/lang/String;", "getMinimalType", "getBestTool", "isEmpty", "explosionDrops", "getExplosionDrops", "()Lcom/nexomc/nexo/utils/drops/Drop;", "getItemType", "itemInHand", "Lorg/bukkit/inventory/ItemStack;", "canDrop", "isTypeEnough", "isToolEnough", "getDiff", "", "item", "spawns", "Lcom/nexomc/nexo/utils/drops/DroppedLoot;", "location", "Lorg/bukkit/Location;", "furnitureSpawns", "", "baseEntity", "Lorg/bukkit/entity/ItemDisplay;", "fortuneMultiplier", "dropLoot", "lootToDrop", "player", "Lorg/bukkit/entity/Player;", "component1", "component2", "component3", "component4", "component5", "component6", "component7", "copy", "equals", "other", "hashCode", "toString", "Companion", "com.nexomc:core"}
)
public final class Drop {
   @NotNull
   public static final Companion Companion = new Companion();
   @NotNull
   private final List<String> hierarchy;
   @NotNull
   private final List<Loot> loots;
   private final boolean isSilktouch;
   private final boolean isFortune;
   @NotNull
   private final String sourceID;
   @Nullable
   private final String minimalType;
   @Nullable
   private final String bestTool;

   public Drop(@NotNull List<String> hierarchy, @NotNull List<Loot> loots, boolean isSilktouch, boolean isFortune, @NotNull String sourceID, @Nullable String minimalType, @Nullable String bestTool) {
      super();
      this.hierarchy = hierarchy;
      this.loots = loots;
      this.isSilktouch = isSilktouch;
      this.isFortune = isFortune;
      this.sourceID = sourceID;
      this.minimalType = minimalType;
      this.bestTool = bestTool;
   }

   @NotNull
   public final List<String> getHierarchy() {
      return this.hierarchy;
   }

   @NotNull
   public final List<Loot> getLoots() {
      return this.loots;
   }

   public final boolean isSilktouch() {
      return this.isSilktouch;
   }

   public final boolean isFortune() {
      return this.isFortune;
   }

   @NotNull
   public final String getSourceID() {
      return this.sourceID;
   }

   @Nullable
   public final String getMinimalType() {
      return this.minimalType;
   }

   @Nullable
   public final String getBestTool() {
      return this.bestTool;
   }

   public final boolean isEmpty() {
      return this.loots.isEmpty() && !this.isSilktouch;
   }

   @NotNull
   public final Drop getExplosionDrops() {
      Iterable iterable = (Iterable)this.loots;
      Collection destination = (Collection)(new ArrayList());

      for(Object element : iterable) {
         Loot p0 = (Loot)element;
         int value = 0;
         if (p0.getInExplosion()) {
            destination.add(element);
         }
      }

      List list = CollectionsKt.toMutableList((Collection)((List)destination));
      String str = this.sourceID;
      boolean flag = false;
      boolean flag2 = false;
      List list2 = list;
      return new Drop(list2, flag2, flag, str);
   }

   public Drop(@NotNull List<Loot> loots, boolean silktouch, boolean fortune, @NotNull String sourceID) {
      this(kotlin.collections.CollectionsKt.emptyList(), loots, silktouch, fortune, sourceID, null, null);
   }

   @NotNull
   public final String getItemType(@Nullable ItemStack itemInHand) {
      ItemTypeMechanic mechanic = ItemTypeMechanicFactory.Companion.instance().getMechanic(itemInHand);
      String str;
      if (mechanic != null) {
         str = mechanic.getItemType();
         if (str != null) {
            return str;
         }
      }

      if (itemInHand != null) {
         Material type = itemInHand.getType();
         if (type != null) {
            String str2 = type.name();
            if (str2 != null) {
               str = StringsKt.substringBefore(str2, "_", "");
               return str;
            }
         }
      }

      str = "";
      return str;
   }

   public final boolean canDrop(@Nullable ItemStack itemInHand) {
      CharSequence charSequence = (CharSequence)this.minimalType;
      return charSequence == null || charSequence.length() == 0 || this.isToolEnough(itemInHand) && this.isTypeEnough(itemInHand);
   }

   public final boolean isTypeEnough(@Nullable ItemStack itemInHand) {
      CharSequence charSequence = (CharSequence)this.minimalType;
      if (charSequence == null || charSequence.length() == 0) {
         return true;
      } else {
         String itemType = this.getItemType(itemInHand);
         return ((CharSequence)itemType).length() > 0 && this.hierarchy.indexOf(itemType) >= this.hierarchy.indexOf(this.minimalType);
      }
   }

   public final boolean isToolEnough(@Nullable ItemStack itemInHand) {
      CharSequence charSequence = (CharSequence)this.bestTool;
      if (charSequence == null || charSequence.length() == 0) {
         return true;
      } else {
         Material material;
         String itemID = NexoItems.idFromItem(itemInHand);
         block: {
            if (itemInHand != null) {
               material = itemInHand.getType();
               if (material != null) {
                  break block;
               }
            }

            material = Material.AIR;
         }

         String type = material.name();
         String str = this.bestTool;
         boolean flag;
         if (!Intrinsics.areEqual(str, itemID) && !Intrinsics.areEqual(str, type)) {
            String str2 = this.bestTool.toUpperCase(Locale.ROOT);
            flag = StringsKt.endsWith(type, "_" + str2, false);
         } else {
            flag = true;
         }

         return flag;
      }
   }

   public final int getDiff(@NotNull ItemStack item) {
      return this.minimalType == null ? 0 : this.hierarchy.indexOf(this.getItemType(item)) - this.hierarchy.indexOf(this.minimalType);
   }

   @NotNull
   public final List<DroppedLoot> spawns(@NotNull Location location, @NotNull ItemStack itemInHand) {
      if (this.canDrop(itemInHand) && BlockHelpers.INSTANCE.isLoaded(location)) {
         ItemBuilder itemBuilder = NexoItems.itemFromId(this.sourceID);
         ItemStack baseItem = itemBuilder != null ? itemBuilder.build() : null;
         if (baseItem != null && this.isSilktouch && itemInHand.containsEnchantment(Enchantment.SILK_TOUCH)) {
            location.getWorld().dropItemNaturally(location.toCenterLocation(), baseItem);
            return CollectionsKt.listOf(new DroppedLoot(new Loot(this.sourceID, baseItem, (double)1.0F, new IntRange(1, 1), false, new org.bukkit.configuration.file.YamlConfiguration()), 1));
         } else {
            List list = this.loots;
            Location toCenterLocation = location.toCenterLocation();
            return this.dropLoot(list, toCenterLocation, this.fortuneMultiplier(itemInHand));
         }
      } else {
         return CollectionsKt.emptyList();
      }
   }

   public final void furnitureSpawns(@NotNull ItemDisplay baseEntity, @NotNull ItemStack itemInHand) {
      ItemBuilder itemBuilder = NexoItems.itemFromId(this.sourceID);
      ItemStack baseItem = itemBuilder != null ? itemBuilder.build() : null;
      Location it = baseEntity.getLocation().toCenterLocation();
      int value = 0;
      Location location2 = it.isWorldLoaded() ? it : null;
      if (location2 != null) {
         Location location = location2;
         ItemStack itemStack = NexoFurniture.furnitureItem(baseEntity);
         if (itemStack == null) {
            ItemBuilder itemBuilder2 = NexoItems.itemFromId(this.sourceID);
            itemStack = itemBuilder2 != null ? itemBuilder2.build() : null;
            if (itemStack == null) {
               return;
            }
         }

         ItemStack furnitureItem = itemStack;
         if (baseItem != null) {
            Component data = baseItem.getData(DataComponentTypes.CUSTOM_NAME);
            if (data != null) {
               Component it_2 = data;
               int value2 = 0;
               furnitureItem.setData(DataComponentTypes.CUSTOM_NAME, it_2);
            }
         }

         String str = baseEntity.getPersistentDataContainer().get(FurnitureMechanic.Companion.getDISPLAY_NAME_KEY(), PersistentDataType.STRING);
         Component displayName = str != null ? AdventureUtilsKt.deserialize(str) : null;
         if (displayName != null) {
            furnitureItem.setData(DataComponentTypes.CUSTOM_NAME, displayName);
         }

         Drop it_1 = this;

         try {
            Drop drop = it_1;
            int value3 = 0;
            furnitureItem.resetData((DataComponentType)DataComponentTypes.BLOCK_DATA);
            Object obj = ResultShim.ok(Unit.INSTANCE);
         } catch (Throwable e) {
            Object obj2 = ResultShim.ok(ResultKt.createFailure(e));
         }

         if (this.canDrop(itemInHand)) {
            if (baseItem != null && this.isSilktouch && itemInHand.containsEnchantment(Enchantment.SILK_TOUCH)) {
               Intrinsics.checkNotNullExpressionValue(location.getWorld().dropItemNaturally(location, baseItem), "dropItemNaturally(...)");
            } else {
               Iterable iterable = (Iterable)this.loots;
               Collection destination = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault(iterable, 10)));

               for(Object item : iterable) {
                  Loot loot = (Loot)item;
                  int value4 = 0;
                  Loot loot2;
                  if (!Intrinsics.areEqual(loot.itemStack(), baseItem)) {
                     loot2 = loot;
                  } else if (!NexoYamlKt.isEmpty(loot.getConfig())) {
                     if (displayName == null) {
                        loot2 = loot;
                     } else {
                        ItemStack namedItem = loot.itemStack().clone();
                        namedItem.setData(DataComponentTypes.CUSTOM_NAME, displayName);
                        loot2 = new Loot(this.sourceID, namedItem, loot.getProbability(), loot.getAmount(), false, new org.bukkit.configuration.file.YamlConfiguration());
                     }
                  } else {
                     loot2 = new Loot(this.sourceID, furnitureItem, loot.getProbability(), loot.getAmount(), false, ((org.bukkit.configuration.ConfigurationSection)new org.bukkit.configuration.file.YamlConfiguration()));
                  }

                  destination.add(loot2);
               }

               List loots = (List)destination;
               this.dropLoot(loots, location, this.fortuneMultiplier(itemInHand));
            }

         }
      }
   }

   public final int fortuneMultiplier(@NotNull ItemStack itemInHand) {
      return this.isFortune ? RangeUtilsKt.randomOrMin(new IntRange(1, itemInHand.getEnchantmentLevel(Enchantment.FORTUNE) + 1)) : 1;
   }

   @NotNull
   public final List<DroppedLoot> dropLoot(@NotNull List<Loot> loots, @NotNull Location location, int fortuneMultiplier) {
      Iterable iterable = (Iterable)loots;
      Collection destination = (Collection)(new ArrayList());

      for(Object element : iterable) {
         int value = 0;
         Loot loot = (Loot)element;
         int value2 = 0;
         Integer number = loot.dropNaturally(location, fortuneMultiplier);
         int it = ((Number)number).intValue();
         int value3 = 0;
         Integer number2 = it > 0 ? number : null;
         DroppedLoot droppedLoot;
         if ((it > 0 ? number : null) != null) {
            it = ((Number)number2).intValue();
            value3 = 0;
            droppedLoot = new DroppedLoot(loot, it);
         } else {
            droppedLoot = null;
         }

         if (droppedLoot != null) {
            Object obj = droppedLoot;
            int value4 = 0;
            destination.add(obj);
         }
      }

      return (List)destination;
   }

   @NotNull
   public final List<Loot> lootToDrop(@NotNull Player player) {
      ItemStack itemInMainHand = player.getInventory().getItemInMainHand();
      ItemStack itemInHand = itemInMainHand;
      Iterable iterable = (Iterable)this.loots;
      Collection destination = (Collection)(new ArrayList());

      for(Object element : iterable) {
         Loot loot = (Loot)element;
         int value = 0;
         if (this.canDrop(itemInHand) && Random.Default.nextDouble() < loot.getProbability()) {
            destination.add(element);
         }
      }

      return (List)destination;
   }

   @NotNull
   public final List<String> component1() {
      return this.hierarchy;
   }

   @NotNull
   public final List<Loot> component2() {
      return this.loots;
   }

   public final boolean component3() {
      return this.isSilktouch;
   }

   public final boolean component4() {
      return this.isFortune;
   }

   @NotNull
   public final String component5() {
      return this.sourceID;
   }

   @Nullable
   public final String component6() {
      return this.minimalType;
   }

   @Nullable
   public final String component7() {
      return this.bestTool;
   }

   @NotNull
   public final Drop copy(@NotNull List<String> hierarchy, @NotNull List<Loot> loots, boolean isSilktouch, boolean isFortune, @NotNull String sourceID, @Nullable String minimalType, @Nullable String bestTool) {
      return new Drop(hierarchy, loots, isSilktouch, isFortune, sourceID, minimalType, bestTool);
   }

   @NotNull
   public String toString() {
      return "Drop(hierarchy=" + this.hierarchy + ", loots=" + this.loots + ", isSilktouch=" + this.isSilktouch + ", isFortune=" + this.isFortune + ", sourceID=" + this.sourceID + ", minimalType=" + this.minimalType + ", bestTool=" + this.bestTool + ")";
   }

   public int hashCode() {
      int result = this.hierarchy.hashCode();
      result = result * 31 + this.loots.hashCode();
      result = result * 31 + Boolean.hashCode(this.isSilktouch);
      result = result * 31 + Boolean.hashCode(this.isFortune);
      result = result * 31 + this.sourceID.hashCode();
      result = result * 31 + (this.minimalType == null ? 0 : this.minimalType.hashCode());
      result = result * 31 + (this.bestTool == null ? 0 : this.bestTool.hashCode());
      return result;
   }

   public boolean equals(@Nullable Object other) {
      if (this == other) {
         return true;
      } else if (!(other instanceof Drop)) {
         return false;
      } else {
         Drop drop = (Drop)other;
         if (!Intrinsics.areEqual(this.hierarchy, drop.hierarchy)) {
            return false;
         } else if (!Intrinsics.areEqual(this.loots, drop.loots)) {
            return false;
         } else if (this.isSilktouch != drop.isSilktouch) {
            return false;
         } else if (this.isFortune != drop.isFortune) {
            return false;
         } else if (!Intrinsics.areEqual(this.sourceID, drop.sourceID)) {
            return false;
         } else if (!Intrinsics.areEqual(this.minimalType, drop.minimalType)) {
            return false;
         } else {
            return Intrinsics.areEqual(this.bestTool, drop.bestTool);
         }
      }
   }

   @JvmStatic
   @NotNull
   public static final Drop createDrop(@Nullable List<String> toolTypes, @NotNull ConfigurationSection dropSection, @NotNull String sourceID) {
      return Companion.createDrop(toolTypes, dropSection, sourceID);
   }

   @JvmStatic
   @NotNull
   public static final Drop emptyDrop() {
      return Companion.emptyDrop();
   }

   @JvmStatic
   @NotNull
   public static final Drop emptyDrop(@NotNull List<Loot> loots) {
      return Companion.emptyDrop(loots);
   }

   @Metadata(
      mv = {2, 4, 0},
      k = 1,
      xi = 48,
      d1 = {"\u00004\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010!\n\u0002\u0018\u0002\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J,\u0010\u0004\u001a\u00020\u00052\u000e\u0010\u0006\u001a\n\u0012\u0004\u0012\u00020\b\u0018\u00010\u00072\u0006\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\bH\u0007b\u0002\b\fJ\f\u0010\r\u001a\u00020\u0005H\u0007b\u0002\b\fJ\u001a\u0010\r\u001a\u00020\u00052\f\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u00100\u000fH\u0007b\u0002\b\f¨\u0006\u0011"},
      d2 = {"Lcom/nexomc/nexo/utils/drops/Drop$Companion;", "", "<init>", "()V", "createDrop", "Lcom/nexomc/nexo/utils/drops/Drop;", "toolTypes", "", "", "dropSection", "Lorg/bukkit/configuration/ConfigurationSection;", "sourceID", "Lkotlin/jvm/JvmStatic;", "emptyDrop", "loots", "", "Lcom/nexomc/nexo/utils/drops/Loot;", "com.nexomc:core"}
   )
   public static final class Companion {
      private Companion() {
      }

      @JvmStatic
      @NotNull
      public final Drop createDrop(@Nullable List<String> toolTypes, @NotNull ConfigurationSection dropSection, @NotNull String sourceID) {
         List list;
         if (dropSection.contains("loots")) {
            Iterable iterable = (Iterable)NexoYamlKt.sectionListOrSingle(dropSection, "loots");
            Collection destination = (Collection)((List)(new ArrayList()));

            for(Object item : iterable) {
               ConfigurationSection it = (ConfigurationSection)item;
               int value = 0;
               destination.add(new Loot(it, sourceID));
            }

            list = (List)destination;
         } else {
            Loot[] minimalType = new Loot[]{new Loot(sourceID, (double)1.0F)};
            list = CollectionsKt.mutableListOf(minimalType);
         }

         List loots = list;
         String string = dropSection.getString("minimal_type");
         if (string == null) {
            string = "";
         }

         String minimalType = string;
         string = dropSection.getString("best_tool");
         if (string == null) {
            string = "";
         }

         String bestTool = string;
         boolean fortune = dropSection.getBoolean("fortune");
         boolean silkTouch = dropSection.getBoolean("silktouch");
         Drop drop;
         List list2 = toolTypes;
         if (toolTypes == null) {
            list2 = CollectionsKt.emptyList();
         }

         drop = new Drop(list2, loots, silkTouch, fortune, sourceID, minimalType, bestTool);
         return drop;
      }

      @JvmStatic
      @NotNull
      public final Drop emptyDrop() {
         return new Drop((List)(new ArrayList()), false, false, "");
      }

      @JvmStatic
      @NotNull
      public final Drop emptyDrop(@NotNull List<Loot> loots) {
         return new Drop(loots, false, false, "");
      }
   }
}
