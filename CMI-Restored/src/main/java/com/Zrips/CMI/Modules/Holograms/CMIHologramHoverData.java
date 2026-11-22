package com.Zrips.CMI.Modules.Holograms;

import com.Zrips.CMI.Modules.Display.CMITextAlignment;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public class CMIHologramHoverData {
  private static ConcurrentHashMap<UUID, CMIHologramHoverData> userData = new ConcurrentHashMap<>();
  private double hovering = -1.0;
  private int hoveringLine = -1;
  private CMIHologram hoveringHologram = null;
  private CMIHologramLine hoveringLineObject = null;
  private CMIHologramLinePortion hoveringLinePortion = null;
  private CMITextAlignment alignment = CMITextAlignment.CENTER;
  private FakeEntityInfo fakeInfo = null;
  private UUID uuid = null;
  private long showCornersUntil = 0L;

  public static synchronized CMIHologramHoverData getData(Player player) {
    return getData(player.getUniqueId());
  }

  public static synchronized CMIHologramHoverData getData(UUID playerUuid) {
    return userData.computeIfAbsent(playerUuid, key -> new CMIHologramHoverData(playerUuid));
  }

  protected static synchronized CMIHologramHoverData removeData(UUID playerUuid) {
    return userData.remove(playerUuid);
  }

  protected static synchronized void clearData() {
    userData.clear();
  }

  public CMIHologramHoverData(UUID playerUuid) {
    this.uuid = playerUuid;
  }

  public int getHoverParts() {
    return this.hoveringLineObject.getParts();
  }

  public boolean hoveringLeftSide() {
    return this.hovering <= 0.5;
  }

  public boolean isChangeInHoverPart(double number) {
    return this.hovering != number;
  }

  public boolean hoveringPart(double number) {
    if (this.getHoverParts() <= 1) {
      return number >= 0.0 && number <= 1.0;
    }

    if (this.hovering < 0.0) {
      return false;
    }

    double partWidth = 1.0 / this.getHoverParts();
    int value = (int) (this.hovering / partWidth);
    double partStart = value * partWidth;
    double partEnd = partStart + partWidth;
    if (this.hovering == this.getHoverParts() - 1) {
      partEnd = 1.0;
    }

    return number >= partStart && number <= partEnd;
  }

  public double getHoveringPart() {
    return this.hovering;
  }

  public int getHoveringIntPart() {
    return (int) (this.hovering / (1.0 / this.getHoverParts()));
  }

  public CMIHologramHoverData setHoveringPart(double number) {
    this.hovering = number;
    return this;
  }

  public int getHoveringLine() {
    return this.hoveringLine;
  }

  public int getHoveringLineAndReset() {
    int value = this.hoveringLine;
    this.hoveringLine = -1;
    this.hoveringLinePortion = null;
    return value;
  }

  public CMIHologramHoverData resetHoverPosition() {
    this.setHoveringLine(-1);
    this.setHoveringPart(-1.0);
    this.hoveringLinePortion = null;
    return this;
  }

  public CMIHologramHoverData setHoveringLine(int value) {
    this.hoveringLine = value < 1 ? -1 : value;
    return this;
  }

  public CMIHologram getHoveringHologram() {
    return this.hoveringHologram;
  }

  public CMIHologramHoverData setHoveringHologram(CMIHologram cmiHologram) {
    this.hoveringHologram = cmiHologram;
    this.recheckTextAlignment();
    return this;
  }

  public FakeEntityInfo getFakeInfo() {
    return this.fakeInfo;
  }

  public CMIHologramHoverData setFakeInfo(FakeEntityInfo fakeEntityInfo) {
    this.fakeInfo = fakeEntityInfo;
    return this;
  }

  @Nullable
  public CMIHologramLine getHoveringLineObject() {
    return this.hoveringLineObject;
  }

  private void recheckTextAlignment() {
    if (this.getHoveringHologram() == null) {
      this.alignment = CMITextAlignment.CENTER;
    } else {
      CMIHologramUserData cmiHologramUserData = this.getHoveringHologram().getData(this.uuid);
      if (cmiHologramUserData != null) {
        switch (cmiHologramUserData.getHologramType()) {
          case TextDisplay:
          case Auto:
            this.alignment = this.getHoveringHologram().getTextSettings().getTextAlignment();
            break;
          case ArmorStand:
            this.alignment = CMITextAlignment.CENTER;
        }
      }
    }
  }

  public boolean isHoveringOverNewLinePortion(double number, CMIHologramPage cmiHologramPage) {
    if (this.hovering == number) {
      return false;
    }

    if (this.getHoveringLineObject() == null) {
      return false;
    }

    CMIHologramLinePortion cmiHologramLinePortion =
        this.getHoveringLineObject().getPortionByRange(number, cmiHologramPage, this.alignment);
    boolean flag = !Objects.equals(cmiHologramLinePortion, this.getHoveringLinePortion());
    if (flag) {
      this.setHoveringLinePortion(cmiHologramLinePortion);
    }

    return flag;
  }

  public void setHoveringLine(@Nullable CMIHologramLine cmiHologramLine) {
    this.hoveringLineObject = cmiHologramLine;
  }

  public CMIHologramLinePortion getHoveringLinePortion() {
    return this.hoveringLinePortion;
  }

  private void setHoveringLinePortion(CMIHologramLinePortion cmiHologramLinePortion) {
    this.hoveringLinePortion = cmiHologramLinePortion;
  }

  public long getShowCornersUntil() {
    return this.showCornersUntil;
  }

  public void setShowCornersUntil(long longValue) {
    this.showCornersUntil = longValue;
  }
}
