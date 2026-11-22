package com.Zrips.CMI.Modules.AStand;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.commands.list.armorstand;
import com.Zrips.CMI.utils.CMIEncoder;
import java.util.UUID;
import net.Zrips.CMILib.NBT.CMINBT;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

public class ArmorStandListener implements Listener {
  private CMI plugin;

  public ArmorStandListener(CMI cmi) {
    this.plugin = cmi;
  }

  @EventHandler
  public void onQuit(PlayerQuitEvent playerQuitEvent) {
    this.plugin
        .getArmorStandManager()
        .removeEditorWithDelay(playerQuitEvent.getPlayer().getUniqueId());
  }

  @EventHandler
  public void onBlockInteract(PlayerInteractEvent playerInteractEvent) {
    if (!playerInteractEvent.isCancelled()) {
      CMIArmorStandEdit cmiArmorStandEdit =
          this.plugin.getArmorStandManager().getEditor(playerInteractEvent.getPlayer());
      if (cmiArmorStandEdit != null) {
        Player player = playerInteractEvent.getPlayer();
        if (cmiArmorStandEdit.getCopy() != null) {
          playerInteractEvent.setCancelled(true);
          cmiArmorStandEdit.setCopy(null);
          cmiArmorStandEdit.disableEditMode();
          CMILC.info(armorstand.class, player, "canceled");
        }
      }
    }
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onArmorStandDeath(EntityDeathEvent entityDeathEvent) {
    if (entityDeathEvent.getEntity().getType().name().equals("ARMOR_STAND")
        && this.plugin
            .getArmorStandManager()
            .isBeingEdited(entityDeathEvent.getEntity().getUniqueId())) {
      UUID uuid =
          this.plugin
              .getArmorStandManager()
              .getEditorOf(entityDeathEvent.getEntity().getUniqueId());
      if (uuid != null) {
        Player player = CMIUser.getOnlinePlayer(uuid);
        if (player != null) {
          CMIUser.closeInventory(player);
        }
      }
    }
  }

  @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
  public void armorStandSpawnEvent(CreatureSpawnEvent creatureSpawnEvent) {
    if (creatureSpawnEvent.getSpawnReason().equals(SpawnReason.DEFAULT)
        && creatureSpawnEvent.getEntity().getType().name().equals("ARMOR_STAND")) {
      Player player = null;
      Double amount = 10.0;

      for (Player playerSecond :
          this.plugin
              .getUtilManager()
              .getPlayersFromRangeForCounter(creatureSpawnEvent.getLocation(), 5, true)) {
        CMIUser cmiUser = this.plugin.getPlayerManager().getUser(playerSecond);
        if (cmiUser != null) {
          ItemStack itemStack = cmiUser.getItemInHand();
          ItemStack item = cmiUser.getItemInOffHand();
          if ((itemStack != null && itemStack.getType().equals(Material.ARMOR_STAND)
                  || item != null && item.getType().equals(Material.ARMOR_STAND))
              && (player == null
                  || !(amount
                      < creatureSpawnEvent
                          .getEntity()
                          .getLocation()
                          .distance(playerSecond.getLocation())))) {
            amount =
                creatureSpawnEvent.getEntity().getLocation().distance(playerSecond.getLocation());
            player = playerSecond;
          }
        }
      }

      Boolean flag = this.processArmorStand(player, creatureSpawnEvent.getEntity());
      if (flag == null) {
        creatureSpawnEvent.setCancelled(true);
      } else if (!flag) {
        Player playerThird = player;
        if (playerThird != null) {
          CMIScheduler.runAtEntityLater(
              this.plugin,
              playerThird,
              () -> {
                Block block = this.plugin.getUtilManager().getTargetBlock(playerThird, 5);
                if (block != null && !block.getType().equals(Material.AIR)) {
                  Entity entity =
                      this.plugin
                          .getUtilManager()
                          .getClosestEntity(
                              block.getLocation().clone().add(0.5, 0.5, 0.5),
                              2,
                              EntityType.ARMOR_STAND);
                  if (entity != null) {
                    if (entity.equals(creatureSpawnEvent.getEntity())) {
                      if (PermissionsManager.CMIPerm.armorstand_hands.hasPermission(playerThird)) {
                        ArmorStand armorStand = (ArmorStand) creatureSpawnEvent.getEntity();
                        armorStand.setArms(true);
                      }
                    }
                  }
                }
              },
              1L);
        }
      }
    }
  }

  private Boolean processArmorStand(Player player, Entity entity) {
    if (player == null) {
      return false;
    }

    CMIUser cmiUser = this.plugin.getPlayerManager().getUser(player);
    if (cmiUser == null) {
      return false;
    }

    ItemStack itemStack = cmiUser.getItemInHand();
    ItemStack item = cmiUser.getItemInOffHand();
    if (itemStack != null && itemStack.getType().equals(Material.ARMOR_STAND)
        || item != null && item.getType().equals(Material.ARMOR_STAND)) {
      ItemStack itemSecond = null;
      if (itemStack != null && new CMINBT(itemStack).hasNBT("CMISavedArmorStand")) {
        itemSecond = itemStack;
      } else if (item != null && new CMINBT(item).hasNBT("CMISavedArmorStand")) {
        itemSecond = item;
      }

      if (itemSecond == null) {
        return false;
      } else {
        String string = new CMINBT(itemSecond).getString("CMISavedArmorStand");
        if (string == null) {
          return false;
        } else {
          string = CMIEncoder.decode(string);
          if (string == null) {
            return false;
          } else {
            ArmorStandSave armorStandSave =
                this.plugin.getArmorStandManager().getSavedArmorStand(string);
            if (armorStandSave == null) {
              return false;
            } else if (ArmorStandManager.TemplatesSpecificPermission
                && !PermissionsManager.CMIPerm.command_armorstand_template_$1.hasPermission(
                    player, true, armorStandSave.getName().toLowerCase())) {
              ItemStack itemThird = itemSecond.clone();
              itemThird.setAmount(1);
              CMIScheduler.runTask(
                  this.plugin, () -> player.getInventory().addItem(new ItemStack[] {itemThird}));
              return null;
            } else {
              armorStandSave.apply((ArmorStand) entity);
              return true;
            }
          }
        }
      }
    } else {
      return false;
    }
  }
}
