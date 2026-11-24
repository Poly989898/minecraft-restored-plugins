package net.Indyuce.mmoitems.gui.edition.recipe.gui;

import java.util.HashMap;
import net.Indyuce.mmoitems.api.item.template.MMOItemTemplate;
import net.Indyuce.mmoitems.gui.edition.recipe.button.RBA_InputOutput;
import net.Indyuce.mmoitems.gui.edition.recipe.interpreter.RMGRI_MegaShaped;
import net.Indyuce.mmoitems.gui.edition.recipe.interpreter.RMG_RecipeInterpreter;
import net.Indyuce.mmoitems.gui.edition.recipe.registry.RecipeRegistry;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class RMG_MegaShaped extends RecipeEditorGUI {
   @NotNull
   HashMap<Integer, Integer> inputLinks = new HashMap<>();
   @NotNull
   final RMGRI_MegaShaped interpreter;

   public RMG_MegaShaped(@NotNull Player var1, @NotNull MMOItemTemplate var2, @NotNull String var3, @NotNull RecipeRegistry var4) {
      super(var1, var2, var3, var4);
      this.addButton(new RBA_InputOutput(this));
      this.interpreter = new RMGRI_MegaShaped(this.getNameSection());
      this.inputLinks.put(1, 0);
      this.inputLinks.put(2, 1);
      this.inputLinks.put(3, 2);
      this.inputLinks.put(4, 3);
      this.inputLinks.put(5, 4);
      this.inputLinks.put(6, 5);
      this.inputLinks.put(10, 6);
      this.inputLinks.put(11, 7);
      this.inputLinks.put(12, 8);
      this.inputLinks.put(13, 9);
      this.inputLinks.put(14, 10);
      this.inputLinks.put(15, 11);
      this.inputLinks.put(19, 12);
      this.inputLinks.put(20, 13);
      this.inputLinks.put(21, 14);
      this.inputLinks.put(22, 15);
      this.inputLinks.put(23, 16);
      this.inputLinks.put(24, 17);
      this.inputLinks.put(28, 18);
      this.inputLinks.put(29, 19);
      this.inputLinks.put(30, 20);
      this.inputLinks.put(31, 21);
      this.inputLinks.put(32, 22);
      this.inputLinks.put(33, 23);
      this.inputLinks.put(37, 24);
      this.inputLinks.put(38, 25);
      this.inputLinks.put(39, 26);
      this.inputLinks.put(40, 27);
      this.inputLinks.put(41, 28);
      this.inputLinks.put(42, 29);
      this.inputLinks.put(46, 30);
      this.inputLinks.put(47, 31);
      this.inputLinks.put(48, 32);
      this.inputLinks.put(49, 33);
      this.inputLinks.put(50, 34);
      this.inputLinks.put(51, 35);
   }

   @Override
   public int getButtonsRow() {
      return -1;
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
