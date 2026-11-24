package net.Indyuce.mmoitems.listener;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.api.item.NBTItem;
import io.lumine.mythic.lib.api.util.ui.SilentNumbers;
import io.lumine.mythic.lib.version.VInventoryView;
import io.lumine.mythic.lib.version.VersionUtils;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import net.Indyuce.mmoitems.ItemStats;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.interaction.util.DurabilityItem;
import net.Indyuce.mmoitems.api.util.MMOItemReforger;
import net.Indyuce.mmoitems.listener.reforging.RFFKeepAmount;
import net.Indyuce.mmoitems.listener.reforging.RFFKeepSkins;
import net.Indyuce.mmoitems.listener.reforging.RFGKeepDurability;
import net.Indyuce.mmoitems.listener.reforging.RFGKeepEnchantments;
import net.Indyuce.mmoitems.listener.reforging.RFGKeepExternalSH;
import net.Indyuce.mmoitems.listener.reforging.RFGKeepGems;
import net.Indyuce.mmoitems.listener.reforging.RFGKeepLore;
import net.Indyuce.mmoitems.listener.reforging.RFGKeepModifications;
import net.Indyuce.mmoitems.listener.reforging.RFGKeepName;
import net.Indyuce.mmoitems.listener.reforging.RFGKeepRNG;
import net.Indyuce.mmoitems.listener.reforging.RFGKeepSoulbound;
import net.Indyuce.mmoitems.listener.reforging.RFGKeepUpgrades;
import net.Indyuce.mmoitems.reforge.ReforgeReason;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ItemListener implements Listener {
   public ItemListener() {
      Bukkit.getPluginManager().registerEvents(new RFGKeepName(), MMOItems.plugin);
      Bukkit.getPluginManager().registerEvents(new RFGKeepLore(), MMOItems.plugin);
      Bukkit.getPluginManager().registerEvents(new RFGKeepEnchantments(), MMOItems.plugin);
      Bukkit.getPluginManager().registerEvents(new RFGKeepExternalSH(), MMOItems.plugin);
      Bukkit.getPluginManager().registerEvents(new RFGKeepGems(), MMOItems.plugin);
      Bukkit.getPluginManager().registerEvents(new RFGKeepModifications(), MMOItems.plugin);
      Bukkit.getPluginManager().registerEvents(new RFGKeepSoulbound(), MMOItems.plugin);
      Bukkit.getPluginManager().registerEvents(new RFGKeepUpgrades(), MMOItems.plugin);
      Bukkit.getPluginManager().registerEvents(new RFGKeepRNG(), MMOItems.plugin);
      Bukkit.getPluginManager().registerEvents(new RFGKeepDurability(), MMOItems.plugin);
      Bukkit.getPluginManager().registerEvents(new RFFKeepAmount(), MMOItems.plugin);
      Bukkit.getPluginManager().registerEvents(new RFFKeepSkins(), MMOItems.plugin);
   }

   @EventHandler
   private void onItemCraftRepair(PrepareItemCraftEvent var1) {
      VInventoryView var2 = VersionUtils.getView(var1);
      if (var2.getPlayer() instanceof Player && var1.isRepair()) {
         Player var3 = (Player)var2.getPlayer();
         CraftingInventory var4 = var1.getInventory();
         ItemStack var5 = new ItemStack(Material.AIR);
         ItemStack var6 = var4.getResult();
         var4.setResult(var5);
         Bukkit.getScheduler()
            .runTaskLater(
               MMOItems.plugin,
               () -> {
                  List<ItemStack> var4x = Arrays.stream(var4.getMatrix()).filter(Objects::nonNull).filter(var0x -> !var0x.getType().isAir()).collect(Collectors.toList());
                  long var5x = var4x.stream().filter(var0x -> NBTItem.get(var0x).hasTag("MMOITEMS_ITEM_ID")).count();
                  if (var5x == 0L) {
                     var4.setResult(var6);
                     var3.updateInventory();
                  } else if (var5x != 1L
                     && NBTItem.get((ItemStack)var4x.get(0))
                        .getString("MMOITEMS_ITEM_ID")
                        .equals(NBTItem.get((ItemStack)var4x.get(1)).getString("MMOITEMS_ITEM_ID"))) {
                     var4.setResult(var6);
                     boolean var7 = var4x.stream().allMatch(var0x -> {
                        NBTItem var1xx = NBTItem.get(var0x);
                        return var1xx.hasTag("MMOITEMS_DISABLE_REPAIRING") && var1xx.getBoolean("MMOITEMS_DISABLE_REPAIRING");
                     });
                     boolean var8 = var4x.stream().allMatch(var1xx -> new DurabilityItem(var3, var1xx).isValid());
                     if (var7) {
                        var4.setResult(var5);
                     } else if (var8) {
                        DurabilityItem var9 = new DurabilityItem(var3, (ItemStack)var4x.get(0));
                        int var10 = var4x.stream().map(var1xx -> new DurabilityItem(var3, var1xx)).map(DurabilityItem::getDurability).reduce(0, Integer::sum);
                        int var11 = var9.getMaxDurability() - Math.min(var9.getMaxDurability(), var10);
                        if (var11 > 0) {
                           var9.addDurability(var11);
                        }

                        var4.setResult(var9.toItem());
                     }

                     var3.updateInventory();
                  } else {
                     var4.setResult(var5);
                     var3.updateInventory();
                  }
               },
               1L
            );
      }
   }

   @EventHandler(ignoreCancelled = true)
   private void itemPickup(EntityPickupItemEvent var1) {
      if (var1.getEntity().getType().equals(EntityType.PLAYER)) {
         ItemStack var2 = modifyItem(var1.getItem().getItemStack(), (Player)var1.getEntity(), ReforgeReason.PICKUP);
         if (var2 != null) {
            var1.getItem().setItemStack(var2);
         }
      }
   }

   @EventHandler(ignoreCancelled = true)
   private void itemCraft(CraftItemEvent var1) {
      if (var1.getWhoClicked() instanceof Player) {
         ItemStack var2 = modifyItem(var1.getCurrentItem(), (Player)var1.getWhoClicked(), ReforgeReason.CRAFT);
         if (var2 != null) {
            var1.setCurrentItem(var2);
         }
      }
   }

   @EventHandler(ignoreCancelled = true)
   private void inventoryMove(InventoryClickEvent var1) {
      if (var1.getInventory().getType() == InventoryType.CRAFTING && var1.getWhoClicked() instanceof Player) {
         ItemStack var2 = modifyItem(var1.getCurrentItem(), (Player)var1.getWhoClicked(), ReforgeReason.CLICK);
         if (var2 != null) {
            var1.setCurrentItem(var2);
         }
      }
   }

   @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
   public void playerJoin(PlayerJoinEvent var1) {
      if (!MythicLib.plugin.hasProfiles()) {
         updateInventory(var1.getPlayer());
      }
   }

   public static void updateInventory(Player var0) {
      ItemStack var1 = modifyItem(var0.getEquipment().getHelmet(), var0, ReforgeReason.JOIN);
      if (var1 != null) {
         var0.getEquipment().setHelmet(var1);
      }

      var1 = modifyItem(var0.getEquipment().getChestplate(), var0, ReforgeReason.JOIN);
      if (var1 != null) {
         var0.getEquipment().setChestplate(var1);
      }

      var1 = modifyItem(var0.getEquipment().getLeggings(), var0, ReforgeReason.JOIN);
      if (var1 != null) {
         var0.getEquipment().setLeggings(var1);
      }

      var1 = modifyItem(var0.getEquipment().getBoots(), var0, ReforgeReason.JOIN);
      if (var1 != null) {
         var0.getEquipment().setBoots(var1);
      }

      for (int var2 = 0; var2 < 9; var2++) {
         var1 = modifyItem(var0.getInventory().getItem(var2), var0, ReforgeReason.JOIN);
         if (var1 != null) {
            var0.getInventory().setItem(var2, var1);
         }
      }

      var1 = modifyItem(var0.getEquipment().getItemInOffHand(), var0, ReforgeReason.JOIN);
      if (var1 != null) {
         var0.getEquipment().setItemInOffHand(var1);
      }
   }

   @Nullable
   private static ItemStack modifyItem(@Nullable ItemStack var0, @NotNull Player var1, @NotNull ReforgeReason var2) {
      if (var0 != null && var0.hasItemMeta()) {
         MMOItemReforger var3 = new MMOItemReforger(var0);
         if (!var3.hasTemplate()) {
            return null;
         }

         if ("VANILLA".equals(var3.getNBTItem().getString("MMOITEMS_ITEM_ID"))) {
            return null;
         }

         if (MMOItems.plugin.getConfig().getBoolean("item-revision.disable-on." + var2.name().toLowerCase())) {
            return null;
         }

         int var4 = var3.getTemplate().getRevisionId();
         int var5 = var3.getNBTItem().hasTag(ItemStats.REVISION_ID.getNBTPath()) ? var3.getNBTItem().getInteger(ItemStats.REVISION_ID.getNBTPath()) : 1;
         if (var4 <= var5) {
            return null;
         }

         if (!var3.reforge(MMOItems.plugin.getLanguage().revisionOptions, var1)) {
            return null;
         }

         for (ItemStack var7 : var1.getInventory().addItem(var3.getReforgingOutput().toArray(new ItemStack[0])).values()) {
            if (!SilentNumbers.isAir(var7)) {
               var1.getWorld().dropItem(var1.getLocation(), var7);
            }
         }

         return var3.getResult();
      } else {
         return null;
      }
   }
}
