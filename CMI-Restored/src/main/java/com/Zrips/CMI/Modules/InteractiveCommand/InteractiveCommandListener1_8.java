package com.Zrips.CMI.Modules.InteractiveCommand;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIInteractType;
import com.Zrips.CMI.Modules.Hooks.CMIHookType;
import java.util.UUID;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;

public class InteractiveCommandListener1_8 implements Listener {
  private CMI plugin;

  public InteractiveCommandListener1_8(CMI cmi) {
    this.plugin = cmi;
  }

  @EventHandler(priority = EventPriority.NORMAL)
  public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent entityDamageByEntityEvent) {
    if (entityDamageByEntityEvent.getDamager() instanceof Player) {
      if (this.processInteraction(
          entityDamageByEntityEvent.getEntity(),
          (Player) entityDamageByEntityEvent.getDamager(),
          CMIInteractType.LeftClick)) {
        entityDamageByEntityEvent.setCancelled(true);
      }
    }
  }

  private boolean processInteraction(
      Entity entity, Player player, CMIInteractType cmiInteractType) {
    UUID uuid = entity.getUniqueId();
    CMIInteractiveCommand cmiInteractiveCommand =
        this.plugin.getInteractiveCommandManager().getByUUID(uuid);
    if (cmiInteractiveCommand == null) {
      if (!entity.hasMetadata("NPC")) {
        return false;
      }

      if (!CMIHookType.Citizens.isEnabled()) {
        return false;
      }

      Integer integer = this.plugin.getCitizensManager().getEntityId(entity);
      if (integer == null) {
        return false;
      }

      cmiInteractiveCommand = this.plugin.getInteractiveCommandManager().getByCitizensId(integer);
      if (cmiInteractiveCommand == null) {
        return false;
      }

      cmiInteractiveCommand.addUUID(uuid, integer);
    }

    Long lastUse = InteractiveCommandListener.limit.get(player.getUniqueId());
    if (lastUse != null && lastUse + 100L > System.currentTimeMillis()) {
      return false;
    }

    InteractiveCommandListener.limit.put(player.getUniqueId(), System.currentTimeMillis());
    player.closeInventory();
    CMIInteractiveCommand interactiveCommand = cmiInteractiveCommand;
    CMIScheduler.runTask(
        this.plugin,
        () -> {
          player.closeInventory();
          CMIInteractType clickType = cmiInteractType;
          if (cmiInteractType.equals(CMIInteractType.RightClick)) {
            if (player.isSneaking()) {
              clickType = CMIInteractType.RightShiftClick;
            }
          } else if (player.isSneaking()) {
            clickType = CMIInteractType.LeftShiftClick;
          }

          this.plugin
              .getSpecializedCommandManager()
              .processCmds(
                  "CMIIC" + interactiveCommand.getName(),
                  interactiveCommand.getCommands(player),
                  player,
                  clickType,
                  null);
        });
    return true;
  }

  @EventHandler(priority = EventPriority.NORMAL)
  public void onPlayerInteractAtEntityEvent(
      PlayerInteractAtEntityEvent playerInteractAtEntityEvent) {
    if (this.processInteraction(
        playerInteractAtEntityEvent.getRightClicked(),
        playerInteractAtEntityEvent.getPlayer(),
        CMIInteractType.RightClick)) {
      playerInteractAtEntityEvent.setCancelled(true);
    }
  }
}
