package net.Indyuce.mmoitems.gui.edition.recipe.gui;

import java.util.HashMap;
import net.Indyuce.mmoitems.api.item.template.MMOItemTemplate;
import net.Indyuce.mmoitems.gui.edition.recipe.button.RBA_HideFromBook;
import net.Indyuce.mmoitems.gui.edition.recipe.button.RBA_InputOutput;
import net.Indyuce.mmoitems.gui.edition.recipe.interpreter.RMGRI_Shapeless;
import net.Indyuce.mmoitems.gui.edition.recipe.interpreter.RMG_RecipeInterpreter;
import net.Indyuce.mmoitems.gui.edition.recipe.registry.RecipeRegistry;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class RMG_Shapeless extends RecipeEditorGUI {
   @NotNull
   final HashMap<Integer, Integer> inputLinks = new HashMap<>();
   @NotNull
   final RMGRI_Shapeless interpreter;

   public RMG_Shapeless(@NotNull Player var1, @NotNull MMOItemTemplate var2, @NotNull String var3, @NotNull RecipeRegistry var4) {
      super(var1, var2, var3, var4);
      this.addButton(new RBA_InputOutput(this));
      this.addButton(new RBA_HideFromBook(this));
      ConfigurationSection var5 = RecipeEditorGUI.getSection(this.getEditedSection(), "crafting");
      ConfigurationSection var6 = RecipeEditorGUI.getSection(var5, this.getRecipeRegistry().getRecipeConfigPath());
      ConfigurationSection var7 = RecipeEditorGUI.getSection(var6, this.getRecipeName());
      this.interpreter = new RMGRI_Shapeless(var7);
      this.inputLinks.put(30, 0);
      this.inputLinks.put(31, 1);
      this.inputLinks.put(32, 2);
      this.inputLinks.put(39, 3);
      this.inputLinks.put(40, 4);
      this.inputLinks.put(41, 5);
      this.inputLinks.put(48, 6);
      this.inputLinks.put(49, 7);
      this.inputLinks.put(50, 8);
   }

   @Override
   public int getButtonsRow() {
      return 1;
   }

   @Override
   public void putRecipe() {
      for (Integer var2 : this.inputLinks.keySet()) {
         this.inventory.setItem(var2, this.getDisplay(this.isShowingInput(), this.inputLinks.get(var2)));
      }
   }

   @Override
   int getInputSlot(int var1) {
      Integer var2 = this.inputLinks.get(var1);
      return var2 != null ? var2 : -1;
   }

   @NotNull
   @Override
   public RMG_RecipeInterpreter getInterpreter() {
      return this.interpreter;
   }
}
