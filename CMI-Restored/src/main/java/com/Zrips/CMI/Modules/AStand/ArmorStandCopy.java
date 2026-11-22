package com.Zrips.CMI.Modules.AStand;

import java.util.HashSet;
import java.util.Set;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;

public class ArmorStandCopy {
  private Player player;
  private ArmorStand stand;
  private Set<ArmorStandManager.armorStandActions> copy;
  private Set<ArmorStandManager.armorStandExtraActions> extraCopy;

  ArmorStandCopy(
      Player targetPlayer,
      ArmorStand armorStand,
      Set<ArmorStandManager.armorStandActions> set,
      Set<ArmorStandManager.armorStandExtraActions> extraActionSet) {
    this.player = targetPlayer;
    this.stand = armorStand;
    this.copy = set;
    this.extraCopy = extraActionSet;
  }

  public Player getPlayer() {
    return this.player;
  }

  public void setPlayer(Player targetPlayer) {
    this.player = targetPlayer;
  }

  public ArmorStand getStand() {
    return this.stand;
  }

  public void setStand(ArmorStand armorStand) {
    this.stand = armorStand;
  }

  public Set<ArmorStandManager.armorStandActions> getCopy() {
    return this.copy;
  }

  public void duplicateOn(ArmorStand armorStand) {
    CMIArmorStandEditor.duplicate(this.stand, armorStand, this.getCopy());
    CMIArmorStandEditor.duplicateExtra(this.stand, armorStand, this.getExtraCopy());
  }

  public Set<ArmorStandManager.armorStandExtraActions> getExtraCopy() {
    if (this.extraCopy == null) {
      this.extraCopy = new HashSet<>();
    }

    return this.extraCopy;
  }
}
