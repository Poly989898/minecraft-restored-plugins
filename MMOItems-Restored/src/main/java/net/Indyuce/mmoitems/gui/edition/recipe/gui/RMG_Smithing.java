package net.Indyuce.mmoitems.gui.edition.recipe.gui;

import java.util.HashMap;
import net.Indyuce.mmoitems.api.item.template.MMOItemTemplate;
import net.Indyuce.mmoitems.gui.edition.recipe.button.RBA_DropGems;
import net.Indyuce.mmoitems.gui.edition.recipe.button.RBA_InputOutput;
import net.Indyuce.mmoitems.gui.edition.recipe.button.RBA_SmithingEnchantments;
import net.Indyuce.mmoitems.gui.edition.recipe.button.RBA_SmithingUpgrades;
import net.Indyuce.mmoitems.gui.edition.recipe.interpreter.RMGRI_Smithing;
import net.Indyuce.mmoitems.gui.edition.recipe.interpreter.RMG_RecipeInterpreter;
import net.Indyuce.mmoitems.gui.edition.recipe.registry.RecipeRegistry;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class RMG_Smithing extends RecipeEditorGUI {
   @NotNull
   final HashMap<Integer, Integer> inputLinks = new HashMap<>();
   @NotNull
   final RMGRI_Smithing interpreter;

   public RMG_Smithing(@NotNull Player var1, @NotNull MMOItemTemplate var2, @NotNull String var3, @NotNull RecipeRegistry var4) {
      super(var1, var2, var3, var4);
      ConfigurationSection var5 = RecipeEditorGUI.getSection(this.getEditedSection(), "crafting");
      ConfigurationSection var6 = RecipeEditorGUI.getSection(var5, this.getRecipeRegistry().getRecipeConfigPath());
      ConfigurationSection var7 = RecipeEditorGUI.getSection(var6, this.getRecipeName());
      this.interpreter = new RMGRI_Smithing(var7);
      this.inputLinks.put(39, 0);
      this.inputLinks.put(41, 1);
      this.addButton(new RBA_InputOutput(this));
      this.addButton(new RBA_SmithingUpgrades(this));
      this.addButton(new RBA_SmithingEnchantments(this));
      this.addButton(new RBA_DropGems(this));
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

   @Override
   public int getButtonsRow() {
      return 1;
   }

   @NotNull
   @Override
   public RMG_RecipeInterpreter getInterpreter() {
      return this.interpreter;
   }
}
