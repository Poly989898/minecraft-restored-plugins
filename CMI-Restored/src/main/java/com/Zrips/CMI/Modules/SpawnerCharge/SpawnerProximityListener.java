package com.Zrips.CMI.Modules.SpawnerCharge;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import net.Zrips.CMILib.Effects.CMIEffectManager.CMIParticle;
import net.Zrips.CMILib.Items.CMIMaterial;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class SpawnerProximityListener implements Listener {
  private CMI plugin;

  public SpawnerProximityListener(CMI cmi) {
    this.plugin = cmi;
  }

  @EventHandler(priority = EventPriority.NORMAL)
  public void BlockPlaceEventRange(BlockPlaceEvent blockPlaceEvent) {
    if (!blockPlaceEvent.isCancelled()) {
      if (CMIMaterial.SPAWNER.equals(blockPlaceEvent.getBlock().getType())) {
        if (this.plugin.getSpawnerChargesManager().isSpawnersProximityUse()) {
          if (!this.plugin.getSpawnerChargesManager().isSpawnersSupportDisabled()) {
            if (!PermissionsManager.CMIPerm.spawners_proximity_bypass.hasPermission(
                blockPlaceEvent.getPlayer())) {
              int value = this.plugin.getSpawnerChargesManager().getSpawnersProximityRange();
              Location location = blockPlaceEvent.getBlock().getLocation();
              World world = location.getWorld();
              Location nearbyLocation = null;

              label65:
              for (int checkX = location.getBlockX() - value;
                  checkX < location.getBlockX() + value + 1;
                  checkX++) {
                for (int checkY = location.getBlockY() - value;
                    checkY < location.getBlockY() + value + 1;
                    checkY++) {
                  for (int checkZ = location.getBlockZ() - value;
                      checkZ < location.getBlockZ() + value + 1;
                      checkZ++) {
                    if (location.getBlockX() != checkX
                        || location.getBlockY() != checkY
                        || location.getBlockZ() != checkZ) {
                      Block block = world.getBlockAt(checkX, checkY, checkZ);
                      if (CMIMaterial.SPAWNER.equals(block.getType())) {
                        nearbyLocation = block.getLocation();
                        break label65;
                      }
                    }
                  }
                }
              }

              if (nearbyLocation != null) {
                this.plugin
                    .getParticleManager()
                    .drawLine(
                        blockPlaceEvent.getPlayer(),
                        location,
                        nearbyLocation,
                        CMIParticle.DUST,
                        true);
                CMILC.info_CantPlaceSpawner.sendMessage(
                    blockPlaceEvent.getPlayer(), "[range]", value);
                blockPlaceEvent.setCancelled(true);
              }
            }
          }
        }
      }
    }
  }
}
