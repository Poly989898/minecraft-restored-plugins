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

   public RecipeEditorGUI(@NotNull Player var1, @NotNull MMOItemTemplate var2, @NotNull String var3, @NotNull RecipeRegistry var4) {
      super(var1, var2);
      this.recipeName = var3;
      this.recipeRegistry = var4;
      this.moveInput();
      this.craftingSection = getSection(this.getEditedSection(), "crafting");
      this.typeSection = getSection(this.craftingSection, this.getRecipeRegistry().getRecipeConfigPath());
      this.nameSection = getSection(this.typeSection, var3);
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

         for (int var1 = 7 * this.buttonsPage; var1 < 7 * (this.buttonsPage + 1); var1++) {
            int var2 = this.buttonRowPageClamp(var1);
            if (var1 >= this.buttons.size()) {
               this.inventory.setItem(var2, this.noButton);
            } else {
               RecipeButtonAction var3 = this.buttons.get(var1);
               this.inventory.setItem(var2, var3.getButton());
               this.buttonsMap.put(var2, var3);
            }
         }
      }
   }

   public int buttonRowPageClamp(int var1) {
      int var2 = SilentNumbers.floor(var1 / 7.0);
      var1 -= var2 * 7;
      int var3 = this.getButtonsRow() * 9;
      int var4 = var1 + 1;
      return var3 + var4;
   }

   public abstract int getButtonsRow();

   abstract int getInputSlot(int var1);

   @Override
   public void arrangeInventory() {
      this.putButtons();
      this.putRecipe();
   }

   public abstract void putRecipe();

   @NotNull
   public ItemStack getDisplay(boolean var1, int var2) {
      ProvidedUIFilter var3 = var1 ? this.getInterpreter().getInput(var2) : this.getInterpreter().getOutput(var2);
      if (var3 != null && !var3.isAir()) {
         return var3.getDisplayStack(null);
      } else {
         return this.isShowingInput() ? this.emptySlot : this.airSlot;
      }
   }

   public void addButton(@NotNull RecipeButtonAction var1) {
      this.buttons.add(var1);
   }

   @NotNull
   public abstract RMG_RecipeInterpreter getInterpreter();

   @Override
   public void whenClicked(InventoryClickEvent var1) {
      if (VersionUtils.getView(var1).getTopInventory() == var1.getClickedInventory()) {
         var1.setCancelled(true);
         int var2 = this.getInputSlot(var1.getRawSlot());
         if (var1.getAction() == InventoryAction.PICKUP_ALL) {
            if (var2 >= 0) {
               if (this.isShowingInput()) {
                  new StatEdition(this, ItemStats.CRAFTING, 0, this.getInterpreter(), var2).enable(this.recipeLog);
               } else {
                  new StatEdition(this, ItemStats.CRAFTING, 1, this.getInterpreter(), var2).enable(this.recipeLog);
               }
            } else {
               RecipeButtonAction var3 = this.buttonsMap.get(var1.getRawSlot());
               if (var3 != null) {
                  var3.runPrimary();
               }
            }
         } else if (var1.getAction() == InventoryAction.PICKUP_HALF) {
            if (var2 >= 0) {
               if (this.isShowingInput()) {
                  this.getInterpreter().deleteInput(var2);
               } else {
                  this.getInterpreter().deleteOutput(this.getInputSlot(var1.getRawSlot()));
               }

               this.registerTemplateEdition();
               this.refreshInventory();
            } else {
               RecipeButtonAction var4 = this.buttonsMap.get(var1.getRawSlot());
               if (var4 != null) {
                  var4.runSecondary();
               }
            }
         }
      }
   }

   public static void switchInputFor(@NotNull UUID var0) {
      showingInput.put(var0, !isShowingInputFor(var0));
   }

   public static boolean isShowingInputFor(@NotNull UUID var0) {
      return showingInput.getOrDefault(var0, true);
   }

   public boolean isShowingInput() {
      return isShowingInputFor(this.getPlayer().getUniqueId());
   }

   public void switchInput() {
      switchInputFor(this.getPlayer().getUniqueId());
   }

   @NotNull
   public static ItemStack rename(@NotNull ItemStack var0, @NotNull String var1) {
      ItemMeta var2 = var0.getItemMeta();
      var2.setDisplayName(MythicLib.plugin.parseColors(var1));
      var0.setItemMeta(var2);
      return var0;
   }

   @NotNull
   public static ItemStack addLore(@NotNull ItemStack var0, @NotNull ArrayList<String> var1) {
      if (!var0.hasItemMeta()) {
         return var0;
      }

      ItemMeta var2 = var0.getItemMeta();
      if (var2 == null) {
         return var0;
      }

      List var3 = var2.getLore();
      if (var3 == null) {
         var3 = new ArrayList();
      }

      var3.addAll(var1);
      AdventureUtils.setLore(var2, var3);
      var0.setItemMeta(var2);
      return var0;
   }

   @NotNull
   public static ConfigurationSection getSection(@NotNull ConfigurationSection var0, @NotNull String var1) {
      ConfigurationSection var2 = var0.getConfigurationSection(var1);
      if (var2 == null) {
         var2 = var0.createSection(var1);
      }

      return var2;
   }

   public void moveInput() {
      ConfigurationSection var1 = getSection(this.getEditedSection(), "crafting");
      ConfigurationSection var2 = getSection(var1, this.getRecipeRegistry().getRecipeConfigPath());
      moveInput(var2, this.recipeName);
   }

   public static void tripleDebug(@NotNull ConfigurationSection var0) {
      MMOItems.print(null, "§d-§7 Section §5" + var0.getCurrentPath(), null);

      for (String var2 : var0.getKeys(false)) {
         MMOItems.print(null, "§d +§7 " + var2, null);
         MMOItems.print(null, "§d-§e-§7 As List §5" + var0.getCurrentPath() + "." + var2 + "§7 {§d" + var0.getStringList(var2).size() + "§7}", null);

         for (String var4 : var0.getStringList(var2)) {
            MMOItems.print(null, "§d +§e-§7" + var4, null);
         }

         ConfigurationSection var11 = getSection(var0, var2);
         MMOItems.print(null, "§8--§d-§7 Section §5" + var11.getCurrentPath(), null);

         for (String var5 : var11.getKeys(false)) {
            MMOItems.print(null, "§8--§d +§7 " + var5, null);
            MMOItems.print(null, "§8--§d-§e-§7 As List §5" + var11.getCurrentPath() + "." + var5 + "§7 {§d" + var11.getStringList(var5).size() + "§7}", null);

            for (String var7 : var11.getStringList(var5)) {
               MMOItems.print(null, "§8--§d +§e-§7" + var7, null);
            }

            ConfigurationSection var13 = getSection(var11, var5);
            MMOItems.print(null, "§0--§8--§d-§7 Section §5" + var13.getCurrentPath(), null);

            for (String var8 : var13.getKeys(false)) {
               MMOItems.print(null, "§0--§8--§d +§7 " + var8, null);
               MMOItems.print(
                  null, "§0--§8--§d-§e-§7 As List §5" + var13.getCurrentPath() + "." + var8 + "§7 {§d" + var13.getStringList(var8).size() + "§7}", null
               );

               for (String var10 : var13.getStringList(var8)) {
                  MMOItems.print(null, "§0--§8--§d +§e-§7" + var10, null);
               }
            }
         }
      }
   }

   public static ConfigurationSection moveInput(@NotNull ConfigurationSection var0, @NotNull String var1) {
      ConfigurationSection var2;
      if (var0.isConfigurationSection(var1)) {
         var2 = getSection(var0, var1);
         String var3 = var2.getString("input1");
         String var4 = var2.getString("input2");
         if (var3 != null && var4 != null) {
            var2.set("input1", null);
            var2.set("input2", null);
            var2.set("input", poofFromLegacy(var3) + "|" + poofFromLegacy(var4));
            var2.set("output", "v AIR 0|v AIR 0");
         }
      } else {
         List var5 = var0.getStringList(var1);
         var0.set(var1, null);
         var2 = getSection(var0, var1);
         var2.set("input", var5);
      }

      return var2;
   }

   @NotNull
   public static String poofFromLegacy(@Nullable String var0) {
      if (var0 == null || "[]".equals(var0)) {
         return "v AIR - 1..";
      }

      if (var0.contains(" ")) {
         return var0;
      }

      int var1 = var0.indexOf(58);
      QuickNumberRange var2 = new QuickNumberRange(1.0, null);
      if (var1 > 0) {
         String var3 = var0.substring(var1 + 1);
         var0 = var0.substring(0, var1);
         Integer var4 = SilentNumbers.IntegerParse(var3);
         if (var4 == null) {
            var4 = 1;
         }

         var2 = new QuickNumberRange((double)var4.intValue(), null);
      }

      if (var0.contains(".")) {
         String[] var5 = var0.split("\\.");
         return "m " + var5[0] + " " + var5[1] + " " + var2;
      } else {
         return "v " + var0 + " - " + var2;
      }
   }

   @NotNull
   public static ProvidedUIFilter readIngredientFrom(@NotNull String var0, @NotNull FriendlyFeedbackProvider var1) {
      Material var2 = null;

      try {
         var2 = Material.valueOf(var0.toUpperCase().replace(" ", "_").replace("-", "_"));
      } catch (IllegalArgumentException var7) {
      }

      if (var2 != null) {
         if (var2.isAir()) {
            ProvidedUIFilter var10 = new ProvidedUIFilter(VanillaUIFilter.get(), "AIR", "0");
            var10.setAmountRange(new QuickNumberRange(null, null));
            return var10;
         }

         if (!var2.isItem()) {
            throw new IllegalArgumentException("Invalid Ingredient $u" + var0 + "$b ($fNot an Item$b).");
         }

         ProvidedUIFilter var9 = UIFilterManager.getUIFilter("v", var2.toString(), "", "1..", var1);
         if (var9 != null) {
            return var9;
         }

         var1.sendTo(FriendlyFeedbackCategory.ERROR, MMOItems.getConsole());
         var1.sendTo(FriendlyFeedbackCategory.FAILURE, MMOItems.getConsole());
         throw new IllegalArgumentException("Invalid Ingredient $u" + var0);
      } else {
         if (var0.contains(".") && !var0.contains(" ")) {
            String[] var3 = var0.split("\\.");
            if (var3.length == 2) {
               String var4 = var3[0];
               String var5 = var3[1];
               ProvidedUIFilter var6 = UIFilterManager.getUIFilter("m", var4, var5, "1..", var1);
               if (var6 != null) {
                  return var6;
               }

               var1.sendAllTo((org.bukkit.command.CommandSender) MMOItems.getConsole());
               throw new IllegalArgumentException("Invalid Ingredient $u" + var0);
            }
         }

         ProvidedUIFilter var8 = UIFilterManager.getUIFilter(var0, var1);
         if (var8 != null) {
            return var8;
         }

         var1.sendAllTo((org.bukkit.command.CommandSender) MMOItems.getConsole());
         throw new IllegalArgumentException("Invalid Ingredient $u" + var0);
      }
   }
}
