package com.Zrips.CMI.AllListeners;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.Modules.PlayerOptions.PlayerOption;
import java.util.HashMap;
import java.util.UUID;
import net.Zrips.CMILib.CMILib;
import net.Zrips.CMILib.Container.CMINumber;
import net.Zrips.CMILib.Items.CMIItemStack;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemDamageEvent;

public class PlayerItemDamageListener implements Listener {
  private CMI plugin;
  HashMap<UUID, Long> informed = new HashMap<>();

  public PlayerItemDamageListener(CMI cmi) {
    this.plugin = cmi;
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void itemDurabilityLoss(PlayerItemDamageEvent playerItemDamageEvent) {
    if (!playerItemDamageEvent.isCancelled() && playerItemDamageEvent.getDamage() != 0) {
      if (this.plugin.getConfigManager().isToolDurabilityLossUse()) {
        CMIItemStack cmiItemStack =
            CMILib.getInstance().getItemManager().getItem(playerItemDamageEvent.getItem());
        if (cmiItemStack != null && cmiItemStack.isTool()) {
          int value = cmiItemStack.getDurability() + playerItemDamageEvent.getDamage();
          int maxDurability = cmiItemStack.getMaxDurability();
          if (maxDurability - value > 0) {
            double number = 100.0 - value * 100.0 / maxDurability;
            if (!(number > this.plugin.getConfigManager().getToolDurabilityLossPercentage())) {
              Long lastInformed =
                  this.informed.get(playerItemDamageEvent.getPlayer().getUniqueId());
              if (lastInformed == null || System.currentTimeMillis() - lastInformed > 1000L) {
                this.informed.put(
                    playerItemDamageEvent.getPlayer().getUniqueId(), System.currentTimeMillis());
                CMIUser cmiUser = CMIUser.getUser(playerItemDamageEvent.getPlayer());
                if (cmiUser != null) {
                  if (cmiUser.getOptionState(PlayerOption.InformDurability)
                      && PermissionsManager.CMIPerm.informDurability.hasPermission(
                          playerItemDamageEvent.getPlayer())) {
                    CMILC.info_ItemWillBreak.sendMessage(
                        playerItemDamageEvent.getPlayer(),
                        "[itemName]",
                        cmiItemStack.getDisplayName(),
                        "[current]",
                        CMINumber.clamp(maxDurability - value, 1, maxDurability),
                        "[max]",
                        cmiItemStack.getMaxDurability());
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
  public void armorDurabilityLoss(PlayerItemDamageEvent playerItemDamageEvent) {
    if (!playerItemDamageEvent.isCancelled() && playerItemDamageEvent.getDamage() != 0) {
      if (this.plugin.getConfigManager().isArmorDurabilityLossUse()) {
        CMIItemStack cmiItemStack =
            CMILib.getInstance().getItemManager().getItem(playerItemDamageEvent.getItem());
        if (cmiItemStack != null && cmiItemStack.isArmor()) {
          int value = cmiItemStack.getDurability() + playerItemDamageEvent.getDamage();
          int maxDurability = cmiItemStack.getMaxDurability();
          if (maxDurability - value > 0) {
            double number = 100.0 - value * 100.0 / maxDurability;
            if (!(number > this.plugin.getConfigManager().getArmorDurabilityLossPercentage())) {
              Long lastInformed =
                  this.informed.get(playerItemDamageEvent.getPlayer().getUniqueId());
              if (lastInformed == null || System.currentTimeMillis() - lastInformed > 1000L) {
                this.informed.put(
                    playerItemDamageEvent.getPlayer().getUniqueId(), System.currentTimeMillis());
                CMIUser cmiUser = CMIUser.getUser(playerItemDamageEvent.getPlayer());
                if (cmiUser != null) {
                  if (cmiUser.getOptionState(PlayerOption.InformDurability)
                      && PermissionsManager.CMIPerm.informDurability.hasPermission(
                          playerItemDamageEvent.getPlayer())) {
                    CMILC.info_ArmorWillBreak.sendMessage(
                        playerItemDamageEvent.getPlayer(),
                        "[itemName]",
                        cmiItemStack.getDisplayName(),
                        "[current]",
                        maxDurability - value,
                        "[max]",
                        cmiItemStack.getMaxDurability());
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
