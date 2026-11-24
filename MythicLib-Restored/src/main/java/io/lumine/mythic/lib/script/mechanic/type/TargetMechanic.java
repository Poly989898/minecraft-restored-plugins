package io.lumine.mythic.lib.script.mechanic.type;

import io.lumine.mythic.lib.script.mechanic.Mechanic;
import io.lumine.mythic.lib.script.targeter.EntityTargeter;
import io.lumine.mythic.lib.script.targeter.entity.DefaultEntityTargeter;
import io.lumine.mythic.lib.skill.SkillMetadata;
import io.lumine.mythic.lib.util.configobject.ConfigObject;
import org.bukkit.entity.Entity;

public abstract class TargetMechanic extends Mechanic {
   private final EntityTargeter targeter;

   public TargetMechanic(ConfigObject config) {
      this.targeter = config.contains("target") ? config.getEntityTargeter("target") : new DefaultEntityTargeter();
   }

   public EntityTargeter getTargeter() {
      return this.targeter;
   }

   @Override
   public void cast(SkillMetadata meta) {
      for (Entity target : this.targeter.findTargets(meta)) {
         this.cast(meta, target);
      }
   }

   public abstract void cast(SkillMetadata var1, Entity var2);
}
