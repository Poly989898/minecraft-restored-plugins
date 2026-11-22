package com.Zrips.CMI.Modules.Warps;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.commands.list.warp;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import net.Zrips.CMILib.Container.CMILocation;
import net.Zrips.CMILib.Items.CMIMaterial;
import net.Zrips.CMILib.Locale.LC;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class CmiWarp {
  private CMILocation loc;
  private List<CMILocation> locations = null;
  private String name;
  private String displayName = null;
  private ItemStack item;
  private ItemStack offItem;
  private Integer slot;
  private Integer page = 1;
  private Boolean reqPerm = false;
  private Boolean showUnavailable = false;
  private Boolean autoLore = true;
  private boolean randomizeYaw = false;
  private boolean repeatingLocation = false;
  private String group = null;
  private boolean hidden = false;
  private UUID creator = null;
  private List<CMILocation> temp = null;

  public CmiWarp(String string) {
    this.name = string;
  }

  public void reset() {
    this.loc = null;
    this.item = null;
  }

  public CmiWarp(String string, CMILocation cmiLocation) {
    this.name = string;
    this.loc = cmiLocation;
  }

  public CMILocation getLoc() {
    this.loc.recheck();
    return this.loc;
  }

  public void resetQueue() {
    this.temp = null;
  }

  public CMILocation getLocIncRand() {
    CMILocation cmiLocation = this.getLoc().clone();
    if (this.locations != null) {
      if (this.temp == null) {
        this.temp = new ArrayList<>();
      }

      if (this.temp.isEmpty()) {
        this.temp.add(this.getLoc());
        this.temp.addAll(this.locations);
        Collections.shuffle(this.temp);
      }

      if (!this.temp.isEmpty()) {
        if (!this.repeatingLocation) {
          cmiLocation = this.temp.remove(0).clone();
        } else {
          if (this.temp.size() < 1 + this.locations.size()) {
            this.temp.clear();
            this.temp.add(this.getLoc());
            this.temp.addAll(this.locations);
            Collections.shuffle(this.temp);
          }

          Collections.shuffle(this.temp);
          cmiLocation = this.temp.get(0).clone();
        }
      } else {
        cmiLocation = this.getLoc().clone();
      }
    }

    if (cmiLocation == null) {
      cmiLocation = this.getLoc().clone();
    }

    if (this.isRandomizeYaw()) {
      cmiLocation.setYaw(new Random().nextInt(359) - 180);
    }

    return cmiLocation;
  }

  public String getName() {
    return this.name;
  }

  public ItemStack getItem() {
    return this.item;
  }

  public ItemStack getIcon() {
    ItemStack itemStack = null;
    if (this.getItem() == null) {
      CMIMaterial cmiMaterial =
          this.getItem() != null && !this.getItem().getType().equals(Material.AIR)
              ? CMIMaterial.get(this.getItem())
              : null;
      if (cmiMaterial == null) {
        int value = new BigInteger(this.getName().getBytes()).intValue() % 3;
        CMIMaterial iconMaterial = CMIMaterial.WHITE_WOOL;
        switch (value) {
          case 1:
            iconMaterial = CMIMaterial.WHITE_CARPET;
            break;
          case 2:
            iconMaterial = CMIMaterial.WHITE_STAINED_GLASS;
            break;
          case 3:
            iconMaterial = CMIMaterial.WHITE_BANNER;
        }

        cmiMaterial =
            CMIMaterial.getByColorId(
                iconMaterial, new BigInteger(this.getName().getBytes()).intValue() % 15);
      }

      if (cmiMaterial != null) {
        itemStack = cmiMaterial.newItemStack();
      }
    } else {
      itemStack = this.getItem();
    }

    if (itemStack == null || CMIMaterial.isAir(itemStack.getType())) {
      itemStack = CMIMaterial.getRandom(CMIMaterial.BLACK_WOOL).newItemStack();
    }

    itemStack = itemStack.clone();
    if (this.isAutoLore() && this.getLoc() != null && this.getLoc().getWorld() != null) {
      ItemMeta itemMeta = itemStack.getItemMeta();
      if (itemMeta != null) {
        itemMeta.setDisplayName(
            CMILC.getIM(warp.class, "autoLoreName", "[warpName]", this.getShownName()));
        var list =
            CMILC.getIML(
                warp.class,
                "autoLoreFormat",
                "[worldName]",
                this.getLoc().getWorld().getName(),
                "[x]",
                this.getLoc().getBlockX(),
                "[y]",
                this.getLoc().getBlockY(),
                "[z]",
                this.getLoc().getBlockZ(),
                "[creator]",
                CMI.getInstance().getWarpManager().isWarpShowCreator() && this.getCreator() != null
                    ? this.getCreatorName()
                    : "");
        itemMeta.setLore(list);
        itemStack.setItemMeta(itemMeta);
      }
    }

    return itemStack;
  }

  public ItemStack getOffIcon() {
    ItemStack itemStack =
        this.getOffItem() != null && !this.getOffItem().getType().equals(Material.AIR)
            ? this.getOffItem()
            : CMIMaterial.RED_WOOL.newItemStack();
    itemStack = itemStack.clone();
    if (this.isAutoLore()) {
      ItemMeta itemMeta = itemStack.getItemMeta();
      itemMeta.setDisplayName(this.getShownName());
      ArrayList<String> arrayList = new ArrayList();
      arrayList.add(CMILC.getIM(warp.class, "noAccess"));
      itemMeta.setLore(arrayList);
      itemStack.setItemMeta(itemMeta);
    }

    return itemStack;
  }

  public void setItem(ItemStack itemStack) {
    this.item = itemStack;
  }

  public void setLoc(CMILocation cmiLocation) {
    this.loc = cmiLocation;
  }

  public boolean isReqPerm() {
    return this.reqPerm;
  }

  public void setReqPerm(boolean flag) {
    this.reqPerm = flag;
  }

  public Integer getSlot() {
    return this.slot;
  }

  public void setSlot(Integer integer) {
    if (integer != null) {
      if (integer > 27) {
        integer = 27;
      }

      if (integer < 0) {
        integer = null;
      }
    }

    this.slot = integer;
  }

  public UUID getCreator() {
    return this.creator;
  }

  public String getCreatorName() {
    return this.creator.equals(CMI.getInstance().getServerUUID())
        ? LC.info_Console.getLocale(new Object[0])
        : (CMI.getInstance().getPlayerManager().getUser(this.creator) == null
            ? "N/A"
            : CMI.getInstance().getPlayerManager().getUser(this.creator).getName());
  }

  public void setCreator(UUID uuid) {
    this.creator = uuid;
  }

  public Boolean isAutoLore() {
    return this.autoLore;
  }

  public void setAutoLore(Boolean autoLoreEnabled) {
    this.autoLore = autoLoreEnabled;
  }

  public ItemStack getOffItem() {
    return this.offItem;
  }

  public void setOffItem(ItemStack itemStack) {
    this.offItem = itemStack;
  }

  public Boolean isShowUnavailable() {
    return this.showUnavailable;
  }

  public void setShowUnavailable(Boolean unavailableShown) {
    this.showUnavailable = unavailableShown;
  }

  public Integer getPage() {
    return this.page;
  }

  public void setPage(int value) {
    if (value < 1) {
      value = 1;
    }

    this.page = value;
  }

  public void setName(String string) {
    this.name = string;
  }

  public boolean isRandomizeYaw() {
    return this.randomizeYaw;
  }

  public void setRandomizeYaw(boolean flag) {
    this.randomizeYaw = flag;
  }

  public String getGroup() {
    return this.group;
  }

  public void setGroup(String string) {
    this.group = string;
  }

  public boolean sameGroup(String string) {
    return this.group == null && string == null
        || this.group != null && string != null && this.group.equalsIgnoreCase(string);
  }

  public boolean isHidden() {
    return this.hidden;
  }

  public void setHidden(boolean flag) {
    this.hidden = flag;
  }

  public String getShownName() {
    return this.displayName == null ? this.getName() : this.displayName;
  }

  public String getDisplayName() {
    return this.displayName;
  }

  public void setDisplayName(String string) {
    this.displayName = string;
  }

  public boolean isRepeatingLocation() {
    return this.repeatingLocation;
  }

  public void setRepeatingLocation(boolean flag) {
    this.repeatingLocation = flag;
  }

  public boolean hasSecondaryLocations() {
    return this.locations != null && !this.locations.isEmpty();
  }

  public List<CMILocation> getLocations() {
    if (this.locations == null) {
      this.locations = new ArrayList<>();
    }

    return this.locations;
  }

  public void setLocations(List<CMILocation> list) {
    this.locations = list;
  }

  public void addLocation(CMILocation cmiLocation) {
    this.getLocations().add(cmiLocation);
  }
}
