package net.Indyuce.mmoitems.gui.edition.recipe.button.type;

import io.lumine.mythic.lib.api.util.ui.FriendlyFeedbackProvider;
import io.lumine.mythic.lib.api.util.ui.QuickNumberRange;
import io.lumine.mythic.lib.api.util.ui.SilentNumbers;
import net.Indyuce.mmoitems.ItemStats;
import net.Indyuce.mmoitems.api.edition.StatEdition;
import net.Indyuce.mmoitems.api.util.message.FFPMMOItems;
import net.Indyuce.mmoitems.gui.edition.recipe.button.RecipeButtonAction;
import net.Indyuce.mmoitems.gui.edition.recipe.gui.RecipeEditorGUI;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class RBA_DoubleButton extends RecipeButtonAction {
   @NotNull
   public final String[] amountLog = new String[]{
      FriendlyFeedbackProvider.quickForPlayer(FFPMMOItems.get(), "Write in the chat a number, ex $e2.5$b.", new String[0])
   };
   @NotNull
   public final String[] integerLog = new String[]{
      FriendlyFeedbackProvider.quickForPlayer(FFPMMOItems.get(), "Write in the chat an integer number, ex $e8$b.", new String[0])
   };

   public RBA_DoubleButton(@NotNull RecipeEditorGUI var1) {
      super(var1);
   }

   public double getValue() {
      return this.getInv().getNameSection().getDouble(this.getDoubleConfigPath(), this.getDefaultValue());
   }

   @NotNull
   public abstract String getDoubleConfigPath();

   @Override
   public boolean runPrimary() {
      new StatEdition(this.getInv(), ItemStats.CRAFTING, 2, this).enable(this.requireInteger() ? this.integerLog : this.amountLog);
      return true;
   }

   @Override
   public void primaryProcessInput(@NotNull String var1, Object... var2) {
      Double var3;
      if (this.requireInteger()) {
         Integer var4 = SilentNumbers.IntegerParse(var1);
         if (var4 == null) {
            throw new IllegalArgumentException("Expected integer number instead of $u" + var1);
         }

         var3 = (double)var4.intValue();
      } else {
         var3 = SilentNumbers.DoubleParse(var1);
         if (var3 == null) {
            throw new IllegalArgumentException("Expected a number instead of $u" + var1);
         }
      }

      if (this.getRange() != null && !this.getRange().inRange(var3)) {
         throw new IllegalArgumentException("Number $r" + var3 + "$b is out of range. Expected " + this.getRange().toStringColored());
      }

      this.getInv().getNameSection().set(this.getDoubleConfigPath(), var3);
   }

   @Nullable
   public abstract QuickNumberRange getRange();

   public abstract boolean requireInteger();

   @Override
   public boolean runSecondary() {
      this.getInv().getNameSection().set(this.getDoubleConfigPath(), null);
      this.clickSFX();
      this.getInv().registerTemplateEdition();
      return true;
   }

   @Override
   public void secondaryProcessInput(@NotNull String var1, Object... var2) {
   }

   public abstract double getDefaultValue();

   @NotNull
   public abstract ItemStack getDoubleButton();

   @NotNull
   @Override
   public ItemStack getButton() {
      return RecipeEditorGUI.addLore(
         this.getDoubleButton().clone(),
         SilentNumbers.toArrayList(
            new String[]{
               "",
               "§7Current Value: " + this.getValue(),
               "",
               ChatColor.YELLOW + "►" + " Right click to reset §8(to§4 " + this.getDefaultValue() + "§8)§e.",
               ChatColor.YELLOW + "►" + " Left click to toggle this option."
            }
         )
      );
   }
}
