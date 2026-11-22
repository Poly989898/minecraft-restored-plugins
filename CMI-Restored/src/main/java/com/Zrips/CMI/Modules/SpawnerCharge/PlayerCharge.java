package com.Zrips.CMI.Modules.SpawnerCharge;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import java.util.UUID;
import net.Zrips.CMILib.Time.CMITimeManager;

public class PlayerCharge {
  private int charges = 0;
  private long lastGive = 0L;
  private SCharges SCharge;
  private UUID uuid;
  private long lastChecked = 0L;

  public PlayerCharge(CMIUser cmiUser, boolean flag) {
    if (cmiUser != null) {
      this.uuid = cmiUser.getUniqueId();
    }

    if (flag) {
      this.updateSpawnerCharge();
    }
  }

  private void updateSpawnerCharge() {
    if (CMIUser.getOnlinePlayer(this.uuid) != null
        && this.lastChecked + 1000L <= System.currentTimeMillis()) {
      this.lastChecked = System.currentTimeMillis();
      this.SCharge =
          CMI.getInstance()
              .getSpawnerChargesManager()
              .getCharge(CMIUser.getOnlinePlayer(this.uuid));
      if (this.lastGive == 0L && this.SCharge != null) {
        this.charges = this.SCharge.getStartingCharges();
      }
    }
  }

  public void reset() {
    this.reset(true);
  }

  public void reset(boolean flag) {
    this.charges = 0;
    this.lastGive = 0L;
    this.SCharge = null;
    if (flag) {
      this.updateSpawnerCharge();
    }
  }

  public int getCharges() {
    this.updateSpawnerCharge();
    this.updateChargeTimer();
    return this.charges;
  }

  public int getChargesClean() {
    return this.charges;
  }

  public boolean haveLeftCharge() {
    if (!CMI.getInstance().getSpawnerChargesManager().isChargesUse()) {
      return true;
    }

    this.updateSpawnerCharge();
    return this.charges > 0;
  }

  public boolean have() {
    this.updateSpawnerCharge();
    return this.SCharge != null;
  }

  public int getBonus() {
    return this.SCharge != null && this.SCharge.getBonus() > 0 ? this.SCharge.getBonus() : 0;
  }

  public boolean lowerCd() {
    if (this.SCharge != null
        && this.SCharge.getBonus() > 0
        && this.getCharges() < this.getMaxCharges()) {
      if (this.lastGive > 0L) {
        this.lastGive = this.lastGive - this.SCharge.getBonus() * 1000;
      }

      return true;
    } else {
      return false;
    }
  }

  public void setCharges(int value) {
    this.charges = value;
  }

  private void updateChargeTimer() {
    if (this.SCharge != null) {
      if (this.lastGive == 0L) {
        this.lastGive = System.currentTimeMillis();
      }

      long longValue = this.SCharge.getCooldown() * 1000L;
      long elapsedSinceLastGive = System.currentTimeMillis() - this.lastGive;
      int value = (int) (elapsedSinceLastGive / longValue);
      if (value > 0) {
        this.addCharge(value);
        long chargeAmount = longValue * value;
        this.lastGive += chargeAmount;
      }
    }
  }

  public String getLeftTime() {
    if (this.lastGive == 0L || this.charges == this.getMaxCharges()) {
      this.lastGive = System.currentTimeMillis();
    }

    if (this.getCharges() >= this.getMaxCharges()) {
      return "-";
    }

    long longValue =
        this.getLastGive() + this.getCooldown() * 1000 + 1000L - System.currentTimeMillis();
    return CMITimeManager.to24hourShort(longValue);
  }

  public boolean addCharge() {
    return this.addCharge(1);
  }

  public boolean addCharge(int value) {
    return this.addCharge(value, false);
  }

  public boolean addCharge(int value, boolean flag) {
    this.updateSpawnerCharge();
    if (flag) {
      this.charges += value;
    } else if (this.SCharge != null) {
      if (this.charges + value >= this.SCharge.getMaxCharges()) {
        this.charges = this.SCharge.getMaxCharges();
        return false;
      }

      this.charges += value;
    }

    return true;
  }

  public boolean takeCharge() {
    return this.takeCharge(1);
  }

  public boolean takeCharge(int value) {
    return this.takeCharge(value, false);
  }

  public boolean takeCharge(int value, boolean flag) {
    this.updateSpawnerCharge();
    this.updateChargeTimer();
    if (this.lastGive == 0L || this.charges >= this.getMaxCharges()) {
      this.lastGive = System.currentTimeMillis();
    }

    if (this.charges < 0 && !flag) {
      return false;
    } else {
      this.charges -= value;
      if (!flag && this.SCharge != null && this.charges < 0) {
        this.charges = 0;
        return false;
      } else {
        return true;
      }
    }
  }

  public int getMaxCharges() {
    this.updateSpawnerCharge();
    return this.SCharge == null ? 0 : this.SCharge.getMaxCharges();
  }

  public long getLastGive() {
    return this.lastGive;
  }

  public void setLastGive(long longValue) {
    this.lastGive = longValue;
  }

  public int getCooldown() {
    this.updateSpawnerCharge();
    return this.SCharge == null ? -1 : this.SCharge.getCooldown();
  }
}
