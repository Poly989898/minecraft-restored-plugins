package com.Zrips.CMI.Modules.Elytra;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIPlayerInventory;
import javax.annotation.Nullable;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Items.CMIItemSerializer;
import net.Zrips.CMILib.Items.CMIItemStack;
import net.Zrips.CMILib.Items.CMIMaterial;
import org.bukkit.inventory.ItemStack;

public class ElytraManager {
  private boolean BoostUseItems = false;
  private double BoostMultyplier = 0.2;
  private double BoostSuperMultyplier = 0.6;
  private ItemStack BoostItem = null;
  private ItemStack BoostItemConsume = null;
  private ItemStack LaunchItem = null;
  private int BoostAmount = 1;
  private int BoostSuperAmount = 5;
  private int BoostSpeedLimit = 1;
  private boolean BoostSpeedLimitStop = false;
  private boolean BoostRequiresItem = true;
  private boolean BoostShowParticles = true;
  private boolean PreventSelfDamage = true;
  private boolean PreventRocketUsage = true;
  private boolean BoostSpeedDecimals = true;
  private double LaunchTime = 2.0;
  private boolean disableRiptide = true;
  private CMI plugin;

  public ElytraManager(CMI cmi) {
    this.plugin = cmi;
  }

  public void loadConfig() {
    ConfigReader configReader = this.plugin.getConfigManager().getConfig();
    configReader.addComment(
        "Elytra.Boost",
        new String[] {
          "cmi.elytra - allows usage of elytra",
          "cmi.elytra.boost - allows usage of boost",
          "cmi.elytra.superboost - allows ussage of super boost",
          "cmi.elytra.speedometer - allows to see speedometer"
        });
    configReader.addComment(
        "Elytra.Boost.SpeedLimit", new String[] {"Max speed until player wont get any boost"});
    this.BoostSpeedLimit = configReader.get("Elytra.Boost.SpeedLimit", 200);
    configReader.addComment(
        "Elytra.Boost.SpeedLimitStop",
        new String[] {"When enabled items/exp wont be consumed if player is over speed limit"});
    this.BoostSpeedLimitStop = configReader.get("Elytra.Boost.SpeedLimitStop", false);
    configReader.addComment(
        "Elytra.Boost.SpeedDecimals", new String[] {"Do you want to show decimals in speed"});
    this.BoostSpeedDecimals = configReader.get("Elytra.Boost.SpeedDecimals", true);
    configReader.addComment(
        "Elytra.Boost.GeneralMultyplier", new String[] {"By how much boost player on each use"});
    this.BoostMultyplier = configReader.get("Elytra.Boost.GeneralMultyplier", 0.1);
    configReader.addComment(
        "Elytra.Boost.SuperMultyplier",
        new String[] {
          "By how much boost player on each super boost use", "Use shift while using simple boost"
        });
    this.BoostSuperMultyplier = configReader.get("Elytra.Boost.SuperMultyplier", 0.3);
    configReader.addComment(
        "Elytra.Boost.UseItems", new String[] {"Uses defined items instead of exp"});
    this.BoostUseItems = configReader.get("Elytra.Boost.UseItems", false);
    configReader.addComment(
        "Elytra.Boost.ConsumedItem",
        new String[] {
          "Item material name", "Defines item which will be consumed for each boost event"
        });
    CMIItemStack cmiItemStack =
        CMIItemSerializer.deserialize(
            configReader.get("Elytra.Boost.ConsumedItem", CMIMaterial.STONE.toString()));
    if (cmiItemStack != null && cmiItemStack.getCMIType().isValidAsItemStack()) {
      this.BoostItemConsume = cmiItemStack.getItemStack();
    }

    this.BoostUseItems = configReader.get("Elytra.Boost.UseItems", false);
    configReader.addComment(
        "Elytra.Boost.Item",
        new String[] {
          "Item material name",
          "Set it to AIR if you want to allow boost without holding any particular item in your"
              + " hand",
          "Keep in mind that you still need to hold any item or click left mouse button for it to"
              + " work when its set to AIR"
        });
    cmiItemStack =
        CMIItemSerializer.deserialize(
            configReader.get("Elytra.Boost.Item", CMIMaterial.FEATHER.toString()));
    if (cmiItemStack != null && cmiItemStack.getCMIType().isValidAsItemStack()) {
      this.BoostItem = cmiItemStack.getItemStack();
    }

    configReader.addComment(
        "Elytra.Boost.LaunchItem",
        new String[] {
          "Item material name",
          "Set it to AIR if you want to allow launch to be performed without holding any particular"
              + " item in your hand",
          "Keep in mind that you still need to hold any item or look at a block for it to work"
              + " properly when its set to AIR"
        });
    cmiItemStack =
        CMIItemSerializer.deserialize(
            configReader.get("Elytra.Boost.LaunchItem", CMIMaterial.FEATHER.toString()));
    if (cmiItemStack != null && cmiItemStack.getCMIType().isValidAsItemStack()) {
      this.LaunchItem = cmiItemStack.getItemStack();
    }

    configReader.addComment(
        "Elytra.Boost.RequiresItem",
        new String[] {"Requires to hold defined item in hand. Only when UseItems is set to false"});
    this.BoostRequiresItem = configReader.get("Elytra.Boost.RequiresItem", true);
    configReader.addComment(
        "Elytra.Boost.Amount", new String[] {"Amount of exp consumed on each boost"});
    this.BoostAmount = configReader.get("Elytra.Boost.Amount", 1);
    configReader.addComment(
        "Elytra.Boost.SuperAmount", new String[] {"Amount of exp consumed on each super boost"});
    this.BoostSuperAmount = configReader.get("Elytra.Boost.SuperAmount", 5);
    configReader.addComment(
        "Elytra.Boost.ShowParticles", new String[] {"Shows particles when flying"});
    this.BoostShowParticles = configReader.get("Elytra.Boost.ShowParticles", true);
    this.LaunchTime = configReader.get("Elytra.Launch.Time", 2);
    configReader.addComment(
        "Elytra.DisableRiptide",
        new String[] {
          "1.13+ servers. Do you want to disable riptide enchant usage while flying with elytra and"
              + " trident which has riptide enchant",
          "This combination is dangerous as player can reach extreme speed's if allowed to use it"
        });
    this.disableRiptide = configReader.get("Elytra.DisableRiptide", false);
    configReader.addComment(
        "Elytra.Fix.PreventSelfDamage",
        new String[] {"Disables option to damage yourself while flying with arrows to boost up"});
    this.PreventSelfDamage = configReader.get("Elytra.Fix.PreventSelfDamage", false);
    configReader.addComment(
        "Elytra.Fix.PreventRocketUsage",
        new String[] {"Disables option to use rockets to boost yourself while flying with elytra"});
    this.PreventRocketUsage = configReader.get("Elytra.Fix.PreventRocketUsage", false);
  }

