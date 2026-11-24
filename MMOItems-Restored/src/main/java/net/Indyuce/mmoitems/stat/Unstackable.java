package net.Indyuce.mmoitems.stat;

import io.lumine.mythic.lib.api.item.ItemTag;
import java.util.UUID;
import net.Indyuce.mmoitems.api.item.build.ItemStackBuilder;
import net.Indyuce.mmoitems.stat.data.BooleanData;
import net.Indyuce.mmoitems.stat.type.BooleanStat;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

public class Unstackable extends BooleanStat {
   public Unstackable() {
      super("UNSTACKABLE", Material.CHEST_MINECART, "Unstackable", new String[]{"This will make the item unable", "to be stacked with itself."}, new String[0]);
   }

   @Override
   public void whenApplied(@NotNull ItemStackBuilder itemStackBuilder, @NotNull BooleanData booleanData) {
      if (booleanData.isEnabled()) {
         itemStackBuilder.addItemTag(new ItemTag(this.getNBTPath(), true));
         itemStackBuilder.addItemTag(new ItemTag(this.getNBTPath() + "_UUID", UUID.randomUUID().toString()));
      }
   }
}
