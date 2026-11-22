package com.Zrips.CMI.Modules.Kits;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIPlayerInventory;
import com.Zrips.CMI.Containers.CMIUser;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import net.Zrips.CMILib.Colors.CMIChatColor;
import net.Zrips.CMILib.Items.CMIMaterial;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;

public class Kit {
  private List<ItemStack> item = new ArrayList<>();
  private List<String> commands = new ArrayList<>();
  private List<String> conditions = new ArrayList<>();
  private List<String> Description = new ArrayList<>();
  private HashMap<CMIPlayerInventory.CMIInventorySlot, ItemStack> extraItems = new HashMap<>();
  private ItemStack Icon = null;
  private ItemStack IconOff = null;
  private double cost = 0.0;
  private int expCost = 0;
  private Long delay = -1L;
  private String name = null;
  private String displayName = null;
  private String group = null;
  private String fileName = "Kits";
  private boolean enabled = true;
  private int weight = 0;
  private String cmdName = null;
  private Integer slot = null;
  private Integer page = null;
  private int maxUsages = -1;
  private boolean showDespiteUsage = false;
  private boolean showDespiteWeight = false;
  private boolean dropItems = true;

  public Kit(String string) {
    this.name = CMIChatColor.stripColor(string);
    this.reset();
  }

  public void reset() {
    this.item = new ArrayList<>();

    for (int value = 0; value < 36; value++) {
      this.item.add(null);
    }

    this.commands = new ArrayList<>();
    this.conditions = new ArrayList<>();
    this.Description = new ArrayList<>();
    this.cost = 0.0;
    this.expCost = 0;
    this.delay = -1L;
    this.group = null;
  }

  public ItemStack getFirstNotNullItem() {
    for (ItemStack itemStack : this.item) {
      this.checkBook(itemStack);
      if (itemStack != null && itemStack.getType() != Material.AIR) {
        return itemStack;
      }
    }

    return null;
  }

  public List<ItemStack> getItems() {
    for (ItemStack itemStack : this.item) {
      this.checkBook(itemStack);
    }

    return this.item;
  }

  public List<ItemStack> getItems(Player player) {
    ArrayList<ItemStack> arrayList = new ArrayList();

    for (ItemStack itemStack : this.item) {
      if (itemStack != null) {
        arrayList.add(itemStack.clone());
      } else {
        arrayList.add(null);
      }
    }

    for (ItemStack kitItem : arrayList) {
      if (kitItem != null && !kitItem.getType().equals(Material.AIR)) {
        CMI.getInstance().getKitsManager().updateItemStackLore(kitItem, player, this);
      }
    }

    for (ItemStack bookItem : arrayList) {
      this.checkBook(bookItem);
    }

    return arrayList;
  }

  public boolean enoughFreeSpace(Player player) {
    CMIUser cmiUser = CMIUser.getUser(player);
    CMIPlayerInventory cmiPlayerInventory = cmiUser.getInventory();
    int value = cmiPlayerInventory.emptySlotsRemainingIfAdded(this.getItems(player));
    if (value < 0) {
      return false;
    }

    CMIPlayerInventory.CMIInventorySlot[] cmiInventorySlots;
    int slotCount = (cmiInventorySlots = CMIPlayerInventory.CMIInventorySlot.values()).length;
    int slotIndex = 0;

    while (slotIndex < slotCount) {
      CMIPlayerInventory.CMIInventorySlot cmiInventorySlot = cmiInventorySlots[slotIndex];
      switch (cmiInventorySlot) {
        case Armor:
        case Helmet:
        case ChestPlate:
        case Pants:
        case Boots:
        case OffHand:
          ItemStack itemStack = cmiPlayerInventory.getItem(cmiInventorySlot);
          if (itemStack != null && itemStack.getType() != Material.AIR) {
            ItemStack slotItem = this.getExtraItem(cmiInventorySlot);
            if (slotItem != null && slotItem.getType() != Material.AIR) {
              if (--value < 0) {
                return false;
              }
            }
          }
        default:
          slotIndex++;
      }
    }

    return value >= 0;
  }

  private ItemStack checkBook(ItemStack itemStack) {
    return itemStack != null
            && itemStack.getItemMeta() != null
            && itemStack.getItemMeta() instanceof BookMeta
        ? itemStack
        : itemStack;
  }

  public void setItem(List<ItemStack> list) {
    this.item.clear();

    for (ItemStack itemStack : list) {
      this.checkBook(itemStack);
      this.item.add(itemStack == null ? null : itemStack.clone());
    }
  }