  public boolean isBoostUseItems() {
    return this.BoostUseItems;
  }

  public double getBoostMultyplier() {
    return this.BoostMultyplier;
  }

  public double getBoostSuperMultyplier() {
    return this.BoostSuperMultyplier;
  }

  @Deprecated
  public CMIMaterial getBoostItem() {
    return CMIMaterial.get(this.getBoostItemStack());
  }

  @Deprecated
  public CMIMaterial getBoostItemConsume() {
    return CMIMaterial.get(this.getBoostItemStackConsume());
  }

  @Deprecated
  public CMIMaterial getLaunchItem() {
    return CMIMaterial.get(this.getLaunchItemStack());
  }

  public boolean isBoostItemStack(ItemStack itemStack) {
    return check(itemStack, this.getBoostItemStack());
  }

  @Nullable
  public ItemStack getBoostItemStack() {
    return this.BoostItem;
  }

  public boolean isBoostItemStackConsume(ItemStack itemStack) {
    return check(itemStack, this.getBoostItemStackConsume());
  }

  @Nullable
  public ItemStack getBoostItemStackConsume() {
    return this.BoostItemConsume;
  }

  public boolean isLaunchItemStack(ItemStack itemStack) {
    return check(itemStack, this.getLaunchItemStack());
  }

  private static boolean check(ItemStack itemStack, ItemStack otherItem) {
    if (itemStack == null) {
      return false;
    } else {
      return otherItem == null
          ? false
          : CMIPlayerInventory.sameByMaterialCustomModel(itemStack, otherItem);
    }
  }

  @Nullable
  public ItemStack getLaunchItemStack() {
    return this.LaunchItem;
  }

  public int getBoostAmount() {
    return this.BoostAmount;
  }

  public int getBoostSuperAmount() {
    return this.BoostSuperAmount;
  }

  public int getBoostSpeedLimit() {
    return this.BoostSpeedLimit;
  }

  public boolean isBoostSpeedLimitStop() {
    return this.BoostSpeedLimitStop;
  }

  public boolean isBoostRequiresItem() {
    return this.BoostRequiresItem;
  }

  public boolean isBoostShowParticles() {
    return this.BoostShowParticles;
  }

  public boolean isPreventSelfDamage() {
    return this.PreventSelfDamage;
  }

  public boolean isPreventRocketUsage() {
    return this.PreventRocketUsage;
  }

  public boolean isBoostSpeedDecimals() {
    return this.BoostSpeedDecimals;
  }

  public double getLaunchTime() {
    return this.LaunchTime;
  }

  public boolean isDisableRiptide() {
    return this.disableRiptide;
  }
}
