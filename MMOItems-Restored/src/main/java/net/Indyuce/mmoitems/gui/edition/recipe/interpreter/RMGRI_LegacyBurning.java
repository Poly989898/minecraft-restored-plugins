package net.Indyuce.mmoitems.gui.edition.recipe.interpreter;

import io.lumine.mythic.lib.api.crafting.uimanager.ProvidedUIFilter;
import net.Indyuce.mmoitems.gui.edition.recipe.gui.RecipeEditorGUI;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Deprecated
public class RMGRI_LegacyBurning implements RMG_RecipeInterpreter {
   @NotNull
   ProvidedUIFilter input;
   @NotNull
   final ConfigurationSection section;
   public static final String ITEM = "item";
   public static final String TIME = "time";
   public static final String EXPERIENCE = "experience";

   @NotNull
   public ProvidedUIFilter getInput() {
      return this.input;
   }

   public void setInput(@Nullable ProvidedUIFilter var1) {
      this.input = var1 == null ? RecipeEditorGUI.AIR : var1;
   }

   @NotNull
   public ConfigurationSection getSection() {
      return this.section;
   }

   public RMGRI_LegacyBurning(@NotNull ConfigurationSection var1) {
      this.section = var1;
      this.input = ProvidedUIFilter.getFromString(RecipeEditorGUI.poofFromLegacy(var1.getString("item")), null);
      if (this.input == null) {
         this.input = RecipeEditorGUI.AIR.clone();
      }
   }

   @Override
   public void editInput(@NotNull ProvidedUIFilter var1, int var2) {
      if (var2 == 0) {
         this.setInput(var1);
         this.section.set("item", var1.toString());
      }
   }

   @Override
   public void editOutput(@NotNull ProvidedUIFilter var1, int var2) {
   }

   @Override
   public void deleteInput(int var1) {
      this.editInput(RecipeEditorGUI.AIR.clone(), var1);
   }

   @Override
   public void deleteOutput(int var1) {
   }

   @Nullable
   @Override
   public ProvidedUIFilter getInput(int var1) {
      return var1 == 0 ? this.input : null;
   }

   @Nullable
   @Override
   public ProvidedUIFilter getOutput(int var1) {
      return null;
   }
}
