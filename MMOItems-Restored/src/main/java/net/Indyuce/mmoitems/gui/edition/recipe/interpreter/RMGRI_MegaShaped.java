package net.Indyuce.mmoitems.gui.edition.recipe.interpreter;

import io.lumine.mythic.lib.api.crafting.uimanager.ProvidedUIFilter;
import io.lumine.mythic.lib.api.util.ui.SilentNumbers;
import java.util.ArrayList;
import java.util.List;
import net.Indyuce.mmoitems.gui.edition.recipe.gui.RecipeEditorGUI;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RMGRI_MegaShaped implements RMG_RecipeInterpreter {
   @NotNull
   final ProvidedUIFilter[][] inputRecipe;
   @NotNull
   final ProvidedUIFilter[][] outputRecipe;
   @NotNull
   final ConfigurationSection section;
   public static final String emptyRow = "v AIR 0|v AIR 0|v AIR 0|v AIR 0|v AIR 0|v AIR 0";

   @NotNull
   ProvidedUIFilter[][] buildIngredientsFromList(@NotNull List<String> texts) {
      ProvidedUIFilter[][] providedUIFilterses = new ProvidedUIFilter[6][6];

      for (int i = 0; i < 6; i++) {
         String text = texts.size() > i ? (String)texts.get(i) : null;
         String updatedRow = updateRow(text);
         String[] rowCells = updatedRow.split("\\|");

         for (int j = 0; j < 6; j++) {
            String cell = rowCells.length > j ? rowCells[j] : null;
            ProvidedUIFilter providedUIFilter = ProvidedUIFilter.getFromString(cell, null);
            if (providedUIFilter == null) {
               providedUIFilter = RecipeEditorGUI.AIR.clone();
            }

            providedUIFilterses[i][j] = providedUIFilter;
         }
      }

      return providedUIFilterses;
   }

   @NotNull
   ArrayList<String> toYML(@NotNull ProvidedUIFilter[][] providedUIFilterses) {
      ArrayList list = new ArrayList();

      for (int i = 0; i < 6; i++) {
         ProvidedUIFilter[] providedUIFilters = providedUIFilterses.length > i ? providedUIFilterses[i] : new ProvidedUIFilter[5];
         StringBuilder builder = new StringBuilder();

         for (ProvidedUIFilter providedUIFilter : providedUIFilters) {
            ProvidedUIFilter ingredientFilter = providedUIFilter;
            if (ingredientFilter == null) {
               ingredientFilter = RecipeEditorGUI.AIR.clone();
            }

            if (builder.length() != 0) {
               builder.append("|");
            }

            builder.append(ingredientFilter);
         }

         list.add(builder.toString());
      }

      return list;
   }

   public void setInput(int count, @NotNull ProvidedUIFilter providedUIFilter) {
      if (count >= 0 && count <= 35) {
         this.inputRecipe[SilentNumbers.floor(count / 6.0)][count - 6 * SilentNumbers.floor(count / 6.0)] = providedUIFilter;
      }
   }

   @Nullable
   @Override
   public ProvidedUIFilter getInput(int count) {
      return count >= 0 && count <= 35 ? this.inputRecipe[SilentNumbers.floor(count / 6.0)][count - 6 * SilentNumbers.floor(count / 6.0)] : null;
   }

   public void setOutput(int count, @NotNull ProvidedUIFilter providedUIFilter) {
      if (count >= 0 && count <= 35) {
         this.outputRecipe[SilentNumbers.floor(count / 6.0)][count - 6 * SilentNumbers.floor(count / 6.0)] = providedUIFilter;
      }
   }

   @Nullable
   @Override
   public ProvidedUIFilter getOutput(int count) {
      return count >= 0 && count <= 35 ? this.outputRecipe[SilentNumbers.floor(count / 6.0)][count - 6 * SilentNumbers.floor(count / 6.0)] : null;
   }

   @NotNull
   public ConfigurationSection getSection() {
      return this.section;
   }

   public RMGRI_MegaShaped(@NotNull ConfigurationSection section) {
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

   @NotNull
   public static String updateRow(@Nullable String text) {
      if (text != null && !text.isEmpty()) {
         if (text.contains("|")) {
            String[] texts = text.split("\\|");
            if (texts.length == 6) {
               return text;
            }

            StringBuilder builder = new StringBuilder();

            for (int i = 0; i < 6; i++) {
               if (i != 0) {
                  builder.append("|");
               }

               if (i < texts.length) {
                  builder.append(RecipeEditorGUI.poofFromLegacy(texts[i]));
               } else {
                  builder.append("v AIR 0");
               }
            }

            return builder.toString();
         } else if (text.contains(" ")) {
            StringBuilder builder = new StringBuilder();
            String[] texts = text.split(" ");

            for (int i = 0; i < 6; i++) {
               if (i != 0) {
                  builder.append("|");
               }

               if (i < texts.length) {
                  builder.append(RecipeEditorGUI.poofFromLegacy(texts[i]));
               } else {
                  builder.append("v AIR 0");
               }
            }

            return builder.toString();
         } else {
            return RecipeEditorGUI.poofFromLegacy(text) + "|v AIR 0|v AIR 0|v AIR 0|v AIR 0|v AIR 0";
         }
      } else {
         return "v AIR 0|v AIR 0|v AIR 0|v AIR 0|v AIR 0|v AIR 0";
      }
   }
}
