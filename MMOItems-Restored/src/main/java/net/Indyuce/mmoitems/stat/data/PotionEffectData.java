package net.Indyuce.mmoitems.stat.data;

import java.util.Objects;
import net.Indyuce.mmoitems.util.MMOUtils;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PotionEffectData {
   private final PotionEffectType type;
   private final double duration;
   private final int level;

   public PotionEffectData(PotionEffectType potionEffectType, int count) {
      this(potionEffectType, MMOUtils.getEffectDuration(potionEffectType) / 20.0, count);
   }

   public PotionEffectData(PotionEffect potionEffect) {
      this.type = potionEffect.getType();
      this.duration = potionEffect.getDuration() / 20.0;
      this.level = potionEffect.getAmplifier() + 1;
   }

   public PotionEffectData(PotionEffectType potionEffectType, double value, int count) {
      this.type = potionEffectType;
      this.duration = value;
      this.level = count;
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
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else if (obj != null && this.getClass() == obj.getClass()) {
         PotionEffectData potionEffectData = (PotionEffectData)obj;
         return Double.compare(potionEffectData.duration, this.duration) == 0 && this.level == potionEffectData.level && this.type.equals(potionEffectData.type);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.type, this.duration, this.level);
   }
}
