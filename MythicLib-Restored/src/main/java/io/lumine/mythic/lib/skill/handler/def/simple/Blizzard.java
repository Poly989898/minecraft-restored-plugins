package io.lumine.mythic.lib.skill.handler.def.simple;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.api.util.TemporaryListener;
import io.lumine.mythic.lib.skill.SkillMetadata;
import io.lumine.mythic.lib.skill.handler.SkillHandler;
import io.lumine.mythic.lib.skill.result.def.SimpleSkillResult;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class Blizzard extends SkillHandler<SimpleSkillResult> {
   public Blizzard() {
      this.registerModifiers("duration", "damage", "inaccuracy", "force");
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
         final Blizzard.CustomSnowballRegistry handler = new Blizzard.CustomSnowballRegistry(skillMeta.getParameter("damage"));
         int j = 0;

         public void run() {
            if (this.j++ > duration) {
               this.handler.close(200L);
               this.cancel();
            } else {
               Location loc = caster.getEyeLocation();
               loc.setPitch((float)(loc.getPitch() + (Blizzard.RANDOM.nextDouble() - 0.5) * inaccuracy));
               loc.setYaw((float)(loc.getYaw() + (Blizzard.RANDOM.nextDouble() - 0.5) * inaccuracy));
               loc.getWorld().playSound(loc, Sound.ENTITY_SNOWBALL_THROW, 1.0F, 1.0F);
               Snowball snowball = (Snowball)caster.launchProjectile(Snowball.class);
               snowball.setVelocity(loc.getDirection().multiply(1.3 * force));
               this.handler.entities.add(snowball.getUniqueId());
            }
         }
      }).runTaskTimer(MythicLib.plugin, 0L, 2L);
   }

   public static class CustomSnowballRegistry extends TemporaryListener {
      private final List<UUID> entities = new ArrayList<>();
      private final double damage;

      public CustomSnowballRegistry(double damage) {
         super();
         this.damage = damage;
      }

      @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
      public void a(EntityDamageByEntityEvent event) {
         if (this.entities.remove(event.getDamager().getUniqueId())) {
            event.setDamage(this.damage);
         }
      }
   }
}
