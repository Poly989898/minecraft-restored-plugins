package net.Indyuce.mmoitems.gui.edition.recipe.interpreter;

import io.lumine.mythic.lib.api.crafting.uimanager.ProvidedUIFilter;
import java.util.ArrayList;
import java.util.List;
import net.Indyuce.mmoitems.gui.edition.recipe.gui.RecipeEditorGUI;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RMGRI_Shapeless implements RMG_RecipeInterpreter {
   @NotNull
   final ProvidedUIFilter[] inputRecipe;
   @NotNull
   final ProvidedUIFilter[] outputRecipe;
   @NotNull
   final ConfigurationSection section;

   @NotNull
   ProvidedUIFilter[] buildIngredientsFromList(@NotNull List<String> texts) {
      ProvidedUIFilter[] providedUIFilters = new ProvidedUIFilter[9];

      for (int i = 0; i < 9; i++) {
         String text = texts.size() > i ? (String)texts.get(i) : null;
         String updatedIngredient = RecipeEditorGUI.poofFromLegacy(text);
         ProvidedUIFilter providedUIFilter = ProvidedUIFilter.getFromString(updatedIngredient, null);
         if (providedUIFilter == null) {
            providedUIFilter = RecipeEditorGUI.AIR.clone();
         }

         providedUIFilters[i] = providedUIFilter;
      }

      return providedUIFilters;
   }

   @NotNull
   ArrayList<String> toYML(@NotNull ProvidedUIFilter[] providedUIFilters) {
      ArrayList list = new ArrayList();

      for (int i = 0; i < 9; i++) {
         ProvidedUIFilter providedUIFilter = providedUIFilters.length > i ? providedUIFilters[i] : RecipeEditorGUI.AIR.clone();
         list.add(providedUIFilter.toString());
      }

      return list;
   }

   public void setInput(int index, @NotNull ProvidedUIFilter providedUIFilter) {
      if (index >= 0 && index <= 8) {
         this.inputRecipe[index] = providedUIFilter;
      }
   }

   @Nullable
   @Override
   public ProvidedUIFilter getInput(int index) {
      return index >= 0 && index <= 8 ? this.inputRecipe[index] : null;
   }

   public void setOutput(int index, @NotNull ProvidedUIFilter providedUIFilter) {
      if (index >= 0 && index <= 8) {
         this.outputRecipe[index] = providedUIFilter;
      }
   }

   @Nullable
   @Override
   public ProvidedUIFilter getOutput(int index) {
      return index >= 0 && index <= 8 ? this.outputRecipe[index] : null;
   }

   @NotNull
   public ConfigurationSection getSection() {
      return this.section;
   }

   public RMGRI_Shapeless(@NotNull ConfigurationSection section) {
      this.section = section;
      this.inputRecipe = this.buildIngredientsFromList(this.section.getStringList("input"));
      this.outputRecipe = this.buildIngredientsFromList(this.section.getStringList("output"));
   }

   @Override
   public void editInput(@NotNull ProvidedUIFilter providedUIFilter, int count) {
      this.setInput(count, providedUIFilter);
      this.section.set("input", this.toYML(this.inputRecipe));
   }

   @Override
   public void editOutput(@NotNull ProvidedUIFilter providedUIFilter, int count) {
      this.setOutput(count, providedUIFilter);
      this.section.set("output", this.toYML(this.outputRecipe));
   }

   @Override
   public void deleteInput(int count) {
      this.editInput(RecipeEditorGUI.AIR.clone(), count);
   }

   @Override
   public void deleteOutput(int count) {
      this.editOutput(RecipeEditorGUI.AIR.clone(), count);
   }
}
