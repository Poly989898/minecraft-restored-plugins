package com.Zrips.CMI.Modules.Particl;

import com.Zrips.CMI.CMI;
import java.util.ArrayList;
import java.util.List;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import net.Zrips.CMILib.Version.Schedulers.CMITask;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class CMIVisualEffect {
  private Player targetPlayerLocation;
  private Location loc;
  CMITask task = null;
  private int interval = 1;
  private Long until = null;
  private CMIPEAnimationInterface effectAnimation = null;
  List<Player> players = new ArrayList<>();
  Long nextPlayerUpdate;

  public CMIVisualEffect(Player player, ParticleManager.CMIPresetAnimations cmiPresetAnimations) {
    this(player, cmiPresetAnimations.getInter(player));
  }

  public CMIVisualEffect(Player player, CMIPEAnimationInterface cmipeAnimationInterface) {
    if (cmipeAnimationInterface != null) {
      if (cmipeAnimationInterface.getCenter() != null) {
        this.loc = cmipeAnimationInterface.getCenter().clone();
      }

      this.effectAnimation = cmipeAnimationInterface;
      this.interval = cmipeAnimationInterface.getInterval();
    }

    this.setTargetPlayer(player);
  }

  public CMIVisualEffect(Location location, CMIPEAnimationInterface cmipeAnimationInterface) {
    this.loc = location;
    this.effectAnimation = cmipeAnimationInterface;
    if (cmipeAnimationInterface != null) {
      this.interval = cmipeAnimationInterface.getInterval();
    }
  }

  private Location getLocation() {
    if (this.loc != null) {
      return this.loc;
    } else {
      return this.getTargetPlayer() != null
          ? (this.getTargetPlayer().isOnline() ? this.getTargetPlayer().getLocation() : null)
          : null;
    }
  }

  public void show() {
    if (this.effectAnimation != null) {
      this.task =
          CMIScheduler.runTimerAsync(
              CMI.getInstance(),
              () -> {
                Location location = this.getLocation();
                if (location == null) {
                  this.stop();
                } else {
                  if (this.nextPlayerUpdate == null
                      || this.nextPlayerUpdate < System.currentTimeMillis()) {
                    this.players =
                        CMI.getInstance()
                            .getUtilManager()
                            .getPlayersFromRange(
                                this.getEffect().isHideWithVanish() ? this.getTargetPlayer() : null,
                                location,
                                32);
                    this.nextPlayerUpdate = System.currentTimeMillis() + 1000L;
                  }

                  if (!this.effectAnimation.isFixedLocation()
                      || this.effectAnimation.getCenter() == null) {
                    this.effectAnimation.setCenter(location);
                  }

                  this.visualize();
                  if (this.until == null || this.until < System.currentTimeMillis()) {
                    this.stop();
                  }
                }
              },
              1L,
              this.interval);
    }
  }

  public void show(Player player) {
    this.players.clear();
    if (player != null) {
      this.players.add(player);
    }

    this.task =
        CMIScheduler.runTimerAsync(
            CMI.getInstance(),
            () -> {
              Location location = this.getLocation();
              if (location == null) {
                this.stop();
              } else {
                if (!this.effectAnimation.isFixedLocation()
                    || this.effectAnimation.getCenter() == null) {
                  this.effectAnimation.setCenter(location);
                }

                this.visualize();
                if (this.until == null || this.until < System.currentTimeMillis()) {
                  this.stop();
                }
              }
            },
            0L,
            this.interval);
  }

  private void visualize() {
    CMIScheduler.runTaskAsynchronously(
        CMI.getInstance(),
        () -> {
          Location location = this.getLocation();
          if (location == null) {
            this.stop();
          } else if (this.effectAnimation == null) {
            this.stop();
          } else if (!this.effectAnimation.render(new ArrayList<>(this.players))) {
            this.stop();
          }
        });
  }

  public void stop() {
    if (this.task != null) {
      this.task.cancel();
      this.task = null;
      CMI.getInstance().getParticleManager().removePlayerEffects(this);
    }
  }

  public void pause() {}

  public void continueAnimation() {}

  public CMIPEAnimationInterface getEffect() {
    return this.effectAnimation;
  }

  public void setEffect(CMIPEAnimationInterface cmipeAnimationInterface) {
    this.effectAnimation = cmipeAnimationInterface;
  }

  public Player getTargetPlayer() {
    return this.targetPlayerLocation;
  }

  public void setTargetPlayer(Player player) {
    this.targetPlayerLocation = player;
    if (this.loc == null) {
      CMI.getInstance().getParticleManager().addPlayerEffect(this);
    }
  }

  public Long getUntil() {
    return this.until;
  }

  public void setUntil(Long newUntil) {
    this.until = newUntil;
  }

  public int getInterval() {
    return this.interval;
  }

  public void setInterval(int value) {
    this.interval = value;
  }
}
