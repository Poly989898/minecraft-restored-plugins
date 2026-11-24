package io.lumine.mythic.lib.skill.handler.def.simple;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.skill.SkillMetadata;
import io.lumine.mythic.lib.skill.handler.SkillHandler;
import io.lumine.mythic.lib.skill.result.def.SimpleSkillResult;
import io.lumine.mythic.lib.version.VMaterial;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class Hoearthquake extends SkillHandler<SimpleSkillResult> {
   public SimpleSkillResult getResult(SkillMetadata meta) {
      return new SimpleSkillResult(meta.getCaster().getPlayer().isOnGround());
   }

   public void whenCast(SimpleSkillResult result, SkillMetadata skillMeta) {
      final Player caster = skillMeta.getCaster().getPlayer();
      (new BukkitRunnable() {
         final Vector vec = caster.getEyeLocation().getDirection().setY(0);
         final Location loc = caster.getLocation();
         int ti = 0;

         public void run() {
            if (this.ti++ > 20) {
               this.cancel();
            }

            this.loc.add(this.vec);
            this.loc.getWorld().playSound(this.loc, Sound.BLOCK_GRAVEL_BREAK, 2.0F, 1.0F);
            this.loc.getWorld().spawnParticle(Particle.CLOUD, this.loc, 1, 0.5, 0.0, 0.5, 0.0);

            for (int x = -1; x < 2; x++) {
               for (int z = -1; z < 2; z++) {
                  Block b = this.loc.clone().add(x, -1.0, z).getBlock();
                  if (b.getType() == VMaterial.GRASS_BLOCK.get() || b.getType() == Material.DIRT) {
                     BlockBreakEvent event = new BlockBreakEvent(b, caster);
                     event.setDropItems(false);
                     Bukkit.getPluginManager().callEvent(event);
                     if (!event.isCancelled()) {
                        b.setType(Material.FARMLAND);
                     }
                  }
               }
            }
         }
      }).runTaskTimer(MythicLib.plugin, 0L, 1L);
   }
}
