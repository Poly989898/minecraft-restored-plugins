package net.Indyuce.mmoitems.gui.edition.recipe.interpreter;

import io.lumine.mythic.lib.api.crafting.uimanager.ProvidedUIFilter;
import io.lumine.mythic.lib.api.util.ui.SilentNumbers;
import java.util.ArrayList;
import java.util.List;
import net.Indyuce.mmoitems.gui.edition.recipe.gui.RecipeEditorGUI;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RMGRI_Shaped implements RMG_RecipeInterpreter {
   @NotNull
   final ProvidedUIFilter[][] inputRecipe;
   @NotNull
   final ProvidedUIFilter[][] outputRecipe;
   @NotNull
   final ConfigurationSection section;
   public static final String emptyRow = "v AIR 0|v AIR 0|v AIR 0";

   @NotNull
   ProvidedUIFilter[][] buildIngredientsFromList(@NotNull List<String> var1) {
      ProvidedUIFilter[][] var2 = new ProvidedUIFilter[3][3];

      for (int var3 = 0; var3 < 3; var3++) {
         String var4 = var1.size() > var3 ? (String)var1.get(var3) : null;
         String var5 = updateRow(var4);
         String[] var6 = var5.split("\\|");

         for (int var7 = 0; var7 < 3; var7++) {
            String var8 = var6.length > var7 ? var6[var7] : null;
            ProvidedUIFilter var9 = ProvidedUIFilter.getFromString(var8, null);
            if (var9 == null) {
               var9 = RecipeEditorGUI.AIR.clone();
            }

            var2[var3][var7] = var9;
         }
      }

      return var2;
   }

   @NotNull
   ArrayList<String> toYML(@NotNull ProvidedUIFilter[][] var1) {
      ArrayList var2 = new ArrayList();

      for (int var3 = 0; var3 < 3; var3++) {
         ProvidedUIFilter[] var4 = var1.length > var3 ? var1[var3] : new ProvidedUIFilter[3];
         StringBuilder var5 = new StringBuilder();

         for (ProvidedUIFilter var9 : var4) {
            ProvidedUIFilter var10 = var9;
            if (var10 == null) {
               var10 = RecipeEditorGUI.AIR.clone();
            }

            if (var5.length() != 0) {
               var5.append("|");
            }

            var5.append(var10);
         }

         var2.add(var5.toString());
      }

      return var2;
   }

   public void setInput(int var1, @NotNull ProvidedUIFilter var2) {
      if (var1 >= 0 && var1 <= 8) {
         this.inputRecipe[SilentNumbers.floor(var1 / 3.0)][var1 - 3 * SilentNumbers.floor(var1 / 3.0)] = var2;
      }
   }

   @Nullable
   @Override
   public ProvidedUIFilter getInput(int var1) {
      return var1 >= 0 && var1 <= 8 ? this.inputRecipe[SilentNumbers.floor(var1 / 3.0)][var1 - 3 * SilentNumbers.floor(var1 / 3.0)] : null;
   }

   public void setOutput(int var1, @NotNull ProvidedUIFilter var2) {
      if (var1 >= 0 && var1 <= 8) {
         this.outputRecipe[SilentNumbers.floor(var1 / 3.0)][var1 - 3 * SilentNumbers.floor(var1 / 3.0)] = var2;
      }
   }

   @Nullable
   @Override
   public ProvidedUIFilter getOutput(int var1) {
      return var1 >= 0 && var1 <= 8 ? this.outputRecipe[SilentNumbers.floor(var1 / 3.0)][var1 - 3 * SilentNumbers.floor(var1 / 3.0)] : null;
   }

   @NotNull
   public ConfigurationSection getSection() {
      return this.section;
   }

   public RMGRI_Shaped(@NotNull ConfigurationSection var1) {
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

   @NotNull
   public static String updateRow(@Nullable String var0) {
      if (var0 != null && !var0.isEmpty()) {
         if (var0.contains("|")) {
            String[] var4 = var0.split("\\|");
            if (var4.length == 3) {
               return var0;
            }

            StringBuilder var5 = new StringBuilder();

            for (int var6 = 0; var6 < 3; var6++) {
               if (var6 != 0) {
                  var5.append("|");
               }

               if (var6 < var4.length) {
                  var5.append(RecipeEditorGUI.poofFromLegacy(var4[var6]));
               } else {
                  var5.append("v AIR 0");
               }
            }

            return var5.toString();
         } else if (var0.contains(" ")) {
            StringBuilder var1 = new StringBuilder();
            String[] var2 = var0.split(" ");

            for (int var3 = 0; var3 < 3; var3++) {
               if (var3 != 0) {
                  var1.append("|");
               }

               if (var3 < var2.length) {
                  var1.append(RecipeEditorGUI.poofFromLegacy(var2[var3]));
               } else {
                  var1.append("v AIR 0");
               }
            }

            return var1.toString();
         } else {
            return RecipeEditorGUI.poofFromLegacy(var0) + "|v AIR 0|v AIR 0";
         }
      } else {
         return "v AIR 0|v AIR 0|v AIR 0";
      }
   }
}
