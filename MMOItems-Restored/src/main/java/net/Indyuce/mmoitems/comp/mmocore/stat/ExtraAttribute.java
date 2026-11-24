package net.Indyuce.mmoitems.comp.mmocore.stat;

import net.Indyuce.mmocore.api.player.attribute.PlayerAttribute;
import net.Indyuce.mmoitems.api.item.build.ItemStackBuilder;
import net.Indyuce.mmoitems.stat.data.DoubleData;
import net.Indyuce.mmoitems.stat.type.DoubleStat;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

public class ExtraAttribute extends DoubleStat {
   public ExtraAttribute(PlayerAttribute playerAttribute) {
      super(
         "ADDITIONAL_" + playerAttribute.getId().toUpperCase().replace("-", "_"),
         Material.LIME_DYE,
         "Additional " + playerAttribute.getName() + " (MMOCore)",
         new String[]{"Amount of " + playerAttribute.getName() + " points the player", "gets when holding/wearing this item."},
         new String[]{"!block", "all"}
      );
   }

   @Deprecated
   @Override
   public void whenApplied(@NotNull ItemStackBuilder itemStackBuilder, @NotNull DoubleData doubleData) {
      int count = (int)Math.round(doubleData.getValue());
      itemStackBuilder.getLore().insert(this.getPath(), DoubleStat.formatPath(this.getPath(), this.getGeneralStatFormat(), false, false, count));
      itemStackBuilder.addItemTag(this.getAppliedNBT(doubleData));
   }
}
