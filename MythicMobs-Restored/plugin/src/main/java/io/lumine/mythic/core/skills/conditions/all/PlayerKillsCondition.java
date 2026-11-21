package io.lumine.mythic.core.skills.conditions.all;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.conditions.IEntityCondition;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.utils.numbers.RangedDouble;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.utils.annotations.MythicCondition;
import io.lumine.mythic.core.utils.annotations.MythicField;

@MythicCondition(
   author = "Ashijin",
   name = "playerKills",
   aliases = {},
   description = "Matches how many players the target mob has killed"
)
public class PlayerKillsCondition extends SkillCondition implements IEntityCondition {
   @MythicField(
      name = "kills",
      aliases = {"k"},
      description = "The number range to match"
   )
   private RangedDouble kills;

   public PlayerKillsCondition(String line, MythicLineConfig mlc) {
      super(line);
      String k = mlc.getString(new String[]{"kills", "k"}, "0", this.conditionVar);
      this.kills = new RangedDouble(k);
   }

   @Override
   public boolean check(AbstractEntity entity) {
      if (MythicBukkit.inst().getMobManager().isActiveMob(entity)) {
         ActiveMob am = MythicBukkit.inst().getMobManager().getMythicMobInstance(entity);
         return this.kills.equals(am.getPlayerKills());
      } else {
         return false;
      }
   }
}
