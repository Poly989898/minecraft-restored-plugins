package io.lumine.mythic.lib.skill.handler.def.simple;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.api.event.PlayerAttackEvent;
import io.lumine.mythic.lib.damage.DamageType;
import io.lumine.mythic.lib.player.PlayerMetadata;
import io.lumine.mythic.lib.skill.SkillMetadata;
import io.lumine.mythic.lib.skill.handler.SkillHandler;
import io.lumine.mythic.lib.skill.result.def.SimpleSkillResult;
import io.lumine.mythic.lib.util.SmallParticleEffect;
import io.lumine.mythic.lib.version.VParticle;
import io.lumine.mythic.lib.version.VSound;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.util.Vector;

public class Empowered_Attack extends SkillHandler<SimpleSkillResult> {
   private static final double PARTICLES_PER_METER = 5.0;

   public Empowered_Attack() {
      this.registerModifiers("radius", "ratio", "extra");
   }

   public SimpleSkillResult getResult(SkillMetadata meta) {
      return new SimpleSkillResult();
   }

   public void whenCast(SimpleSkillResult result, SkillMetadata skillMeta) {
      Player caster = skillMeta.getCaster().getPlayer();
      caster.playSound(caster.getLocation(), Sound.BLOCK_END_PORTAL_FRAME_FILL, 1.0F, 1.0F);
      new Empowered_Attack.EmpoweredAttack(
         skillMeta.getCaster(), skillMeta.getParameter("extra"), skillMeta.getParameter("ratio"), skillMeta.getParameter("radius")
      );
   }

   private void drawVector(Location loc, Vector vec) {
      double steps = vec.length() * 5.0;
      Vector v = vec.clone().normalize().multiply(0.2);

      for (int j = 0; j < Math.min(steps, 124.0); j++) {
         loc.getWorld().spawnParticle(VParticle.FIREWORK.get(), loc.add(v), 0);
      }
   }

   public class EmpoweredAttack implements Listener {
      private final PlayerMetadata caster;
      private final double c;
      private final double r;
      private final double rad;

      public EmpoweredAttack(PlayerMetadata caster, double extra, double ratio, double radius) {
         this.caster = caster;
         this.c = 1.0 + extra / 100.0;
         this.r = ratio / 100.0;
         this.rad = radius;
         if (caster.getData().isOnline()) {
            new SmallParticleEffect(caster.getPlayer(), VParticle.FIREWORK.get());
         }

         Bukkit.getPluginManager().registerEvents(this, MythicLib.plugin);
         Bukkit.getScheduler().scheduleSyncDelayedTask(MythicLib.plugin, this::close, 80L);
      }

      private void close() {
         PlayerAttackEvent.getHandlerList().unregister(this);
      }

      @EventHandler
      public void a(PlayerAttackEvent event) {
         if (this.caster.getData().isOnline()) {
            if (event.getAttacker().getPlayer().equals(this.caster.getPlayer()) && event.getAttack().getDamage().hasType(DamageType.WEAPON)) {
               this.close();
               Entity target = event.getEntity();
               Location loc = target.getLocation().add(0.0, target.getHeight() / 2.0, 0.0);

               for (int j = 0; j < 3; j++) {
                  Location clone = loc.clone();
                  double a = Empowered_Attack.RANDOM.nextDouble() * Math.PI * 2.0;
                  loc.add(Math.cos(a), 5.0, Math.sin(a));
                  Empowered_Attack.this.drawVector(clone, loc.clone().subtract(clone).toVector());
               }

               target.getWorld().playSound(target.getLocation(), VSound.ENTITY_FIREWORK_ROCKET_BLAST.get(), 2.0F, 0.5F);
               target.getWorld().spawnParticle(VParticle.FIREWORK.get(), target.getLocation().add(0.0, target.getHeight() / 2.0, 0.0), 32, 0.0, 0.0, 0.0, 0.2);
               double sweep = event.getAttack().getDamage().getDamage() * this.r;
               Location src = target.getLocation().add(0.0, target.getHeight() / 2.0, 0.0);

               for (Entity entity : target.getNearbyEntities(this.rad, this.rad, this.rad)) {
                  if (UtilityMethods.canTarget(this.caster.getPlayer(), entity)) {
                     Empowered_Attack.this.drawVector(src, entity.getLocation().add(0.0, entity.getHeight() / 2.0, 0.0).subtract(src).toVector());
                     event.getAttacker().attack((LivingEntity)entity, sweep, DamageType.SKILL, DamageType.PHYSICAL);
                  }
               }

               event.getAttack().getDamage().multiplicativeModifier(this.c, DamageType.WEAPON);
            }
         }
      }
   }
}
