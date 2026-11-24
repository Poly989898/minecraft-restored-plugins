package io.lumine.mythic.lib.script.mechanic.misc;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.script.Script;
import io.lumine.mythic.lib.script.mechanic.Mechanic;
import io.lumine.mythic.lib.script.mechanic.MechanicMetadata;
import io.lumine.mythic.lib.script.targeter.EntityTargeter;
import io.lumine.mythic.lib.script.targeter.LocationTargeter;
import io.lumine.mythic.lib.script.variable.def.IntegerVariable;
import io.lumine.mythic.lib.skill.SkillMetadata;
import io.lumine.mythic.lib.util.DoubleFormula;
import io.lumine.mythic.lib.util.configobject.ConfigObject;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Nullable;
import org.apache.commons.lang.Validate;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

@MechanicMetadata
public class ScriptMechanic extends Mechanic {
   private final Script skill;
   private final DoubleFormula iterations;
   @Nullable
   private final String counterVarName;
   @Nullable
   private final LocationTargeter targetLocation;
   @Nullable
   private final LocationTargeter sourceLocation;
   @Nullable
   private final EntityTargeter targetEntity;

   public ScriptMechanic(ConfigObject config) {
      config.validateKeys("name");
      this.counterVarName = config.getString("counter", "counter");
      this.iterations = config.getDoubleFormula("iterations", DoubleFormula.constant(1.0));
      this.sourceLocation = config.contains("source") ? config.getLocationTargeter("source") : null;
      this.targetEntity = config.contains("target") ? config.getEntityTargeter("target") : null;
      this.targetLocation = config.contains("target_location") ? config.getLocationTargeter("target_location") : null;
      this.skill = MythicLib.plugin.getSkills().getScriptOrThrow(config.getString("name"));
   }

   @Override
   public void cast(SkillMetadata meta) {
      if (this.counterVarName == null) {
         this.castWithNewMeta(meta);
      } else {
         int ni = (int)this.iterations.evaluate(meta);
         Validate.isTrue(ni >= 0, "Number of iterations must be positive");

         for (int i = 0; i < ni; i++) {
            meta.getVariableList().registerVariable(new IntegerVariable(this.counterVarName, i + 1));
            this.castWithNewMeta(meta);
         }
      }
   }

   private void castWithNewMeta(SkillMetadata old) {
      if (this.sourceLocation == null && this.targetEntity == null && this.targetLocation == null) {
         this.skill.cast(old);
      } else {
         Location sourceLocation = this.sourceLocation == null ? old.getSourceLocation() : this.sourceLocation.findTargets(old).get(0);
         List<Entity> newTargetEntities = this.targetEntity == null ? Arrays.asList(old.getTargetEntityOrNull()) : this.targetEntity.findTargets(old);

         for (Location targetLocation : this.targetLocation == null ? Arrays.asList(old.getTargetLocationOrNull()) : this.targetLocation.findTargets(old)) {
            for (Entity targetEntity : newTargetEntities) {
               this.skill.cast(old.clone(sourceLocation, targetLocation, targetEntity, old.getOrientationOrNull()));
            }
         }
      }
   }
}
