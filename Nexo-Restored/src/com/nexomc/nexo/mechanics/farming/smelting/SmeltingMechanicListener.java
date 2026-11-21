package com.nexomc.nexo.mechanics.farming.smelting;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.random.Random;
import kotlin.text.StringsKt;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.CookingRecipe;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u00000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J&\u0010\u0006\u001a\u00020\u0007*\u00020\bH\u0007b\u0018\b\t\u0012\n\b\n\u0012\u0006\b\n0\u000b8\f\u0012\b\b\r\u0012\u0004\b\u0007\u0010\u0002J\u0014\u0010\u000e\u001a\u0004\u0018\u00010\u000f2\b\u0010\u0010\u001a\u0004\u0018\u00010\u000fH\u0002R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u0011"},
   d2 = {"Lcom/nexomc/nexo/mechanics/farming/smelting/SmeltingMechanicListener;", "Lorg/bukkit/event/Listener;", "factory", "Lcom/nexomc/nexo/mechanics/farming/smelting/SmeltingMechanicFactory;", "<init>", "(Lcom/nexomc/nexo/mechanics/farming/smelting/SmeltingMechanicFactory;)V", "onBlockBreak", "", "Lorg/bukkit/event/block/BlockBreakEvent;", "Lorg/bukkit/event/EventHandler;", "priority", "Lorg/bukkit/event/EventPriority;", "NORMAL", "ignoreCancelled", "furnace", "Lorg/bukkit/inventory/ItemStack;", "item", "com.nexomc:core"}
)
public final class SmeltingMechanicListener implements Listener {
   @NotNull
   private final SmeltingMechanicFactory factory;

   public SmeltingMechanicListener(@NotNull SmeltingMechanicFactory factory) {
      super();
      this.factory = factory;
   }

   @EventHandler(
      priority = EventPriority.NORMAL,
      ignoreCancelled = true
   )
   public final void onBlockBreak(@NotNull BlockBreakEvent blockBreakEvent) {
      ItemStack itemInMainHand = blockBreakEvent.getPlayer().getInventory().getItemInMainHand();
      ItemStack item = itemInMainHand;
      Location p0 = blockBreakEvent.getBlock().getLocation().toCenterLocation();
      int value = 0;
      Location location2 = p0.isWorldLoaded() ? p0 : null;
      if (location2 != null) {
         Location location = location2;
         SmeltingMechanic smeltingMechanic = this.factory.getMechanic(item);
         if (smeltingMechanic != null) {
            SmeltingMechanic mechanic = smeltingMechanic;
            if (!blockBreakEvent.getBlock().getDrops().isEmpty() && blockBreakEvent.getPlayer().getGameMode() != GameMode.CREATIVE) {
               Collection drops = blockBreakEvent.getBlock().getDrops();
               ItemStack itemStack = this.furnace((ItemStack)CollectionsKt.randomOrNull(drops, (Random)Random.Default));
               if (itemStack != null) {
                  ItemStack loot = itemStack;
                  if (StringsKt.contains((CharSequence)blockBreakEvent.getBlock().getType().toString(), (CharSequence)"ORE", false) && item.containsEnchantment(Enchantment.FORTUNE)) {
                     loot.setAmount(1 + Random.Default.nextInt(item.getEnchantmentLevel(Enchantment.FORTUNE)));
                  }

                  blockBreakEvent.setDropItems(false);
                  location.getWorld().dropItemNaturally(location, loot);
                  if (mechanic.playSound()) {
                     location.getWorld().playSound(location, Sound.ENTITY_GUARDIAN_ATTACK, 0.1F, 0.8F);
                  }

               }
            }
         }
      }
   }

   private final ItemStack furnace(ItemStack item) {
      java.lang.Object obj;
      if (item != null) {
         Material material = item.getType();
         if (material != null) {
            String str = material.toString();
            if (str != null) {
               String type = str;
               if (StringsKt.startsWith(type, "RAW_", false) && !StringsKt.endsWith(type, "_BLOCK", false)) {
                  String str2 = type.substring(4);
                  Material material2 = Material.matchMaterial(str2 + "_INGOT");
                  if (material2 == null) {
                     return null;
                  }

                  item.setType(material2);
                  return item;
               }

               List recipesFor = Bukkit.getRecipesFor(item);
               Iterable iterable = (Iterable)recipesFor;
               Collection destination = (Collection)(new ArrayList());

               for(Object element : iterable) {
                  if (element instanceof CookingRecipe) {
                     destination.add(element);
                  }
               }

               iterable = (Iterable)((List)destination);
               Iterator iterator = iterable.iterator();

               while(true) {
                  if (iterator.hasNext()) {
                     Object obj2 = iterator.next();
                     CookingRecipe recipe = (CookingRecipe)obj2;
                     int value = 0;
                     if (!recipe.getInputChoice().test(item)) {
                        continue;
                     }

                     obj = obj2;
                     break;
                  }

                  obj = null;
                  break;
               }

               CookingRecipe it = (CookingRecipe)obj;
               ItemStack itemStack;
               if (it != null) {
                  int value2 = 0;
                  itemStack = ItemStack.of(it.getResult().getType(), item.getAmount());
               } else {
                  itemStack = null;
               }

               return itemStack;
            }
         }
      }

      return null;
   }
}
