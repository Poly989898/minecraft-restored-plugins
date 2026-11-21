package io.lumine.mythic.core.skills.conditions.all;

import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.conditions.ILocationCondition;
import io.lumine.mythic.api.skills.placeholders.PlaceholderInt;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.utils.Schedulers;
import io.lumine.mythic.bukkit.utils.folia.FoliaHelper;
import io.lumine.mythic.bukkit.utils.version.ServerVersion;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.utils.annotations.MythicCondition;
import io.lumine.mythic.core.utils.annotations.MythicField;
import org.bukkit.Location;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Farmland;

@MythicCondition(
   author = "Joshinn",
   name = "moisturelevel",
   aliases = {"moistness", "moistureness"},
   description = "Whether or not a location of farmland has specific moisture."
)
public class MoistureCondition extends SkillCondition implements ILocationCondition {
   @MythicField(
      name = "level",
      aliases = {"l", "m", "moistness"},
      description = "The level to check for"
   )
   private final PlaceholderInt level;

   public MoistureCondition(String line, MythicLineConfig mlc) {
      super(line);
      this.level = mlc.getPlaceholderInteger(new String[]{"l", "m", "moistness"}, 7);
   }

   @Override
   public boolean check(AbstractLocation l) {
      Location location = BukkitAdapter.adapt(l);
      if (ServerVersion.isFolia() && !FoliaHelper.isOwnedByCurrentRegion(location)) {
         Boolean result = Schedulers.of(location).supply(() -> this.checkMoisture(l, location)).join();
         return result != null ? result : false;
      } else {
         return this.checkMoisture(l, location);
      }
   }

   private boolean checkMoisture(AbstractLocation l, Location location) {
      BlockData blockData = location.getBlock().getBlockData();
      MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "Block {0}", l.toLocus().serializeShortForm());
      if (blockData instanceof Farmland) {
         MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "Block {0} moistness {1}", l.toLocus().serializeShortForm(), ((Farmland)blockData).getMoisture());
         return ((Farmland)blockData).getMoisture() == this.level.get();
      } else {
         return false;
      }
   }
}
