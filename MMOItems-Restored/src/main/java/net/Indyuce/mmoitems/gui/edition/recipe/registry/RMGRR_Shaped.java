package net.Indyuce.mmoitems.gui.edition.recipe.registry;

import io.lumine.mythic.lib.api.crafting.ingredients.ShapedIngredient;
import io.lumine.mythic.lib.api.crafting.outputs.MRORecipe;
import io.lumine.mythic.lib.api.crafting.recipes.MythicRecipeBlueprint;
import io.lumine.mythic.lib.api.crafting.recipes.MythicRecipeStation;
import io.lumine.mythic.lib.api.crafting.recipes.ShapedRecipe;
import io.lumine.mythic.lib.api.crafting.uimanager.ProvidedUIFilter;
import io.lumine.mythic.lib.api.util.Ref;
import io.lumine.mythic.lib.api.util.ui.FriendlyFeedbackProvider;
import java.util.ArrayList;
import net.Indyuce.mmoitems.ItemStats;
import net.Indyuce.mmoitems.api.crafting.MMOItemUIFilter;
import net.Indyuce.mmoitems.api.item.template.MMOItemTemplate;
import net.Indyuce.mmoitems.api.util.message.FFPMMOItems;
import net.Indyuce.mmoitems.gui.edition.EditionInventory;
import net.Indyuce.mmoitems.gui.edition.recipe.gui.RMG_Shaped;
import net.Indyuce.mmoitems.gui.edition.recipe.gui.RecipeEditorGUI;
import net.Indyuce.mmoitems.gui.edition.recipe.interpreter.RMGRI_Shaped;
import net.Indyuce.mmoitems.stat.data.StringData;
import net.Indyuce.mmoitems.stat.data.random.RandomStatData;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RMGRR_Shaped implements RecipeRegistry {
   @NotNull
   final ItemStack displayListItem = RecipeEditorGUI.rename(new ItemStack(Material.CRAFTING_TABLE), FFPMMOItems.get().getExampleFormat() + "Shaped Recipe");

   @NotNull
   @Override
   public String getRecipeConfigPath() {
      return "shaped";
   }

   @NotNull
   @Override
   public String getRecipeTypeName() {
      return "Shaped";
   }

   @NotNull
   @Override
   public ItemStack getDisplayListItem() {
      return this.displayListItem;
   }

   @Override
   public void openForPlayer(@NotNull EditionInventory editionInventory, @NotNull String text, Object... objs) {
      new RMG_Shaped(editionInventory.getPlayer(), editionInventory.getEdited(), text, this).open(editionInventory);
   }

   @NotNull
   @Override
   public MythicRecipeBlueprint sendToMythicLib(
      @NotNull MMOItemTemplate mmoItemTemplate,
      @NotNull ConfigurationSection section,
      @NotNull String text,
      @NotNull Ref<NamespacedKey> ref,
      @NotNull FriendlyFeedbackProvider friendlyFeedbackProvider
   ) {
      ConfigurationSection section2 = RecipeEditorGUI.moveInput(section, text);
      NamespacedKey namespacedKey = (NamespacedKey)ref.getValue();
      if (namespacedKey == null) {
         throw new IllegalArgumentException(FriendlyFeedbackProvider.quickForConsole(FFPMMOItems.get(), "Illegal (Null) Namespace", new String[0]));
      }

      ShapedRecipe shapedRecipe = shapedRecipeFromList(namespacedKey.getKey(), new ArrayList<>(section2.getStringList("input")), friendlyFeedbackProvider);
      if (shapedRecipe == null) {
         throw new IllegalArgumentException(
            FriendlyFeedbackProvider.quickForConsole(FFPMMOItems.get(), "Shaped recipe containing only AIR, $fignored$b.", new String[0])
         );
      }

      ShapedRecipe shapedRecipe2 = shapedRecipeFromList(namespacedKey.getKey(), new ArrayList<>(section2.getStringList("output")), friendlyFeedbackProvider);
      int amount = section2.getInt("amount", 1);
      boolean hidden = section2.getBoolean("hidden", false);
      ShapedRecipe shapedRecipe3 = ShapedRecipe.single(
         namespacedKey.getKey(), new ProvidedUIFilter[]{new ProvidedUIFilter(MMOItemUIFilter.get(), mmoItemTemplate.getType().getId(), mmoItemTemplate.getId(), Math.max(amount, 1))}
      );
      MRORecipe mroRecipe = new MRORecipe(shapedRecipe3, shapedRecipe2);
      MythicRecipeBlueprint mythicRecipeBlueprint = new MythicRecipeBlueprint(shapedRecipe, mroRecipe, namespacedKey);
      RandomStatData randomStatData = mmoItemTemplate.getBaseItemData().get(ItemStats.CRAFT_PERMISSION);
      if (randomStatData instanceof StringData) {
         String text2 = ((StringData)randomStatData).getString();
         if (text2 != null) {
            mythicRecipeBlueprint.addRequiredPermission(text2);
         }
      }

      mythicRecipeBlueprint.deploy(MythicRecipeStation.WORKBENCH, ref);
      if (hidden) {
         ref.setValue(null);
      }

      return mythicRecipeBlueprint;
   }

   @Nullable
   public static ShapedRecipe shapedRecipeFromList(@NotNull String text, @NotNull ArrayList<String> texts, @NotNull FriendlyFeedbackProvider friendlyFeedbackProvider) {
      ArrayList list = new ArrayList();
      boolean flag = false;
      int count = 0;

      for (String text2 : texts) {
         String text3 = RMGRI_Shaped.updateRow(text2);
         String[] texts2;
         if (text3.contains("|")) {
            texts2 = text3.split("\\|");
         } else {
            texts2 = text3.split(" ");
         }

         if (texts2.length != 3) {
            throw new IllegalArgumentException("Invalid crafting table row $u" + text3 + "$b ($fNot exactly 3 ingredients wide$b).");
         }

         ProvidedUIFilter ingredientFrom = RecipeEditorGUI.readIngredientFrom(texts2[0], friendlyFeedbackProvider);
         ProvidedUIFilter ingredientFrom2 = RecipeEditorGUI.readIngredientFrom(texts2[1], friendlyFeedbackProvider);
         ProvidedUIFilter ingredientFrom3 = RecipeEditorGUI.readIngredientFrom(texts2[2], friendlyFeedbackProvider);
         if (!ingredientFrom.isAir()) {
            flag = true;
         }

         if (!ingredientFrom2.isAir()) {
            flag = true;
         }

         if (!ingredientFrom3.isAir()) {
            flag = true;
         }

         ShapedIngredient shapedIngredient = new ShapedIngredient(ingredientFrom, 0, -count);
         ShapedIngredient shapedIngredient2 = new ShapedIngredient(ingredientFrom2, 1, -count);
         ShapedIngredient shapedIngredient3 = new ShapedIngredient(ingredientFrom3, 2, -count);
         list.add(shapedIngredient);
         list.add(shapedIngredient2);
         list.add(shapedIngredient3);
         count++;
      }

      return !flag ? null : ShapedRecipe.unsharpen(new ShapedRecipe(text, list));
   }
}
