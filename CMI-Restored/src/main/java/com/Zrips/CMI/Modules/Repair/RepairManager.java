package com.Zrips.CMI.Modules.Repair;

import com.Zrips.CMI.CMI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.Zrips.CMILib.Container.CMINumber;
import net.Zrips.CMILib.FileHandler.ConfigReader;

public class RepairManager {
  private boolean repairCostsMoney = false;
  private double baseCost = 0.0;
  private boolean checkDurability = true;
  private double worthPercentage = 10.0;
  private double enchantWorthPercentage = 10.0;
  private boolean RSProtectNormalRepair = false;
  private boolean RSProtectCommandRepair = false;
  private boolean RSBypassWithPerm = true;
  private boolean RSAddLore = true;
  private boolean RSCancelEvent = true;
  private boolean RSInformWithMessage = true;
  private int RSDurability = 1;
  private boolean RepairConfirmation = true;
  private List<Integer> BlockedCustomDataID = new ArrayList<>();
  private CMI plugin;

  public RepairManager(CMI cmi) {
    this.plugin = cmi;
  }

  public void loadConfig() {
    ConfigReader configReader = this.plugin.getConfigManager().getConfig();
    configReader.addComment(
        "ItemRepair.RepairShare.ProtectNormalRepair",
        new String[] {
          "When neabling you will need to perform full server restart for it to take effect",
          "When enabled will prevent players repairing items for others in anvil regular way. They"
              + " still can use items and repairs normaly for them selfs",
          "Can be bypassed with cmi.command.repair.repairshare.bypass"
        });
    this.RSProtectNormalRepair =
        configReader.get("ItemRepair.RepairShare.ProtectNormalRepair", false);
    configReader.addComment(
        "ItemRepair.RepairShare.ProtectCommandRepair",
        new String[] {
          "When enabled will prevent players repairing items for others with CMI command. They"
              + " still can use items and repairs normaly for them selfs"
        });
    this.RSProtectCommandRepair =
        configReader.get("ItemRepair.RepairShare.ProtectCommandRepair", false);
    configReader.addComment(
        "ItemRepair.RepairShare.Durability",
        new String[] {
          "Sets durability on item when another picks it up or selects in inventory. Set to 0 or"
              + " less if you don't want to change durability",
          "Attention! Keep this number above 0, otherwise item will get removed without option for"
              + " the player to repair it throw some means, unless this is what you actually want"
        });
    this.RSDurability = configReader.get("ItemRepair.RepairShare.Durability", 1);
    configReader.addComment(
        "ItemRepair.RepairShare.BypassWithRepairPermission",
        new String[] {
          "When set to true, player who have cmi.command.repair permission will bypass this"
              + " protection and can use other user repaired items without any additional actions"
        });
    this.RSBypassWithPerm =
        configReader.get("ItemRepair.RepairShare.BypassWithRepairPermission", true);
    configReader.addComment(
        "ItemRepair.RepairShare.AddLore",
        new String[] {
          "When enabled additional lore line will be added when player can't use that item. This"
              + " will not be shown for owner of item"
        });
    this.RSAddLore = configReader.get("ItemRepair.RepairShare.AddLore", true);
    configReader.addComment(
        "ItemRepair.RepairShare.CancelEvent",
        new String[] {"When set to true, interact event will be canceled to prevent item usage"});
    this.RSCancelEvent = configReader.get("ItemRepair.RepairShare.CancelEvent", true);
    configReader.addComment(
        "ItemRepair.RepairShare.InformWithMessage",
        new String[] {
          "When set to true, player will get message informing about item usage he dint repaired"
              + " him self"
        });
    this.RSInformWithMessage = configReader.get("ItemRepair.RepairShare.InformWithMessage", true);
    configReader.addComment(
        "ItemRepair.Repair.BlockedCustomDataID",
        new String[] {
          "List of custom model data id's we should not allow to be repaired with /repair command"
        });
    this.BlockedCustomDataID =
        configReader.getIntList("ItemRepair.Repair.BlockedCustomDataID", Arrays.asList(298785423));
    configReader.addComment(
        "ItemRepair.Repair.Confirmation",
        new String[] {
          "When item costs money, player will be required to confirm repair action by clicking"
              + " message in chat"
        });
    this.RepairConfirmation = configReader.get("ItemRepair.Repair.Confirmation", true);
    configReader.addComment(
        "ItemRepair.Repair.CostsMoney",
        new String[] {
          "When set to true, item repair with /cmi repair will cost money depending on setup",
          "Player who performs command will pay repair cost",
          "If command gets performed from console, then player whose items are repaired will be"
              + " paying"
        });
    this.repairCostsMoney = configReader.get("ItemRepair.Repair.CostsMoney", false);
    configReader.addComment(
        "ItemRepair.Repair.BasePrice",
        new String[] {
          "Base price to repair item",
          "If you have enabled durability check, then this value will wary depending on items"
              + " condition"
        });
    this.baseCost = configReader.get("ItemRepair.Repair.BasePrice", 100.0);
    configReader.addComment(
        "ItemRepair.Repair.CheckDurability",
        new String[] {"Item repair cost will depend on how baddly item is damaged"});
    this.checkDurability = configReader.get("ItemRepair.Repair.CheckDurability", true);
    configReader.addComment(
        "ItemRepair.Repair.WorthPercentage",
        new String[] {
          "Adds extra cost to repair depending on items cost set in /cmi setworth",
          "Value should be set from 0 to 100 range"
        });
    this.worthPercentage =
        CMINumber.clamp(configReader.get("ItemRepair.Repair.WorthPercentage", 10.0), 0.0, 100);
    configReader.addComment(
        "ItemRepair.Repair.enchantWorthPercentage",
        new String[] {
          "Adds extra cost to repair depending on items enchantment cost set in /cmi"
              + " setenchantworth",
          "Value should be set from 0 to 100 range",
          "If item has more then one enchantment, then prices will be added up"
        });
    this.enchantWorthPercentage =
        CMINumber.clamp(
            configReader.get("ItemRepair.Repair.enchantWorthPercentage", 10.0), 0.0, 100);
  }

  public double getBaseRepairCost() {
    return this.baseCost;
  }

  public boolean isCheckDurability() {
    return this.checkDurability;
  }

  public double getWorthPercentage() {
    return this.worthPercentage;
  }

  public double getEnchantWorthPercentage() {
    return this.enchantWorthPercentage;
  }

  public boolean isRepairCostsMoney() {
    return this.repairCostsMoney;
  }

  public boolean isRSProtectNormalRepair() {
    return this.RSProtectNormalRepair;
  }

  public boolean isRSProtectCommandRepair() {
    return this.RSProtectCommandRepair;
  }

  public boolean isRSBypassWithPerm() {
    return this.RSBypassWithPerm;
  }

  public boolean isRSAddLore() {
    return this.RSAddLore;
  }

  public boolean isRSCancelEvent() {
    return this.RSCancelEvent;
  }

  public boolean isRSInformWithMessage() {
    return this.RSInformWithMessage;
  }

  public int getRSDurability() {
    return this.RSDurability;
  }

  public boolean isRepairConfirmation() {
    return this.RepairConfirmation;
  }

  public List<Integer> getBlockedCustomDataID() {
    return this.BlockedCustomDataID;
  }
}
