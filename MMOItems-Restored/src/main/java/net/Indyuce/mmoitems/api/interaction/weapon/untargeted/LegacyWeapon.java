package net.Indyuce.mmoitems.api.interaction.weapon.untargeted;

import io.lumine.mythic.lib.api.player.EquipmentSlot;
import io.lumine.mythic.lib.player.PlayerMetadata;

@Deprecated
public interface LegacyWeapon {
   @Deprecated
   boolean canAttack(boolean var1, EquipmentSlot var2);

   @Deprecated
   void applyAttackEffect(PlayerMetadata var1, EquipmentSlot var2);
}
