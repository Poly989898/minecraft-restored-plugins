package io.lumine.mythic.lib.skill.handler.def.target;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.api.event.AttackEvent;
import io.lumine.mythic.lib.skill.SkillMetadata;
import io.lumine.mythic.lib.skill.handler.SkillHandler;
import io.lumine.mythic.lib.skill.result.def.TargetSkillResult;
import io.lumine.mythic.lib.version.VParticle;
import io.lumine.mythic.lib.version.VSound;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle.DustOptions;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class Weaken_Target extends SkillHandler<TargetSkillResult> implements Listener {
   private static final Map<UUID, Double> MARKED_ENTITIES = new HashMap<>();

   public Weaken_Target() {
      this.registerModifiers("duration", "extra-damage");
   }

   public TargetSkillResult getResult(SkillMetadata meta) {
      return new TargetSkillResult(meta);
   }

   public void whenCast(TargetSkillResult result, final SkillMetadata skillMeta) {
      final LivingEntity target = result.getTarget();
      MARKED_ENTITIES.put(target.getUniqueId(), 1.0 + skillMeta.getParameter("extra-damage") / 100.0);
      this.playWeakenEffect(target.getLocation());
      target.getWorld().playSound(target.getLocation(), VSound.ENTITY_ENDERMAN_HURT.get(), 2.0F, 1.5F);
      (new BukkitRunnable() {
         final long expire = System.currentTimeMillis() + (long)(skillMeta.getParameter("duration") * 1000.0);

         public void run() {
            if (Weaken_Target.MARKED_ENTITIES.containsKey(target.getUniqueId()) && this.expire >= System.currentTimeMillis()) {
               for (double j = 0.0; j < Math.PI * 2; j += Math.PI / 18) {
                  target.getWorld().spawnParticle(VParticle.SMOKE.get(), target.getLocation().clone().add(Math.cos(j) * 0.7, 0.1, Math.sin(j) * 0.7), 0);
               }
            } else {
               this.cancel();
            }
         }
      }).runTaskTimer(MythicLib.plugin, 0L, 20L);
   }

   @EventHandler
   public void a(AttackEvent event) {
      if (event.toBukkit().getCause() == DamageCause.ENTITY_ATTACK
         || event.toBukkit().getCause() == DamageCause.ENTITY_EXPLOSION
         || event.toBukkit().getCause() == DamageCause.PROJECTILE) {
         Entity entity = event.getEntity();
         Double found = MARKED_ENTITIES.get(entity.getUniqueId());
         if (found != null) {
            event.getDamage().multiplicativeModifier(found);
            this.playWeakenEffect(entity.getLocation());
            MARKED_ENTITIES.remove(entity.getUniqueId());
            entity.getWorld().playSound(entity.getLocation(), VSound.ENTITY_ENDERMAN_DEATH.get(), 2.0F, 2.0F);
         }
      }
   }

   @EventHandler
   public void removeMark(PlayerItemConsumeEvent event) {
      Player player = event.getPlayer();
      ItemStack item = event.getItem();
      if (item.getType() == Material.MILK_BUCKET && MARKED_ENTITIES.containsKey(player.getUniqueId())) {
         MARKED_ENTITIES.remove(player.getUniqueId());
         player.getWorld().playSound(player.getLocation(), VSound.ENTITY_ENDERMAN_DEATH.get(), 2.0F, 2.0F);
      }
   }

   private void playWeakenEffect(final Location loc) {
      (new BukkitRunnable() {
            double y = 0.0;

            public void run() {
               for (int j = 0; j < 3; j++) {
                  this.y += 0.07;

                  for (int k = 0; k < 3; k++) {
                     loc.getWorld()
                        .spawnParticle(
                           VParticle.REDSTONE.get(),
                           loc.clone()
                              .add(
                                 Math.cos(this.y * Math.PI + k * Math.PI * 2.0 / 3.0) * (3.0 - this.y) / 2.5,
                                 this.y,
                                 Math.sin(this.y * Math.PI + k * Math.PI * 2.0 / 3.0) * (3.0 - this.y) / 2.5
                              ),
                           1,
                           new DustOptions(Color.BLACK, 1.0F)
                        );
                  }
               }

               if (this.y > 3.0) {
                  this.cancel();
               }
            }
         })
         .runTaskTimer(MythicLib.plugin, 0L, 1L);
   }
}
