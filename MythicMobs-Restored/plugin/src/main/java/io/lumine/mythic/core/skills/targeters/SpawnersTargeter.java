package io.lumine.mythic.core.skills.targeters;

import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.spawning.spawners.MythicSpawner;
import io.lumine.mythic.core.utils.annotations.MythicTargeter;
import java.util.Collection;
import java.util.HashSet;

@MythicTargeter(
   author = "Ashijin",
   name = "spawners",
   aliases = {},
   description = "Targets the location of specified mob spawners"
)
public class SpawnersTargeter extends ILocationSelector {
   protected String spawnerString;

   public SpawnersTargeter(SkillExecutor manager, MythicLineConfig mlc) {
      super(manager, mlc);
      this.spawnerString = mlc.getString(new String[]{"spawners", "spawner", "s"});
   }

   @Override
   public Collection<AbstractLocation> getLocations(SkillMetadata data) {
      HashSet<AbstractLocation> targets = new HashSet<>();

      for (MythicSpawner ms : MythicBukkit.inst().getSpawnerManager().getSpawnersByString(this.spawnerString)) {
         targets.add(ms.getLocation());
      }

      return targets;
   }
}
