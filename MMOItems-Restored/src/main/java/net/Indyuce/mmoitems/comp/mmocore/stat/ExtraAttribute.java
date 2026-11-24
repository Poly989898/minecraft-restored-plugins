package net.Indyuce.mmoitems.comp.mmocore.stat;

import net.Indyuce.mmocore.api.player.attribute.PlayerAttribute;
import net.Indyuce.mmoitems.api.item.build.ItemStackBuilder;
import net.Indyuce.mmoitems.stat.data.DoubleData;
import net.Indyuce.mmoitems.stat.type.DoubleStat;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

public class ExtraAttribute extends DoubleStat {
   public ExtraAttribute(PlayerAttribute var1) {
      super(
         "ADDITIONAL_" + var1.getId().toUpperCase().replace("-", "_"),
         Material.LIME_DYE,
         "Additional " + var1.getName() + " (MMOCore)",
         new String[]{"Amount of " + var1.getName() + " points the player", "gets when holding/wearing this item."},
         new String[]{"!block", "all"}
      );
   }

   @Deprecated
   @Override
   public void whenApplied(@NotNull ItemStackBuilder var1, @NotNull DoubleData var2) {
      int var3 = (int)Math.round(var2.getValue());
      var1.getLore().insert(this.getPath(), DoubleStat.formatPath(this.getPath(), this.getGeneralStatFormat(), false, false, var3));
      var1.addItemTag(this.getAppliedNBT(var2));
   }
}
