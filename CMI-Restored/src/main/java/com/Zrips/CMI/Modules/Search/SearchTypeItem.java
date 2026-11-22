package com.Zrips.CMI.Modules.Search;

import com.Zrips.CMI.Containers.CMIShulkerBox;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.Zrips.CMILib.Items.CMIMC;
import net.Zrips.CMILib.Items.CMIMaterial;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BundleMeta;

public class SearchTypeItem extends SearchTypeContentsBase {
  SearchTypeItem parent = null;
  private CMIPlayerSearchPlaceType currentCheckPlace = CMIPlayerSearchPlaceType.EnderChest;
  Set<CMIDataResultBase> results = new HashSet<>();
  private UUID uuid = null;
  private ItemStack itemToCompare = null;
  protected int checkTotalAmount = 0;
  Map<CMIPlayerSearchPlaceType, CMIDataResultItem> totalCountResult = new ConcurrentHashMap<>();

  public SearchTypeItem(SearchTypeItem searchTypeItem) {
    this.parent = searchTypeItem;
  }

  public synchronized void setCurrentCheckedLocation(
      CMIPlayerSearchPlaceType cmiPlayerSearchPlaceType) {
    this.currentCheckPlace = cmiPlayerSearchPlaceType;
  }

  private CMIPlayerSearchPlaceType getCurrentCheckedLocation() {
    return this.parent == null ? this.currentCheckPlace : this.parent.getCurrentCheckedLocation();
  }

  protected CMIDataResultItem getTotalCountResult(
      CMIPlayerSearchPlaceType cmiPlayerSearchPlaceType) {
    return this.parent == null
        ? this.totalCountResult.get(cmiPlayerSearchPlaceType)
        : this.parent.getTotalCountResult(cmiPlayerSearchPlaceType);
  }

  protected void setTotalCountResult(CMIDataResultItem cmiDataResultItem) {
    if (this.parent == null) {
      this.totalCountResult.put(cmiDataResultItem.getType(), cmiDataResultItem);
    } else {
      this.parent.setTotalCountResult(cmiDataResultItem);
    }
  }

  protected void clearTotalCountResult() {
    if (this.parent == null) {
      this.totalCountResult.clear();
    } else {
      this.parent.clearTotalCountResult();
    }
  }

  protected UUID getCurrentUUID() {
    return this.parent == null ? this.uuid : this.parent.getCurrentUUID();
  }

  public void setCurrentUUID(UUID currentUuid) {
    if (this.parent == null) {
      this.uuid = currentUuid;
    } else {
      this.parent.setCurrentUUID(currentUuid);
    }
  }

  protected ItemStack getItemToCompare() {
    return this.parent == null ? this.itemToCompare : this.parent.getItemToCompare();
  }

  protected int getCheckTotalAmount() {
    return this.parent == null ? this.checkTotalAmount : this.parent.getCheckTotalAmount();
  }

  protected void setItemToCompare(ItemStack itemStack) {
    if (this.parent == null) {
      this.itemToCompare = itemStack;
    } else {
      this.parent.setItemToCompare(itemStack);
    }
  }

  protected void addResult(CMIDataResultBase cmiDataResultBase) {
    if (this.parent == null) {
      this.results.add(cmiDataResultBase);
    } else {
      this.parent.addResult(cmiDataResultBase);
    }
  }

  @Override
  public Set<CMIDataResultBase> checkContent(ItemStack[] itemStacks) {
    this.results.clear();
    ItemStack[] stackArray = itemStacks;
    int value = itemStacks.length;

    for (int index = 0; index < value; index++) {
      ItemStack itemStack = stackArray[index];
      if (itemStack != null) {
        this.setItemToCompare(itemStack);
        if (this.isValid()) {
          this.checkTotalResult();
        }
      }
    }

    this.finishTotalResult();
    return new HashSet<>(this.results);
  }

  private void checkTotalResult() {
    this.checkTotalResult(this.getCurrentCheckedLocation(), null);
  }

