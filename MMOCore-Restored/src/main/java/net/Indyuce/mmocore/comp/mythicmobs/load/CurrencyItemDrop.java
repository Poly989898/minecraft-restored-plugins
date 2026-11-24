package net.Indyuce.mmocore.comp.mythicmobs.load;

import io.lumine.mythic.api.adapters.AbstractItemStack;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.drops.DropMetadata;
import io.lumine.mythic.api.drops.IItemDrop;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.core.drops.Drop;
import java.util.Random;
import net.Indyuce.mmocore.util.item.CurrencyItemBuilder;

public class CurrencyItemDrop extends Drop implements IItemDrop {
   private final String key;
   private final int minw;
   private final int maxw;
   private static final Random random = new Random();

   public CurrencyItemDrop(String key, MythicLineConfig config) {
      super(config.getLine(), config);
      this.key = key;
      this.minw = config.getInteger("minw", 1);
      this.maxw = config.getInteger("maxw", 1);
   }

   public AbstractItemStack getDrop(DropMetadata dropMetadata, double v) {
      return BukkitAdapter.adapt(new CurrencyItemBuilder(this.key, this.random(this.minw, this.maxw)).build());
   }

   private int random(int a, int b) {
      return random.nextInt(b - a + 1) + a;
   }
}
