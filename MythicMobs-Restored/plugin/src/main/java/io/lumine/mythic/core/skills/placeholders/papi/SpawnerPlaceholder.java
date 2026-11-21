package io.lumine.mythic.core.skills.placeholders.papi;

import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.utils.time.DurationFormatter;
import io.lumine.mythic.core.skills.placeholders.PAPIPlaceholder;
import io.lumine.mythic.core.spawning.spawners.MythicSpawner;
import io.lumine.mythic.core.utils.annotations.MythicPAPIPlaceholder;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import org.bukkit.OfflinePlayer;

@MythicPAPIPlaceholder(
   placeholder = "spawner"
)
public class SpawnerPlaceholder extends PAPIPlaceholder<MythicBukkit> {
   public SpawnerPlaceholder(MythicBukkit plugin) {
      super(plugin);
   }

   @Override
   public String onPlaceholderRequest(OfflinePlayer player, String[] args) {
      if (args.length < 2) {
         return "[Invalid]";
      } else {
         String spawnerName = args[0];
         String attribute = args[1];
         MythicSpawner spawner = this.getPlugin().getSpawnerManager().getSpawnerByName(spawnerName);
         if (spawner == null) {
            return "[Invalid_Spawner]";
         } else {
            return switch (attribute) {
               case "mobs" -> String.valueOf(spawner.getNumberOfMobs());
               case "cooldown" -> String.valueOf(spawner.getCooldownSeconds());
               case "cooldownleft" -> DurationFormatter.CONCISE.format(Duration.of(spawner.getRemainingCooldownSeconds(), ChronoUnit.SECONDS));
               case "warmup" -> String.valueOf(spawner.getWarmupSeconds());
               case "warmupleft" -> DurationFormatter.CONCISE.format(Duration.of(spawner.getRemainingWarmupSeconds(), ChronoUnit.SECONDS));
               default -> "[Invalid_Attribute]";
            };
         }
      }
   }
}
