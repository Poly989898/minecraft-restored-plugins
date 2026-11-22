package net.Zrips.CMILib.Sounds;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import javax.annotation.Nonnull;
import net.Zrips.CMILib.CMILib;
import net.Zrips.CMILib.Messages.CMIMessages;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class CMISound {
  private static HashMap<String, Sound> soundsByname = new HashMap<>();
  private String rawName = null;
  private Sound sound = null;
  private float pitch = 1.0F;
  private float volume = 1.0F;
  private boolean enabled = true;

  public CMISound(String name) {
    this(name, 1.0F, 1.0F);
  }

  @Nonnull
  public static List<String> getSoundNames() {
    return new ArrayList<>(soundsByname.keySet());
  }

  public CMISound(String name, float volume, float pitch) {
    if (name != null && !name.isEmpty()) {
      if (name.contains(":")) {
        String[] split = name.split(":");
        name = split[0];
        if (split.length > 1) {
          try {
            volume = Float.parseFloat(split[1]);
          } catch (Exception var7) {
          }
        }

        if (split.length > 2) {
          try {
            pitch = Float.parseFloat(split[2]);
          } catch (Exception var6) {
          }
        }
      }

      this.volume = volume;
      this.pitch = pitch;
      name = name.toLowerCase().replace("_", "").replace(".", "");
      this.rawName = name;
      this.sound = soundsByname.get(name);
      if (this.sound == null) {
        for (Entry<String, Sound> one : soundsByname.entrySet()) {
          if (one.getKey().contains(name)) {
            this.sound = one.getValue();
          }
        }
      }

      if (this.sound != null) {
        this.rawName = getSoundName(this.sound);
      }
    }
  }

  private static String getSoundName(Sound sound) {
    if (Version.isCurrentEqualOrHigher(Version.v1_21_R2)) {
      if (Version.isPaperBranch()) {
        try {
          return sound.getKey().getKey();
        } catch (Throwable e) {
          return sound.toString();
        }
      } else {
        return sound.toString();
      }
    } else {
      try {
        return (String) Sound.class.getMethod("toString").invoke(sound);
      } catch (Throwable e) {
        e.printStackTrace();
        return sound.toString();
      }
    }
  }

  public Sound getSound() {
    return this.sound;
  }

  public CMISound setSound(Sound sound) {
    this.sound = sound;
    return this;
  }

  public CMISound play(Location loc) {
    if (!this.enabled) {
      return this;
    }

    if (this.sound == null) {
      return this;
    }

    CMIScheduler.runTask(
        CMILib.getInstance(),
        () -> loc.getWorld().playSound(loc, this.sound, this.volume, this.pitch));
    return this;
  }

  public CMISound play(Player player) {
    if (!this.enabled) {
      return this;
    } else if (this.sound == null) {
      return this;
    } else {
      return player == null ? this : this.play(player, player.getLocation());
    }
  }

  public CMISound play(Player player, Location loc) {
    if (!this.enabled) {
      return this;
    } else if (this.sound == null) {
      return this;
    } else if (player != null && loc != null) {
      CMILib.getInstance()
          .getReflectionManager()
          .playSound(player, loc, this.sound, this.volume, this.pitch);
      return this;
    } else {
      return this;
    }
  }

  public float getPitch() {
    return this.pitch;
  }

  public CMISound setPitch(float pitch) {
    this.pitch = pitch;
    return this;
  }

  public float getVolume() {
    return this.volume;
  }

  public CMISound setVolume(float volume) {
    this.volume = volume;
    return this;
  }

  public String getRawName() {
    return this.rawName;
  }

  @Override
  public String toString() {
    return this.sound == null
        ? "Unknown"
        : this.getRawName() + ":" + fmt(this.volume) + ":" + fmt(this.pitch);
  }

  private static String fmt(float d) {
    return String.format("%s", d);
  }

  public boolean isEnabled() {
    return this.enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  static {
    try {
      Class<?> c = Class.forName("org.bukkit.Sound");
      Object[] sounds = (Object[]) c.getMethod("values").invoke(c);

      for (Object sound : sounds) {
        if (sound != null) {
          try {
            String name = getSoundName((Sound) sound);
            soundsByname.put(name.toLowerCase().replace("_", "").replace(".", ""), (Sound) sound);
          } catch (Exception e) {
            CMIMessages.consoleMessage(
                "&4Failed to recognize biome by (" + sound.toString() + ") name. Skipping.");
          }
        }
      }
    } catch (Throwable e) {
      e.printStackTrace();
    }
  }
}
