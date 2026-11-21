package io.lumine.mythic.bukkit.adapters;

import io.lumine.mythic.api.adapters.AbstractPotionEffect;
import io.lumine.mythic.bukkit.utils.version.MinecraftVersions;
import io.lumine.mythic.bukkit.utils.version.ServerVersion;
import lombok.Generated;
import lombok.Getter;
import lombok.ToString;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

@Getter
@ToString
public class BukkitPotionEffect implements AbstractPotionEffect {
   private final String effectName;
   private final int duration;
   private final int level;
   private final boolean ambient;
   private final boolean particles;
   private final boolean icon;
   private final PotionEffect effect;

   public BukkitPotionEffect(String effectType, int duration, int level, boolean ambient, boolean particles, boolean icon) {
      this.effectName = effectType;
      this.duration = duration;
      this.level = level;
      this.ambient = ambient;
      this.particles = particles;
      this.icon = icon;
      this.effect = this.buildEffect();
   }

   public PotionEffect toBukkitEffect() {
      return this.effect;
   }

   private PotionEffect buildEffect() {
      return new PotionEffect(getEffectType(this.effectName), this.duration, this.level, this.ambient, this.particles, this.icon);
   }

   public static String getEffectName(PotionEffectType effect) {
      return ServerVersion.isBefore(MinecraftVersions.v1_20_5) ? effect.getName() : effect.getKey().getKey();
   }

   public static PotionEffectType getEffectType(String effectName) {
      if (ServerVersion.isBefore(MinecraftVersions.v1_20_5)) {
         return PotionEffectType.getByName(effectName);
      } else {
         String toUpperCase = effectName.toUpperCase();

         return switch (toUpperCase) {
            case "SPEED", "SWIFTNESS", "QUICKNESS" -> PotionEffectType.SPEED;
            case "SLOWNESS", "SLOW" -> PotionEffectType.SLOWNESS;
            case "HASTE", "QUICK_DIGGING", "FAST_MINING" -> PotionEffectType.HASTE;
            case "MINING_FATIGUE", "DIG_SLOWDOWN", "SLOW_DIGGING" -> PotionEffectType.MINING_FATIGUE;
            case "STRENGTH", "INCREASE_DAMAGE", "POWER" -> PotionEffectType.STRENGTH;
            case "INSTANT_HEALTH", "HEAL", "HEALING" -> PotionEffectType.INSTANT_HEALTH;
            case "INSTANT_DAMAGE", "HARM", "HURTING" -> PotionEffectType.INSTANT_DAMAGE;
            case "JUMP_BOOST", "JUMP", "HIGH_JUMP" -> PotionEffectType.JUMP_BOOST;
            case "NAUSEA", "CONFUSION", "SICKNESS" -> PotionEffectType.NAUSEA;
            case "REGENERATION", "REGEN", "HEAL_OVER_TIME" -> PotionEffectType.REGENERATION;
            case "RESISTANCE", "DAMAGE_RESISTANCE", "LESS_DAMAGE" -> PotionEffectType.RESISTANCE;
            case "FIRE_RESISTANCE", "FIRE_PROOF" -> PotionEffectType.FIRE_RESISTANCE;
            case "WATER_BREATHING", "AQUA_BREATHING", "UNDERWATER_BREATHING" -> PotionEffectType.WATER_BREATHING;
            case "INVISIBILITY", "INVISIBLE", "STEALTH" -> PotionEffectType.INVISIBILITY;
            case "BLINDNESS", "BLIND" -> PotionEffectType.BLINDNESS;
            case "NIGHT_VISION", "SEE_IN_DARK" -> PotionEffectType.NIGHT_VISION;
            case "HUNGER", "STARVATION" -> PotionEffectType.HUNGER;
            case "WEAKNESS", "LESS_STRENGTH" -> PotionEffectType.WEAKNESS;
            case "POISON", "TOXIN" -> PotionEffectType.POISON;
            case "WITHER", "DECAY" -> PotionEffectType.WITHER;
            case "HEALTH_BOOST", "EXTRA_HEALTH" -> PotionEffectType.HEALTH_BOOST;
            case "ABSORPTION", "EXTRA_HEARTS" -> PotionEffectType.ABSORPTION;
            case "SATURATION", "FULLNESS" -> PotionEffectType.SATURATION;
            case "GLOWING", "LUMINANCE" -> PotionEffectType.GLOWING;
            case "LEVITATION", "FLOATING" -> PotionEffectType.LEVITATION;
            case "LUCK", "GOOD_LUCK" -> PotionEffectType.LUCK;
            case "UNLUCK", "BAD_LUCK", "CURSE_OF_LUCK" -> PotionEffectType.UNLUCK;
            case "SLOW_FALLING", "SOFT_LANDING" -> PotionEffectType.SLOW_FALLING;
            case "CONDUIT_POWER", "CONDUIT_BOOST" -> PotionEffectType.CONDUIT_POWER;
            case "DOLPHINS_GRACE", "SWIM_BOOST" -> PotionEffectType.DOLPHINS_GRACE;
            case "BAD_OMEN", "OMEN", "VILLAGE_CURSE" -> PotionEffectType.BAD_OMEN;
            case "HERO_OF_THE_VILLAGE", "HEROISM", "VILLAGE_HERO" -> PotionEffectType.HERO_OF_THE_VILLAGE;
            case "DARKNESS", "BLACKOUT" -> PotionEffectType.DARKNESS;
            case "TRIAL_OMEN", "TRIAL_CURSE" -> PotionEffectType.TRIAL_OMEN;
            case "RAID_OMEN", "RAID_TRIGGER" -> PotionEffectType.RAID_OMEN;
            case "WIND_CHARGED", "GUST_CHARGE" -> PotionEffectType.WIND_CHARGED;
            case "WEAVING", "SPIDER_DEATH" -> PotionEffectType.WEAVING;
            case "OOZING", "SLIME_DEATH" -> PotionEffectType.OOZING;
            case "INFESTED", "SILVERFISH_HIT" -> PotionEffectType.INFESTED;
            default -> PotionEffectType.getByName(effectName);
         };
      }
   }

