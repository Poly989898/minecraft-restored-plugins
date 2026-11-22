package com.Zrips.CMI.Modules.InvRegEditor;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIPlayerInventory;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Containers.Snd;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.Modules.Teleportations.CMITeleportType;
import com.Zrips.CMI.commands.CommandsHandler;
import com.Zrips.CMI.commands.list.ender;
import com.Zrips.CMI.utils.DateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import net.Zrips.CMILib.CMILib;
import net.Zrips.CMILib.Container.CMINumber;
import net.Zrips.CMILib.Container.CMIText;
import net.Zrips.CMILib.GUI.CMIGui;
import net.Zrips.CMILib.GUI.CMIGuiButton;
import net.Zrips.CMILib.GUI.GUIManager.GUIClickType;
import net.Zrips.CMILib.GUI.GUIManager.GUIRows;
import net.Zrips.CMILib.GUI.GUIManager.InvType;
import net.Zrips.CMILib.Items.CMIItemStack;
import net.Zrips.CMILib.Items.CMIMaterial;
import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.NBT.CMINBT;
import net.Zrips.CMILib.Time.CMITimeManager;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class RegChestManager {
  private HashMap<UUID, CMIInvSee> watcherList = new HashMap<>();
  private HashMap<UUID, CMIInvSee> slaveList = new HashMap<>();
  private static final int HELMET_SLOT = 0;
  private static final int LEGGINGS_SLOT = 1;
  private static final int CHESTPLATE_SLOT = 9;
  private static final int BOOTS_SLOT = 10;
  private static final int OFFHAND_SLOT = 2;
  private static final int CURSOR_SLOT = 11;
  private CMI plugin;

  public RegChestManager(CMI cmi) {
    this.plugin = cmi;
  }

  public boolean isWatching(Player player) {
    return this.watcherList.containsKey(player.getUniqueId());
  }

  public CMIInvSee getInformation(Player player) {
    CMIInvSee cmiInvSee = this.watcherList.get(player.getUniqueId());
    if (cmiInvSee == null) {
      cmiInvSee = this.slaveList.get(player.getUniqueId());
    }

    return cmiInvSee;
  }

  public void remove(Player player) {
    CMIInvSee cmiInvSee = this.watcherList.remove(player.getUniqueId());
    if (cmiInvSee != null) {
      this.removeSlave(cmiInvSee.getTarget());
    } else {
      cmiInvSee = this.slaveList.remove(player.getUniqueId());
      if (cmiInvSee != null) {
        this.removeWatcher(cmiInvSee.getWatcher());
      }
    }
  }

  public CMIInvSee removeWatcher(Player player) {
    return this.watcherList.remove(player.getUniqueId());
  }

  public CMIInvSee removeSlave(Player player) {
    return this.slaveList.remove(player.getUniqueId());
  }

  public boolean isSlave(Player player) {
    return this.slaveList.isEmpty() ? false : this.slaveList.containsKey(player.getUniqueId());
  }

  public boolean openInventory(Player player, Player target) {
    if (target == null) {
      return false;
    }

    CMIInvSee cmiInvSee = this.getInformation(player);
    if (cmiInvSee != null && cmiInvSee.getTask() != null) {
      cmiInvSee.getTask().cancel();
    }

    CMIGui cmiGui = this.createGui(player, target);
    if (cmiGui == null) {
      LC.info_cantDoForPlayer.sendMessage(player, new Object[0]);
      return false;
    }

    if (!PermissionsManager.CMIPerm.invedit.hasPermission(player)
        || PermissionsManager.CMIPerm.command_inv_preventmodify.hasPermission(target)) {
      cmiGui.addLock(InvType.Gui);
    }

    cmiGui.open();
    CMIInvSee invSee = new CMIInvSee(player, target);
    this.watcherList.put(player.getUniqueId(), invSee);
    this.slaveList.put(target.getUniqueId(), invSee);
    ItemStack[] itemStacks = new ItemStack[target.getInventory().getContents().length];

    for (int value = 0; value < target.getInventory().getContents().length; value++) {
      if (target.getInventory().getContents()[value] != null) {
        itemStacks[value] = target.getInventory().getContents()[value].clone();
      }
    }

    invSee.setTargetContents(itemStacks);
    invSee.setTargetArmorContents((ItemStack[]) target.getInventory().getArmorContents().clone());
    invSee.setTask(
        CMIScheduler.scheduleSyncRepeatingTask(
            this.plugin, () -> this.recheckPlayerInventory(target), 1L, 1L));
    return true;
  }

  public void tempLockWatcher(Player player) {
    if (!this.slaveList.isEmpty()) {
      CMIInvSee cmiInvSee = this.slaveList.get(player.getUniqueId());
      if (cmiInvSee != null) {
        cmiInvSee.setTempLocked(true);
      }
    }
  }

  public void recheckPlayerInventory(Player player) {
    if (!this.slaveList.isEmpty()) {
      CMIInvSee cmiInvSee = this.slaveList.get(player.getUniqueId());
      if (cmiInvSee != null) {
        Player watcher = cmiInvSee.getWatcher();
        boolean flag = player.isOnline() && watcher.canSee(player);
        if (flag) {
          if (Version.isCurrentEqualOrHigher(Version.v1_9_R1)) {
            ItemStack itemStack = player.getInventory().getItemInOffHand();
            if (itemStack == null && cmiInvSee.getOffHand() != null
                || itemStack != null && cmiInvSee.getOffHand() == null
                || itemStack != null
                    && cmiInvSee.getOffHand() != null
                    && (!itemStack.equals(cmiInvSee.getOffHand())
                        || itemStack.getAmount() != cmiInvSee.getOffHand().getAmount())) {
              this.updateWatcherInventory(watcher);
              cmiInvSee.setOffHand(itemStack == null ? itemStack : itemStack.clone());
              return;
            }
          }

          ItemStack cursorItem = player.getItemOnCursor();
          if ((cursorItem != null || cmiInvSee.getCursor() == null)
              && (cursorItem == null || cmiInvSee.getCursor() != null)
              && (cursorItem == null
                  || cmiInvSee.getCursor() == null
                  || cursorItem.equals(cmiInvSee.getCursor())
                      && cursorItem.getAmount() == cmiInvSee.getCursor().getAmount())) {
            ItemStack[] itemStacks = player.getInventory().getContents();

            for (int value = 0; value < itemStacks.length; value++) {
              if ((itemStacks[value] != null || cmiInvSee.getContents()[value] != null)
                  && (itemStacks[value] == null && cmiInvSee.getContents()[value] != null
                      || itemStacks[value] != null && cmiInvSee.getContents()[value] == null
                      || itemStacks[value] != null
                          && cmiInvSee.getContents()[value] != null
                          && (!itemStacks[value].equals(cmiInvSee.getContents()[value])
                              || itemStacks[value].getAmount()
                                  != cmiInvSee.getContents()[value].getAmount()))) {
                this.updateWatcherInventory(watcher);
                ItemStack[] newStacks = new ItemStack[itemStacks.length];

                for (int index = 0; index < itemStacks.length; index++) {
                  if (itemStacks[index] != null) {
                    newStacks[index] = itemStacks[index].clone();
                  }
                }

                cmiInvSee.setTargetContents(newStacks);
                return;
              }
            }

            ItemStack[] armorStacks = player.getInventory().getArmorContents();

            for (int armorIndex = 0; armorIndex < armorStacks.length; armorIndex++) {
              if ((armorStacks[armorIndex] != null
                      || cmiInvSee.getArmorContents()[armorIndex] != null)
                  && (armorStacks[armorIndex] == null
                          && cmiInvSee.getArmorContents()[armorIndex] != null
                      || armorStacks[armorIndex] != null
                          && cmiInvSee.getArmorContents()[armorIndex] == null
                      || armorStacks[armorIndex] != null
                          && cmiInvSee.getArmorContents()[armorIndex] != null
                          && (!armorStacks[armorIndex].equals(
                                  cmiInvSee.getArmorContents()[armorIndex])
                              || armorStacks[armorIndex].getAmount()
                                  != cmiInvSee.getArmorContents()[armorIndex].getAmount()))) {
                this.updateWatcherInventory(watcher);
                cmiInvSee.setTargetArmorContents((ItemStack[]) armorStacks.clone());
                return;
              }
            }
          } else {
            this.updateWatcherInventory(watcher);
            cmiInvSee.setCursor(cursorItem == null ? cursorItem : cursorItem.clone());
          }
        }
      }
    }
  }

  private void updateWatcherInventory(Player player) {
    CMIInvSee cmiInvSee = this.getInformation(player);
    cmiInvSee.setTempLocked(true);
    Player target = cmiInvSee.getTarget();
    ItemStack[] itemStacks = target.getInventory().getContents();
    CMIGui cmiGui = CMILib.getInstance().getGUIManager().getGui(player);
    if (cmiGui == null) {
      player.closeInventory();
    } else {
      int value = 0;
      boolean flag =
          PermissionsManager.CMIPerm.invedit.hasPermission(player)
              && !PermissionsManager.CMIPerm.command_inv_preventmodify.hasPermission(target);
      if (!flag) {
        itemStacks = hideItems(itemStacks);
      }

      for (int index = 45; index <= 53; index++) {
        ((CMIGuiButton) cmiGui.getButtons().get(index)).setItem(itemStacks[value]);
        ((CMIGuiButton) cmiGui.getButtons().get(index)).update();
        value++;
      }

      for (int invIndex = 18; invIndex <= 44; invIndex++) {
        ((CMIGuiButton) cmiGui.getButtons().get(invIndex)).setItem(itemStacks[value]);
        ((CMIGuiButton) cmiGui.getButtons().get(invIndex)).update();
        value++;
      }

      ItemStack[] targetArmor = target.getInventory().getArmorContents();
      ((CMIGuiButton) cmiGui.getButtons().get(10))
          .setItem(
              targetArmor[0] == null ? CMIMaterial.CHAINMAIL_BOOTS.newItemStack() : targetArmor[0]);
      ((CMIGuiButton) cmiGui.getButtons().get(10)).update();
      ((CMIGuiButton) cmiGui.getButtons().get(1))
          .setItem(
              targetArmor[1] == null
                  ? CMIMaterial.CHAINMAIL_LEGGINGS.newItemStack()
                  : targetArmor[1]);
      ((CMIGuiButton) cmiGui.getButtons().get(1)).update();
      ((CMIGuiButton) cmiGui.getButtons().get(9))
          .setItem(
              targetArmor[2] == null
                  ? CMIMaterial.CHAINMAIL_CHESTPLATE.newItemStack()
                  : targetArmor[2]);
      ((CMIGuiButton) cmiGui.getButtons().get(9)).update();
      ((CMIGuiButton) cmiGui.getButtons().get(0))
          .setItem(
              targetArmor[3] == null
                  ? CMIMaterial.CHAINMAIL_HELMET.newItemStack()
                  : targetArmor[3]);
      ((CMIGuiButton) cmiGui.getButtons().get(0)).update();
      if (Version.isCurrentEqualOrHigher(Version.v1_9_R1)) {
        ((CMIGuiButton) cmiGui.getButtons().get(2))
            .setItem(target.getInventory().getItemInOffHand());
        ((CMIGuiButton) cmiGui.getButtons().get(2)).update();
      }

      if (target.isOnline()) {
        ((CMIGuiButton) cmiGui.getButtons().get(11)).setItem(target.getItemOnCursor());
        ((CMIGuiButton) cmiGui.getButtons().get(11)).update();
      } else {
        ((CMIGuiButton) cmiGui.getButtons().get(11))
            .setItem(CMILib.getInstance().getConfigManager().getGUIEmptyField().getItemStack());
        ((CMIGuiButton) cmiGui.getButtons().get(11)).update();
      }

      if (target.isOnline()) {
        ItemStack[] targetCrafting = this.getCraftingInventory(target);
        ((CMIGuiButton) cmiGui.getButtons().get(6)).setItem(targetCrafting[1]);
        ((CMIGuiButton) cmiGui.getButtons().get(6)).update();
        ((CMIGuiButton) cmiGui.getButtons().get(7)).setItem(targetCrafting[2]);
        ((CMIGuiButton) cmiGui.getButtons().get(7)).update();
        ((CMIGuiButton) cmiGui.getButtons().get(15)).setItem(targetCrafting[3]);
        ((CMIGuiButton) cmiGui.getButtons().get(15)).update();
        ((CMIGuiButton) cmiGui.getButtons().get(16)).setItem(targetCrafting[4]);
        ((CMIGuiButton) cmiGui.getButtons().get(16)).update();
        ((CMIGuiButton) cmiGui.getButtons().get(8)).setItem(targetCrafting[0]);
        ((CMIGuiButton) cmiGui.getButtons().get(8)).update();
      } else {
        ItemStack itemStack =
            CMILib.getInstance().getConfigManager().getGUIEmptyField().getItemStack();
        ((CMIGuiButton) cmiGui.getButtons().get(6)).setItem(itemStack);
        ((CMIGuiButton) cmiGui.getButtons().get(6)).update();
        ((CMIGuiButton) cmiGui.getButtons().get(7)).setItem(itemStack);
        ((CMIGuiButton) cmiGui.getButtons().get(7)).update();
        ((CMIGuiButton) cmiGui.getButtons().get(15)).setItem(itemStack);
        ((CMIGuiButton) cmiGui.getButtons().get(15)).update();
        ((CMIGuiButton) cmiGui.getButtons().get(16)).setItem(itemStack);
        ((CMIGuiButton) cmiGui.getButtons().get(16)).update();
        ((CMIGuiButton) cmiGui.getButtons().get(8)).setItem(itemStack);
        ((CMIGuiButton) cmiGui.getButtons().get(8)).update();
      }
    }
  }

  private boolean invClick(
      GUIClickType guiClickType,
      RegChestManager.sectionType section,
      UUID uuid,
      int value,
      int slot) {
    Player player = CMIUser.getOnlinePlayer(uuid);
    if (player == null) {
      return false;
    }

    if (!PermissionsManager.CMIPerm.invedit.hasPermission(player, true)) {
      return false;
    }

    CMIGui cmiGui = CMILib.getInstance().getGUIManager().getGui(player);
    if (cmiGui == null) {
      return false;
    }

    CMIInvSee cmiInvSee = this.getInformation(player);
    if (cmiInvSee == null) {
      return false;
    }

    if (cmiInvSee.isTempLocked()) {
      return false;
    }

    Player target = cmiInvSee.getTarget();
    if (PermissionsManager.CMIPerm.command_inv_preventmodify.hasPermission(target)) {
      return false;
    }

    CMIScheduler.runTask(
        this.plugin,
        () -> {
          switch (section) {
            case inventory:
              ItemStack itemStack = CMIPlayerInventory.getTopInventory(player).getItem(value);
              if (!validInventoryClick(player, target, value, itemStack)) {
                return;
              }

              target.getInventory().setItem(slot, itemStack);
              break;
            case armor:
              ItemStack topItem = CMIPlayerInventory.getTopInventory(player).getItem(value);
              CMINBT cminbt = new CMINBT(topItem);
              String string = cminbt.getString("CMIGUIIcon");
              ItemStack[] itemStacks = target.getInventory().getArmorContents();
              if (!validInventoryClick(player, target, value, topItem)) {
                return;
              }

              if (string != null) {
                ItemStack storedItem = itemStacks[slot];
                if (storedItem == null
                    || player.getItemOnCursor() != null
                        && !player.getItemOnCursor().getType().equals(Material.AIR)) {
                  if ((storedItem == null || storedItem.getType().equals(Material.AIR))
                      && player.getItemOnCursor() != null
                      && !player.getItemOnCursor().getType().equals(Material.AIR)) {
                    itemStacks[slot] = player.getItemOnCursor();
                    player.setItemOnCursor(null);
                  }
                } else {
                  itemStacks[slot] = null;
                  player.setItemOnCursor(storedItem);
                }

                target.getInventory().setArmorContents(itemStacks);
              }

              this.updateWatcherInventory(player);
              break;
            case offhand:
              ItemStack slotItem = CMIPlayerInventory.getTopInventory(player).getItem(value);
              if (!validInventoryClick(player, target, value, slotItem)) {
                return;
              }

              if (target.getInventory().getItemInOffHand() == null
                  || target.getInventory().getItemInOffHand().getType().equals(Material.AIR)) {
                target.getInventory().setItemInOffHand(slotItem);
              } else if ((slotItem == null || slotItem.getType().equals(Material.AIR))
                  && (target.getInventory().getItemInOffHand() != null
                      || !target
                          .getInventory()
                          .getItemInOffHand()
                          .getType()
                          .equals(Material.AIR))) {
                target.getInventory().setItemInOffHand(null);
              }
              break;
            case cursor:
              if (target.isOnline()) {
                ItemStack topSlotItem = CMIPlayerInventory.getTopInventory(player).getItem(value);
                if (!validInventoryClick(player, target, value, topSlotItem)) {
                  return;
                }

                if (target.getItemOnCursor() == null
                    || target.getItemOnCursor().getType().equals(Material.AIR)) {
                  target.setItemOnCursor(topSlotItem);
                } else if ((topSlotItem == null || topSlotItem.getType().equals(Material.AIR))
                    && (target.getItemOnCursor() != null
                        || !target.getItemOnCursor().getType().equals(Material.AIR))
                    && target.isOnline()) {
                  target.setItemOnCursor(null);
                }
              }
            case crafting:
          }

          this.plugin.save(target);
          if (target.isOnline()) {
            target.updateInventory();
          }
        });
    return true;
  }

  private void addInventoryButton(
      final CMIGui cmiGui, ItemStack itemStack, int value, final int slotIndex) {
    CMIGuiButton cmiGuiButton =
        new CMIGuiButton(value, itemStack) {
          public void click(GUIClickType guiClickType) {
            RegChestManager.this.invClick(
                guiClickType,
                RegChestManager.sectionType.inventory,
                cmiGui.getPlayer().getUniqueId(),
                this.getSlot(),
                slotIndex);
          }
        };
    cmiGuiButton.unlockField();
    cmiGui.addButton(cmiGuiButton);
  }

  private double cleanNumber(double number) {
    return number * 100.0 / 100.0;
  }

  private static ItemStack[] hideItems(ItemStack[] itemStacks) {
    for (int value = 0; value < itemStacks.length; value++) {
      ItemStack itemStack = itemStacks[value];
      if (itemStack != null) {
        CMIMaterial cmiMaterial = CMIMaterial.get(itemStack);
        if (cmiMaterial.equals(CMIMaterial.WRITTEN_BOOK) || cmiMaterial.isShulkerBox()) {
          CMIItemStack cmiItemStack = new CMIItemStack(itemStack.clone());
          CMIItemStack typeStack = cmiItemStack.getCMIType().newCMIItemStack();
          typeStack.setDisplayName(cmiItemStack.getDisplayName());
          typeStack.setLore(cmiItemStack.getLore());
          itemStacks[value] = typeStack.getItemStack();
        }
      }
    }

    return itemStacks;
  }

  private static boolean areItemStacksEqual(ItemStack itemStack, ItemStack other) {
    if (itemStack == other) {
      return true;
    } else {
      return itemStack != null && other != null ? itemStack.equals(other) : false;
    }
  }

  private static boolean validInventoryClick(
      Player player, Player target, int value, ItemStack itemStack) {
    if (value >= 0 && (value < 3 || value > 8) && (value < 12 || value > 17) && value <= 53) {
      int slotIndex = value >= 45 && value <= 53 ? value - 45 : value - 9;
      ItemStack slotItem = null;
      if (value == 0) {
        slotItem = target.getInventory().getArmorContents()[3];
      } else if (value == 1) {
        slotItem = target.getInventory().getArmorContents()[1];
      } else if (value == 9) {
        slotItem = target.getInventory().getArmorContents()[2];
      } else if (value == 10) {
        slotItem = target.getInventory().getArmorContents()[0];
      } else if (value == 2) {
        slotItem = CMIItemStack.getItemInOffHand(target);
      } else if (value == 11) {
        slotItem = target.getItemOnCursor();
      } else {
        slotItem = target.getInventory().getItem(slotIndex);
      }

      if (value == 0 || value == 1 || value == 9 || value == 10) {
        return true;
      }

      if (!Material.AIR.equals(player.getItemOnCursor().getType())) {
        return true;
      }

      if (itemStack == null) {
        itemStack = player.getItemOnCursor();
      }

      return slotItem != null && (itemStack == null || itemStack.getType().equals(Material.AIR))
          ? areItemStacksEqual(slotItem, itemStack)
          : true;
    } else {
      return true;
    }
  }

  public CMIGui createGui(final Player player, final Player target) {
    if (target != null && target.getInventory() != null) {
      boolean flag =
          PermissionsManager.CMIPerm.invedit.hasPermission(player)
              && !PermissionsManager.CMIPerm.command_inv_preventmodify.hasPermission(target);
      CMIGui cmiGui =
          new CMIGui(player) {
            public boolean click(int value, GUIClickType guiClickType, ItemStack itemStack) {
              CMIInvSee cmiInvSee = RegChestManager.this.getInformation(player);
              if (cmiInvSee == null) {
                return false;
              } else if (cmiInvSee.isTempLocked()) {
                return false;
              } else if (!RegChestManager.validInventoryClick(player, target, value, itemStack)) {
                return false;
              } else {
                return !guiClickType.equals(GUIClickType.LeftShift) ? true : true;
              }
            }

            public void onClose() {
              CMIInvSee invSee = RegChestManager.this.getInformation(player);
              if (invSee != null && invSee.getTask() != null) {
                invSee.getTask().cancel();
              }

              RegChestManager.this.remove(player);
            }
          };
      cmiGui.setTitle(CMILC.info_InventorySave_info.getLocale(new Snd(player, target)));
      cmiGui.setInvSize(GUIRows.r6);
      if (flag) {
        try {
          cmiGui.setAllowClone(true);
        } catch (Exception exception) {
        }
      }

      boolean targetVisible = target.isOnline() && player.canSee(target);
      ItemStack[] itemStacks = target.getInventory().getContents();
      ItemStack[] armorContents = target.getInventory().getArmorContents();
      if (!flag) {
        itemStacks = hideItems(itemStacks);
      }

      Double percent = 0.0;
      final CMIUser cmiUser = CMIUser.getUser(target);
      if (cmiUser.isOnline() && !targetVisible && cmiUser.isVanished()) {
        percent =
            (System.currentTimeMillis() - cmiUser.getVanish().getVanishedFrom() - 50L) / 1000.0;
      }

      CMIGuiButton cmiGuiButton =
          new CMIGuiButton(
              10,
              armorContents[0] == null
                  ? CMIMaterial.CHAINMAIL_BOOTS.newCMIItemStack()
                  : new CMIItemStack(armorContents[0])) {
            public void click(GUIClickType clickType) {
              RegChestManager.this.invClick(
                  clickType,
                  RegChestManager.sectionType.armor,
                  player.getUniqueId(),
                  this.getSlot(),
                  0);
            }
          };
      cmiGui.addButton(cmiGuiButton);
      cmiGuiButton =
          new CMIGuiButton(
              1,
              armorContents[1] == null
                  ? CMIMaterial.CHAINMAIL_LEGGINGS.newCMIItemStack()
                  : new CMIItemStack(armorContents[1])) {
            public void click(GUIClickType clickType) {
              RegChestManager.this.invClick(
                  clickType,
                  RegChestManager.sectionType.armor,
                  player.getUniqueId(),
                  this.getSlot(),
                  1);
            }
          };
      cmiGui.addButton(cmiGuiButton);
      cmiGuiButton =
          new CMIGuiButton(
              9,
              armorContents[2] == null
                  ? CMIMaterial.CHAINMAIL_CHESTPLATE.newCMIItemStack()
                  : new CMIItemStack(armorContents[2])) {
            public void click(GUIClickType clickType) {
              RegChestManager.this.invClick(
                  clickType,
                  RegChestManager.sectionType.armor,
                  player.getUniqueId(),
                  this.getSlot(),
                  2);
            }
          };
      cmiGui.addButton(cmiGuiButton);
      cmiGuiButton =
          new CMIGuiButton(
              0,
              armorContents[3] == null
                  ? CMIMaterial.CHAINMAIL_HELMET.newCMIItemStack()
                  : new CMIItemStack(armorContents[3])) {
            public void click(GUIClickType clickType) {
              RegChestManager.this.invClick(
                  clickType,
                  RegChestManager.sectionType.armor,
                  player.getUniqueId(),
                  this.getSlot(),
                  3);
            }
          };
      cmiGui.addButton(cmiGuiButton);
      int count = 0;

      for (int invSlot = 45; invSlot <= 53; invSlot++) {
        this.addInventoryButton(cmiGui, itemStacks[count], invSlot, count);
        count++;
      }

      for (int bottomSlot = 18; bottomSlot <= 44; bottomSlot++) {
        this.addInventoryButton(cmiGui, itemStacks[count], bottomSlot, count);
        count++;
      }

      ItemStack slotItem =
          CMILib.getInstance().getConfigManager().getGUIEmptyField().getItemStack().clone();
      if (PermissionsManager.CMIPerm.command_inv_location.hasPermission(player)) {
        CMIItemStack cmiItemStack = CMIMaterial.MAP.newCMIItemStack();
        cmiItemStack.setDisplayName(LC.Location_Title.getLocale(new Object[0]));
        final Location location = target.getLocation();
        cmiItemStack.addLore(
            LC.Location_World.getLocale(new Object[] {"[world]", location.getWorld().getName()}));
        cmiItemStack.addLore(
            LC.Location_X.getLocale(new Object[] {"[x]", this.cleanNumber(location.getX())}));
        cmiItemStack.addLore(
            LC.Location_Y.getLocale(new Object[] {"[y]", this.cleanNumber(location.getY())}));
        cmiItemStack.addLore(
            LC.Location_Z.getLocale(new Object[] {"[z]", this.cleanNumber(location.getZ())}));
        cmiItemStack.addLore(
            LC.Location_Pitch.getLocale(
                new Object[] {"[pitch]", this.cleanNumber(location.getPitch())}));
        cmiItemStack.addLore(
            LC.Location_Yaw.getLocale(new Object[] {"[yaw]", this.cleanNumber(location.getYaw())}));
        cmiGuiButton =
            new CMIGuiButton(4, cmiItemStack) {
              public void click(GUIClickType clickType) {
                if (PermissionsManager.CMIPerm.buttonteleport.hasPermission(player, true)) {
                  RegChestManager.this
                      .plugin
                      .getTeleportHandler()
                      .teleportPlayer(player, location, true, CMITeleportType.InvEdit)
                      .thenAccept(
                          cmiTeleportFeedback -> {
                            if (!cmiTeleportFeedback.isSuccess()) {
                              cmiTeleportFeedback.showFeedbackMessage(player, cmiUser.getPlayer());
                            }
                          });
                }
              }
            };
        cmiGui.addButton(cmiGuiButton);
      } else {
        cmiGui.addButton(new CMIGuiButton(4, slotItem.clone()));
      }

      CMIItemStack enderChestIcon = CMIMaterial.ENDER_CHEST.newCMIItemStack();
      enderChestIcon.setDisplayName(CMILC.Ender_Title.getLocale());
      cmiGui.addButton(
          new CMIGuiButton(5, enderChestIcon) {
            public void click(GUIClickType clickType) {
              RegChestManager.this.plugin.performCommand(
                  player,
                  CommandsHandler.getLabel()
                      + " "
                      + ender.class.getSimpleName()
                      + " "
                      + target.getName());
            }
          });
      if (PermissionsManager.CMIPerm.command_inv_information.hasPermission(player)) {
        ArrayList<String> arrayList = new ArrayList();
        if (!target.getActivePotionEffects().isEmpty()) {
          for (PotionEffect potionEffect : target.getActivePotionEffects()) {
            if (!Version.isCurrentEqualOrHigher(Version.v1_9_R1)
                || !cmiUser.isVanished()
                || player.canSee(target) && cmiUser.isOnline()
                || !potionEffect.getType().equals(PotionEffectType.NIGHT_VISION)) {
              arrayList.add(
                  CMILC.Potion_List.getLocale(
                      "[PotionName]",
                      CMIText.firstToUpperCase(potionEffect.getType().getName()),
                      "[PotionAmplifier]",
                      CMINumber.toRoman(
                          potionEffect.getAmplifier() == 0 ? 1 : potionEffect.getAmplifier() + 1),
                      "[LeftDuration]",
                      (int) (potionEffect.getDuration() / 20.0 + percent)));
            }
          }
        } else {
          arrayList.add(CMILC.Potion_NoPotions.getLocale());
        }

        CMIItemStack potionIcon = CMIMaterial.POTION.newCMIItemStack();
        potionIcon.setDisplayName(CMILC.Potion_Effects.getLocale());
        potionIcon.setLore(arrayList);
        cmiGuiButton =
            new CMIGuiButton(3, potionIcon) {
              public void click(GUIClickType clickType) {}

              public void updateLooks() {}
            };
        cmiGuiButton.hideItemFlags();
        cmiGui.addButton(cmiGuiButton);
        CMIItemStack bookIcon = CMIMaterial.BOOK.newCMIItemStack();
        bookIcon.setDisplayName(CMILC.Information_Title.getLocale());
        bookIcon.addLore(
            CMILC.Information_Health.getLocale(
                "[Health]", target.getHealth(), "[maxHealth]", target.getMaxHealth()));
        bookIcon.addLore(CMILC.Information_Hunger.getLocale("[Hunger]", target.getFoodLevel()));
        bookIcon.addLore(
            CMILC.Information_Saturation.getLocale("[Saturation]", target.getSaturation()));
        if (cmiUser != null) {
          bookIcon.addLore(CMILC.Information_Exp.getLocale("[Exp]", cmiUser.getExp()));
        }

        if (target.getGameMode() != null && target.getGameMode().name() != null) {
          bookIcon.addLore(
              CMILC.Information_GameMode.getLocale(
                  "[GameMode]", target.getGameMode().name().toLowerCase()));
        } else {
          bookIcon.addLore(CMILC.Information_GameMode.getLocale("[GameMode]", "Unknown"));
        }

        bookIcon.addLore(CMILC.Information_Flying.getLocale("[Flying]", target.getAllowFlight()));
        cmiGui.addButton(new CMIGuiButton(12, bookIcon));
        CMIItemStack bookIconAlt = CMIMaterial.BOOK.newCMIItemStack();
        bookIconAlt.addLore(CMILC.Information_Uuid.getLocale("[uuid]", target.getUniqueId()));
        bookIconAlt.addLore(
            CMILC.Information_FirstConnection.getLocale(
                "[time]", DateFormat.MiliToDateShort(target.getFirstPlayed())));
        if (cmiUser != null) {
          String string =
              CMITimeManager.to24hourShort(
                  System.currentTimeMillis()
                      - (target.isOnline()
                          ? cmiUser.getLastLogin(true)
                          : cmiUser.getLastLogoff(true)));
          if (target.isOnline() && player.canSee(target)) {
            bookIconAlt.addLore(CMILC.Information_Onlinesince.getLocale("[time]", string));
          } else {
            bookIconAlt.addLore(CMILC.Information_Lastseen.getLocale("[time]", string));
          }

          if (this.plugin.getConfigManager().VaultMoney
              && this.plugin.getEconomyManager().isVaultEnabled()) {
            try {
              bookIconAlt.addLore(
                  CMILC.Information_Money.getLocale("[money]", cmiUser.getFormatedBalance()));
            } catch (Exception exception2) {
            }
          }
        }

        if (this.plugin.getVaultPerm() != null) {
          try {
            bookIconAlt.addLore(
                CMILC.Information_Group.getLocale(
                    "[group]", this.plugin.getVaultPerm().getPrimaryGroup(target)));
          } catch (Exception exception3) {
          }
        }

        bookIconAlt.setDisplayName(CMILC.Information_Title.getLocale());
        cmiGui.addButton(new CMIGuiButton(13, bookIconAlt));
      } else {
        cmiGui.addButton(new CMIGuiButton(12, slotItem.clone()));
        cmiGui.addButton(new CMIGuiButton(13, slotItem.clone()));
      }

      if (Version.isCurrentEqualOrHigher(Version.v1_9_R1)) {
        cmiGuiButton =
            new CMIGuiButton(2, target.getInventory().getItemInOffHand()) {
              public void click(GUIClickType clickType) {
                RegChestManager.this.invClick(
                    clickType, RegChestManager.sectionType.offhand, player.getUniqueId(), 2, -1);
              }
            };
        cmiGuiButton.unlockField();
        cmiGui.addButton(cmiGuiButton);
      } else {
        cmiGui.addButton(new CMIGuiButton(2, slotItem.clone()));
      }

      if (targetVisible && target.isOnline()) {
        cmiGuiButton =
            new CMIGuiButton(11, target.getItemOnCursor()) {
              public void click(GUIClickType clickType) {
                RegChestManager.this.invClick(
                    clickType, RegChestManager.sectionType.cursor, player.getUniqueId(), 11, -1);
              }
            };
        cmiGuiButton.unlockField();
        cmiGui.addButton(cmiGuiButton);
      } else {
        cmiGui.addButton(new CMIGuiButton(11, slotItem.clone()));
      }

      cmiGui.addButton(new CMIGuiButton(14, slotItem.clone()));
      cmiGui.addButton(new CMIGuiButton(17, slotItem.clone()));
      if (targetVisible && target.isOnline()) {
        ItemStack[] craftingItems = this.getCraftingInventory(target);
        cmiGui.addButton(new CMIGuiButton(6, craftingItems[1]));
        cmiGui.addButton(new CMIGuiButton(7, craftingItems[2]));
        cmiGui.addButton(new CMIGuiButton(15, craftingItems[3]));
        cmiGui.addButton(new CMIGuiButton(16, craftingItems[4]));
        cmiGui.addButton(new CMIGuiButton(8, craftingItems[0]));
      } else {
        cmiGui.addButton(new CMIGuiButton(6, slotItem.clone()));
        cmiGui.addButton(new CMIGuiButton(7, slotItem.clone()));
        cmiGui.addButton(new CMIGuiButton(15, slotItem.clone()));
        cmiGui.addButton(new CMIGuiButton(16, slotItem.clone()));
        cmiGui.addButton(new CMIGuiButton(8, slotItem.clone()));
      }

      return cmiGui;
    } else {
      return null;
    }
  }

  private ItemStack[] getCraftingInventory(Player player) {
    ItemStack[] itemStacks = CMIPlayerInventory.getTopInventoryContents(player);
    ItemStack[] craftingCopy = new ItemStack[5];
    if (itemStacks != null && itemStacks.length == 5) {
      for (int value = 0; value <= 4; value++) {
        craftingCopy[value] = itemStacks[value] == null ? null : itemStacks[value].clone();
      }

      return craftingCopy;
    } else {
      return craftingCopy;
    }
  }

  enum sectionType {
    inventory,
    armor,
    offhand,
    cursor,
    crafting;
  }
}
