package net.Indyuce.mmoitems.stat;

import io.lumine.mythic.lib.api.item.ItemTag;
import net.Indyuce.mmoitems.api.item.build.ItemStackBuilder;
import net.Indyuce.mmoitems.stat.data.DoubleData;
import net.Indyuce.mmoitems.stat.type.DoubleStat;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

public class MaxConsume extends DoubleStat {
   public MaxConsume() {
      super("MAX_CONSUME", Material.BLAZE_POWDER, "Max Consume", new String[]{"Max amount of usage before", "item disappears."}, new String[]{"consumable"});
   }

   @Override
   public void whenApplied(@NotNull ItemStackBuilder var1, @NotNull DoubleData var2) {
      int var3 = (int)var2.getValue();
      var1.addItemTag(new ItemTag(this.getNBTPath(), var3));
      String var4 = this.getGeneralStatFormat().replace("{value}", String.valueOf(var3));
      var1.getLore().insert(this.getPath(), var4);
   }
}
