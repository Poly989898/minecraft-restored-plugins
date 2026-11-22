package com.Zrips.CMI.AllListeners;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Modules.PlayerOptions.PlayerOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import net.Zrips.CMILib.Entities.CMIEntityType;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.Chunk;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetEvent.TargetReason;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class NoTargetListener implements Listener {
  private CMI plugin;
  public static Set<UUID> noTargetPlayers = new HashSet<>();

  public NoTargetListener(CMI cmi) {
    this.plugin = cmi;
  }

  public static boolean isInNoTargetPlayers(UUID uuid) {
    return noTargetPlayers.isEmpty() ? false : noTargetPlayers.contains(uuid);
  }

  public static void toggleOn(CMIUser cmiUser) {
    addNoTargetPlayers(cmiUser.getUniqueId());
    if (cmiUser.isOnline()) {
      ArrayList<Entity> arrayList = new ArrayList<>();
      Chunk chunk = cmiUser.getPlayer().getLocation().getChunk();

      for (int value = -1; value <= 1; value++) {
        for (int dz = -1; dz <= 1; dz++) {
          Chunk neighborChunk =
              chunk.getWorld().getChunkAt(chunk.getX() + value, chunk.getZ() + dz);
          arrayList.addAll(Arrays.asList(neighborChunk.getEntities()));
        }
      }

      for (Entity entity : arrayList) {
        if (entity instanceof Monster) {
          Monster monster = (Monster) entity;
          if (monster.getTarget() != null
              && monster.getTarget().getUniqueId().equals(cmiUser.getUniqueId())) {
            monster.setTarget(null);
          }
        }
      }
    }
  }

  public static void toggleOff(CMIUser cmiUser) {
    removeNoTargetPlayers(cmiUser.getUniqueId());
  }

  public static void addNoTargetPlayers(UUID uuid) {
    noTargetPlayers.add(uuid);
  }

  public static void removeNoTargetPlayers(UUID uuid) {
    noTargetPlayers.remove(uuid);
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onPlayerJoin(PlayerJoinEvent playerJoinEvent) {
    Player player = playerJoinEvent.getPlayer();
    CMIUser cmiUser = CMIUser.getUser(player);
    if (cmiUser.getOptionState(PlayerOption.notarget)) {
      addNoTargetPlayers(player.getUniqueId());
    }
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onPlayerQuit(PlayerQuitEvent playerQuitEvent) {
    Player player = playerQuitEvent.getPlayer();
    removeNoTargetPlayers(player.getUniqueId());
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void EntityTargetLivingEntityEvent(
      EntityTargetLivingEntityEvent entityTargetLivingEntityEvent) {
    if (!entityTargetLivingEntityEvent.isCancelled()) {
      if (!noTargetPlayers.isEmpty()) {
        if (entityTargetLivingEntityEvent.getTarget() instanceof Player) {
          if (isInNoTargetPlayers(entityTargetLivingEntityEvent.getTarget().getUniqueId())) {
            if (!Version.isCurrentEqualOrHigher(Version.v1_17_R1)
                || !entityTargetLivingEntityEvent.getReason().equals(TargetReason.TEMPT)) {
              if (!entityTargetLivingEntityEvent.getReason().equals(TargetReason.CLOSEST_PLAYER)
                      && !entityTargetLivingEntityEvent
                          .getReason()
                          .equals(TargetReason.CLOSEST_ENTITY)
                      && (!Version.isCurrentEqualOrHigher(Version.v1_10_R1)
                          || !entityTargetLivingEntityEvent
                              .getReason()
                              .equals(TargetReason.TARGET_ATTACKED_ENTITY))
                      && (!Version.isCurrentEqualOrHigher(Version.v1_11_R1)
                          || !entityTargetLivingEntityEvent
                              .getReason()
                              .equals(TargetReason.RANDOM_TARGET))
                      && (!Version.isCurrentEqualOrHigher(Version.v1_15_R1)
                          || !entityTargetLivingEntityEvent
                              .getReason()
                              .equals(TargetReason.TARGET_ATTACKED_NEARBY_ENTITY))
                  || !CMIEntityType.get(entityTargetLivingEntityEvent.getEntity())
                      .equals(CMIEntityType.EXPERIENCE_ORB)
                  || CMIUser.getUser((Player) entityTargetLivingEntityEvent.getTarget())
                      .isVanished()) {
                entityTargetLivingEntityEvent.setCancelled(true);
              }
            }
          }
        }
      }
    }
  }
}
