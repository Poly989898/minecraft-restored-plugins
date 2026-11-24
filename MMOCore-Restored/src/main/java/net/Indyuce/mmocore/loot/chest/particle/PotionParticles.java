package net.Indyuce.mmocore.loot.chest.particle;

import io.lumine.mythic.lib.version.VParticle;
import net.Indyuce.mmocore.MMOCore;
import org.bukkit.Color;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.scheduler.BukkitRunnable;

public class PotionParticles extends BukkitRunnable {
   private double r;
   private double g;
   private double b;
   private final ThrownPotion potion;
   private boolean valid = true;

   public PotionParticles(ThrownPotion potion) {
      this.potion = potion;
      Color color = ((PotionMeta)potion.getItem().getItemMeta()).getColor();
      if (color == null) {
         this.valid = false;
      } else {
         this.r = Math.max(0.00392156862745098, this.ratio(color.getRed()));
         this.g = this.ratio(color.getGreen());
         this.b = this.ratio(color.getBlue());
      }
   }

   public void start() {
      if (this.valid) {
         this.runTaskTimer(MMOCore.plugin, 0L, 1L);
      }
   }

   private double ratio(int l) {
      return l / 255.0;
   }

   public void run() {
      if (this.potion != null && !this.potion.isDead()) {
         this.potion.getWorld().spawnParticle(VParticle.ENTITY_EFFECT.get(), this.potion.getLocation(), 0, this.r, this.g, this.b);
      } else {
         this.cancel();
      }
   }
}
