package com.Zrips.CMI.Modules.Homes;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.commands.list.sethome;
import net.Zrips.CMILib.Container.CMIBlock;
import net.Zrips.CMILib.Container.CMILocation;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import org.bukkit.Location;
import org.bukkit.World.Environment;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;

public class HomeListener1_11 implements Listener {
  private CMI plugin;

  public HomeListener1_11(CMI cmi) {
    this.plugin = cmi;
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onEnderChestClick(PlayerBedEnterEvent playerBedEnterEvent) {
    if (this.plugin.getHomeManager().isHomesBedInteraction()) {
      Block block = playerBedEnterEvent.getBed();
      if (!block.getWorld().getEnvironment().equals(Environment.NETHER)
          && !block.getWorld().getEnvironment().equals(Environment.THE_END)
          && PermissionsManager.CMIPerm.bedhome.hasPermission(playerBedEnterEvent.getPlayer())) {
        CMIScheduler.runAtLocation(
            this.plugin,
            block.getLocation(),
            () -> {
              Player player = playerBedEnterEvent.getPlayer();
              Block bedBlock = playerBedEnterEvent.getBed();
              CMIUser cmiUser = this.plugin.getPlayerManager().getUser(player);
              if (cmiUser != null) {
                CmiHome cmiHome =
                    cmiUser.getHome(CMI.getInstance().getHomeManager().getDefaultBedHomeName());
                CMIBlock cmiBlock = new CMIBlock(bedBlock);
                Block bedFootBlock = cmiBlock.getBedFootBlock();
                Location location =
                    bedFootBlock == null
                        ? player.getLocation()
                        : bedFootBlock.getLocation().add(0.5, 0.6, 0.5);
                if (cmiHome == null
                    || !(this.plugin.getUtilManager().getDistance(cmiHome.getLoc(), location)
                        < 1.0)) {
                  location.setDirection(player.getLocation().getDirection());
                  CmiHome bedHome =
                      new CmiHome(
                          CMI.getInstance().getHomeManager().getDefaultBedHomeName(),
                          new CMILocation(location));
                  bedHome.setBed(true);
                  cmiUser.addHome(bedHome, true);
                  CMILC.info(sethome.class, player, "set", "[homeName]", bedHome.getName());
                }
              }
            });
      }
    }
  }
}
