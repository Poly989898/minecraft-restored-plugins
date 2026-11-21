package io.lumine.mythic.core.skills.cinematic;

import io.lumine.mythic.api.skills.Skill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.events.MythicCinematicEndEvent;
import io.lumine.mythic.bukkit.events.MythicCinematicStartEvent;
import io.lumine.mythic.bukkit.events.MythicReloadEvent;
import io.lumine.mythic.bukkit.utils.Events;
import io.lumine.mythic.bukkit.utils.plugin.PluginModule;
import io.lumine.mythic.bukkit.utils.version.ServerVersion;
import io.lumine.mythic.core.logging.MythicLogger;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerKickEvent.Cause;

public final class CinematicManager extends PluginModule<MythicBukkit> {
   private CameraPathRegistry registry;
   private ConcurrentHashMap<UUID, CinematicState> active;
   private CameraPathLoader pathLoader;

   public CinematicManager(MythicBukkit plugin) {
      super(plugin);
   }

   public void load(MythicBukkit plugin) {
      if (this.registry == null) {
         this.registry = new CameraPathRegistry();
      }

      if (this.active == null) {
         this.active = new ConcurrentHashMap<>();
      }

      this.pathLoader = new CameraPathLoader(plugin, this.registry);
      this.pathLoader.load();
      Events.subscribe(PlayerQuitEvent.class).handler(event -> this.stopWith(event.getPlayer())).bindWith(this);
      Events.subscribe(MythicReloadEvent.class).handler(event -> this.reload()).bindWith(this);
      if (ServerVersion.isPaper()) {
         Events.subscribe(PlayerKickEvent.class)
            .filter(event -> event.getCause() == Cause.SELF_INTERACTION)
            .filter(event -> this.isInCinematic(event.getPlayer().getUniqueId()))
            .handler(event -> event.setCancelled(true))
            .bindWith(this);
      }
   }

   @Override
   public void unload() {
      for (UUID uuid : this.active.keySet()) {
         this.stop(uuid);
      }

      this.active.clear();
   }

   public void reload() {
      if (this.pathLoader != null) {
         this.pathLoader.load();
      }
   }

   public CameraPathRegistry getRegistry() {
      return this.registry;
   }

   public void start(
      Player player,
      CameraPath path,
      int maxDuration,
      boolean freezePlayer,
      CinematicMode mode,
      Optional<Skill> onStartSkill,
      Optional<Skill> onEndSkill,
      SkillMetadata meta
   ) {
      this.start(player, null, path, maxDuration, freezePlayer, mode, onStartSkill, onEndSkill, meta);
   }

   public void start(
      Player player,
      String pathId,
      CameraPath path,
      int maxDuration,
      boolean freezePlayer,
      CinematicMode mode,
      Optional<Skill> onStartSkill,
      Optional<Skill> onEndSkill,
      SkillMetadata meta
   ) {
      if (player != null && path != null) {
         String resolvedId = pathId != null
            ? pathId
            : this.registry.keys().stream().filter(k -> this.registry.get(k).orElse(null) == path).findFirst().orElse("inline");
         MythicCinematicStartEvent startEvent = new MythicCinematicStartEvent(player, resolvedId, path);
         Bukkit.getPluginManager().callEvent(startEvent);
         if (!startEvent.isCancelled()) {
            UUID uuid = player.getUniqueId();
            CinematicState existing = this.active.remove(uuid);
            if (existing != null) {
               existing.close();
            }

            CinematicState state = new CinematicState(this, player, resolvedId, path, maxDuration, freezePlayer, mode, onStartSkill, onEndSkill, meta);
            this.active.put(uuid, state);
         }
      }
   }

   public void stop(UUID playerUuid) {
      CinematicState state = this.active.remove(playerUuid);
      if (state != null) {
         state.close();
      }
   }

   public void cancel(UUID playerUuid) {
      CinematicState state = this.active.remove(playerUuid);
      if (state != null) {
         state.setEndReason(MythicCinematicEndEvent.Reason.CANCELLED);
         state.close();
      }
   }

   public void stopWith(Player player) {
      if (player != null) {
         CinematicState state = this.active.remove(player.getUniqueId());
         if (state != null) {
            state.setEndReason(MythicCinematicEndEvent.Reason.QUIT);
            state.closeWith(player);
         }
      }
   }

   public boolean isInCinematic(UUID playerUuid) {
      return this.active.containsKey(playerUuid);
   }

   public Optional<CinematicState> getState(UUID playerUuid) {
      return Optional.ofNullable(this.active.get(playerUuid));
   }

   public boolean addPoint(String pathId, Location loc, int index, int hold, Easing easing, boolean relative) {
      CameraPath path = this.registry.get(pathId).orElse(null);
      if (path != null && path.getSourceFile() != null) {
         String entry = formatPoint(loc, hold, easing, relative);
         return this.mutatePoints(path, pathId, points -> {
            int target = index > 0 && index <= points.size() + 1 ? index - 1 : points.size();
            points.add(target, entry);
         });
      } else {
         return false;
      }
   }

   public boolean removePoint(String pathId, int index) {
      CameraPath path = this.registry.get(pathId).orElse(null);
      return path != null && path.getSourceFile() != null ? this.mutatePoints(path, pathId, points -> {
         if (!points.isEmpty()) {
            int target = index > 0 && index <= points.size() ? index - 1 : points.size() - 1;
            points.remove(target);
         }
      }) : false;
   }

   private boolean mutatePoints(CameraPath path, String pathId, Consumer<List<String>> mutator) {
      File file = path.getSourceFile();

      try {
         YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
         ConfigurationSection section = yaml.getConfigurationSection(pathId);
         if (section == null) {
            return false;
         } else {
            List<String> points = new ArrayList<>(section.getStringList("points"));
            int sizeBefore = points.size();
            mutator.accept(points);
            if (points.size() == sizeBefore && points.equals(section.getStringList("points"))) {
               return false;
            } else {
               section.set("points", points);
               yaml.save(file);
               if (this.pathLoader != null) {
                  this.pathLoader.load();
               }

               return true;
            }
         }
      } catch (Exception e) {
         MythicLogger.error("Failed to mutate camera path '{0}' in {1}: {2}", pathId, file.getName(), e.getMessage());
         return false;
      }
   }

   private static String formatPoint(Location loc, int hold, Easing easing, boolean relative) {
      double x = loc.getX();
      double y = loc.getY();
      double z = loc.getZ();
      String worldToken;
      if (relative) {
         worldToken = "~";
      } else {
         worldToken = loc.getWorld() == null ? "world" : loc.getWorld().getName();
      }

      StringBuilder sb = new StringBuilder();
      sb.append(worldToken).append(',').append(String.format(Locale.ROOT, "%.2f,%.2f,%.2f,%.2f,%.2f", x, y, z, loc.getYaw(), loc.getPitch()));
      boolean needHold = hold > 0;
      boolean needEasing = easing != null;
      if (needHold || needEasing) {
         sb.append(',').append(needHold ? hold : 0);
      }

      if (needEasing) {
         sb.append(',').append(easing.name());
      }

      return sb.toString();
   }
}