  public void setItem(int value, ItemStack itemStack) {
    if (itemStack != null && !itemStack.getType().equals(Material.AIR)) {
      this.checkBook(itemStack);
      this.item.set(value, itemStack);
    } else {
      this.item.set(value, null);
    }
  }

  public void addItem(ItemStack itemStack) {
    this.checkBook(itemStack);
    if (itemStack != null && !itemStack.getType().equals(Material.AIR)) {
      this.item.add(itemStack);
    } else {
      this.item.add(null);
    }
  }

  public List<String> getCommands(Player player) {
    ArrayList<String> arrayList = new ArrayList();

    for (String string : this.commands) {
      arrayList.add(CMI.getInstance().getKitsManager().processText(string, player, this));
    }

    return arrayList;
  }

  public List<String> getCommands() {
    return this.commands;
  }

  public void setCommands(List<String> list) {
    this.commands = list;
  }

  public double getCost() {
    return this.cost;
  }

  public void setCost(double number) {
    this.cost = number;
  }

  public int getExpCost() {
    return this.expCost;
  }

  public void setExpCost(int value) {
    this.expCost = value;
  }

  public long getDelay() {
    return this.delay;
  }

  public void setDelay(long longValue) {
    this.delay = longValue;
  }

  public List<String> getConditions() {
    return this.conditions;
  }

  public void setConditions(List<String> list) {
    this.conditions = list;
  }

  public String getConfigName() {
    return this.name;
  }

  public void setName(String string) {
    this.name = CMIChatColor.stripColor(string);
  }

  public List<String> getDescription(Player player) {
    ArrayList<String> arrayList = new ArrayList();

    for (String string : this.Description) {
      arrayList.add(CMI.getInstance().getKitsManager().processText(string, player, this));
    }

    return arrayList;
  }

  public List<String> getDescription() {
    return this.Description;
  }

  public void setDescription(List<String> list) {
    this.Description = list;
  }

  public String getGroup() {
    return this.group;
  }

  public void setGroup(String string) {
    if (!string.equalsIgnoreCase("na") && !string.equalsIgnoreCase("none")) {
      this.group = string;
    } else {
      this.group = null;
    }
  }

  public boolean isEnabled() {
    return this.enabled;
  }

  public void setEnabled(boolean flag) {
    this.enabled = flag;
  }

  public int getWeight() {
    return this.weight;
  }

  public void setWeight(int value) {
    this.weight = value;
  }

  public String getCommandName() {
    if (this.cmdName == null) {
      this.cmdName = this.getConfigName();
    }

    return this.cmdName;
  }

  public void setCommandName(String string) {
    this.cmdName = string;
  }

  public ItemStack getExtraItem(CMIPlayerInventory.CMIInventorySlot cmiInventorySlot) {
    return this.extraItems.get(cmiInventorySlot);
  }

  public ItemStack getExtraItem(
      Player player, CMIPlayerInventory.CMIInventorySlot cmiInventorySlot) {
    ItemStack itemStack = this.getExtraItem(cmiInventorySlot);
    if (itemStack != null && !itemStack.getType().equals(Material.AIR)) {
      itemStack = itemStack.clone();
      ItemMeta itemMeta = itemStack.getItemMeta();
      if (itemMeta != null) {
        if (itemMeta.hasDisplayName()) {
          itemMeta.setDisplayName(
              CMI.getInstance()
                  .getKitsManager()
                  .processText(itemMeta.getDisplayName(), player, this));
        }

        if (itemMeta.hasLore()) {
          ArrayList<String> arrayList = new ArrayList();

          for (String string : itemMeta.getLore()) {
            arrayList.add(CMI.getInstance().getKitsManager().processText(string, player, this));
          }

          itemMeta.setLore(arrayList);
        }

        itemStack.setItemMeta(itemMeta);
      }

      return itemStack;
    } else {
      return null;
    }
  }

  public void setExtraItem(
      CMIPlayerInventory.CMIInventorySlot cmiInventorySlot, ItemStack itemStack) {
    if (itemStack != null) {
      this.extraItems.put(cmiInventorySlot, itemStack.clone());
    } else {
      this.extraItems.put(cmiInventorySlot, null);
    }
  }

  @Deprecated
  public ItemStack getHelmet() {
    return this.extraItems.get(CMIPlayerInventory.CMIInventorySlot.Helmet);
  }

