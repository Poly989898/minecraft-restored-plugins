package com.Zrips.CMI.Modules.Display;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.NBT.NMS;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;
import net.Zrips.CMILib.CMILib;
import net.Zrips.CMILib.Container.CMINumber;
import net.Zrips.CMILib.Container.CMIVector3D;
import net.Zrips.CMILib.Messages.CMIMessages;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.entity.Display.Billboard;
import org.bukkit.entity.Display.Brightness;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Transformation;
import org.joml.Vector3f;

public class CMIDisplay {
  CMIDisplayTransform transform;
  protected Location loc;
  protected boolean positionModified = true;
  protected Entity display;
  protected Object d = null;
  private int id = 0;
  private CMIDisplayType type;

  public CMIDisplay(CMIDisplayType cmiDisplayType, Location location) {
    if (cmiDisplayType != null && location != null) {
      this.type = cmiDisplayType;
      this.loc = location;

      try {
        NMS nms = CMI.getInstance().getNMS();
        this.d = nms.createDisplayHandle(cmiDisplayType, location);
        if (this.d != null) {
          this.display = nms.getDisplayBukkitEntity(this.d);
          if (Version.isMojangMappings()) {
            this.id = nms.getDisplayEntityId(this.d);
          } else if (this.display != null) {
            this.id = this.display.getEntityId();
          }
        } else if (!Version.isMojangMappings()) {
          CMIMessages.consoleMessage("Failed to determine display entity " + cmiDisplayType);
        }
      } catch (Throwable throwable) {
        throwable.printStackTrace();
      }
    }
  }

  public Entity getDisplayEntity() {
    return this.display;
  }

  public Transformation getTransformation() {
    return ((Display) this.display).getTransformation();
  }

  @Deprecated
  public void getTransformation(Transformation transformation) {
    this.setTransformation(transformation);
  }

  public void setTransformation(Transformation transformation) {
    if (this.display != null) {
      ((Display) this.display).setTransformation(transformation);
    }
  }

  public void setWidth(double number) {
    if (this.display != null) {
      Transformation transformation = ((Display) this.display).getTransformation();
      Vector3f vector3f = transformation.getScale();
      vector3f.x = (float) CMINumber.clamp(number, 0.01, 1000) + 1.0E-6F;
      ((Display) this.display).setTransformation(transformation);
    }
  }

  public void setHeight(double number) {
    if (this.display != null) {
      Transformation transformation = ((Display) this.display).getTransformation();
      Vector3f vector3f = transformation.getScale();
      vector3f.y = (float) CMINumber.clamp(number, 0.01, 1000) + 1.0E-6F;
      ((Display) this.display).setTransformation(transformation);
    }
  }

  public void setInterpolationDuration(int value) {
    ((Display) this.display).setInterpolationDuration(value);
  }

  public int getInterpolationDuration() {
    return ((Display) this.display).getInterpolationDuration();
  }

  public void setInterpolationDelay(int value) {
    ((Display) this.display).setInterpolationDelay(value);
  }

  public int getInterpolationDelay() {
    return ((Display) this.display).getInterpolationDelay();
  }

  public void setLocation(Location location) {
    this.loc = location;
    this.positionModified = true;
  }

  public float getRange() {
    return ((Display) this.display).getViewRange();
  }

  public void setRange(int value) {
    if (this.display != null) {
      ((Display) this.display).setViewRange(value);
    }
  }

  public void setBrightness(int value, int count) {
    if (this.display != null) {
      ((Display) this.display)
          .setBrightness(
              value < 0 && count < 0
                  ? null
                  : new Brightness(CMINumber.clamp(value, 0, 15), CMINumber.clamp(count, 0, 15)));
    }
  }

  public void setBillboard(Billboard billboard) {
    if (this.display != null) {
      ((Display) this.display).setBillboard(billboard);
    }
  }

  public void setOffset(CMIVector3D cmiVector3D) {
    this.modifyOffset(cmiVector3D);
  }

  private void modifyOffset(CMIVector3D cmiVector3D) {
    Transformation transformation = ((Display) this.display).getTransformation();
    Vector3f vector3f = transformation.getTranslation();
    vector3f.set(
        (float) cmiVector3D.getX(), (float) cmiVector3D.getY(), (float) cmiVector3D.getZ());
    ((Display) this.display).setTransformation(transformation);
  }

  public Billboard getBillboard() {
    return ((Display) this.display).getBillboard();
  }

  public Location getLocation() {
    return this.loc;
  }

  public Object getDisplay() {
    return this.d;
  }

  public void addAsPassenger(Player player, Entity entity) {
    this.addAsPassenger(Collections.singleton(player), entity);
  }

