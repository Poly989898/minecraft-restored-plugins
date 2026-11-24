package net.Indyuce.mmoitems.comp.mythicmobs.stat;

import net.Indyuce.mmoitems.stat.type.DoubleStat;
import org.bukkit.Material;

public class FactionDamage extends DoubleStat {
   public FactionDamage(String var1) {
      super(
         "FACTION_DAMAGE_" + var1.toUpperCase(),
         Material.RED_DYE,
         var1 + " Faction Damage",
         new String[]{"Deals additional damage to mobs", "from the " + var1 + " faction in %."},
         new String[]{"!block", "all"}
      );
   }
}
