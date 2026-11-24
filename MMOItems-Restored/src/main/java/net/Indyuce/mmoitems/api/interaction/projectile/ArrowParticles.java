package net.Indyuce.mmoitems.api.interaction.projectile;

import io.lumine.mythic.lib.api.item.NBTItem;
import io.lumine.mythic.lib.gson.JsonParser;
import io.lumine.mythic.lib.player.particle.ParticleInformation;
import net.Indyuce.mmoitems.MMOItems;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.scheduler.BukkitRunnable;

public class ArrowParticles extends BukkitRunnable {
   private final AbstractArrow arrow;
   private final ParticleInformation particleInfo;

   public ArrowParticles(AbstractArrow var1, NBTItem var2) {
      this.arrow = var1;
      this.particleInfo = new ParticleInformation(JsonParser.parseString(var2.getString("MMOITEMS_ARROW_PARTICLES")).getAsJsonObject());
      this.runTaskTimer(MMOItems.plugin, 0L, 1L);
   }

   public void run() {
      if (!this.arrow.isDead() && !this.arrow.isOnGround()) {
         this.particleInfo.display(this.arrow.getLocation().add(0.0, 0.0, 0.0));
      } else {
         this.cancel();
      }
   }
}