  public void addAsPassenger(Set<Player> set, Entity entity) {
    var list = entity.getPassengers();
    ArrayList<Integer> arrayList = new ArrayList();

    for (Entity entitySecond : list) {
      arrayList.add(entitySecond.getEntityId());
    }

    arrayList.add(this.getId());
    int[] values = arrayList.stream().mapToInt(object -> object).toArray();

    try {
      CMI.getInstance().getNMS().sendDisplayPassengersPacket(set, entity, values);
    } catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  public void show(Player player) {
    this.show(player, this.d);
  }

  private void show(Player player, Object value) {
    if (player != null
        && player.isOnline()
        && value != null
        && player.getWorld().equals(this.loc.getWorld())) {
      NMS nms = CMI.getInstance().getNMS();
      if (Version.isMojangMappings()) {
        try {
          nms.sendDisplaySpawnPacket(player, value, this.id, this.getLocation());
          nms.sendDisplayDataPacket(player, value, this.id);
          CMILib.getInstance()
              .getReflectionManager()
              .superficialEntityTeleport(player, value, this.getLocation());
        } catch (Throwable throwable) {
          throwable.printStackTrace();
        }
      } else {
        if (Version.isFolia()) {
          CMIScheduler.runAtLocation(
              CMI.getInstance(),
              this.getLocation(),
              () -> {
                try {
                  nms.sendDisplaySpawnPacket(player, value, this.id, this.getLocation());
                  if (!this.type.equals(CMIDisplayType.Interaction)) {
                    nms.sendDisplayDataPacket(player, value, this.id);
                  }
                } catch (Throwable throwable2) {
                  throwable2.printStackTrace();
                }

                CMILib.getInstance()
                    .getReflectionManager()
                    .superficialEntityTeleport(player, value, this.getLocation());
              });
        } else {
          try {
            nms.sendDisplaySpawnPacket(player, value, this.id, this.getLocation());
            if (!this.type.equals(CMIDisplayType.Interaction)) {
              nms.sendDisplayDataPacket(player, value, this.id);
            }
          } catch (Throwable throwable3) {
            throwable3.printStackTrace();
          }

          CMILib.getInstance()
              .getReflectionManager()
              .superficialEntityTeleport(player, value, this.getLocation());
        }
      }
    }
  }

  public void update(Player player) {
    this.update(player, this.d);
  }

  public void update(Player player, boolean flag) {
    this.update(player, this.d, flag);
  }

  private void update(Player player, Object value) {
    this.update(player, value, this.positionModified);
  }

  private void update(Player player, Object value, boolean flag) {
    if (player != null
        && player.isOnline()
        && value != null
        && player.getWorld().equals(this.loc.getWorld())) {
      NMS nms = CMI.getInstance().getNMS();
      if (Version.isMojangMappings()) {
        if (Version.isPaperBranch()) {
          try {
            if (!this.type.equals(CMIDisplayType.Interaction)) {
              nms.sendDisplayDataPacket(player, value, this.id);
            }

            if (flag) {
              CMILib.getInstance()
                  .getReflectionManager()
                  .superficialEntityTeleport(player, value, this.getLocation());
            }
          } catch (Throwable throwable) {
            throwable.printStackTrace();
          }

          this.positionModified = false;
        } else {
          CMIScheduler.runAtLocation(
              CMI.getInstance(),
              this.getLocation(),
              () -> {
                try {
                  if (!this.type.equals(CMIDisplayType.Interaction)) {
                    nms.sendDisplayDataPacket(player, value, this.id);
                  }

                  if (flag) {
                    CMILib.getInstance()
                        .getReflectionManager()
                        .superficialEntityTeleport(player, value, this.getLocation());
                  }
                } catch (Throwable throwable2) {
                  throwable2.printStackTrace();
                }
              });
          this.positionModified = false;
        }
      } else {
        if (Version.isFolia()) {
          CMIScheduler.runAtLocation(
              CMI.getInstance(),
              this.getLocation(),
              () -> {
                try {
                  if (!this.type.equals(CMIDisplayType.Interaction)) {
                    nms.sendDisplayDataPacket(player, value, this.id);
                  }

                  if (flag) {
                    CMILib.getInstance()
                        .getReflectionManager()
                        .superficialEntityTeleport(player, value, this.getLocation());
                  }
                } catch (Throwable throwable3) {
                  throwable3.printStackTrace();
                }
              });
        } else {
          try {
            if (!this.type.equals(CMIDisplayType.Interaction)) {
              nms.sendDisplayDataPacket(player, value, this.id);
              if (flag) {
                CMILib.getInstance()
                    .getReflectionManager()
                    .superficialEntityTeleport(player, value, this.getLocation());
              }
            }
          } catch (Throwable throwable4) {
            throwable4.printStackTrace();
          }
        }

        this.positionModified = false;
      }
    }
  }

  public void destroy(Player player) {
    if (player != null && player.isOnline()) {
      try {
        CMI.getInstance().getNMS().sendDisplayDestroyPacket(player, this.id);
      } catch (Throwable throwable) {
        throwable.printStackTrace();
      }
    }
  }

  public int getId() {
    return this.id;
  }
}
