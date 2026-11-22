package net.Zrips.CMILib.BossBar;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import javax.annotation.Nullable;
import net.Zrips.CMILib.CMILib;
import net.Zrips.CMILib.Colors.CMIChatColor;
import net.Zrips.CMILib.Container.CMINumber;
import net.Zrips.CMILib.Locale.Snd;
import net.Zrips.CMILib.Time.CMITimeManager;
import net.Zrips.CMILib.Version.Schedulers.CMITask;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

public class BossBarInfo {
  private Player player;
  private Double percentage = null;
  private Double adjustPerc = null;
  private Integer keepFor = 60;
  private Integer auto = null;
  private BossBar bar;
  private BarColor startingColor = null;
  private BarStyle style = null;
  private CMITask autoScheduler = null;
  private CMITask hideSheduler = null;
  private String nameOfBar;
  private String titleOfBar = "";
  private boolean withPlaceholder = false;
  private List<String> cmds = null;
  private boolean global = false;
  private boolean makeVisible = false;
  private long started = 0L;
  private boolean translateColors = true;
  private List<CMIChatColor> colors = null;
  private int colorChangeIntervalTicks = 20;
  private long nextColorChange = 0L;
  boolean outdatedFormat = true;

  public BossBarInfo clone(Player player) {
    BossBarInfo barInfo = new BossBarInfo(player, this.nameOfBar);
    barInfo.percentage = this.percentage;
    barInfo.adjustPerc = this.adjustPerc;
    barInfo.keepFor = this.keepFor;
    barInfo.auto = this.auto;
    barInfo.bar = this.bar;
    barInfo.startingColor = this.startingColor;
    barInfo.style = this.style;
    barInfo.nameOfBar = this.nameOfBar;
    barInfo.translateColors = this.translateColors;
    barInfo.titleOfBar = this.titleOfBar;
    barInfo.withPlaceholder =
        CMILib.getInstance().getPlaceholderAPIManager().containsPlaceHolder(this.titleOfBar);
    barInfo.cmds = this.cmds;
    barInfo.global = this.global;
    barInfo.colors = this.colors == null ? null : new ArrayList<>(this.colors);
    barInfo.colorChangeIntervalTicks = this.colorChangeIntervalTicks;
    return barInfo;
  }

  public BossBarInfo(String nameOfBar) {
    this(null, nameOfBar, null);
  }

  public BossBarInfo(Player player, String nameOfBar) {
    this(player, nameOfBar, null);
  }

  public BossBarInfo(Player player, String nameOfBar, BossBar bar) {
    this.player = player;
    this.nameOfBar = nameOfBar;
    this.bar = bar;
    this.started = System.currentTimeMillis();
  }

  public void setHideScheduler(CMITask cmiTask) {
    this.cancelHideScheduler();
    this.hideSheduler = cmiTask;
  }

  public synchronized void cancelAutoScheduler() {
    if (this.autoScheduler != null) {
      this.autoScheduler.cancel();
      this.autoScheduler = null;
    }
  }

  public synchronized void cancelHideScheduler() {
    if (this.hideSheduler != null) {
      this.hideSheduler.cancel();
      this.hideSheduler = null;
    }
  }

  public void remove() {
    this.cancelAutoScheduler();
    this.cancelHideScheduler();
    if (this.bar != null) {
      this.bar.setVisible(false);
    }

    if (this.player != null) {
      CMILib.getInstance().getBossBarManager().removeBossBar(this.player, this);
    }
  }

  @Nullable
  public Player getPlayer() {
    return this.player;
  }

  public BossBar getBar() {
    return this.bar;
  }

  public Double getPercentage() {
    if (this.percentage == null) {
      this.percentage = 0.0;
    }

    return CMINumber.normalize(this.percentage);
  }

  public void setPercentage(double max, double current) {
    if (max == 0.0) {
      max = 1.0;
    }

    current = current * 100.0 / max / 100.0;
    this.setPercentage(current);
  }

