package net.Indyuce.mmoitems.api.item.util.crafting;

import io.lumine.mythic.lib.api.item.ItemTag;
import io.lumine.mythic.lib.api.item.NBTItem;
import io.lumine.mythic.lib.util.AdventureUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import net.Indyuce.mmoitems.api.crafting.ConditionalDisplay;
import net.Indyuce.mmoitems.api.crafting.condition.CheckedCondition;
import net.Indyuce.mmoitems.api.crafting.recipe.CheckedRecipe;
import net.Indyuce.mmoitems.api.crafting.recipe.UpgradingRecipe;
import net.Indyuce.mmoitems.api.item.util.ConfigItem;
import net.Indyuce.mmoitems.util.MMOUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class UpgradingRecipeDisplay extends ConfigItem {
   public UpgradingRecipeDisplay() {
      super(
         "UPGRADING_RECIPE_DISPLAY",
         Material.BARRIER,
         "&e&lUpgrade&f #name#",
         "{conditions}",
         "{conditions}&8Conditions:",
         "",
         "&8Ingredients:",
         "#ingredients#",
         "",
         "&eLeft-Click to craft!",
         "&eRight-Click to preview!"
      );
   }

   public UpgradingRecipeDisplay.ItemBuilder newBuilder(CheckedRecipe checkedRecipe) {
      return new UpgradingRecipeDisplay.ItemBuilder(checkedRecipe);
   }

   public class ItemBuilder {
      private final CheckedRecipe recipe;
      private final UpgradingRecipe upgradingRecipe;
      private final String name = UpgradingRecipeDisplay.this.getName();
      private final List<String> lore = new ArrayList<>(UpgradingRecipeDisplay.this.getLore());

      public ItemBuilder(CheckedRecipe nullx) {
         this.recipe = nullx;
         this.upgradingRecipe = (UpgradingRecipe)nullx.getRecipe();
      }

      public ItemStack build() {
         HashMap<String, String> map = new HashMap<>();
         int count = -1;
         ListIterator listIterator = this.lore.listIterator();

         while (listIterator.hasNext()) {
            int ingredientIndex = listIterator.nextIndex();
            String text = (String)listIterator.next();
            if (text.startsWith("{conditions}")) {
               count = ingredientIndex;
               if (this.recipe.getConditions().size() == 0) {
                  listIterator.remove();
               } else {
                  map.put(text, text.replace("{conditions}", ""));
               }
            }
         }

         for (String key : map.keySet()) {
            this.lore.set(this.lore.indexOf(key), (String)map.get(key));
         }

         int ingredientsIndex = this.lore.indexOf("#ingredients#");
         this.lore.remove(ingredientsIndex);
         this.recipe.getIngredients().forEach(ingredient -> this.lore.add(ingredientsIndex, ingredient.format()));
         if (count >= 0) {
            for (CheckedCondition condition : this.recipe.getConditions()) {
               ConditionalDisplay display = condition.getCondition().getDisplay();
               if (display != null) {
                  this.lore.add(count++, condition.format());
               }
            }
         }

         ItemStack preview = this.upgradingRecipe.getItem().getPreview();
         ItemMeta itemMeta = preview.getItemMeta();
         AdventureUtils.setDisplayName(itemMeta, this.name.replace("#name#", MMOUtils.getDisplayName(preview)));
         AdventureUtils.setLore(itemMeta, this.lore);
         itemMeta.addItemFlags(ItemFlag.values());
         preview.setItemMeta(itemMeta);
         return NBTItem.get(preview).addTag(new ItemTag[]{new ItemTag("recipeId", this.recipe.getRecipe().getId())}).toItem();
      }
   }
}
