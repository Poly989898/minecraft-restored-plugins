package net.Indyuce.mmoitems.stat.type;

import org.bukkit.Material;

@Deprecated
public class PlayerStat extends DoubleStat {
   public PlayerStat(String var1, Material var2, String var3, String[] var4) {
      this(var1, var2, var3, var4, new String[]{"!miscellaneous", "!block", "all"}, true);
   }

   public PlayerStat(String var1, Material var2, String var3, String[] var4, String[] var5, Material... var6) {
      this(var1, var2, var3, var4, var5, true, var6);
   }

   public PlayerStat(String var1, Material var2, String var3, String[] var4, String[] var5, boolean var6, Material... var7) {
      super(var1, var2, var3, var4, var5, var6, var7);
   }
}
