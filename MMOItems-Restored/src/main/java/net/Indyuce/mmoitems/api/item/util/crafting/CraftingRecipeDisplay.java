package net.Indyuce.mmoitems.api.item.util.crafting;

import io.lumine.mythic.lib.MythicLib;
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
import net.Indyuce.mmoitems.api.crafting.recipe.CraftingRecipe;
import net.Indyuce.mmoitems.api.item.util.ConfigItem;
import net.Indyuce.mmoitems.api.util.message.Message;
import net.Indyuce.mmoitems.util.MMOUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class CraftingRecipeDisplay extends ConfigItem {
   public CraftingRecipeDisplay() {
      super(
         "CRAFTING_RECIPE_DISPLAY",
         Material.BARRIER,
         "&a&lCraft&f #name#",
         "{conditions}",
         "{conditions}&8Conditions:",
         "{crafting_time}",
         "{crafting_time}&7Crafting Time: &c#crafting-time#&7s",
         "",
         "&8Ingredients:",
         "#ingredients#",
         "",
         "&eLeft-Click to craft!",
         "&eRight-Click to preview!"
      );
   }

   public CraftingRecipeDisplay.ItemBuilder newBuilder(CheckedRecipe checkedRecipe) {
      return new CraftingRecipeDisplay.ItemBuilder(checkedRecipe);
   }

   public class ItemBuilder {
      private final CheckedRecipe recipe;
      private final CraftingRecipe craftingRecipe;
      private final String name = CraftingRecipeDisplay.this.getName();
      private final List<String> lore = new ArrayList<>(CraftingRecipeDisplay.this.getLore());

      public ItemBuilder(CheckedRecipe nullx) {
         this.recipe = nullx;
         this.craftingRecipe = (CraftingRecipe)nullx.getRecipe();
      }

      public ItemStack build() {
         HashMap<String, String> map = new HashMap<>();
         int count = -1;
         ListIterator listIterator = this.lore.listIterator();

         while (listIterator.hasNext()) {
            int ingredientIndex = listIterator.nextIndex();
            String text = (String)listIterator.next();
            if (text.startsWith("{crafting_time}")) {
               if (this.craftingRecipe.getCraftingTime() <= 0.0) {
                  listIterator.remove();
                  continue;
               }

               map.put(
                  text,
                  text.replace("{crafting_time}", "")
                     .replace("#crafting-time#", MythicLib.plugin.getMMOConfig().decimal.format(this.craftingRecipe.getCraftingTime()))
               );
            }

            if (text.startsWith("{conditions}")) {
               count = ingredientIndex + 1;
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

         ItemStack previewItemStack = this.craftingRecipe.getPreviewItemStack();
         int outputAmount = this.craftingRecipe.getOutputAmount();
         if (outputAmount > 64) {
            this.lore.add(0, Message.STATION_BIG_STACK.format(ChatColor.GOLD, "#size#", String.valueOf(outputAmount)).toString());
         } else {
            previewItemStack.setAmount(outputAmount);
         }

         ItemMeta itemMeta = previewItemStack.getItemMeta();
         itemMeta.addItemFlags(ItemFlag.values());
         AdventureUtils.setDisplayName(
            itemMeta, this.name.replace("#name#", (outputAmount > 1 ? ChatColor.WHITE + "" + outputAmount + " x " : "") + MMOUtils.getDisplayName(previewItemStack))
         );
         AdventureUtils.setLore(itemMeta, this.lore);
         previewItemStack.setItemMeta(itemMeta);
         return NBTItem.get(previewItemStack).addTag(new ItemTag[]{new ItemTag("recipeId", this.craftingRecipe.getId())}).toItem();
      }
   }
}
