package com.Zrips.CMI.AllListeners;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.events.CMIPortalCreateEvent;
import java.util.ArrayList;
import net.Zrips.CMILib.CMILib;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.PortalCreateEvent;
import org.bukkit.event.world.PortalCreateEvent.CreateReason;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class PortalCreationListener implements Listener {
  private CMI plugin;

  public PortalCreationListener(CMI cmi) {
    this.plugin = cmi;
  }

  @EventHandler(priority = EventPriority.LOW)
  public void PortalCreateEvent(PortalCreateEvent portalCreateEvent) {
    CMIPortalCreateEvent cmiPortalCreateEvent = this.proccessEvent(portalCreateEvent);
    if (cmiPortalCreateEvent != null && cmiPortalCreateEvent.isCancelled()) {
      portalCreateEvent.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.LOW)
  public void CMIPortalCreateEvent(CMIPortalCreateEvent cmiPortalCreateEvent) {
    if (cmiPortalCreateEvent.getType().equals(CMIPortalCreateEvent.CMIPortalType.Nether)) {
      Player player = cmiPortalCreateEvent.getPlayer();

      try {
        if (!cmiPortalCreateEvent.getReason().equals(CreateReason.FIRE)) {
          return;
        }
      } catch (Throwable throwable) {
      }

      if (!this.plugin.getConfigManager().isNetherPortalPreventCreation()
          || player != null
              && PermissionsManager.CMIPerm.netherportalbypass.hasPermission(player)) {
        if (cmiPortalCreateEvent.getHeight()
            > this.plugin.getConfigManager().NetherPortalMaxHeight) {
          cmiPortalCreateEvent.setCancelled(true);
          if (player != null) {
            CMILC.NetherPortal_ToHigh.sendMessage(
                player, "[max]", this.plugin.getConfigManager().NetherPortalMaxHeight);
          }
        } else if (cmiPortalCreateEvent.getWidth()
            > this.plugin.getConfigManager().NetherPortalMaxWidth) {
          cmiPortalCreateEvent.setCancelled(true);
          if (player != null) {
            CMILC.NetherPortal_ToWide.sendMessage(
                player, "[max]", this.plugin.getConfigManager().NetherPortalMaxWidth);
          }
        } else {
          CMILC.NetherPortal_Creation.sendMessage(
              player,
              "[height]",
              cmiPortalCreateEvent.getHeight(),
              "[width]",
              cmiPortalCreateEvent.getWidth());
        }
      } else {
        cmiPortalCreateEvent.setCancelled(true);
        if (player != null) {
          CMILC.NetherPortal_Disabled.sendMessage(player);
        }
      }
    }
  }

  private CMIPortalCreateEvent proccessEvent(PortalCreateEvent portalCreateEvent) {
    ArrayList<Location> arrayList = new ArrayList();
    ArrayList portalBlocks = new ArrayList();

    try {
      if (Version.isCurrentEqualOrLower(Version.v1_13_R2)) {
        portalBlocks =
            (ArrayList)
                portalCreateEvent.getClass().getMethod("getBlocks").invoke(portalCreateEvent);
      } else {
        portalBlocks =
            (ArrayList)
                portalCreateEvent.getClass().getMethod("getBlocks").invoke(portalCreateEvent);
      }
    } catch (Throwable throwable) {
      throwable.printStackTrace();
    }

    int value = 255;
    int maxA = -255;
    int minA = Integer.MAX_VALUE;
    int minB = Integer.MAX_VALUE;
    int maxB = -2147483647;
    int maxC = -2147483647;
    CMIPortalCreateEvent.CMIPortalType cmiPortalType = CMIPortalCreateEvent.CMIPortalType.End;

    for (Object object : portalBlocks) {
      if (!Version.isCurrentEqualOrHigher(Version.v1_14_R1)
          || !((BlockState) object).getType().equals(Material.OBSIDIAN)) {
        Location location =
            Version.isCurrentEqualOrHigher(Version.v1_14_R1)
                ? ((BlockState) object).getLocation()
                : ((Block) object).getLocation();
        arrayList.add(location);
        if (location.getBlockY() < value) {
          value = location.getBlockY();
        }

        if (location.getBlockX() < minA) {
          minA = location.getBlockX();
        }

        if (location.getBlockZ() < minB) {
          minB = location.getBlockZ();
        }

        if (location.getBlockY() > maxA) {
          maxA = location.getBlockY();
        }

        if (location.getBlockX() > maxB) {
          maxB = location.getBlockX();
        }

        if (location.getBlockZ() > maxC) {
          maxC = location.getBlockZ();
        }
      }
    }

    int distance = Math.abs(maxA - value);
    distance = distance < 0 ? -distance : distance;
    int widthX = maxB - minA;
    widthX = widthX < 0 ? -widthX : widthX;
    int widthZ = maxC - minB;
    widthZ = widthZ < 0 ? -widthZ : widthZ;
    widthZ = widthZ > 0 ? widthZ : widthX;
    if (distance >= 2) {
      cmiPortalType = CMIPortalCreateEvent.CMIPortalType.Nether;
    }

    if (Version.isCurrentEqualOrLower(Version.v1_15_R1)) {
      widthZ++;
      if (cmiPortalType == CMIPortalCreateEvent.CMIPortalType.Nether) {
        distance += 2;
      }
    } else if (Version.isCurrentEqualOrHigher(Version.v1_14_R1)
        && cmiPortalType == CMIPortalCreateEvent.CMIPortalType.Nether) {
      widthZ++;
      distance++;
    } else {
      widthZ--;
    }

    Block block = getMiddleBlock(arrayList);
    if (block == null) {
      return null;
    }

    Player player = null;
    if (Version.isCurrentEqualOrHigher(Version.v1_14_R1)
        && portalCreateEvent.getEntity() instanceof Player) {
      player = (Player) portalCreateEvent.getEntity();
    } else {
      for (Player nearbyPlayer :
          this.plugin
              .getUtilManager()
              .getPlayersFromRangeForCounter(block.getLocation(), 20, true)) {
        ItemStack itemStack =
            CMILib.getInstance().getReflectionManager().getItemInMainHand(nearbyPlayer);
        if (itemStack != null && itemStack.getType() == Material.FLINT_AND_STEEL) {
          player = nearbyPlayer;
          break;
        }
      }
    }

    CMIPortalCreateEvent cmiPortalCreateEvent =
        new CMIPortalCreateEvent(
            player, portalCreateEvent.getWorld(), portalCreateEvent.getReason(), distance, widthZ);
    cmiPortalCreateEvent.setType(cmiPortalType);
    cmiPortalCreateEvent.setLowestPoint(new Vector(minA, value, minB));
    Bukkit.getServer().getPluginManager().callEvent(cmiPortalCreateEvent);
    return cmiPortalCreateEvent;
  }

  private static Block getMiddleBlock(ArrayList<Location> arrayList) {
    if (arrayList.isEmpty()) {
      return null;
    }

    int value = 256;
    ArrayList<Location> locations = new ArrayList();

    for (Location location : arrayList) {
      if (location.getBlockY() < value) {
        value = location.getBlockY();
      }
    }

    for (Location location : arrayList) {
      if (location.getBlockY() == value) {
        locations.add(location);
      }
    }

    if (locations.isEmpty()) {
      return null;
    }

    int midIndex = (int) (locations.size() / 2.0);
    return midIndex <= locations.size()
        ? ((Location) locations.get(midIndex)).getBlock()
        : ((Location) locations.get(0)).getBlock();
  }
}
