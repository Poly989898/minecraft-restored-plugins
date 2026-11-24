package net.Indyuce.mmoitems.stat.type;

import org.bukkit.Material;

public class DisableStat extends BooleanStat {
   public DisableStat(String text, Material material, String name, String... texts) {
      super("DISABLE_" + text, material, name, texts, new String[0]);
   }

   public DisableStat(String text, Material material, String name, Material[] materials, String... texts) {
      super("DISABLE_" + text, material, name, texts, new String[0], materials);
   }

   public DisableStat(String text, Material material, String name, String[] texts, String... lore) {
      super("DISABLE_" + text, material, name, lore, texts);
   }
}
