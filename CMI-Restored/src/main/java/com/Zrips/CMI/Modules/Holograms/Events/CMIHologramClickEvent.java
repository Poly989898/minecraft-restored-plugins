package com.Zrips.CMI.Modules.Holograms.Events;

import com.Zrips.CMI.Containers.CMIInteractType;
import com.Zrips.CMI.Modules.Holograms.CMIHologram;
import com.Zrips.CMI.Modules.Holograms.CMIHologramLine;
import com.Zrips.CMI.Modules.Holograms.CMIHologramLinePortion;
import com.Zrips.CMI.events.CMIPlayerCancellableEvent;
import net.Zrips.CMILib.Container.CMIVector3D;
import org.bukkit.entity.Player;

public final class CMIHologramClickEvent extends CMIPlayerCancellableEvent {
  private CMIHologram hologram;
  private int line;
  private CMIVector3D pos;
  private CMIInteractType actionType;
  private CMIHologramLine hoveringLineObject;
  private CMIHologramLinePortion hoveringLinePortion;

  public CMIHologramClickEvent(
      Player player,
      CMIHologram cmiHologram,
      CMIHologramLine cmiHologramLine,
      CMIHologramLinePortion cmiHologramLinePortion,
      int value,
      CMIVector3D cmiVector3D,
      CMIInteractType cmiInteractType) {
    super(player);
    this.hologram = cmiHologram;
    this.hoveringLineObject = cmiHologramLine;
    this.hoveringLinePortion = cmiHologramLinePortion;
    this.line = value;
    this.pos = cmiVector3D.clone();
    this.actionType = cmiInteractType;
  }

  public CMIHologram getHologram() {
    return this.hologram;
  }

  public int getLineNumber() {
    return this.line;
  }

  public CMIVector3D getPos() {
    return this.pos;
  }

  public CMIInteractType getActionType() {
    return this.actionType;
  }

  public CMIHologramLine getHoveringLineObject() {
    return this.hoveringLineObject;
  }

  public CMIHologramLinePortion getHoveringLinePortion() {
    return this.hoveringLinePortion;
  }
}
