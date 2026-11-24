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
   ProvidedUIFilter[] buildIngredientsFromList(@NotNull List<String> var1) {
      ProvidedUIFilter[] var2 = new ProvidedUIFilter[9];

      for (int var3 = 0; var3 < 9; var3++) {
         String var4 = var1.size() > var3 ? (String)var1.get(var3) : null;
         String var5 = RecipeEditorGUI.poofFromLegacy(var4);
         ProvidedUIFilter var6 = ProvidedUIFilter.getFromString(var5, null);
         if (var6 == null) {
            var6 = RecipeEditorGUI.AIR.clone();
         }

         var2[var3] = var6;
      }

      return var2;
   }

   @NotNull
   ArrayList<String> toYML(@NotNull ProvidedUIFilter[] var1) {
      ArrayList var2 = new ArrayList();

      for (int var3 = 0; var3 < 9; var3++) {
         ProvidedUIFilter var4 = var1.length > var3 ? var1[var3] : RecipeEditorGUI.AIR.clone();
         var2.add(var4.toString());
      }

      return var2;
   }

   public void setInput(int var1, @NotNull ProvidedUIFilter var2) {
      if (var1 >= 0 && var1 <= 8) {
         this.inputRecipe[var1] = var2;
      }
   }

   @Nullable
   @Override
   public ProvidedUIFilter getInput(int var1) {
      return var1 >= 0 && var1 <= 8 ? this.inputRecipe[var1] : null;
   }

   public void setOutput(int var1, @NotNull ProvidedUIFilter var2) {
      if (var1 >= 0 && var1 <= 8) {
         this.outputRecipe[var1] = var2;
      }
   }

   @Nullable
   @Override
   public ProvidedUIFilter getOutput(int var1) {
      return var1 >= 0 && var1 <= 8 ? this.outputRecipe[var1] : null;
   }

   @NotNull
   public ConfigurationSection getSection() {
      return this.section;
   }

   public RMGRI_Shapeless(@NotNull ConfigurationSection var1) {
      this.section = var1;
      this.inputRecipe = this.buildIngredientsFromList(this.section.getStringList("input"));
      this.outputRecipe = this.buildIngredientsFromList(this.section.getStringList("output"));
   }

   @Override
   public void editInput(@NotNull ProvidedUIFilter var1, int var2) {
      this.setInput(var2, var1);
      this.section.set("input", this.toYML(this.inputRecipe));
   }

   @Override
   public void editOutput(@NotNull ProvidedUIFilter var1, int var2) {
      this.setOutput(var2, var1);
      this.section.set("output", this.toYML(this.outputRecipe));
   }

   @Override
   public void deleteInput(int var1) {
      this.editInput(RecipeEditorGUI.AIR.clone(), var1);
   }

   @Override
   public void deleteOutput(int var1) {
      this.editOutput(RecipeEditorGUI.AIR.clone(), var1);
   }
}
