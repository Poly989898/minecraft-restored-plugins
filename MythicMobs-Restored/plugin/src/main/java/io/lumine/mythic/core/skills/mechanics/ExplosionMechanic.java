package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.ITargetedLocationSkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import io.lumine.mythic.api.skills.placeholders.PlaceholderFloat;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.utils.annotations.MythicField;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;

@MythicMechanic(
   author = "Ashijin",
   name = "explosion",
   aliases = {"explode"},
   description = "Causes an explosion at the target location"
)
public class ExplosionMechanic extends SkillMechanic implements ITargetedEntitySkill, ITargetedLocationSkill {
   @MythicField(
      name = "yield",
      aliases = {"y"},
      description = "The strength of the explosion",
      defValue = "0.01337"
   )
   protected PlaceholderFloat yield;
   @MythicField(
      name = "blockdamage",
      aliases = {"bd"},
      description = "Whether the explosion will damage the block",
      defValue = "false"
   )
   protected boolean blockdamage;
   @MythicField(
      name = "fire",
      aliases = {"ft", "f"},
      description = "Whether the explosion leaves fire behind",
      defValue = "false"
   )
   protected boolean fire;
   @MythicField(
      name = "usesource",
      aliases = {"us", "usecaster"},
      description = "Whether the explosion uses the caster as a source",
      defValue = "true"
   )
   protected boolean useSource;

   public ExplosionMechanic(SkillExecutor manager, File file, String line, MythicLineConfig mlc) {
      super(manager, file, line, mlc);
      this.threadSafetyLevel = ThreadSafetyLevel.SYNC_ONLY;
      this.yield = mlc.getPlaceholderFloat(new String[]{"yield", "y"}, 0.01337F);
      this.blockdamage = mlc.getBoolean(new String[]{"blockdamage", "bd"}, false);
      this.fire = mlc.getBoolean(new String[]{"fire", "ft", "f"}, false);
      this.useSource = mlc.getBoolean(new String[]{"usesource", "us", "usecaster"}, true);
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      AbstractEntity source = this.useSource ? data.getCaster().getEntity() : null;
      target.getLocation().getWorld().createExplosion(target.getLocation(), this.yield.get(data), this.fire, this.blockdamage, source);
      return SkillResult.SUCCESS;
   }

   @Override
   public SkillResult castAtLocation(SkillMetadata data, AbstractLocation target) {
      AbstractEntity source = this.useSource ? data.getCaster().getEntity() : null;
      target.getWorld().createExplosion(target, this.yield.get(data), this.fire, this.blockdamage, source);
      return SkillResult.SUCCESS;
   }
}
