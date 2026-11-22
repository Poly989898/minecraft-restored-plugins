package com.Zrips.CMI.AllListeners;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Config;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Permissions.PermissionInfo;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import net.Zrips.CMILib.Entities.CMIEntityType;
import net.Zrips.CMILib.Items.CMIMaterial;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World.Environment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPortalEvent;
import org.bukkit.event.entity.EntityTeleportEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerEditBookEvent;
import org.bukkit.inventory.meta.BookMeta;

public class GlicthListeners implements Listener {
  private CMI plugin;

  public GlicthListeners(CMI cmi) {
    this.plugin = cmi;
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onCommand(PlayerCommandPreprocessEvent playerCommandPreprocessEvent) {
    if (!playerCommandPreprocessEvent.isCancelled()
        && Config.NoCommandsInBed
        && playerCommandPreprocessEvent.getPlayer().isSleeping()) {
      playerCommandPreprocessEvent.setCancelled(true);
      CMILC.info_NoCommandWhileSleeping.sendMessage(playerCommandPreprocessEvent.getPlayer());
    }
  }

  @EventHandler(priority = EventPriority.NORMAL)
  public void onPlayerEditBookEvent(PlayerEditBookEvent playerEditBookEvent) {
    if (!playerEditBookEvent.isCancelled() && Config.LimitBooks) {
      BookMeta bookMeta = playerEditBookEvent.getNewBookMeta();
      PermissionInfo permissionInfo =
          this.plugin
              .getPermissionsManager()
              .getPermissionInfo(
                  playerEditBookEvent.getPlayer(), PermissionsManager.CMIPerm.book_pages_$1);
      int value = permissionInfo.getMaxValue(20);
      if (bookMeta.getPageCount() > value) {
        for (int page = value + 1; page <= bookMeta.getPageCount(); page++) {
          bookMeta.setPage(page, "");
        }

        playerEditBookEvent.setNewBookMeta(bookMeta);
        CMILC.info_book_exploit.sendMessage(playerEditBookEvent.getPlayer(), "[amount]", value);
      }
    }
  }

  @EventHandler(priority = EventPriority.NORMAL)
  public void EntityTeleportEvent(EntityTeleportEvent entityTeleportEvent) {
    if (Config.BlockEndPortalItemTransfers) {
      if (Version.isCurrentEqualOrHigher(Version.v1_20_R4)) {
        if (!entityTeleportEvent.getEntityType().equals(EntityType.ITEM)) {
          return;
        }
      } else if (CMIEntityType.get(entityTeleportEvent.getEntityType()) != CMIEntityType.ITEM) {
        return;
      }

      if (entityTeleportEvent.getFrom().getWorld().getEnvironment().equals(Environment.THE_END)) {
        Location location = entityTeleportEvent.getFrom();
        if (CMIMaterial.get(location.getBlock()).equals(CMIMaterial.END_GATEWAY)
            || CMIMaterial.get(location.clone().add(1.0, 0.0, 0.0).getBlock())
                .equals(CMIMaterial.END_GATEWAY)
            || CMIMaterial.get(location.clone().add(0.0, 0.0, 1.0).getBlock())
                .equals(CMIMaterial.END_GATEWAY)
            || CMIMaterial.get(location.clone().add(-1.0, 0.0, 0.0).getBlock())
                .equals(CMIMaterial.END_GATEWAY)
            || CMIMaterial.get(location.clone().add(0.0, 0.0, -1.0).getBlock())
                .equals(CMIMaterial.END_GATEWAY)
            || CMIMaterial.get(location.clone().add(0.0, 1.0, 0.0).getBlock())
                .equals(CMIMaterial.END_GATEWAY)
            || CMIMaterial.get(location.clone().add(0.0, -1.0, 0.0).getBlock())
                .equals(CMIMaterial.END_GATEWAY)) {
          Item item = (Item) entityTeleportEvent.getEntity();
          item.setPickupDelay(0);
          entityTeleportEvent.setCancelled(true);
          entityTeleportEvent
              .getEntity()
              .setVelocity(entityTeleportEvent.getEntity().getVelocity().multiply(-1));
        }
      }
    }
  }

  @EventHandler(priority = EventPriority.NORMAL)
  public void EntityPortalEvent(EntityPortalEvent entityPortalEvent) {
    if (Config.BlockEndPortalItemTransfers) {
      if (Version.isCurrentEqualOrHigher(Version.v1_20_R4)) {
        if (!entityPortalEvent.getEntityType().equals(EntityType.ITEM)) {
          return;
        }
      } else if (CMIEntityType.get(entityPortalEvent.getEntityType()) != CMIEntityType.ITEM) {
        return;
      }

      if (entityPortalEvent.getFrom().getWorld().getEnvironment().equals(Environment.THE_END)
          && entityPortalEvent.getFrom().getBlock().getType().equals(Material.END_PORTAL)) {
        entityPortalEvent.setCancelled(true);
      }
    }
  }
}
