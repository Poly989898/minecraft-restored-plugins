package com.Zrips.CMI.Modules.Worth;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.commands.list.sell;
import java.util.ArrayList;
import net.Zrips.CMILib.CMILib;
import net.Zrips.CMILib.GUI.CMIGUICloseEvent;
import net.Zrips.CMILib.GUI.CMIGui;
import net.Zrips.CMILib.GUI.GUIManager.CmiInventoryType;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class WorthListener implements Listener {
  private CMI plugin;

  public WorthListener(CMI cmi) {
    this.plugin = cmi;
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void InventoryClickEvent(InventoryClickEvent inventoryClickEvent) {
    Player player = (Player) inventoryClickEvent.getWhoClicked();
    if (CMILib.getInstance().getGUIManager().isOpenedGui(player)) {
      CMIGui cmiGui = CMILib.getInstance().getGUIManager().getGui(player);
      if (cmiGui.getType().equals(CmiInventoryType.SellHand)
          && inventoryClickEvent.getClickedInventory() != null
          && (inventoryClickEvent.getClickedInventory().getType() != InventoryType.PLAYER
              || inventoryClickEvent.getClick() != ClickType.LEFT)) {
        CMIScheduler.runTaskLater(
            this.plugin,
            () -> {
              CMIGui sellGui = CMILib.getInstance().getGUIManager().getGui(player);
              if (sellGui != null) {
                if (sellGui.getType().equals(CmiInventoryType.SellHand)) {
                  Inventory inventory = sellGui.getInv();
                  ArrayList<ItemStack> arrayList = new ArrayList();
                  ItemStack[] itemStacks;
                  int value = (itemStacks = inventory.getContents()).length;

                  for (int index = 0; index < value; index++) {
                    ItemStack itemStack = itemStacks[index];
                    if (itemStack != null) {
                      arrayList.add(itemStack);
                    }
                  }

                  Double totalPrice = 0.0;

                  for (ItemStack soldStack : arrayList) {
                    WorthItem worthItem = this.plugin.getWorthManager().getWorth(soldStack);
                    if (worthItem != null) {
                      totalPrice = totalPrice + worthItem.getSellPrice() * soldStack.getAmount();
                    }
                  }

                  String string = this.plugin.getEconomyManager().format(totalPrice);
                  if (totalPrice == 0.0) {
                    sellGui.updateTitle(CMILC.getIM(sell.class, "close"));
                  } else {
                    sellGui.updateTitle(CMILC.getIM(sell.class, "sellInfo", "[price]", string));
                  }
                }
              }
            },
            1L);
      }
    }
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void InventoryCloseEvent(CMIGUICloseEvent cmiguiCloseEvent) {
    Player player = cmiguiCloseEvent.getPlayer();
    CMIGui cmiGui = cmiguiCloseEvent.getGui();
    if (cmiGui.getType().equals(CmiInventoryType.SellHand)) {
      Inventory inventory = cmiGui.getInv();
      ArrayList<ItemStack> arrayList = new ArrayList();
      ItemStack[] itemStacks;
      int value = (itemStacks = inventory.getContents()).length;

      for (int index = 0; index < value; index++) {
        ItemStack itemStack = itemStacks[index];
        if (itemStack != null) {
          arrayList.add(itemStack);
        }
      }

      sell.sellingItems(player, player, WorthManager.worthType.gui, arrayList, 0, false, false);
    }
  }
}
