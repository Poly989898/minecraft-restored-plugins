package io.lumine.mythic.core.skills.targeters;

import com.google.common.collect.Lists;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.utils.serialize.Region;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.pins.Pin;
import io.lumine.mythic.core.utils.annotations.MythicTargeter;
import java.util.Collection;
import java.util.Optional;
import org.bukkit.block.Block;

@MythicTargeter(
   author = "Ashijin",
   name = "blocksInPinRegion",
   aliases = {},
   description = "Targets the blocks in a pin region"
)
public class BlocksInPinRegionTargeter extends ILocationSelector {
   protected PlaceholderString pin1;
   protected PlaceholderString pin2;

   public BlocksInPinRegionTargeter(SkillExecutor manager, MythicLineConfig mlc) {
      super(manager, mlc);
      this.pin1 = mlc.getPlaceholderString(new String[]{"pin1", "p1"}, null);
      if (this.pin1 == null) {
         MythicLogger.errorTargeterConfig(this, mlc, "Pin1 name must be set.");
      } else {
         this.pin2 = mlc.getPlaceholderString(new String[]{"pin2", "p2"}, null);
         if (this.pin2 == null) {
            MythicLogger.errorTargeterConfig(this, mlc, "Pin2 name must be set.");
         }
      }
   }

   @Override
   public Collection<AbstractLocation> getLocations(SkillMetadata data) {
      Collection<AbstractLocation> targets = Lists.newArrayList();
      String strPin1 = this.pin1.get(data);
      Optional<Pin> maybePin1 = this.getPlugin().getPinManager().getPin(strPin1);
      if (maybePin1.isEmpty()) {
         MythicLogger.debug(MythicLogger.DebugLevel.SKILL_CHECK, "Pin {0} is not defined", strPin1);
         return targets;
      } else {
         Pin pin1 = maybePin1.get();
         String strPin2 = this.pin2.get(data);
         Optional<Pin> maybePin2 = this.getPlugin().getPinManager().getPin(strPin2);
         if (maybePin2.isEmpty()) {
            MythicLogger.debug(MythicLogger.DebugLevel.SKILL_CHECK, "Pin {0} is not defined", strPin2);
            return targets;
         } else {
            Pin pin2 = maybePin2.get();
            Region region = Region.of(pin1.getLocations().get(0).toPosition(), pin2.getLocations().get(0).toPosition());

            for (Block block : region.getBlocks()) {
               AbstractLocation abstractLocation = BukkitAdapter.adapt(block.getLocation());
               targets.add(this.mutate(data, abstractLocation));
            }

            return targets;
         }
      }
   }
}
