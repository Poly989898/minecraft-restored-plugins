package com.Zrips.CMI.Modules.MoneyCheque;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.AttachedCommands.CustomNBTManager;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.commands.list.cheque;
import com.Zrips.CMI.events.CMIAnvilItemRenameEvent;
import com.Zrips.CMI.events.CMIChequeUsageEvent;
import com.Zrips.CMI.utils.CMIEncoder;
import net.Zrips.CMILib.Items.CMIItemStack;
import net.Zrips.CMILib.Items.CMIMaterial;
import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.NBT.CMINBT;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class MoneyChequeListener implements Listener {
  private CMI plugin;

  public MoneyChequeListener(CMI cmi) {
    this.plugin = cmi;
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void playerInteractOneTime(PlayerInteractEvent playerInteractEvent) {
    try {
      if (Version.isCurrentHigher(Version.v1_8_R3)
          && playerInteractEvent.getHand() != EquipmentSlot.HAND) {
        return;
      }
    } catch (Exception exception) {
    }

    if (playerInteractEvent.getAction() == Action.RIGHT_CLICK_BLOCK
        || playerInteractEvent.getAction() == Action.RIGHT_CLICK_AIR) {
      ItemStack itemStack = CMIItemStack.getItemInMainHand(playerInteractEvent.getPlayer());
      if (itemStack != null && itemStack.getType().equals(Material.PAPER)) {
        Player player = playerInteractEvent.getPlayer();
        CMINBT cminbt = new CMINBT(CMIItemStack.getItemInMainHand(player));
        Long chequeValue = cminbt.getLong(CustomNBTManager.CustomNBTType.MoneyCheque.name());
        if (chequeValue != null && !this.plugin.getEconomyManager().isChequeAcceptNotEncoded()) {
          CMILC.info(cheque.class, player, "clarification");
        } else {
          if (chequeValue == null
              && cminbt.hasNBT(CustomNBTManager.CustomNBTType.CMICheque.name())) {
            try {
              chequeValue =
                  Long.parseLong(
                      CMIEncoder.decode(
                          cminbt.getString(CustomNBTManager.CustomNBTType.CMICheque.name())));
            } catch (Throwable throwable) {
              throwable.printStackTrace();
            }
          }

          if (chequeValue != null) {
            Block block = playerInteractEvent.getClickedBlock();
            CMIMaterial cmiMaterial = CMIMaterial.get(block);
            if (!cmiMaterial.equals(CMIMaterial.CHEST)
                && !cmiMaterial.equals(CMIMaterial.TRAPPED_CHEST)
                && !cmiMaterial.equals(CMIMaterial.ENDER_CHEST)
                && !cmiMaterial.isDoor()
                && !cmiMaterial.isTrapDoor()
                && !cmiMaterial.isGate()
                && !cmiMaterial.isButton()) {
              if (!this.plugin.getEconomyManager().isChequeRequiresPermission()
                  || PermissionsManager.CMIPerm.command_cheque_withdraw.hasPermission(
                      player, true)) {
                if (!this.plugin.getEconomyManager().isChequeInCreative()
                    && player.getGameMode().equals(GameMode.CREATIVE)) {
                  LC.info_cantDoInGamemode.sendMessage(player, new Object[0]);
                } else {
                  CMIUser cmiUser = this.plugin.getPlayerManager().getUser(player);
                  if (cmiUser != null) {
                    if (player.isOnline()) {
                      CMIChequeUsageEvent cmiChequeUsageEvent =
                          new CMIChequeUsageEvent(player, itemStack, chequeValue.longValue());
                      Bukkit.getPluginManager().callEvent(cmiChequeUsageEvent);
                      if (cmiChequeUsageEvent.isCancelled()) {
                        playerInteractEvent.setCancelled(true);
                      } else {
                        itemStack = cmiChequeUsageEvent.getCheque();
                        chequeValue = (long) cmiChequeUsageEvent.getPrice();
                        cmiUser.deposit(chequeValue.doubleValue());
                        CMILC.info(
                            cheque.class,
                            player,
                            "transferred",
                            "[amount]",
                            this.plugin.getEconomyManager().format(chequeValue.doubleValue()),
                            "[balance]",
                            cmiUser.getFormatedBalance());
                        if (itemStack.getAmount() > 1) {
                          itemStack.setAmount(itemStack.getAmount() - 1);
                        } else {
                          itemStack = null;
                        }

                        CMIItemStack.setItemInMainHand(player, itemStack);
                        playerInteractEvent.setCancelled(true);
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
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void playerInteractOneTime(CMIAnvilItemRenameEvent cmiAnvilItemRenameEvent) {
    ItemStack itemStack = cmiAnvilItemRenameEvent.getItemTo();
    if (itemStack != null && itemStack.getType().equals(Material.PAPER)) {
      CMINBT cminbt = new CMINBT(itemStack);
      if (cminbt.hasNBT(CustomNBTManager.CustomNBTType.MoneyCheque.name())
          || cminbt.hasNBT(CustomNBTManager.CustomNBTType.CMICheque.name())) {
        cmiAnvilItemRenameEvent.setCancelled(true);
      }
    }
  }
}
