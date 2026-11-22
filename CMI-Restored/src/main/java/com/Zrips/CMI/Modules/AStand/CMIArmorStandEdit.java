package com.Zrips.CMI.Modules.AStand;

import java.util.UUID;
import org.bukkit.entity.Entity;

public class CMIArmorStandEdit {
  private UUID playerUUID;
  private Entity armorStand;
  private boolean editing = false;
  private ArmorStandCopy copy = null;

  public CMIArmorStandEdit(UUID uuid, Entity entity) {
    this.playerUUID = uuid;
    this.armorStand = entity;
  }

  public UUID getPlayerUUID() {
    return this.playerUUID;
  }

  public Entity getArmorStand() {
    return this.armorStand;
  }

  public void setArmorStand(Entity entity) {
    this.armorStand = entity;
  }

  public ArmorStandCopy getCopy() {
    return this.copy;
  }

  public void setCopy(ArmorStandCopy armorStandCopy) {
    this.copy = armorStandCopy;
  }

  public boolean isEditing() {
    return this.editing;
  }

  public void disableEditMode() {
    this.editing = false;
  }

  public void enableEditMode() {
    this.editing = true;
  }
}
