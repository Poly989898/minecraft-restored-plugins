package net.Indyuce.mmoitems.api.crafting.ingredient;

import io.lumine.mythic.lib.api.MMOLineConfig;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.crafting.ConditionalDisplay;
import net.Indyuce.mmoitems.api.crafting.ingredient.inventory.IngredientInventory;
import net.Indyuce.mmoitems.api.crafting.ingredient.inventory.PlayerIngredient;
import net.Indyuce.mmoitems.api.player.RPGPlayer;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public abstract class Ingredient<C extends PlayerIngredient> {
   private final String id;
   private int amount;

   public Ingredient(String text, MMOLineConfig mmoLineConfig) {
      this(text, mmoLineConfig.getInt("amount", 1));
   }

   public Ingredient(String text, int count) {
      this.id = text;
      this.amount = count;
   }

   public String getId() {
      return this.id;
   }

   public void setAmount(int count) {
      this.amount = count;
   }

   public int getAmount() {
      return this.amount;
   }

   public ConditionalDisplay getDisplay() {
      return MMOItems.plugin.getCrafting().getIngredients().stream().filter(ingredientType -> ingredientType.getId().equals(this.id)).findAny().orElseThrow().getDisplay();
   }

   @Deprecated
   public abstract String getKey();

   public abstract String formatDisplay(String text);

   public abstract boolean matches(C value);

   @NotNull
   public abstract ItemStack generateItemStack(@NotNull RPGPlayer rpgPlayer, boolean flag);

   public CheckedIngredient evaluateIngredient(@NotNull IngredientInventory ingredientInventory) {
      return ingredientInventory.findMatching(this);
   }
}
