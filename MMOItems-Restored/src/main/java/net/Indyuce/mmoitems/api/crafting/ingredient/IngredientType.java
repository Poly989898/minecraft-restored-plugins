package net.Indyuce.mmoitems.api.crafting.ingredient;

import io.lumine.mythic.lib.api.MMOLineConfig;
import io.lumine.mythic.lib.api.item.NBTItem;
import java.util.function.Function;
import java.util.function.Predicate;
import net.Indyuce.mmoitems.api.crafting.ConditionalDisplay;
import net.Indyuce.mmoitems.api.crafting.LoadedCraftingObject;
import net.Indyuce.mmoitems.api.crafting.ingredient.inventory.PlayerIngredient;

public class IngredientType extends LoadedCraftingObject<Ingredient> {
   private final Predicate<NBTItem> check;
   private final Function<NBTItem, PlayerIngredient> readIngredient;

   public IngredientType(
      String text, Function<MMOLineConfig, Ingredient> function, ConditionalDisplay conditionalDisplay, Predicate<NBTItem> predicate, Function<NBTItem, PlayerIngredient> readIngredientFunction
   ) {
      super(text, function, conditionalDisplay);
      this.check = predicate;
      this.readIngredient = readIngredientFunction;
   }

   public boolean check(NBTItem nbtItem) {
      return this.check.test(nbtItem);
   }

   public PlayerIngredient readPlayerIngredient(NBTItem nbtItem) {
      return this.readIngredient.apply(nbtItem);
   }
}
