package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.INoTargetSkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.placeholders.PlaceholderFloat;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.utils.annotations.MythicField;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

@MythicMechanic(
   author = "Ashijin",
   name = "jump",
   description = "Causes the caster to jump"
)
public class JumpMechanic extends SkillMechanic implements INoTargetSkill {
   @MythicField(
      name = "velocity",
      aliases = {"v"},
      description = "The velocity of the jump",
      defValue = "1"
   )
   protected PlaceholderFloat velocity;

   public JumpMechanic(SkillExecutor manager, File file, String line, MythicLineConfig mlc) {
      super(manager, file, line, mlc);
      this.velocity = mlc.getPlaceholderFloat(new String[]{"velocity", "v"}, 1.0F);
   }

   @Override
   public SkillResult cast(SkillMetadata data) {
      Entity entity = BukkitAdapter.adapt(data.getCaster().getEntity());
      Vector velocity = entity.getVelocity();
      velocity.setY(velocity.getY() + this.velocity.get(data));
      if (velocity.length() > 4.0) {
         velocity = velocity.normalize().multiply(4);
      }

      if (Double.isNaN(velocity.getX())) {
         velocity.setX(0);
      }

      if (Double.isNaN(velocity.getY())) {
         velocity.setY(0);
      }

      if (Double.isNaN(velocity.getZ())) {
         velocity.setZ(0);
      }

      entity.setVelocity(velocity);
      return SkillResult.SUCCESS;
   }
}
