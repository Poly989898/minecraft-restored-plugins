package io.lumine.mythic.core.skills.conditions.all;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.conditions.IEntityCondition;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.utils.annotations.MythicCondition;
import io.lumine.mythic.core.utils.annotations.MythicField;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

@MythicCondition(
   author = "jaylawl",
   name = "lastDamageCause",
   aliases = {},
   version = "4.5",
   description = "Checks the target's last damage cause"
)
public class LastDamageCauseCondition extends SkillCondition implements IEntityCondition {
   @MythicField(
      name = "damagecause",
      aliases = {"cause", "c"},
      description = "The damage cause to match"
   )
   protected DamageCause cause;

   public LastDamageCauseCondition(String line, MythicLineConfig mlc) {
      super(line);
      String c = mlc.getString(new String[]{"damagecause", "cause", "c"}, "ENTITY_ATTACK", this.conditionVar);

      try {
         this.cause = DamageCause.valueOf(c.toUpperCase());
      } catch (Exception e) {
         this.cause = DamageCause.ENTITY_ATTACK;
         MythicLogger.errorConditionConfig(this, mlc, "Damage cause not found (or not supported by this version of MythicMobs");
      }
   }

   @Override
   public boolean check(AbstractEntity e) {
      if (!e.isLiving()) {
         return false;
      } else {
         EntityDamageEvent event = e.getBukkitEntity().getLastDamageCause();
         return event != null && this.cause.equals(event.getCause());
      }
   }
}
