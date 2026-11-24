package net.Indyuce.mmoitems.stat.type;

import org.bukkit.Material;

@Deprecated
public class PlayerStat extends DoubleStat {
   public PlayerStat(String text, Material material, String name, String[] texts) {
      this(text, material, name, texts, new String[]{"!miscellaneous", "!block", "all"}, true);
   }

   public PlayerStat(String text, Material material, String name, String[] texts, String[] compatibleTypes, Material... materials) {
      this(text, material, name, texts, compatibleTypes, true, materials);
   }

   public PlayerStat(String text, Material material, String name, String[] texts, String[] compatibleTypes, boolean flag, Material... materials) {
      super(text, material, name, texts, compatibleTypes, flag, materials);
   }
}
