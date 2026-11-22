package com.Zrips.CMI.Modules.Holograms;

import java.util.UUID;
import org.bukkit.Location;

public class FakeEntityInfo {
  private int id = -1;
  private UUID playerUUID;
  private Object entity;
  private Object object1;
  private Object object2;
  private Location location;

  public FakeEntityInfo() {
    this(0, null, null);
  }

  public FakeEntityInfo(int value, UUID uuid, Location entityLocation) {
    this.id = value;
    this.playerUUID = uuid;
    this.location = entityLocation;
  }

  public int getId() {
    return this.id;
  }

  public FakeEntityInfo setId(int value) {
    this.id = value;
    return this;
  }

  public UUID getPlayerUUID() {
    return this.playerUUID;
  }

  public Object getEntity() {
    return this.entity;
  }

  public FakeEntityInfo setEntity(Object object) {
    this.entity = object;
    return this;
  }

  public Object getObject() {
    return this.object1;
  }

  public FakeEntityInfo setObject(Object object) {
    this.object1 = object;
    return this;
  }

  public Object getObjectSecond() {
    return this.object2;
  }

  public FakeEntityInfo setObjectSecond(Object object) {
    this.object2 = object;
    return this;
  }

  public Location getLocation() {
    return this.location;
  }

  public FakeEntityInfo setLocation(Location entityLocation) {
    this.location = entityLocation;
    return this;
  }
}
