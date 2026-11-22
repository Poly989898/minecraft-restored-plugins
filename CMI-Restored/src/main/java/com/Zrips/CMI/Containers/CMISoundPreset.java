package com.Zrips.CMI.Containers;

import net.Zrips.CMILib.Sounds.CMISound;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public enum CMISoundPreset {
  WarpGuiOpen("entity_bat_takeoff:0.5:1"),
  TeleportHome("block_beacon_activate:2:1"),
  TeleportWarp("entity_enderman_teleport:0.5:1"),
  TeleportSpawn("entity_enderman_teleport:0.5:1"),
  TeleportFail("entity_villager_no:2:1"),
  PrivateMessage("entity_endermite_death:2:1"),
  TpaRequest("block_anvil_land:0.5:2"),
  MailNotification("entity_creeper_hurt:1:0.5"),
  TeleportUp("entity_enderman_teleport:2:1"),
  TeleportDown("entity_enderman_teleport:0.2:1"),
  CommandWarmup("blockrespawnanchorcharge:1:1"),
  CommandCooldown("blocknoteblockpling:0.5:0.5");

  private String soundString;
  private CMISound sound;
  private boolean enabled = true;

  CMISoundPreset(String string) {
    this.soundString = string;

    try {
      this.sound = new CMISound(this.soundString);
      this.sound.setEnabled(this.enabled);
    } catch (Throwable throwable) {
      throwable.printStackTrace();
    }
  }

  public String getSoundString() {
    return this.soundString;
  }

  public void setSoundString(String string) {
    this.soundString = string;
    this.sound = new CMISound(this.soundString);
    this.sound.setEnabled(this.enabled);
  }

  public CMISound getSound() {
    return this.sound;
  }

  public void play(Player player, Location location) {
    if (this.enabled && player != null && player.isOnline()) {
      CMISound cmiSound = this.getSound();
      if (cmiSound != null) {
        cmiSound.play(player, location);
      }
    }
  }

  public void play(Player player) {
    if (this.enabled && player != null && player.isOnline()) {
      CMISound cmiSound = this.getSound();
      if (cmiSound != null) {
        cmiSound.play(player);
      }
    }
  }

  public void play(Location location) {
    if (this.enabled && location != null) {
      CMISound cmiSound = this.getSound();
      if (cmiSound != null) {
        cmiSound.play(location);
      }
    }
  }

  public void setSound(CMISound cmiSound) {
    this.sound = cmiSound;
    this.sound.setEnabled(this.enabled);
  }

  public boolean isEnabled() {
    return this.enabled;
  }

  public void setEnabled(boolean flag) {
    this.enabled = flag;
    this.sound.setEnabled(flag);
  }
}
