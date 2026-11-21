package io.lumine.mythic.core.skills.conditions.all;

import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.conditions.ILocationCondition;
import io.lumine.mythic.bukkit.utils.numbers.RangedDouble;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.utils.annotations.MythicCondition;
import io.lumine.mythic.core.utils.annotations.MythicField;

@MythicCondition(
   author = "Ashijin",
   name = "playersOnline",
   aliases = {"onlinePlayerCount", "onlinePlayers"},
   description = "Matches how many players are online on the server"
)
public class PlayersOnlineCondition extends SkillCondition implements ILocationCondition {
   @MythicField(
      name = "amount",
      aliases = {"a"},
      description = "The number range to match"
   )
   private RangedDouble amount;

   public PlayersOnlineCondition(String line, MythicLineConfig mlc) {
      super(line);
      String a = mlc.getString(new String[]{"amount", "a"}, "0", this.conditionVar);
      this.amount = new RangedDouble(a);
   }

   @Override
   public boolean check(AbstractLocation location) {
      return this.amount.equals(getPlugin().getBootstrap().getOnlinePlayerCount());
   }
}
