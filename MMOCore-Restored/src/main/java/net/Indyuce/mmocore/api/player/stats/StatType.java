package net.Indyuce.mmocore.api.player.stats;

import java.util.Objects;
import net.Indyuce.mmocore.api.util.math.formula.LinearValue;
import net.Indyuce.mmocore.experience.Profession;
import net.Indyuce.mmocore.player.stats.StatInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Deprecated
public enum StatType {
   ATTACK_DAMAGE,
   ATTACK_SPEED,
   MAX_HEALTH,
   HEALTH_REGENERATION,
   MAX_HEALTH_REGENERATION,
   MOVEMENT_SPEED,
   SPEED_MALUS_REDUCTION,
   KNOCKBACK_RESISTANCE,
   MAX_MANA,
   MANA_REGENERATION,
   MAX_MANA_REGENERATION,
   MAX_STAMINA,
   STAMINA_REGENERATION,
   MAX_STAMINA_REGENERATION,
   MAX_STELLIUM,
   STELLIUM_REGENERATION,
   MAX_STELLIUM_REGENERATION,
   ARMOR,
   ARMOR_TOUGHNESS,
   CRITICAL_STRIKE_CHANCE,
   CRITICAL_STRIKE_POWER,
   SKILL_CRITICAL_STRIKE_CHANCE,
   SKILL_CRITICAL_STRIKE_POWER,
   DEFENSE,
   BLOCK_POWER,
   BLOCK_RATING,
   BLOCK_COOLDOWN_REDUCTION,
   DODGE_RATING,
   DODGE_COOLDOWN_REDUCTION,
   PARRY_RATING,
   PARRY_COOLDOWN_REDUCTION,
   ADDITIONAL_EXPERIENCE,
   COOLDOWN_REDUCTION,
   CHANCE,
   MAGIC_DAMAGE,
   PHYSICAL_DAMAGE,
   PROJECTILE_DAMAGE,
   WEAPON_DAMAGE,
   SKILL_DAMAGE,
   UNARMED_DAMAGE,
   UNDEAD_DAMAGE,
   PVP_DAMAGE,
   PVE_DAMAGE,
   DAMAGE_REDUCTION,
   MAGIC_DAMAGE_REDUCTION,
   PHYSICAL_DAMAGE_REDUCTION,
   PROJECTILE_DAMAGE_REDUCTION,
   WEAPON_DAMAGE_REDUCTION,
   SKILL_DAMAGE_REDUCTION,
   FISHING_STRENGTH,
   CRITICAL_FISHING_CHANCE,
   CRITICAL_FISHING_FAILURE_CHANCE,
   FORTUNE,
   GATHERING_HASTE,
   LUCK_OF_THE_FIELD;

   @Deprecated
   public String getProfession() {
      return this.findProfession().getId();
   }

   @Deprecated
   @Nullable
   public Profession findProfession() {
      return StatInfo.valueOf(this.name()).profession;
   }

   @Deprecated
   public boolean hasProfession() {
      return this.findProfession() != null;
   }

   @Deprecated
   @NotNull
   public LinearValue getDefault() {
      return StatInfo.valueOf(this.name()).getDefaultFormula();
   }

   @Deprecated
   public boolean matches(Profession profession) {
      return Objects.equals(this.findProfession(), profession);
   }

   @Deprecated
   public String format(double value) {
      return StatInfo.valueOf(this.name()).format(value);
   }
}
