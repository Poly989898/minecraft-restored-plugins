package com.Zrips.CMI.Containers;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.utils.Util;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.Zrips.CMILib.CMILib;
import net.Zrips.CMILib.Items.CMIItemStack;
import net.Zrips.CMILib.Items.CMIMaterial;
import net.Zrips.CMILib.NBT.CMINBT;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

public class CMIPlayerInventory {
  private CMIUser user;
  private static Method topInventory = null;

  public CMIPlayerInventory(CMIUser cmiUser) {
    this.user = cmiUser;
  }

  public void setItem(CMIPlayerInventory.CMIInventorySlot cmiInventorySlot, ItemStack itemStack) {
    Player player = this.user.getPlayer();
    if (cmiInventorySlot.equals(CMIPlayerInventory.CMIInventorySlot.MainHand)) {
      CMIItemStack.setItemInMainHand(this.user.getPlayer(), itemStack);
    } else {
      for (Integer integer : cmiInventorySlot.getSlots()) {
        player.getInventory().setItem(integer, itemStack);
      }
    }
  }

  public HashMap<Integer, ItemStack> addItem(ItemStack itemStack, int value) {
    return this.addItem(itemStack, CMIPlayerInventory.CMIFullInventoryAction.Drop, value);
  }

  public HashMap<Integer, ItemStack> addItem(ItemStack itemStack) {
    return this.addItem(itemStack, CMIPlayerInventory.CMIFullInventoryAction.Drop, -1);
  }

  public HashMap<Integer, ItemStack> addItem(
      ItemStack itemStack, CMIPlayerInventory.CMIFullInventoryAction cmiFullInventoryAction) {
    return this.addItem(itemStack, cmiFullInventoryAction, -1);
  }

  public HashMap<Integer, ItemStack> addItem(
      ItemStack itemStack,
      CMIPlayerInventory.CMIFullInventoryAction cmiFullInventoryAction,
      int value) {
    Player player = this.user.getPlayer();
    if (value >= 0 && value <= 40) {
      player.updateInventory();
      ItemStack singleStack = itemStack.clone();
      if (itemStack.getAmount() > itemStack.getType().getMaxStackSize()) {
        singleStack.setAmount(itemStack.getType().getMaxStackSize());
        itemStack.setAmount(itemStack.getAmount() - itemStack.getType().getMaxStackSize());
        ItemStack existingItem = player.getInventory().getItem(value);
        if (existingItem != null && existingItem.getType() != Material.AIR) {
          itemStack.setAmount(itemStack.getAmount() + singleStack.getAmount());
        } else {
          player.getInventory().setItem(value, singleStack);
        }
      } else {
        ItemStack targetItem = player.getInventory().getItem(value);
        if (targetItem == null || targetItem.getType() == Material.AIR) {
          player.getInventory().setItem(value, singleStack);
          return new HashMap<>();
        }
      }
    }

    var hashMap = addItem(player.getInventory(), itemStack);
    switch (cmiFullInventoryAction) {
      case Drop:
        if (hashMap != null && this.user.isOnline()) {
          for (Entry entry : hashMap.entrySet()) {
            int fullStacks =
                ((ItemStack) entry.getValue()).getAmount()
                    / ((ItemStack) entry.getValue()).getMaxStackSize();
            if (fullStacks > 1) {
              int remainderAmount =
                  ((ItemStack) entry.getValue()).getAmount()
                      - fullStacks * ((ItemStack) entry.getValue()).getMaxStackSize();

              for (int stackIndex = 0; stackIndex < fullStacks; stackIndex++) {
                ItemStack dropStack = ((ItemStack) entry.getValue()).clone();
                dropStack.setAmount(dropStack.getType().getMaxStackSize());
                if (!CMIMaterial.isAir(dropStack.getType())) {
                  this.user.dropItemNearPlayer(dropStack);
                }
              }

              if (remainderAmount > 0) {
                ItemStack remainderStack = ((ItemStack) entry.getValue()).clone();
                remainderStack.setAmount(remainderAmount);
                if (!CMIMaterial.isAir(remainderStack.getType())) {
                  this.user.dropItemNearPlayer(remainderStack);
                }
              }
            } else if (!CMIMaterial.isAir(((ItemStack) entry.getValue()).getType())) {
              this.user.dropItemNearPlayer((ItemStack) entry.getValue());
            }
          }

          hashMap.clear();
        }
      case Abort:
      case Return:
      default:
        return hashMap;
    }
  }

