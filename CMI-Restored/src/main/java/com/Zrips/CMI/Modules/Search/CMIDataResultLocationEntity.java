package com.Zrips.CMI.Modules.Search;

import java.util.UUID;
import net.Zrips.CMILib.Container.CMIVectorInt3D;
import net.Zrips.CMILib.Entities.CMIEntityType;
import org.bukkit.entity.Entity;

public class CMIDataResultLocationEntity extends CMIDataResultLocationBase {
  CMIEntityType type;

  public CMIDataResultLocationEntity(Entity entity, CMIVectorInt3D cmiVectorInt3D, int value) {
    super(cmiVectorInt3D, value);
    this.type = CMIEntityType.get(entity);
    this.uuid = entity.getUniqueId();
  }

  public CMIDataResultLocationEntity(
      CMIEntityType cmiEntityType, CMIVectorInt3D cmiVectorInt3D, int value) {
    super(cmiVectorInt3D, value);
    this.type = cmiEntityType;
  }

  @Override
  public UUID getUUID() {
    return this.uuid;
  }

  public CMIEntityType getType() {
    return this.type;
  }

  @Override
  public String getValueOutput() {
    return this.type == null ? "Unknown" : this.type.toString();
  }
}
