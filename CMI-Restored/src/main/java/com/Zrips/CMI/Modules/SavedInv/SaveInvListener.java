package com.Zrips.CMI.Modules.SavedInv;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIPlayerInventory;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.commands.CommandsHandler;
import com.Zrips.CMI.commands.list.back;
import com.Zrips.CMI.commands.list.dback;
import com.Zrips.CMI.utils.CMINPCChecker;
import net.Zrips.CMILib.CMILib;
import net.Zrips.CMILib.GUI.CMIGui;
import net.Zrips.CMILib.GUI.GUIManager.CmiInventoryType;
import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.RawMessages.RawMessage;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;

public class SaveInvListener implements Listener {
  private CMI plugin;

  public SaveInvListener(CMI cmi) {
    this.plugin = cmi;
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onPlayerDeathShowLocation(PlayerDeathEvent playerDeathEvent) {
    Player player = playerDeathEvent.getEntity();
    if (!CMINPCChecker.isNPC(player)
        && PermissionsManager.CMIPerm.deathlocation.hasPermission(player)) {
      Location location = player.getLocation();
      boolean flag =
          PermissionsManager.CMIPerm.command_$1.hasPermission(player, dback.class.getSimpleName());
      boolean useBack = false;
      if (!flag) {
        useBack =
            PermissionsManager.CMIPerm.command_$1.hasPermission(player, back.class.getSimpleName())
                && PermissionsManager.CMIPerm.command_back_ondeath.hasPermission(player);
      }

      RawMessage rawMessage = new RawMessage();
      if (!flag && !useBack) {
        rawMessage.addText(
            CMILC.info_deathlocation.getLocale(
                "[x]",
                location.getBlockX(),
                "[y]",
                location.getBlockY(),
                "[z]",
                location.getBlockZ(),
                "[world]",
                location.getWorld().getName(),
                player));
      } else {
        rawMessage
            .addText(
                CMILC.info_deathlocation.getLocale(
                    "[x]",
                    location.getBlockX(),
                    "[y]",
                    location.getBlockY(),
                    "[z]",
                    location.getBlockZ(),
                    "[world]",
                    location.getWorld().getName(),
                    player))
            .addHover(LC.info_clickToTeleport.getLocale(new Object[0]))
            .addCommand(
                CommandsHandler.getLabel()
                    + " "
                    + (!flag && useBack
                        ? back.class.getSimpleName()
                        : dback.class.getSimpleName()));
      }

      rawMessage.show(player);
    }
  }

  @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
  public void onPlayerDeath(PlayerDeathEvent playerDeathEvent) {
    Player player = playerDeathEvent.getEntity();
    if (!CMINPCChecker.isNPC(player)
        && this.plugin.getSavedInventoryManager().isSaveOnDeath()
        && (!this.plugin.getSavedInventoryManager().isSaveOnDeathRequiresPermission()
            || PermissionsManager.CMIPerm.saveinv.hasPermission(player))) {
      EntityDamageEvent entityDamageEvent = playerDeathEvent.getEntity().getLastDamageCause();
      Entity entity = null;
      if (entityDamageEvent instanceof EntityDamageByEntityEvent) {
        EntityDamageByEntityEvent entityDamageByEntityEvent =
            (EntityDamageByEntityEvent) entityDamageEvent;
        entity = entityDamageByEntityEvent.getDamager();
      }

      DamageCause damageCause = null;
      if (entityDamageEvent != null) {
        damageCause = entityDamageEvent.getCause();
      }

      this.plugin.getSavedInventoryManager().saveInv(player, entity, damageCause);
    }
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onInventoryClick(InventoryClickEvent inventoryClickEvent) {
    Player player = (Player) inventoryClickEvent.getWhoClicked();
    if (CMILib.getInstance().getGUIManager().isOpenedGui(player)) {
      CMIGui cmiGui = CMILib.getInstance().getGUIManager().getGui(player);
      if (cmiGui.getType() != null
          && cmiGui.getType() == CmiInventoryType.EditableInv
          && cmiGui.getWhatShows() instanceof CMIInventory) {
        Inventory inventory = CMIPlayerInventory.getTopInventory(player);
        CMIInventory cmiInventory = (CMIInventory) cmiGui.getWhatShows();
        this.plugin.getSavedInventoryManager().updateCMIInventoryItems(inventory, cmiInventory);
      }
    }
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onInventoryDrag(InventoryDragEvent inventoryDragEvent) {
    Player player = (Player) inventoryDragEvent.getWhoClicked();
    if (CMILib.getInstance().getGUIManager().isOpenedGui(player)) {
      CMIGui cmiGui = CMILib.getInstance().getGUIManager().getGui(player);
      if (cmiGui.getType() != null
          && cmiGui.getType() == CmiInventoryType.EditableInv
          && cmiGui.getWhatShows() instanceof CMIInventory) {
        Inventory inventory = CMIPlayerInventory.getTopInventory(player);
        CMIInventory cmiInventory = (CMIInventory) cmiGui.getWhatShows();
        this.plugin.getSavedInventoryManager().updateCMIInventoryItems(inventory, cmiInventory);
      }
    }
  }

  @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
  public void onInventoryOpenEvent(InventoryOpenEvent inventoryOpenEvent) {}

  @EventHandler(priority = EventPriority.NORMAL)
  public void onNormalInventoryClose(InventoryCloseEvent inventoryCloseEvent) {
    Player player = (Player) inventoryCloseEvent.getPlayer();
    if (CMILib.getInstance().getGUIManager().isOpenedGui(player)) {
      CMIGui cmiGui = CMILib.getInstance().getGUIManager().getGui(player);
      if (cmiGui.getType() != null && cmiGui.getWhatShows() instanceof CMIInventory) {
        CMIInventory cmiInventory = (CMIInventory) cmiGui.getWhatShows();
        this.plugin.getSavedInventoryManager().saveAllInventories(cmiInventory.getUuid());
      }
    }
  }
}
