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
      FriendlyFeedbackProvider var1 = new FriendlyFeedbackProvider(FFPMMOItems.get());
      var1.activatePrefix(true, "Custom Crafting");

      for (Type var3 : MMOItems.plugin.getTypes().getAll()) {
         FileConfiguration var4 = var3.getConfigFile().getConfig();

         for (MMOItemTemplate var6 : MMOItems.plugin.getTemplates().getTemplates(var3)) {
            if (var4.contains(var6.getId() + ".base.crafting")) {
               ConfigurationSection var7 = RecipeEditorGUI.getSection(var4, var6.getId() + ".base.crafting");

               for (String var9 : RecipeTypeListGUI.getRegisteredRecipes()) {
                  if (var7.contains(var9)) {
                     RecipeRegistry var10 = RecipeTypeListGUI.getRegisteredRecipe(var9);
                     ConfigurationSection var11 = RecipeEditorGUI.getSection(var7, var9);

                     for (String var13 : var11.getKeys(false)) {
                        NamespacedKey var14 = this.getRecipeKey(var6.getType(), var6.getId(), var9, var13);
                        Ref var15 = new Ref(var14);
                        FriendlyFeedbackProvider var16 = new FriendlyFeedbackProvider(FFPMMOItems.get());
                        var16.activatePrefix(true, "Recipe of $u" + var6.getType() + " " + var6.getId());

                        try {
                           MythicRecipeBlueprint var17 = var10.sendToMythicLib(var6, var11, var13, var15, var16);
                           (var15.getValue() != null ? this.customRecipes : this.booklessRecipes).add(var17);
                        } catch (IllegalArgumentException var18) {
                           if (!var18.getMessage().isEmpty()) {
                              MMOItems.print(
                                 null,
                                 "Cannot register custom recipe '$u{2}$b' for $e{0} {1}$b;$f {3}",
                                 "Custom Crafting",
                                 var3.getId(),
                                 var6.getId(),
                                 var13,
                                 var18.getMessage()
                              );
                              var16.sendTo(FriendlyFeedbackCategory.ERROR, MMOItems.getConsole());
                              var16.sendTo(FriendlyFeedbackCategory.FAILURE, MMOItems.getConsole());
                           }
                        }
                     }
                  }
               }
            }
         }
      }

      var1.sendTo(FriendlyFeedbackCategory.ERROR, MMOItems.getConsole());
      var1.sendTo(FriendlyFeedbackCategory.FAILURE, MMOItems.getConsole());
      Bukkit.getScheduler().runTask(MMOItems.plugin, () -> this.getBukkitRecipes().forEach(Bukkit::addRecipe));
   }

   @Deprecated
   public void registerBurningRecipe(
      @NotNull RecipeManager.BurningRecipeType var1,
      @NotNull MMOItem var2,
      @NotNull BurningRecipeInformation var3,
      int var4,
      @NotNull NamespacedKey var5,
      boolean var6
   ) {
      ItemStack var7 = var2.newBuilder().build();
      var7.setAmount(var4);
      CookingRecipe var8 = var1.provideRecipe(var5, var7, var3.getChoice().toBukkit(), var3.getExp(), var3.getBurnTime());
      this.loadedLegacyRecipes.add(var8);
      if (var6) {
         this.blacklistedFromAutomaticDiscovery.add(var5);
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
      this.customRecipes.forEach(var1 -> this.generatedNamespacedKeys.add(var1.getNk()));
      this.loadedLegacyRecipes.forEach(var1 -> this.generatedNamespacedKeys.add(((Keyed)var1).getKey()));
      return this.generatedNamespacedKeys;
   }

   @NotNull
   public NamespacedKey getRecipeKey(@NotNull Type var1, @NotNull String var2, @NotNull String var3, @NotNull String var4) {
      return new NamespacedKey(MMOItems.plugin, var3 + "_" + var1.getId() + "_" + var2 + "_" + var4);
   }

   @Override
   public void reload() {
      Bukkit.getScheduler()
         .runTask(
            MMOItems.plugin,
            () -> {
               for (NamespacedKey var2 : this.getNamespacedKeys()) {
                  if (var2 != null) {
                     try {
                        Bukkit.removeRecipe(var2);
                     } catch (Throwable var4) {
                        MMOItems.print(
                           null, "Could not register crafting book recipe for $r{0}$b:$f {1}", "MMOItems Custom Crafting", var2.getKey(), var4.getMessage()
                        );
                     }
                  }
               }

               this.loadedLegacyRecipes.clear();
               this.blacklistedFromAutomaticDiscovery.clear();
               this.customRecipes.forEach(var0 -> {
                  try {
                     var0.disable();
                     Bukkit.removeRecipe(var0.getNk());
                  } catch (Throwable var2x) {
                     MMOItems.plugin.getLogger().log(Level.SEVERE, "Could not unregister knowledge book recipe '" + var0.getNk() + "': " + var2x.getMessage());
                  }
               });
               this.customRecipes.clear();

               for (MythicRecipeBlueprint var7 : this.booklessRecipes) {
                  var7.disable();
               }

               this.booklessRecipes.clear();
               this.generatedNamespacedKeys = null;
               this.loadRecipes();
               if (this.book) {
                  for (Player var8 : Bukkit.getOnlinePlayers()) {
                     this.refreshRecipeBook(var8);
                  }
               }
            }
         );
   }

   public void refreshRecipeBook(Player var1) {
      if (!this.book) {
         for (NamespacedKey var14 : var1.getDiscoveredRecipes()) {
            if ("mmoitems".equals(var14.getNamespace())) {
               var1.undiscoverRecipe(var14);
            }
         }
      } else if (MythicLib.plugin.getVersion().isStrictlyHigher(new int[]{1, 16})) {
         for (NamespacedKey var12 : var1.getDiscoveredRecipes()) {
            if ("mmoitems".equals(var12.getNamespace()) && !this.getNamespacedKeys().contains(var12)) {
               var1.undiscoverRecipe(var12);
            }
         }

         for (NamespacedKey var13 : this.getNamespacedKeys()) {
            if (var13 != null) {
               boolean var15 = false;

               for (NamespacedKey var17 : this.blacklistedFromAutomaticDiscovery) {
                  if (var13.equals(var17)) {
                     var15 = true;
                     break;
                  }
               }

               if (!var15) {
                  try {
                     if (!var1.hasDiscoveredRecipe(var13)) {
                        var1.discoverRecipe(var13);
                     }
                  } catch (Throwable var7) {
                     MMOItems.print(
                        null, "Could not register crafting book recipe for $r{0}$b:$f {1}", "MMOItems Custom Crafting", var13.getKey(), var7.getMessage()
                     );
                  }
               }
            }
         }
      } else {
         for (NamespacedKey var3 : this.getNamespacedKeys()) {
            if (var3 != null) {
               boolean var4 = false;

               for (NamespacedKey var6 : this.blacklistedFromAutomaticDiscovery) {
                  if (var3.equals(var6)) {
                     var4 = true;
                     break;
                  }
               }

               if (!var4) {
                  try {
                     var1.discoverRecipe(var3);
                  } catch (Throwable var8) {
                     MMOItems.print(
                        null, "Could not register crafting book recipe for $r{0}$b:$f {1}", "MMOItems Custom Crafting", var3.getKey(), var8.getMessage()
                     );
                  }
               }
            }
         }
      }
   }

   @NotNull
   public static WorkbenchIngredient getWorkbenchIngredient(@NotNull String var0) {
      ProvidedUIFilter var1 = ProvidedUIFilter.getFromString(RecipeEditorGUI.poofFromLegacy(var0), null);
      if (var1 == null) {
         return new AirIngredient();
      } else {
         FriendlyFeedbackProvider var2 = new FriendlyFeedbackProvider(FFPMMOItems.get());
         if (!var1.isValid(var2)) {
            throw new IllegalArgumentException(
               SilentNumbers.collapseList(
                  SilentNumbers.transcribeList(
                     var2.getFeedbackOf(FriendlyFeedbackCategory.ERROR), var0x -> ((FriendlyFeedbackMessage)var0x).forConsole(FFPMMOItems.get())
                  ),
                  ". "
               )
            );
         } else {
            int var3 = var1.getAmount(0);
            if (var1.getParent() instanceof MMOItemUIFilter) {
               Type var4 = MMOItems.plugin.getTypes().getOrThrow(var1.getArgument());
               MMOItemTemplate var5 = MMOItems.plugin.getTemplates().getTemplateOrThrow(var4, var1.getData());
               return new MMOItemIngredient(var4, var5.getId(), var3);
            } else if (var1.getParent() instanceof VanillaUIFilter) {
               return new VanillaIngredient(Material.valueOf(var1.getArgument().toUpperCase().replace("-", "_").replace(" ", "_")), var3);
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

      public CookingRecipe<?> provideRecipe(NamespacedKey var1, ItemStack var2, RecipeChoice var3, float var4, int var5) {
         return this.provider.provide(var1, var2, var3, var4, var5);
      }

      public String getPath() {
         return this.name().toLowerCase();
      }
   }

   @FunctionalInterface
   public interface RecipeProvider {
      CookingRecipe<?> provide(NamespacedKey var1, ItemStack var2, RecipeChoice var3, float var4, int var5);
   }
}
