package com.Zrips.CMI.Modules.PlayerCombat;

import com.Zrips.CMI.CMI;
import java.util.UUID;

public class CMIPlayerCombat {
  private long gotLastDamageAt = 0L;
  private long gotLastDamageFromPlayer = 0L;
  private long didLastDamageToPlayer = 0L;
  private long gotLastDamageFromEntity = 0L;
  private long didLastDamageToEntity = 0L;

  public Long getGotLastDamageAt() {
    return this.gotLastDamageAt;
  }

  public void setGotLastDamageAt(Long timeMillis) {
    this.gotLastDamageAt = timeMillis;
  }

  public boolean isInCombatWithPlayer() {
    if (CMI.getInstance().getPlayerCombatManager().getCombatTimer() <= 0) {
      return false;
    } else if (this.gotLastDamageFromPlayer == 0L && this.didLastDamageToPlayer == 0L) {
      return false;
    } else {
      return this.gotLastDamageFromPlayer != 0L
              && this.gotLastDamageFromPlayer
                      + CMI.getInstance().getPlayerCombatManager().getCombatTimer() * 1000L
                  > System.currentTimeMillis()
          ? true
          : this.didLastDamageToPlayer != 0L
              && this.didLastDamageToPlayer
                      + CMI.getInstance().getPlayerCombatManager().getCombatTimer() * 1000L
                  > System.currentTimeMillis();
    }
  }

  public long getGotLastDamageFromPlayer() {
    return this.gotLastDamageFromPlayer;
  }

  public void setGotLastDamageFromPlayer(Long timeMillis) {
    this.gotLastDamageFromPlayer = timeMillis;
  }

  public void setDidLastDamageToPlayer(Long timeMillis) {
    this.didLastDamageToPlayer = timeMillis;
  }

  public boolean isInCombat() {
    return this.isInCombatWithMob() || this.isInCombatWithPlayer();
  }

  public boolean isInCombatWithMob() {
    if (CMI.getInstance().getPlayerCombatManager().getCombatTimer() <= 0) {
      return false;
    } else if (this.gotLastDamageFromEntity == 0L && this.didLastDamageToEntity == 0L) {
      return false;
    } else {
      return this.gotLastDamageFromEntity != 0L
              && this.gotLastDamageFromEntity
                      + CMI.getInstance().getPlayerCombatManager().getCombatTimer() * 1000L
                  > System.currentTimeMillis()
          ? true
          : this.didLastDamageToEntity != 0L
              && this.didLastDamageToEntity
                      + CMI.getInstance().getPlayerCombatManager().getCombatTimer() * 1000L
                  > System.currentTimeMillis();
    }
  }

  public long getGotLastDamageFromMob() {
    return this.gotLastDamageFromEntity;
  }

  public void setGotLastDamageFromMob(Long timeMillis) {
    this.gotLastDamageFromEntity = timeMillis;
  }

  public void setDidLastDamageToMob(Long timeMillis) {
    this.didLastDamageToEntity = timeMillis;
  }

  public long getLeftCombatTime() {
    if (CMI.getInstance().getPlayerCombatManager().getCombatTimer() <= 0) {
      return 0L;
    }

    long longValue =
        Math.max(
            Math.max(this.gotLastDamageFromPlayer, this.didLastDamageToPlayer),
            Math.max(this.gotLastDamageFromEntity, this.didLastDamageToEntity));
    return longValue != 0L
            && longValue + CMI.getInstance().getPlayerCombatManager().getCombatTimer() * 1000L
                >= System.currentTimeMillis()
        ? longValue
            + CMI.getInstance().getPlayerCombatManager().getCombatTimer() * 1000L
            - System.currentTimeMillis()
        : 0L;
  }

  public static CMIPlayerCombat getRecord(UUID uuid) {
    return CMI.getInstance().getPlayerCombatManager().getCombatRecord(uuid);
  }
}
