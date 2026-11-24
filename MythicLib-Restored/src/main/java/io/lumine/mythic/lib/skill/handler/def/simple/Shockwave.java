package io.lumine.mythic.lib.skill.handler.def.simple;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.skill.SkillMetadata;
import io.lumine.mythic.lib.skill.handler.SkillHandler;
import io.lumine.mythic.lib.skill.result.def.SimpleSkillResult;
import io.lumine.mythic.lib.version.VParticle;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.EntityEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class Shockwave extends SkillHandler<SimpleSkillResult> {
   public Shockwave() {
      this.registerModifiers("knock-up", "length");
   }

   public SimpleSkillResult getResult(SkillMetadata meta) {
      return new SimpleSkillResult();
   }

   public void whenCast(SimpleSkillResult result, SkillMetadata skillMeta) {
      final double knockUp = skillMeta.getParameter("knock-up");
      final double length = skillMeta.getParameter("length");
      final Player caster = skillMeta.getCaster().getPlayer();
      (new BukkitRunnable() {
            final Vector vec = caster.getEyeLocation().getDirection().setY(0);
            final Location loc = caster.getLocation();
            final List<Integer> hit = new ArrayList<>();
            int ti = 0;

            public void run() {
               this.ti++;
               if (this.ti >= Math.min(20.0, length)) {
                  this.cancel();
               }

               this.loc.add(this.vec);
               this.loc.getWorld().playSound(this.loc, Sound.BLOCK_GRAVEL_BREAK, 1.0F, 2.0F);
               this.loc.getWorld().spawnParticle(VParticle.BLOCK.get(), this.loc, 12, 0.5, 0.0, 0.5, 0.0, Material.DIRT.createBlockData());

               for (Entity ent : UtilityMethods.getNearbyChunkEntities(this.loc)) {
                  if (ent.getLocation().distanceSquared(this.loc) < 1.2100000000000002
                     && UtilityMethods.canTarget(caster, ent)
                     && !this.hit.contains(ent.getEntityId())) {
                     this.hit.add(ent.getEntityId());
                     ent.playEffect(EntityEffect.HURT);
                     ent.setVelocity(ent.getVelocity().setY(0.4 * knockUp));
                  }
               }
            }
         })
         .runTaskTimer(MythicLib.plugin, 0L, 1L);
   }
}