  private static HashMap<Integer, ItemStack> addItem(Inventory inventory, ItemStack... itemStacks) {
    ArrayList<ItemStack> arrayList = new ArrayList();
    if (itemStacks == null) {
      return new HashMap<>();
    }

    ItemStack[] stacks = itemStacks;
    int value = itemStacks.length;

    for (int stackIndex = 0; stackIndex < value; stackIndex++) {
      ItemStack itemStack = stacks[stackIndex];
      int maxStackSize = itemStack.getMaxStackSize();
      int remaining = itemStack.getAmount();

      while (remaining > 0) {
        int chunkAmount = remaining < maxStackSize ? remaining : maxStackSize;
        ItemStack chunk = itemStack.clone();
        chunk.setAmount(chunkAmount);
        arrayList.add(chunk);
        remaining -= chunkAmount;
      }
    }

    return !arrayList.isEmpty()
        ? inventory.addItem(arrayList.toArray(new ItemStack[arrayList.size()]))
        : new HashMap<>();
  }

  public HashMap<Integer, ItemStack> removeItemByCriteria(
      ItemStack itemStack, CMIPlayerInventory.itemCheckType... itemCheckTypes) {
    return this.removeItemByCriteria(itemStack, false, itemCheckTypes);
  }

  public HashMap<Integer, ItemStack> removeItemByCriteria(
      ItemStack itemStack, boolean flag, CMIPlayerInventory.itemCheckType... itemCheckTypes) {
    HashMap<Integer, ItemStack> hashMap = new HashMap();
    if (itemStack == null) {
      return hashMap;
    }

    int value = itemStack.getAmount();

    do {
      int slot = this.check(itemStack, flag, itemCheckTypes);
      if (slot == -1) {
        itemStack.setAmount(value);
        hashMap.put(0, itemStack);
        break;
      }

      ItemStack slotItem = this.getItem(slot);
      if (slotItem == null) {
        break;
      }

      int slotAmount = slotItem.getAmount();
      if (slotAmount <= value) {
        value -= slotAmount;
        this.clear(slot);
      } else {
        slotItem.setAmount(slotAmount - value);
        this.setItem(slot, slotItem);
        value = 0;
      }
    } while (value > 0);

    return hashMap;
  }

  public HashMap<Integer, ItemStack> removeItem(ItemStack... itemStacks) {
    HashMap<Integer, ItemStack> hashMap = new HashMap();
    if (itemStacks == null) {
      return hashMap;
    }

    for (int value = 0; value < itemStacks.length; value++) {
      ItemStack itemStack = itemStacks[value];
      int remainingAmount = itemStack.getAmount();

      do {
        int slot = this.first(itemStack, false);
        if (slot == -1) {
          itemStack.setAmount(remainingAmount);
          hashMap.put(value, itemStack);
          break;
        }

        ItemStack slotItem = this.getItem(slot);
        int slotAmount = slotItem.getAmount();
        if (slotAmount <= remainingAmount) {
          remainingAmount -= slotAmount;
          this.clear(slot);
        } else {
          slotItem.setAmount(slotAmount - remainingAmount);
          this.setItem(slot, slotItem);
          remainingAmount = 0;
        }
      } while (remainingAmount > 0);
    }

    return hashMap;
  }

  public ItemStack getItem(int value) {
    return this.user.getPlayer().getInventory().getItem(value);
  }

  public void clear(int value) {
    this.user.getPlayer().getInventory().setItem(value, null);
  }

  public void setItem(int value, ItemStack itemStack) {
    this.user.getPlayer().getInventory().setItem(value, itemStack);
  }

  private ItemStack[] getStorageContents() {
    return Version.isCurrentEqualOrLower(Version.v1_9_R1)
        ? this.user.getPlayer().getInventory().getContents()
        : this.user.getPlayer().getInventory().getStorageContents();
  }

  public boolean has(ItemStack itemStack, CMIPlayerInventory.itemCheckType... itemCheckTypes) {
    return this.has(itemStack, false, itemCheckTypes);
  }

