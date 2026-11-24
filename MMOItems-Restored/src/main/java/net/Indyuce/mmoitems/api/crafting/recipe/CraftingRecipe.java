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

   public CraftingRecipe(@NotNull ConfigurationSection section) {
      super(section);
      this.craftingTime = section.getDouble("crafting-time");
      String outputItem = section.getString("output.item", "N/A");
      String text = section.getString("output.type", "N/A").toUpperCase().replace("-", "_").replace(" ", "_");
      String text2 = section.getString("output.id", "N/A").toUpperCase().replace("-", "_").replace(" ", "_");
      FriendlyFeedbackProvider friendlyFeedbackProvider = new FriendlyFeedbackProvider(FFPMMOItems.get());
      if (!"N/A".equals(text) && !"N/A".equals(text2)) {
         ProvidedUIFilter uIFilter = UIFilterManager.getUIFilter("m", text, text2, section.getString("output.amount", "1"), friendlyFeedbackProvider);
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

         this.output = uIFilter;
      } else {
         if ("N/A".equals(outputItem)) {
            throw new IllegalArgumentException(
               FriendlyFeedbackProvider.quickForConsole(FFPMMOItems.get(), "Config must contain a valid Type and ID, or a valid UIFilter. ", new String[0])
            );
         }

         ProvidedUIFilter uIFilter = UIFilterManager.getUIFilter(outputItem, friendlyFeedbackProvider);
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

         this.output = uIFilter;
      }

      if (!this.output.isValid(friendlyFeedbackProvider)) {
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

      if (this.output.getItemStack(friendlyFeedbackProvider) == null) {
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

      if (this.output.getParent() instanceof MMOItemUIFilter) {
         MMOItemTemplate template = MMOItems.plugin.getTemplates().getTemplate(MMOItems.plugin.getTypes().get(this.output.getArgument()), this.output.getData());
         if (template == null) {
            throw new IllegalArgumentException(
               FriendlyFeedbackProvider.quickForConsole(
                  FFPMMOItems.get(),
                  "This should be impossible, please contact $egunging$b: $fThe ProvidedUIFilter was flagged as 'valid' but clearly is not. $enet.Indyuce.mmoitems.api.crafting.recipe$b. ",
                  new String[0]
               )
            );
         }

         this.identifiedMMO = new ConfigMMOItem(template, this.output.getAmount(1));
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
   public ItemStack getOutputItemStack(@Nullable RPGPlayer rpgPlayer) {
      return this.identifiedMMO != null && rpgPlayer != null ? this.identifiedMMO.generate(rpgPlayer) : this.output.getItemStack(null);
   }

   @NotNull
   public ItemStack getPreviewItemStack() {
      if (this.identifiedMMO != null) {
         return this.identifiedMMO.getPreview();
      }

      ItemStack displayStack = this.output.getParent().getDisplayStack(this.output.getArgument(), this.output.getData(), null);
      displayStack.setAmount(this.output.getAmount(1));
      ItemMeta itemMeta = displayStack.getItemMeta();
      if (itemMeta != null) {
         itemMeta.setDisplayName(SilentNumbers.getItemName(displayStack, false) + "§ʫ");
         displayStack.setItemMeta(itemMeta);
      }

      return displayStack;
   }

   public int getOutputAmount() {
      return this.output.getAmount(1);
   }

   @Override
   public boolean whenUsed(PlayerData playerData, IngredientInventory ingredientInventory, CheckedRecipe checkedRecipe, CraftingStation craftingStation) {
      if (!playerData.isOnline()) {
         return false;
      }

      if (this.isInstant()) {
         ItemStack item = this.hasOption(Recipe.RecipeOption.OUTPUT_ITEM) ? this.getOutputItemStack(playerData.getRPG()) : null;
         PlayerUseCraftingStationEvent event = new PlayerUseCraftingStationEvent(playerData, craftingStation, checkedRecipe, item);
         Bukkit.getPluginManager().callEvent(event);
         if (event.isCancelled()) {
            return false;
         }

         checkedRecipe.getRecipe().whenClaimed().forEach(var1x -> var1x.whenCrafting(playerData));
         if (item != null) {
            new SmartGive(playerData.getPlayer()).give(new ItemStack[]{item});
         }

         if (!this.hasOption(Recipe.RecipeOption.SILENT_CRAFT)) {
            playerData.getPlayer().playSound(playerData.getPlayer().getLocation(), craftingStation.getSound(), 1.0F, 1.0F);
         }

         return true;
      } else {
         PlayerUseCraftingStationEvent event = new PlayerUseCraftingStationEvent(playerData, craftingStation, checkedRecipe);
         Bukkit.getPluginManager().callEvent(event);
         if (event.isCancelled()) {
            return false;
         }

         if (!this.hasOption(Recipe.RecipeOption.SILENT_CRAFT)) {
            playerData.getPlayer().playSound(playerData.getPlayer().getLocation(), craftingStation.getSound(), 1.0F, 1.0F);
         }

         playerData.getCrafting().getQueue(craftingStation).add(this);
         return true;
      }
   }

   @Override
   public boolean canUse(PlayerData playerData, IngredientInventory ingredientInventory, CheckedRecipe checkedRecipe, CraftingStation craftingStation) {
      if (this.isInstant()) {
         return true;
      }

      CraftingStatus.CraftingQueue queue = playerData.getCrafting().getQueue(craftingStation);
      if (queue.isFull(craftingStation)) {
         if (!playerData.isOnline()) {
            return false;
         }

         Message.CRAFTING_QUEUE_FULL.format(ChatColor.RED).send(playerData.getPlayer());
         playerData.getPlayer().playSound(playerData.getPlayer().getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0F, 1.0F);
         return false;
      } else {
         return true;
      }
   }

   @Override
   public ItemStack display(CheckedRecipe checkedRecipe) {
      return ConfigItems.CRAFTING_RECIPE_DISPLAY.newBuilder(checkedRecipe).build();
   }

   @Override
   public CheckedRecipe evaluateRecipe(PlayerData playerData, IngredientInventory ingredientInventory) {
      return new CheckedRecipe(this, playerData, ingredientInventory);
   }
}
