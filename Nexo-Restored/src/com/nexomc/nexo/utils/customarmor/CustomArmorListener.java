package com.nexomc.nexo.utils.customarmor;

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import com.nexomc.nexo.api.NexoItems;
import com.nexomc.nexo.configs.Settings;
import com.nexomc.nexo.items.ItemUpdater;
import com.nexomc.nexo.utils.RegistryUtilsKt;
import com.nexomc.nexo.utils.logs.Logs;
import io.papermc.paper.datacomponent.DataComponentBuilder;
import io.papermc.paper.datacomponent.DataComponentType;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemArmorTrim;
import io.papermc.paper.registry.RegistryKey;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import kotlin.Lazy;
import kotlin.LazyKt;
import kotlin.Metadata;
import kotlin.collections.ArraysKt;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.StringsKt;
import net.kyori.adventure.key.Key;
import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.inventory.PrepareSmithingEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerAttemptPickupItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ComplexRecipe;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.trim.ArmorTrim;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000\\\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0000\u0018\u00002\u00020\u0001B\u0007¢\u0006\u0004\b\u0002\u0010\u0003J&\u0010\u0004\u001a\u00020\u0005*\u00020\u0006H\u0007b\u0018\b\u0007\u0012\n\b\b\u0012\u0006\b\n0\t8\n\u0012\b\b\u000b\u0012\u0004\b\u0007\u0010\u0002J\u0010\u0010\f\u001a\u00020\u0005*\u00020\rH\u0007b\u0002\b\u0007J\u0010\u0010\u000e\u001a\u00020\u0005*\u00020\u000fH\u0007b\u0002\b\u0007J\u0010\u0010\u0010\u001a\u00020\u0005*\u00020\u0011H\u0007b\u0002\b\u0007J\u0010\u0010\u0012\u001a\u00020\u0005*\u00020\u0013H\u0007b\u0002\b\u0007J\u0010\u0010\u0014\u001a\u00020\u0005*\u00020\u0015H\u0007b\u0002\b\u0007J\u0012\u0010 \u001a\u00020\u00052\b\u0010!\u001a\u0004\u0018\u00010\"H\u0002R\u0013\u0010\u0016\u001a\u00070\u0017¢\u0006\u0002\b\u0018X\u0082\u0004¢\u0006\u0002\n\u0000R\"\u0010\u0019\u001a\t\u0018\u00010\u001a¢\u0006\u0002\b\u001b8BX\u0082\u0084\u0002¢\u0006\f\n\u0004\b\u001e\u0010\u001f\u001a\u0004\b\u001c\u0010\u001d¨\u0006#"},
   d2 = {"Lcom/nexomc/nexo/utils/customarmor/CustomArmorListener;", "Lorg/bukkit/event/Listener;", "<init>", "()V", "onMerge", "", "Lorg/bukkit/event/inventory/PrepareItemCraftEvent;", "Lorg/bukkit/event/EventHandler;", "priority", "Lorg/bukkit/event/EventPriority;", "HIGHEST", "ignoreCancelled", "onTrimCustomArmor", "Lorg/bukkit/event/inventory/PrepareSmithingEvent;", "updateVanillaArmor", "Lorg/bukkit/event/player/PlayerJoinEvent;", "onPlayerEquipVanilla", "Lcom/destroystokyo/paper/event/player/PlayerArmorChangeEvent;", "onPlayerPickup", "Lorg/bukkit/event/player/PlayerAttemptPickupItemEvent;", "onAmorStandEquip", "Lorg/bukkit/event/player/PlayerArmorStandManipulateEvent;", "vanillaPatternKey", "Lnet/kyori/adventure/key/Key;", "Lorg/jetbrains/annotations/NotNull;", "vanillaPattern", "Lorg/bukkit/inventory/meta/trim/TrimPattern;", "Lorg/jspecify/annotations/Nullable;", "getVanillaPattern", "()Lorg/bukkit/inventory/meta/trim/TrimPattern;", "vanillaPattern$delegate", "Lkotlin/Lazy;", "setVanillaArmorTrim", "itemStack", "Lorg/bukkit/inventory/ItemStack;", "com.nexomc:core"}
)
public final class CustomArmorListener implements Listener {
   @NotNull
   private final Key vanillaPatternKey;
   @NotNull
   private final Lazy vanillaPattern$delegate;

