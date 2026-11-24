package net.Indyuce.mmoitems.stat.type;

import net.Indyuce.mmoitems.api.player.PlayerData;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;

public abstract class AttackWeaponStat extends DoubleStat {
   private final Attribute attribute;

   public AttackWeaponStat(String text, Material material, String text2, String[] texts, Attribute attribute) {
      super(text, material, text2, texts, new String[]{"!consumable", "!block", "!miscellaneous", "all"});
      this.attribute = attribute;
   }

   public Attribute getAttribute() {
      return this.attribute;
   }

   public double getOffset(PlayerData playerData) {
      return playerData.getPlayer().getAttribute(this.attribute).getBaseValue();
   }
}
