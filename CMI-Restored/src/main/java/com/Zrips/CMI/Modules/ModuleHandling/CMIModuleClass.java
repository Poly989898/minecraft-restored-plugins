package com.Zrips.CMI.Modules.ModuleHandling;

import net.Zrips.CMILib.Version.Version;

public class CMIModuleClass {
  private Class<?> listener;
  private Version from = null;
  private Version to = null;
  private CMIModuleServerType type = CMIModuleServerType.Any;

  public CMIModuleClass(Class<?> listenerClass) {
    this(listenerClass, null, null, CMIModuleServerType.Any);
  }

  public CMIModuleClass(
      Class<?> listenerClass, Version version, CMIModuleServerType cmiModuleServerType) {
    this(listenerClass, version, null, cmiModuleServerType);
  }

  public CMIModuleClass(Class<?> listenerClass, Version version) {
    this(listenerClass, version, null, CMIModuleServerType.Any);
  }

  public CMIModuleClass(Class<?> listenerClass, Version version, Version toVersion) {
    this(listenerClass, version, toVersion, CMIModuleServerType.Any);
  }

  public CMIModuleClass(
      Class<?> listenerClass,
      Version version,
      Version toVersion,
      CMIModuleServerType cmiModuleServerType) {
    this.listener = listenerClass;
    this.from = version;
    this.to = toVersion;
    this.type = cmiModuleServerType;
  }

  public Version getFrom() {
    return this.from;
  }

  public Version getTo() {
    return this.to;
  }

  public Class<?> getListener() {
    return this.listener;
  }

  public CMIModuleServerType getType() {
    return this.type;
  }
}
