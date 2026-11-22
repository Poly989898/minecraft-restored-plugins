package com.Zrips.CMI.Modules.Teleportations;

import com.Zrips.CMI.Modules.Particl.ParticleManager;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

public enum CMITeleportType {
  Unknown(TeleportCause.UNKNOWN),
  Elevator(TeleportCause.UNKNOWN),
  SafeLogin(TeleportCause.PLUGIN),
  Spawn(TeleportCause.COMMAND),
  NetherRoof(TeleportCause.PLUGIN),
  BelowBedrock(TeleportCause.PLUGIN),
  Back(TeleportCause.COMMAND),
  DBack(TeleportCause.COMMAND),
  Home(TeleportCause.COMMAND),
  Jump(TeleportCause.COMMAND),
  Patrol(TeleportCause.COMMAND),
  Portal(TeleportCause.PLUGIN),
  WarmUp(TeleportCause.COMMAND),
  Biome(TeleportCause.COMMAND),
  FlightCharge(TeleportCause.PLUGIN),
  InvEdit(TeleportCause.COMMAND),
  TimedCommand(TeleportCause.PLUGIN),
  TpaAll(TeleportCause.COMMAND),
  Tp(TeleportCause.COMMAND),
  Bungee(TeleportCause.PLUGIN),
  Top(TeleportCause.COMMAND),
  Down(TeleportCause.COMMAND),
  TpAll(TeleportCause.COMMAND),
  TpHere(TeleportCause.COMMAND),
  TpPos(TeleportCause.COMMAND),
  TpoPos(TeleportCause.COMMAND),
  Warp(TeleportCause.COMMAND),
  JoinSpawn(TeleportCause.PLUGIN),
  Totem(TeleportCause.PLUGIN),
  randomTp(TeleportCause.COMMAND),
  World(TeleportCause.COMMAND),
  HoloEdit(TeleportCause.PLUGIN);

  private ParticleManager.CMIPresetAnimations from = null;
  private ParticleManager.CMIPresetAnimations to = null;
  private TeleportCause cause;

  CMITeleportType(TeleportCause teleportCause) {
    this.cause = teleportCause;
  }

  public ParticleManager.CMIPresetAnimations getFrom() {
    return this.from;
  }

  public void setFrom(ParticleManager.CMIPresetAnimations cmiPresetAnimations) {
    this.from = cmiPresetAnimations;
  }

  public ParticleManager.CMIPresetAnimations getTo() {
    return this.to;
  }

  public void setTo(ParticleManager.CMIPresetAnimations cmiPresetAnimations) {
    this.to = cmiPresetAnimations;
  }

  public TeleportCause getCause() {
    return this.cause;
  }
}
