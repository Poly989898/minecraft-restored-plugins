package io.lumine.mythic.lib.api.crafting.outputs;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.api.crafting.event.MythicCraftItemEvent;
import io.lumine.mythic.lib.api.crafting.ingredients.MythicBlueprintInventory;
import io.lumine.mythic.lib.api.crafting.ingredients.MythicIngredient;
import io.lumine.mythic.lib.api.crafting.ingredients.MythicRecipeInventory;
import io.lumine.mythic.lib.api.crafting.recipes.MythicCachedResult;
import io.lumine.mythic.lib.api.crafting.recipes.vmp.VanillaInventoryMapping;
import io.lumine.mythic.lib.api.crafting.uimanager.ProvidedUIFilter;
import io.lumine.mythic.lib.api.placeholders.MythicPlaceholders;
import io.lumine.mythic.lib.api.util.ui.FFPMythicLib;
import io.lumine.mythic.lib.api.util.ui.FriendlyFeedbackProvider;
import io.lumine.mythic.lib.api.util.ui.SilentNumbers;
import java.util.ArrayList;
import java.util.Objects;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MROCommand extends MythicRecipeOutput {
   @Nullable
   final String displayedName;
   @Nullable
   final ArrayList<String> description;
   @NotNull
   final MythicIngredient display;
   @NotNull
   final ArrayList<String> commands;
   @Nullable
   ItemStack displayItem;

   @Nullable
   public String getDisplayedName() {
      return this.displayedName;
   }

   @Nullable
   public ArrayList<String> getDescription() {
      return this.description;
   }

   @NotNull
   public MythicIngredient getDisplay() {
      return this.display;
   }

   @NotNull
   public ArrayList<String> getCommands() {
      return this.commands;
   }

   @Nullable
   public ItemStack getDisplayItem() {
      return this.displayItem;
   }

   public MROCommand(@Nullable String displayedName, @Nullable String description, @NotNull MythicIngredient display, @NotNull String... commands) throws IllegalArgumentException {
      this.displayedName = displayedName;
      if (description != null) {
         this.description = SilentNumbers.chop(description, 50, "§e§o");
      } else {
         this.description = null;
      }

      this.display = display;
      this.commands = SilentNumbers.addAll(null, commands);
      if (display.getRandomSubstituteItem(null) == null) {
         throw new IllegalArgumentException(
            FriendlyFeedbackProvider.quickForConsole(
               FFPMythicLib.get(), "Could not generate $fCommand Recipe Result$b: You must specify a valid UIFilter that generates an Item Stack!"
            )
         );
      }

      if (display.getSubstitutes().size() == 1) {
         ProvidedUIFilter sub = display.getRandomSubstitute(true);
         if (sub != null) {
            ItemStack gen = sub.getDisplayStack(null);
            ItemMeta gMeta = Objects.requireNonNull(gen.getItemMeta());
            if (displayedName != null) {
               gMeta.setDisplayName(MythicLib.plugin.parseColors(displayedName));
            }

            if (description != null) {
               gMeta.setLore(this.description);
            }

            gen.setItemMeta(gMeta);
            this.displayItem = gen;
         }
      }
   }

   @NotNull
   @Override
   public MythicRecipeInventory applyDisplay(
      @NotNull MythicBlueprintInventory inventory, @NotNull InventoryClickEvent eventTrigger, @NotNull VanillaInventoryMapping mapping
   ) {
      ItemStack git = this.getDisplayItem();
      if (git == null) {
         git = this.getDisplay().getRandomDisplayItem(null);
      }

      MythicRecipeInventory edited = inventory.getResultInventory().clone();
      edited.clean();
      edited.setItemAt(0, 0, git);
      return edited;
   }

   @Override
   public void applyResult(
      @NotNull MythicRecipeInventory resultInventory,
      @NotNull MythicBlueprintInventory otherInventories,
      @NotNull MythicCachedResult cache,
      @NotNull InventoryClickEvent eventTrigger,
      @NotNull VanillaInventoryMapping map,
      int times
   ) {
      eventTrigger.setCancelled(true);
      if (times <= 1 || eventTrigger.getWhoClicked() instanceof Player) {
         MythicCraftItemEvent preEvent = new MythicCraftItemEvent(eventTrigger, resultInventory, map, otherInventories, cache);
         Bukkit.getPluginManager().callEvent(preEvent);
         if (!preEvent.isCancelled()) {
            ArrayList<String> parsed = new ArrayList<>();

            for (String str : this.getCommands()) {
               if (str != null) {
                  parsed.add(MythicPlaceholders.parse(str, eventTrigger.getWhoClicked()));
               }
            }

            for (int t = 1; t <= times; t++) {
               for (String command : parsed) {
                  SilentNumbers.executeCommand(MythicLib.plugin.getServer().getConsoleSender(), command, null);
               }
            }

            this.consumeIngredients(otherInventories, cache, eventTrigger.getInventory(), map, times);
         }
      }
   }
}