  public void setPercentage(Double percentage) {
    if (percentage != null) {
      percentage = CMINumber.normalize(percentage);
      if (Double.isNaN(percentage) || Double.isInfinite(percentage)) {
        if (this.adjustPerc != null && this.adjustPerc > 0.0) {
          percentage = 0.0;
        } else {
          percentage = 1.0;
        }
      }
    }

    this.percentage = percentage;
  }

  public String getNameOfBar() {
    if (this.nameOfBar == null) {
      this.nameOfBar = "CmiBossbar" + new Random().nextInt(Integer.MAX_VALUE);
    }

    return this.nameOfBar;
  }

  public void setNameOfBar(String nameOfBar) {
    this.nameOfBar = nameOfBar;
  }

  public Integer getKeepFor() {
    return this.keepFor == null ? 30 : this.keepFor;
  }

  public void setKeepForTicks(Integer keepFor) {
    if (keepFor != null) {
      this.keepFor = keepFor;
    }
  }

  public String getTitleOfBarClean() {
    return this.titleOfBar == null ? "" : this.titleOfBar;
  }

  public String getTitleOfBar() {
    if (this.titleOfBar != null && this.titleOfBar.contains("[autoTimeLeft]")) {
      return this.percentage != null && this.adjustPerc != null && this.auto != null
          ? this.getDynamicColor()
              + this.titleOfBar.replace(
                  "[autoTimeLeft]", CMITimeManager.to24hourShort(this.getLeftDuration(), false))
          : this.getDynamicColor()
              + this.titleOfBar.replace("[autoTimeLeft]", CMITimeManager.to24hourShort(0L, false));
    } else {
      return this.titleOfBar == null ? "" : this.getDynamicColor() + this.titleOfBar;
    }
  }

  public String getTitleOfBar(Player player) {
    if (player == null) {
      return this.getTitleOfBar();
    }

    String t = this.getTitleOfBar();
    if (this.isWithPlaceholder()) {
      t = CMILib.getInstance().getPlaceholderAPIManager().updatePlaceHolders(player, t);
    }

    return t == null ? "" : (this.isTranslateColors() ? CMIChatColor.colorize(t) : t);
  }

  private String getDynamicColor() {
    if (this.colors != null && !this.colors.isEmpty()) {
      if (this.nextColorChange > System.currentTimeMillis()) {
        return this.colors.get(this.colors.size() - 1).toString();
      }

      this.nextColorChange = System.currentTimeMillis() + this.colorChangeIntervalTicks * 50L;
      CMIChatColor c = this.colors.remove(0);
      this.colors.add(c);
      return c.toString();
    } else {
      return "";
    }
  }

  public long getLeftDuration() {
    Long mili = 0L;
    if (this.percentage != null && this.adjustPerc != null && this.auto != null) {
      double leftTicks =
          this.percentage / (this.adjustPerc < 0.0 ? -this.adjustPerc : this.adjustPerc);
      Long totalTicks = (long) (leftTicks * (this.auto < 0 ? -this.auto : this.auto));
      mili = totalTicks * 50L;
    }

    return mili;
  }

  public void setTitleOfBar(String titleOfBar) {
    if (titleOfBar != null && !titleOfBar.isEmpty()) {
      this.titleOfBar = titleOfBar;
    } else {
      this.titleOfBar = null;
    }

    this.withPlaceholder =
        CMILib.getInstance().getPlaceholderAPIManager().containsPlaceHolder(titleOfBar);
  }

  public void setBar(BossBar bar) {
    this.bar = bar;
  }

  public BarColor getColor() {
    return this.startingColor;
  }

  public void setColor(BarColor startingColor) {
    this.outdatedFormat =
        !this.outdatedFormat
            ? !Objects.equals(this.startingColor, startingColor)
            : this.outdatedFormat;
    this.startingColor = startingColor;
  }

  public Double getAdjustPerc() {
    return this.adjustPerc;
  }

  public void setAdjustPerc(Double adjustPerc) {
    this.adjustPerc = adjustPerc;
  }

  public BarStyle getStyle() {
    return this.style;
  }

