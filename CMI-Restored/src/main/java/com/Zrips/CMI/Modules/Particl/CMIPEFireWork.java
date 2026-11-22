package com.Zrips.CMI.Modules.Particl;

import com.Zrips.CMI.CMI;
import java.lang.reflect.Method;
import java.util.List;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Builder;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Location;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;

public class CMIPEFireWork extends CMIPEAnimation {
  private static Method world_getHandle = null;
  private static Method nms_world_broadcastEntityEffect = null;
  private static Method firework_getHandle = null;
  private Color fadeFrom = null;
  private Color fadeTo = null;
  private Type type = null;
  private Boolean flicker = null;
  private Boolean trail = null;

  public CMIPEFireWork() {
    this.setDuration(0.0);
  }

  @Override
  public boolean render(List<Player> list) {
    Builder builder = FireworkEffect.builder();
    if (this.flicker != null) {
      builder.flicker(this.flicker);
    }

    if (this.trail != null) {
      builder.trail(this.trail);
    }

    if (this.fadeFrom != null) {
      builder.withColor(this.fadeFrom);
    } else {
      builder.withColor(Color.WHITE);
    }

    if (this.fadeTo != null) {
      builder.withFade(this.fadeTo);
    } else {
      builder.withFade(Color.WHITE);
    }

    if (this.type != null) {
      builder.with(this.type);
    } else {
      builder.with(Type.BALL);
    }

    FireworkEffect fireworkEffect = builder.build();

    try {
      ShotFirework(this.getCenter(), fireworkEffect);
    } catch (Exception exception) {
      exception.printStackTrace();
    }

    return true;
  }

  private static void ShotFirework(Location location, FireworkEffect fireworkEffect) {
    CMIScheduler.runTask(
        CMI.getInstance(),
        () -> {
          try {
            Firework firework = (Firework) location.getWorld().spawn(location, Firework.class);
            Object object = null;
            Object fireworkHandle = null;
            if (world_getHandle == null) {
              world_getHandle = getMethod(location.getWorld().getClass(), "getHandle");
              firework_getHandle = getMethod(firework.getClass(), "getHandle");
            }

            object = world_getHandle.invoke(location.getWorld(), null);
            fireworkHandle = firework_getHandle.invoke(firework, null);
            if (nms_world_broadcastEntityEffect == null) {
              nms_world_broadcastEntityEffect =
                  getMethod(object.getClass(), "broadcastEntityEffect");
            }

            FireworkMeta fireworkMeta = firework.getFireworkMeta();
            fireworkMeta.clearEffects();
            fireworkMeta.setPower(0);
            fireworkMeta.addEffect(fireworkEffect);
            firework.setFireworkMeta(fireworkMeta);

            try {
              nms_world_broadcastEntityEffect.invoke(object, fireworkHandle, (byte) 17);
            } catch (Exception exception) {
            }
          } catch (Exception exception2) {
            exception2.printStackTrace();
          }
        });
  }

  private static Method getMethod(Class<?> targetClass, String string) {
    Method[] methods;
    int value = (methods = targetClass.getMethods()).length;

    for (int index = 0; index < value; index++) {
      Method method = methods[index];
      if (method.getName().equals(string)) {
        return method;
      }
    }

    return null;
  }

  public Color getFadeFrom() {
    return this.fadeFrom;
  }

  public void setFadeFrom(Color color) {
    this.fadeFrom = color;
  }

  public Color getFadeTo() {
    return this.fadeTo;
  }

  public void setFadeTo(Color color) {
    this.fadeTo = color;
  }

  public Type getType() {
    return this.type;
  }

  public void setType(Type newType) {
    this.type = newType;
  }

  public Boolean getFlicker() {
    return this.flicker;
  }

  public void setFlicker(Boolean flickerEnabled) {
    this.flicker = flickerEnabled;
  }

  public Boolean getTrail() {
    return this.trail;
  }

  public void setTrail(Boolean trailEnabled) {
    this.trail = trailEnabled;
  }
}
