package com.Zrips.CMI.Modules.Selection;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.Modules.Portals.CuboidArea;
import com.Zrips.CMI.events.CMISelectionEvent;
import net.Zrips.CMILib.Container.CMICuboidArea;
import net.Zrips.CMILib.Items.CMIItemStack;
import net.Zrips.CMILib.Items.CMIMaterial;
import net.Zrips.CMILib.Locale.LC;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

public class SelectionListener implements Listener {
  private CMI plugin;

  public SelectionListener(CMI cmi) {
    this.plugin = cmi;
  }

  @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
  public void onSelection(PlayerInteractEvent playerInteractEvent) {
    if (playerInteractEvent.getPlayer() != null
        && (playerInteractEvent.getAction() == Action.LEFT_CLICK_BLOCK
            || playerInteractEvent.getAction() == Action.RIGHT_CLICK_BLOCK)) {
      Player player = playerInteractEvent.getPlayer();
      CMIMaterial cmiMaterial = CMIMaterial.get(CMIItemStack.getItemInMainHand(player));
      if (cmiMaterial == this.plugin.getConfigManager().getSelectionTool().getCMIType()) {
        if (player.getGameMode() == GameMode.CREATIVE) {
          playerInteractEvent.setCancelled(true);
        }

        if (PermissionsManager.CMIPerm.select.hasPermission(player)) {
          Block block = playerInteractEvent.getClickedBlock();
          if (playerInteractEvent.getAction() == Action.LEFT_CLICK_BLOCK) {
            Location location = block.getLocation();
            this.plugin.getSelectionManager().placeLoc1(player, location, true);
            CMICuboidArea cmiCuboidArea =
                SelectionManager.getPlayerSelection(player).getSelectedCuboid();
            if (SelectionManager.getPlayerSelection(player).placedBothPoints()) {
              CMISelectionEvent cmiSelectionEvent =
                  new CMISelectionEvent(
                      player,
                      new CuboidArea(
                          cmiCuboidArea.getWorld(),
                          cmiCuboidArea.getLowPoint(),
                          cmiCuboidArea.getHighPoint()));
              Bukkit.getPluginManager().callEvent(cmiSelectionEvent);
            }

            LC.Selection_PrimaryPoint.sendMessage(
                player,
                new Object[] {
                  "[point]",
                  LC.Selection_CoordsTop.getLocale(
                      new Object[] {
                        location.getBlockX(), location.getBlockY(), location.getBlockZ()
                      })
                });
            playerInteractEvent.setCancelled(true);
          } else if (playerInteractEvent.getAction() == Action.RIGHT_CLICK_BLOCK) {
            boolean flag = false;

            try {
              if (playerInteractEvent.getHand() == EquipmentSlot.HAND) {
                flag = true;
              }
            } catch (Exception | NoSuchMethodError value) {
              flag = true;
            }

            if (flag) {
              Location clickedLocation = block.getLocation();
              this.plugin.getSelectionManager().placeLoc2(player, clickedLocation, true);
              CMICuboidArea cuboid =
                  SelectionManager.getPlayerSelection(player).getSelectedCuboid();
              if (SelectionManager.getPlayerSelection(player).placedBothPoints()) {
                CMISelectionEvent selectionEvent =
                    new CMISelectionEvent(
                        player,
                        new CuboidArea(
                            cuboid.getWorld(), cuboid.getLowPoint(), cuboid.getHighPoint()));
                Bukkit.getPluginManager().callEvent(selectionEvent);
              }

              LC.Selection_SecondaryPoint.sendMessage(
                  player,
                  new Object[] {
                    "[point]",
                    LC.Selection_CoordsBottom.getLocale(
                        new Object[] {
                          clickedLocation.getBlockX(),
                          clickedLocation.getBlockY(),
                          clickedLocation.getBlockZ()
                        })
                  });
              playerInteractEvent.setCancelled(true);
            }
          }

          SelectionManager.getPlayerSelection(player).showFull();
        }
      }
    }
  }
}
