package com.Zrips.CMI.events;

import net.Zrips.CMILib.Items.ArmorTypes;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CMIArmorChangeEvent extends CMIPlayerCancellableEvent {
  private final CMIArmorChangeEvent.EquipMethod equipType;
  private final ArmorTypes type;
  private ItemStack oldArmorPiece;
  private ItemStack newArmorPiece;

  public CMIArmorChangeEvent(
      Player player,
      CMIArmorChangeEvent.EquipMethod equipMethod,
      ArmorTypes armorTypes,
      ItemStack itemStack,
      ItemStack newArmorItem) {
    super(player);
    this.equipType = equipMethod;
    this.type = armorTypes;
    this.oldArmorPiece = itemStack;
    this.newArmorPiece = newArmorItem;
  }

  public final ArmorTypes getType() {
    return this.type;
  }

  public final ItemStack getOldArmorPiece() {
    return this.oldArmorPiece;
  }

  public final void setOldArmorPiece(ItemStack itemStack) {
    this.oldArmorPiece = itemStack;
  }

  public final ItemStack getNewArmorPiece() {
    return this.newArmorPiece;
  }

  public final void setNewArmorPiece(ItemStack itemStack) {
    this.newArmorPiece = itemStack;
  }

  public CMIArmorChangeEvent.EquipMethod getMethod() {
    return this.equipType;
  }

  public enum EquipMethod {
    SHIFT_CLICK,
    DRAG,
    HOTBAR,
    HOTBAR_SWAP,
    DISPENSER,
    BROKE,
    DEATH;
  }
}
