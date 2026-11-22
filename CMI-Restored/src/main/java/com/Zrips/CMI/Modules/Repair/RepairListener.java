package com.Zrips.CMI.Modules.Repair;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.AttachedCommands.CustomNBTManager;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.events.CMIAnvilItemRepairEvent;
import com.Zrips.CMI.events.CMIArmorChangeEvent;
import java.util.UUID;
import net.Zrips.CMILib.NBT.CMINBT;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;

public class RepairListener implements Listener {
  private CMI plugin;

  public RepairListener(CMI cmi) {
    this.plugin = cmi;
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void CMIAnvilItemRepairEvent(CMIAnvilItemRepairEvent cmiAnvilItemRepairEvent) {
    if (!cmiAnvilItemRepairEvent.isCancelled()
        && this.plugin.getRepairManager().isRSProtectNormalRepair()
        && !PermissionsManager.CMIPerm.command_repair_repairshare_bypass.hasPermission(
            cmiAnvilItemRepairEvent.getPlayer())) {
      ItemStack itemStack = cmiAnvilItemRepairEvent.getItemTo();
      itemStack =
          (ItemStack)
              new CMINBT(itemStack)
                  .setString(
                      CustomNBTManager.CustomNBTType.CMIRepairMan.name(),
                      cmiAnvilItemRepairEvent.getPlayer().getUniqueId().toString());
      cmiAnvilItemRepairEvent.setItemTo(itemStack);
    }
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void PlayerItemHeldEvent(PlayerPickupItemEvent playerPickupItemEvent) {
    if (!playerPickupItemEvent.isCancelled()) {
      Player player = playerPickupItemEvent.getPlayer();
      ItemStack itemStack = playerPickupItemEvent.getItem().getItemStack();
      if (itemStack != null) {
        String string =
            new CMINBT(itemStack).getString(CustomNBTManager.CustomNBTType.CMIRepairMan.name());
        if (string != null
            && !PermissionsManager.CMIPerm.command_repair_repairshare_bypass.hasPermission(
                player)) {
          if (this.plugin.getRepairManager().isRSBypassWithPerm()
              && PermissionsManager.CMIPerm.command_repair.hasPermission(player)) {
            itemStack =
                (ItemStack)
                    new CMINBT(itemStack)
                        .setString(
                            CustomNBTManager.CustomNBTType.CMIRepairMan.name(),
                            playerPickupItemEvent.getPlayer().getUniqueId().toString());
            this.plugin.getCustomNBTManager().updateRepairLore(itemStack, null);
          } else if (this.plugin.getRepairManager().isRSAddLore()
              && string.equals(player.getUniqueId().toString())) {
            this.plugin.getCustomNBTManager().updateRepairLore(itemStack, null);
          } else {
            UUID uuid = null;

            try {
              uuid = UUID.fromString(string);
            } catch (Exception exception) {
            }

            if (uuid != null) {
              if (this.plugin.getRepairManager().isRSAddLore()) {
                itemStack = this.plugin.getCustomNBTManager().updateRepairLore(itemStack, uuid);
              }

              int value = this.plugin.getRepairManager().getRSDurability();
              if (value > 0) {
                short shortValue = itemStack.getType().getMaxDurability();
                if (shortValue < value) {
                  value = shortValue;
                }

                itemStack.setDurability((short) (shortValue - value));
              }

              player.updateInventory();
            }
          }
        }
      }
    }
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void PlayerItemHeldEvent(PlayerItemHeldEvent playerItemHeldEvent) {
    if (!playerItemHeldEvent.isCancelled()) {
      int value = playerItemHeldEvent.getNewSlot();
      Player player = playerItemHeldEvent.getPlayer();
      ItemStack itemStack = player.getInventory().getItem(value);
      if (itemStack != null) {
        String string =
            new CMINBT(itemStack).getString(CustomNBTManager.CustomNBTType.CMIRepairMan.name());
        if (string != null
            && !PermissionsManager.CMIPerm.command_repair_repairshare_bypass.hasPermission(
                player)) {
          if (this.plugin.getRepairManager().isRSBypassWithPerm()
              && PermissionsManager.CMIPerm.command_repair.hasPermission(player)) {
            itemStack =
                (ItemStack)
                    new CMINBT(itemStack)
                        .setString(
                            CustomNBTManager.CustomNBTType.CMIRepairMan.name(),
                            playerItemHeldEvent.getPlayer().getUniqueId().toString());
            this.plugin.getCustomNBTManager().updateRepairLore(itemStack, null);
          } else if (this.plugin.getRepairManager().isRSAddLore()
              && string.equals(player.getUniqueId().toString())) {
            this.plugin.getCustomNBTManager().updateRepairLore(itemStack, null);
          } else {
            UUID uuid = null;

            try {
              uuid = UUID.fromString(string);
            } catch (Exception exception) {
            }

            if (uuid != null) {
              if (this.plugin.getRepairManager().isRSAddLore()) {
                itemStack = this.plugin.getCustomNBTManager().updateRepairLore(itemStack, uuid);
              }

              int repairAmount = this.plugin.getRepairManager().getRSDurability();
              if (repairAmount > 0) {
                short shortValue = itemStack.getType().getMaxDurability();
                if (shortValue < repairAmount) {
                  repairAmount = shortValue;
                }

                itemStack.setDurability((short) (shortValue - repairAmount));
              }

              player.updateInventory();
            }
          }
        }
      }
    }
  }

  @EventHandler(priority = EventPriority.NORMAL)
  public void CMIArmorChangeEvent(CMIArmorChangeEvent cmiArmorChangeEvent) {
    if (!cmiArmorChangeEvent.isCancelled()) {
      Player player = cmiArmorChangeEvent.getPlayer();
      ItemStack itemStack = cmiArmorChangeEvent.getNewArmorPiece();
      if (itemStack != null) {
        String string =
            new CMINBT(itemStack).getString(CustomNBTManager.CustomNBTType.CMIRepairMan.name());
        if (string != null
            && !PermissionsManager.CMIPerm.command_repair_repairshare_bypass.hasPermission(
                player)) {
          if (this.plugin.getRepairManager().isRSBypassWithPerm()
              && PermissionsManager.CMIPerm.command_repair.hasPermission(player)) {
            itemStack =
                (ItemStack)
                    new CMINBT(itemStack)
                        .setString(
                            CustomNBTManager.CustomNBTType.CMIRepairMan.name(),
                            cmiArmorChangeEvent.getPlayer().getUniqueId().toString());
            this.plugin.getCustomNBTManager().updateRepairLore(itemStack, null);
          } else if (!string.equals(player.getUniqueId().toString())) {
            if (this.plugin.getRepairManager().isRSCancelEvent()) {
              cmiArmorChangeEvent.setCancelled(true);
            }

            if (this.plugin.getRepairManager().isRSInformWithMessage()) {
              CMILC.info("repair", player, "cantUse");
            }

            int value = this.plugin.getRepairManager().getRSDurability();
            if (value > 0) {
              short shortValue = itemStack.getType().getMaxDurability();
              if (shortValue < value) {
                value = shortValue;
              }

              itemStack.setDurability((short) (shortValue - value));
            }

            UUID uuid = null;

            try {
              uuid = UUID.fromString(string);
            } catch (Exception exception) {
            }

            if (uuid != null) {
              if (this.plugin.getRepairManager().isRSAddLore()) {
                this.plugin.getCustomNBTManager().updateRepairLore(itemStack, uuid);
              }
            }
          }
        }
      }
    }
  }
}
