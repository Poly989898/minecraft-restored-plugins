package com.Zrips.CMI.Modules.Homes;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.commands.list.home;
import com.Zrips.CMI.commands.list.sethome;
import net.Zrips.CMILib.Container.CMIBlock;
import net.Zrips.CMILib.Container.CMILocation;
import net.Zrips.CMILib.Items.CMIMaterial;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.Location;
import org.bukkit.World.Environment;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Bed;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class HomeListener implements Listener {
  private CMI plugin;

  public HomeListener(CMI cmi) {
    this.plugin = cmi;
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void PlayerJoinEvent(PlayerJoinEvent playerJoinEvent) {
    if (this.plugin.getHomeManager().isLimitsNotifyOnLogin()) {
      Player player = playerJoinEvent.getPlayer();
      if (player != null) {
        CMIUser cmiUser = CMIUser.getUser(player);
        if (cmiUser != null) {
          if (!this.plugin.getHomeManager().canUseHomes(player)) {
            CMILC.info(home.class, player, "limitReached");
          }
        }
      }
    }
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onEnderChestClick(PlayerInteractEvent playerInteractEvent) {
    Block block = playerInteractEvent.getClickedBlock();
    if (block != null
        && CMIMaterial.isBed(block.getType())
        && !block.getWorld().getEnvironment().equals(Environment.NETHER)
        && !block.getWorld().getEnvironment().equals(Environment.THE_END)) {
      CMIScheduler.runAtLocation(
          this.plugin,
          block.getLocation(),
          () -> {
            Player player = playerInteractEvent.getPlayer();
            Block bedBlock = playerInteractEvent.getClickedBlock().getLocation().getBlock();
            if (Version.isCurrentEqualOrHigher(Version.v1_13_R1)) {
              try {
                Bed bed = (Bed) bedBlock.getBlockData();
                if (bed.isOccupied()) {
                  return;
                }
              } catch (Throwable throwable) {
              }
            }

            if (!this.plugin.getHomeManager().isOnlyShiftBed() || player.isSneaking()) {
              if (this.plugin.getHomeManager().isHomesBedInteraction()) {
                if (bedBlock == null || !CMIMaterial.isBed(bedBlock.getType())) {
                  return;
                }

                if (!playerInteractEvent.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
                  return;
                }

                if (!PermissionsManager.CMIPerm.bedhome.hasPermission(player)) {
                  return;
                }

                CMIUser cmiUser = this.plugin.getPlayerManager().getUser(player);
                if (cmiUser == null) {
                  return;
                }

                CmiHome cmiHome =
                    cmiUser.getHome(CMI.getInstance().getHomeManager().getDefaultBedHomeName());
                CMIBlock cmiBlock = new CMIBlock(bedBlock);
                Block bedFootBlock = cmiBlock.getBedFootBlock();
                Location location =
                    bedFootBlock == null
                        ? player.getLocation()
                        : bedFootBlock.getLocation().add(0.5, 0.6, 0.5);
                if (cmiHome != null
                    && this.plugin.getUtilManager().getDistance(cmiHome.getLoc(), location) < 1.0) {
                  return;
                }

                location.setDirection(player.getLocation().getDirection());
                CmiHome newHome =
                    new CmiHome(
                        CMI.getInstance().getHomeManager().getDefaultBedHomeName(),
                        new CMILocation(location));
                newHome.setBed(true);
                cmiUser.addHome(newHome, true);
                CMILC.info(sethome.class, player, "set", "[homeName]", newHome.getName());
              } else {
                player.setBedSpawnLocation(bedBlock.getLocation().clone());
                CMILC.info(sethome.class, player, "setBed");
              }
            }
          });
    }
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void bedBreak(BlockBreakEvent blockBreakEvent) {
    if (!blockBreakEvent.isCancelled()
        && this.plugin.getHomeManager().isRemoveBedLocationOnBedBreak()
        && CMIMaterial.isBed(blockBreakEvent.getBlock().getType())) {
      CMIBlock cmiBlock = new CMIBlock(blockBreakEvent.getBlock());
      Block block = cmiBlock.getBedFootBlock();
      if (block != null) {
        this.plugin.getHomeManager().removeBedHome(block.getLocation());
      }
    }
  }
}
