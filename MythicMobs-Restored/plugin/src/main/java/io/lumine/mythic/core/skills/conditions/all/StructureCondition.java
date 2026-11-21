package io.lumine.mythic.core.skills.conditions.all;

import com.google.common.collect.Sets;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.adapters.AbstractStructure;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.conditions.ILocationCondition;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.utils.Schedulers;
import io.lumine.mythic.bukkit.utils.folia.FoliaHelper;
import io.lumine.mythic.bukkit.utils.version.ServerVersion;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.utils.annotations.MythicCondition;
import io.lumine.mythic.core.utils.annotations.MythicField;
import java.util.Set;
import org.bukkit.Location;

@MythicCondition(
   author = "Ashijin",
   name = "structure",
   aliases = {},
   description = "Tests if the target is within the given list of structures"
)
public class StructureCondition extends SkillCondition implements ILocationCondition {
   @MythicField(
      name = "structures",
      aliases = {"s"},
      description = "A list of structures to check",
      defValue = "village"
   )
   private Set<AbstractStructure> structures = Sets.newConcurrentHashSet();

   public StructureCondition(String line, MythicLineConfig mlc) {
      super(line);
      String strStructures = mlc.getString(new String[]{"structures", "structure", "s"}, "village");

      for (String strStructure : strStructures.split(",")) {
         for (AbstractStructure structure : getPlugin().getVolatileCodeHandler().getWorldHandler().getStructures()) {
            if (structure.matches(strStructure)) {
               this.structures.add(structure);
            }
         }
      }
   }

   @Override
   public boolean check(AbstractLocation target) {
      Location location = BukkitAdapter.adapt(target);
      if (ServerVersion.isFolia() && !FoliaHelper.isOwnedByCurrentRegion(location)) {
         Boolean result = Schedulers.of(location).supply(() -> this.checkStructures(target)).join();
         return result != null ? result : false;
      } else {
         return this.checkStructures(target);
      }
   }

   private boolean checkStructures(AbstractLocation target) {
      for (AbstractStructure struct : getPlugin().getVolatileCodeHandler().getWorldHandler().getStructures(target)) {
         MythicLogger.debug(MythicLogger.DebugLevel.CONDITION, "StructureCondition checking {0} contains {1}", this.structures.toString(), struct.getKey());
         if (this.structures.contains(struct)) {
            return true;
         }
      }

      return false;
   }
}
