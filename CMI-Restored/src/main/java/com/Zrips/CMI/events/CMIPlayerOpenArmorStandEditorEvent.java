package com.Zrips.CMI.events;

import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;

public class CMIPlayerOpenArmorStandEditorEvent extends CMIPlayerCancellableEvent {
  private ArmorStand armorStand;

  public CMIPlayerOpenArmorStandEditorEvent(Player player, ArmorStand armorStand) {
    super(player);
    this.armorStand = armorStand;
  }

  public ArmorStand getArmorStand() {
    return this.armorStand;
  }

  public void setArmorStand(ArmorStand armorStand) {
    this.armorStand = armorStand;
  }
}