  public boolean has(
      ItemStack itemStack, boolean flag, CMIPlayerInventory.itemCheckType... itemCheckTypes) {
    return itemStack == null
        ? true
        : this.getTotalAmount(itemStack, flag, itemCheckTypes) >= itemStack.getAmount();
  }

  public int getTotalAmount(
      ItemStack itemStack, CMIPlayerInventory.itemCheckType... itemCheckTypes) {
    return this.getTotalAmount(itemStack, false, itemCheckTypes);
  }

  public int getTotalAmount(
      ItemStack itemStack, boolean flag, CMIPlayerInventory.itemCheckType... itemCheckTypes) {
    if (itemStack == null) {
      return 0;
    }

    int value = 0;
    itemCheckTypes =
        itemCheckTypes != null && itemCheckTypes.length != 0
            ? itemCheckTypes
            : new CMIPlayerInventory.itemCheckType[] {CMIPlayerInventory.itemCheckType.material};
    ItemStack[] itemStacks = this.getStorageContents();
    ItemStack[] storageCopy = itemStacks;
    int storageSize = itemStacks.length;

    for (int slotIndex = 0; slotIndex < storageSize; slotIndex++) {
      ItemStack storageItem = storageCopy[slotIndex];
      if (storageItem != null
          && (itemStack == storageItem || matches(storageItem, itemStack, flag, itemCheckTypes))) {
        value += storageItem.getAmount();
      }
    }

    ItemStack offHandItem = this.user.getItemInOffHand();
    if (offHandItem != null && matches(offHandItem, itemStack, flag, itemCheckTypes)) {
      value += offHandItem.getAmount();
    }

    return value;
  }

  private int check(
      ItemStack itemStack, boolean flag, CMIPlayerInventory.itemCheckType... itemCheckTypes) {
    if (itemStack == null) {
      return -1;
    }

    itemCheckTypes =
        itemCheckTypes != null && itemCheckTypes.length != 0
            ? itemCheckTypes
            : new CMIPlayerInventory.itemCheckType[] {CMIPlayerInventory.itemCheckType.material};
    ItemStack[] itemStacks = this.getStorageContents();

    for (int value = 0; value < itemStacks.length; value++) {
      ItemStack slotItem = itemStacks[value];
      if (slotItem != null
          && (itemStack == slotItem || matches(slotItem, itemStack, flag, itemCheckTypes))) {
        return value;
      }
    }

    ItemStack offHandItem = this.user.getItemInOffHand();
    return offHandItem != null && matches(offHandItem, itemStack, flag, itemCheckTypes) ? 40 : -1;
  }

  public static boolean matches(
      ItemStack itemStack, ItemStack other, CMIPlayerInventory.itemCheckType... itemCheckTypes) {
    return matches(itemStack, other, false, itemCheckTypes);
  }

  public static boolean sameByMaterialCustomModel(ItemStack itemStack, ItemStack other) {
    return matches(
        itemStack,
        other,
        true,
        CMIPlayerInventory.itemCheckType.material,
        CMIPlayerInventory.itemCheckType.custommodel);
  }

