package io.lumine.mythic.lib.damage;

import io.lumine.mythic.lib.api.player.EquipmentSlot;
import io.lumine.mythic.lib.api.stat.provider.StatProvider;
import io.lumine.mythic.lib.player.PlayerMetadata;
import org.apache.commons.lang.Validate;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public class MeleeAttackMetadata extends AttackMetadata {
   public MeleeAttackMetadata(DamageMetadata damage, @NotNull LivingEntity target, @NotNull StatProvider attacker) {
      super(damage, target, attacker);
   }

   @Deprecated
   public EquipmentSlot getHand() {
      Validate.notNull(this.getAttacker(), "No attacker found");
      Validate.isTrue(this.getAttacker() instanceof PlayerMetadata, "Attacker is not a player");
      return ((PlayerMetadata)this.getAttacker()).getActionHand();
   }
}
