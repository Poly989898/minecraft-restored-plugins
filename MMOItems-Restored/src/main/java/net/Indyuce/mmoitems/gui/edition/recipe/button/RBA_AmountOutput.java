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

   public RBA_AmountOutput(@NotNull RecipeEditorGUI recipeEditorGUI, @NotNull ItemStack item) {
      super(recipeEditorGUI);
      this.button = RecipeEditorGUI.rename(
         ItemFactory.of(item.getType()).lore(SilentNumbers.chop("The amount of items produced every time the player crafts.", 65, "§7")).build(),
         "§cChoose Output Amount"
      );
      ItemMeta itemMeta = Objects.requireNonNull(this.button.getItemMeta());
      ItemMeta outputMeta = Objects.requireNonNull(item.getItemMeta());
      if (MythicLib.plugin.getVersion().isStrictlyHigher(new int[]{1, 13}) && outputMeta.hasCustomModelData()) {
         itemMeta.setCustomModelData(outputMeta.getCustomModelData());
      }

      if (outputMeta instanceof LeatherArmorMeta) {
         ((LeatherArmorMeta)itemMeta).setColor(((LeatherArmorMeta)outputMeta).getColor());
      }

      if (outputMeta instanceof BannerMeta) {
         ((BannerMeta)itemMeta).setPatterns(((BannerMeta)outputMeta).getPatterns());
      }

      itemMeta.addItemFlags(new ItemFlag[]{ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_DYE, HidePotionEffects.ITEM_FLAG});
      this.button.setItemMeta(itemMeta);
   }

   @Override
   public boolean runPrimary() {
      new StatEdition(this.inv, ItemStats.CRAFTING, 2, this).enable(this.amountLog);
      return true;
   }

   @Override
   public void primaryProcessInput(@NotNull String text, Object... objs) {
      Integer value = SilentNumbers.IntegerParse(text);
      if (value == null) {
         throw new IllegalArgumentException("Expected an integer number instead of $u" + text);
      }

      if (value > 64) {
         throw new IllegalArgumentException("Max stack size is $e64$b, Minecraft doesnt support $u" + text);
      }

      if (value <= 0) {
         throw new IllegalArgumentException("Min output stack size is $e0$b, you specified $u" + text);
      }

      this.getInv().getNameSection().set("amount", value);
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
   public void secondaryProcessInput(@NotNull String text, Object... objs) {
   }

   @NotNull
   @Override
   public ItemStack getButton() {
      ItemStack item = this.button.clone();
      item.setAmount(this.getOutputAmount());
      return RecipeEditorGUI.addLore(
         item,
         SilentNumbers.toArrayList(
            new String[]{"", ChatColor.YELLOW + "►" + " Right click to reset to 1.", ChatColor.YELLOW + "►" + " Left click to edit amount."}
         )
      );
   }
}
