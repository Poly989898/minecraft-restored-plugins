package io.lumine.mythic.lib.skill.handler.def.simple;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.api.util.TemporaryListener;
import io.lumine.mythic.lib.skill.SkillMetadata;
import io.lumine.mythic.lib.skill.handler.SkillHandler;
import io.lumine.mythic.lib.skill.result.def.SimpleSkillResult;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerEggThrowEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class Chicken_Wraith extends SkillHandler<SimpleSkillResult> {
   public Chicken_Wraith() {
      this.registerModifiers("damage", "duration", "inaccuracy", "force");
   }

   public SimpleSkillResult getResult(SkillMetadata meta) {
      return new SimpleSkillResult();
   }

   public void whenCast(SimpleSkillResult result, final SkillMetadata skillMeta) {
      final double duration = skillMeta.getParameter("duration") * 10.0;
      final double force = skillMeta.getParameter("force");
      final double inaccuracy = skillMeta.getParameter("inaccuracy");
      final Player caster = skillMeta.getCaster().getPlayer();
      (new BukkitRunnable() {
         final Chicken_Wraith.CustomEggRegistry handler = new Chicken_Wraith.CustomEggRegistry(skillMeta.getParameter("damage"));
         int j = 0;

         public void run() {
            if (this.j++ > duration) {
               this.handler.close(200L);
               this.cancel();
            } else {
               Location loc = caster.getEyeLocation();
               loc.setPitch((float)(loc.getPitch() + (Chicken_Wraith.RANDOM.nextDouble() - 0.5) * inaccuracy));
               loc.setYaw((float)(loc.getYaw() + (Chicken_Wraith.RANDOM.nextDouble() - 0.5) * inaccuracy));
               loc.getWorld().playSound(loc, Sound.ENTITY_CHICKEN_EGG, 1.0F, 1.0F);
               Egg egg = (Egg)caster.launchProjectile(Egg.class);
               egg.setVelocity(loc.getDirection().multiply(1.3 * force));
               this.handler.entities.add(egg.getEntityId());
            }
         }
      }).runTaskTimer(MythicLib.plugin, 0L, 2L);
   }

   public static class CustomEggRegistry extends TemporaryListener {
      private final List<Integer> entities = new ArrayList<>();
      private final double damage;

      public CustomEggRegistry(double damage) {
         super();
         this.damage = damage;
      }

      @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
      public void a(PlayerEggThrowEvent event) {
         if (this.entities.contains(event.getEgg().getEntityId())) {
            event.setHatching(false);
         }
      }

      @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
      public void b(EntityDamageByEntityEvent event) {
         if (this.entities.contains(event.getDamager().getEntityId())) {
            event.setDamage(this.damage);
         }
      }
   }
}
