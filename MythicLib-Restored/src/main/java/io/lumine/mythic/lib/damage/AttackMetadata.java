package io.lumine.mythic.lib.damage;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.api.player.MMOPlayerData;
import io.lumine.mythic.lib.api.stat.provider.PlayerStatProvider;
import io.lumine.mythic.lib.api.stat.provider.StatProvider;
import io.lumine.mythic.lib.player.PlayerMetadata;
import java.util.Objects;
import org.apache.commons.lang.Validate;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AttackMetadata {
   @NotNull
   private final DamageMetadata damage;
   @NotNull
   private final LivingEntity target;
   @Nullable
   private final StatProvider attacker;

   @Deprecated
   public AttackMetadata(@NotNull DamageMetadata damage, @Nullable StatProvider attacker) {
      this(damage, null, attacker);
   }

   public AttackMetadata(@NotNull DamageMetadata damage, @NotNull LivingEntity target, @Nullable StatProvider attacker) {
      this.attacker = attacker;
      this.target = target;
      this.damage = Objects.requireNonNull(damage, "Damage cannot be null");
   }

   @NotNull
   public DamageMetadata getDamage() {
      return this.damage;
   }

   @NotNull
   public LivingEntity getTarget() {
      return this.target;
   }

   @Nullable
   public StatProvider getAttacker() {
      return this.attacker;
   }

   public boolean hasAttacker() {
      return this.attacker != null;
   }

   public boolean isPlayer() {
      return this.attacker != null && this.attacker instanceof PlayerStatProvider;
   }

   @Deprecated
   public boolean hasExpired() {
      return false;
   }

   @Deprecated
   public void expire() {
   }

   @Deprecated
   public AttackMetadata clone() {
      return new AttackMetadata(this.damage.clone(), this.target, this.attacker);
   }

   @Deprecated
   public void damage(LivingEntity target) {
      this.damage(target, true);
   }

   @Deprecated
   public void damage(LivingEntity target, boolean knockback) {
      MythicLib.plugin.getDamage().damage(this, target, knockback);
   }

   @Deprecated
   public Player getPlayer() {
      Validate.notNull(this.attacker, "No attacker was found");
      Validate.isTrue(this.attacker instanceof PlayerMetadata, "Attacker is not a player");
      return ((PlayerMetadata)this.attacker).getPlayer();
   }

   @Deprecated
   public MMOPlayerData getData() {
      Validate.notNull(this.attacker, "No attacker was found");
      Validate.isTrue(this.attacker instanceof PlayerMetadata, "Attacker is not a player");
      return ((PlayerMetadata)this.attacker).getData();
   }

   @Deprecated
   public double getStat(String stat) {
      Validate.notNull(this.attacker, "No attacker was found");
      return this.attacker.getStat(stat);
   }

   @Deprecated
   public void setStat(String stat, double value) {
      Validate.notNull(this.attacker, "No attacker was found");
      Validate.isTrue(this.attacker instanceof PlayerMetadata, "Attacker is not a player");
      ((PlayerMetadata)this.attacker).setStat(stat, value);
   }

   @Deprecated
   public AttackMetadata attack(LivingEntity target, double damage, DamageType... types) {
      Validate.notNull(this.attacker, "No attacker was found");
      Validate.isTrue(this.attacker instanceof PlayerMetadata, "Attacker is not a player");
      return ((PlayerMetadata)this.attacker).attack(target, damage, types);
   }

   @Deprecated
   public AttackMetadata attack(LivingEntity target, double damage, boolean knockback, DamageType... types) {
      Validate.notNull(this.attacker, "No attacker was found");
      Validate.isTrue(this.attacker instanceof PlayerMetadata, "Attacker is not a player");
      return ((PlayerMetadata)this.attacker).attack(target, damage, knockback, types);
   }
}
