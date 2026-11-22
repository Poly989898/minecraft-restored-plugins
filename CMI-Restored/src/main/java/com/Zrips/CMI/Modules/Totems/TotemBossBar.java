package com.Zrips.CMI.Modules.Totems;

import com.Zrips.CMI.Modules.Particl.CMIVisualEffect;
import org.bukkit.boss.BossBar;

public class TotemBossBar {
  private BossBar bar = null;
  private TotemManager.BossBarType type;
  private CMIVisualEffect effect;

  public TotemBossBar(BossBar bossBar, TotemManager.BossBarType bossBarType) {
    this.bar = bossBar;
    this.type = bossBarType;
  }

  public BossBar getBar() {
    return this.bar;
  }

  public void setBar(BossBar bossBar) {
    this.bar = bossBar;
  }

  public TotemManager.BossBarType getType() {
    return this.type;
  }

  public void setType(TotemManager.BossBarType bossBarType) {
    this.type = bossBarType;
  }

  public CMIVisualEffect getEffect() {
    return this.effect;
  }

  public void setEffect(CMIVisualEffect cmiVisualEffect) {
    this.effect = cmiVisualEffect;
  }
}
