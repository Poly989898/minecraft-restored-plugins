package net.Indyuce.mmoitems.gui.edition.recipe.gui;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.api.crafting.uifilters.VanillaUIFilter;
import io.lumine.mythic.lib.api.crafting.uimanager.ProvidedUIFilter;
import io.lumine.mythic.lib.api.crafting.uimanager.UIFilterManager;
import io.lumine.mythic.lib.api.util.ItemFactory;
import io.lumine.mythic.lib.api.util.ui.FriendlyFeedbackCategory;
import io.lumine.mythic.lib.api.util.ui.FriendlyFeedbackProvider;
import io.lumine.mythic.lib.api.util.ui.QuickNumberRange;
import io.lumine.mythic.lib.api.util.ui.SilentNumbers;
import io.lumine.mythic.lib.util.AdventureUtils;
import io.lumine.mythic.lib.version.VersionUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import net.Indyuce.mmoitems.ItemStats;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.edition.StatEdition;
import net.Indyuce.mmoitems.api.item.template.MMOItemTemplate;
import net.Indyuce.mmoitems.api.util.message.FFPMMOItems;
import net.Indyuce.mmoitems.gui.edition.EditionInventory;
import net.Indyuce.mmoitems.gui.edition.recipe.button.RBA_AmountOutput;
import net.Indyuce.mmoitems.gui.edition.recipe.button.RecipeButtonAction;
import net.Indyuce.mmoitems.gui.edition.recipe.interpreter.RMG_RecipeInterpreter;
import net.Indyuce.mmoitems.gui.edition.recipe.registry.RecipeRegistry;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class RecipeEditorGUI extends EditionInventory {
   @NotNull
   final ItemStack nextButtonPage = ItemFactory.of(Material.SPECTRAL_ARROW).name("§eMore Options §c»").build();
   @NotNull
   final ItemStack prevButtonPage = ItemFactory.of(Material.SPECTRAL_ARROW).name("§c« §eMore Options").build();
   @NotNull
   public final ItemStack noButton = ItemFactory.of(Material.IRON_BARS).name("§8---").build();
   @NotNull
   public final ItemStack emptySlot = ItemFactory.of(Material.BARRIER).name("§7No Item").build();
   @NotNull
   public final ItemStack airSlot = ItemFactory.of(Material.STRUCTURE_VOID).name("§7No Item").build();
   @NotNull
   final ConfigurationSection craftingSection;
   @NotNull
   final ConfigurationSection typeSection;
   @NotNull
   final ConfigurationSection nameSection;
   @NotNull
   final RecipeRegistry recipeRegistry;
   @NotNull
   final RBA_AmountOutput amountButton;
   @NotNull
   final String recipeName;
   int buttonsPage;
   @NotNull
   final HashMap<Integer, RecipeButtonAction> buttonsMap = new HashMap<>();
   @NotNull
   final ArrayList<RecipeButtonAction> buttons = new ArrayList<>();
   @NotNull
   public final String[] recipeLog = new String[]{
      FriendlyFeedbackProvider.quickForPlayer(FFPMMOItems.get(), "Write in the chat the item you want, follow any format:", new String[0]),
      FriendlyFeedbackProvider.quickForPlayer(FFPMMOItems.get(), "Vanilla: $e[MATERIAL] [AMOUNT] $bex $eDIAMOND 2..", new String[0]),
      FriendlyFeedbackProvider.quickForPlayer(FFPMMOItems.get(), "MMOItem: $e[TYPE].[ID] [AMOUNT] $bex $eSWORD.CUTLASS 1..", new String[0]),
      FriendlyFeedbackProvider.quickForPlayer(FFPMMOItems.get(), "Other: $e[KEY] [ARG] [DAT] [AMOUNT]$b (check wiki)", new String[0]),
      FriendlyFeedbackProvider.quickForPlayer(
         FFPMMOItems.get(), "§8Amount is in the range format, $e[min]..[max]§8, assumed to be $r1..§8 if unspecified.", new String[0]
      )
   };
   @NotNull
   static final HashMap<UUID, Boolean> showingInput = new HashMap<>();
   public static final int INPUT = 0;
   public static final int OUTPUT = 1;
   public static final int PRIMARY = 2;
   public static final int SECONDARY = 3;
   public static final String INPUT_INGREDIENTS = "input";
   public static final String OUTPUT_INGREDIENTS = "output";
   public static final ProvidedUIFilter AIR = new ProvidedUIFilter(new VanillaUIFilter(), "AIR", "0");

   public RecipeEditorGUI(@NotNull Player player, @NotNull MMOItemTemplate mmoItemTemplate, @NotNull String text, @NotNull RecipeRegistry recipeRegistry) {
      super(player, mmoItemTemplate);
      this.recipeName = text;
      this.recipeRegistry = recipeRegistry;
      this.moveInput();
      this.craftingSection = getSection(this.getEditedSection(), "crafting");
      this.typeSection = getSection(this.craftingSection, this.getRecipeRegistry().getRecipeConfigPath());
      this.nameSection = getSection(this.typeSection, text);
      this.addButton(this.amountButton = new RBA_AmountOutput(this, this.getCachedItem().clone()));
   }

   @Override
   public String getName() {
      return "Edit " + this.getRecipeRegistry().getRecipeTypeName() + " Recipe";
   }

   @NotNull
   public ConfigurationSection getCraftingSection() {
      return this.craftingSection;
   }

   @NotNull
   public ConfigurationSection getTypeSection() {
      return this.typeSection;
   }

   @NotNull
   public ConfigurationSection getNameSection() {
      return this.nameSection;
   }

   @NotNull
   public RecipeRegistry getRecipeRegistry() {
      return this.recipeRegistry;
   }

   @NotNull
   public RBA_AmountOutput getAmountButton() {
      return this.amountButton;
   }

   @NotNull
   public String getRecipeName() {
      return this.recipeName;
   }

   public void putButtons() {
      if (this.getButtonsRow() >= 0) {
         this.buttonsMap.clear();
         if (this.buttonsPage > 0) {
            this.inventory.setItem(this.getButtonsRow() * 9 + 8, this.prevButtonPage);
         }

         if (this.buttonsMap.size() >= (this.buttonsPage + 1) * 7) {
            this.inventory.setItem(this.getButtonsRow() * 9, this.nextButtonPage);
         }

         for (int i = 7 * this.buttonsPage; i < 7 * (this.buttonsPage + 1); i++) {
            int count = this.buttonRowPageClamp(i);
            if (i >= this.buttons.size()) {
               this.inventory.setItem(count, this.noButton);
            } else {
               RecipeButtonAction recipeButtonAction = this.buttons.get(i);
               this.inventory.setItem(count, recipeButtonAction.getButton());
               this.buttonsMap.put(count, recipeButtonAction);
            }
         }
      }
   }

   public int buttonRowPageClamp(int count) {
      int rowIndex = SilentNumbers.floor(count / 7.0);
      count -= rowIndex * 7;
      int baseSlot = this.getButtonsRow() * 9;
      int columnOffset = count + 1;
      return baseSlot + columnOffset;
   }

   public abstract int getButtonsRow();

   abstract int getInputSlot(int count);

   @Override
   public void arrangeInventory() {
      this.putButtons();
      this.putRecipe();
   }

   public abstract void putRecipe();

   @NotNull
   public ItemStack getDisplay(boolean flag, int count) {
      ProvidedUIFilter providedUIFilter = flag ? this.getInterpreter().getInput(count) : this.getInterpreter().getOutput(count);
      if (providedUIFilter != null && !providedUIFilter.isAir()) {
         return providedUIFilter.getDisplayStack(null);
      } else {
         return this.isShowingInput() ? this.emptySlot : this.airSlot;
      }
   }

   public void addButton(@NotNull RecipeButtonAction recipeButtonAction) {
      this.buttons.add(recipeButtonAction);
   }

   @NotNull
   public abstract RMG_RecipeInterpreter getInterpreter();

   @Override
   public void whenClicked(InventoryClickEvent event) {
      if (VersionUtils.getView(event).getTopInventory() == event.getClickedInventory()) {
         event.setCancelled(true);
         int inputSlot = this.getInputSlot(event.getRawSlot());
         if (event.getAction() == InventoryAction.PICKUP_ALL) {
            if (inputSlot >= 0) {
               if (this.isShowingInput()) {
                  new StatEdition(this, ItemStats.CRAFTING, 0, this.getInterpreter(), inputSlot).enable(this.recipeLog);
               } else {
                  new StatEdition(this, ItemStats.CRAFTING, 1, this.getInterpreter(), inputSlot).enable(this.recipeLog);
               }
            } else {
               RecipeButtonAction recipeButtonAction = this.buttonsMap.get(event.getRawSlot());
               if (recipeButtonAction != null) {
                  recipeButtonAction.runPrimary();
               }
            }
         } else if (event.getAction() == InventoryAction.PICKUP_HALF) {
            if (inputSlot >= 0) {
               if (this.isShowingInput()) {
                  this.getInterpreter().deleteInput(inputSlot);
               } else {
                  this.getInterpreter().deleteOutput(this.getInputSlot(event.getRawSlot()));
               }

               this.registerTemplateEdition();
               this.refreshInventory();
            } else {
               RecipeButtonAction recipeButtonAction = this.buttonsMap.get(event.getRawSlot());
               if (recipeButtonAction != null) {
                  recipeButtonAction.runSecondary();
               }
            }
         }
      }
   }

   public static void switchInputFor(@NotNull UUID uuid) {
      showingInput.put(uuid, !isShowingInputFor(uuid));
   }

   public static boolean isShowingInputFor(@NotNull UUID uuid) {
      return showingInput.getOrDefault(uuid, true);
   }

   public boolean isShowingInput() {
      return isShowingInputFor(this.getPlayer().getUniqueId());
   }

   public void switchInput() {
      switchInputFor(this.getPlayer().getUniqueId());
   }

   @NotNull
   public static ItemStack rename(@NotNull ItemStack item, @NotNull String text) {
      ItemMeta itemMeta = item.getItemMeta();
      itemMeta.setDisplayName(MythicLib.plugin.parseColors(text));
      item.setItemMeta(itemMeta);
      return item;
   }

   @NotNull
   public static ItemStack addLore(@NotNull ItemStack item, @NotNull ArrayList<String> texts) {
      if (!item.hasItemMeta()) {
         return item;
      }

      ItemMeta itemMeta = item.getItemMeta();
      if (itemMeta == null) {
         return item;
      }

      List lore = itemMeta.getLore();
      if (lore == null) {
         lore = new ArrayList();
      }

      lore.addAll(texts);
      AdventureUtils.setLore(itemMeta, lore);
      item.setItemMeta(itemMeta);
      return item;
   }

   @NotNull
   public static ConfigurationSection getSection(@NotNull ConfigurationSection section, @NotNull String text) {
      ConfigurationSection childSection = section.getConfigurationSection(text);
      if (childSection == null) {
         childSection = section.createSection(text);
      }

      return childSection;
   }

   public void moveInput() {
      ConfigurationSection section = getSection(this.getEditedSection(), "crafting");
      ConfigurationSection registrySection = getSection(section, this.getRecipeRegistry().getRecipeConfigPath());
      moveInput(registrySection, this.recipeName);
   }

   public static void tripleDebug(@NotNull ConfigurationSection section) {
      MMOItems.print(null, "§d-§7 Section §5" + section.getCurrentPath(), null);

      for (String key : section.getKeys(false)) {
         MMOItems.print(null, "§d +§7 " + key, null);
         MMOItems.print(null, "§d-§e-§7 As List §5" + section.getCurrentPath() + "." + key + "§7 {§d" + section.getStringList(key).size() + "§7}", null);

         for (String text : section.getStringList(key)) {
            MMOItems.print(null, "§d +§e-§7" + text, null);
         }

         ConfigurationSection recipeTypeSection = getSection(section, key);
         MMOItems.print(null, "§8--§d-§7 Section §5" + recipeTypeSection.getCurrentPath(), null);

         for (String recipeName : recipeTypeSection.getKeys(false)) {
            MMOItems.print(null, "§8--§d +§7 " + recipeName, null);
            MMOItems.print(null, "§8--§d-§e-§7 As List §5" + recipeTypeSection.getCurrentPath() + "." + recipeName + "§7 {§d" + recipeTypeSection.getStringList(recipeName).size() + "§7}", null);

            for (String text : recipeTypeSection.getStringList(recipeName)) {
               MMOItems.print(null, "§8--§d +§e-§7" + text, null);
            }

            ConfigurationSection recipeSection = getSection(recipeTypeSection, recipeName);
            MMOItems.print(null, "§0--§8--§d-§7 Section §5" + recipeSection.getCurrentPath(), null);

            for (String field : recipeSection.getKeys(false)) {
               MMOItems.print(null, "§0--§8--§d +§7 " + field, null);
               MMOItems.print(
                  null, "§0--§8--§d-§e-§7 As List §5" + recipeSection.getCurrentPath() + "." + field + "§7 {§d" + recipeSection.getStringList(field).size() + "§7}", null
               );

               for (String text : recipeSection.getStringList(field)) {
                  MMOItems.print(null, "§0--§8--§d +§e-§7" + text, null);
               }
            }
         }
      }
   }

   public static ConfigurationSection moveInput(@NotNull ConfigurationSection section, @NotNull String text) {
      ConfigurationSection recipeSection;
      if (section.isConfigurationSection(text)) {
         recipeSection = getSection(section, text);
         String firstInput = recipeSection.getString("input1");
         String secondInput = recipeSection.getString("input2");
         if (firstInput != null && secondInput != null) {
            recipeSection.set("input1", null);
            recipeSection.set("input2", null);
            recipeSection.set("input", poofFromLegacy(firstInput) + "|" + poofFromLegacy(secondInput));
            recipeSection.set("output", "v AIR 0|v AIR 0");
         }
      } else {
         List list = section.getStringList(text);
         section.set(text, null);
         recipeSection = getSection(section, text);
         recipeSection.set("input", list);
      }

      return recipeSection;
   }

   @NotNull
   public static String poofFromLegacy(@Nullable String text) {
      if (text == null || "[]".equals(text)) {
         return "v AIR - 1..";
      }

      if (text.contains(" ")) {
         return text;
      }

      int count = text.indexOf(58);
      QuickNumberRange quickNumberRange = new QuickNumberRange(1.0, null);
      if (count > 0) {
         String remainder = text.substring(count + 1);
         text = text.substring(0, count);
         Integer value = SilentNumbers.IntegerParse(remainder);
         if (value == null) {
            value = 1;
         }

         quickNumberRange = new QuickNumberRange((double)value.intValue(), null);
      }

      if (text.contains(".")) {
         String[] texts = text.split("\\.");
         return "m " + texts[0] + " " + texts[1] + " " + quickNumberRange;
      } else {
         return "v " + text + " - " + quickNumberRange;
      }
   }

   @NotNull
   public static ProvidedUIFilter readIngredientFrom(@NotNull String text, @NotNull FriendlyFeedbackProvider friendlyFeedbackProvider) {
      Material material = null;

      try {
         material = Material.valueOf(text.toUpperCase().replace(" ", "_").replace("-", "_"));
      } catch (IllegalArgumentException exception) {
      }

      if (material != null) {
         if (material.isAir()) {
            ProvidedUIFilter providedUIFilter = new ProvidedUIFilter(VanillaUIFilter.get(), "AIR", "0");
            providedUIFilter.setAmountRange(new QuickNumberRange(null, null));
            return providedUIFilter;
         }

         if (!material.isItem()) {
            throw new IllegalArgumentException("Invalid Ingredient $u" + text + "$b ($fNot an Item$b).");
         }

         ProvidedUIFilter uIFilter = UIFilterManager.getUIFilter("v", material.toString(), "", "1..", friendlyFeedbackProvider);
         if (uIFilter != null) {
            return uIFilter;
         }

         friendlyFeedbackProvider.sendTo(FriendlyFeedbackCategory.ERROR, MMOItems.getConsole());
         friendlyFeedbackProvider.sendTo(FriendlyFeedbackCategory.FAILURE, MMOItems.getConsole());
         throw new IllegalArgumentException("Invalid Ingredient $u" + text);
      } else {
         if (text.contains(".") && !text.contains(" ")) {
            String[] texts = text.split("\\.");
            if (texts.length == 2) {
               String firstPart = texts[0];
               String secondPart = texts[1];
               ProvidedUIFilter uIFilter = UIFilterManager.getUIFilter("m", firstPart, secondPart, "1..", friendlyFeedbackProvider);
               if (uIFilter != null) {
                  return uIFilter;
               }

               friendlyFeedbackProvider.sendAllTo((org.bukkit.command.CommandSender) MMOItems.getConsole());
               throw new IllegalArgumentException("Invalid Ingredient $u" + text);
            }
         }

         ProvidedUIFilter uIFilter = UIFilterManager.getUIFilter(text, friendlyFeedbackProvider);
         if (uIFilter != null) {
            return uIFilter;
         }

         friendlyFeedbackProvider.sendAllTo((org.bukkit.command.CommandSender) MMOItems.getConsole());
         throw new IllegalArgumentException("Invalid Ingredient $u" + text);
      }
   }
}
