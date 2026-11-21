package com.nexomc.nexo.recipes.loaders;

import com.nexomc.nexo.NexoPlugin;
import com.nexomc.nexo.api.NexoItems;
import com.nexomc.nexo.compatibilities.mythiccrucible.WrappedCrucibleItem;
import com.nexomc.nexo.items.ItemBuilder;
import com.nexomc.nexo.items.ItemUpdater;
import com.nexomc.nexo.recipes.CustomRecipe;
import com.nexomc.nexo.recipes.listeners.RecipeEventManager;
import com.nexomc.nexo.utils.NexoYamlKt;
import com.nexomc.nexo.utils.RecipeUtils;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import net.Indyuce.mmoitems.MMOItems;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000D\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\b&\u0018\u00002\u00020\u0001B\u0011\b\u0004\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\u0012\u0010\f\u001a\u0004\u0018\u00010\r2\u0006\u0010\u000e\u001a\u00020\u0003H\u0004J\b\u0010\u0016\u001a\u00020\u0017H&J\u0012\u0010\u0018\u001a\u00020\u00172\b\u0010\u0019\u001a\u0004\u0018\u00010\u001aH\u0004J\u0012\u0010\u001b\u001a\u00020\u00172\b\u0010\u0019\u001a\u0004\u0018\u00010\u001aH\u0004R\u0014\u0010\u0002\u001a\u00020\u0003X\u0084\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007R\u0014\u0010\b\u001a\u00020\tX\u0084\u0004¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000bR\u0013\u0010\u000f\u001a\u00070\u0010¢\u0006\u0002\b\u0011X\u0082\u0004¢\u0006\u0002\n\u0000R\u0014\u0010\u0012\u001a\u00020\u0013X\u0084\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u0015¨\u0006\u001c"},
   d2 = {"Lcom/nexomc/nexo/recipes/loaders/RecipeLoader;", "", "section", "Lorg/bukkit/configuration/ConfigurationSection;", "<init>", "(Lorg/bukkit/configuration/ConfigurationSection;)V", "getSection", "()Lorg/bukkit/configuration/ConfigurationSection;", "result", "Lorg/bukkit/inventory/ItemStack;", "getResult", "()Lorg/bukkit/inventory/ItemStack;", "recipeChoice", "Lorg/bukkit/inventory/RecipeChoice;", "iSection", "recipeName", "", "Lorg/jetbrains/annotations/NotNull;", "key", "Lorg/bukkit/NamespacedKey;", "getKey", "()Lorg/bukkit/NamespacedKey;", "registerRecipe", "", "loadRecipe", "recipe", "Lorg/bukkit/inventory/Recipe;", "addToWhitelist", "com.nexomc:core"}
)
public abstract class RecipeLoader {
   @NotNull
   private final ConfigurationSection section;
   @NotNull
   private final ItemStack result;
   @NotNull
   private final String recipeName;
   @NotNull
   private final NamespacedKey key;

   protected RecipeLoader(@NotNull ConfigurationSection section) {
      super();
      this.section = section;
      RecipeLoader recipeLoader = this;
      int value = 0;
      ConfigurationSection configurationSection = NexoYamlKt.section(this.section, "result");
      ItemStack itemStack;
      if (configurationSection == null) {
         itemStack = ItemStack.empty();
      } else {
         block: {
            block2: {
               ConfigurationSection resultSection = configurationSection;
               if (resultSection.isString("nexo_item")) {
                  block3: {
                     String str = NexoYamlKt.stringOrNull(resultSection, "nexo_item");
                     if (str != null) {
                        String p0 = str;
                        NexoItems nexoItems = NexoItems.INSTANCE;
                        int p0_4 = 0;
                        ItemBuilder itemBuilder = NexoItems.itemFromId(p0);
                        if (itemBuilder != null) {
                           ItemStack itemStack2 = itemBuilder.build();
                           if (itemStack2 != null) {
                              ItemStack p0_3 = itemStack2;
                              ItemUpdater.Companion companion = ItemUpdater.Companion;
                              int value2 = 0;
                              itemStack = companion.updateItem(p0_3);
                              break block3;
                           }
                        }
                     }

                     itemStack = null;
                  }
               } else if (resultSection.isString("crucible_item")) {
                  itemStack = (new WrappedCrucibleItem(resultSection.getString("crucible_item"), true)).build();
               } else if (resultSection.isString("mmoitems_id") && resultSection.isString("mmoitems_type")) {
                  itemStack = MMOItems.plugin.getItem(resultSection.getString("mmoitems_type"), resultSection.getString("mmoitems_id"));
               } else if (resultSection.isString("minecraft_type")) {
                  String string = resultSection.getString("minecraft_type");
                  if (string == null) {
                     string = "";
                  }

                  Material it = Material.matchMaterial(string);
                  if (it == null) {
                     break block2;
                  }

                  int p0 = 0;
                  Material p0_2 = !it.isAir() ? it : null;
                  if (p0_2 == null) {
                     break block2;
                  }

                  int value3 = 0;
                  ItemStack p0_1 = ItemStack.of(p0_2);
                  if (p0_1 == null) {
                     break block2;
                  }

                  itemStack = p0_1;
               } else {
                  itemStack = resultSection.getItemStack("minecraft_item");
               }

               ItemStack itemStack3 = itemStack;
               if (itemStack3 != null) {
                  ItemStack it = itemStack3.asQuantity(resultSection.getInt("amount", 1));
                  if (it != null) {
                     itemStack = it;
                     break block;
                  }
               }

               itemStack = ItemStack.empty();
               break block;
            }

            itemStack = ItemStack.empty();
         }
      }

      this.result = itemStack;
      String name = this.section.getName();
      this.recipeName = name;
      this.key = new NamespacedKey((Plugin)NexoPlugin.Companion.instance(), this.recipeName);
   }

