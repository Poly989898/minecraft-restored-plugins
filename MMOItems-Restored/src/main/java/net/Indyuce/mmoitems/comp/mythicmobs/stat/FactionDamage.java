package net.Indyuce.mmoitems.comp.mythicmobs.stat;

import net.Indyuce.mmoitems.stat.type.DoubleStat;
import org.bukkit.Material;

public class FactionDamage extends DoubleStat {
   public FactionDamage(String text) {
      super(
         "FACTION_DAMAGE_" + text.toUpperCase(),
         Material.RED_DYE,
         text + " Faction Damage",
         new String[]{"Deals additional damage to mobs", "from the " + text + " faction in %."},
         new String[]{"!block", "all"}
      );
   }
}
