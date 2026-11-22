package com.Zrips.CMI.Modules.ShulkerBoxInventory;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIShulkerBox;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.events.CMIBackpackOpenEvent;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.Zrips.CMILib.CMILib;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Items.CMIMaterial;
import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.NBT.CMINBT;
import net.Zrips.CMILib.Time.CMITimeManager;
import net.Zrips.CMILib.Version.Schedulers.CMITask;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class ShulkerBoxManager {
  public static final String identifier = "CMIBackPack";
  HashMap<UUID, ShulkerInv> map = new HashMap<>();
  private static final ConcurrentHashMap<UUID, Long> lastMove = new ConcurrentHashMap<>();
  private CMI plugin;
  private boolean RequirePermission = true;
  private boolean PreventInCombat = true;
  private double CostToOpen = 0.0;
  CMITask task = null;

  public ShulkerBoxManager(CMI cmi) {
    this.plugin = cmi;
  }

  public static void recordMove(Player player) {
    if (player != null) {
      lastMove.put(player.getUniqueId(), System.currentTimeMillis());
    }
  }

  public long getLastMove(UUID uuid) {
    return uuid == null ? 0L : lastMove.getOrDefault(uuid, 0L);
  }

  public static void removeMove(Player player) {
    if (player != null) {
      lastMove.remove(player.getUniqueId());
    }
  }

  public boolean canOpenShulkerBox(Player player) {
    if (player == null) {
      return false;
    }

    long longValue = this.getLastMove(player.getUniqueId());
    return longValue + 150L < System.currentTimeMillis();
  }

  public boolean stillHaveItem(Player player) {
    ShulkerInv shulkerInv = this.map.get(player.getUniqueId());
    if (shulkerInv == null) {
      return false;
    }

    ItemStack[] itemStacks;
    int value = (itemStacks = player.getInventory().getContents()).length;

    for (int index = 0; index < value; index++) {
      ItemStack itemStack = itemStacks[index];
      if (itemStack != null && CMIMaterial.isShulkerBox(itemStack.getType())) {
        CMINBT cminbt = new CMINBT(itemStack);
        Integer integer = cminbt.getInt("CMIBackPack");
        if (integer != null && integer.equals(shulkerInv.getSourceId())) {
          return true;
        }
      }
    }

    return false;
  }

  public void closeAll() {
    for (var entry : this.map.entrySet()) {
      Player player = CMIUser.getOnlinePlayer((UUID) entry.getKey());
      if (player != null) {
        player.closeInventory();
      }
    }
  }

  public boolean isEditing(Player player) {
    return this.map.isEmpty() ? false : this.map.containsKey(player.getUniqueId());
  }

  private Inventory getExisting(Player player) {
    ShulkerInv shulkerInv = this.map.get(player.getUniqueId());
    return shulkerInv != null ? shulkerInv.getCustomInv() : null;
  }

  public ShulkerInv getShulkerInvRecord(Player player) {
    return this.map.get(player.getUniqueId());
  }

  public void loadConfig() {
    ConfigReader configReader = this.plugin.getConfigManager().getConfig();
    configReader.addComment(
        "ShulkerBoxes.PreventInCombat",
        new String[] {
          "When set to true, players will not have option to open shulker boxes while in combat",
          "Combat timer can be defined under combat section"
        });
    this.PreventInCombat = configReader.get("ShulkerBoxes.PreventInCombat", true);
    configReader.addComment(
        "ShulkerBoxes.RequirePermission",
        new String[] {
          "When set to true player will be required to have "
              + PermissionsManager.CMIPerm.openshulker_edit.getPermission()
              + " permission to edit shulker box from inventory"
        });
    this.RequirePermission = configReader.get("ShulkerBoxes.RequirePermission", false);
    configReader.addComment(
        "ShulkerBoxes.CostToOpen",
        new String[] {
          "Money cost to open shulker box, if player doesn't have enough money then we will not"
              + " allow to open shulker box from inventory",
          "Setting this to 0 will disable money requirement",
          "This can be bypassed with "
              + PermissionsManager.CMIPerm.openshulker_free.getPermission()
              + " permission node"
        });
    this.CostToOpen = configReader.get("ShulkerBoxes.CostToOpen", 0.0);
  }

  public void openShulkerBox(Player player, ItemStack itemStack) {
    if (itemStack != null && itemStack.getAmount() <= 1) {
      Inventory inventory = CMIShulkerBox.getShulkerInv(itemStack);
      if (inventory != null) {
        if (!this.canOpenShulkerBox(player)) {
          CMILC.info_move_CantUseShulkerBox.sendMessage(player);
        } else {
          CMINBT cminbt = new CMINBT(itemStack);
          if (cminbt.hasNBT("CMIBackPack")) {
            Inventory existingInventory = this.getExisting(player);
            if (this.PreventInCombat) {
              CMIUser cmiUser = this.plugin.getPlayerManager().getUser(player);
              if (cmiUser.isInCombatWithPlayer()) {
                CMILC.info_combat_CantUseShulkerBox.sendMessage(
                    player,
                    "[time]",
                    CMITimeManager.to24hourShort(
                        CMI.getInstance().getPlayerCombatManager().getCombatTimer() * 1000L
                            - (System.currentTimeMillis() - cmiUser.getGotLastDamageFromPlayer())));
                return;
              }
            }

            if (existingInventory != null) {
              player.closeInventory();
            }

            existingInventory =
                Bukkit.createInventory(
                    inventory.getHolder(),
                    InventoryType.SHULKER_BOX,
                    CMILib.getInstance().getItemManager().getItem(itemStack).getDisplayName());

            for (int value = 0; value < inventory.getContents().length; value++) {
              existingInventory.setItem(value, inventory.getContents()[value]);
            }

            if (this.plugin.getShulkerBoxManager().isEditing(player)) {
              this.updateShulkerItem(player);
            }

            player.closeInventory();
            ShulkerInv shulkerInv = new ShulkerInv(player, existingInventory);
            shulkerInv.setShulkerBox(itemStack);
            shulkerInv.setSourceId(cminbt.getInt("CMIBackPack"));
            CMIBackpackOpenEvent cmiBackpackOpenEvent =
                new CMIBackpackOpenEvent(shulkerInv, this.CostToOpen);
            Bukkit.getPluginManager().callEvent(cmiBackpackOpenEvent);
            if (!cmiBackpackOpenEvent.isCancelled()) {
              if (cmiBackpackOpenEvent.getCostToOpen() > 0.0
                  && !PermissionsManager.CMIPerm.openshulker_free.hasPermission(player)) {
                CMIUser playerUser = CMIUser.getUser(player);
                if (!playerUser.hasMoney(cmiBackpackOpenEvent.getCostToOpen())) {
                  LC.econ_notEnoughMoney.sendMessage(
                      player, new Object[] {"[amount]", cmiBackpackOpenEvent.getCostToOpen()});
                  return;
                }

                playerUser.withdraw(cmiBackpackOpenEvent.getCostToOpen());
                CMILC.info_combat_CostToOpen.sendMessage(
                    cmiBackpackOpenEvent.getPlayer(),
                    "[amount]",
                    this.plugin.getEconomyManager().format(cmiBackpackOpenEvent.getCostToOpen()));
              }

              this.map.put(
                  cmiBackpackOpenEvent.getPlayer().getUniqueId(),
                  cmiBackpackOpenEvent.getShulkerbackpack());
              cmiBackpackOpenEvent.getPlayer().openInventory(cmiBackpackOpenEvent.getGuiInv());
              if (Version.isCurrentEqualOrHigher(Version.v1_17_R1)) {
                player.updateInventory();
              }
            }
          }
        }
      }
    }
  }

  public void updateShulkerItem(Player player) {
    ShulkerInv shulkerInv = this.map.get(player.getUniqueId());
    if (shulkerInv != null) {
      ItemStack itemStack = null;
      ItemStack[] itemStacks;
      int value = (itemStacks = player.getInventory().getContents()).length;

      for (int index = 0; index < value; index++) {
        ItemStack shulkerBoxItem = itemStacks[index];
        if (shulkerBoxItem != null && CMIMaterial.isShulkerBox(shulkerBoxItem.getType())) {
          CMINBT cminbt = new CMINBT(shulkerBoxItem);
          Integer integer = cminbt.getInt("CMIBackPack");
          if (integer != null && integer.equals(shulkerInv.getSourceId())) {
            itemStack = shulkerBoxItem;
            break;
          }
        }
      }

      if (itemStack != null) {
        Inventory inventory = CMIShulkerBox.getShulkerInv(itemStack);
        if (inventory != null) {
          ItemStack[] storageContents = shulkerInv.getCustomInv().getContents();
          inventory.setStorageContents(storageContents);
          this.setShulkerInv(itemStack, storageContents);
        }
      }
    }
  }

  public void remove(Player player) {
    this.map.remove(player.getUniqueId());
  }

  @Deprecated
  public boolean isShulkerBox(ItemStack itemStack) {
    return CMIShulkerBox.isShulkerBox(itemStack);
  }

  @Deprecated
  public List<ItemStack> getShulkerBoxContents(ItemStack itemStack) {
    return CMIShulkerBox.getShulkerBoxContents(itemStack);
  }

  public void updateEntireShulkerInv(Player player, ItemStack[] itemStacks) {
    ShulkerInv shulkerInv = this.map.get(player.getUniqueId());
    if (shulkerInv != null) {
      ItemStack itemStack = null;
      ItemStack[] playerContents;
      int value = (playerContents = player.getInventory().getContents()).length;

      for (int index = 0; index < value; index++) {
        ItemStack shulkerBoxItem = playerContents[index];
        if (shulkerBoxItem != null && CMIMaterial.isShulkerBox(shulkerBoxItem.getType())) {
          CMINBT cminbt = new CMINBT(shulkerBoxItem);
          Integer integer = cminbt.getInt("CMIBackPack");
          if (integer != null && integer.equals(shulkerInv.getSourceId())) {
            itemStack = shulkerBoxItem;
            break;
          }
        }
      }

      if (itemStack != null) {
        this.setShulkerInv(itemStack, itemStacks);
      }
    }
  }

  public void setShulkerInv(ItemStack itemStack, ItemStack[] itemStacks) {
    CMIShulkerBox.setShulkerInv(itemStack, itemStacks);
  }

  public boolean isRequirePermission() {
    return this.RequirePermission;
  }
}
