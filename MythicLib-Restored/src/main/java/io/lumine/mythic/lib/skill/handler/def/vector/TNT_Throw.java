package io.lumine.mythic.lib.skill.handler.def.vector;

import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.api.util.TemporaryListener;
import io.lumine.mythic.lib.skill.SkillMetadata;
import io.lumine.mythic.lib.skill.handler.SkillHandler;
import io.lumine.mythic.lib.skill.result.def.VectorSkillResult;
import io.lumine.mythic.lib.version.VParticle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.util.Vector;

public class TNT_Throw extends SkillHandler<VectorSkillResult> {
   public TNT_Throw() {
      this.registerModifiers("force");
   }

   public VectorSkillResult getResult(SkillMetadata meta) {
      return new VectorSkillResult(meta);
   }

   public void whenCast(VectorSkillResult result, SkillMetadata skillMeta) {
      Player caster = skillMeta.getCaster().getPlayer();
      Vector vec = result.getTarget().multiply(2.0 * skillMeta.getParameter("force"));
      TNTPrimed tnt = (TNTPrimed)caster.getWorld().spawn(caster.getLocation().add(0.0, 1.0, 0.0), TNTPrimed.class);
      tnt.setFuseTicks(80);
      tnt.setVelocity(vec);
      new TNT_Throw.CancelTeamDamage(caster, tnt);
      caster.getWorld().playSound(caster.getLocation(), Sound.ENTITY_SNOWBALL_THROW, 1.0F, 0.0F);
      caster.getWorld().spawnParticle(VParticle.EXPLOSION.get(), caster.getLocation().add(0.0, 1.0, 0.0), 12, 0.0, 0.0, 0.0, 0.1);
   }

   public static class CancelTeamDamage extends TemporaryListener {
      private final Player player;
      private final TNTPrimed tnt;

      public CancelTeamDamage(Player player, TNTPrimed tnt) {
         super();
         this.player = player;
         this.tnt = tnt;
         this.close(100L);
      }

      @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
      public void a(EntityDamageByEntityEvent event) {
         if (event.getDamager().equals(this.tnt) && !UtilityMethods.canTarget(this.player, event.getEntity())) {
            event.setCancelled(true);
         }
      }

      @Override
      public void whenClosed() {
      }
   }
}
