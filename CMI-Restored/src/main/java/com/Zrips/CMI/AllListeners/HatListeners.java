package com.Zrips.CMI.AllListeners;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.commands.list.hat;
import com.Zrips.CMI.events.CMIArmorChangeEvent;
import net.Zrips.CMILib.Items.ArmorTypes;
import net.Zrips.CMILib.Items.CMIMaterial;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.components.EquippableComponent;

public class HatListeners implements Listener {
  private CMI plugin;

  public HatListeners(CMI cmi) {
    this.plugin = cmi;
  }

  @EventHandler(priority = EventPriority.NORMAL)
  public void armorChangeEvent(CMIArmorChangeEvent cmiArmorChangeEvent) {
    if (cmiArmorChangeEvent.getNewArmorPiece() != null
        && !CMIMaterial.isAir(cmiArmorChangeEvent.getNewArmorPiece().getType())
        && cmiArmorChangeEvent.getType().equals(ArmorTypes.HELMET)) {
      ItemStack itemStack = cmiArmorChangeEvent.getNewArmorPiece();
      Boolean hatAllowed = hat.checkHatItem(cmiArmorChangeEvent.getPlayer(), itemStack);
      if (hatAllowed != null && !hatAllowed) {
        cmiArmorChangeEvent.setCancelled(true);
      }
    }
  }

  @EventHandler(priority = EventPriority.NORMAL)
  public void hatPlacement(InventoryClickEvent inventoryClickEvent) {
    if (!inventoryClickEvent.isCancelled() && inventoryClickEvent.getSlot() == 39) {
      Inventory inventory = inventoryClickEvent.getClickedInventory();
      if (inventory.getType() == InventoryType.PLAYER) {
        Player player = (Player) inventoryClickEvent.getWhoClicked();
        CMIMaterial cmiMaterial = CMIMaterial.get(inventoryClickEvent.getCursor().getType());
        if (!cmiMaterial.isHelmet()) {
          if (Version.isCurrentEqualOrHigher(Version.v1_21_R4)
              && inventoryClickEvent.getCursor().hasItemMeta()
              && inventoryClickEvent.getCursor().getItemMeta().hasEquippable()) {
            EquippableComponent equippableComponent =
                inventoryClickEvent.getCursor().getItemMeta().getEquippable();
            if (equippableComponent.getSlot().equals(EquipmentSlot.HEAD)) {
              return;
            }
          }

          if (!PermissionsManager.CMIPerm.inventoryhat.hasPermission(player)) {
            if (inventoryClickEvent.getCursor() != null
                && !CMIMaterial.isAir(inventoryClickEvent.getCursor().getType())) {
              if (!hat.isHelmet(inventoryClickEvent.getCursor())) {
                inventoryClickEvent.setCancelled(true);
              }
            }
          } else if (inventoryClickEvent.getClick() == ClickType.LEFT) {
            if (!inventoryClickEvent.isShiftClick()) {
              ItemStack itemStack = player.getInventory().getHelmet();
              ItemStack cursorItem = player.getItemOnCursor();
              CMIArmorChangeEvent cmiArmorChangeEvent =
                  new CMIArmorChangeEvent(
                      player,
                      CMIArmorChangeEvent.EquipMethod.SHIFT_CLICK,
                      ArmorTypes.HELMET,
                      itemStack,
                      cursorItem);
              Bukkit.getServer().getPluginManager().callEvent(cmiArmorChangeEvent);
              if (!cmiArmorChangeEvent.isCancelled()) {
                player.getInventory().setHelmet(null);
                player.setItemOnCursor(null);
                CMIScheduler.runTask(
                    this.plugin,
                    () -> {
                      player.getInventory().setHelmet(cursorItem);
                      player.setItemOnCursor(itemStack);
                    });
              }

              inventoryClickEvent.setCancelled(true);
            }
          }
        }
      }
    }
  }
}
