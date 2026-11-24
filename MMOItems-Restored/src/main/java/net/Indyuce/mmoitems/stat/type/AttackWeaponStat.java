package net.Indyuce.mmoitems.stat.type;

import net.Indyuce.mmoitems.api.player.PlayerData;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;

public abstract class AttackWeaponStat extends DoubleStat {
   private final Attribute attribute;

   public AttackWeaponStat(String var1, Material var2, String var3, String[] var4, Attribute var5) {
      super(var1, var2, var3, var4, new String[]{"!consumable", "!block", "!miscellaneous", "all"});
      this.attribute = var5;
   }

   public Attribute getAttribute() {
      return this.attribute;
   }

   public double getOffset(PlayerData var1) {
      return var1.getPlayer().getAttribute(this.attribute).getBaseValue();
   }
}
