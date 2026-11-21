package io.lumine.mythic.core.skills.conditions.all;

import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.conditions.ILocationCondition;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDouble;
import io.lumine.mythic.bukkit.utils.numbers.RangedInt;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.utils.annotations.MythicCondition;
import io.lumine.mythic.core.utils.annotations.MythicField;

@MythicCondition(
   author = "Ashijin",
   name = "playersInRadius",
   aliases = {"pir", "playerInRadius"},
   description = "Checks for a given number of players within a radius of the target"
)
public class PlayersInRadiusCondition extends SkillCondition implements ILocationCondition {
   @MythicField(
      name = "amount",
      aliases = {"a"},
      description = "The number of players to check for"
   )
   private RangedInt amount;
   @MythicField(
      name = "radius",
      aliases = {"range", "r"},
      description = "The radius to check in"
   )
   private PlaceholderDouble distance;
   @MythicField(
      name = "ignoreSpectator",
      aliases = {"is"},
      description = "Whether to ignore non-survival players"
   )
   private boolean ignoreSpectator;

   public PlayersInRadiusCondition(String line, MythicLineConfig mlc) {
      super(line);
      String amountFallback = "0".equals(this.conditionVar) ? ">0" : this.conditionVar;
      this.amount = new RangedInt(mlc.getString(new String[]{"amount", "a"}, ">0", amountFallback));
      this.distance = mlc.getPlaceholderDouble(new String[]{"radius", "r", "distance", "d"}, "32");
      this.ignoreSpectator = mlc.getBoolean(new String[]{"ignoreSpectator", "is"}, true);
   }

   @Override
   public boolean check(AbstractLocation location) {
      double range = this.distance.get();
      double radiusSq = range * range;
      return this.amount
         .equals(
            getPlugin()
               .getVolatileCodeHandler()
               .getWorldHandler()
               .getEntitiesNearLocation(
                  location,
                  range,
                  e -> e.isPlayer()
                     && !e.asPlayer().isInCreativeMode()
                     && (!this.ignoreSpectator || !e.asPlayer().isInSpectatorMode())
                     && e.getLocation().distanceSquared(location) <= radiusSq
               )
               .size()
         );
   }
}
