package fr.phoenixdevt.mmoprofiles.bukkit.utils;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import fr.phoenixdevt.mmoprofiles.bukkit.MMOProfiles;
import fr.phoenixdevt.mmoprofiles.shared.request.Request;
import fr.phoenixdevt.profiles.exception.InvalidLocationException;
import fr.phoenixdevt.profiles.util.WrappedLocation;
import io.lumine.mythic.lib.api.player.MMOPlayerData;
import io.lumine.mythic.lib.skill.Skill;
import io.lumine.mythic.lib.skill.SkillMetadata;
import java.util.Objects;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ProfileUtils {
   public static Location readLocation(@NotNull ConfigurationSection config) {
      return new Location(
         Bukkit.getWorld(config.getString("world")),
         config.getDouble("x"),
         config.getDouble("y"),
         config.getDouble("z"),
         (float)config.getDouble("yaw"),
         (float)config.getDouble("pitch")
      );
   }

   @NotNull
   public static Location toBukkit(WrappedLocation wrapped) {
      World world = Bukkit.getWorld(wrapped.getWorldName());
      if (world == null) {
         throw new InvalidLocationException(wrapped);
      } else {
         return new Location(world, wrapped.getX(), wrapped.getY(), wrapped.getZ(), wrapped.getYaw(), wrapped.getPitch());
      }
   }

   public static void resetCompassTarget(@NotNull Player player) {
      Location loc = Objects.requireNonNullElse(player.getBedSpawnLocation(), player.getWorld().getSpawnLocation());
      player.setCompassTarget(loc);
   }

   @NotNull
   public static WrappedLocation wrap(@NotNull Location bukkit) {
      return new WrappedLocation(bukkit.getWorld().getName(), bukkit.getX(), bukkit.getY(), bukkit.getZ(), bukkit.getYaw(), bukkit.getPitch());
   }

   public static void sendRequest(@NotNull Player player, @NotNull Request request) {
      ByteArrayDataOutput out = ByteStreams.newDataOutput();
      out.writeUTF(request.toJson().toString());
      Bukkit.getServer().sendPluginMessage(MMOProfiles.plugin, "mmoprofiles:main", out.toByteArray());
   }

   @Nullable
   public static Location fixSpawnLocation(@Nullable Location loc) {
      if (loc == null) {
         return null;
      }

      for (BlockFace face : BlockFace.values()) {
         Block neighbor = loc.clone().add(face.getDirection()).getBlock();
         if (neighbor.getType().name().endsWith("BED") || neighbor.getType().name().equals("RESPAWN_ANCHOR")) {
            return neighbor.getLocation();
         }
      }

      return loc;
   }

   public static void castScript(@NotNull MMOPlayerData caster, @Nullable Skill skill) {
      if (skill != null) {
         skill.cast(new SkillMetadata(skill, caster));
      }
   }
}
