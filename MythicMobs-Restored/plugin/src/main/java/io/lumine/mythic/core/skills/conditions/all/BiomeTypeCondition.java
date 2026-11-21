package io.lumine.mythic.core.skills.conditions.all;

import com.google.common.collect.Sets;
import io.lumine.mythic.api.adapters.AbstractBiome;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.conditions.ILocationCondition;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.utils.annotations.MythicCondition;
import io.lumine.mythic.core.utils.annotations.MythicField;
import java.util.Arrays;
import java.util.Set;

@MythicCondition(
   author = "Ashijin",
   name = "biometype",
   aliases = {"biomecategory"},
   description = "Tests if the target is within the given list of biome types"
)
public class BiomeTypeCondition extends SkillCondition implements ILocationCondition {
   @MythicField(
      name = "type",
      aliases = {"t"},
      description = "A list of biomes to check"
   )
   private Set<String> biomes = Sets.newConcurrentHashSet();
   @MythicField(
      name = "exact",
      aliases = {"e"},
      description = "Whether to match the type exactly"
   )
   private boolean exact;

   public BiomeTypeCondition(String line, MythicLineConfig mlc) {
      super(line);
      this.exact = mlc.getBoolean(new String[]{"exact", "e"}, true);
      String b = mlc.getString(new String[]{"type", "t"}, "ocean", this.conditionVar).toLowerCase();
      this.biomes.addAll(Arrays.asList(b.split(",")));
   }

   @Override
   public boolean check(AbstractLocation target) {
      AbstractBiome biome = getPlugin().getVolatileCodeHandler().getWorldHandler().getBiome(target);
      if (this.exact) {
         MythicLogger.debug(MythicLogger.DebugLevel.CONDITION, "BiomeTypeCondition checking {0} matches {1} exactly", this.biomes.toString(), biome.getType());
         return this.biomes.contains(biome.getType());
      } else {
         MythicLogger.debug(MythicLogger.DebugLevel.CONDITION, "BiomeTypeCondition checking {0} matches {1}", this.biomes.toString(), biome.getType());

         for (String check : this.biomes) {
            if (biome.getType().contains(check)) {
               return true;
            }
         }

         return false;
      }
   }
}