  public static boolean matches(
      ItemStack itemStack,
      ItemStack other,
      boolean flag,
      CMIPlayerInventory.itemCheckType... itemCheckTypes) {
    if (itemStack != null && other != null) {
      CMIPlayerInventory.itemCheckType[] checkTypes = itemCheckTypes;
      int value = itemCheckTypes.length;

      for (int typeIndex = 0; typeIndex < value; typeIndex++) {
        CMIPlayerInventory.itemCheckType checkType = checkTypes[typeIndex];
        switch (checkType) {
          case name:
            ItemMeta itemMeta = other.getItemMeta();
            ItemMeta targetMeta = itemStack.getItemMeta();
            String string =
                itemMeta != null && itemMeta.getDisplayName() != null
                    ? itemMeta.getDisplayName()
                    : "";
            String targetName =
                targetMeta != null && targetMeta.getDisplayName() != null
                    ? targetMeta.getDisplayName()
                    : "";
            if (flag) {
              if (!string.equalsIgnoreCase(targetName)) {
                return false;
              }
            } else if (!string.isEmpty() && !string.equalsIgnoreCase(targetName)) {
              return false;
            }
            break;
          case lore:
            ItemMeta otherLoreMeta = other.getItemMeta();
            ItemMeta targetLoreMeta = itemStack.getItemMeta();
            List list =
                otherLoreMeta != null && otherLoreMeta.getLore() != null
                    ? otherLoreMeta.getLore()
                    : new ArrayList();
            List targetLore =
                targetLoreMeta != null && targetLoreMeta.getLore() != null
                    ? targetLoreMeta.getLore()
                    : new ArrayList();
            if (flag) {
              if (!list.equals(targetLore)) {
                return false;
              }
            } else if (!list.isEmpty() && !list.equals(targetLore)) {
              return false;
            }
            break;
          case amount:
            if (flag) {
              if (itemStack.getAmount() != other.getAmount()) {
                return false;
              }
            } else if (itemStack.getAmount() < other.getAmount()) {
              return false;
            }
            break;
          case nbt:
            if (Version.isCurrentEqualOrHigher(Version.v1_20_R4)) {
              ItemStack otherCopy = other.clone();
              otherCopy.setAmount(1);
              ItemStack targetCopy = itemStack.clone();
              targetCopy.setAmount(1);
              CMINBT cminbt = new CMINBT(otherCopy);
              CMINBT targetNbt = new CMINBT(targetCopy);
              if (cminbt.getNbt() == null && targetNbt.getNbt() == null) {
                return true;
              }

              if ((cminbt.getNbt() == null || targetNbt.getNbt() != null)
                  && (cminbt.getNbt() != null || targetNbt.getNbt() == null)) {
                return cminbt.getNbt().equals(targetNbt.getNbt());
              }

              return false;
            }

            CMINBT otherNbt = new CMINBT(other);
            CMINBT targetNbt = new CMINBT(itemStack);
            if (!otherNbt.getNbt().equals(targetNbt.getNbt())) {
              return false;
            }
            break;
          case material:
            if (itemStack.getType() != other.getType()) {
              return false;
            }
            break;
          case custommodel:
            Integer integer = Util.getCustomModelData(other);
            Integer targetModelData = Util.getCustomModelData(itemStack);
            if (integer != null || targetModelData == null) {
              boolean modelDataMatch =
                  integer == null && targetModelData == null
                      || integer != null
                          && targetModelData != null
                          && integer.equals(targetModelData);
              if (!modelDataMatch) {
                return false;
              }
            }
            break;
          case durability:
            if (flag) {
              int otherDurability = CMIItemStack.getDurability(other);
              int otherMaxDurability = CMIItemStack.getMaxDurability(other);
              if (otherDurability != otherMaxDurability) {
                int targetDurability = CMIItemStack.getDurability(itemStack);
                int targetMaxDurability = CMIItemStack.getMaxDurability(itemStack);
                if (otherDurability > 0 && otherMaxDurability != targetMaxDurability
                    || otherDurability > 0 && targetDurability != otherDurability) {
                  return false;
                }
              }
            }
        }
      }

      return true;
    } else {
      return false;
    }
  }

  private int first(ItemStack itemStack, boolean flag) {
    if (itemStack == null) {
      return -1;
    }

    ItemStack[] itemStacks = this.getStorageContents();

    for (int value = 0; value < itemStacks.length; value++) {
      if (itemStacks[value] != null
          && (flag
              ? itemStack.equals(itemStacks[value])
              : isSimilar(itemStack, itemStacks[value]))) {
        return value;
      }
    }

    return -1;
  }

