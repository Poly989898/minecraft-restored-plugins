package com.Zrips.CMI.Modules.Chat;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Components.CMIComponentBase;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Modules.Display.CMITextDisplay;
import com.Zrips.CMI.Modules.Holograms.Animations.CMIHologramAnimationType;
import com.Zrips.CMI.Modules.Holograms.Animations.CMIHologramFadeAnimation;
import com.Zrips.CMI.Modules.Holograms.Animations.CMIHologramValueChanger;
import com.Zrips.CMI.Modules.PlayerOptions.PlayerOption;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import net.Zrips.CMILib.Container.CMINumber;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import net.Zrips.CMILib.Version.Schedulers.CMITask;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Display.Billboard;
import org.bukkit.entity.Player;
import org.bukkit.util.Transformation;
import org.joml.Vector3f;

public class CMIChatBubble {
  private CMITextDisplay tDisplay;
  private Player player;
  private Location originPoint;
  private CMIComponentBase text;
  private long startTime;
  private long endTime;
  private Set<Player> receivers;
  private CMITask schedId = null;
  private CompletableFuture<Void> initTask = null;
  CMIHologramFadeAnimation faneId = null;

  public CMIChatBubble(Player sourcePlayer, String string, Set<Player> set) {
    this(sourcePlayer, CMIComponentBase.quickAdd(string), set);
  }

  public CMIChatBubble(Player sourcePlayer, CMIComponentBase cmiComponentBase, Set<Player> set) {
    this.player = sourcePlayer;
    this.text = cmiComponentBase;
    this.receivers = new HashSet<>(set);
    this.receivers.remove(sourcePlayer);
    this.originPoint = this.generateLoc();
    this.startTime = System.currentTimeMillis();
    this.endTime =
        (long)
            (this.startTime
                + CMI.getInstance()
                        .getChatBubbleManager()
                        .getShowFor(cmiComponentBase.getTextOnly())
                    * 1000.0);
    Color color = CMI.getInstance().getChatBubbleManager().getBackgroundColor();
    this.faneId =
        new CMIHologramFadeAnimation(
            new CMIHologramValueChanger(
                0.0,
                CMI.getInstance().getChatBubbleManager().getScale(),
                CMIHologramValueChanger.increment(
                    0.0, CMI.getInstance().getChatBubbleManager().getScale(), 20, 0.2)));
    this.initialRecheckReceivers();
    this.initTask =
        CMIScheduler.runAtLocation(
            CMI.getInstance(), sourcePlayer.getLocation(), this::generateDisplay);
  }

  public void show() {
    this.fadeInTasker();
  }

  private void fadeInTasker() {
    if (this.schedId != null) {
      this.schedId.cancel();
      this.schedId = null;
    }

    this.initTask.thenAccept(
        ignored ->
            CMIScheduler.runAtLocation(CMI.getInstance(), this.generateLoc(), this::spawnIn)
                .thenAccept(
                    ignoredError -> {
                      this.tDisplay.addAsPassenger(this.receivers, this.player);
                      this.schedId =
                          CMIScheduler.runTimerAsync(
                              CMI.getInstance(),
                              () -> {
                                if (this.endTime < System.currentTimeMillis()
                                    || this.receivers.isEmpty()
                                    || !this.originPoint
                                        .getWorld()
                                        .equals(this.player.getWorld())) {
                                  this.remove();
                                  if (this.schedId != null) {
                                    this.schedId.cancel();
                                  }

                                  this.schedId = null;
                                } else if (this.tDisplay != null) {
                                  if (!this.faneId.get(CMIHologramAnimationType.Base).finished()) {
                                    CMIScheduler.runAtLocation(
                                        CMI.getInstance(),
                                        this.generateLoc(),
                                        () -> {
                                          this.updateLooks();

                                          for (Player viewer : this.receivers) {
                                            this.tDisplay.update(viewer, false);
                                          }
                                        });
                                  }

                                  this.faneId.update();
                                }
                              },
                              0L,
                              1L);
                    }));
  }

  public CompletableFuture<Void> delete() {
    if (this.schedId != null) {
      this.schedId.cancel();
      this.schedId = null;
    }

    return this.remove();
  }

  private CompletableFuture<Void> remove() {
    if (this.tDisplay == null) {
      return CompletableFuture.completedFuture(null);
    }

    this.receivers.forEach(viewer -> this.tDisplay.destroy(viewer));
    return CMIScheduler.runAtLocation(
        CMI.getInstance(),
        this.tDisplay.getLocation(),
        () -> {
          this.tDisplay.getDisplayEntity().leaveVehicle();
          this.tDisplay.getDisplayEntity().remove();
        });
  }

  private Location generateLoc() {
    Location location = this.player.getLocation().clone().add(0.0, 1.8, 0.0);
    location.setPitch(0.0F);
    location.setYaw(0.0F);
    return location;
  }

  private void spawnIn() {
    for (Player viewer : this.receivers) {
      this.tDisplay.show(viewer);
    }
  }

  private void initialRecheckReceivers() {
    var iterator = this.receivers.iterator();

    while (iterator.hasNext()) {
      Player viewer = (Player) iterator.next();
      if (!CMI.getInstance()
              .getChatBubbleManager()
              .inRange(viewer.getLocation(), this.player.getLocation())
          || !viewer.canSee(this.player)
          || !CMIUser.getUser(viewer).getOptionState(PlayerOption.chatbubble)) {
        if (this.tDisplay != null) {
          this.tDisplay.destroy(viewer);
        }

        iterator.remove();
      }
    }
  }

  private void updateLooks() {
    if (this.tDisplay != null) {
      this.tDisplay.setWidth(this.faneId.getCurrent(CMIHologramAnimationType.Base));
      this.tDisplay.setHeight(this.faneId.getCurrent(CMIHologramAnimationType.Base));
      if (CMI.getInstance().getChatBubbleManager().getBackgroundColor() != null) {
        Color color = CMI.getInstance().getChatBubbleManager().getBackgroundColor();
        color =
            Color.fromARGB(
                CMINumber.clamp(
                    (int) (this.faneId.getCurrent(CMIHologramAnimationType.Base) * 255.0), 0, 255),
                color.getRed(),
                color.getGreen(),
                color.getBlue());
        this.tDisplay.setBackgroundColor(color);
      }

      this.tDisplay.setTextOpacity(
          (byte)
              CMINumber.clamp(
                  (int) (this.faneId.getCurrent(CMIHologramAnimationType.Base) * 255.0), 0, 255));
    }
  }

  private void generateDisplay() {
    this.tDisplay = new CMITextDisplay(this.originPoint);
    this.tDisplay.setText(this.text);
    this.tDisplay.setLineWidth(CMI.getInstance().getChatBubbleManager().getLineWidth());
    this.tDisplay.setBillboard(Billboard.VERTICAL);
    this.tDisplay.setRange(32);
    this.updateLooks();
    this.tDisplay.setShadowed(CMI.getInstance().getChatBubbleManager().isShadow());
    Transformation transformation = this.tDisplay.getTransformation();
    Vector3f vector3f = transformation.getTranslation();
    vector3f.add(0.0F, (float) (CMI.getInstance().getChatBubbleManager().getOffset() - 1.7F), 0.0F);
    this.tDisplay.getTransformation(transformation);
  }

  public CMITextDisplay getDisplay() {
    return this.tDisplay;
  }
}