  private void checkTotalResult(CMIPlayerSearchPlaceType cmiPlayerSearchPlaceType) {
    this.checkTotalResult(cmiPlayerSearchPlaceType, null);
  }

  private void checkTotalResult(CMIPlayerSearchPlaceType cmiPlayerSearchPlaceType, String string) {
    if (this.getCheckTotalAmount() > 0) {
      if (this.getTotalCountResult(this.getCurrentCheckedLocation()) == null) {
        this.setTotalCountResult(
            new CMIDataResultItem(
                this.getCurrentCheckedLocation(),
                this.getCurrentUUID(),
                this.getItemToCompare().getAmount()));
        if (string != null) {
          this.getTotalCountResult(this.getCurrentCheckedLocation()).setSubIdentifier(string);
        }
      } else {
        this.getTotalCountResult(this.getCurrentCheckedLocation())
            .addAmount(this.getItemToCompare().getAmount());
      }
    } else {
      this.addResult(
          new CMIDataResultItem(
              cmiPlayerSearchPlaceType,
              this.getCurrentUUID(),
              this.getItemToCompare().getAmount()));
    }
  }

  private void finishTotalResult() {
    this.finishTotalResult(true);
  }

  private void finishTotalResult(boolean flag) {
    if (this.getTotalCountResult(this.getCurrentCheckedLocation()) != null
        && this.getTotalCountResult(this.getCurrentCheckedLocation()).getAmount()
            >= this.getCheckTotalAmount()) {
      this.addResult(this.getTotalCountResult(this.getCurrentCheckedLocation()));
    }

    if (flag) {
      this.clearTotalCountResult();
    }
  }

  public void finishedLocationCheck() {}

  protected boolean checkShulkerBundle() {
    return this.checkShulker()
        || Version.isCurrentEqualOrHigher(Version.v1_21_R1) && this.checkBundle();
  }

  private boolean checkShulker() {
    Inventory inventory = CMIShulkerBox.getShulkerInv(this.getItemToCompare());
    if (inventory == null) {
      return false;
    }

    CMIPlayerSearchPlaceType cmiPlayerSearchPlaceType =
        this.getCurrentCheckedLocation().equals(CMIPlayerSearchPlaceType.EnderChest)
            ? CMIPlayerSearchPlaceType.EnderChestShulker
            : CMIPlayerSearchPlaceType.InventoryShulker;

    for (int value = 0; value < inventory.getContents().length; value++) {
      ItemStack itemStack = inventory.getContents()[value];
      if (itemStack != null) {
        this.setItemToCompare(itemStack);
        if ((this.parent == null || this.parent.isValid())
            && (this.parent != null || this.isValid())) {
          this.checkTotalResult(cmiPlayerSearchPlaceType);
        }
      }
    }

    this.finishTotalResult(false);
    return true;
  }

  private boolean checkBundle() {
    if (!CMIMaterial.get(this.getItemToCompare().getType()).containsCriteria(CMIMC.BUNDLE)) {
      return false;
    }

    BundleMeta bundleMeta = (BundleMeta) this.getItemToCompare().getItemMeta();
    CMIPlayerSearchPlaceType cmiPlayerSearchPlaceType =
        this.getCurrentCheckedLocation().equals(CMIPlayerSearchPlaceType.EnderChest)
            ? CMIPlayerSearchPlaceType.EnderChestBundle
            : CMIPlayerSearchPlaceType.InventoryBundle;

    for (ItemStack itemStack : new ArrayList<>(bundleMeta.getItems())) {
      if (itemStack != null) {
        this.setItemToCompare(itemStack);
        if ((this.parent == null || this.parent.isValid())
            && (this.parent != null || this.isValid())) {
          this.checkTotalResult(cmiPlayerSearchPlaceType);
        }
      }
    }

    this.finishTotalResult(false);
    return true;
  }

  protected boolean isValid() {
    return false;
  }
}
