package io.lumine.mythic.lib.skill.handler.def.vector;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.api.util.TemporaryListener;
import io.lumine.mythic.lib.damage.DamageType;
import io.lumine.mythic.lib.player.PlayerMetadata;
import io.lumine.mythic.lib.skill.SkillMetadata;
import io.lumine.mythic.lib.skill.handler.SkillHandler;
import io.lumine.mythic.lib.skill.result.def.VectorSkillResult;
import java.util.HashSet;
import java.util.Set;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.EvokerFangs;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class Corrupted_Fangs extends SkillHandler<VectorSkillResult> {
   public Corrupted_Fangs() {
      this.registerModifiers("damage", "fangs");
   }

   public VectorSkillResult getResult(SkillMetadata meta) {
      return new VectorSkillResult(meta);
   }

   public void whenCast(final VectorSkillResult result, final SkillMetadata skillMeta) {
      final Player caster = skillMeta.getCaster().getPlayer();
      caster.getWorld().playSound(caster.getLocation(), Sound.ENTITY_WITHER_SHOOT, 2.0F, 2.0F);
      (new BukkitRunnable() {
         final Vector vec = result.getTarget().setY(0).multiply(2);
         final Location loc = caster.getLocation();
         final Corrupted_Fangs.FangsHandler handler = new Corrupted_Fangs.FangsHandler(skillMeta.getCaster(), skillMeta.getParameter("damage"));
         final double fangAmount = skillMeta.getParameter("fangs");
         double ti = 0.0;

         public void run() {
            if (this.ti++ >= this.fangAmount) {
               this.handler.close(60L);
               this.cancel();
            } else {
               this.loc.add(this.vec);
               EvokerFangs evokerFangs = (EvokerFangs)caster.getWorld().spawnEntity(this.loc, EntityType.EVOKER_FANGS);
               this.handler.entities.add(evokerFangs.getEntityId());
            }
         }
      }).runTaskTimer(MythicLib.plugin, 0L, 1L);
   }

   public static class FangsHandler extends TemporaryListener {
      private final Set<Integer> entities = new HashSet<>();
      private final PlayerMetadata caster;
      private final double skillDamage;

      public FangsHandler(PlayerMetadata caster, double skillDamage) {
         super();
         this.caster = caster;
         this.skillDamage = skillDamage;
      }

      @EventHandler(priority = EventPriority.LOWEST)
      public void a(EntityDamageByEntityEvent event) {
         if (event.getDamager() instanceof EvokerFangs && this.entities.contains(event.getDamager().getEntityId())) {
            event.setCancelled(true);
            if (UtilityMethods.canTarget(this.caster.getPlayer(), event.getEntity())) {
               this.caster.attack((LivingEntity)event.getEntity(), this.skillDamage, DamageType.MAGIC, DamageType.SKILL);
            }
         }
      }
   }
}
