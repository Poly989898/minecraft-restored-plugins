package com.Zrips.CMI.Modules.AttachedCommands;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIEquipmentSlot;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;

public class CustomNBTListener1_8 implements Listener {
  private CMI plugin;

  public CustomNBTListener1_8(CMI cmi) {
    this.plugin = cmi;
  }

  @EventHandler(priority = EventPriority.NORMAL)
  public void playerInteractAtEntityCommand(
      PlayerInteractAtEntityEvent playerInteractAtEntityEvent) {
    Entity entity = playerInteractAtEntityEvent.getRightClicked();
    if (CustomNBTListener.processInteract(
        playerInteractAtEntityEvent.getPlayer(),
        entity instanceof Player ? (Player) entity : null,
        CMIEquipmentSlot.get(playerInteractAtEntityEvent),
        Action.RIGHT_CLICK_AIR,
        null,
        playerInteractAtEntityEvent.getRightClicked(),
        false)) {
      playerInteractAtEntityEvent.setCancelled(true);
    }
  }
}
