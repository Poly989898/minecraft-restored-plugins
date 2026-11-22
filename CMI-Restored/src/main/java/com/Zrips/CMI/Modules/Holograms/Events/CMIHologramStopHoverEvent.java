package com.Zrips.CMI.Modules.Holograms.Events;

import com.Zrips.CMI.Modules.Holograms.CMIHologram;
import com.Zrips.CMI.events.CMIPlayerEvent;
import org.bukkit.entity.Player;

public final class CMIHologramStopHoverEvent extends CMIPlayerEvent {
  private CMIHologram hologram;

  public CMIHologramStopHoverEvent(Player player, CMIHologram cmiHologram) {
    super(player, true);
    this.hologram = cmiHologram;
  }

  public CMIHologram getHologram() {
    return this.hologram;
  }
}
