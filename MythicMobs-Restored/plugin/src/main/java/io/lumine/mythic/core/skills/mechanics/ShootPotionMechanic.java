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
import io.lumine.mythic.bukkit.utils.version.MinecraftVersions;
import io.lumine.mythic.bukkit.utils.version.ServerVersion;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.placeholders.PlaceholderContext;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

@MythicMechanic(
   author = "Ashijin",
   name = "shootpotion",
   description = "Shoots a splash potion"
)
public class ShootPotionMechanic extends PotionMechanic implements ITargetedEntitySkill, ITargetedLocationSkill {
   protected PlaceholderFloat velocity;

   public ShootPotionMechanic(SkillExecutor manager, File file, String line, MythicLineConfig mlc) {
      super(manager, file, line, mlc);
      this.threadSafetyLevel = ThreadSafetyLevel.SYNC_ONLY;
      this.velocity = mlc.getPlaceholderFloat(new String[]{"velocity", "v"}, 1.0F);
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      PlaceholderContext context = PlaceholderContext.builder().meta(data).entity(target).build();
      return this.cast(context);
   }

   @Override
   public SkillResult castAtLocation(SkillMetadata data, AbstractLocation target) {
      PlaceholderContext context = PlaceholderContext.builder().meta(data).location(target).build();
      return this.cast(context);
   }

   public SkillResult cast(PlaceholderContext context) {
      String potionEffect = this.effect.get(context);
      int duration = this.duration.get(context).getTicks();
      int lvl = this.lvl.get(context);

      PotionEffect effect;
      try {
         if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_15)) {
            effect = new PotionEffect(PotionEffectType.getByName(potionEffect), duration, lvl, this.hasParticles, this.hasIcon);
         } else {
            effect = new PotionEffect(PotionEffectType.getByName(potionEffect), duration, lvl);
         }
      } catch (Exception e) {
         MythicLogger.errorMechanicConfig(this, this.config, "The 'type' attribute must be a valid potion type.");
         return SkillResult.INVALID_CONFIG;
      }

      ItemStack potion = new ItemStack(Material.SPLASH_POTION);
      PotionMeta pm = (PotionMeta)potion.getItemMeta();
      pm.addCustomEffect(effect, true);
      potion.setItemMeta(pm);
      LivingEntity bukkitShooter = (LivingEntity)context.meta().getCaster().getEntity().getBukkitEntity();
      Projectile projectile = bukkitShooter.launchProjectile(ThrownPotion.class);
      ((ThrownPotion)projectile).setItem(potion);
      projectile.setVelocity(bukkitShooter.getLocation().getDirection().multiply(this.velocity.get(context)));
      projectile.setBounce(false);
      projectile.setShooter(bukkitShooter);
      return SkillResult.SUCCESS;
   }
}
