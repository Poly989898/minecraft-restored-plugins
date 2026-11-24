package net.Indyuce.mmoitems.listener;

import java.util.Arrays;
import java.util.List;
import net.Indyuce.mmoitems.api.interaction.util.DurabilityItem;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.event.player.PlayerItemMendEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class DurabilityListener implements Listener {
   private static final List<DamageCause> IGNORED_CAUSES = Arrays.asList(
      DamageCause.DROWNING,
      DamageCause.SUICIDE,
      DamageCause.FALL,
      DamageCause.VOID,
      DamageCause.FIRE_TICK,
      DamageCause.SUFFOCATION,
      DamageCause.POISON,
      DamageCause.WITHER,
      DamageCause.STARVATION,
      DamageCause.MAGIC
   );
   private static final EquipmentSlot[] ARMOR_SLOTS = new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};

   @EventHandler(ignoreCancelled = true)
   public void itemDamage(PlayerItemDamageEvent event) {
      DurabilityItem durabilityItem = new DurabilityItem(event.getPlayer(), event.getItem());
      if (durabilityItem.isValid()) {
         durabilityItem.decreaseDurability(event.getDamage());
         ItemStack item = durabilityItem.toItem();
         if (item == null) {
            event.setDamage(999);
         } else {
            event.setCancelled(true);
            event.getItem().setItemMeta(item.getItemMeta());
         }
      }
   }

   @EventHandler(ignoreCancelled = true)
   public void playerDamage(EntityDamageEvent event) {
      if (event.getEntityType() == EntityType.PLAYER && !IGNORED_CAUSES.contains(event.getCause())) {
         Player entity = (Player)event.getEntity();
         int count = Math.max((int)event.getDamage() / 4, 1);

         for (EquipmentSlot equipmentSlot : ARMOR_SLOTS) {
            if (this.hasItem(entity, equipmentSlot)) {
               this.handleUndamageableItem(entity.getInventory().getItem(equipmentSlot), entity, equipmentSlot, count);
            }
         }
      }
   }

   @EventHandler(ignoreCancelled = true)
   public void playerMeleeAttack(EntityDamageByEntityEvent event) {
      if (event.getDamage() != 0.0
         && event.getCause() == DamageCause.ENTITY_ATTACK
         && event.getEntity() instanceof LivingEntity
         && event.getDamager() instanceof Player
         && !event.getEntity().hasMetadata("NPC")
         && !event.getDamager().hasMetadata("NPC")) {
         Player damager = (Player)event.getDamager();
         ItemStack itemInMainHand = damager.getInventory().getItemInMainHand();
         this.handleUndamageableItem(itemInMainHand, damager, EquipmentSlot.HAND, 1);
      }
   }

   @EventHandler(ignoreCancelled = true)
   public void playerBowAttack(EntityShootBowEvent event) {
      if (event.getEntity() instanceof Player) {
         Player entity = (Player)event.getEntity();
         ItemStack bow = event.getBow();
         this.handleUndamageableItem(bow, entity, EquipmentSlot.HAND, 1);
      }
   }

   @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
   public void mendEvent(PlayerItemMendEvent event) {
      if (event.getRepairAmount() > 0) {
         DurabilityItem durabilityItem = new DurabilityItem(event.getPlayer(), event.getItem());
         if (durabilityItem.isValid()) {
            event.getItem().setItemMeta(durabilityItem.addDurability(event.getRepairAmount()).toItem().getItemMeta());
            event.setCancelled(true);
         }
      }
   }

   private void handleUndamageableItem(ItemStack item, Player player, EquipmentSlot equipmentSlot, int count) {
      if (item.getType().getMaxDurability() == 0) {
         DurabilityItem durabilityItem = new DurabilityItem(player, item);
         if (durabilityItem.isValid()) {
            durabilityItem.decreaseDurability(count);
            ItemStack item2 = durabilityItem.toItem();
            if (item2 == null) {
               player.getInventory().setItem(equipmentSlot, null);
               player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 1.0F, 1.0F);
            } else {
               player.getInventory().getItem(equipmentSlot).setItemMeta(item2.getItemMeta());
            }
         }
      }
   }

   private boolean hasItem(Player player, EquipmentSlot equipmentSlot) {
      return player.getInventory().getItem(equipmentSlot) != null && player.getInventory().getItem(equipmentSlot).getType() != Material.AIR;
   }
}
