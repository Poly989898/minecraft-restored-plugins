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

   public VanillaIngredient(MMOLineConfig var1) {
      super("vanilla", var1);
      FriendlyFeedbackProvider var2 = new FriendlyFeedbackProvider(FFPMMOItems.get());
      var1.validate(new String[]{"type"});
      String var3 = var1.getString("type", "");
      if (var3.contains(" ")) {
         this.vanillaBackward = false;
         ProvidedUIFilter var4 = UIFilterManager.getUIFilter(var3, var2);
         if (var4 == null) {
            throw new IllegalArgumentException(
               SilentNumbers.collapseList(
                  SilentNumbers.transcribeList(
                     var2.getFeedbackOf(FriendlyFeedbackCategory.ERROR),
                     var0 -> var0 instanceof FriendlyFeedbackMessage ? ((FriendlyFeedbackMessage)var0).forConsole(FFPMMOItems.get()) : ""
                  ),
                  ""
               )
            );
         }

         this.filter = var4;
         if (!this.filter.isValid(var2)) {
            throw new IllegalArgumentException(
               SilentNumbers.collapseList(
                  SilentNumbers.transcribeList(
                     var2.getFeedbackOf(FriendlyFeedbackCategory.ERROR),
                     var0 -> var0 instanceof FriendlyFeedbackMessage ? ((FriendlyFeedbackMessage)var0).forConsole(FFPMMOItems.get()) : ""
                  ),
                  ""
               )
            );
         }

         this.setAmount(this.filter.getAmount(this.getAmount()));
         this.filter.setAmount(this.getAmount());
         this.display = var1.getString("display", this.findName());
         this.material = Material.STONE;
      } else {
         this.material = Material.valueOf(var3.toUpperCase().replace("-", "_").replace(" ", "_"));
         this.filter = new ProvidedUIFilter(VanillaUIFilter.get(), this.material.toString(), "0");
         this.filter.setAmount(this.getAmount());
         this.display = var1.getString("display", UtilityMethods.caseOnWords(this.material.toString().toLowerCase().replace("_", " ")));
      }

      if (this.filter.getItemStack(null) == null) {
         throw new IllegalArgumentException(
            SilentNumbers.collapseList(
               SilentNumbers.transcribeList(
                  var2.getFeedbackOf(FriendlyFeedbackCategory.ERROR),
                  var0 -> var0 instanceof FriendlyFeedbackMessage ? ((FriendlyFeedbackMessage)var0).forConsole(FFPMMOItems.get()) : ""
               ),
               ""
            )
         );
      }

      this.displayName = var1.contains("name") ? var1.getString("name") : null;
   }

   @Override
   public String getKey() {
      return "vanilla:"
         + (this.vanillaBackward ? this.material.toString().toLowerCase() : this.filter.toString().toLowerCase().replace(" ", "__"))
         + "_"
         + this.displayName;
   }

   @Override
   public String formatDisplay(String var1) {
      return var1.replace("#item#", this.display).replace("#amount#", String.valueOf(this.getAmount()));
   }

   public boolean matches(VanillaPlayerIngredient var1) {
      if (this.vanillaBackward) {
         if (var1.getType() != this.material) {
            return false;
         } else {
            return var1.getDisplayName() != null ? var1.getDisplayName().equals(this.displayName) : this.displayName == null;
         }
      } else {
         return this.filter.matches(var1.getSourceItem(), true, null);
      }
   }

   @NotNull
   @Override
   public ItemStack generateItemStack(@NotNull RPGPlayer var1, boolean var2) {
      ItemStack var3 = this.filter.getItemStack(null);
      var3.setAmount(this.getAmount());
      if (this.displayName != null) {
         ItemMeta var4 = var3.getItemMeta();
         var4.setDisplayName(MythicLib.plugin.parseColors(this.displayName));
         var3.setItemMeta(var4);
      }

      return var3;
   }

   @NotNull
   String findName() {
      return SilentNumbers.getItemName(this.filter.getParent().getDisplayStack(this.filter.getArgument(), this.filter.getData(), null), false);
   }
}
