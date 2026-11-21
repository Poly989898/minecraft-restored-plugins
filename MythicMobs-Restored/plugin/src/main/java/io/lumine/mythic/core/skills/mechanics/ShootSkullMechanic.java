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
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.WitherSkull;
import org.bukkit.util.Vector;

@MythicMechanic(
   author = "Ashijin",
   name = "shootSkull",
   aliases = {"shootWitherSkull", "witherSkull", "skull"},
   description = "Shoots a wither skull towards the target location"
)
public class ShootSkullMechanic extends SkillMechanic implements ITargetedEntitySkill, ITargetedLocationSkill {
   protected PlaceholderFloat yield;
   protected boolean playsound;

   public ShootSkullMechanic(SkillExecutor manager, File file, String line, MythicLineConfig mlc) {
      super(manager, file, line, mlc);
      this.threadSafetyLevel = ThreadSafetyLevel.SYNC_ONLY;
      this.yield = mlc.getPlaceholderFloat(new String[]{"yield", "strength", "y", "s"}, 1.0F);
      this.playsound = mlc.getBoolean("playsound", false);
      this.playsound = mlc.getBoolean("ps", this.playsound);
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      return this.castAtLocation(data, target.getLocation());
   }

   @Override
   public SkillResult castAtLocation(SkillMetadata data, AbstractLocation target) {
      Entity l = BukkitAdapter.adapt(data.getCaster().getEntity());
      Location t = BukkitAdapter.adapt(target);
      WitherSkull fireball;
      if (l instanceof Creature && ((Creature)l).getTarget() == target) {
         fireball = (WitherSkull)((LivingEntity)l).launchProjectile(WitherSkull.class);
      } else {
         Vector facing = t.toVector().subtract(l.getLocation().toVector()).normalize();
         Location loc = l.getLocation().clone();
         double yaw = Math.toDegrees(Math.atan2(-facing.getX(), facing.getZ()));
         double pitch = Math.toDegrees(-Math.asin(facing.getY()));
         loc.setYaw((float)yaw);
         loc.setPitch((float)pitch);
         loc.add(facing.multiply(2));
         fireball = (WitherSkull)l.getLocation().getWorld().spawn(loc, WitherSkull.class);
      }

      if (this.playsound) {
         l.getWorld().playEffect(l.getLocation(), Effect.GHAST_SHOOT, 0);
      }

      fireball.setBounce(false);
      fireball.setYield(this.yield.get(data));
      if (l instanceof LivingEntity) {
         fireball.setShooter((LivingEntity)l);
      }

      return SkillResult.SUCCESS;
   }
}
