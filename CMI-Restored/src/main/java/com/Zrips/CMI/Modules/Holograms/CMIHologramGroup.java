package com.Zrips.CMI.Modules.Holograms;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Modules.Holograms.Settings.CMIHologramSettings;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import net.Zrips.CMILib.Container.CMILocation;
import org.bukkit.Location;
import org.bukkit.util.Vector;

public class CMIHologramGroup {
  private static HashMap<String, Set<CMIHologram>> hologramsByGroup = new HashMap<>();

  public static void clear() {
    hologramsByGroup.clear();
  }

  public static Set<CMIHologram> get(String string) {
    return hologramsByGroup.getOrDefault(string.toLowerCase(), new HashSet<>());
  }

  public static Set<CMIHologram> remove(String string) {
    return hologramsByGroup.remove(string.toLowerCase());
  }

  public static void add(CMIHologram cmiHologram) {
    add(cmiHologram, cmiHologram.getSettings().getGroup());
  }

  public static void add(CMIHologram cmiHologram, String string) {
    hologramsByGroup.computeIfAbsent(string.toLowerCase(), key -> new HashSet<>()).add(cmiHologram);
  }

  public static void updateHologramGroup(CMIHologram cmiHologram, String string) {
    CMIHologramSettings cmiHologramSettings = cmiHologram.getSettings();
    if (cmiHologramSettings.getGroup() != null) {
      var set = get(cmiHologramSettings.getGroup().toLowerCase());
      if (set != null) {
        set.remove(cmiHologram);
        if (set.isEmpty()) {
          remove(cmiHologramSettings.getGroup().toLowerCase());
        }
      }
    }

    cmiHologramSettings.setGroup(string);
    if (string != null) {
      add(cmiHologram);
    }
  }

  public static Set<CMIHologram> getHologramsByGroup(String string) {
    return string == null ? new HashSet<>() : new HashSet<>(get(string));
  }

  public static Location getCenterOfGroup(String string) {
    var set = CMI.getInstance().getHologramManager().getHologramsByGroup(string);
    if (set.isEmpty()) {
      return null;
    }

    Optional optional = CMI.getInstance().getHologramManager().getCenterLocation(set);
    return !optional.isPresent() ? null : (Location) optional.get();
  }

  public static void relocateHologramGroup(Location location, String string) {
    var set = CMI.getInstance().getHologramManager().getHologramsByGroup(string);
    if (!set.isEmpty()) {
      Optional optional = CMI.getInstance().getHologramManager().getCenterLocation(set);
      if (optional.isPresent()) {
        Vector vector = ((Location) optional.get()).toVector();

        for (CMIHologram cmiHologram : set) {
          Vector vectorSecond = cmiHologram.getLocation().toVector().subtract(vector);
          Location locationSecond = location.clone();
          locationSecond.add(vectorSecond);
          cmiHologram.setLocation(locationSecond);
          cmiHologram.reset();
        }

        CMI.getInstance().getHologramManager().save();
      }
    }
  }

  public static void shiftHologramGroup(Vector vector, String string) {
    var set = CMI.getInstance().getHologramManager().getHologramsByGroup(string);
    if (!set.isEmpty()) {
      for (CMIHologram cmiHologram : set) {
        cmiHologram.setLocation(cmiHologram.getLocation().add(vector));
        cmiHologram.requestFullUpdate();
      }

      CMI.getInstance().getHologramManager().save();
    }
  }

  public static void rotateHologramGroup(int value, int count, String string, Location location) {
    var set = CMI.getInstance().getHologramManager().getHologramsByGroup(string);
    if (!set.isEmpty()) {
      double number = Math.toRadians(value);
      double amount = Math.toRadians(count);
      double amountSecond = Math.cos(number);
      double amountThird = Math.sin(number);
      double amountFourth = Math.cos(amount);
      double amountFifth = Math.sin(amount);

      for (CMIHologram cmiHologram : set) {
        CMILocation cmiLocation = cmiHologram.getLocation();
        Vector vector = cmiLocation.toVector().subtract(location.toVector());
        if (vector.lengthSquared() == 0.0) {
          cmiHologram
              .getSettings()
              .setYaw((cmiHologram.getSettings().getYaw() + value + 360.0) % 360.0);
          cmiHologram
              .getSettings()
              .setPitch((cmiHologram.getSettings().getPitch() + count + 360.0) % 360.0);
          cmiHologram.requestFullUpdate();
        } else {
          double amountSixth = vector.getX() * amountSecond - vector.getZ() * amountThird;
          double amountSeventh = vector.getX() * amountThird + vector.getZ() * amountSecond;
          vector.setX(amountSixth).setZ(amountSeventh);
          double amountEighth = vector.getY() * amountFourth - vector.getZ() * amountFifth;
          amountSeventh = vector.getY() * amountFifth + vector.getZ() * amountFourth;
          vector.setY(amountEighth).setZ(amountSeventh);
          Location locationSecond = location.clone().add(vector);
          cmiHologram.setLocation(locationSecond);
          cmiHologram
              .getSettings()
              .setYaw((cmiHologram.getSettings().getYaw() + value + 360.0) % 360.0);
          cmiHologram
              .getSettings()
              .setPitch((cmiHologram.getSettings().getPitch() + count + 360.0) % 360.0);
          cmiHologram.requestFullUpdate();
        }
      }

      CMI.getInstance().getHologramManager().save();
    }
  }

  public static void rotateHologramYawGroup(int value, String string, Location location) {
    var set = getHologramsByGroup(string);
    if (!set.isEmpty()) {
      for (CMIHologram cmiHologram : set) {
        CMILocation cmiLocation = cmiHologram.getLocation();
        Vector vector = cmiLocation.toVector().subtract(location.toVector());
        if (vector.lengthSquared() == 0.0) {
          cmiHologram
              .getSettings()
              .setYaw((cmiHologram.getSettings().getYaw() + value + 360.0) % 360.0);
          cmiHologram.requestFullUpdate();
        } else {
          double number = Math.toRadians(value);
          double amount = Math.cos(number);
          double amountSecond = Math.sin(number);
          double amountThird = vector.getX() * amount - vector.getZ() * amountSecond;
          double amountFourth = vector.getX() * amountSecond + vector.getZ() * amount;
          vector.setX(amountThird).setZ(amountFourth);
          Location locationSecond = location.clone().add(vector);
          cmiHologram.setLocation(locationSecond);
          cmiHologram
              .getSettings()
              .setYaw((cmiHologram.getSettings().getYaw() + value + 360.0) % 360.0);
          cmiHologram.requestFullUpdate();
        }
      }

      CMI.getInstance().getHologramManager().save();
    }
  }
}
