package com.Zrips.CMI.Modules.Display;

import com.Zrips.CMI.Modules.Holograms.CMIHologram;
import com.Zrips.CMI.Modules.Holograms.CMIHologramType;
import com.Zrips.CMI.Modules.Holograms.Settings.CMIHologramSettings;
import net.Zrips.CMILib.Container.CMIVector3D;
import org.bukkit.entity.Display.Billboard;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public enum CMIBillboard {
  VERTICAL(false, true, false),
  CENTER(true, true, false),
  FIXED(false, false, true),
  HORIZONTAL(true, false, true);

  private boolean canPitch = false;
  private boolean canYaw = false;
  private boolean canHaveBackSide = false;
  private Billboard bukkitBoard = null;

  CMIBillboard(boolean flag, boolean yawEnabled, boolean backSideEnabled) {
    this.canPitch = flag;
    this.canYaw = yawEnabled;
    this.canHaveBackSide = backSideEnabled;
  }

  public Billboard getBillboard() {
    return this.bukkitBoard != null ? this.bukkitBoard : Billboard.valueOf(this.toString());
  }

  public static CMIBillboard getByName(String string) {
    CMIBillboard[] cmiBillboards;
    int value = (cmiBillboards = values()).length;

    for (int index = 0; index < value; index++) {
      CMIBillboard cmiBillboard = cmiBillboards[index];
      if (cmiBillboard.toString().equalsIgnoreCase(string)) {
        return cmiBillboard;
      }
    }

    return null;
  }

  public CMIBillboard next() {
    switch (this) {
      case VERTICAL:
        return HORIZONTAL;
      case CENTER:
        return VERTICAL;
      case FIXED:
        return CENTER;
      case HORIZONTAL:
        return FIXED;
      default:
        return CENTER;
    }
  }

  public boolean isCanPitch() {
    return this.canPitch;
  }

  public boolean isCanYaw() {
    return this.canYaw;
  }

  public boolean isCanHaveBackSide() {
    return this.canHaveBackSide;
  }

  public double getPlanePitch(Player player, @NotNull CMIHologram cmiHologram) {
    CMIHologramType cmiHologramType = cmiHologram.getHologramType(player.getUniqueId());
    switch (cmiHologramType) {
      case TextDisplay:
      case Auto:
      default:
        switch (this) {
          case VERTICAL:
            CMIHologramSettings cmiHologramSettings = cmiHologram.getSettings();
            return Math.toRadians(-cmiHologramSettings.getPitch());
          case CENTER:
            return Math.toRadians(player.getLocation().getPitch());
          case FIXED:
            CMIHologramSettings settings = cmiHologram.getSettings();
            return Math.toRadians(settings.getPitch());
          case HORIZONTAL:
            return Math.toRadians(-player.getLocation().getPitch());
          default:
            return 0.0;
        }
      case ArmorStand:
        return Math.toRadians(0.0);
    }
  }

  public double getPlaneYaw(Player player, @NotNull CMIHologram cmiHologram) {
    CMIHologramType cmiHologramType = cmiHologram.getHologramType(player.getUniqueId());
    switch (cmiHologramType) {
      case TextDisplay:
      case Auto:
      default:
        switch (this) {
          case VERTICAL:
            return Math.toRadians(player.getLocation().getYaw());
          case CENTER:
            return Math.toRadians(player.getLocation().getYaw());
          case FIXED:
            CMIHologramSettings cmiHologramSettings = cmiHologram.getSettings();
            return Math.toRadians(cmiHologramSettings.getYaw());
          case HORIZONTAL:
            CMIHologramSettings settings = cmiHologram.getSettings();
            return Math.toRadians(settings.getYaw());
          default:
            return 0.0;
        }
      case ArmorStand:
        return Math.toRadians(player.getLocation().getYaw());
    }
  }

  public CMIVector3D getPlaneDirection(Player player, @NotNull CMIHologram cmiHologram) {
    double number = this.getPlanePitch(player, cmiHologram);
    double planeYaw = this.getPlaneYaw(player, cmiHologram);
    double vectorX = Math.cos(number) * Math.sin(planeYaw);
    double vectorY = Math.sin(number);
    double vectorZ = -Math.cos(number) * Math.cos(planeYaw);
    return new CMIVector3D(vectorX, vectorY, vectorZ);
  }
}
