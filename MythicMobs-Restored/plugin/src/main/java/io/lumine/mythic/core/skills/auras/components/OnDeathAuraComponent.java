package io.lumine.mythic.core.skills.auras.components;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.Skill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.adapters.BukkitAttribute;
import io.lumine.mythic.bukkit.utils.Events;
import io.lumine.mythic.bukkit.utils.version.ServerVersion;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.skills.auras.Aura;
import io.lumine.mythic.core.skills.mechanics.AuraMechanic;
import io.lumine.mythic.core.utils.annotations.MythicAuraComponent;
import java.util.Optional;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;

public class OnDeathAuraComponent implements AuraComponent {
   private final Aura.AuraTracker tracker;
   private final Optional<Skill> onDeathSkill;
   private final boolean cancelEvent;

   public OnDeathAuraComponent(Aura.AuraTracker tracker, Optional<Skill> onDeathSkill, boolean cancelEvent) {
      this.tracker = tracker;
      this.onDeathSkill = onDeathSkill;
      this.cancelEvent = cancelEvent;
   }

   @Override
   public void auraStart() {
      Optional<AbstractEntity> maybeEntity = this.tracker.getEntity();
      if (!maybeEntity.isEmpty()) {
         this.tracker
            .registerAuraComponent(
               Events.subscribe(EntityDeathEvent.class, EventPriority.LOWEST)
                  .handleSubclasses()
                  .filter(event -> event.getEntity().getUniqueId().equals(maybeEntity.get().getUniqueId()))
                  .handler(event -> {
                     if (!ServerVersion.isPaper() || !event.isCancelled()) {
                        SkillMetadata meta = this.tracker.getSkillMetadata().deepClone();
                        meta.setTrigger(BukkitAdapter.adapt(this.getKiller(event)));
                        if (this.tracker.executeAuraSkill(this.onDeathSkill, meta)) {
                           this.tracker.consumeCharge();
                           if (this.cancelEvent && ServerVersion.isPaper()) {
                              LivingEntity dead = event.getEntity();
                              ActiveMob am = MythicBukkit.inst().getMobManager().getMythicMobInstance(BukkitAdapter.adapt(dead));
                              double maxHealth = dead.getAttribute(BukkitAttribute.getAttribute("MAX_HEALTH")).getValue();
                              event.setReviveHealth(resolveReviveHealth(am, maxHealth));
                              event.setCancelled(true);
                           }
                        }
                     }
                  })
            );
      }
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
            if (shooter != null) {
               return shooter;
            }
         }

         if (damager instanceof LivingEntity) {
            return (LivingEntity)damager;
         }
      }

      return event.getEntity().getKiller();
   }

   static double resolveReviveHealth(ActiveMob am, double fallbackHealth) {
      return am != null ? am.getType().getReviveHealth(am) : fallbackHealth;
   }

   @MythicAuraComponent(
      name = "ondeath",
      aliases = {"ondead"},
      description = "Triggers a skill when the aura holder dies"
   )
   public static class Factory implements AuraComponentFactory {
      private final AuraMechanic mechanic;
      private final String onDeathSkillName;
      private Optional<Skill> onDeathSkill = Optional.empty();
      private final boolean cancelEvent;

      public Factory(AuraMechanic mechanic, MythicLineConfig mlc) {
         this.mechanic = mechanic;
         this.onDeathSkillName = mlc.getString(new String[]{"ondeathskill", "od"});
         this.cancelEvent = mlc.getBoolean(new String[]{"cancelevent", "ce", "cancel"}, false);
         mechanic.getManager().queueSecondPass(() -> {
            if (this.onDeathSkillName != null) {
               this.onDeathSkill = mechanic.getManager().getSkill(mechanic.getFile(), mechanic, this.onDeathSkillName);
            }
         });
      }

      @Override
      public AuraComponent create(Aura.AuraTracker tracker) {
         return new OnDeathAuraComponent(tracker, this.onDeathSkill, this.cancelEvent);
      }
   }
}
