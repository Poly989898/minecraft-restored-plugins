package net.Zrips.CMILib.RawMessages;

import java.util.UUID;
import org.bukkit.command.CommandSender;

public class RawMessageCommand {
  private long id = 0L;
  private long time = 0L;
  private UUID uuid = null;
  private boolean keep = false;
  private String command = null;

  public RawMessageCommand() {
    this(null);
  }

  public RawMessageCommand(UUID uuid) {
    this.id = RawMessageManager.add(this);
    this.time = System.currentTimeMillis();
    this.uuid = uuid;
  }

  public void run(CommandSender sender) {}

  public Long getId() {
    return this.id;
  }

  @Deprecated
  public String getCommandToPerform() {
    return this.getCommand();
  }

  public String getCommand() {
    return RawMessageManager.rmccmd + this.id;
  }

  public void delete() {
    RawMessageManager.delete(this);
  }

  public boolean isKeep() {
    return this.keep;
  }

  public void setKeep(boolean keep) {
    this.keep = keep;
  }

  public Long getCreationTime() {
    return this.time;
  }

  public UUID getUUID() {
    return this.uuid;
  }

  public void setOriginalCommand(String command) {
    this.command = command.replace("  ", " ");
  }

  public String getOriginalCommand() {
    return this.command;
  }
}
