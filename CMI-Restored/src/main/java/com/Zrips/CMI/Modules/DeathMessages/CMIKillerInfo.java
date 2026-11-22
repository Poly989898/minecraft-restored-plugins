package com.Zrips.CMI.Modules.DeathMessages;

import java.util.ArrayList;
import java.util.List;
import net.Zrips.CMILib.Items.CMIItemStack;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;

class CMIKillerInfo {
  private damageInformation damage;
  private Entity killer = null;
  private Block killerBlock = null;
  private CMIDeathType type = CMIDeathType.Custom;
  private CMIItemStack killerItem = null;
  private List<CMIDeathType> path = new ArrayList<>();

  public CMIKillerInfo(damageInformation damageInformation) {
    this.damage = damageInformation;
  }

  public Entity getKiller() {
    return this.killer;
  }

  public void setKiller(Entity entity) {
    this.killer = entity;
  }

  public List<CMIDeathType> getPath() {
    return this.path;
  }

  public void setPath(List<CMIDeathType> list) {
    this.path = list;
  }

  public damageInformation getDamage() {
    return this.damage;
  }

  public Block getKillerBlock() {
    return this.killerBlock;
  }

  public void setKillerBlock(Block block) {
    this.killerBlock = block;
  }

  public CMIItemStack getKillerItem() {
    return this.killerItem;
  }

  public void setKillerItem(CMIItemStack cmiItemStack) {
    this.killerItem = cmiItemStack;
  }

  public CMIDeathType getType() {
    return this.type;
  }

  public void setType(CMIDeathType cmiDeathType) {
    this.type = cmiDeathType;
  }
}
