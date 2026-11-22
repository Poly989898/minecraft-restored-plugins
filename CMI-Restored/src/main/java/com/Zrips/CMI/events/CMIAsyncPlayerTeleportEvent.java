package com.Zrips.CMI.events;

import com.Zrips.CMI.Modules.Teleportations.CMITeleportType;
import com.Zrips.CMI.Modules.Teleportations.SafeTeleport;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class CMIAsyncPlayerTeleportEvent extends CMIPlayerCancellableEvent {
  private CommandSender sender;
  private SafeTeleport safe;
  private CMITeleportType type;

  public CMIAsyncPlayerTeleportEvent(
      CommandSender commandSender,
      Player player,
      SafeTeleport safeTeleport,
      CMITeleportType cmiTeleportType) {
    super(player, true);
    this.sender = commandSender;
    this.safe = safeTeleport;
    this.type = cmiTeleportType;
  }

  public CommandSender getSender() {
    return this.sender;
  }

  public SafeTeleport getSafe() {
    return this.safe;
  }

  public CMITeleportType getType() {
    return this.type;
  }

  public Location getTo() {
    return this.getSafe().getSafeLoc();
  }
}
