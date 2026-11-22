package com.Zrips.CMI.Modules.SpecializedCommands;

import java.util.ArrayList;
import java.util.List;

public class specCommand {
  private String cmd;
  private SpecializedCommandManager.specialisedCommand action = null;
  private List<SpecializedCommandManager.specCommandAction> subactions = new ArrayList<>();
  private List<SpecializedCommandManager.specCommandAction> list = new ArrayList<>();
  private List<SpecializedCommandManager.specCommandAction> statements = new ArrayList<>();

  public String getCmd() {
    return this.cmd;
  }

  public void setCmd(String string) {
    this.cmd = string;
  }

  public List<SpecializedCommandManager.specCommandAction> getConditionList() {
    return this.list;
  }

  public void add(SpecializedCommandManager.specCommandAction specCommandAction) {
    if (specCommandAction
        .getCmd()
        .getType()
        .equals(SpecializedCommandManager.specialisedCommandType.action)) {
      this.action = specCommandAction.getCmd();
    } else if (specCommandAction
        .getCmd()
        .getType()
        .equals(SpecializedCommandManager.specialisedCommandType.subaction)) {
      this.subactions.add(specCommandAction);
    } else if (specCommandAction
        .getCmd()
        .getType()
        .equals(SpecializedCommandManager.specialisedCommandType.statement)) {
      this.statements.add(specCommandAction);
    } else {
      this.list.add(specCommandAction);
    }
  }

  public SpecializedCommandManager.specialisedCommand getAction() {
    return this.action;
  }

  public List<SpecializedCommandManager.specCommandAction> getSubactions() {
    return this.subactions;
  }

  public List<SpecializedCommandManager.specCommandAction> getStatements() {
    return this.statements;
  }

  public boolean containsSubaction(
      SpecializedCommandManager.specialisedCommand specialisedCommand) {
    for (SpecializedCommandManager.specCommandAction specCommandAction : this.getSubactions()) {
      if (specCommandAction.getCmd().equals(specialisedCommand)) {
        return true;
      }
    }

    return false;
  }
}
