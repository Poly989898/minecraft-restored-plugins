package io.lumine.mythic.core.skills.conditions.all;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractPlayer;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.conditions.IEntityCondition;
import io.lumine.mythic.bukkit.utils.numbers.RangedDouble;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.skills.stats.StatType;
import io.lumine.mythic.core.utils.annotations.MythicCondition;
import io.lumine.mythic.core.utils.annotations.MythicField;

@MythicCondition(
   author = "Ghmmy",
   name = "hasStat",
   description = "Tests if the target entity has a stat"
)
public class HasStatCondition extends SkillCondition implements IEntityCondition {
   @MythicField(
      name = "stat",
      aliases = {"s", "type", "t"},
      description = "The stat type"
   )
   private final StatType stat;
   @MythicField(
      name = "level",
      aliases = {"value", "v"},
      description = "A ranged value to match"
   )
   private RangedDouble value = null;

   public HasStatCondition(String line, MythicLineConfig mlc) {
      super(line);
      String stat = mlc.getString(new String[]{"s", "type", "t"}, null);
      this.stat = getPlugin().getStatManager().getStat(stat).get();
      String strLevel = mlc.getString(new String[]{"value", "v"}, ">0");
      if (strLevel != null) {
         this.value = new RangedDouble(strLevel);
      }
   }

   @Override
   public boolean check(AbstractEntity entity) {
      if (entity.isPlayer()) {
         double amount = getPlugin().getPlayerManager().getProfile((AbstractPlayer)entity).getStatRegistry().get(this.stat);
         MythicLogger.debug(MythicLogger.DebugLevel.CONDITION, "Checking if player has Stat type = " + this.stat.getKey() + ", value = " + this.value);
         MythicLogger.debug(MythicLogger.DebugLevel.CONDITION, "Returning " + amount);
         return this.value.equals(amount);
      } else {
         ActiveMob activeMob = getPlugin().getMobManager().getMythicMobInstance(entity);
         if (activeMob == null) {
            return false;
         } else {
            double amount = activeMob.getStatRegistry().get(this.stat);
            MythicLogger.debug(MythicLogger.DebugLevel.CONDITION, "Checking if mob has Stat type = " + this.stat.getKey() + ", value = " + this.value);
            MythicLogger.debug(MythicLogger.DebugLevel.CONDITION, "Returning " + amount);
            return this.value.equals(amount);
         }
      }
   }
}
