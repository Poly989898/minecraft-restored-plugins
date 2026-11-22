package com.Zrips.CMI.Modules.Painting;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIEvent;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import java.util.ArrayList;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.Art;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Hanging;
import org.bukkit.entity.Painting;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;

public class PaintingListener implements Listener {
  private CMI plugin;

  public PaintingListener(CMI cmi) {
    this.plugin = cmi;
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onPaintingBreak(PlayerQuitEvent playerQuitEvent) {
    Player player = playerQuitEvent.getPlayer();
    this.plugin.getPaintingManager().removeEditor(player);
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onPaintingBreak(HangingBreakEvent hangingBreakEvent) {
    if (!hangingBreakEvent.isCancelled()) {
      Hanging hanging = hangingBreakEvent.getEntity();
      if (hanging.getType() == EntityType.PAINTING) {
        this.plugin.getPaintingManager().removeEditor(hanging);
      }
    }
  }

  @EventHandler
  public void onSignInteractShift(PlayerInteractEvent playerInteractEvent) {
    if (!playerInteractEvent.isCancelled()) {
      try {
        if (playerInteractEvent.getHand() != EquipmentSlot.HAND) {
          return;
        }
      } catch (Exception | NoSuchMethodError value) {
      }

      Player player = playerInteractEvent.getPlayer();
      if (this.plugin.getPaintingManager().isEditing(player.getUniqueId())) {
        this.plugin.getPaintingManager().removeEditor(player.getUniqueId());
        CMILC.info_canceledEditingPainting.sendMessage(player);
        playerInteractEvent.setCancelled(true);
      }
    }
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onPlayerInteractPaiting(PlayerInteractEntityEvent playerInteractEntityEvent) {
    if (!playerInteractEntityEvent.isCancelled()) {
      try {
        if (playerInteractEntityEvent.getHand() != EquipmentSlot.HAND) {
          return;
        }
      } catch (Exception | NoSuchMethodError value) {
      }

      Entity entity = playerInteractEntityEvent.getRightClicked();
      Player player = playerInteractEntityEvent.getPlayer();
      if (this.plugin.getPaintingManager().isEditing(player.getUniqueId())) {
        this.plugin.getPaintingManager().removeEditor(player.getUniqueId());
        CMILC.info_canceledEditingPainting.sendMessage(player);
        playerInteractEntityEvent.setCancelled(true);
      } else if (entity.getType() == EntityType.PAINTING) {
        if (player.isSneaking()) {
          if (PermissionsManager.CMIPerm.scrollpainting.hasPermission(player)) {
            Painting painting = (Painting) entity;
            Block block = painting.getLocation().getBlock();
            BlockPlaceEvent blockPlaceEvent = CMIEvent.placeBlockEvent(block, player);
            Bukkit.getServer().getPluginManager().callEvent(blockPlaceEvent);
            if (!blockPlaceEvent.isCancelled()) {
              playerInteractEntityEvent.setCancelled(true);
              this.plugin.getPaintingManager().addEditor(player.getUniqueId(), entity);
              CMILC.info_startedEditingPainting.sendMessage(player);
            }
          }
        }
      }
    }
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onItemHeldChange(PlayerItemHeldEvent playerItemHeldEvent) {
    Player player = playerItemHeldEvent.getPlayer();
    int value = playerItemHeldEvent.getPreviousSlot();
    int count = playerItemHeldEvent.getNewSlot();
    boolean flag = value - count > 0;
    if (value == 0 && count == 8 || value == 8 && count == 0) {
      flag = !flag;
    }

    if (this.plugin.getPaintingManager().isEditing(player.getUniqueId())) {
      Painting painting = this.plugin.getPaintingManager().getPainting(player.getUniqueId());
      if (painting != null) {
        int countSecond = -1;
        Object[] objects = null;
        if (Version.isCurrentEqualOrLower(Version.v1_21_R2)) {
          try {
            Object object = painting.getClass().getMethod("getArt").invoke(painting);
            countSecond = (Integer) object.getClass().getMethod("ordinal").invoke(object);
            objects = (Object[]) Art.class.getMethod("values").invoke(null);
          } catch (Throwable throwable) {
            throwable.printStackTrace();
          }
        } else {
          countSecond = painting.getArt().ordinal();
          objects = Art.values();
        }

        if (objects != null) {
          int countThird = objects.length;
          ArrayList<Object> arrayList = new ArrayList();
          if (!flag) {
            for (int countFourth = countSecond + 1; countFourth < countThird; countFourth++) {
              arrayList.add(objects[countFourth]);
            }

            for (int countFifth = 0; countFifth < countSecond; countFifth++) {
              arrayList.add(objects[countFifth]);
            }
          } else {
            for (int countSixth = countSecond - 1; countSixth >= 0; countSixth--) {
              arrayList.add(objects[countSixth]);
            }

            for (int countSeventh = countThird - 1; countSeventh > countSecond; countSeventh--) {
              arrayList.add(objects[countSeventh]);
            }
          }

          for (Object valueSecond : arrayList) {
            if (painting.setArt((Art) valueSecond)) {
              break;
            }
          }

          String string = "";
          int countEighth = 0;
          if (Version.isCurrentEqualOrLower(Version.v1_21_R2)) {
            try {
              Object valueThird = painting.getClass().getMethod("getArt").invoke(painting);
              countEighth = (Integer) valueThird.getClass().getMethod("ordinal").invoke(valueThird);
              string = (String) valueThird.getClass().getMethod("name").invoke(valueThird);
            } catch (Throwable throwable2) {
              throwable2.printStackTrace();
            }
          } else {
            countEighth = painting.getArt().ordinal();
            string = painting.getArt().name();
          }

          CMILC.info_changedPainting.sendMessage(
              player, "[name]", string.toLowerCase(), "[id]", countEighth);
        }
      }
    }
  }
}
