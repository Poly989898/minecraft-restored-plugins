package net.Indyuce.mmoitems.gui.edition.recipe.interpreter;

import io.lumine.mythic.lib.api.crafting.uimanager.ProvidedUIFilter;
import net.Indyuce.mmoitems.gui.edition.recipe.gui.RecipeEditorGUI;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RMGRI_Smithing implements RMG_RecipeInterpreter {
   @NotNull
   ProvidedUIFilter inputItem;
   @NotNull
   ProvidedUIFilter outputItem;
   @NotNull
   ProvidedUIFilter inputIngot;
   @NotNull
   ProvidedUIFilter outputIngot;
   @NotNull
   final ConfigurationSection section;
   public static final String emptyIngredients = "v AIR -|v AIR -";

   @NotNull
   String toYML(@NotNull ProvidedUIFilter providedUIFilter, @NotNull ProvidedUIFilter providedUIFilter2) {
      return providedUIFilter + "|" + providedUIFilter2;
   }

   @NotNull
   public ProvidedUIFilter getInputItem() {
      return this.inputItem;
   }

   public void setInputItem(@NotNull ProvidedUIFilter providedUIFilter) {
      this.inputItem = providedUIFilter;
   }

   @NotNull
   public ProvidedUIFilter getOutputItem() {
      return this.outputItem;
   }

   public void setOutputItem(@NotNull ProvidedUIFilter providedUIFilter) {
      this.outputItem = providedUIFilter;
   }

   @NotNull
   public ProvidedUIFilter getInputIngot() {
      return this.inputIngot;
   }

   public void setInputIngot(@NotNull ProvidedUIFilter providedUIFilter) {
      this.inputIngot = providedUIFilter;
   }

   @NotNull
   public ProvidedUIFilter getOutputIngot() {
      return this.outputIngot;
   }

   public void setOutputIngot(@NotNull ProvidedUIFilter providedUIFilter) {
      this.outputIngot = providedUIFilter;
   }

   @NotNull
   public ConfigurationSection getSection() {
      return this.section;
   }

   public RMGRI_Smithing(@NotNull ConfigurationSection section) {
      this.section = section;
      String text = updateIngredients(this.section.getString("input"));
      String updatedOutput = updateIngredients(this.section.getString("output"));
      String[] texts = text.split("\\|");
      String[] outputParts = updatedOutput.split("\\|");
      ProvidedUIFilter providedUIFilter = ProvidedUIFilter.getFromString(texts[0], null);
      ProvidedUIFilter outputFirstFilter = ProvidedUIFilter.getFromString(outputParts[0], null);
      ProvidedUIFilter inputSecondFilter = ProvidedUIFilter.getFromString(texts[1], null);
      ProvidedUIFilter outputSecondFilter = ProvidedUIFilter.getFromString(outputParts[1], null);
      this.inputItem = providedUIFilter != null ? providedUIFilter : RecipeEditorGUI.AIR.clone();
      this.inputIngot = inputSecondFilter != null ? inputSecondFilter : RecipeEditorGUI.AIR.clone();
      this.outputItem = outputFirstFilter != null ? outputFirstFilter : RecipeEditorGUI.AIR.clone();
      this.outputIngot = outputSecondFilter != null ? outputSecondFilter : RecipeEditorGUI.AIR.clone();
   }

   public void setInput(int count, @NotNull ProvidedUIFilter providedUIFilter) {
      if (count == 0) {
         this.setInputItem(providedUIFilter);
      } else if (count == 1) {
         this.setInputIngot(providedUIFilter);
      }
   }

   @Nullable
   @Override
   public ProvidedUIFilter getInput(int count) {
      if (count == 0) {
         return this.getInputItem();
      } else {
         return count == 1 ? this.getInputIngot() : null;
      }
   }

   public void setOutput(int count, @NotNull ProvidedUIFilter providedUIFilter) {
      if (count == 0) {
         this.setOutputItem(providedUIFilter);
      } else if (count == 1) {
         this.setOutputIngot(providedUIFilter);
      }
   }

   @Nullable
   @Override
   public ProvidedUIFilter getOutput(int count) {
      if (count == 0) {
         return this.getOutputItem();
      } else {
         return count == 1 ? this.getOutputIngot() : null;
      }
   }

   @Override
   public void editInput(@NotNull ProvidedUIFilter providedUIFilter, int count) {
      this.setInput(count, providedUIFilter);
      this.section.set("input", this.toYML(this.getInputItem(), this.getInputIngot()));
   }

   @Override
   public void editOutput(@NotNull ProvidedUIFilter providedUIFilter, int count) {
      this.setOutput(count, providedUIFilter);
      this.section.set("output", this.toYML(this.getOutputItem(), this.getOutputIngot()));
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
   public static String updateIngredients(@Nullable String text) {
      if (text != null && !text.isEmpty()) {
         if (text.contains("|")) {
            String[] texts = text.split("\\|");
            if (texts.length == 2) {
               return text;
            }

            StringBuilder builder = new StringBuilder();

            for (int i = 0; i < 2; i++) {
               if (i != 0) {
                  builder.append("|");
               }

               if (i < texts.length) {
                  builder.append(RecipeEditorGUI.poofFromLegacy(texts[i]));
               } else {
                  builder.append("v AIR -");
               }
            }

            return builder.toString();
         } else if (text.contains(" ")) {
            StringBuilder builder = new StringBuilder();
            String[] texts = text.split(" ");

            for (int i = 0; i < 2; i++) {
               if (i != 0) {
                  builder.append("|");
               }

               if (i < texts.length) {
                  builder.append(RecipeEditorGUI.poofFromLegacy(texts[i]));
               } else {
                  builder.append("v AIR -");
               }
            }

            return builder.toString();
         } else {
            return RecipeEditorGUI.poofFromLegacy(text) + "|v AIR 0";
         }
      } else {
         return "v AIR -|v AIR -";
      }
   }
}