  @Deprecated
  public void setHelmet(ItemStack itemStack) {
    if (itemStack != null) {
      this.extraItems.put(CMIPlayerInventory.CMIInventorySlot.Helmet, itemStack.clone());
    }
  }

  @Deprecated
  public ItemStack getChest() {
    return this.extraItems.get(CMIPlayerInventory.CMIInventorySlot.ChestPlate);
  }

  @Deprecated
  public void setChest(ItemStack itemStack) {
    if (itemStack != null) {
      this.extraItems.put(CMIPlayerInventory.CMIInventorySlot.ChestPlate, itemStack.clone());
    }
  }

  @Deprecated
  public ItemStack getLegs() {
    return this.extraItems.get(CMIPlayerInventory.CMIInventorySlot.Pants);
  }

  @Deprecated
  public void setLegs(ItemStack itemStack) {
    if (itemStack != null) {
      this.extraItems.put(CMIPlayerInventory.CMIInventorySlot.Pants, itemStack.clone());
    }
  }

  @Deprecated
  public ItemStack getBoots() {
    return this.extraItems.get(CMIPlayerInventory.CMIInventorySlot.Boots);
  }

  @Deprecated
  public void setBoots(ItemStack itemStack) {
    if (itemStack != null) {
      this.extraItems.put(CMIPlayerInventory.CMIInventorySlot.Boots, itemStack.clone());
    }
  }

  @Deprecated
  public ItemStack getOffHand() {
    return this.extraItems.get(CMIPlayerInventory.CMIInventorySlot.OffHand);
  }

  @Deprecated
  public void setOffHand(ItemStack itemStack) {
    if (itemStack != null) {
      this.extraItems.put(CMIPlayerInventory.CMIInventorySlot.OffHand, itemStack.clone());
    }
  }

  public Integer getSlot() {
    return this.slot;
  }

  public void setSlot(Integer integer) {
    if (integer == null) {
      this.slot = integer;
    } else {
      if (integer > 27) {
        this.slot = 27;
      } else if (integer < 0) {
        this.slot = null;
      } else {
        this.slot = integer;
      }
    }
  }

  public ItemStack getSafeIcon() {
    if (this.Icon != null) {
      return this.Icon.clone();
    } else {
      return this.item.isEmpty()
          ? CMIMaterial.WHITE_WOOL.newItemStack()
          : (this.getFirstNotNullItem() != null
              ? this.getFirstNotNullItem()
              : CMIMaterial.WHITE_WOOL.newItemStack());
    }
  }

  public ItemStack getIcon() {
    return this.Icon != null ? this.Icon.clone() : this.Icon;
  }

  public void setIcon(ItemStack itemStack) {
    this.Icon = itemStack;
  }

  public ItemStack getSafeIconOff() {
    return this.IconOff != null ? this.IconOff.clone() : this.getSafeIcon();
  }

  public ItemStack getIconOff() {
    return this.IconOff != null ? this.IconOff.clone() : this.getIcon();
  }

  public void setIconOff(ItemStack itemStack) {
    this.IconOff = itemStack;
  }

  public int getMaxUsages() {
    return this.maxUsages;
  }

  public void setMaxUsages(int value) {
    this.maxUsages = value;
  }

  public boolean isLimitedUse() {
    return this.maxUsages > 0;
  }

  public boolean isShowDespiteWeight() {
    return this.showDespiteWeight;
  }

  public void setShowDespiteWeight(boolean flag) {
    this.showDespiteWeight = flag;
  }

  public boolean isDropItems() {
    return this.dropItems;
  }

  public void setDropItems(boolean flag) {
    this.dropItems = flag;
  }

  public String getDisplayName() {
    return this.displayName == null ? this.getCommandName() : this.displayName;
  }

  public void setDisplayName(String string) {
    this.displayName = string;
  }

  public boolean isShowDespiteUsage() {
    return this.showDespiteUsage;
  }

  public void setShowDespiteUsage(boolean flag) {
    this.showDespiteUsage = flag;
  }

  public Integer getPage() {
    return this.page;
  }

  public void setPage(Integer integer) {
    if (integer == null) {
      this.page = integer;
    } else {
      if (integer < 1) {
        this.page = null;
      } else {
        this.page = integer;
      }
    }
  }

  public String getFileName() {
    return this.fileName;
  }

  public void setFileName(String string) {
    this.fileName = string;
  }
}
