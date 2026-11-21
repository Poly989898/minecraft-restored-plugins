package io.lumine.mythic.core.skills.mechanics;

import com.google.common.collect.Lists;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.INoTargetSkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.utils.serialize.Position;
import io.lumine.mythic.bukkit.utils.serialize.Region;
import io.lumine.mythic.bukkit.utils.worldedit.Regions;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.skills.SkillTargeter;
import io.lumine.mythic.core.skills.targeters.RegionTargeter;
import io.lumine.mythic.core.utils.annotations.MythicField;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;
import java.util.Collection;
import java.util.Optional;
import org.bukkit.Material;

@MythicMechanic(
   author = "Ashijin",
   name = "worldEditReplace",
   aliases = {"weReplace"},
   premium = true,
   version = "5.2",
   description = "Replaces blocks in a region using WorldEdit"
)
public class WEReplaceMechanic extends SkillMechanic implements INoTargetSkill {
   @MythicField(
      name = "from",
      aliases = {"f"},
      description = "The block to replace",
      defValue = "AIR"
   )
   private PlaceholderString from;
   @MythicField(
      name = "to",
      aliases = {"t"},
      description = "The block to replace with",
      defValue = "AIR"
   )
   private PlaceholderString to;

   public WEReplaceMechanic(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
      super(manager, file, skill, mlc);
      if (!MythicBukkit.isVolatile()) {
         MythicLogger.errorMechanicConfig(this, mlc, "Mechanic requires Mythic Premium");
      } else {
         this.from = mlc.getPlaceholderString(new String[]{"from", "f"}, "AIR");
         this.to = mlc.getPlaceholderString(new String[]{"to", "t"}, "AIR");
      }
   }

   @Override
   public SkillResult cast(SkillMetadata data) {
      if (!MythicBukkit.isVolatile()) {
         MythicLogger.errorMechanicConfig(this, this.config, "Mechanic requires Mythic Premium");
         return SkillResult.REQUIRES_PREMIUM;
      } else {
         Optional<SkillTargeter> maybeTargeter = this.getTargeter();
         if (maybeTargeter.isEmpty()) {
            MythicLogger.errorMechanicConfig(this, this.config, "Mechanic requires a region-targeting targeter");
            return SkillResult.INVALID_CONFIG;
         } else {
            SkillTargeter targeter = maybeTargeter.get();
            if (targeter instanceof RegionTargeter regionTargeter) {
               Position min = regionTargeter.getMin(data);
               Position max = regionTargeter.getMax(data);
               Region region = Region.of(min, max);
               java.util.List<Material> from;
               Material to;

               try {
                  from = Lists.newArrayList();

                  for (String spl : this.from.get(data).split(",")) {
                     from.add(Material.valueOf(spl.toUpperCase()));
                  }
               } catch (Error | Exception e) {
                  MythicLogger.errorMechanicConfig(this, this.config, "Invalid input for 'from' attribute");
                  return SkillResult.INVALID_CONFIG;
               }

               try {
                  to = Material.valueOf(this.to.get(data));
               } catch (Error | Exception ex) {
                  MythicLogger.errorMechanicConfig(this, this.config, "Invalid input for 'to' attribute");
                  return SkillResult.INVALID_CONFIG;
               }

               MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "Replacing blocks in {0}", region.toString());
               Regions.replaceRegion(region, from, to);
               return SkillResult.SUCCESS;
            } else {
               MythicLogger.errorMechanicConfig(this, this.config, "Mechanic requires a region-targeting targeter");
               return SkillResult.INVALID_CONFIG;
            }
         }
      }
   }
}
