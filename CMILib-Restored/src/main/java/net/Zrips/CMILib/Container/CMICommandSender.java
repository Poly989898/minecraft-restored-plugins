package net.Zrips.CMILib.Container;

import java.util.UUID;
import net.Zrips.CMILib.CMILib;
import org.bukkit.Location;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.ProxiedCommandSender;
import org.bukkit.entity.Player;

public class CMICommandSender {
  private CMICommandSender.commandSenderType type = CMICommandSender.commandSenderType.invalid;
  private CommandSender sender;

  public CMICommandSender(CommandSender sender) {
    this.sender = sender;
    if (sender != null) {
      if (sender instanceof Player) {
        this.type = CMICommandSender.commandSenderType.player;
      } else if (sender instanceof ConsoleCommandSender) {
        this.type = CMICommandSender.commandSenderType.console;
      } else {
        try {
          if (Class.forName("org.bukkit.command.BlockCommandSender") != null
              && sender instanceof BlockCommandSender) {
            this.type = CMICommandSender.commandSenderType.commandblock;
          }
        } catch (Exception var4) {
        }

        try {
          if (Class.forName("org.bukkit.command.ProxiedCommandSender") != null
              && sender instanceof ProxiedCommandSender) {
            this.type = CMICommandSender.commandSenderType.rcon;
          }
        } catch (Exception var3) {
        }
      }
    }
  }

  public boolean isPlayer() {
    return this.type.equals(CMICommandSender.commandSenderType.player);
  }

  public boolean isConsole() {
    return this.type.equals(CMICommandSender.commandSenderType.console);
  }

  public boolean isCommandBlock() {
    return this.type.equals(CMICommandSender.commandSenderType.commandblock);
  }

  public Player getPlayer() {
    return !this.isPlayer() ? null : (Player) this.sender;
  }

  public boolean isValid() {
    return this.sender != null && !this.type.equals(CMICommandSender.commandSenderType.invalid);
  }

  public Location getLocation() {
    if (!this.isValid()) {
      return null;
    } else if (this.isPlayer()) {
      return this.getPlayer().getLocation();
    } else {
      return this.isCommandBlock()
          ? ((BlockCommandSender) this.sender).getBlock().getLocation()
          : null;
    }
  }

  public UUID getUUID() {
    if (!this.isValid()) {
      return null;
    } else if (this.isPlayer()) {
      return this.getPlayer().getUniqueId();
    } else {
      return this.isConsole() ? CMILib.getInstance().getServerUUID() : null;
    }
  }

  public CMICommandSender.commandSenderType getType() {
    return this.type;
  }

  public CommandSender getSender() {
    return this.sender;
  }

  public void setSender(CommandSender sender) {
    this.sender = sender;
  }

  public void sendMessage(String message) {
    this.getSender().sendMessage(message);
  }

  public void performCommand(String command) {
    CMICommands.performCommand(this.getSender(), command);
  }

  public enum commandSenderType {
    player,
    console,
    commandblock,
    rcon,
    invalid;
  }
}
