package com.Zrips.CMI.Modules.Holograms;

import com.Zrips.CMI.CMI;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import net.Zrips.CMILib.Container.CMIVector2D;
import net.Zrips.CMILib.Container.CMIVector3D;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class CMIAiming {
  private static ConcurrentHashMap<UUID, CMIAiming> aimingAt = new ConcurrentHashMap<>();
  private CMIHologram aimAt;
  private CMIVector3D global;
  private CMIVector2D local;
  private boolean frontSide = true;

  public static void addAimAt(UUID uuid, CMIHologram cmiHologram, CMIVector3D cmiVector3D) {
    aimingAt.put(uuid, new CMIAiming(cmiHologram, cmiVector3D));
  }

  public static void addAimAt(
      UUID uuid, CMIHologram cmiHologram, CMIVector3D cmiVector3D, CMIVector2D cmiVector2D) {
    aimingAt.put(uuid, new CMIAiming(cmiHologram, cmiVector3D, cmiVector2D));
  }

  public static CMIAiming getAimAt(Player player) {
    return getAimAt(player.getUniqueId());
  }

  public static CMIAiming getAimAt(UUID uuid) {
    return aimingAt.get(uuid);
  }

  public static void removeAimAt(Player player) {
    removeAimAt(player.getUniqueId());
  }

  public static void removeAimAt(UUID uuid) {
    aimingAt.remove(uuid);
  }

  public CMIAiming(CMIHologram cmiHologram, CMIVector3D cmiVector3D) {
    this.aimAt = cmiHologram;
    this.global = cmiVector3D;
  }

  public CMIAiming(CMIHologram cmiHologram, CMIVector3D cmiVector3D, CMIVector2D cmiVector2D) {
    this.aimAt = cmiHologram;
    this.global = cmiVector3D.clone();
    this.local = cmiVector2D.clone();
  }

  public CMIHologram getAimAt() {
    return this.aimAt;
  }

  public void setAimAt(CMIHologram cmiHologram) {
    this.aimAt = cmiHologram;
  }

  public CMIVector3D getGlobalPos() {
    return this.global;
  }

  public void setGlobalPos(CMIVector3D cmiVector3D) {
    this.global = cmiVector3D;
  }

  public CMIVector2D getLocalPos() {
    return this.local;
  }

  public boolean isFrontSide() {
    return this.frontSide;
  }

  public void setFrontSide(boolean flag) {
    this.frontSide = flag;
  }

  public static CompletableFuture<CMIHologram> getLookingAt(UUID uuid) {
    return getLookingAtHologram(uuid)
        .thenCompose(
            cmiHologram ->
                cmiHologram != null
                    ? CompletableFuture.completedFuture(cmiHologram)
                    : getLookingAtHologramApproximate(uuid));
  }

  public static CompletableFuture<CMIHologram> getLookingAtHologram(UUID uuid) {
    if (uuid == null) {
      return CompletableFuture.completedFuture(null);
    }

    removeAimAt(uuid);
    var set = CMI.getInstance().getHologramManager().getPlayerVisibleHolograms(uuid);
    if (set == null) {
      return CompletableFuture.completedFuture(null);
    }

    ArrayList<CompletableFuture<Void>> arrayList = new ArrayList();

    for (CMIHologram cmiHologram : set) {
      arrayList.add(cmiHologram.getHoverModule().checkHoverPosition(uuid, true));
    }

    return CompletableFuture.allOf(arrayList.toArray(new CompletableFuture[0]))
        .thenApply(
            value -> {
              try {
                CMIAiming cmiAiming = getAimAt(uuid);
                return cmiAiming != null ? cmiAiming.getAimAt() : null;
              } catch (Throwable throwable) {
                throwable.printStackTrace();
                return null;
              }
            });
  }

  public static CompletableFuture<CMIHologram> getLookingAtHologramApproximate(UUID uuid) {
    if (uuid == null) {
      return CompletableFuture.completedFuture(null);
    }

    var set = CMI.getInstance().getHologramManager().getPlayerVisibleHolograms(uuid);
    if (set != null && !set.isEmpty()) {
      Player player = Bukkit.getPlayer(uuid);
      if (player == null) {
        return CompletableFuture.completedFuture(null);
      }

      Location location = player.getEyeLocation();
      Vector vector = location.getDirection().normalize();
      double number = Math.toRadians(20.0);
      CMIHologram cmiHologram = null;
      double bestScore = Double.MAX_VALUE;

      for (CMIHologram candidate : set) {
        Location hologramLocation = candidate.getCenterLocation();
        if (hologramLocation != null && hologramLocation.getWorld().equals(location.getWorld())) {
          Vector toHologram = hologramLocation.toVector().subtract(location.toVector());
          double distance = toHologram.length();
          Vector direction = toHologram.clone().normalize();
          double angle = vector.angle(direction);
          if (!(angle > number)) {
            double angleWeight = 1.0;
            double distanceWeight = 0.01;
            double score = angle * angleWeight + distance * distanceWeight;
            if (score < bestScore) {
              bestScore = score;
              cmiHologram = candidate;
            }
          }
        }
      }

      return CompletableFuture.completedFuture(cmiHologram);
    } else {
      return CompletableFuture.completedFuture(null);
    }
  }
}