  private static boolean isSimilar(ItemStack itemStack, ItemStack other) {
    if (itemStack == null) {
      return false;
    }

    if (itemStack == other) {
      return true;
    }

    CMIMaterial cmiMaterial = CMIMaterial.get(itemStack);
    CMIMaterial otherMaterial = CMIMaterial.get(other);
    if (cmiMaterial.isShulkerBox() && otherMaterial.isShulkerBox()) {
      Inventory inventory = CMIShulkerBox.getShulkerInv(itemStack);
      if (inventory != null) {
        ItemStack[] itemStacks;
        int value = (itemStacks = inventory.getContents()).length;

        for (int slot = 0; slot < value; slot++) {
          ItemStack slotItem = itemStacks[slot];
          if (slotItem != null) {
            return false;
          }
        }
      }

      inventory = CMIShulkerBox.getShulkerInv(other);
      if (inventory != null) {
        ItemStack[] invContents;
        int invSize = (invContents = inventory.getContents()).length;

        for (int invIndex = 0; invIndex < invSize; invIndex++) {
          ItemStack invItem = invContents[invIndex];
          if (invItem != null) {
            return false;
          }
        }
      }

      return other.getType().equals(itemStack.getType())
          && other.getDurability() == itemStack.getDurability();
    } else if (cmiMaterial == CMIMaterial.WRITTEN_BOOK
        && otherMaterial == CMIMaterial.WRITTEN_BOOK) {
      BookMeta bookMeta = (BookMeta) itemStack.getItemMeta();
      BookMeta otherBookMeta = (BookMeta) other.getItemMeta();
      boolean flag =
          bookMeta.getPages().isEmpty()
              || bookMeta.getPages().size() == otherBookMeta.getPages().size();
      if (bookMeta.getPages().size() == otherBookMeta.getPages().size()) {
        for (int pageNumber = 1; pageNumber <= bookMeta.getPages().size(); pageNumber++) {
          String string = bookMeta.getPage(pageNumber);
          if (!string.equalsIgnoreCase(otherBookMeta.getPage(pageNumber))) {
            flag = false;
            break;
          }
        }
      }

      if (bookMeta.getAuthor() != null && otherBookMeta.getAuthor() == null
          || bookMeta.getAuthor() != null
              && otherBookMeta.getAuthor() != null
              && !bookMeta.getAuthor().equalsIgnoreCase(otherBookMeta.getAuthor())) {
        flag = false;
      }

      if (bookMeta.getTitle() != null && otherBookMeta.getTitle() == null
          || bookMeta.getTitle() != null
              && otherBookMeta.getTitle() != null
              && !bookMeta.getTitle().equalsIgnoreCase(otherBookMeta.getTitle())) {
        flag = false;
      }

      return flag;
    } else {
      return other.getType().equals(itemStack.getType())
          && other.getDurability() == itemStack.getDurability()
          && other.hasItemMeta() == itemStack.hasItemMeta()
          && (!other.hasItemMeta()
              || Bukkit.getItemFactory().equals(other.getItemMeta(), itemStack.getItemMeta()));
    }
  }

  public boolean canFit(List<ItemStack> list) {
    ArrayList<ItemStack> arrayList = new ArrayList();

    for (ItemStack itemStack : list) {
      if (itemStack != null) {
        arrayList.add(itemStack);
      }
    }

    ItemStack[] itemStacks = new ItemStack[arrayList.size()];
    arrayList.toArray(itemStacks);
    return this.canFit(itemStacks);
  }

  public boolean canFit(ItemStack... itemStacks) {
    Player player = this.user.getPlayer();
    if (player == null) {
      return false;
    }

    Inventory inventory = Bukkit.createInventory(null, 36);

    for (int value = 0; value < 36; value++) {
      ItemStack itemStack = player.getInventory().getItem(value);
      if (itemStack != null) {
        inventory.setItem(value, itemStack.clone());
      }
    }

    return inventory.addItem(itemStacks).isEmpty();
  }

  public int emptySlotsRemainingIfAdded(List<ItemStack> list) {
    ArrayList<ItemStack> arrayList = new ArrayList();

    for (ItemStack itemStack : list) {
      if (itemStack != null) {
        arrayList.add(itemStack);
      }
    }

    ItemStack[] itemStacks = new ItemStack[arrayList.size()];
    arrayList.toArray(itemStacks);
    return this.emptySlotsRemainingIfAdded(itemStacks);
  }

