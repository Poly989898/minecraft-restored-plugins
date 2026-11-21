package io.lumine.mythic.core.skills.conditions.all;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.conditions.IEntityCondition;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.utils.Patterns;
import io.lumine.mythic.core.utils.annotations.MythicCondition;
import io.lumine.mythic.core.utils.annotations.MythicField;

@MythicCondition(
   author = "Ashijin",
   name = "stance",
   aliases = {},
   description = "Checks the stance of the target mob"
)
public class StanceCondition extends SkillCondition implements IEntityCondition {
   @MythicField(
      name = "stance",
      aliases = {"s"},
      description = "The stance to match"
   )
   private final String stance;
   @MythicField(
      name = "strict",
      aliases = {"str"},
      description = "Whether to match exactly",
      defValue = "false"
   )
   private final boolean strictMatch;
   private final boolean parse;

   public StanceCondition(String line, MythicLineConfig mlc) {
      super(line);
      this.stance = mlc.getString(new String[]{"stance", "s"}, "DEFAULT", this.conditionVar);
      this.strictMatch = mlc.getBoolean(new String[]{"strict", "str"}, true);
      this.parse = mlc.getBoolean(new String[]{"parse", "p"}, false);
   }

   @Override
   public boolean check(AbstractEntity entity) {
      if (MythicBukkit.inst().getMobManager().isActiveMob(entity)) {
         ActiveMob am = MythicBukkit.inst().getMobManager().getMythicMobInstance(entity);
         String stance = am.getStance();
         if (stance == null) {
            return this.stance == null;
         } else {
            if (this.parse) {
               Patterns.CompilePatterns();
            }

            return this.strictMatch ? stance.equals(this.stance) : stance.contains(this.stance);
         }
      } else {
         return false;
      }
   }
}
