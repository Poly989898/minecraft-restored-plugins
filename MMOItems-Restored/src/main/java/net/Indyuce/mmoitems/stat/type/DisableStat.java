package net.Indyuce.mmoitems.stat.type;

import org.bukkit.Material;

public class DisableStat extends BooleanStat {
   public DisableStat(String var1, Material var2, String var3, String... var4) {
      super("DISABLE_" + var1, var2, var3, var4, new String[0]);
   }

   public DisableStat(String var1, Material var2, String var3, Material[] var4, String... var5) {
      super("DISABLE_" + var1, var2, var3, var5, new String[0], var4);
   }

   public DisableStat(String var1, Material var2, String var3, String[] var4, String... var5) {
      super("DISABLE_" + var1, var2, var3, var5, var4);
   }
}
