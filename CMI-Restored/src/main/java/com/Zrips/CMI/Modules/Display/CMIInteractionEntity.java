package com.Zrips.CMI.Modules.Display;

import org.bukkit.Location;
import org.bukkit.entity.Interaction;
import org.bukkit.util.Transformation;

public class CMIInteractionEntity extends CMIDisplay {
  public CMIInteractionEntity(Location location) {
    super(CMIDisplayType.Interaction, location);
  }

  @Override
  public void setWidth(double number) {
    ((Interaction) this.display).setInteractionWidth((float) number);
  }

  @Override
  public void setHeight(double number) {
    ((Interaction) this.display).setInteractionHeight((float) number);
  }

  @Override
  public Transformation getTransformation() {
    return null;
  }
}
