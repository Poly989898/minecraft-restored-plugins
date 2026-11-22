package com.Zrips.CMI.events;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.world.PortalCreateEvent.CreateReason;
import org.bukkit.util.Vector;

public class CMIPortalCreateEvent extends CMIPlayerCancellableEvent {
  private int height;
  private int width;
  private World world;
  private Vector lowestPoint;
  private CreateReason reason;
  private CMIPortalCreateEvent.CMIPortalType type;

  public CMIPortalCreateEvent(
      Player player, World eventWorld, CreateReason createReason, int value, int widthValue) {
    super(player);
    this.height = value;
    this.width = widthValue;
    this.world = eventWorld;
    this.reason = createReason;
  }

  public int getHeight() {
    return this.height;
  }

  public int getWidth() {
    return this.width;
  }

  public World getWorld() {
    return this.world;
  }

  public CreateReason getReason() {
    return this.reason;
  }

  public CMIPortalCreateEvent.CMIPortalType getType() {
    return this.type;
  }

  public void setType(CMIPortalCreateEvent.CMIPortalType cmiPortalType) {
    this.type = cmiPortalType;
  }

  public Vector getLowestPoint() {
    return this.lowestPoint;
  }

  public void setLowestPoint(Vector vector) {
    this.lowestPoint = vector;
  }

  public enum CMIPortalType {
    Nether,
    End;
  }
}
