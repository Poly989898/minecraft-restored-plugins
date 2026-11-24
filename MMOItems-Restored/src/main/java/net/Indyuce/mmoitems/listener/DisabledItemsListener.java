package net.Indyuce.mmoitems.listener;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.api.event.PlayerAttackEvent;
import io.lumine.mythic.lib.api.item.NBTItem;
import io.lumine.mythic.lib.damage.MeleeAttackMetadata;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.util.MMOUtils;
import org.bukkit.GameMode;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;

public class DisabledItemsListener implements Listener {
   private final MMOItems plugin;

   public DisabledItemsListener(MMOItems var1) {
      this.plugin = var1;
   }

   @EventHandler(priority = EventPriority.LOWEST)
   public void rightClickEffects(PlayerInteractEvent var1) {
      if (var1.hasItem()) {
         NBTItem var2 = MythicLib.plugin.getVersion().getWrapper().getNBTItem(var1.getItem());
         if (MMOUtils.hasBeenRemoved(var2)) {
            var1.setCancelled(true);
         }
      }
   }

   @EventHandler(priority = EventPriority.LOWEST)
   public void meleeAttacks(PlayerAttackEvent var1) {
      if (var1.getAttack() instanceof MeleeAttackMetadata) {
         ItemStack var2 = var1.getPlayer().getInventory().getItem(((MeleeAttackMetadata)var1.getAttack()).getHand().toBukkit());
         NBTItem var3 = MythicLib.plugin.getVersion().getWrapper().getNBTItem(var2);
         if (MMOUtils.hasBeenRemoved(var3)) {
            var1.setCancelled(true);
         }
      }
   }

   @EventHandler(priority = EventPriority.LOWEST)
   public void specialToolAbilities(BlockBreakEvent var1) {
      Player var2 = var1.getPlayer();
      if (var2.getGameMode() != GameMode.CREATIVE) {
         NBTItem var3 = MythicLib.plugin.getVersion().getWrapper().getNBTItem(var2.getInventory().getItemInMainHand());
         if (MMOUtils.hasBeenRemoved(var3)) {
            var1.setCancelled(true);
         }
      }
   }

   @EventHandler(priority = EventPriority.LOWEST)
   public void rightClickWeaponInteractions(PlayerInteractEntityEvent var1) {
      Player var2 = var1.getPlayer();
      if (var1.getRightClicked() instanceof LivingEntity) {
         NBTItem var3 = MythicLib.plugin.getVersion().getWrapper().getNBTItem(var2.getInventory().getItemInMainHand());
         if (MMOUtils.hasBeenRemoved(var3)) {
            var1.setCancelled(true);
         }
      }
   }

   @EventHandler(priority = EventPriority.LOWEST)
   public void gemStonesAndItemStacks(InventoryClickEvent var1) {
      Player var2 = (Player)var1.getWhoClicked();
      if (var1.getAction() == InventoryAction.SWAP_WITH_CURSOR) {
         NBTItem var3 = MythicLib.plugin.getVersion().getWrapper().getNBTItem(var1.getCursor());
         if (MMOUtils.hasBeenRemoved(var3)) {
            var1.setCancelled(true);
         }
      }
   }

   @EventHandler(priority = EventPriority.LOWEST)
   public void handleCustomBows(EntityShootBowEvent var1) {
      if (var1.getProjectile() instanceof Arrow && var1.getEntity() instanceof Player) {
         NBTItem var2 = NBTItem.get(var1.getBow());
         if (MMOUtils.hasBeenRemoved(var2)) {
            var1.setCancelled(true);
         }
      }
   }

   @EventHandler(priority = EventPriority.LOWEST)
   public void handleVanillaEatenConsumables(PlayerItemConsumeEvent var1) {
      NBTItem var2 = MythicLib.plugin.getVersion().getWrapper().getNBTItem(var1.getItem());
      if (MMOUtils.hasBeenRemoved(var2)) {
         var1.setCancelled(true);
      }
   }
}
