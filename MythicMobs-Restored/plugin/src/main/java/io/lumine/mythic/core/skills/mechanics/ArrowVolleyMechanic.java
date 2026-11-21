package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.adapters.SkillAdapter;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.ITargetedLocationSkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDuration;
import io.lumine.mythic.api.skills.placeholders.PlaceholderFloat;
import io.lumine.mythic.api.skills.placeholders.PlaceholderInt;
import io.lumine.mythic.bukkit.utils.numbers.DurationUnit;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.utils.annotations.MythicField;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;

@MythicMechanic(
   author = "Ashijin",
   name = "arrowvolley",
   description = "Shoots a volley of arrows"
)
public class ArrowVolleyMechanic extends SkillMechanic implements ITargetedEntitySkill, ITargetedLocationSkill {
   @MythicField(
      name = "amount",
      aliases = {"arrows", "a"},
      description = "The number of arrows in the volley",
      defValue = "20"
   )
   protected PlaceholderInt amount;
   @MythicField(
      name = "spread",
      aliases = {"s"},
      description = "How spread out the arrows are",
      defValue = "45"
   )
   protected PlaceholderInt spread;
   @MythicField(
      name = "fireTicks",
      aliases = {"ft", "f"},
      description = "The duration hit entities will burn for in ticks",
      defValue = "0"
   )
   protected PlaceholderDuration fireTicks;
   @MythicField(
      name = "removeDelay",
      aliases = {"rd", "r"},
      description = "The time the arrows will stay before disappearing",
      defValue = "200"
   )
   protected PlaceholderDuration removeDelay;
   @MythicField(
      name = "velocity",
      aliases = {"v"},
      description = "The velocity of the arrows",
      defValue = "20"
   )
   protected PlaceholderFloat velocity;
   @MythicField(
      name = "pickup",
      aliases = {"canPickup"},
      description = "If the arrows can be picked up",
      defValue = "true"
   )
   protected boolean canPickup;

   public ArrowVolleyMechanic(SkillExecutor manager, File file, String line, MythicLineConfig mlc) {
      super(manager, file, line, mlc);
      this.threadSafetyLevel = ThreadSafetyLevel.SYNC_ONLY;
      this.amount = mlc.getPlaceholderInteger(new String[]{"amount", "arrows", "a"}, 20);
      this.spread = mlc.getPlaceholderInteger(new String[]{"spread", "s"}, 45);
      this.fireTicks = mlc.getPlaceholderDuration(new String[]{"fireticks", "ft", "f"}, DurationUnit.TICKS, "0");
      this.velocity = mlc.getPlaceholderFloat(new String[]{"velocity", "v"}, 20.0F);
      this.removeDelay = mlc.getPlaceholderDuration(new String[]{"removedelay", "rd", "r"}, DurationUnit.TICKS, "200");
      this.canPickup = mlc.getBoolean(new String[]{"pickup", "canPickup"}, true);
   }

   @Override
   public SkillResult castAtLocation(SkillMetadata data, AbstractLocation target) {
      SkillAdapter.get()
         .executeVolley(
            data.getCaster(),
            target,
            this.amount.get(data),
            this.velocity.get(data) / 10.0F,
            this.spread.get(data),
            this.fireTicks.get(data).getTicks(),
            this.removeDelay.get(data).getTicks(),
            this.canPickup
         );
      return SkillResult.SUCCESS;
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      SkillAdapter.get()
         .executeVolley(
            data.getCaster(),
            target.getLocation(),
            this.amount.get(data, target),
            this.velocity.get(data, target) / 10.0F,
            this.spread.get(data, target),
            this.fireTicks.get(data, target).getTicks(),
            this.removeDelay.get(data, target).getTicks(),
            this.canPickup
         );
      return SkillResult.SUCCESS;
   }
}