  public void setStyle(BarStyle style) {
    this.outdatedFormat =
        !this.outdatedFormat ? !Objects.equals(this.style, style) : this.outdatedFormat;
    this.style = style;
  }

  public void setPlayer(Player player) {
    this.player = player;
  }

  public CMITask getHideScheduler() {
    return this.hideSheduler;
  }

  public Integer getAuto() {
    return CMINumber.clamp(
        this.auto == null ? (this.withPlaceholder ? 60 : this.getKeepFor()) : this.auto, 1, 999);
  }

  public void setAuto(Integer auto) {
    this.cancelAutoScheduler();
    this.auto = auto;
  }

  public CMITask getAutoId() {
    return this.autoScheduler;
  }

  public void setAutoId(CMITask cmiTask) {
    this.autoScheduler = cmiTask;
  }

  public List<String> getCommands() {
    return this.cmds;
  }

  public List<String> getCommands(Player player) {
    Snd snd = new Snd();
    snd.setSender(player);
    snd.setTarget(player);
    return CMILib.getInstance().getLM().updateSnd(snd, new ArrayList<>(this.cmds));
  }

  public void setCmds(List<String> cmds) {
    this.cmds = cmds;
  }

  public boolean stillRunning() {
    if (this.getKeepFor() < 0) {
      return true;
    } else if (this.getPercentage() < 1.0
        && this.getAdjustPerc() != null
        && this.getAdjustPerc() > 0.0) {
      return true;
    } else if (this.getPercentage() > 0.0
        && this.getAdjustPerc() != null
        && this.getAdjustPerc() < 0.0) {
      return true;
    } else if (this.getPercentage() <= 0.0
        && this.getAdjustPerc() != null
        && this.getAdjustPerc() < 0.0) {
      return false;
    } else if (this.getPercentage() >= 0.0
        && this.getAdjustPerc() != null
        && this.getAdjustPerc() > 0.0) {
      return false;
    } else {
      return this.getAdjustPerc() == null
              && this.getKeepFor() > 0
              && this.getStarted() > 0L
              && System.currentTimeMillis() < this.getStarted() + this.getKeepFor().intValue() * 50L
          ? true
          : this.getAdjustPerc() == null && this.getKeepFor() < 0
              || this.getPercentage() != null
                  && this.getPercentage() <= 0.0
                  && this.getAdjustPerc() != null
                  && this.getAdjustPerc() < 0.0
              || this.getPercentage() != null
                  && this.getPercentage() >= 1.0
                  && this.getAdjustPerc() != null
                  && this.getAdjustPerc() > 0.0;
    }
  }

  public boolean isGlobal() {
    return this.global;
  }

  public void setGlobal(boolean global) {
    this.global = global;
  }

  public boolean isMakeVisible() {
    return this.makeVisible;
  }

  public void setMakeVisible(boolean makeVisible) {
    this.makeVisible = makeVisible;
  }

  public long getStarted() {
    return this.started;
  }

  public void setStarted(long started) {
    this.started = started;
  }

  public void setSeconds(int time) {
    double change = 100.0 / (time * 20.0) / 100.0;
    this.setAdjustPerc(change);
    if (time < 0) {
      this.setPercentage(1.0);
    } else {
      this.setPercentage(0.0);
    }

    this.setAuto(1);
  }

  public boolean isWithPlaceholder() {
    return this.withPlaceholder;
  }

  public boolean isTranslateColors() {
    return this.translateColors;
  }

  public void setTranslateColors(boolean translateColors) {
    this.translateColors = translateColors;
  }

  public void updateCycle() {}

  public List<CMIChatColor> getColors() {
    return this.colors;
  }

  public void setColors(List<CMIChatColor> colors) {
    this.colors = colors;
  }

  public int getColorChangeIntervalTicks() {
    return this.colorChangeIntervalTicks;
  }

  public void setColorChangeIntervalTicks(int colorChangeIntervalTicks) {
    this.colorChangeIntervalTicks = colorChangeIntervalTicks;
  }

  public boolean timerRunOut() {
    return true;
  }
}
