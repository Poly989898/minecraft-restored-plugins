package io.lumine.mythic.core.skills.conditions.all;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.conditions.IEntityCondition;
import io.lumine.mythic.bukkit.utils.numbers.RangedDouble;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.utils.annotations.MythicCondition;
import io.lumine.mythic.core.utils.annotations.MythicField;

@MythicCondition(
   author = "jaylawl",
   name = "yaw",
   aliases = {},
   version = "4.5",
   description = "Checks the yaw of the target entity against a range."
)
public class YawCondition extends SkillCondition implements IEntityCondition {
   @MythicField(
      name = "yaw",
      aliases = {"y"},
      description = "The yaw to check for",
      defValue = "0"
   )
   private RangedDouble yaw;
   private boolean clamp;

   public YawCondition(String line, MythicLineConfig mlc) {
      super(line);
      this.yaw = new RangedDouble(mlc.getString(new String[]{"yaw", "y"}, this.conditionVar));
      this.clamp = mlc.getBoolean(new String[]{"clamp", "c"}, true);
   }

   @Override
   public boolean check(AbstractEntity e) {
      float yaw = e.getBukkitEntity().getLocation().getYaw();
      float check = this.clamp ? yaw % 360.0F : yaw;
      return this.yaw.equals(check);
   }
}
