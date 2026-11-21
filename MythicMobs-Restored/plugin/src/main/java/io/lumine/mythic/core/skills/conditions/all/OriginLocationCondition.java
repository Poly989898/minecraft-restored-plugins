package io.lumine.mythic.core.skills.conditions.all;

import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.conditions.ISkillMetaCondition;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDouble;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.utils.annotations.MythicCondition;
import io.lumine.mythic.core.utils.annotations.MythicField;

@MythicCondition(
   author = "Ashijin",
   name = "originLocation",
   aliases = {},
   description = "Tests if the origin is a given location"
)
public class OriginLocationCondition extends SkillCondition implements ISkillMetaCondition {
   protected PlaceholderString world;
   protected PlaceholderDouble x;
   protected PlaceholderDouble y;
   protected PlaceholderDouble z;
   @MythicField(
      name = "exact",
      aliases = {"e"},
      description = "Whether to match the biome exactly"
   )
   private boolean exact;

   public OriginLocationCondition(String line, MythicLineConfig mlc) {
      super(line);
      this.exact = mlc.getBoolean(new String[]{"exact", "e"}, false);
      String coords = mlc.getString(new String[]{"location", "loc", "l", "c"}, null);
      if (coords != null) {
         String[] split = coords.split(",");

         try {
            this.x = PlaceholderDouble.of(split[0]);
            this.y = PlaceholderDouble.of(split[1]);
            this.z = PlaceholderDouble.of(split[2]);
         } catch (Exception e) {
            MythicLogger.errorConditionConfig(this, mlc, "The 'location' attribute is required and must be in the format l=x,y,z.");
            return;
         }
      } else {
         this.x = mlc.getPlaceholderDouble("x", "0");
         this.y = mlc.getPlaceholderDouble("y", "0");
         this.z = mlc.getPlaceholderDouble("z", "0");
      }
   }

   @Override
   public boolean check(SkillMetadata meta) {
      AbstractLocation location = meta.getOrigin();
      return this.exact
         ? location.getX() == this.x.get(meta) && location.getY() == this.y.get(meta) && location.getZ() == this.z.get(meta)
         : location.getBlockX() == this.x.get(meta) && location.getBlockY() == this.y.get(meta) && location.getBlockZ() == this.z.get(meta);
   }
}
