package net.Indyuce.mmoitems.stat;

import net.Indyuce.mmoitems.stat.annotation.HasCategory;
import net.Indyuce.mmoitems.stat.type.AttackWeaponStat;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;

@HasCategory(cat = "vanilla_attribute")
public class AttackSpeed extends AttackWeaponStat {
   public AttackSpeed() {
      super(
         "ATTACK_SPEED",
         Material.LIGHT_GRAY_DYE,
         "Attack Speed",
         new String[]{"The speed at which your weapon strikes.", "In attacks/sec."},
         Attribute.ATTACK_SPEED
      );
   }
}
