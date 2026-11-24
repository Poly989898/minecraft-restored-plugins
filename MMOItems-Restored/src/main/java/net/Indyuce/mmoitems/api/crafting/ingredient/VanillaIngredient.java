package net.Indyuce.mmoitems.api.crafting.ingredient;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.api.MMOLineConfig;
import io.lumine.mythic.lib.api.crafting.uifilters.VanillaUIFilter;
import io.lumine.mythic.lib.api.crafting.uimanager.ProvidedUIFilter;
import io.lumine.mythic.lib.api.crafting.uimanager.UIFilterManager;
import io.lumine.mythic.lib.api.util.ui.FriendlyFeedbackCategory;
import io.lumine.mythic.lib.api.util.ui.FriendlyFeedbackMessage;
import io.lumine.mythic.lib.api.util.ui.FriendlyFeedbackProvider;
import io.lumine.mythic.lib.api.util.ui.SilentNumbers;
import net.Indyuce.mmoitems.api.crafting.ingredient.inventory.VanillaPlayerIngredient;
import net.Indyuce.mmoitems.api.player.RPGPlayer;
import net.Indyuce.mmoitems.api.util.message.FFPMMOItems;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class VanillaIngredient extends Ingredient<VanillaPlayerIngredient> {
   @NotNull
   final ProvidedUIFilter filter;
   @NotNull
   final Material material;
   @NotNull
   final String display;
   @Nullable
   final String displayName;
   boolean vanillaBackward = true;

   @NotNull
   public ProvidedUIFilter getFilter() {
      return this.filter;
   }

   @NotNull
   public Material getMaterial() {
      return this.material;
   }

   public VanillaIngredient(MMOLineConfig mmoLineConfig) {
      super("vanilla", mmoLineConfig);
      FriendlyFeedbackProvider friendlyFeedbackProvider = new FriendlyFeedbackProvider(FFPMMOItems.get());
      mmoLineConfig.validate(new String[]{"type"});
      String type = mmoLineConfig.getString("type", "");
      if (type.contains(" ")) {
         this.vanillaBackward = false;
         ProvidedUIFilter uIFilter = UIFilterManager.getUIFilter(type, friendlyFeedbackProvider);
         if (uIFilter == null) {
            throw new IllegalArgumentException(
               SilentNumbers.collapseList(
                  SilentNumbers.transcribeList(
                     friendlyFeedbackProvider.getFeedbackOf(FriendlyFeedbackCategory.ERROR),
                     obj -> obj instanceof FriendlyFeedbackMessage ? ((FriendlyFeedbackMessage)obj).forConsole(FFPMMOItems.get()) : ""
                  ),
                  ""
               )
            );
         }

         this.filter = uIFilter;
         if (!this.filter.isValid(friendlyFeedbackProvider)) {
            throw new IllegalArgumentException(
               SilentNumbers.collapseList(
                  SilentNumbers.transcribeList(
                     friendlyFeedbackProvider.getFeedbackOf(FriendlyFeedbackCategory.ERROR),
                     obj -> obj instanceof FriendlyFeedbackMessage ? ((FriendlyFeedbackMessage)obj).forConsole(FFPMMOItems.get()) : ""
                  ),
                  ""
               )
            );
         }

         this.setAmount(this.filter.getAmount(this.getAmount()));
         this.filter.setAmount(this.getAmount());
         this.display = mmoLineConfig.getString("display", this.findName());
         this.material = Material.STONE;
      } else {
         this.material = Material.valueOf(type.toUpperCase().replace("-", "_").replace(" ", "_"));
         this.filter = new ProvidedUIFilter(VanillaUIFilter.get(), this.material.toString(), "0");
         this.filter.setAmount(this.getAmount());
         this.display = mmoLineConfig.getString("display", UtilityMethods.caseOnWords(this.material.toString().toLowerCase().replace("_", " ")));
      }

      if (this.filter.getItemStack(null) == null) {
         throw new IllegalArgumentException(
            SilentNumbers.collapseList(
               SilentNumbers.transcribeList(
                  friendlyFeedbackProvider.getFeedbackOf(FriendlyFeedbackCategory.ERROR),
                  obj -> obj instanceof FriendlyFeedbackMessage ? ((FriendlyFeedbackMessage)obj).forConsole(FFPMMOItems.get()) : ""
               ),
               ""
            )
         );
      }

      this.displayName = mmoLineConfig.contains("name") ? mmoLineConfig.getString("name") : null;
   }

   @Override
   public String getKey() {
      return "vanilla:"
         + (this.vanillaBackward ? this.material.toString().toLowerCase() : this.filter.toString().toLowerCase().replace(" ", "__"))
         + "_"
         + this.displayName;
   }

   @Override
   public String formatDisplay(String text) {
      return text.replace("#item#", this.display).replace("#amount#", String.valueOf(this.getAmount()));
   }

   public boolean matches(VanillaPlayerIngredient vanillaPlayerIngredient) {
      if (this.vanillaBackward) {
         if (vanillaPlayerIngredient.getType() != this.material) {
            return false;
         } else {
            return vanillaPlayerIngredient.getDisplayName() != null ? vanillaPlayerIngredient.getDisplayName().equals(this.displayName) : this.displayName == null;
         }
      } else {
         return this.filter.matches(vanillaPlayerIngredient.getSourceItem(), true, null);
      }
   }

   @NotNull
   @Override
   public ItemStack generateItemStack(@NotNull RPGPlayer rpgPlayer, boolean flag) {
      ItemStack itemStack = this.filter.getItemStack(null);
      itemStack.setAmount(this.getAmount());
      if (this.displayName != null) {
         ItemMeta itemMeta = itemStack.getItemMeta();
         itemMeta.setDisplayName(MythicLib.plugin.parseColors(this.displayName));
         itemStack.setItemMeta(itemMeta);
      }

      return itemStack;
   }

   @NotNull
   String findName() {
      return SilentNumbers.getItemName(this.filter.getParent().getDisplayStack(this.filter.getArgument(), this.filter.getData(), null), false);
   }
}
