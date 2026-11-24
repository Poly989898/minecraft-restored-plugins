package io.lumine.mythic.lib.skill.handler.def.target;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.api.event.AttackEvent;
import io.lumine.mythic.lib.api.player.MMOPlayerData;
import io.lumine.mythic.lib.comp.interaction.InteractionType;
import io.lumine.mythic.lib.skill.SkillMetadata;
import io.lumine.mythic.lib.skill.handler.SkillHandler;
import io.lumine.mythic.lib.skill.result.def.TargetSkillResult;
import io.lumine.mythic.lib.version.VParticle;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

public class Human_Shield extends SkillHandler<TargetSkillResult> {
   public Human_Shield() {
      this.registerModifiers("reduction", "redirect", "duration", "low");
   }

   public TargetSkillResult getResult(SkillMetadata meta) {
      TargetSkillResult result = new TargetSkillResult(meta, 7.0, InteractionType.SUPPORT_SKILL);
      return result.isSuccessful() && result.getTarget() instanceof Player ? result : new TargetSkillResult((LivingEntity)null);
   }

   public void whenCast(TargetSkillResult result, SkillMetadata skillMeta) {
      Player caster = skillMeta.getCaster().getPlayer();
      caster.getWorld().playSound(caster.getLocation(), Sound.ENTITY_BLAZE_AMBIENT, 1.0F, 1.0F);
      new Human_Shield.HumanShield(
         skillMeta.getCaster().getData(),
         (Player)result.getTarget(),
         skillMeta.getParameter("reduction"),
         skillMeta.getParameter("redirect"),
         skillMeta.getParameter("duration"),
         skillMeta.getParameter("low")
      );
   }

   public static class HumanShield extends BukkitRunnable implements Listener {
      private final MMOPlayerData caster;
      private final Player target;
      private final double damageCoefficient;
      private final double redirectRate;
      private final double duration;
      private final double minimumHealthPercentage;
      private int j;

      public HumanShield(MMOPlayerData caster, Player target, double reduction, double redirect, double duration, double low) {
         this.target = target;
         this.caster = caster;
         this.damageCoefficient = 1.0 - Math.min(1.0, reduction / 100.0);
         this.redirectRate = redirect / 100.0;
         this.duration = duration * 20.0;
         this.minimumHealthPercentage = low / 100.0;
         this.runTaskTimer(MythicLib.plugin, 0L, 1L);
         Bukkit.getPluginManager().registerEvents(this, MythicLib.plugin);
      }

      @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
      public void a(AttackEvent event) {
         if (event.getEntity().equals(this.target)) {
            double initialDamage = event.getDamage().getDamage();
            event.getDamage().multiplicativeModifier(this.damageCoefficient);
            double health = this.caster.getPlayer().getHealth() - initialDamage * this.redirectRate;
            if (health > this.caster.getPlayer().getAttribute(Attribute.MAX_HEALTH).getValue() * this.minimumHealthPercentage) {
               this.caster.getPlayer().setHealth(health);
            } else {
               this.caster.getPlayer().setHealth(1.0);
               this.close();
            }
         }
      }

      public void run() {
         if (!UtilityMethods.isInvalidated(this.caster) && !UtilityMethods.isInvalidated(this.target) && !(this.j++ >= this.duration)) {
            double a = this.j / 5.0;
            this.target
               .getWorld()
               .spawnParticle(VParticle.HAPPY_VILLAGER.get(), this.target.getLocation().add(Math.cos(a), 1.0 + Math.sin(a / 3.0) / 1.3, Math.sin(a)), 0);
         } else {
            this.close();
         }
      }

      private void close() {
         this.cancel();
         HandlerList.unregisterAll(this);
      }
   }
}
