package net.Indyuce.mmoitems.api.crafting.ingredient;

import java.util.Iterator;
import java.util.List;
import net.Indyuce.mmoitems.api.crafting.ingredient.inventory.PlayerIngredient;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CheckedIngredient {
   @NotNull
   private final Ingredient ingredient;
   @Nullable
   private final List<PlayerIngredient> found;
   private final boolean isHad;

   public CheckedIngredient(@NotNull Ingredient ingredient, @NotNull List<PlayerIngredient> playerIngredients) {
      this.ingredient = ingredient;
      this.found = playerIngredients;
      this.isHad = this.getTotalAmount() >= ingredient.getAmount();
   }

   public boolean isHad() {
      return this.isHad;
   }

   public int getTotalAmount() {
      int count = 0;

      for (PlayerIngredient playerIngredient : this.found) {
         count += playerIngredient.getAmount();
      }

      return count;
   }

   public void takeAway() {
      this.reduceItem(this.ingredient.getAmount());
   }

   public void reduceItem(int count) {
      Iterator iterator = this.found.iterator();

      while (iterator.hasNext() && count > 0) {
         ItemStack item = ((PlayerIngredient)iterator.next()).getItem();
         if (item.getAmount() <= 0) {
            iterator.remove();
         } else {
            int count2 = Math.min(item.getAmount(), count);
            count -= count2;
            item.setAmount(item.getAmount() - count2);
         }
      }
   }

   @NotNull
   public Ingredient getIngredient() {
      return this.ingredient;
   }

   @Nullable
   public List<PlayerIngredient> getFound() {
      return this.found;
   }

   @NotNull
   public String format() {
      return this.ingredient.formatDisplay(this.ingredient.getDisplay().format(this.isHad));
   }
}
