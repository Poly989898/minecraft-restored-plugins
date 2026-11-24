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

   public void setInput(@Nullable ProvidedUIFilter providedUIFilter) {
      this.input = providedUIFilter == null ? RecipeEditorGUI.AIR : providedUIFilter;
   }

   @NotNull
   public ConfigurationSection getSection() {
      return this.section;
   }

   public RMGRI_LegacyBurning(@NotNull ConfigurationSection section) {
      this.section = section;
      this.input = ProvidedUIFilter.getFromString(RecipeEditorGUI.poofFromLegacy(section.getString("item")), null);
      if (this.input == null) {
         this.input = RecipeEditorGUI.AIR.clone();
      }
   }

   @Override
   public void editInput(@NotNull ProvidedUIFilter providedUIFilter, int count) {
      if (count == 0) {
         this.setInput(providedUIFilter);
         this.section.set("item", providedUIFilter.toString());
      }
   }

   @Override
   public void editOutput(@NotNull ProvidedUIFilter providedUIFilter, int count) {
   }

   @Override
   public void deleteInput(int count) {
      this.editInput(RecipeEditorGUI.AIR.clone(), count);
   }

   @Override
   public void deleteOutput(int count) {
   }

   @Nullable
   @Override
   public ProvidedUIFilter getInput(int count) {
      return count == 0 ? this.input : null;
   }

   @Nullable
   @Override
   public ProvidedUIFilter getOutput(int count) {
      return null;
   }
}
