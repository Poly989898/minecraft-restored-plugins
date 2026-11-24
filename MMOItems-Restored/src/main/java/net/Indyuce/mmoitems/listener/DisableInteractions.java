package net.Indyuce.mmoitems.listener;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.api.item.NBTItem;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.interaction.util.DurabilityItem;
import org.bukkit.Keyed;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class DisableInteractions implements Listener {
   @EventHandler(ignoreCancelled = true)
   public void itemDropping(PlayerDropItemEvent var1) {
      ItemStack var2 = var1.getItemDrop().getItemStack();
      if (this.isDisabled(NBTItem.get(var2), "drop")) {
         var1.setCancelled(true);
      }
   }

   @EventHandler
   public void anvilInteractions(InventoryClickEvent var1) {
      Inventory var2 = var1.getClickedInventory();
      if (var2 != null && var2.getType() == InventoryType.ANVIL && var1.getSlotType() == SlotType.RESULT) {
         if (this.isDisabled(NBTItem.get(var1.getCurrentItem()), "repair")) {
            var1.setCancelled(true);
         } else if (var2.getItem(1) != null && this.isDisabled(NBTItem.get(var2.getItem(1)), "repair")) {
            var1.setCancelled(true);
         }
      }
   }

   @EventHandler
   public void grindstoneInteractions(InventoryClickEvent var1) {
      if (!MythicLib.plugin.getVersion().isBelowOrEqual(new int[]{1, 13})) {
         Inventory var2 = var1.getClickedInventory();
         if (var2 != null && var2.getType() == InventoryType.GRINDSTONE && var1.getSlotType() == SlotType.RESULT) {
            if (this.isDisabled(NBTItem.get(var2.getItem(0)), "repair") || this.isDisabled(NBTItem.get(var2.getItem(1)), "repair")) {
               var1.setCancelled(true);
            }
         }
      }
   }

   @EventHandler
   public void smithingTableInteractions(InventoryClickEvent var1) {
      if (!MythicLib.plugin.getVersion().isBelowOrEqual(new int[]{1, 15})) {
         Inventory var2 = var1.getClickedInventory();
         if (var2 != null && var2.getType() == InventoryType.SMITHING && var1.getSlotType() == SlotType.RESULT) {
            if (this.isDisabled(NBTItem.get(var2.getItem(0)), "smith") || this.isDisabled(NBTItem.get(var2.getItem(1)), "smith")) {
               var1.setCancelled(true);
            }
         }
      }
   }

   @EventHandler
   public void enchantTablesInteractions(EnchantItemEvent var1) {
      if (this.isDisabled(NBTItem.get(var1.getItem()), "enchant")) {
         var1.setCancelled(true);
      }
   }

   @EventHandler
   public void furnaceInteractions(FurnaceSmeltEvent var1) {
      if (this.isDisabled(NBTItem.get(var1.getSource()), "smelt")) {
         var1.setCancelled(true);
      }
   }

   @EventHandler(priority = EventPriority.LOW)
   public void clickInteractions(PlayerInteractEvent var1) {
      if (var1.hasItem()) {
         NBTItem var2 = NBTItem.get(var1.getItem());
         if (var2.getBoolean("MMOITEMS_DISABLE_INTERACTION") || var2.hasTag("MMOITEMS_UNIDENTIFIED_ITEM")) {
            var1.setCancelled(true);
         }
      }
   }

   @EventHandler(priority = EventPriority.LOW)
   public void miningInteractions(BlockBreakEvent var1) {
      NBTItem var2 = NBTItem.get(var1.getPlayer().getInventory().getItemInMainHand());
      if (var2.hasTag("MMOITEMS_UNIDENTIFIED_ITEM")) {
         var1.setCancelled(true);
      }
   }

   @EventHandler
   public void entityInteractions(PlayerInteractEntityEvent var1) {
      if (!(var1.getRightClicked() instanceof ArmorStand)) {
         NBTItem var2 = NBTItem.get(
            var1.getHand() == EquipmentSlot.OFF_HAND ? var1.getPlayer().getInventory().getItemInOffHand() : var1.getPlayer().getInventory().getItemInMainHand()
         );
         if (var2.getBoolean("MMOITEMS_DISABLE_INTERACTION")) {
            var1.setCancelled(true);
         }
      }
   }

   @EventHandler
   public void consumeInteractions(PlayerItemConsumeEvent var1) {
      NBTItem var2 = NBTItem.get(var1.getItem());
      if (var2.getBoolean("MMOITEMS_DISABLE_INTERACTION")) {
         var1.setCancelled(true);
      }
   }

   @EventHandler
   public void workbenchInteractions(CraftItemEvent var1) {
      if (var1.getRecipe() instanceof Keyed && ((Keyed)var1.getRecipe()).getKey().getNamespace().equals("mmoitems")) {
         String var6 = NBTItem.get(var1.getCurrentItem()).getString("MMOITEMS_CRAFT_PERMISSION");
         if (!var6.isEmpty() && !var1.getWhoClicked().hasPermission(var6)) {
            var1.setCancelled(true);
         }
      } else {
         for (ItemStack var5 : var1.getInventory().getMatrix()) {
            if (this.isDisabled(NBTItem.get(var5), "craft")) {
               var1.setCancelled(true);
               return;
            }
         }

         if (MMOItems.plugin.getConfig().getStringList("disable-vanilla-recipes").contains(var1.getCurrentItem().getType().name())) {
            var1.setCancelled(true);
         }
      }
   }

   @EventHandler
   public void shootBowInteractions(EntityShootBowEvent var1) {
      if (var1.getEntity() instanceof Player) {
         DurabilityItem var2 = new DurabilityItem(((Player)var1.getEntity()).getPlayer(), var1.getBow());
         if (var2.isBroken()) {
            var1.setCancelled(true);
         }

         Player var3 = (Player)var1.getEntity();
         int var4 = this.firstArrow(var3);
         if (var4 >= 0) {
            ItemStack var5 = var3.getInventory().getItem(var4);
            if (var5 != null) {
               NBTItem var6 = NBTItem.get(var5);
               if (var6.hasType()
                  && (MMOItems.plugin.getConfig().getBoolean("disable-interactions.arrow-shooting") || var6.getBoolean("MMOITEMS_DISABLE_ARROW_SHOOTING"))) {
                  var1.setCancelled(true);
               }
            }
         }
      }
   }

   @EventHandler(priority = EventPriority.LOW)
   public void attackInteractions(EntityDamageByEntityEvent var1) {
      if (var1.getDamage() != 0.0
         && var1.getCause() == DamageCause.ENTITY_ATTACK
         && var1.getEntity() instanceof LivingEntity
         && var1.getDamager() instanceof Player
         && !var1.getEntity().hasMetadata("NPC")
         && !var1.getDamager().hasMetadata("NPC")) {
         Player var2 = (Player)var1.getDamager();
         ItemStack var3 = var2.getInventory().getItemInMainHand();
         DurabilityItem var4 = new DurabilityItem(var2, var3);
         if (var4.isBroken()) {
            var1.setCancelled(true);
         }

         if (var4.getNBTItem().hasTag("MMOITEMS_UNIDENTIFIED_ITEM")) {
            var1.setCancelled(true);
         }
      }
   }

   private int firstArrow(Player var1) {
      if (var1.getInventory().getItemInOffHand() != null && var1.getInventory().getItemInOffHand().getType().name().contains("ARROW")) {
         return 40;
      }

      ItemStack[] var2 = var1.getInventory().getStorageContents();

      for (int var3 = 0; var3 < var2.length; var3++) {
         ItemStack var4 = var2[var3];
         if (var4 != null && var4.getType().name().contains("ARROW")) {
            return var3;
         }
      }

      return -1;
   }

   private boolean isDisabled(NBTItem var1, String var2) {
      return var1.hasType() && MMOItems.plugin.getConfig().getBoolean("disable-interactions." + var2)
         || var1.getBoolean("MMOITEMS_DISABLE_" + var2.toUpperCase().replace("-", "_") + "ING");
   }
}
