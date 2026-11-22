package com.Zrips.CMI.Modules.Holograms;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Modules.Holograms.Animations.CMIHologramAnimationCycle;
import com.Zrips.CMI.Modules.Holograms.Animations.CMIHologramAnimationType;
import com.Zrips.CMI.Modules.Holograms.Animations.CMIHologramFadeAnimation;
import com.Zrips.CMI.Modules.Holograms.Animations.CMIHologramValueChanger;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import java.util.UUID;
import java.util.function.Consumer;
import net.Zrips.CMILib.Container.CMINumber;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import net.Zrips.CMILib.Version.Schedulers.CMITask;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public class CMIHologramUserData {
  private int page = 1;
  private boolean skipPageChange = false;
  private CMIHologramBatch batch = null;
  private CMIHologramType displayType = null;
  private Consumer<CMIHologramAnimationCycle> consumer = null;
  private boolean hidden = false;
  private UUID uuid = null;
  private int tick = 0;
  private int steps = 10;
  private CMIHologramFadeAnimation fadeInAnimation =
      new CMIHologramFadeAnimation(new CMIHologramValueChanger().increment(0.03));
  private CMIHologramFadeAnimation fadeOutAnimation =
      new CMIHologramFadeAnimation(new CMIHologramValueChanger().increment(-0.03));
  CMITask animationTasker = null;

  public CMIHologramUserData(UUID hologramUuid) {
    this.uuid = hologramUuid;
  }

  public int getPage() {
    return this.page;
  }

  public CMIHologramUserData setPage(int value) {
    this.page = CMINumber.clamp(value, 1, Integer.MAX_VALUE);
    return this;
  }

  public boolean isSkipPageChange() {
    return this.skipPageChange;
  }

  public CMIHologramUserData setSkipPageChange(boolean flag) {
    this.skipPageChange = flag;
    return this;
  }

  public boolean isFadeAnimationRunning() {
    return this.fadeInAnimation != null && !this.fadeInAnimation.finished()
        || this.fadeOutAnimation != null && !this.fadeOutAnimation.finished();
  }

  public boolean isFadeAnimationActive() {
    return this.fadeInAnimation != null || this.fadeOutAnimation != null;
  }

  public CMIHologramFadeAnimation getFadeInAnimation() {
    return this.fadeInAnimation;
  }

  public void setFadeInAnimation(CMIHologramFadeAnimation cmiHologramFadeAnimation) {
    this.fadeInAnimation = cmiHologramFadeAnimation;
  }

  public void resetFadeInAnimation() {
    this.fadeInAnimation = new CMIHologramFadeAnimation(new CMIHologramValueChanger(1.0, 1.0, 0.0));
  }

  public CMIHologramFadeAnimation getFadeOutAnimation() {
    return this.fadeOutAnimation;
  }

  public void setFadeOutAnimation(CMIHologramFadeAnimation cmiHologramFadeAnimation) {
    this.fadeOutAnimation = cmiHologramFadeAnimation;
  }

  public void initializeFadeInAnimation(double number) {
    this.stopFadeAnimation();
    double stepValue = number / this.getSteps();
    this.fadeInAnimation =
        new CMIHologramFadeAnimation(new CMIHologramValueChanger(0.0, number, stepValue));
  }

  public void initializeFadeOutAnimation(double number) {
    this.stopFadeAnimation();
    double stepValue = number / this.getSteps();
    this.fadeOutAnimation =
        new CMIHologramFadeAnimation(new CMIHologramValueChanger(number, 0.0, -stepValue));
  }

  public double getAnimationCurrentValue(CMIHologramAnimationType cmiHologramAnimationType) {
    if (this.getFadeInAnimation() != null) {
      return this.getFadeInAnimation().get(cmiHologramAnimationType).getCurrent();
    } else {
      return this.getFadeOutAnimation() != null
          ? this.getFadeOutAnimation().get(cmiHologramAnimationType).getCurrent()
          : 1.0;
    }
  }

  public void stopFadeAnimation() {
    this.cancelFadeAnimation();
    this.setFadeInAnimation(null);
    this.setFadeOutAnimation(null);
  }

  public void cancelFadeAnimation() {
    if (this.animationTasker != null) {
      this.animationTasker.cancel();
      this.animationTasker = null;
    }

    if (this.consumer != null) {
      this.consumer.accept(CMIHologramAnimationCycle.Canceled);
      this.consumer = null;
    }
  }

  public CMIHologramUserData startFadeAnimation(
      CMIHologram cmiHologram,
      UUID hologramUuid,
      Consumer<CMIHologramAnimationCycle> animationConsumer) {
    if (this.consumer != null) {
      this.consumer.accept(CMIHologramAnimationCycle.Canceled);
    }

    this.consumer = animationConsumer;
    if (this.animationTasker == null
        && (this.getFadeInAnimation() != null || this.getFadeOutAnimation() != null)) {
      if (!CMI.getInstance().isEnabled()) {
        return this;
      }

      this.animationTasker =
          CMIScheduler.runTimerAsync(CMI.getInstance(), () -> this.runAnim(cmiHologram), 1L, 1L);
      return this;
    } else {
      return this;
    }
  }

  private void runAnim(CMIHologram cmiHologram) {
    CMIHologramFadeAnimation cmiHologramFadeAnimation = this.getFadeInAnimation();
    if (cmiHologramFadeAnimation != null) {
      if (!cmiHologramFadeAnimation.finished()
          && cmiHologram.getPlayersFromVisibilityRange().contains(this.uuid)) {
        cmiHologramFadeAnimation.update();
        if (this.consumer != null) {
          this.consumer.accept(CMIHologramAnimationCycle.Running);
        }

        cmiHologram.requestUpdate(
            this.uuid,
            CMIHologramUpdateType.Text,
            CMIHologramUpdateType.Icon,
            CMIHologramUpdateType.Billboard);
      } else {
        if (this.consumer != null) {
          this.consumer.accept(CMIHologramAnimationCycle.Finished);
          this.consumer = null;
        }

        this.cancelFadeAnimation();
        cmiHologram.requestUpdate(
            this.uuid,
            CMIHologramUpdateType.Text,
            CMIHologramUpdateType.Icon,
            CMIHologramUpdateType.Billboard);
      }
    } else {
      CMIHologramFadeAnimation fadeOutAnim = this.getFadeOutAnimation();
      if (fadeOutAnim != null) {
        if (fadeOutAnim.finished()) {
          if (this.consumer != null) {
            this.consumer.accept(CMIHologramAnimationCycle.Finished);
            this.consumer = null;
          }

          this.cancelFadeAnimation();
          return;
        }

        fadeOutAnim.update();
        if (this.consumer != null) {
          this.consumer.accept(CMIHologramAnimationCycle.Running);
        }
      }
    }
  }

  @Nullable
  public CMIHologramBatch getHologramBatch() {
    return this.batch;
  }

  public CMIHologramUserData setHologramBatch(CMIHologramBatch cmiHologramBatch) {
    this.batch = cmiHologramBatch;
    return this;
  }

  @Nullable
  public CMIHologramType getHologramType() {
    if (this.displayType == null) {
      this.displayType = this.recheckType();
    }

    return this.displayType;
  }

  private CMIHologramType recheckType() {
    if (Version.isCurrentLower(Version.v1_20_R1)) {
      return CMIHologramType.ArmorStand;
    }

    Player player = CMIUser.getOnlinePlayer(this.uuid);
    if (player == null) {
      return CMIHologramType.Auto;
    }

    boolean flag = PermissionsManager.CMIPerm.hologram_seeold.hasSetPermission(player);
    return !flag ? CMIHologramType.Auto : CMIHologramType.ArmorStand;
  }

  public CMIHologramUserData setHologramType(@Nullable CMIHologramType cmiHologramType) {
    if (Version.isCurrentLower(Version.v1_20_R1)) {
      this.displayType = CMIHologramType.ArmorStand;
    } else {
      this.displayType = cmiHologramType;
    }

    return this;
  }

  public boolean isHidden() {
    return this.hidden;
  }

  public CMIHologramUserData setHidden(boolean flag) {
    this.hidden = flag;
    return this;
  }

  public int getTick() {
    return this.tick;
  }

  public CMIHologramUserData setTick(int value) {
    this.tick = value;
    return this;
  }

  public int incrementTick() {
    this.tick++;
    return this.tick;
  }

  public double getCurrentAutoRotateExtraAngle(double number) {
    return number / 100.0 * this.getTick();
  }

  public int getSteps() {
    return this.steps;
  }

  public void setSteps(int value) {
    this.steps = value;
  }
}
