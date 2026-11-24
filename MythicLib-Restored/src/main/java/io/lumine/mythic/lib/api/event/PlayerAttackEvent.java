package io.lumine.mythic.lib.api.event;

import io.lumine.mythic.lib.api.player.EquipmentSlot;
import io.lumine.mythic.lib.api.player.MMOPlayerData;
import io.lumine.mythic.lib.api.stat.provider.PlayerStatProvider;
import io.lumine.mythic.lib.damage.AttackMetadata;
import io.lumine.mythic.lib.player.PlayerMetadata;
import org.apache.commons.lang.Validate;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jetbrains.annotations.NotNull;

public class PlayerAttackEvent extends AttackEvent implements Cancellable {
   private final PlayerMetadata attacker;

   public PlayerAttackEvent(EntityDamageEvent event, AttackMetadata attack) {
      super(event, attack);
      Validate.isTrue(attack.isPlayer(), "Not a player attack");
      this.attacker = ((PlayerStatProvider)attack.getAttacker()).cache(EquipmentSlot.MAIN_HAND);
   }

   @NotNull
   public PlayerMetadata getAttacker() {
      return this.attacker;
   }

   @Deprecated
   public MMOPlayerData getData() {
      return this.attacker.getData();
   }

   @Deprecated
   public Player getPlayer() {
      return this.attacker.getPlayer();
   }
}
