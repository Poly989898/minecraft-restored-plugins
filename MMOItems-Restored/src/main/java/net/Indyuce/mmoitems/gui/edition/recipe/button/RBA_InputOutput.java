package net.Indyuce.mmoitems.gui.edition.recipe.button;

import io.lumine.mythic.lib.api.util.ItemFactory;
import io.lumine.mythic.lib.api.util.ui.SilentNumbers;
import net.Indyuce.mmoitems.gui.edition.recipe.gui.RecipeEditorGUI;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class RBA_InputOutput extends RecipeButtonAction {
   boolean showingInput;
   @NotNull
   final ItemStack button = ItemFactory.of(Material.CRAFTING_TABLE)
      .name("§cSwitch to Output Mode")
      .lore(
         SilentNumbers.chop(
            "INPUT is the ingredients of the recipe, but (like milk buckets when crafting a cake) these ingredients may not be entirely consumed. In such cases, use the OUTPUT mode to specify what the ingredients will turn into.",
            63,
            "§7"
         )
      )
      .build();

   public RBA_InputOutput(@NotNull RecipeEditorGUI var1) {
      super(var1);
      this.showingInput = true;
   }

   @Override
   public boolean runPrimary() {
      this.getInv().switchInput();
      this.getInv().refreshInventory();
      this.clickSFX();
      return true;
   }

   @Override
   public void primaryProcessInput(@NotNull String var1, Object... var2) {
   }

   @Override
   public boolean runSecondary() {
      return false;
   }

   @Override
   public void secondaryProcessInput(@NotNull String var1, Object... var2) {
   }

   @NotNull
   @Override
   public ItemStack getButton() {
      String var1 = this.getInv().isShowingInput() ? "§6INPUT" : "§3OUTPUT";
      return RecipeEditorGUI.addLore(
         this.button.clone(),
         SilentNumbers.toArrayList(new String[]{"§7Currently Showing: " + var1, "", ChatColor.YELLOW + "►" + " Left click to switch mode."})
      );
   }
}