   public CustomArmorListener() {
      Key key = Key.key("chainmail");
      this.vanillaPatternKey = key;
      this.vanillaPattern$delegate = LazyKt.lazy((() -> CustomArmorListener.vanillaPattern_delegate$lambda$0(this)));
   }

   @EventHandler(
      priority = EventPriority.HIGHEST,
      ignoreCancelled = true
   )
   public final void onMerge(@NotNull PrepareItemCraftEvent prepareItemCraftEvent) {
      if (prepareItemCraftEvent.isRepair() && prepareItemCraftEvent.getRecipe() instanceof ComplexRecipe) {
         ItemStack[] itemStacks = prepareItemCraftEvent.getInventory().getMatrix();
         List matrix = ArraysKt.filterNotNull(itemStacks);
         Iterable iterable = (Iterable)matrix;
         NexoItems nexoItems = NexoItems.INSTANCE;
         Collection destination = (Collection)(new ArrayList());

         for(Object element : iterable) {
            int value = 0;
            ItemStack p0 = (ItemStack)element;
            int value2 = 0;
            String str = NexoItems.idFromItem(p0);
            if (str != null) {
               Object obj = str;
               int value3 = 0;
               destination.add(obj);
            }
         }

         List itemIds = (List)destination;
         if (!itemIds.isEmpty()) {
            if (itemIds.size() != matrix.size()) {
               prepareItemCraftEvent.getInventory().setResult(ItemStack.empty());
            } else if (CollectionsKt.distinct((Iterable)itemIds).size() > 1) {
               prepareItemCraftEvent.getInventory().setResult(ItemStack.empty());
            } else if (CollectionsKt.distinct((Iterable)itemIds).size() == 1) {
               CraftingInventory inventory = prepareItemCraftEvent.getInventory();
               ItemStack result = prepareItemCraftEvent.getInventory().getResult();
               if (result != null) {
                  ItemStack it = result;
                  CraftingInventory craftingInventory = inventory;
                  int value4 = 0;
                  it.editPersistentDataContainer((a0_ -> CustomArmorListener.onMerge$lambda$0$0(itemIds, (PersistentDataContainer)a0_)));
                  result = ItemUpdater.Companion.updateItem(it);
                  inventory = craftingInventory;
               } else {
                  result = null;
               }

               inventory.setResult(result);
            }

         }
      }
   }

   @EventHandler
   public final void onTrimCustomArmor(@NotNull PrepareSmithingEvent prepareSmithingEvent) {
      ItemStack inputEquipment = prepareSmithingEvent.getInventory().getInputEquipment();
      if (inputEquipment != null) {
         ItemStack p0 = inputEquipment;
         NexoItems nexoItems = NexoItems.INSTANCE;
         int value = 0;
         ItemStack itemStack = NexoItems.exists(p0) ? p0 : null;
         if (itemStack != null) {
            ItemArmorTrim data = (ItemArmorTrim)itemStack.getData(DataComponentTypes.TRIM);
            if (data != null) {
               ArmorTrim armorTrim = data.armorTrim();
               if (armorTrim != null) {
                  TrimPattern pattern = armorTrim.getPattern();
                  if (pattern != null) {
                     TrimPattern trimPattern = pattern;
                     RegistryKey registryKey = RegistryKey.TRIM_PATTERN;
                     NamespacedKey key = RegistryUtilsKt.toRegistry(registryKey).getKey((Keyed)trimPattern);
                     if (key != null) {
                        String str = key.getNamespace();
                        if (str != null) {
                           String namespace = str;
                           if (CustomArmorType.Companion.getSetting() != CustomArmorType.NONE && Intrinsics.areEqual(namespace, "nexo")) {
                              prepareSmithingEvent.setResult((ItemStack)null);
                              return;
                           }

                           return;
                        }
                     }

                     return;
                  }
               }
            }
         }
      }

   }

   @EventHandler
   public final void updateVanillaArmor(@NotNull PlayerJoinEvent playerJoinEvent) {
      ItemStack[] contents = playerJoinEvent.getPlayer().getInventory().getContents();
      ItemStack[] itemStacks = contents;
      int value = 0;

      for(int i = itemStacks.length; value < i; ++value) {
         ItemStack item = itemStacks[value];
         this.setVanillaArmorTrim(item);
      }

   }

