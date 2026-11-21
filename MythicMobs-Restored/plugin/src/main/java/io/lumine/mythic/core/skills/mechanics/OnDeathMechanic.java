package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.IParentSkill;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.Skill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.utils.Events;
import io.lumine.mythic.bukkit.utils.version.ServerVersion;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.auras.Aura;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;
import java.util.Optional;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;

@MythicMechanic(
   author = "Phil",
   name = "ondeath",
   description = "Applies an aura to the target that triggers a skill when they die",
   deprecated = true
)
public class OnDeathMechanic extends Aura implements ITargetedEntitySkill {
   protected Optional<Skill> onDeathSkill = Optional.empty();
   protected String onDeathSkillName;

   public OnDeathMechanic(SkillExecutor manager, File file, String line, MythicLineConfig mlc) {
      super(manager, file, line, mlc);
      this.cancelOnDeath = false;
      this.onDeathSkillName = mlc.getString(new String[]{"ondeathskill", "od"});
      this.getManager().queueSecondPass(() -> {
         if (this.onDeathSkillName != null) {
            this.onDeathSkill = this.getManager().getSkill(file, this, this.onDeathSkillName);
         }
      });
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      new OnDeathMechanic.Tracker(data, target);
      return SkillResult.SUCCESS;
   }

   protected LivingEntity getKiller(EntityDeathEvent event) {
      EntityDamageEvent entityDamageEvent = event.getEntity().getLastDamageCause();
      if (entityDamageEvent != null && !entityDamageEvent.isCancelled() && entityDamageEvent instanceof EntityDamageByEntityEvent) {
         Entity damager = ((EntityDamageByEntityEvent)entityDamageEvent).getDamager();
         if (damager instanceof Projectile) {
            if (!(((Projectile)damager).getShooter() instanceof LivingEntity)) {
               return event.getEntity().getKiller();
            }

            LivingEntity shooter = (LivingEntity)((Projectile)damager).getShooter();
            if (shooter != null && shooter instanceof LivingEntity) {
               return shooter;
            }
         }

         if (damager instanceof LivingEntity) {
            return (LivingEntity)damager;
         }
      }

      return event.getEntity().getKiller();
   }

   private class Tracker extends Aura.AuraTracker implements IParentSkill, Runnable {
      public Tracker(SkillMetadata data, AbstractEntity entity) {
         super(entity, data);
         this.start();
      }

      @Override
      public void auraStart() {
         this.registerAuraComponent(
            Events.subscribe(EntityDeathEvent.class)
               .handleSubclasses()
               .filter(event -> event.getEntity().getUniqueId().equals(this.entity.get().getUniqueId()))
               .handler(event -> {
                  if (!ServerVersion.isPaper() || !event.isCancelled()) {
                     SkillMetadata meta = this.skillMetadata.deepClone();
                     meta.setTrigger(BukkitAdapter.adapt(OnDeathMechanic.this.getKiller(event)));
                     if (this.executeAuraSkill(OnDeathMechanic.this.onDeathSkill, meta)) {
                        this.consumeCharge();
                     }
                  }
               })
         );
         this.executeAuraSkill(OnDeathMechanic.this.onStartSkill, this.skillMetadata);
      }
   }
}
