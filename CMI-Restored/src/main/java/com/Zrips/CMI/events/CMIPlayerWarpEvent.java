package com.Zrips.CMI.events;

import com.Zrips.CMI.Modules.Warps.CmiWarp;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CMIPlayerWarpEvent extends CMIPlayerCancellableEvent {
  private CmiWarp warp;
  private CommandSender sender;

  public CMIPlayerWarpEvent(Player player, CmiWarp cmiWarp, CommandSender commandSender) {
    super(player);
    this.warp = cmiWarp;
    this.sender = commandSender;
  }

  public CmiWarp getWarp() {
    return this.warp;
  }

  public void setWarp(CmiWarp cmiWarp) {
    this.warp = cmiWarp;
  }

  public CommandSender getCommandSender() {
    return this.sender;
  }

  public void setCommandSender(CommandSender commandSender) {
    this.sender = commandSender;
  }
}