   @EventHandler
   public final void onPlayerEquipVanilla(@NotNull PlayerArmorChangeEvent playerArmorChangeEvent) {
      this.setVanillaArmorTrim(playerArmorChangeEvent.getOldItem());
      this.setVanillaArmorTrim(playerArmorChangeEvent.getNewItem());
   }

   @EventHandler
   public final void onPlayerPickup(@NotNull PlayerAttemptPickupItemEvent playerAttemptPickupItemEvent) {
      Item item = playerAttemptPickupItemEvent.getItem();
      ItemStack itemStack = playerAttemptPickupItemEvent.getItem().getItemStack();
      Item item2 = item;
      int value = 0;
      this.setVanillaArmorTrim(itemStack);
      item2.setItemStack(itemStack);
   }

   @EventHandler
   public final void onAmorStandEquip(@NotNull PlayerArmorStandManipulateEvent playerArmorStandManipulateEvent) {
      this.setVanillaArmorTrim(playerArmorStandManipulateEvent.getPlayerItem());
      this.setVanillaArmorTrim(playerArmorStandManipulateEvent.getArmorStandItem());
   }

   private final TrimPattern getVanillaPattern() {
      Lazy lazy = this.vanillaPattern$delegate;
      return (TrimPattern)lazy.getValue();
   }

   private final void setVanillaArmorTrim(ItemStack itemStack) {
      org.bukkit.NamespacedKey namespacedKey;
      if (CustomArmorType.Companion.getSetting() == CustomArmorType.TRIMS) {
         boolean flag;
         block: {
            if (itemStack != null) {
               Material type = itemStack.getType();
               if (type != null) {
                  String str = type.name();
                  if (str != null) {
                     flag = StringsKt.startsWith(str, "CHAINMAIL", false);
                     break block;
                  }
               }
            }

            flag = false;
         }

         if (flag) {
            ItemArmorTrim currentTrim;
            block: {
               RegistryKey registryKey = RegistryKey.TRIM_PATTERN;
               Registry registry = RegistryUtilsKt.toRegistry(registryKey);
               currentTrim = (ItemArmorTrim)itemStack.getData(DataComponentTypes.TRIM);
               if (currentTrim != null) {
                  ArmorTrim armorTrim = currentTrim.armorTrim();
                  if (armorTrim != null) {
                     TrimPattern pattern = armorTrim.getPattern();
                     if (pattern != null) {
                        TrimPattern p0 = pattern;
                        int value = 0;
                        namespacedKey = registry.getKey((Keyed)p0);
                        break block;
                     }
                  }
               }

               namespacedKey = null;
            }

            NamespacedKey patternKey = namespacedKey;
            if (currentTrim == null || !Intrinsics.areEqual(patternKey != null ? patternKey.getNamespace() : null, "nexo")) {
               if (this.getVanillaPattern() == null || currentTrim != null && Intrinsics.areEqual(patternKey, this.vanillaPatternKey)) {
                  if (this.getVanillaPattern() == null && Settings.DEBUG.toBool()) {
                     Logs.logWarn("Vanilla trim-pattern not found for " + itemStack.getType().name() + ": " + this.vanillaPatternKey.asString(), false);
                  }
               } else {
                  DataComponentType.Valued<ItemArmorTrim> valued = DataComponentTypes.TRIM;
                  TrimMaterial trimMaterial = TrimMaterial.REDSTONE;
                  TrimPattern vanillaPattern = this.getVanillaPattern();
                  itemStack.setData(valued, (DataComponentBuilder<ItemArmorTrim>)ItemArmorTrim.itemArmorTrim(new ArmorTrim(trimMaterial, vanillaPattern)));
               }

            }
         }
      }
   }

   private static final void onMerge$lambda$0$0(List itemIds, PersistentDataContainer pdc) {
      pdc.set(NexoItems.INSTANCE.getITEM_ID(), PersistentDataType.STRING, (String)CollectionsKt.first(itemIds));
   }

   private static final TrimPattern vanillaPattern_delegate$lambda$0(CustomArmorListener customArmorListener) {
      RegistryKey registryKey = RegistryKey.TRIM_PATTERN;
      return (TrimPattern)RegistryUtilsKt.toRegistry(registryKey).get(customArmorListener.vanillaPatternKey);
   }
}
