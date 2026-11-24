package net.Indyuce.mmocore.loot.droptable.dropitem;

import io.lumine.mythic.lib.api.MMOLineConfig;
import net.Indyuce.mmocore.loot.LootBuilder;
import net.Indyuce.mmocore.util.item.CurrencyItemBuilder;
import org.bukkit.inventory.ItemStack;

public class NoteDropItem extends DropItem {
   private final int min;
   private final int max;

   public NoteDropItem(MMOLineConfig config) {
      super(config);
      config.validate(new String[]{"max", "min"});
      this.min = (int)config.getDouble("min");
      this.max = (int)config.getDouble("max");
   }

   @Override
   public void collect(LootBuilder builder) {
      ItemStack item = new CurrencyItemBuilder("NOTE", random.nextInt(this.max - this.min + 1) + this.min).build();
      item.setAmount(this.rollAmount());
      builder.addLoot(item);
   }
}
