package net.Indyuce.mmoitems.api.interaction.weapon.untargeted;

import io.lumine.mythic.lib.api.player.EquipmentSlot;
import io.lumine.mythic.lib.player.PlayerMetadata;

@Deprecated
public interface LegacyWeapon {
   @Deprecated
   boolean canAttack(boolean flag, EquipmentSlot equipmentSlot);

   @Deprecated
   void applyAttackEffect(PlayerMetadata playerMetadata, EquipmentSlot equipmentSlot);
}
