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
import net.Indyuce.mmoitems.gui.edition.recipe.gui.RMG_SuperShaped;
import net.Indyuce.mmoitems.gui.edition.recipe.gui.RecipeEditorGUI;
import net.Indyuce.mmoitems.gui.edition.recipe.interpreter.RMGRI_SuperShaped;
import net.Indyuce.mmoitems.stat.data.StringData;
import net.Indyuce.mmoitems.stat.data.random.RandomStatData;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RMGRR_SuperShaped implements RecipeRegistry {
   @NotNull
   final ItemStack displayListItem = RecipeEditorGUI.rename(new ItemStack(Material.NOTE_BLOCK), FFPMMOItems.get().getExampleFormat() + "Super Shaped Recipe");

   @NotNull
   @Override
   public String getRecipeConfigPath() {
      return "supershaped";
   }

   @NotNull
   @Override
   public String getRecipeTypeName() {
      return "Super Shaped";
   }

   @NotNull
   @Override
   public ItemStack getDisplayListItem() {
      return this.displayListItem;
   }

   @Override
   public void openForPlayer(@NotNull EditionInventory editionInventory, @NotNull String text, Object... objs) {
      new RMG_SuperShaped(editionInventory.getPlayer(), editionInventory.getEdited(), text, this).open(editionInventory);
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
      ConfigurationSection inputSection = RecipeEditorGUI.moveInput(section, text);
      NamespacedKey namespacedKey = (NamespacedKey)ref.getValue();
      if (namespacedKey == null) {
         throw new IllegalArgumentException(FriendlyFeedbackProvider.quickForConsole(FFPMMOItems.get(), "Illegal (Null) Namespace", new String[0]));
      }

      ShapedRecipe shapedRecipe = superShapedRecipeFromList(namespacedKey.getKey(), new ArrayList<>(inputSection.getStringList("input")), friendlyFeedbackProvider);
      if (shapedRecipe == null) {
         throw new IllegalArgumentException(
            FriendlyFeedbackProvider.quickForConsole(FFPMMOItems.get(), "Shaped recipe containing only AIR, $fignored$b.", new String[0])
         );
      }

      ShapedRecipe outputRecipe = superShapedRecipeFromList(namespacedKey.getKey(), new ArrayList<>(inputSection.getStringList("output")), friendlyFeedbackProvider);
      int amount = inputSection.getInt("amount", 1);
      ShapedRecipe singleRecipe = ShapedRecipe.single(
         namespacedKey.getKey(), new ProvidedUIFilter[]{new ProvidedUIFilter(MMOItemUIFilter.get(), mmoItemTemplate.getType().getId(), mmoItemTemplate.getId(), Math.max(amount, 1))}
      );
      MRORecipe mroRecipe = new MRORecipe(singleRecipe, outputRecipe);
      MythicRecipeBlueprint mythicRecipeBlueprint = new MythicRecipeBlueprint(shapedRecipe, mroRecipe, namespacedKey);
      RandomStatData randomStatData = mmoItemTemplate.getBaseItemData().get(ItemStats.CRAFT_PERMISSION);
      if (randomStatData instanceof StringData) {
         String outputText = ((StringData)randomStatData).getString();
         if (outputText != null) {
            mythicRecipeBlueprint.addRequiredPermission(outputText);
         }
      }

      mythicRecipeBlueprint.deploy(MythicRecipeStation.WORKBENCH, null);
      ref.setValue(null);
      return mythicRecipeBlueprint;
   }

   @Nullable
   public static ShapedRecipe superShapedRecipeFromList(@NotNull String text, @NotNull ArrayList<String> texts, @NotNull FriendlyFeedbackProvider friendlyFeedbackProvider) {
      ArrayList list = new ArrayList();
      boolean flag = false;
      int count = 0;

      for (String row : texts) {
         String updatedRow = RMGRI_SuperShaped.updateRow(row);
         String[] rowCells;
         if (updatedRow.contains("|")) {
            rowCells = updatedRow.split("\\|");
         } else {
            rowCells = updatedRow.split(" ");
         }

         if (rowCells.length != 5) {
            throw new IllegalArgumentException("Invalid super crafting table row $u" + updatedRow + "$b ($fNot exactly 5 ingredients wide$b).");
         }

         ProvidedUIFilter ingredientFrom = RecipeEditorGUI.readIngredientFrom(rowCells[0], friendlyFeedbackProvider);
         ProvidedUIFilter secondFilter = RecipeEditorGUI.readIngredientFrom(rowCells[1], friendlyFeedbackProvider);
         ProvidedUIFilter thirdFilter = RecipeEditorGUI.readIngredientFrom(rowCells[2], friendlyFeedbackProvider);
         ProvidedUIFilter fourthFilter = RecipeEditorGUI.readIngredientFrom(rowCells[3], friendlyFeedbackProvider);
         ProvidedUIFilter fifthFilter = RecipeEditorGUI.readIngredientFrom(rowCells[4], friendlyFeedbackProvider);
         if (!ingredientFrom.isAir()) {
            flag = true;
         }

         if (!secondFilter.isAir()) {
            flag = true;
         }

         if (!thirdFilter.isAir()) {
            flag = true;
         }

         if (!fourthFilter.isAir()) {
            flag = true;
         }

         if (!fifthFilter.isAir()) {
            flag = true;
         }

         ShapedIngredient shapedIngredient = new ShapedIngredient(ingredientFrom, 0, -count);
         ShapedIngredient secondIngredient = new ShapedIngredient(secondFilter, 1, -count);
         ShapedIngredient thirdIngredient = new ShapedIngredient(thirdFilter, 2, -count);
         ShapedIngredient fourthIngredient = new ShapedIngredient(fourthFilter, 3, -count);
         ShapedIngredient fifthIngredient = new ShapedIngredient(fifthFilter, 4, -count);
         list.add(shapedIngredient);
         list.add(secondIngredient);
         list.add(thirdIngredient);
         list.add(fourthIngredient);
         list.add(fifthIngredient);
         count++;
      }

      return !flag ? null : ShapedRecipe.unsharpen(new ShapedRecipe(text, list));
   }
}
