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
   String toYML(@NotNull ProvidedUIFilter var1, @NotNull ProvidedUIFilter var2) {
      return var1 + "|" + var2;
   }

   @NotNull
   public ProvidedUIFilter getInputItem() {
      return this.inputItem;
   }

   public void setInputItem(@NotNull ProvidedUIFilter var1) {
      this.inputItem = var1;
   }

   @NotNull
   public ProvidedUIFilter getOutputItem() {
      return this.outputItem;
   }

   public void setOutputItem(@NotNull ProvidedUIFilter var1) {
      this.outputItem = var1;
   }

   @NotNull
   public ProvidedUIFilter getInputIngot() {
      return this.inputIngot;
   }

   public void setInputIngot(@NotNull ProvidedUIFilter var1) {
      this.inputIngot = var1;
   }

   @NotNull
   public ProvidedUIFilter getOutputIngot() {
      return this.outputIngot;
   }

   public void setOutputIngot(@NotNull ProvidedUIFilter var1) {
      this.outputIngot = var1;
   }

   @NotNull
   public ConfigurationSection getSection() {
      return this.section;
   }

   public RMGRI_Smithing(@NotNull ConfigurationSection var1) {
      this.section = var1;
      String var2 = updateIngredients(this.section.getString("input"));
      String var3 = updateIngredients(this.section.getString("output"));
      String[] var4 = var2.split("\\|");
      String[] var5 = var3.split("\\|");
      ProvidedUIFilter var6 = ProvidedUIFilter.getFromString(var4[0], null);
      ProvidedUIFilter var7 = ProvidedUIFilter.getFromString(var5[0], null);
      ProvidedUIFilter var8 = ProvidedUIFilter.getFromString(var4[1], null);
      ProvidedUIFilter var9 = ProvidedUIFilter.getFromString(var5[1], null);
      this.inputItem = var6 != null ? var6 : RecipeEditorGUI.AIR.clone();
      this.inputIngot = var8 != null ? var8 : RecipeEditorGUI.AIR.clone();
      this.outputItem = var7 != null ? var7 : RecipeEditorGUI.AIR.clone();
      this.outputIngot = var9 != null ? var9 : RecipeEditorGUI.AIR.clone();
   }

   public void setInput(int var1, @NotNull ProvidedUIFilter var2) {
      if (var1 == 0) {
         this.setInputItem(var2);
      } else if (var1 == 1) {
         this.setInputIngot(var2);
      }
   }

   @Nullable
   @Override
   public ProvidedUIFilter getInput(int var1) {
      if (var1 == 0) {
         return this.getInputItem();
      } else {
         return var1 == 1 ? this.getInputIngot() : null;
      }
   }

   public void setOutput(int var1, @NotNull ProvidedUIFilter var2) {
      if (var1 == 0) {
         this.setOutputItem(var2);
      } else if (var1 == 1) {
         this.setOutputIngot(var2);
      }
   }

   @Nullable
   @Override
   public ProvidedUIFilter getOutput(int var1) {
      if (var1 == 0) {
         return this.getOutputItem();
      } else {
         return var1 == 1 ? this.getOutputIngot() : null;
      }
   }

   @Override
   public void editInput(@NotNull ProvidedUIFilter var1, int var2) {
      this.setInput(var2, var1);
      this.section.set("input", this.toYML(this.getInputItem(), this.getInputIngot()));
   }

   @Override
   public void editOutput(@NotNull ProvidedUIFilter var1, int var2) {
      this.setOutput(var2, var1);
      this.section.set("output", this.toYML(this.getOutputItem(), this.getOutputIngot()));
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
   public static String updateIngredients(@Nullable String var0) {
      if (var0 != null && !var0.isEmpty()) {
         if (var0.contains("|")) {
            String[] var4 = var0.split("\\|");
            if (var4.length == 2) {
               return var0;
            }

            StringBuilder var5 = new StringBuilder();

            for (int var6 = 0; var6 < 2; var6++) {
               if (var6 != 0) {
                  var5.append("|");
               }

               if (var6 < var4.length) {
                  var5.append(RecipeEditorGUI.poofFromLegacy(var4[var6]));
               } else {
                  var5.append("v AIR -");
               }
            }

            return var5.toString();
         } else if (var0.contains(" ")) {
            StringBuilder var1 = new StringBuilder();
            String[] var2 = var0.split(" ");

            for (int var3 = 0; var3 < 2; var3++) {
               if (var3 != 0) {
                  var1.append("|");
               }

               if (var3 < var2.length) {
                  var1.append(RecipeEditorGUI.poofFromLegacy(var2[var3]));
               } else {
                  var1.append("v AIR -");
               }
            }

            return var1.toString();
         } else {
            return RecipeEditorGUI.poofFromLegacy(var0) + "|v AIR 0";
         }
      } else {
         return "v AIR -|v AIR -";
      }
   }
}
