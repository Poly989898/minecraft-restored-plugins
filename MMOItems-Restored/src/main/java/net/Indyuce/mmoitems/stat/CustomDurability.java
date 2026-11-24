package net.Indyuce.mmoitems.stat;

import io.lumine.mythic.lib.api.item.ItemTag;
import net.Indyuce.mmoitems.api.item.build.ItemStackBuilder;
import net.Indyuce.mmoitems.stat.data.DoubleData;
import net.Indyuce.mmoitems.stat.type.DoubleStat;
import net.Indyuce.mmoitems.stat.type.InternalStat;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

public class CustomDurability extends DoubleStat implements InternalStat {
   public CustomDurability() {
      super("DURABILITY", Material.SHEARS, "Custom Durability", new String[0], new String[]{"!block", "all"});
   }

   @Override
   public void whenApplied(@NotNull ItemStackBuilder itemStackBuilder, @NotNull DoubleData doubleData) {
      int count = (int)doubleData.getValue();
      if (count != 0) {
         itemStackBuilder.addItemTag(new ItemTag(this.getNBTPath(), count));
      }
   }
}
