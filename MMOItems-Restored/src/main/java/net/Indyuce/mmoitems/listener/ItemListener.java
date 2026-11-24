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
   private void onItemCraftRepair(PrepareItemCraftEvent event) {
      VInventoryView view = VersionUtils.getView(event);
      if (view.getPlayer() instanceof Player && event.isRepair()) {
         Player player = (Player)view.getPlayer();
         CraftingInventory inventory = event.getInventory();
         ItemStack item = new ItemStack(Material.AIR);
         ItemStack result = inventory.getResult();
         inventory.setResult(item);
         Bukkit.getScheduler()
            .runTaskLater(
               MMOItems.plugin,
               () -> {
                  List<ItemStack> craftingItems = Arrays.stream(inventory.getMatrix()).filter(Objects::nonNull).filter(craftingItem -> !craftingItem.getType().isAir()).collect(Collectors.toList());
                  long mmoItemCount = craftingItems.stream().filter(craftingItem -> NBTItem.get(craftingItem).hasTag("MMOITEMS_ITEM_ID")).count();
                  if (mmoItemCount == 0L) {
                     inventory.setResult(result);
                     player.updateInventory();
                  } else if (mmoItemCount != 1L
                     && NBTItem.get((ItemStack)craftingItems.get(0))
                        .getString("MMOITEMS_ITEM_ID")
                        .equals(NBTItem.get((ItemStack)craftingItems.get(1)).getString("MMOITEMS_ITEM_ID"))) {
                     inventory.setResult(result);
                     boolean flag = craftingItems.stream().allMatch(craftingItem -> {
                        NBTItem nbtItem = NBTItem.get(craftingItem);
                        return nbtItem.hasTag("MMOITEMS_DISABLE_REPAIRING") && nbtItem.getBoolean("MMOITEMS_DISABLE_REPAIRING");
                     });
                     boolean allValid = craftingItems.stream().allMatch(stack -> new DurabilityItem(player, stack).isValid());
                     if (flag) {
                        inventory.setResult(item);
                     } else if (allValid) {
                        DurabilityItem durabilityItem = new DurabilityItem(player, (ItemStack)craftingItems.get(0));
                        int count = craftingItems.stream().map(stack -> new DurabilityItem(player, stack)).map(DurabilityItem::getDurability).reduce(0, Integer::sum);
                        int remainingDurability = durabilityItem.getMaxDurability() - Math.min(durabilityItem.getMaxDurability(), count);
                        if (remainingDurability > 0) {
                           durabilityItem.addDurability(remainingDurability);
                        }

                        inventory.setResult(durabilityItem.toItem());
                     }

                     player.updateInventory();
                  } else {
                     inventory.setResult(item);
                     player.updateInventory();
                  }
               },
               1L
            );
      }
   }

   @EventHandler(ignoreCancelled = true)
   private void itemPickup(EntityPickupItemEvent event) {
      if (event.getEntity().getType().equals(EntityType.PLAYER)) {
         ItemStack item = modifyItem(event.getItem().getItemStack(), (Player)event.getEntity(), ReforgeReason.PICKUP);
         if (item != null) {
            event.getItem().setItemStack(item);
         }
      }
   }

   @EventHandler(ignoreCancelled = true)
   private void itemCraft(CraftItemEvent event) {
      if (event.getWhoClicked() instanceof Player) {
         ItemStack item = modifyItem(event.getCurrentItem(), (Player)event.getWhoClicked(), ReforgeReason.CRAFT);
         if (item != null) {
            event.setCurrentItem(item);
         }
      }
   }

   @EventHandler(ignoreCancelled = true)
   private void inventoryMove(InventoryClickEvent event) {
      if (event.getInventory().getType() == InventoryType.CRAFTING && event.getWhoClicked() instanceof Player) {
         ItemStack item = modifyItem(event.getCurrentItem(), (Player)event.getWhoClicked(), ReforgeReason.CLICK);
         if (item != null) {
            event.setCurrentItem(item);
         }
      }
   }

   @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
   public void playerJoin(PlayerJoinEvent event) {
      if (!MythicLib.plugin.hasProfiles()) {
         updateInventory(event.getPlayer());
      }
   }

   public static void updateInventory(Player player) {
      ItemStack item = modifyItem(player.getEquipment().getHelmet(), player, ReforgeReason.JOIN);
      if (item != null) {
         player.getEquipment().setHelmet(item);
      }

      item = modifyItem(player.getEquipment().getChestplate(), player, ReforgeReason.JOIN);
      if (item != null) {
         player.getEquipment().setChestplate(item);
      }

      item = modifyItem(player.getEquipment().getLeggings(), player, ReforgeReason.JOIN);
      if (item != null) {
         player.getEquipment().setLeggings(item);
      }

      item = modifyItem(player.getEquipment().getBoots(), player, ReforgeReason.JOIN);
      if (item != null) {
         player.getEquipment().setBoots(item);
      }

      for (int i = 0; i < 9; i++) {
         item = modifyItem(player.getInventory().getItem(i), player, ReforgeReason.JOIN);
         if (item != null) {
            player.getInventory().setItem(i, item);
         }
      }

      item = modifyItem(player.getEquipment().getItemInOffHand(), player, ReforgeReason.JOIN);
      if (item != null) {
         player.getEquipment().setItemInOffHand(item);
      }
   }

   @Nullable
   private static ItemStack modifyItem(@Nullable ItemStack item, @NotNull Player player, @NotNull ReforgeReason reforgeReason) {
      if (item != null && item.hasItemMeta()) {
         MMOItemReforger mmoItemReforger = new MMOItemReforger(item);
         if (!mmoItemReforger.hasTemplate()) {
            return null;
         }

         if ("VANILLA".equals(mmoItemReforger.getNBTItem().getString("MMOITEMS_ITEM_ID"))) {
            return null;
         }

         if (MMOItems.plugin.getConfig().getBoolean("item-revision.disable-on." + reforgeReason.name().toLowerCase())) {
            return null;
         }

         int revisionId = mmoItemReforger.getTemplate().getRevisionId();
         int count = mmoItemReforger.getNBTItem().hasTag(ItemStats.REVISION_ID.getNBTPath()) ? mmoItemReforger.getNBTItem().getInteger(ItemStats.REVISION_ID.getNBTPath()) : 1;
         if (revisionId <= count) {
            return null;
         }

         if (!mmoItemReforger.reforge(MMOItems.plugin.getLanguage().revisionOptions, player)) {
            return null;
         }

         for (ItemStack leftoverItem : player.getInventory().addItem(mmoItemReforger.getReforgingOutput().toArray(new ItemStack[0])).values()) {
            if (!SilentNumbers.isAir(leftoverItem)) {
               player.getWorld().dropItem(player.getLocation(), leftoverItem);
            }
         }

         return mmoItemReforger.getResult();
      } else {
         return null;
      }
   }
}
