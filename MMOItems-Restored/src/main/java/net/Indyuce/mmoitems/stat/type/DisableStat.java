package net.Indyuce.mmoitems.stat.type;

import org.bukkit.Material;

public class DisableStat extends BooleanStat {
   public DisableStat(String text, Material material, String text2, String... texts) {
      super("DISABLE_" + text, material, text2, texts, new String[0]);
   }

   public DisableStat(String text, Material material, String text2, Material[] materials, String... texts) {
      super("DISABLE_" + text, material, text2, texts, new String[0], materials);
   }

   public DisableStat(String text, Material material, String text2, String[] texts, String... texts2) {
      super("DISABLE_" + text, material, text2, texts2, texts);
   }
}
