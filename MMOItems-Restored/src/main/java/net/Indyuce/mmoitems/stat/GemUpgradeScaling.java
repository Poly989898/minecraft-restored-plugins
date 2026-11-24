package net.Indyuce.mmoitems.stat;

import net.Indyuce.mmoitems.stat.data.StringData;
import net.Indyuce.mmoitems.stat.type.ChooseStat;
import net.Indyuce.mmoitems.stat.type.GemStoneStat;
import net.Indyuce.mmoitems.util.StatChoice;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

public class GemUpgradeScaling extends ChooseStat implements GemStoneStat {
   public static final StatChoice NEVER = new StatChoice("NEVER", "Gem stats are never scaled by upgrading the item.");
   public static final StatChoice HISTORIC = new StatChoice("HISTORIC", "Gem stats instantly upgrade to the current item level, and subsequently thereafter.");
   public static final StatChoice SUBSEQUENT = new StatChoice("SUBSEQUENT", "Gem stats scale by upgrading the item, but only after putting the gem in.");
   public static String defaultValue = SUBSEQUENT.getId();

   public GemUpgradeScaling() {
      super(
         "GEM_UPGRADE_SCALING",
         Material.LIME_DYE,
         "Gem Upgrade Scaling",
         new String[]{
            "Gem stones add their stats to items, but you may also",
            "upgrade your items via crafting stations or consumables.",
            "",
            "§6Should this gem stone stats be affected by upgrading?"
         },
         new String[]{"gem_stone"}
      );
      this.addChoices(SUBSEQUENT, NEVER, HISTORIC);
   }

   @NotNull
   @Override
   public StringData getClearStatData() {
      return new StringData(defaultValue);
   }
}