   @NotNull
   protected final ConfigurationSection getSection() {
      return this.section;
   }

   @NotNull
   protected final ItemStack getResult() {
      return this.result;
   }

   @Nullable
   protected final RecipeChoice recipeChoice(@NotNull ConfigurationSection iSection) {
      if (iSection.isString("nexo_item")) {
         ItemBuilder itemBuilder = NexoItems.itemFromId(iSection.getString("nexo_item"));
         if (itemBuilder != null) {
            ItemStack itemStack = itemBuilder.build();
            if (itemStack != null) {
               ItemStack ingredient = itemStack;
               return (RecipeChoice)(new RecipeChoice.ExactChoice(ItemUpdater.Companion.updateItem(ingredient)));
            }
         }

         return null;
      } else if (iSection.isString("crucible_item")) {
         ItemStack ingredient = (new WrappedCrucibleItem(iSection.getString("crucible_item"), true)).build();
         RecipeChoice.ExactChoice exactChoice;
         ItemStack itemStack2 = ingredient;
         if (ingredient == null) {
            itemStack2 = ItemStack.of(Material.AIR);
         }

         exactChoice = new RecipeChoice.ExactChoice(itemStack2);
         return (RecipeChoice)exactChoice;
      } else if (iSection.isString("mmoitems_id") && iSection.isString("mmoitems_type")) {
         ItemStack item = MMOItems.plugin.getItem(iSection.getString("mmoitems_type"), iSection.getString("mmoitems_id"));
         if (item == null) {
            item = ItemStack.of(Material.AIR);
         }

         ItemStack ingredient = item;
         return (RecipeChoice)(new RecipeChoice.ExactChoice(ingredient));
      } else {
         String ingredient = iSection.getString("minecraft_type");
         if (ingredient != null) {
            int value = 0;
            Material material = Material.getMaterial(ingredient);
            return material != null && !material.isAir() ? RecipeUtils.INSTANCE.recipeChoice(material) : null;
         } else {
            ingredient = iSection.getString("tag");
            if (ingredient != null) {
               int value2 = 0;
               NamespacedKey namespacedKey = NamespacedKey.fromString(ingredient);
               if (namespacedKey == null) {
                  namespacedKey = NamespacedKey.minecraft("oak_logs");
               }

               NamespacedKey tagId = namespacedKey;
               Tag tag2 = Bukkit.getTag("blocks", tagId, Material.class);
               if (tag2 == null) {
                  tag2 = Bukkit.getTag("items", tagId, Material.class);
               }

               Tag tag = tag2;
               RecipeChoice recipeChoice;
               if (tag != null) {
                  RecipeUtils recipeUtils = RecipeUtils.INSTANCE;
                  int value3 = 0;
                  recipeChoice = recipeUtils.recipeChoice(tag);
               } else {
                  recipeChoice = null;
               }

               return recipeChoice;
            } else {
               RecipeChoice.ExactChoice exactChoice2;
               ItemStack itemStack3 = iSection.getItemStack("minecraft_item");
               if (itemStack3 == null) {
                  return null;
               } else {
                  exactChoice2 = new RecipeChoice.ExactChoice(itemStack3);
                  return (RecipeChoice)exactChoice2;
               }
            }
         }
      }
   }

   @NotNull
   protected final NamespacedKey getKey() {
      return this.key;
   }

   public abstract void registerRecipe();

   protected final void loadRecipe(@Nullable Recipe recipe) {
      Bukkit.addRecipe(recipe, false);
      CustomRecipe customRecipe2 = CustomRecipe.Companion.fromRecipe(recipe);
      if (customRecipe2 != null) {
         CustomRecipe customRecipe = customRecipe2;
         RecipeEventManager.Companion.instance().addPermissionRecipe(customRecipe, NexoYamlKt.stringOrNull(this.section, "permission"));
      }
   }

   protected final void addToWhitelist(@Nullable Recipe recipe) {
      RecipeEventManager recipeEventManager = RecipeEventManager.Companion.instance();
      CustomRecipe customRecipe = CustomRecipe.Companion.fromRecipe(recipe);
      if (customRecipe != null) {
         recipeEventManager.whitelistRecipe(customRecipe);
      }
   }
}
