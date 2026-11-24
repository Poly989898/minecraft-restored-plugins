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
   public void itemDamage(PlayerItemDamageEvent var1) {
      DurabilityItem var2 = new DurabilityItem(var1.getPlayer(), var1.getItem());
      if (var2.isValid()) {
         var2.decreaseDurability(var1.getDamage());
         ItemStack var3 = var2.toItem();
         if (var3 == null) {
            var1.setDamage(999);
         } else {
            var1.setCancelled(true);
            var1.getItem().setItemMeta(var3.getItemMeta());
         }
      }
   }

   @EventHandler(ignoreCancelled = true)
   public void playerDamage(EntityDamageEvent var1) {
      if (var1.getEntityType() == EntityType.PLAYER && !IGNORED_CAUSES.contains(var1.getCause())) {
         Player var2 = (Player)var1.getEntity();
         int var3 = Math.max((int)var1.getDamage() / 4, 1);

         for (EquipmentSlot var7 : ARMOR_SLOTS) {
            if (this.hasItem(var2, var7)) {
               this.handleUndamageableItem(var2.getInventory().getItem(var7), var2, var7, var3);
            }
         }
      }
   }

   @EventHandler(ignoreCancelled = true)
   public void playerMeleeAttack(EntityDamageByEntityEvent var1) {
      if (var1.getDamage() != 0.0
         && var1.getCause() == DamageCause.ENTITY_ATTACK
         && var1.getEntity() instanceof LivingEntity
         && var1.getDamager() instanceof Player
         && !var1.getEntity().hasMetadata("NPC")
         && !var1.getDamager().hasMetadata("NPC")) {
         Player var2 = (Player)var1.getDamager();
         ItemStack var3 = var2.getInventory().getItemInMainHand();
         this.handleUndamageableItem(var3, var2, EquipmentSlot.HAND, 1);
      }
   }

   @EventHandler(ignoreCancelled = true)
   public void playerBowAttack(EntityShootBowEvent var1) {
      if (var1.getEntity() instanceof Player) {
         Player var2 = (Player)var1.getEntity();
         ItemStack var3 = var1.getBow();
         this.handleUndamageableItem(var3, var2, EquipmentSlot.HAND, 1);
      }
   }

   @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
   public void mendEvent(PlayerItemMendEvent var1) {
      if (var1.getRepairAmount() > 0) {
         DurabilityItem var2 = new DurabilityItem(var1.getPlayer(), var1.getItem());
         if (var2.isValid()) {
            var1.getItem().setItemMeta(var2.addDurability(var1.getRepairAmount()).toItem().getItemMeta());
            var1.setCancelled(true);
         }
      }
   }

   private void handleUndamageableItem(ItemStack var1, Player var2, EquipmentSlot var3, int var4) {
      if (var1.getType().getMaxDurability() == 0) {
         DurabilityItem var5 = new DurabilityItem(var2, var1);
         if (var5.isValid()) {
            var5.decreaseDurability(var4);
            ItemStack var6 = var5.toItem();
            if (var6 == null) {
               var2.getInventory().setItem(var3, null);
               var2.getWorld().playSound(var2.getLocation(), Sound.ENTITY_ITEM_BREAK, 1.0F, 1.0F);
            } else {
               var2.getInventory().getItem(var3).setItemMeta(var6.getItemMeta());
            }
         }
      }
   }

   private boolean hasItem(Player var1, EquipmentSlot var2) {
      return var1.getInventory().getItem(var2) != null && var1.getInventory().getItem(var2).getType() != Material.AIR;
   }
}
