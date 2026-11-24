package net.Indyuce.mmoitems.api.crafting.recipe;

import io.lumine.mythic.lib.api.crafting.uimanager.ProvidedUIFilter;
import io.lumine.mythic.lib.api.crafting.uimanager.UIFilterManager;
import io.lumine.mythic.lib.api.util.SmartGive;
import io.lumine.mythic.lib.api.util.ui.FriendlyFeedbackCategory;
import io.lumine.mythic.lib.api.util.ui.FriendlyFeedbackMessage;
import io.lumine.mythic.lib.api.util.ui.FriendlyFeedbackProvider;
import io.lumine.mythic.lib.api.util.ui.SilentNumbers;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.crafting.ConfigMMOItem;
import net.Indyuce.mmoitems.api.crafting.CraftingStation;
import net.Indyuce.mmoitems.api.crafting.CraftingStatus;
import net.Indyuce.mmoitems.api.crafting.MMOItemUIFilter;
import net.Indyuce.mmoitems.api.crafting.ingredient.inventory.IngredientInventory;
import net.Indyuce.mmoitems.api.event.PlayerUseCraftingStationEvent;
import net.Indyuce.mmoitems.api.item.template.MMOItemTemplate;
import net.Indyuce.mmoitems.api.item.util.ConfigItems;
import net.Indyuce.mmoitems.api.player.PlayerData;
import net.Indyuce.mmoitems.api.player.RPGPlayer;
import net.Indyuce.mmoitems.api.util.message.FFPMMOItems;
import net.Indyuce.mmoitems.api.util.message.Message;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CraftingRecipe extends Recipe {
   @NotNull
   public static final String UNSPECIFIED = "N/A";
   private final double craftingTime;
   @NotNull
   private final ProvidedUIFilter output;
   @Nullable
   ConfigMMOItem identifiedMMO;

   public CraftingRecipe(@NotNull ConfigurationSection var1) {
      super(var1);
      this.craftingTime = var1.getDouble("crafting-time");
      String var2 = var1.getString("output.item", "N/A");
      String var3 = var1.getString("output.type", "N/A").toUpperCase().replace("-", "_").replace(" ", "_");
      String var4 = var1.getString("output.id", "N/A").toUpperCase().replace("-", "_").replace(" ", "_");
      FriendlyFeedbackProvider var5 = new FriendlyFeedbackProvider(FFPMMOItems.get());
      if (!"N/A".equals(var3) && !"N/A".equals(var4)) {
         ProvidedUIFilter var7 = UIFilterManager.getUIFilter("m", var3, var4, var1.getString("output.amount", "1"), var5);
         if (var7 == null) {
            throw new IllegalArgumentException(
               SilentNumbers.collapseList(
                  SilentNumbers.transcribeList(
                     var5.getFeedbackOf(FriendlyFeedbackCategory.ERROR),
                     var0 -> var0 instanceof FriendlyFeedbackMessage ? ((FriendlyFeedbackMessage)var0).forConsole(FFPMMOItems.get()) : ""
                  ),
                  ""
               )
            );
         }

         this.output = var7;
      } else {
         if ("N/A".equals(var2)) {
            throw new IllegalArgumentException(
               FriendlyFeedbackProvider.quickForConsole(FFPMMOItems.get(), "Config must contain a valid Type and ID, or a valid UIFilter. ", new String[0])
            );
         }

         ProvidedUIFilter var6 = UIFilterManager.getUIFilter(var2, var5);
         if (var6 == null) {
            throw new IllegalArgumentException(
               SilentNumbers.collapseList(
                  SilentNumbers.transcribeList(
                     var5.getFeedbackOf(FriendlyFeedbackCategory.ERROR),
                     var0 -> var0 instanceof FriendlyFeedbackMessage ? ((FriendlyFeedbackMessage)var0).forConsole(FFPMMOItems.get()) : ""
                  ),
                  ""
               )
            );
         }

         this.output = var6;
      }

      if (!this.output.isValid(var5)) {
         throw new IllegalArgumentException(
            SilentNumbers.collapseList(
               SilentNumbers.transcribeList(
                  var5.getFeedbackOf(FriendlyFeedbackCategory.ERROR),
                  var0 -> var0 instanceof FriendlyFeedbackMessage ? ((FriendlyFeedbackMessage)var0).forConsole(FFPMMOItems.get()) : ""
               ),
               ""
            )
         );
      }

      if (this.output.getItemStack(var5) == null) {
         throw new IllegalArgumentException(
            SilentNumbers.collapseList(
               SilentNumbers.transcribeList(
                  var5.getFeedbackOf(FriendlyFeedbackCategory.ERROR),
                  var0 -> var0 instanceof FriendlyFeedbackMessage ? ((FriendlyFeedbackMessage)var0).forConsole(FFPMMOItems.get()) : ""
               ),
               ""
            )
         );
      }

      if (this.output.getParent() instanceof MMOItemUIFilter) {
         MMOItemTemplate var8 = MMOItems.plugin.getTemplates().getTemplate(MMOItems.plugin.getTypes().get(this.output.getArgument()), this.output.getData());
         if (var8 == null) {
            throw new IllegalArgumentException(
               FriendlyFeedbackProvider.quickForConsole(
                  FFPMMOItems.get(),
                  "This should be impossible, please contact $egunging$b: $fThe ProvidedUIFilter was flagged as 'valid' but clearly is not. $enet.Indyuce.mmoitems.api.crafting.recipe$b. ",
                  new String[0]
               )
            );
         }

         this.identifiedMMO = new ConfigMMOItem(var8, this.output.getAmount(1));
      }
   }

   public double getCraftingTime() {
      return this.craftingTime;
   }

   public boolean isInstant() {
      return this.craftingTime <= 0.0;
   }

   @NotNull
   public ProvidedUIFilter getOutput() {
      return this.output;
   }

   @NotNull
   public ItemStack getOutputItemStack(@Nullable RPGPlayer var1) {
      return this.identifiedMMO != null && var1 != null ? this.identifiedMMO.generate(var1) : this.output.getItemStack(null);
   }

   @NotNull
   public ItemStack getPreviewItemStack() {
      if (this.identifiedMMO != null) {
         return this.identifiedMMO.getPreview();
      }

      ItemStack var1 = this.output.getParent().getDisplayStack(this.output.getArgument(), this.output.getData(), null);
      var1.setAmount(this.output.getAmount(1));
      ItemMeta var2 = var1.getItemMeta();
      if (var2 != null) {
         var2.setDisplayName(SilentNumbers.getItemName(var1, false) + "§ʫ");
         var1.setItemMeta(var2);
      }

      return var1;
   }

   public int getOutputAmount() {
      return this.output.getAmount(1);
   }

   @Override
   public boolean whenUsed(PlayerData var1, IngredientInventory var2, CheckedRecipe var3, CraftingStation var4) {
      if (!var1.isOnline()) {
         return false;
      }

      if (this.isInstant()) {
         ItemStack var7 = this.hasOption(Recipe.RecipeOption.OUTPUT_ITEM) ? this.getOutputItemStack(var1.getRPG()) : null;
         PlayerUseCraftingStationEvent var6 = new PlayerUseCraftingStationEvent(var1, var4, var3, var7);
         Bukkit.getPluginManager().callEvent(var6);
         if (var6.isCancelled()) {
            return false;
         }

         var3.getRecipe().whenClaimed().forEach(var1x -> var1x.whenCrafting(var1));
         if (var7 != null) {
            new SmartGive(var1.getPlayer()).give(new ItemStack[]{var7});
         }

         if (!this.hasOption(Recipe.RecipeOption.SILENT_CRAFT)) {
            var1.getPlayer().playSound(var1.getPlayer().getLocation(), var4.getSound(), 1.0F, 1.0F);
         }

         return true;
      } else {
         PlayerUseCraftingStationEvent var5 = new PlayerUseCraftingStationEvent(var1, var4, var3);
         Bukkit.getPluginManager().callEvent(var5);
         if (var5.isCancelled()) {
            return false;
         }

         if (!this.hasOption(Recipe.RecipeOption.SILENT_CRAFT)) {
            var1.getPlayer().playSound(var1.getPlayer().getLocation(), var4.getSound(), 1.0F, 1.0F);
         }

         var1.getCrafting().getQueue(var4).add(this);
         return true;
      }
   }

   @Override
   public boolean canUse(PlayerData var1, IngredientInventory var2, CheckedRecipe var3, CraftingStation var4) {
      if (this.isInstant()) {
         return true;
      }

      CraftingStatus.CraftingQueue var5 = var1.getCrafting().getQueue(var4);
      if (var5.isFull(var4)) {
         if (!var1.isOnline()) {
            return false;
         }

         Message.CRAFTING_QUEUE_FULL.format(ChatColor.RED).send(var1.getPlayer());
         var1.getPlayer().playSound(var1.getPlayer().getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0F, 1.0F);
         return false;
      } else {
         return true;
      }
   }

   @Override
   public ItemStack display(CheckedRecipe var1) {
      return ConfigItems.CRAFTING_RECIPE_DISPLAY.newBuilder(var1).build();
   }

   @Override
   public CheckedRecipe evaluateRecipe(PlayerData var1, IngredientInventory var2) {
      return new CheckedRecipe(this, var1, var2);
   }
}
