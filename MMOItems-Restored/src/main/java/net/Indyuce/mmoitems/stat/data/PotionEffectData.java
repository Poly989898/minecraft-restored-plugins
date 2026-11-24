package net.Indyuce.mmoitems.stat.data;

import java.util.Objects;
import net.Indyuce.mmoitems.util.MMOUtils;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PotionEffectData {
   private final PotionEffectType type;
   private final double duration;
   private final int level;

   public PotionEffectData(PotionEffectType var1, int var2) {
      this(var1, MMOUtils.getEffectDuration(var1) / 20.0, var2);
   }

   public PotionEffectData(PotionEffect var1) {
      this.type = var1.getType();
      this.duration = var1.getDuration() / 20.0;
      this.level = var1.getAmplifier() + 1;
   }

   public PotionEffectData(PotionEffectType var1, double var2, int var4) {
      this.type = var1;
      this.duration = var2;
      this.level = var4;
   }

   public PotionEffectType getType() {
      return this.type;
   }

   public double getDuration() {
      return this.duration;
   }

   public int getLevel() {
      return this.level;
   }

   public PotionEffect toEffect() {
      return new PotionEffect(this.type, (int)(this.duration * 20.0), this.level - 1, true, false);
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         PotionEffectData var2 = (PotionEffectData)var1;
         return Double.compare(var2.duration, this.duration) == 0 && this.level == var2.level && this.type.equals(var2.type);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.type, this.duration, this.level);
   }
}
