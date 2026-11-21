package io.lumine.mythic.core.skills.conditions.all;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.conditions.IEntityCondition;
import io.lumine.mythic.api.skills.conditions.ILocationCondition;
import io.lumine.mythic.api.skills.conditions.ISkillMetaComparisonCondition;
import io.lumine.mythic.api.skills.conditions.ISkillMetaLocationComparisonCondition;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.skills.bounds.BoundsSpec;
import io.lumine.mythic.core.skills.placeholders.PlaceholderContext;
import io.lumine.mythic.core.utils.annotations.MythicCondition;
import io.lumine.mythic.core.utils.annotations.MythicField;
import io.lumine.mythic.core.utils.annotations.MythicFields;

@MythicCondition(
   author = "Archontas",
   name = "inBounds",
   aliases = {"withinBounds", "insideBounds"},
   version = "5.10.1",
   description = "Tests whether the target is inside a cuboid, sphere, or cylinder bounds shape"
)
public class InBoundsCondition
   extends SkillCondition
   implements IEntityCondition,
   ILocationCondition,
   ISkillMetaComparisonCondition,
   ISkillMetaLocationComparisonCondition {
   @MythicFields({@MythicField(
         name = "corner1",
         aliases = {"c1"},
         description = "First corner of the bounds box: location targeter, pin name, world,x,y,z, or x,y,z"
      ), @MythicField(
         name = "corner2",
         aliases = {"c2"},
         description = "Second corner of the bounds box: location targeter, pin name, world,x,y,z, or x,y,z"
      ), @MythicField(
         name = "shape",
         description = "Bounds shape: cuboid (default), sphere, or cylinder"
      ), @MythicField(
         name = "location",
         aliases = {"loc", "l", "center", "centre", "origin"},
         description = "Location for sphere/cylinder bounds: location targeter, pin name, world,x,y,z, or x,y,z"
      ), @MythicField(
         name = "radius",
         aliases = {"r"},
         description = "Radius for sphere/cylinder bounds"
      ), @MythicField(
         name = "height",
         aliases = {"h"},
         description = "Upward height for cylinder bounds"
      ), @MythicField(
         name = "x1",
         description = "Overrides the resolved first cuboid corner's X coordinate"
      ), @MythicField(
         name = "y1",
         description = "Overrides the resolved first cuboid corner's Y coordinate"
      ), @MythicField(
         name = "z1",
         description = "Overrides the resolved first cuboid corner's Z coordinate"
      ), @MythicField(
         name = "x2",
         description = "Overrides the resolved second cuboid corner's X coordinate"
      ), @MythicField(
         name = "y2",
         description = "Overrides the resolved second cuboid corner's Y coordinate"
      ), @MythicField(
         name = "z2",
         description = "Overrides the resolved second cuboid corner's Z coordinate"
      )})
   private BoundsSpec bounds;

   public InBoundsCondition(String line, MythicLineConfig mlc) {
      super(line);

      try {
         this.bounds = new BoundsSpec(mlc);
      } catch (IllegalArgumentException e) {
         MythicLogger.errorConditionConfig(this, mlc, e.getMessage());
      }
   }

   @Override
   public boolean check(AbstractEntity target) {
      if (this.bounds == null || target == null) {
         return false;
      } else {
         return this.bounds.isStatic()
            ? this.bounds.contains(target.getLocation())
            : this.bounds.contains(target.getLocation(), PlaceholderContext.builder().entity(target).build());
      }
   }

   @Override
   public boolean check(SkillMetadata meta, AbstractEntity target) {
      if (this.bounds == null || target == null) {
         return false;
      } else {
         return this.bounds.isStatic()
            ? this.bounds.contains(target.getLocation())
            : this.bounds.contains(target.getLocation(), PlaceholderContext.builder().meta(meta).entity(target).build());
      }
   }

   @Override
   public boolean evaluateTrigger(SkillMetadata meta) {
      if (this.getDispatchTarget() == this && meta.getTrigger() != null) {
         try {
            return this.handleOutcome(meta, this.check(meta, meta.getTrigger()));
         } catch (Error | Exception e) {
            MythicLogger.errorCondition(this, "An internal error occurred", e);
            return false;
         }
      } else {
         return super.evaluateTrigger(meta);
      }
   }

   @Override
   public boolean check(AbstractLocation location) {
      return this.bounds != null && this.bounds.contains(location);
   }

   @Override
   public boolean check(SkillMetadata meta, AbstractLocation location) {
      if (this.bounds == null || location == null) {
         return false;
      } else {
         return this.bounds.isStatic()
            ? this.bounds.contains(location)
            : this.bounds.contains(location, PlaceholderContext.builder().meta(meta).location(location).build());
      }
   }
}