   @Generated
   @Override
   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof BukkitPotionEffect other)) {
         return false;
      } else if (!other.canEqual(this)) {
         return false;
      } else if (this.getDuration() != other.getDuration()) {
         return false;
      } else if (this.getLevel() != other.getLevel()) {
         return false;
      } else if (this.isAmbient() != other.isAmbient()) {
         return false;
      } else if (this.isParticles() != other.isParticles()) {
         return false;
      } else if (this.isIcon() != other.isIcon()) {
         return false;
      } else {
         Object this$effectName = this.getEffectName();
         Object other$effectName = other.getEffectName();
         if (this$effectName == null ? other$effectName == null : this$effectName.equals(other$effectName)) {
            Object this$effect = this.getEffect();
            Object other$effect = other.getEffect();
            return this$effect == null ? other$effect == null : this$effect.equals(other$effect);
         } else {
            return false;
         }
      }
   }

   @Generated
   protected boolean canEqual(Object other) {
      return other instanceof BukkitPotionEffect;
   }

   @Generated
   @Override
   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      result = result * 59 + this.getDuration();
      result = result * 59 + this.getLevel();
      result = result * 59 + (this.isAmbient() ? 79 : 97);
      result = result * 59 + (this.isParticles() ? 79 : 97);
      result = result * 59 + (this.isIcon() ? 79 : 97);
      Object $effectName = this.getEffectName();
      result = result * 59 + ($effectName == null ? 43 : $effectName.hashCode());
      Object $effect = this.getEffect();
      return result * 59 + ($effect == null ? 43 : $effect.hashCode());
   }
}