  public int emptySlotsRemainingIfAdded(ItemStack... itemStacks) {
    Player player = this.user.getPlayer();
    if (player == null) {
      return 0;
    }

    Inventory inventory = Bukkit.createInventory(null, 36);

    for (int value = 0; value < 36; value++) {
      ItemStack itemStack = player.getInventory().getItem(value);
      if (itemStack != null) {
        inventory.setItem(value, itemStack.clone());
      }
    }

    var hashMap = inventory.addItem(itemStacks);
    if (!hashMap.isEmpty()) {
      return -inventory.addItem(itemStacks).size();
    }

    int emptyCount = 0;

    for (int slotIndex = 0; slotIndex < 36; slotIndex++) {
      ItemStack slotItem = player.getInventory().getItem(slotIndex);
      if (slotItem == null || slotItem.getType() == Material.AIR) {
        emptyCount++;
      }
    }

    return emptyCount;
  }

  public void removeItemFromMainHand(ItemStack itemStack) {
    Player player = this.user.getPlayer();
    ItemStack handItem = this.user.getItemInHand();
    if (handItem != null
        && !handItem.getType().equals(Material.AIR)
        && handItem.getType().equals(itemStack.getType())) {
      if (handItem.getAmount() > itemStack.getAmount()) {
        handItem.setAmount(handItem.getAmount() - itemStack.getAmount());
      } else if (!this.user.isOnline()) {
        CMILib.getInstance().getReflectionManager().setItemInMainHand(player, null);
      } else {
        this.user.getPlayer().getInventory().setItemInHand(null);
      }

      if (this.user.isOnline()) {
        this.user.updateInventory();
      }

      CMI.getInstance().save(player);
    }
  }

  public ItemStack getItem(CMIPlayerInventory.CMIInventorySlot cmiInventorySlot) {
    if (cmiInventorySlot.equals(CMIPlayerInventory.CMIInventorySlot.MainHand)) {
      return CMILib.getInstance().getReflectionManager().getItemInMainHand(this.user.getPlayer());
    }

    if (cmiInventorySlot.equals(CMIPlayerInventory.CMIInventorySlot.OffHand)
        && Version.isCurrentHigher(Version.v1_8_R3)) {
      return this.user.getPlayer().getInventory().getItemInOffHand();
    }

    var list = this.getItems(cmiInventorySlot);
    return list.isEmpty() ? null : (ItemStack) list.get(0);
  }

  public List<ItemStack> getItems(CMIPlayerInventory.CMIInventorySlot cmiInventorySlot) {
    Player player = this.user.getPlayer();
    ArrayList<ItemStack> arrayList = new ArrayList();
    if (cmiInventorySlot.equals(CMIPlayerInventory.CMIInventorySlot.MainHand)) {
      arrayList.add(CMILib.getInstance().getReflectionManager().getItemInMainHand(player));
      return arrayList;
    }

    if (cmiInventorySlot.equals(CMIPlayerInventory.CMIInventorySlot.OffHand)) {
      if (Version.isCurrentHigher(Version.v1_8_R3)) {
        arrayList.add(this.user.getPlayer().getInventory().getItemInOffHand());
      }

      return arrayList;
    } else {
      if (player == null) {
        return arrayList;
      }

      PlayerInventory playerInventory = player.getInventory();

      for (Integer integer : cmiInventorySlot.getSlots()) {
        ItemStack itemStack = null;

        try {
          itemStack = playerInventory.getItem(integer);
        } catch (Exception exception) {
          continue;
        }

        if (itemStack != null && !itemStack.getType().equals(Material.AIR)) {
          arrayList.add(itemStack);
        }
      }

      return arrayList;
    }
  }

  public int getFreeSlots() {
    return this.getFreeSlots(CMIPlayerInventory.CMIInventorySlot.MainInventory);
  }

  public int getFreeSlots(CMIPlayerInventory.CMIInventorySlot cmiInventorySlot) {
    return cmiInventorySlot.getSlots().size() - this.getItems(cmiInventorySlot).size();
  }

