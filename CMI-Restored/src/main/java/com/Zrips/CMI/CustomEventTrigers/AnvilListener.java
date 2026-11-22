package com.Zrips.CMI.CustomEventTrigers;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.events.CMIAnvilItemRenameEvent;
import com.Zrips.CMI.events.CMIAnvilItemRepairEvent;
import net.Zrips.CMILib.CMILib;
import net.Zrips.CMILib.Items.CMIItemStack;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.Repairable;

public class AnvilListener implements Listener {
  private CMI plugin;

  public AnvilListener(CMI cmi) {
    this.plugin = cmi;
  }

  @EventHandler(priority = EventPriority.NORMAL)
  public void PrepareAnvilEvent(PrepareAnvilEvent prepareAnvilEvent) {
    if (!prepareAnvilEvent.getViewers().isEmpty()) {
      HumanEntity humanEntity = (HumanEntity) prepareAnvilEvent.getViewers().get(0);
      if (humanEntity instanceof Player) {
        Player player = (Player) humanEntity;
        AnvilInventory anvilInventory = prepareAnvilEvent.getInventory();
        if (anvilInventory instanceof AnvilInventory) {
          AnvilInventory preparedInventory = prepareAnvilEvent.getInventory();
          ItemStack itemStack = prepareAnvilEvent.getResult();
          if (itemStack != null) {
            ItemMeta itemMeta = itemStack.getItemMeta();
            if (itemMeta != null) {
              if (itemMeta.hasDisplayName()) {
                itemStack = itemStack.clone();
                String string = itemMeta.getDisplayName();
                ItemStack leftItem = preparedInventory.getItem(0);
                String renameText =
                    leftItem != null
                        ? (leftItem.hasItemMeta() && leftItem.getItemMeta().hasDisplayName()
                            ? leftItem.getItemMeta().getDisplayName()
                            : "")
                        : "";
                CMIItemStack cmiItemStack = CMILib.getInstance().getItemManager().getItem(leftItem);
                String renamedText = null;
                if (leftItem != null
                    && cmiItemStack.isSimilar(itemStack)
                    && !renameText.equals(string)) {
                  renamedText = renameText;
                }

                if (renamedText != null) {
                  CMIAnvilItemRenameEvent cmiAnvilItemRenameEvent =
                      new CMIAnvilItemRenameEvent(player, leftItem, itemStack, 1);
                  Bukkit.getServer().getPluginManager().callEvent(cmiAnvilItemRenameEvent);
                  prepareAnvilEvent.setResult(cmiAnvilItemRenameEvent.getItemTo());
                  if (cmiAnvilItemRenameEvent.isCancelled()) {
                    prepareAnvilEvent.setResult(null);
                  }
                }
              }
            }
          }
        }
      }
    }
  }

  @EventHandler(priority = EventPriority.NORMAL)
  public void onitemRename(InventoryClickEvent inventoryClickEvent) {}

  @EventHandler(priority = EventPriority.NORMAL)
  public void onItemRepair(PrepareAnvilEvent prepareAnvilEvent) {
    if (!prepareAnvilEvent.getViewers().isEmpty()) {
      HumanEntity humanEntity = (HumanEntity) prepareAnvilEvent.getViewers().get(0);
      if (humanEntity instanceof Player) {
        Player player = (Player) humanEntity;
        AnvilInventory anvilInventory = prepareAnvilEvent.getInventory();
        if (anvilInventory instanceof AnvilInventory) {
          AnvilInventory currentInventory = anvilInventory;
          ItemStack itemStack = prepareAnvilEvent.getResult();
          if (itemStack != null) {
            ItemStack leftItem = currentInventory.getItem(0);
            ItemStack rightItem = currentInventory.getItem(1);
            int value = this.plugin.getAnvilManager().getRepairCost(leftItem, rightItem, itemStack);
            if (leftItem != null && rightItem != null) {
              ItemMeta itemMeta = leftItem.getItemMeta();
              if (itemMeta != null) {
                if (itemMeta instanceof Repairable) {
                  if (player.getLevel() >= value) {
                    CMIAnvilItemRepairEvent cmiAnvilItemRepairEvent =
                        new CMIAnvilItemRepairEvent(player, leftItem, itemStack, value);
                    Bukkit.getServer().getPluginManager().callEvent(cmiAnvilItemRepairEvent);
                    if (cmiAnvilItemRepairEvent.isCancelled()) {
                      prepareAnvilEvent.setResult(null);
                      this.plugin.getNMS().updateExpBar(player);
                    } else {
                      prepareAnvilEvent.setResult(cmiAnvilItemRepairEvent.getItemTo());
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

  @EventHandler(priority = EventPriority.NORMAL)
  public void onItemRepair(InventoryClickEvent inventoryClickEvent) {}
}
