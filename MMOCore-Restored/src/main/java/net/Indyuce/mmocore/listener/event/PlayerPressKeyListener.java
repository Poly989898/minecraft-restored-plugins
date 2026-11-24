package net.Indyuce.mmocore.listener.event;

import net.Indyuce.mmocore.api.event.PlayerKeyPressEvent;
import net.Indyuce.mmocore.api.player.PlayerData;
import net.Indyuce.mmocore.skill.cast.PlayerKey;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.Event.Result;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.EquipmentSlot;

public class PlayerPressKeyListener implements Listener {
   private static final long CLICK_EVENT_TIMEOUT = 30L;

   @EventHandler(priority = EventPriority.LOWEST)
   public void registerCrouchKey(PlayerToggleSneakEvent event) {
      if (event.isSneaking()) {
         PlayerKeyPressEvent called = new PlayerKeyPressEvent(PlayerData.get(event.getPlayer()), PlayerKey.CROUCH, event);
         Bukkit.getPluginManager().callEvent(called);
      }
   }

   @EventHandler(priority = EventPriority.LOWEST)
   public void registerClickKey(PlayerInteractEvent event) {
      if (event.useItemInHand() != Result.DENY && event.getAction().name().contains("CLICK") && event.getHand().equals(EquipmentSlot.HAND)) {
         PlayerData playerData = PlayerData.get(event.getPlayer());
         if (System.currentTimeMillis() - playerData.lastDropEvent < 30L) {
            playerData.lastDropEvent = 0L;
            return;
         }

         boolean rightClick = event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK;
         Bukkit.getPluginManager().callEvent(new PlayerKeyPressEvent(playerData, rightClick ? PlayerKey.RIGHT_CLICK : PlayerKey.LEFT_CLICK, event));
      }
   }

   @EventHandler(priority = EventPriority.LOWEST)
   public void registerDropKey(PlayerDropItemEvent event) {
      PlayerData playerData = PlayerData.get(event.getPlayer());
      Bukkit.getPluginManager().callEvent(new PlayerKeyPressEvent(playerData, PlayerKey.DROP, event));
      playerData.lastDropEvent = System.currentTimeMillis();
   }

   @EventHandler(priority = EventPriority.LOWEST)
   public void registerSwapHandsKey(PlayerSwapHandItemsEvent event) {
      PlayerKeyPressEvent called = new PlayerKeyPressEvent(PlayerData.get(event.getPlayer()), PlayerKey.SWAP_HANDS, event);
      Bukkit.getPluginManager().callEvent(called);
   }
}
