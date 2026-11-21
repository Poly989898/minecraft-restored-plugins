package io.lumine.mythic.bukkit.adapters;

import io.lumine.mythic.api.adapters.AbstractEnchantment;
import io.lumine.mythic.bukkit.utils.version.MinecraftVersions;
import io.lumine.mythic.bukkit.utils.version.ServerVersion;
import lombok.Generated;
import lombok.Getter;
import lombok.ToString;
import org.bukkit.enchantments.Enchantment;

@Getter
@ToString
public class BukkitEnchantment implements AbstractEnchantment {
   private final String enchantmentName;
   private final Enchantment enchantment;

   public BukkitEnchantment(String enchantmentName) {
      this.enchantmentName = enchantmentName;
      this.enchantment = getEnchantment(enchantmentName);
   }

   public static Enchantment getEnchantment(String enchantmentName) {
      if (ServerVersion.isBefore(MinecraftVersions.v1_20_5)) {
         return Enchantment.getByName(enchantmentName);
      } else {
         String toUpperCase = enchantmentName.toUpperCase();

         return switch (toUpperCase) {
            case "PROTECTION", "GENERAL_PROTECTION", "PROTECTION_ENVIRONMENTAL" -> Enchantment.PROTECTION;
            case "PROTECTION_FIRE", "FIRE_PROTECTION", "FIRE_RESISTANCE" -> Enchantment.FIRE_PROTECTION;
            case "PROTECTION_FALL", "FEATHER_FALLING", "FALL_PROTECTION" -> Enchantment.FEATHER_FALLING;
            case "BLAST_PROTECTION", "PROTECTION_EXPLOSIONS", "EXPLOSION_PROTECTION" -> Enchantment.BLAST_PROTECTION;
            case "PROTECTION_PROJECTILE", "PROJECTILE_PROTECTION", "ARROW_PROTECTION" -> Enchantment.PROJECTILE_PROTECTION;
            case "OXYGEN", "RESPIRATION" -> Enchantment.RESPIRATION;
            case "WATER_WORKER", "AQUA_AFFINITY", "UNDERWATER_MINING_SPEED" -> Enchantment.AQUA_AFFINITY;
            case "THORNS" -> Enchantment.THORNS;
            case "DEPTH_STRIDER", "WATER_SPEED" -> Enchantment.DEPTH_STRIDER;
            case "FROST_WALKER", "ICE_WALKER" -> Enchantment.FROST_WALKER;
            case "CURSE_OF_BINDING", "BINDING_CURSE" -> Enchantment.BINDING_CURSE;
            case "DAMAGE_ALL", "SHARPNESS" -> Enchantment.SHARPNESS;
            case "DAMAGE_UNDEAD", "SMITE" -> Enchantment.SMITE;
            case "DAMAGE_ARTHROPODS", "BANE_OF_ARTHROPODS" -> Enchantment.BANE_OF_ARTHROPODS;
            case "KNOCKBACK", "PUSHBACK" -> Enchantment.KNOCKBACK;
            case "FIRE_ASPECT", "FIRE_DAMAGE" -> Enchantment.FIRE_ASPECT;
            case "LOOT_BONUS_MOBS", "LOOTING", "MOB_LOOTING" -> Enchantment.LOOTING;
            case "SWEEPING_EDGE", "SWEEP_DAMAGE" -> Enchantment.SWEEPING_EDGE;
            case "DIG_SPEED", "EFFICIENCY", "FAST_MINING" -> Enchantment.EFFICIENCY;
            case "SILK_TOUCH" -> Enchantment.SILK_TOUCH;
            case "DURABILITY", "UNBREAKING", "INCREASED_DURABILITY" -> Enchantment.UNBREAKING;
            case "LOOT_BONUS_BLOCKS", "FORTUNE", "BLOCK_LOOTING" -> Enchantment.FORTUNE;
            case "ARROW_DAMAGE", "POWER", "BOW_POWER" -> Enchantment.POWER;
            case "ARROW_KNOCKBACK", "PUNCH" -> Enchantment.PUNCH;
            case "ARROW_FIRE", "FLAME" -> Enchantment.FLAME;
            case "ARROW_INFINITE", "INFINITY", "UNLIMITED_ARROWS" -> Enchantment.INFINITY;
            case "LUCK", "LUCK_OF_THE_SEA", "FISHING_LUCK" -> Enchantment.LUCK_OF_THE_SEA;
            case "LURE" -> Enchantment.LURE;
            case "LOYALTY", "TRIDENT_RETURN" -> Enchantment.LOYALTY;
            case "IMPALING", "OCEAN_MOB_DAMAGE" -> Enchantment.IMPALING;
            case "RIPTIDE", "TRIDENT_LAUNCH" -> Enchantment.RIPTIDE;
            case "CHANNELING" -> Enchantment.CHANNELING;
            case "MULTISHOT" -> Enchantment.MULTISHOT;
            case "QUICK_CHARGE", "FAST_RELOAD" -> Enchantment.QUICK_CHARGE;
            case "PIERCING", "ARROW_PIERCE" -> Enchantment.PIERCING;
            case "DENSITY" -> Enchantment.DENSITY;
            case "BREACH" -> Enchantment.BREACH;
            case "WIND_BURST" -> Enchantment.WIND_BURST;
            case "MENDING" -> Enchantment.MENDING;
            case "VANISHING_CURSE" -> Enchantment.VANISHING_CURSE;
            case "SOUL_SPEED" -> Enchantment.SOUL_SPEED;
            case "SWIFT_SNEAK", "QUICK_SNEAK" -> Enchantment.SWIFT_SNEAK;
            default -> Enchantment.getByName(enchantmentName);
         };
      }
   }

   @Generated
   @Override
   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof BukkitEnchantment other)) {
         return false;
      } else if (!other.canEqual(this)) {
         return false;
      } else {
         Object this$enchantmentName = this.getEnchantmentName();
         Object other$enchantmentName = other.getEnchantmentName();
         if (this$enchantmentName == null ? other$enchantmentName == null : this$enchantmentName.equals(other$enchantmentName)) {
            Object this$enchantment = this.getEnchantment();
            Object other$enchantment = other.getEnchantment();
            return this$enchantment == null ? other$enchantment == null : this$enchantment.equals(other$enchantment);
         } else {
            return false;
         }
      }
   }

   @Generated
   protected boolean canEqual(Object other) {
      return other instanceof BukkitEnchantment;
   }

   @Generated
   @Override
   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      Object $enchantmentName = this.getEnchantmentName();
      result = result * 59 + ($enchantmentName == null ? 43 : $enchantmentName.hashCode());
      Object $enchantment = this.getEnchantment();
      return result * 59 + ($enchantment == null ? 43 : $enchantment.hashCode());
   }
}
