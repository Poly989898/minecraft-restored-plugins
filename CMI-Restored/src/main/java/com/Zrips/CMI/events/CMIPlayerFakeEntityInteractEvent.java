package com.Zrips.CMI.events;

import com.Zrips.CMI.Containers.CMIInteractType;
import org.bukkit.entity.Player;

public class CMIPlayerFakeEntityInteractEvent extends CMIPlayerEvent {
  private final int id;
  private Object obj;
  private CMIInteractType type;

  public CMIPlayerFakeEntityInteractEvent(
      Player player, int value, Object object, CMIInteractType cmiInteractType) {
    super(player);
    this.id = value;
    this.obj = object;
    this.type = cmiInteractType;
  }

  public int getEntityId() {
    return this.id;
  }

  public Object getObject() {
    return this.obj;
  }

  public CMIInteractType getType() {
    return this.type;
  }

  public void setType(CMIInteractType cmiInteractType) {
    this.type = cmiInteractType;
  }
}
