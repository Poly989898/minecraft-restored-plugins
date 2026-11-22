package com.Zrips.CMI.Modules.CmdCost;

import com.Zrips.CMI.commands.CommandsHandler;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class CMICommandCost {
  private String command = "";
  private double cost = 0.0;
  private boolean informOnCharge = true;
  private boolean requiresConfirmation = true;
  ConcurrentHashMap<UUID, String> confirmations = new ConcurrentHashMap<>();

  public CMICommandCost(String string, double number) {
    this.command = string;
    this.cost = number > 0.0 ? number : 0.0;
  }

  public String getCommand() {
    return this.command;
  }

  public void setCommand(String string) {
    this.command = string;
  }

  public double getCost() {
    return this.cost;
  }

  public String getSubCommand() {
    return this.command.startsWith(CommandsHandler.getLabel() + " ")
        ? this.command.substring(CommandsHandler.getLabel().length() + 1)
        : this.command;
  }

  public void setCost(double number) {
    this.cost = number;
  }

  public boolean isInformOnCharge() {
    return this.informOnCharge;
  }

  public void setInformOnCharge(boolean flag) {
    this.informOnCharge = flag;
  }

  public boolean isRequiresConfirmation() {
    return this.requiresConfirmation;
  }

  public void setRequiresConfirmation(boolean flag) {
    this.requiresConfirmation = flag;
  }

  public void addConfirmation(UUID uuid, String string) {
    this.confirmations.put(uuid, string);
  }

  public boolean isConfirmation(UUID uuid, String string) {
    String confirmation = this.confirmations.get(uuid);
    return confirmation == null ? false : confirmation.equals(string);
  }

  public void removeConfirmation(UUID uuid) {
    this.confirmations.remove(uuid);
  }
}
