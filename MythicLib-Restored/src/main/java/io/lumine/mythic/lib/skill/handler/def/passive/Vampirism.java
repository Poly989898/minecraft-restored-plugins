package io.lumine.mythic.lib.skill.handler.def.passive;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.api.event.PlayerAttackEvent;
import io.lumine.mythic.lib.damage.DamageType;
import io.lumine.mythic.lib.player.skill.PassiveSkill;
import io.lumine.mythic.lib.skill.SkillMetadata;
import io.lumine.mythic.lib.skill.handler.SkillHandler;
import io.lumine.mythic.lib.skill.result.def.AttackSkillResult;
import io.lumine.mythic.lib.skill.trigger.TriggerMetadata;
import io.lumine.mythic.lib.skill.trigger.TriggerType;
import io.lumine.mythic.lib.version.VParticle;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.Particle.DustOptions;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

public class Vampirism extends SkillHandler<AttackSkillResult> implements Listener {
   public Vampirism() {
      super(false);
      this.registerModifiers("drain");
   }

   public AttackSkillResult getResult(SkillMetadata meta) {
      return new AttackSkillResult(meta);
   }

   public void whenCast(AttackSkillResult result, SkillMetadata skillMeta) {
      final LivingEntity target = result.getTarget();
      Player caster = skillMeta.getCaster().getPlayer();
      (new BukkitRunnable() {
            final Location loc = target.getLocation();
            double ti = 0.0;
            double dis = 0.0;

            public void run() {
               for (int j1 = 0; j1 < 4; j1++) {
                  this.ti += 0.75;
                  this.dis = this.dis + (this.ti <= 10.0 ? 0.15 : -0.15);

                  for (double j = 0.0; j < Math.PI * 2; j += Math.PI / 4) {
                     this.loc
                        .getWorld()
                        .spawnParticle(
                           VParticle.REDSTONE.get(),
                           this.loc.clone().add(Math.cos(j + this.ti / 20.0) * this.dis, 0.0, Math.sin(j + this.ti / 20.0) * this.dis),
                           1,
                           new DustOptions(Color.RED, 1.0F)
                        );
                  }
               }

               if (this.ti >= 17.0) {
                  this.cancel();
               }
            }
         })
         .runTaskTimer(MythicLib.plugin, 0L, 1L);
      target.getWorld().playSound(target.getLocation(), Sound.ENTITY_WITCH_DRINK, 1.0F, 2.0F);
      UtilityMethods.heal(caster, skillMeta.getAttackSource().getDamage().getDamage() * skillMeta.getParameter("drain") / 100.0);
   }

   @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
   public void a(PlayerAttackEvent event) {
      if (event.getAttack().getDamage().hasType(DamageType.WEAPON)) {
         PassiveSkill skill = event.getAttacker().getData().getPassiveSkillMap().getSkill(this);
         if (skill != null) {
            skill.getTriggeredSkill().cast(new TriggerMetadata(event, TriggerType.API));
         }
      }
   }
}
