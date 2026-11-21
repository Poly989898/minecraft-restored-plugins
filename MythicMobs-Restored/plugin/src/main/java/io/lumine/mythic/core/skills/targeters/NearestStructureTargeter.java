package io.lumine.mythic.core.skills.targeters;

import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.utils.annotations.MythicTargeter;
import java.util.Collection;
import java.util.HashSet;
import org.bukkit.Location;
import org.bukkit.StructureType;

@MythicTargeter(
   author = "Ashijin",
   name = "nearestStructure",
   aliases = {},
   description = "Targets the nearest structure"
)
public class NearestStructureTargeter extends ILocationSelector {
   private StructureType structureType;
   private int radius;
   private boolean unexplored;

   public NearestStructureTargeter(SkillExecutor manager, MythicLineConfig mlc) {
      super(manager, mlc);
      String strType = mlc.getString(new String[]{"type", "t"}, "STRONGHOLD");
      this.structureType = StructureType.STRONGHOLD;

      for (StructureType type : StructureType.getStructureTypes().values()) {
         if (type.getName().equalsIgnoreCase(strType) || type.toString().equalsIgnoreCase(strType)) {
            this.structureType = type;
            break;
         }
      }

      this.radius = mlc.getInteger(new String[]{"radius", "r"}, 5000);
      this.unexplored = mlc.getBoolean(new String[]{"unexplored", "u"}, false);
   }

   @Override
   public Collection<AbstractLocation> getLocations(SkillMetadata data) {
      HashSet<AbstractLocation> targets = new HashSet<>();
      Location location = BukkitAdapter.adapt(data.getCaster().getEntity().getLocation());
      Location found = location.getWorld().locateNearestStructure(location, this.structureType, this.radius, this.unexplored);
      if (found != null) {
         targets.add(BukkitAdapter.adapt(found));
      }

      return targets;
   }
}
