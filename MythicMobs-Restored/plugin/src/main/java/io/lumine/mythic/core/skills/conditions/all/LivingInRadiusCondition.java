package io.lumine.mythic.core.skills.conditions.all;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillCaster;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.conditions.ILocationCondition;
import io.lumine.mythic.api.skills.conditions.ISkillMetaCondition;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDouble;
import io.lumine.mythic.bukkit.utils.numbers.RangedInt;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.utils.annotations.MythicCondition;
import io.lumine.mythic.core.utils.annotations.MythicField;

@MythicCondition(
   author = "Ashijin",
   name = "livinginradius",
   aliases = {},
   description = "Matches a range to how many living entities are in the given radius"
)
public class LivingInRadiusCondition extends SkillCondition implements ISkillMetaCondition, ILocationCondition {
   @MythicField(
      name = "amount",
      aliases = {"a"},
      description = "The number range to match"
   )
   private final RangedInt amount;
   @MythicField(
      name = "radius",
      aliases = {"r"},
      description = "The radius to check"
   )
   private final PlaceholderDouble radius;

   public LivingInRadiusCondition(String line, MythicLineConfig mlc) {
      super(line);
      String k = mlc.getString(new String[]{"amount", "a"}, "1", this.conditionVar);
      this.amount = new RangedInt(k);
      this.radius = mlc.getPlaceholderDouble(new String[]{"radius", "r"}, "5", this.conditionVar);
   }

   @Override
   public boolean check(SkillMetadata meta) {
      SkillCaster caster = meta.getCaster();
      AbstractLocation location = caster.getEntity().getLocation();
      return this.check(location, this.radius.get(meta));
   }

   @Override
   public boolean check(AbstractLocation location) {
      return this.check(location, this.radius.get());
   }

   private boolean check(AbstractLocation location, double radius) {
      double radiusSq = Math.pow(radius, 2.0);
      int count = 0;

      for (AbstractEntity entity : getPlugin().getVolatileCodeHandler().getWorldHandler().getEntitiesNearLocation(location, radius)) {
         if (entity.isLiving() && location.distanceSquared(entity.getLocation()) <= radiusSq) {
            count++;
         }
      }

      return this.amount.equals(count);
   }
}
