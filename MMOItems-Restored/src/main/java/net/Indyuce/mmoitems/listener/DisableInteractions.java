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
   public void itemDropping(PlayerDropItemEvent event) {
      ItemStack itemStack = event.getItemDrop().getItemStack();
      if (this.isDisabled(NBTItem.get(itemStack), "drop")) {
         event.setCancelled(true);
      }
   }

   @EventHandler
   public void anvilInteractions(InventoryClickEvent event) {
      Inventory clickedInventory = event.getClickedInventory();
      if (clickedInventory != null && clickedInventory.getType() == InventoryType.ANVIL && event.getSlotType() == SlotType.RESULT) {
         if (this.isDisabled(NBTItem.get(event.getCurrentItem()), "repair")) {
            event.setCancelled(true);
         } else if (clickedInventory.getItem(1) != null && this.isDisabled(NBTItem.get(clickedInventory.getItem(1)), "repair")) {
            event.setCancelled(true);
         }
      }
   }

   @EventHandler
   public void grindstoneInteractions(InventoryClickEvent event) {
      if (!MythicLib.plugin.getVersion().isBelowOrEqual(new int[]{1, 13})) {
         Inventory clickedInventory = event.getClickedInventory();
         if (clickedInventory != null && clickedInventory.getType() == InventoryType.GRINDSTONE && event.getSlotType() == SlotType.RESULT) {
            if (this.isDisabled(NBTItem.get(clickedInventory.getItem(0)), "repair") || this.isDisabled(NBTItem.get(clickedInventory.getItem(1)), "repair")) {
               event.setCancelled(true);
            }
         }
      }
   }

   @EventHandler
   public void smithingTableInteractions(InventoryClickEvent event) {
      if (!MythicLib.plugin.getVersion().isBelowOrEqual(new int[]{1, 15})) {
         Inventory clickedInventory = event.getClickedInventory();
         if (clickedInventory != null && clickedInventory.getType() == InventoryType.SMITHING && event.getSlotType() == SlotType.RESULT) {
            if (this.isDisabled(NBTItem.get(clickedInventory.getItem(0)), "smith") || this.isDisabled(NBTItem.get(clickedInventory.getItem(1)), "smith")) {
               event.setCancelled(true);
            }
         }
      }
   }

   @EventHandler
   public void enchantTablesInteractions(EnchantItemEvent event) {
      if (this.isDisabled(NBTItem.get(event.getItem()), "enchant")) {
         event.setCancelled(true);
      }
   }

   @EventHandler
   public void furnaceInteractions(FurnaceSmeltEvent event) {
      if (this.isDisabled(NBTItem.get(event.getSource()), "smelt")) {
         event.setCancelled(true);
      }
   }

   @EventHandler(priority = EventPriority.LOW)
   public void clickInteractions(PlayerInteractEvent event) {
      if (event.hasItem()) {
         NBTItem nbtItem = NBTItem.get(event.getItem());
         if (nbtItem.getBoolean("MMOITEMS_DISABLE_INTERACTION") || nbtItem.hasTag("MMOITEMS_UNIDENTIFIED_ITEM")) {
            event.setCancelled(true);
         }
      }
   }

   @EventHandler(priority = EventPriority.LOW)
   public void miningInteractions(BlockBreakEvent event) {
      NBTItem nbtItem = NBTItem.get(event.getPlayer().getInventory().getItemInMainHand());
      if (nbtItem.hasTag("MMOITEMS_UNIDENTIFIED_ITEM")) {
         event.setCancelled(true);
      }
   }

   @EventHandler
   public void entityInteractions(PlayerInteractEntityEvent event) {
      if (!(event.getRightClicked() instanceof ArmorStand)) {
         NBTItem nbtItem = NBTItem.get(
            event.getHand() == EquipmentSlot.OFF_HAND ? event.getPlayer().getInventory().getItemInOffHand() : event.getPlayer().getInventory().getItemInMainHand()
         );
         if (nbtItem.getBoolean("MMOITEMS_DISABLE_INTERACTION")) {
            event.setCancelled(true);
         }
      }
   }

   @EventHandler
   public void consumeInteractions(PlayerItemConsumeEvent event) {
      NBTItem nbtItem = NBTItem.get(event.getItem());
      if (nbtItem.getBoolean("MMOITEMS_DISABLE_INTERACTION")) {
         event.setCancelled(true);
      }
   }

   @EventHandler
   public void workbenchInteractions(CraftItemEvent event) {
      if (event.getRecipe() instanceof Keyed && ((Keyed)event.getRecipe()).getKey().getNamespace().equals("mmoitems")) {
         String mMOITEMSCRAFTPERMISSION = NBTItem.get(event.getCurrentItem()).getString("MMOITEMS_CRAFT_PERMISSION");
         if (!mMOITEMSCRAFTPERMISSION.isEmpty() && !event.getWhoClicked().hasPermission(mMOITEMSCRAFTPERMISSION)) {
            event.setCancelled(true);
         }
      } else {
         for (ItemStack item : event.getInventory().getMatrix()) {
            if (this.isDisabled(NBTItem.get(item), "craft")) {
               event.setCancelled(true);
               return;
            }
         }

         if (MMOItems.plugin.getConfig().getStringList("disable-vanilla-recipes").contains(event.getCurrentItem().getType().name())) {
            event.setCancelled(true);
         }
      }
   }

   @EventHandler
   public void shootBowInteractions(EntityShootBowEvent event) {
      if (event.getEntity() instanceof Player) {
         DurabilityItem durabilityItem = new DurabilityItem(((Player)event.getEntity()).getPlayer(), event.getBow());
         if (durabilityItem.isBroken()) {
            event.setCancelled(true);
         }

         Player entity = (Player)event.getEntity();
         int index = this.firstArrow(entity);
         if (index >= 0) {
            ItemStack item = entity.getInventory().getItem(index);
            if (item != null) {
               NBTItem nbtItem = NBTItem.get(item);
               if (nbtItem.hasType()
                  && (MMOItems.plugin.getConfig().getBoolean("disable-interactions.arrow-shooting") || nbtItem.getBoolean("MMOITEMS_DISABLE_ARROW_SHOOTING"))) {
                  event.setCancelled(true);
               }
            }
         }
      }
   }

   @EventHandler(priority = EventPriority.LOW)
   public void attackInteractions(EntityDamageByEntityEvent event) {
      if (event.getDamage() != 0.0
         && event.getCause() == DamageCause.ENTITY_ATTACK
         && event.getEntity() instanceof LivingEntity
         && event.getDamager() instanceof Player
         && !event.getEntity().hasMetadata("NPC")
         && !event.getDamager().hasMetadata("NPC")) {
         Player damager = (Player)event.getDamager();
         ItemStack itemInMainHand = damager.getInventory().getItemInMainHand();
         DurabilityItem durabilityItem = new DurabilityItem(damager, itemInMainHand);
         if (durabilityItem.isBroken()) {
            event.setCancelled(true);
         }

         if (durabilityItem.getNBTItem().hasTag("MMOITEMS_UNIDENTIFIED_ITEM")) {
            event.setCancelled(true);
         }
      }
   }

   private int firstArrow(Player player) {
      if (player.getInventory().getItemInOffHand() != null && player.getInventory().getItemInOffHand().getType().name().contains("ARROW")) {
         return 40;
      }

      ItemStack[] storageContents = player.getInventory().getStorageContents();

      for (int i = 0; i < storageContents.length; i++) {
         ItemStack item = storageContents[i];
         if (item != null && item.getType().name().contains("ARROW")) {
            return i;
         }
      }

      return -1;
   }

   private boolean isDisabled(NBTItem nbtItem, String text) {
      return nbtItem.hasType() && MMOItems.plugin.getConfig().getBoolean("disable-interactions." + text)
         || nbtItem.getBoolean("MMOITEMS_DISABLE_" + text.toUpperCase().replace("-", "_") + "ING");
   }
}
