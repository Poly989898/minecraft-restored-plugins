package com.Zrips.CMI.commands;

import com.Zrips.CMI.CMI;

public class CMICommand {
  private Cmd cmdClass;
  private String name;
  private CAnnotation anottation;
  private Boolean enabled = null;
  private boolean baseEnabled = true;

  public CMICommand(Cmd cmd, String string, CAnnotation cAnnotation) {
    this.cmdClass = cmd;
    this.name = string;
    this.anottation = cAnnotation;
  }

  public Cmd getCmdClass() {
    return this.cmdClass;
  }

  public CMICommand setCmdClass(Cmd cmd) {
    this.cmdClass = cmd;
    return this;
  }

  public String getName() {
    return this.name;
  }

  public CMICommand setName(String string) {
    this.name = string;
    return this;
  }

  public CAnnotation getAnottation() {
    return this.anottation;
  }

  public String getTranslatedArgs() {
    String string = "";
    String helpKey = "command." + this.cmdClass.getClass().getSimpleName() + ".help.args";
    if (CMI.getInstance().getLM().containsKey(helpKey)
        && !CMI.getInstance().getLM().getMessage(helpKey).isEmpty()) {
      string = CMI.getInstance().getLM().getMessage(helpKey);
    }

    return string;
  }

  public void setAnottation(CAnnotation cAnnotation) {
    this.anottation = cAnnotation;
  }

  public Boolean getEnabled() {
    return this.enabled;
  }

  public void setEnabled(Boolean enabledValue) {
    this.enabled = enabledValue;
  }

  public boolean getBaseEnabled() {
    return this.baseEnabled;
  }

  public void setBaseEnabled(boolean flag) {
    this.baseEnabled = flag;
  }
}
