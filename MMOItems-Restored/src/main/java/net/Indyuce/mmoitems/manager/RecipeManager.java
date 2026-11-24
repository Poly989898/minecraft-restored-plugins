package net.Indyuce.mmoitems.manager;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.api.crafting.recipes.MythicRecipeBlueprint;
import io.lumine.mythic.lib.api.crafting.uifilters.VanillaUIFilter;
import io.lumine.mythic.lib.api.crafting.uimanager.ProvidedUIFilter;
import io.lumine.mythic.lib.api.util.Ref;
import io.lumine.mythic.lib.api.util.ui.FriendlyFeedbackCategory;
import io.lumine.mythic.lib.api.util.ui.FriendlyFeedbackMessage;
import io.lumine.mythic.lib.api.util.ui.FriendlyFeedbackProvider;
import io.lumine.mythic.lib.api.util.ui.SilentNumbers;
import java.util.ArrayList;
import java.util.logging.Level;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.Type;
import net.Indyuce.mmoitems.api.crafting.MMOItemUIFilter;
import net.Indyuce.mmoitems.api.item.mmoitem.MMOItem;
import net.Indyuce.mmoitems.api.item.template.MMOItemTemplate;
import net.Indyuce.mmoitems.api.recipe.workbench.ingredients.AirIngredient;
import net.Indyuce.mmoitems.api.recipe.workbench.ingredients.MMOItemIngredient;
import net.Indyuce.mmoitems.api.recipe.workbench.ingredients.VanillaIngredient;
import net.Indyuce.mmoitems.api.recipe.workbench.ingredients.WorkbenchIngredient;
import net.Indyuce.mmoitems.api.util.message.FFPMMOItems;
import net.Indyuce.mmoitems.gui.edition.recipe.RecipeTypeListGUI;
import net.Indyuce.mmoitems.gui.edition.recipe.gui.RecipeEditorGUI;
import net.Indyuce.mmoitems.gui.edition.recipe.registry.RecipeRegistry;
import net.Indyuce.mmoitems.gui.edition.recipe.registry.burninglegacy.BurningRecipeInformation;
import org.bukkit.Bukkit;
import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.BlastingRecipe;
import org.bukkit.inventory.CampfireRecipe;
import org.bukkit.inventory.CookingRecipe;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.SmokingRecipe;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RecipeManager implements Reloadable {
   private final ArrayList<Recipe> loadedLegacyRecipes = new ArrayList<>();
   private final ArrayList<MythicRecipeBlueprint> customRecipes = new ArrayList<>();
   private final ArrayList<MythicRecipeBlueprint> booklessRecipes = new ArrayList<>();
   @NotNull
   private final ArrayList<NamespacedKey> blacklistedFromAutomaticDiscovery = new ArrayList<>();
   private boolean book;
   @Nullable
   private ArrayList<NamespacedKey> generatedNamespacedKeys;

   public void loadRecipes() {
      this.book = MMOItems.plugin.getConfig().getBoolean("recipes.use-recipe-book");
      FriendlyFeedbackProvider friendlyFeedbackProvider = new FriendlyFeedbackProvider(FFPMMOItems.get());
      friendlyFeedbackProvider.activatePrefix(true, "Custom Crafting");

      for (Type type : MMOItems.plugin.getTypes().getAll()) {
         FileConfiguration config = type.getConfigFile().getConfig();

         for (MMOItemTemplate template : MMOItems.plugin.getTemplates().getTemplates(type)) {
            if (config.contains(template.getId() + ".base.crafting")) {
               ConfigurationSection section = RecipeEditorGUI.getSection(config, template.getId() + ".base.crafting");

               for (String registeredRecipe : RecipeTypeListGUI.getRegisteredRecipes()) {
                  if (section.contains(registeredRecipe)) {
                     RecipeRegistry registry = RecipeTypeListGUI.getRegisteredRecipe(registeredRecipe);
                     ConfigurationSection registrySection = RecipeEditorGUI.getSection(section, registeredRecipe);

                     for (String key : registrySection.getKeys(false)) {
                        NamespacedKey recipeKey = this.getRecipeKey(template.getType(), template.getId(), registeredRecipe, key);
                        Ref ref = new Ref(recipeKey);
                        FriendlyFeedbackProvider feedbackProvider = new FriendlyFeedbackProvider(FFPMMOItems.get());
                        feedbackProvider.activatePrefix(true, "Recipe of $u" + template.getType() + " " + template.getId());

                        try {
                           MythicRecipeBlueprint mythicRecipeBlueprint = registry.sendToMythicLib(template, registrySection, key, ref, feedbackProvider);
                           (ref.getValue() != null ? this.customRecipes : this.booklessRecipes).add(mythicRecipeBlueprint);
                        } catch (IllegalArgumentException exception) {
                           if (!exception.getMessage().isEmpty()) {
                              MMOItems.print(
                                 null,
                                 "Cannot register custom recipe '$u{2}$b' for $e{0} {1}$b;$f {3}",
                                 "Custom Crafting",
                                 type.getId(),
                                 template.getId(),
                                 key,
                                 exception.getMessage()
                              );
                              feedbackProvider.sendTo(FriendlyFeedbackCategory.ERROR, MMOItems.getConsole());
                              feedbackProvider.sendTo(FriendlyFeedbackCategory.FAILURE, MMOItems.getConsole());
                           }
                        }
                     }
                  }
               }
            }
         }
      }

      friendlyFeedbackProvider.sendTo(FriendlyFeedbackCategory.ERROR, MMOItems.getConsole());
      friendlyFeedbackProvider.sendTo(FriendlyFeedbackCategory.FAILURE, MMOItems.getConsole());
      Bukkit.getScheduler().runTask(MMOItems.plugin, () -> this.getBukkitRecipes().forEach(Bukkit::addRecipe));
   }

   @Deprecated
   public void registerBurningRecipe(
      @NotNull RecipeManager.BurningRecipeType burningRecipeType,
      @NotNull MMOItem mmoItem,
      @NotNull BurningRecipeInformation burningRecipeInformation,
      int count,
      @NotNull NamespacedKey namespacedKey,
      boolean flag
   ) {
      ItemStack item = mmoItem.newBuilder().build();
      item.setAmount(count);
      CookingRecipe cookingRecipe = burningRecipeType.provideRecipe(namespacedKey, item, burningRecipeInformation.getChoice().toBukkit(), burningRecipeInformation.getExp(), burningRecipeInformation.getBurnTime());
      this.loadedLegacyRecipes.add(cookingRecipe);
      if (flag) {
         this.blacklistedFromAutomaticDiscovery.add(namespacedKey);
      }
   }

   @NotNull
   public ArrayList<Recipe> getBukkitRecipes() {
      return this.loadedLegacyRecipes;
   }

   @NotNull
   public ArrayList<MythicRecipeBlueprint> getCustomRecipes() {
      return this.customRecipes;
   }

   @NotNull
   public ArrayList<MythicRecipeBlueprint> getBooklessRecipes() {
      return this.booklessRecipes;
   }

   public ArrayList<NamespacedKey> getNamespacedKeys() {
      if (this.generatedNamespacedKeys != null) {
         return this.generatedNamespacedKeys;
      }

      this.generatedNamespacedKeys = new ArrayList<>();
      this.customRecipes.forEach(mythicRecipeBlueprint -> this.generatedNamespacedKeys.add(mythicRecipeBlueprint.getNk()));
      this.loadedLegacyRecipes.forEach(recipe -> this.generatedNamespacedKeys.add(((Keyed)recipe).getKey()));
      return this.generatedNamespacedKeys;
   }

   @NotNull
   public NamespacedKey getRecipeKey(@NotNull Type type, @NotNull String text, @NotNull String recipeType, @NotNull String recipeKey) {
      return new NamespacedKey(MMOItems.plugin, recipeType + "_" + type.getId() + "_" + text + "_" + recipeKey);
   }

   @Override
   public void reload() {
      Bukkit.getScheduler()
         .runTask(
            MMOItems.plugin,
            () -> {
               for (NamespacedKey namespacedKey : this.getNamespacedKeys()) {
                  if (namespacedKey != null) {
                     try {
                        Bukkit.removeRecipe(namespacedKey);
                     } catch (Throwable exception) {
                        MMOItems.print(
                           null, "Could not register crafting book recipe for $r{0}$b:$f {1}", "MMOItems Custom Crafting", namespacedKey.getKey(), exception.getMessage()
                        );
                     }
                  }
               }

               this.loadedLegacyRecipes.clear();
               this.blacklistedFromAutomaticDiscovery.clear();
               this.customRecipes.forEach(mythicRecipeBlueprint -> {
                  try {
                     mythicRecipeBlueprint.disable();
                     Bukkit.removeRecipe(mythicRecipeBlueprint.getNk());
                  } catch (Throwable exception) {
                     MMOItems.plugin.getLogger().log(Level.SEVERE, "Could not unregister knowledge book recipe '" + mythicRecipeBlueprint.getNk() + "': " + exception.getMessage());
                  }
               });
               this.customRecipes.clear();

               for (MythicRecipeBlueprint booklessRecipe : this.booklessRecipes) {
                  booklessRecipe.disable();
               }

               this.booklessRecipes.clear();
               this.generatedNamespacedKeys = null;
               this.loadRecipes();
               if (this.book) {
                  for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                     this.refreshRecipeBook(onlinePlayer);
                  }
               }
            }
         );
   }

   public void refreshRecipeBook(Player player) {
      if (!this.book) {
         for (NamespacedKey discoveredRecipe : player.getDiscoveredRecipes()) {
            if ("mmoitems".equals(discoveredRecipe.getNamespace())) {
               player.undiscoverRecipe(discoveredRecipe);
            }
         }
      } else if (MythicLib.plugin.getVersion().isStrictlyHigher(new int[]{1, 16})) {
         for (NamespacedKey discoveredRecipe : player.getDiscoveredRecipes()) {
            if ("mmoitems".equals(discoveredRecipe.getNamespace()) && !this.getNamespacedKeys().contains(discoveredRecipe)) {
               player.undiscoverRecipe(discoveredRecipe);
            }
         }

         for (NamespacedKey namespacedKey : this.getNamespacedKeys()) {
            if (namespacedKey != null) {
               boolean flag = false;

               for (NamespacedKey blacklistedKey : this.blacklistedFromAutomaticDiscovery) {
                  if (namespacedKey.equals(blacklistedKey)) {
                     flag = true;
                     break;
                  }
               }

               if (!flag) {
                  try {
                     if (!player.hasDiscoveredRecipe(namespacedKey)) {
                        player.discoverRecipe(namespacedKey);
                     }
                  } catch (Throwable exception) {
                     MMOItems.print(
                        null, "Could not register crafting book recipe for $r{0}$b:$f {1}", "MMOItems Custom Crafting", namespacedKey.getKey(), exception.getMessage()
                     );
                  }
               }
            }
         }
      } else {
         for (NamespacedKey namespacedKey : this.getNamespacedKeys()) {
            if (namespacedKey != null) {
               boolean flag = false;

               for (NamespacedKey blacklistedKey : this.blacklistedFromAutomaticDiscovery) {
                  if (namespacedKey.equals(blacklistedKey)) {
                     flag = true;
                     break;
                  }
               }

               if (!flag) {
                  try {
                     player.discoverRecipe(namespacedKey);
                  } catch (Throwable exception) {
                     MMOItems.print(
                        null, "Could not register crafting book recipe for $r{0}$b:$f {1}", "MMOItems Custom Crafting", namespacedKey.getKey(), exception.getMessage()
                     );
                  }
               }
            }
         }
      }
   }

   @NotNull
   public static WorkbenchIngredient getWorkbenchIngredient(@NotNull String text) {
      ProvidedUIFilter providedUIFilter = ProvidedUIFilter.getFromString(RecipeEditorGUI.poofFromLegacy(text), null);
      if (providedUIFilter == null) {
         return new AirIngredient();
      } else {
         FriendlyFeedbackProvider friendlyFeedbackProvider = new FriendlyFeedbackProvider(FFPMMOItems.get());
         if (!providedUIFilter.isValid(friendlyFeedbackProvider)) {
            throw new IllegalArgumentException(
               SilentNumbers.collapseList(
                  SilentNumbers.transcribeList(
                     friendlyFeedbackProvider.getFeedbackOf(FriendlyFeedbackCategory.ERROR), message -> ((FriendlyFeedbackMessage)message).forConsole(FFPMMOItems.get())
                  ),
                  ". "
               )
            );
         } else {
            int amount = providedUIFilter.getAmount(0);
            if (providedUIFilter.getParent() instanceof MMOItemUIFilter) {
               Type type = MMOItems.plugin.getTypes().getOrThrow(providedUIFilter.getArgument());
               MMOItemTemplate templateOrThrow = MMOItems.plugin.getTemplates().getTemplateOrThrow(type, providedUIFilter.getData());
               return new MMOItemIngredient(type, templateOrThrow.getId(), amount);
            } else if (providedUIFilter.getParent() instanceof VanillaUIFilter) {
               return new VanillaIngredient(Material.valueOf(providedUIFilter.getArgument().toUpperCase().replace("-", "_").replace(" ", "_")), amount);
            } else {
               throw new IllegalArgumentException("Unsupported ingredient, you may only specify vanilla or mmoitems.");
            }
         }
      }
   }

   @Deprecated
   public enum BurningRecipeType {
      FURNACE(FurnaceRecipe::new),
      SMOKER(SmokingRecipe::new),
      CAMPFIRE(CampfireRecipe::new),
      BLAST(BlastingRecipe::new);

      private final RecipeManager.RecipeProvider provider;

      BurningRecipeType(RecipeManager.RecipeProvider nullxx) {
         this.provider = nullxx;
      }

      public CookingRecipe<?> provideRecipe(NamespacedKey namespacedKey, ItemStack item, RecipeChoice recipeChoice, float value, int count) {
         return this.provider.provide(namespacedKey, item, recipeChoice, value, count);
      }

      public String getPath() {
         return this.name().toLowerCase();
      }
   }

   @FunctionalInterface
   public interface RecipeProvider {
      CookingRecipe<?> provide(NamespacedKey namespacedKey, ItemStack item, RecipeChoice recipeChoice, float value, int count);
   }
}