  public boolean contains(ItemStack itemStack) {
    CMIItemStack cmiItemStack = CMILib.getInstance().getItemManager().getItem(itemStack);
    Integer integer = Util.getCustomModelData(itemStack);
    var list = this.getItems(CMIPlayerInventory.CMIInventorySlot.MainInventory);
    list.addAll(this.getItems(CMIPlayerInventory.CMIInventorySlot.OffHand));
    list.addAll(this.getItems(CMIPlayerInventory.CMIInventorySlot.Armor));
    int total = 0;
    Iterator iterator = list.iterator();

    while (true) {
      ItemStack inventoryItem;
      CMIItemStack inventoryStack;
      while (true) {
        if (!iterator.hasNext()) {
          if (total >= cmiItemStack.getAmount()) {
            return true;
          }

          return false;
        }

        inventoryItem = (ItemStack) iterator.next();
        if (inventoryItem != null) {
          inventoryStack = CMILib.getInstance().getItemManager().getItem(inventoryItem);
          if (inventoryStack != null
              && inventoryStack.getType().equals(cmiItemStack.getType())
              && (!Version.isCurrentEqualOrLower(Version.v1_12_R1)
                  || inventoryStack.getData() == cmiItemStack.getData())) {
            try {
              if (!(inventoryItem.getItemMeta() instanceof SkullMeta)
                  || !(itemStack.getItemMeta() instanceof SkullMeta)) {
                break;
              }

              SkullMeta skullMeta = (SkullMeta) inventoryItem.getItemMeta();
              SkullMeta targetSkull = (SkullMeta) itemStack.getItemMeta();
              if ((skullMeta.getOwner() == null || targetSkull.getOwner() != null)
                  && (skullMeta.getOwner() != null || targetSkull.getOwner() == null)
                  && (skullMeta.getOwner() == null
                      || targetSkull.getOwner() == null
                      || skullMeta.getOwner().equals(targetSkull.getOwner()))) {
                break;
              }
            } catch (Throwable throwable) {
              throwable.printStackTrace();
              break;
            }
          }
        }
      }

      Integer targetModelData = Util.getCustomModelData(inventoryItem);
      if ((integer != null || targetModelData == null)
          && (integer == null || targetModelData != null)
          && (integer == null || targetModelData == null || integer.equals(targetModelData))) {
        total += inventoryStack.getAmount();
      }
    }
  }

  @Nonnull
  public static ItemStack[] getTopInventoryContents(Player player) {
    Inventory inventory = getTopInventory(player);
    return inventory == null ? new ItemStack[54] : inventory.getContents();
  }

  @Nullable
  public static Inventory getTopInventory(Player player) {
    if (Version.isCurrentEqualOrHigher(Version.v1_21_R1)) {
      return player.getOpenInventory().getTopInventory();
    }

    try {
      InventoryView inventoryView = player.getOpenInventory();
      if (topInventory == null) {
        topInventory = InventoryView.class.getMethod("getTopInventory");
      }

      return (Inventory) topInventory.invoke(inventoryView);
    } catch (Throwable throwable) {
      throwable.printStackTrace();
      return null;
    }
  }

  @Nullable
  public static Inventory getBottomInventory(Player player) {
    if (Version.isCurrentEqualOrHigher(Version.v1_21_R1)) {
      return player.getOpenInventory().getBottomInventory();
    }

    try {
      InventoryView inventoryView = player.getOpenInventory();
      if (topInventory == null) {
        topInventory = InventoryView.class.getMethod("getBottomInventory");
      }

      return (Inventory) topInventory.invoke(inventoryView);
    } catch (Throwable throwable) {
      throwable.printStackTrace();
      return null;
    }
  }

  public enum CMIFullInventoryAction {
    Abort,
    Drop,
    Return;
  }

  public enum CMIInventorySlot {
    Armor(36, 39),
    Helmet(39),
    ChestPlate(38),
    Pants(37),
    Boots(36),
    OffHand(40),
    MainHand(-1),
    QuickBar(0, 8),
    PartInventory(9, 35),
    MainInventory(0, 35),
    CraftingIngredients(1, 4),
    CraftingResult(0);

    private List<Integer> slot = new ArrayList<>();

    CMIInventorySlot(Integer integer, Integer secondInt) {
      for (int index = integer; index <= secondInt; index++) {
        this.slot.add(index);
      }
    }

    CMIInventorySlot(Integer integer) {
      this.slot.add(integer);
    }

    CMIInventorySlot(List<Integer> list) {
      this.slot.addAll(list);
    }

    public List<Integer> getSlots() {
      return this.slot;
    }

    public Integer getSlot() {
      return this.slot.get(0);
    }
  }

  public enum itemCheckType {
    name,
    lore,
    amount,
    nbt,
    material,
    custommodel,
    durability;
  }
}
