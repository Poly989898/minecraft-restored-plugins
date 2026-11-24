package net.Indyuce.mmoitems.api.droptable.item;

import java.util.Random;
import javax.annotation.Nullable;
import net.Indyuce.mmoitems.api.player.PlayerData;
import org.bukkit.inventory.ItemStack;

public abstract class DropItem {
   private final double drop;
   private final int min;
   private final int max;
   protected static final Random random = new Random();

   public DropItem(double value, int count, int count2) {
      this.drop = value;
      this.min = count;
      this.max = count2;
   }

   public DropItem(String text) {
      String[] texts = text.split(",");
      this.drop = Double.parseDouble(texts[0]) / 100.0;
      String[] rangeParts = texts[1].split("-");
      this.min = Integer.parseInt(rangeParts[0]);
      this.max = rangeParts.length > 1 ? Integer.parseInt(rangeParts[1]) : this.min;
   }

   public boolean rollDrop() {
      return random.nextDouble() < this.drop;
   }

   public int rollAmount() {
      return this.max > this.min ? this.min + random.nextInt(this.max - this.min + 1) : this.min;
   }

   public ItemStack getItem(@Nullable PlayerData playerData) {
      return this.getItem(playerData, this.rollAmount());
   }

   public abstract ItemStack getItem(@Nullable PlayerData playerData, int count);

   public abstract String getKey();
}
