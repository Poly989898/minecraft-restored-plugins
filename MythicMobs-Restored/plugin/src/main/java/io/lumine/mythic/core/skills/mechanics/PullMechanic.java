package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDouble;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

@MythicMechanic(
   author = "Ashijin",
   name = "pull",
   description = "Pulls the target entity towards the caster"
)
public class PullMechanic extends SkillMechanic implements ITargetedEntitySkill {
   protected boolean toOrigin;
   protected PlaceholderDouble velocity;

   public PullMechanic(SkillExecutor manager, File file, String line, MythicLineConfig mlc) {
      super(manager, file, line, mlc);
      this.velocity = mlc.getPlaceholderDouble(new String[]{"velocity", "v"}, 1.0);
      this.toOrigin = mlc.getBoolean(new String[]{"toorigin", "to"}, false);
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      double velocity = this.velocity.get(data, target) / 10.0;
      Location l;
      if (this.toOrigin) {
         l = BukkitAdapter.adapt(data.getOrigin());
      } else {
         l = BukkitAdapter.adapt(data.getCaster().getEntity()).getLocation();
      }

      Entity t = BukkitAdapter.adapt(target);
      double distance = l.distance(t.getLocation());
      double modxz = distance * 0.5 * velocity;
      double mody = distance * 0.34 * velocity;
      mody = l.getY() - target.getLocation().getY() != 0.0 ? mody * (Math.abs(l.getY() - target.getLocation().getY()) * 0.5) : mody;
      Vector v = t.getLocation().toVector().subtract(l.toVector()).normalize().multiply(velocity);
      v.setX(v.getX() * -1.0 * modxz);
      v.setZ(v.getZ() * -1.0 * modxz);
      v.setY(v.getY() * -1.0 * mody);
      if (v.length() > 4.0) {
         v = v.normalize().multiply(4);
      }

      if (Double.isNaN(v.getX())) {
         v.setX(0);
      }

      if (Double.isNaN(v.getY())) {
         v.setY(0);
      }

      if (Double.isNaN(v.getZ())) {
         v.setZ(0);
      }

      t.setVelocity(v);
      return SkillResult.SUCCESS;
   }
}
