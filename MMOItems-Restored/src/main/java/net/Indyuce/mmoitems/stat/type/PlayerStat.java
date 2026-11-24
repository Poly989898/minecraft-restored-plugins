package net.Indyuce.mmoitems.stat.type;

import org.bukkit.Material;

@Deprecated
public class PlayerStat extends DoubleStat {
   public PlayerStat(String text, Material material, String text2, String[] texts) {
      this(text, material, text2, texts, new String[]{"!miscellaneous", "!block", "all"}, true);
   }

   public PlayerStat(String text, Material material, String text2, String[] texts, String[] texts2, Material... materials) {
      this(text, material, text2, texts, texts2, true, materials);
   }

   public PlayerStat(String text, Material material, String text2, String[] texts, String[] texts2, boolean flag, Material... materials) {
      super(text, material, text2, texts, texts2, flag, materials);
   }
}
