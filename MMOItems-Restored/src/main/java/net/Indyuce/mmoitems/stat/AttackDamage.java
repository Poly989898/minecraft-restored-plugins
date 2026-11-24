package net.Indyuce.mmoitems.stat;

import net.Indyuce.mmoitems.stat.annotation.HasCategory;
import net.Indyuce.mmoitems.stat.type.AttackWeaponStat;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;

@HasCategory(cat = "vanilla_attribute")
public class AttackDamage extends AttackWeaponStat {
   public AttackDamage() {
      super("ATTACK_DAMAGE", Material.IRON_SWORD, "Attack Damage", new String[]{"The amount of damage your weapon deals."}, Attribute.ATTACK_DAMAGE);
   }
}
