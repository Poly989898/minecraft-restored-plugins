package com.Zrips.CMI.Modules.AStand;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIEvent;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.commands.list.armorstand;
import net.Zrips.CMILib.CMILib;
import net.Zrips.CMILib.Items.CMIMaterial;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class ArmorStandListener1_8 implements Listener {
  private CMI plugin;

  public ArmorStandListener1_8(CMI cmi) {
    this.plugin = cmi;
  }

  public static boolean checkInteract(Player player, Entity entity) {
    PlayerInteractAtEntityEvent playerInteractAtEntityEvent =
        new PlayerInteractAtEntityEvent(player, entity, new Vector(0.1, 0.5, 0.1));
    Bukkit.getPluginManager().callEvent(playerInteractAtEntityEvent);
    if (playerInteractAtEntityEvent.isCancelled()) {
      return false;
    }

    playerInteractAtEntityEvent.setCancelled(true);
    return true;
  }

  public static boolean checkManipulation(Player player, Entity entity) {
    PlayerArmorStandManipulateEvent playerArmorStandManipulateEvent =
        new PlayerArmorStandManipulateEvent(
            player,
            (ArmorStand) entity,
            CMIMaterial.AIR.newItemStack(),
            CMIMaterial.AIR.newItemStack(),
            EquipmentSlot.HAND);
    Bukkit.getPluginManager().callEvent(playerArmorStandManipulateEvent);
    if (playerArmorStandManipulateEvent.isCancelled()) {
      return false;
    }

    playerArmorStandManipulateEvent.setCancelled(true);
    return true;
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onPlayerInteractAtEntityEvent(
      PlayerInteractAtEntityEvent playerInteractAtEntityEvent) {
    if (!playerInteractAtEntityEvent.isCancelled()) {
      Player player = playerInteractAtEntityEvent.getPlayer();
      if (!player.isSneaking()) {
        CMIArmorStandEdit cmiArmorStandEdit =
            this.plugin.getArmorStandManager().getEditor(playerInteractAtEntityEvent.getPlayer());
        if (cmiArmorStandEdit != null) {
          if (cmiArmorStandEdit.getCopy() != null) {
            Entity entity = playerInteractAtEntityEvent.getRightClicked();
            if (entity instanceof ArmorStand) {
              try {
                if (playerInteractAtEntityEvent.getClickedPosition() == null) {
                  return;
                }
              } catch (Exception | Error value) {
              }

              playerInteractAtEntityEvent.setCancelled(true);
              Block block = entity.getLocation().add(0.0, -1.0, 0.0).getBlock();
              BlockPlaceEvent blockPlaceEvent = CMIEvent.placeBlockEvent(block, player);
              Bukkit.getServer().getPluginManager().callEvent(blockPlaceEvent);
              if (!blockPlaceEvent.isCancelled()) {
                cmiArmorStandEdit.getCopy().duplicateOn((ArmorStand) entity);
                CMILC.info(armorstand.class, player, "updated");
              }
            }
          }
        }
      }
    }
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void PlayerInteractAtEntityEvent(PlayerInteractAtEntityEvent playerInteractAtEntityEvent) {
    if (!playerInteractAtEntityEvent.isCancelled()) {
      Entity entity = playerInteractAtEntityEvent.getRightClicked();
      Player player = playerInteractAtEntityEvent.getPlayer();
      if (player.isSneaking()) {
        try {
          if (playerInteractAtEntityEvent.getClickedPosition() == null) {
            return;
          }
        } catch (Exception | Error value) {
        }

        try {
          if (Version.isCurrentHigher(Version.v1_8_R3)
              && playerInteractAtEntityEvent.getHand() != EquipmentSlot.HAND) {
            return;
          }
        } catch (Exception exception) {
        }

        if (this.plugin.getArmorStandManager().isBeingEdited(entity.getUniqueId())) {
          CMILC.info("armorstand", player, "cantClick");
          playerInteractAtEntityEvent.setCancelled(true);
        } else if (player.getItemInHand().getType() == Material.NAME_TAG
            && player.getItemInHand().hasItemMeta()
            && !player.getItemInHand().getItemMeta().hasDisplayName()) {
          playerInteractAtEntityEvent.setCancelled(true);
        } else {
          if (PermissionsManager.CMIPerm.command_$1.hasPermission(player, "armorstand")) {
            CMIScheduler.runAtEntity(
                this.plugin,
                entity,
                () -> this.plugin.getArmorStandManager().openEditor(player, entity, false));
            playerInteractAtEntityEvent.setCancelled(true);
          }
        }
      }
    }
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void editorCheck(PlayerInteractAtEntityEvent playerInteractAtEntityEvent) {
    if (!playerInteractAtEntityEvent.isCancelled()) {
      Entity entity = playerInteractAtEntityEvent.getRightClicked();
      Player player = playerInteractAtEntityEvent.getPlayer();

      try {
        if (playerInteractAtEntityEvent.getClickedPosition() == null) {
          return;
        }
      } catch (Exception | Error value) {
      }

      try {
        if (Version.isCurrentHigher(Version.v1_8_R3)
            && playerInteractAtEntityEvent.getHand() != EquipmentSlot.HAND) {
          return;
        }
      } catch (Exception exception) {
      }

      if (this.plugin.getArmorStandManager().isBeingEdited(entity.getUniqueId())) {
        CMILC.info("armorstand", player, "cantClick");
        playerInteractAtEntityEvent.setCancelled(true);
      }
    }
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void vanillaBugFix(PlayerInteractAtEntityEvent playerInteractAtEntityEvent) {
    if (!playerInteractAtEntityEvent.isCancelled()) {
      Entity entity = playerInteractAtEntityEvent.getRightClicked();
      if (entity.getType().name().equals("ARMOR_STAND")) {
        Player player = playerInteractAtEntityEvent.getPlayer();
        if (this.plugin.getArmorStandManager().isBeingEdited(entity.getUniqueId())) {
          CMILC.info("armorstand", player, "cantClick");
          playerInteractAtEntityEvent.setCancelled(true);
        }

        try {
          if (Version.isCurrentHigher(Version.v1_8_R3)
              && playerInteractAtEntityEvent.getHand() != EquipmentSlot.HAND) {
            return;
          }
        } catch (Exception exception) {
        }

        if (!player.isSneaking()) {
          ArmorStand armorStand = (ArmorStand) playerInteractAtEntityEvent.getRightClicked();
          if (armorStand.hasArms()) {
            if (PermissionsManager.CMIPerm.armorstand_offhand.hasPermission(player)) {
              CMIUser cmiUser = this.plugin.getPlayerManager().getUser(player);
              if (cmiUser != null) {
                ItemStack itemStack = cmiUser.getItemInHand();
                if (Version.isCurrentHigher(Version.v1_8_R3)) {
                  try {
                    if (playerInteractAtEntityEvent.getClickedPosition() == null) {
                      return;
                    }
                  } catch (Exception | Error value) {
                    return;
                  }

                  LivingEntity livingEntity = (LivingEntity) entity;
                  if (livingEntity.getEquipment().getItemInHand() != null
                      && !livingEntity.getEquipment().getItemInHand().getType().equals(Material.AIR)
                      && itemStack != null
                      && !itemStack.getType().equals(Material.AIR)
                      && (livingEntity.getEquipment().getItemInOffHand() == null
                          || livingEntity
                              .getEquipment()
                              .getItemInOffHand()
                              .getType()
                              .equals(Material.AIR))) {
                    ItemStack singleItem = itemStack.clone();
                    if (singleItem.getAmount() > 1) {
                      singleItem.setAmount(1);
                    }

                    livingEntity.getEquipment().setItemInOffHand(singleItem);
                    ItemStack remainingItem = itemStack.clone();
                    if (remainingItem.getAmount() > 1) {
                      remainingItem.setAmount(itemStack.getAmount() - 1);
                    }

                    CMILib.getInstance()
                        .getReflectionManager()
                        .setItemInMainHand(
                            player, itemStack.getAmount() > 1 ? remainingItem : null);
                    playerInteractAtEntityEvent.setCancelled(true);
                  }

                  if ((livingEntity.getEquipment().getItemInHand() == null
                          || livingEntity
                              .getEquipment()
                              .getItemInHand()
                              .getType()
                              .equals(Material.AIR))
                      && (itemStack == null || itemStack.getType().equals(Material.AIR))
                      && livingEntity.getEquipment().getItemInOffHand() != null
                      && !livingEntity
                          .getEquipment()
                          .getItemInOffHand()
                          .getType()
                          .equals(Material.AIR)) {
                    CMILib.getInstance()
                        .getReflectionManager()
                        .setItemInMainHand(
                            player, livingEntity.getEquipment().getItemInOffHand().clone());
                    livingEntity.getEquipment().setItemInOffHand(null);
                    playerInteractAtEntityEvent.setCancelled(true);
                  }
                }
              }
            }
          }
        }
      }
    }
  }
}
