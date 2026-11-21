package io.lumine.mythic.core.skills.conditions.all;

import com.google.common.collect.Sets;
import io.lumine.mythic.api.adapters.AbstractBiome;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.conditions.ILocationCondition;
import io.lumine.mythic.bukkit.utils.version.MinecraftVersions;
import io.lumine.mythic.bukkit.utils.version.ServerVersion;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.utils.annotations.MythicCondition;
import io.lumine.mythic.core.utils.annotations.MythicField;
import java.util.Set;

@MythicCondition(
   author = "Ashijin",
   name = "biome",
   aliases = {},
   description = "Tests if the target is within the given list of biomes"
)
public class BiomeCondition extends SkillCondition implements ILocationCondition {
   @MythicField(
      name = "biome",
      aliases = {"b"},
      description = "A list of biomes to check"
   )
   private Set<String> biomes = Sets.newConcurrentHashSet();
   @MythicField(
      name = "exact",
      aliases = {"e"},
      description = "Whether to match the biome exactly"
   )
   private boolean exact;

   public BiomeCondition(String line, MythicLineConfig mlc) {
      super(line);
      this.exact = mlc.getBoolean(new String[]{"exact", "e"}, true);
      if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_18)) {
         String b = mlc.getString(new String[]{"biome", "b"}, "minecraft:plains", this.conditionVar).toLowerCase();

         for (String biome : b.split(",")) {
            if (this.exact && !biome.contains(":")) {
               biome = "minecraft:" + biome;
            }

            this.biomes.add(biome);
         }
      } else {
         String b = mlc.getString(new String[]{"biome", "b"}, "PLAINS", this.conditionVar);

         for (String biome : b.split(",")) {
            this.biomes.add(biome.toUpperCase());
         }
      }
   }

   @Override
   public boolean check(AbstractLocation target) {
      if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_18)) {
         AbstractBiome biome = getPlugin().getVolatileCodeHandler().getWorldHandler().getBiome(target);
         if (this.exact) {
            MythicLogger.debug(MythicLogger.DebugLevel.CONDITION, "BiomeCondition checking {0} matches {1} exactly", this.biomes.toString(), biome.getKey());
            return this.biomes.contains(biome.getKey());
         }

         MythicLogger.debug(MythicLogger.DebugLevel.CONDITION, "BiomeCondition checking {0} matches {1}", this.biomes.toString(), biome.getKey());

         for (String check : this.biomes) {
            if (biome.getKey().contains(check)) {
               return true;
            }
         }
      } else {
         String b = target.getBiome().toString().toUpperCase();
         if (this.exact) {
            MythicLogger.debug(MythicLogger.DebugLevel.CONDITION, "BiomeCondition checking {0} contains {1}", this.biomes.toString(), b);
            return this.biomes.contains(b);
         }

         MythicLogger.debug(MythicLogger.DebugLevel.CONDITION, "BiomeCondition checking {0} matches {1}", this.biomes.toString(), b);

         for (String biome : this.biomes) {
            if (b.contains(biome)) {
               return true;
            }
         }
      }

      return false;
   }
}
