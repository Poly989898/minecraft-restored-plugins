package com.Zrips.CMI.Modules.Holograms.Events;

import com.Zrips.CMI.Modules.Holograms.CMIHologram;
import com.Zrips.CMI.Modules.Holograms.CMIPlaneIntersection;
import com.Zrips.CMI.events.CMIPlayerEvent;
import org.bukkit.entity.Player;

public final class CMIHologramStartHoverEvent extends CMIPlayerEvent {
  private CMIHologram hologram;
  private CMIPlaneIntersection pos;

  public CMIHologramStartHoverEvent(
      Player player, CMIHologram cmiHologram, CMIPlaneIntersection cmiPlaneIntersection) {
    super(player, true);
    this.hologram = cmiHologram;
    this.pos = cmiPlaneIntersection;
  }

  public CMIHologram getHologram() {
    return this.hologram;
  }

  public CMIPlaneIntersection getPos() {
    return this.pos;
  }
}
