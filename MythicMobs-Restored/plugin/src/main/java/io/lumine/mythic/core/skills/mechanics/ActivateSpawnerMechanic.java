package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.INoTargetSkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.spawning.spawners.MythicSpawner;
import io.lumine.mythic.core.utils.annotations.MythicField;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;

@MythicMechanic(
   author = "Ashijin",
   name = "activatespawner",
   aliases = {"as"},
   description = "Activate a Mythic Spawner"
)
public class ActivateSpawnerMechanic extends SkillMechanic implements INoTargetSkill {
   @MythicField(
      name = "spawner",
      aliases = {"spawners", "s"},
      description = "The name of the spawner(s) to activate. This can accept groups and wildcards also using the appropriate syntax"
   )
   protected PlaceholderString search;

   public ActivateSpawnerMechanic(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
      super(manager, file, skill, mlc);
      this.threadSafetyLevel = ThreadSafetyLevel.SYNC_ONLY;
      this.search = mlc.getPlaceholderString(new String[]{"spawners", "spawner", "s"}, "");
   }

   @Override
   public SkillResult cast(SkillMetadata data) {
      for (MythicSpawner ms : this.getPlugin().getSpawnerManager().getSpawnersByString(data.getCaster().getEntity().getLocation(), this.search.get(data))) {
         ms.ActivateSpawner();
      }

      return SkillResult.SUCCESS;
   }
}
