package net.Indyuce.mmoitems.gui.edition.recipe.button;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.api.util.ItemFactory;
import io.lumine.mythic.lib.api.util.ui.FriendlyFeedbackProvider;
import io.lumine.mythic.lib.api.util.ui.SilentNumbers;
import java.util.Objects;
import net.Indyuce.mmoitems.ItemStats;
import net.Indyuce.mmoitems.api.edition.StatEdition;
import net.Indyuce.mmoitems.api.util.message.FFPMMOItems;
import net.Indyuce.mmoitems.gui.edition.recipe.gui.RecipeEditorGUI;
import net.Indyuce.mmoitems.stat.HidePotionEffects;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.jetbrains.annotations.NotNull;

public class RBA_AmountOutput extends RecipeButtonAction {
   @NotNull
   public final String[] amountLog = new String[]{
      FriendlyFeedbackProvider.quickForPlayer(FFPMMOItems.get(), "Write in the chat the amount of output of this recipe.", new String[0]),
      FriendlyFeedbackProvider.quickForPlayer(FFPMMOItems.get(), "It must be an integer number, ex $e4$b.", new String[0])
   };
   @NotNull
   final ItemStack button;
   public static final String AMOUNT_INGREDIENTS = "amount";

   public RBA_AmountOutput(@NotNull RecipeEditorGUI var1, @NotNull ItemStack var2) {
      super(var1);
      this.button = RecipeEditorGUI.rename(
         ItemFactory.of(var2.getType()).lore(SilentNumbers.chop("The amount of items produced every time the player crafts.", 65, "§7")).build(),
         "§cChoose Output Amount"
      );
      ItemMeta var3 = Objects.requireNonNull(this.button.getItemMeta());
      ItemMeta var4 = Objects.requireNonNull(var2.getItemMeta());
      if (MythicLib.plugin.getVersion().isStrictlyHigher(new int[]{1, 13}) && var4.hasCustomModelData()) {
         var3.setCustomModelData(var4.getCustomModelData());
      }

      if (var4 instanceof LeatherArmorMeta) {
         ((LeatherArmorMeta)var3).setColor(((LeatherArmorMeta)var4).getColor());
      }

      if (var4 instanceof BannerMeta) {
         ((BannerMeta)var3).setPatterns(((BannerMeta)var4).getPatterns());
      }

      var3.addItemFlags(new ItemFlag[]{ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_DYE, HidePotionEffects.ITEM_FLAG});
      this.button.setItemMeta(var3);
   }

   @Override
   public boolean runPrimary() {
      new StatEdition(this.inv, ItemStats.CRAFTING, 2, this).enable(this.amountLog);
      return true;
   }

   @Override
   public void primaryProcessInput(@NotNull String var1, Object... var2) {
      Integer var3 = SilentNumbers.IntegerParse(var1);
      if (var3 == null) {
         throw new IllegalArgumentException("Expected an integer number instead of $u" + var1);
      }

      if (var3 > 64) {
         throw new IllegalArgumentException("Max stack size is $e64$b, Minecraft doesnt support $u" + var1);
      }

      if (var3 <= 0) {
         throw new IllegalArgumentException("Min output stack size is $e0$b, you specified $u" + var1);
      }

      this.getInv().getNameSection().set("amount", var3);
   }

   @Override
   public boolean runSecondary() {
      this.getInv().getNameSection().set("amount", null);
      this.clickSFX();
      this.inv.registerTemplateEdition();
      return true;
   }

   public int getOutputAmount() {
      return this.getInv().getNameSection().getInt("amount", 1);
   }

   @Override
   public void secondaryProcessInput(@NotNull String var1, Object... var2) {
   }

   @NotNull
   @Override
   public ItemStack getButton() {
      ItemStack var1 = this.button.clone();
      var1.setAmount(this.getOutputAmount());
      return RecipeEditorGUI.addLore(
         var1,
         SilentNumbers.toArrayList(
            new String[]{"", ChatColor.YELLOW + "►" + " Right click to reset to 1.", ChatColor.YELLOW + "►" + " Left click to edit amount."}
         )
      );
   }
}
