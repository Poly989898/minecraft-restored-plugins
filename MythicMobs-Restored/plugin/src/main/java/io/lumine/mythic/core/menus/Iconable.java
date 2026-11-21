package io.lumine.mythic.core.menus;

import io.lumine.mythic.api.MythicProvider;
import io.lumine.mythic.api.adapters.AbstractItemStack;
import io.lumine.mythic.api.config.MythicConfig;
import io.lumine.mythic.api.packs.Pack;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.compatibility.CompatibilityManager;
import io.lumine.mythic.bukkit.compatibility.CrucibleSupport;
import io.lumine.mythic.bukkit.utils.items.ItemFactory;
import io.lumine.mythic.bukkit.utils.numbers.Numbers;
import io.lumine.mythic.core.logging.MythicLogger;
import java.util.Collection;
import java.util.OptionalInt;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;

public interface Iconable {
   private static MythicBukkit getPlugin() {
      return MythicBukkit.inst();
   }

   private static String getIconOption(MythicConfig config, String key) {
      String iconPath = "Icon." + key;
      if (config.isSet(iconPath)) {
         return config.getString(iconPath);
      } else {
         return config.isSet(key) ? config.getString(key) : null;
      }
   }

   private static boolean hasIconSectionOptions(MythicConfig config) {
      return config.isConfigurationSection("Icon")
         || getIconOption(config, "Item") != null
         || getIconOption(config, "Material") != null
         || getIconOption(config, "Model") != null;
   }

   String getDisplayName();

   Collection<String> getDescription();

   Pack getPack();

   static ItemFactory get(Iconable iconable, MythicConfig config) {
      CompatibilityManager compatManager = MythicBukkit.inst().getCompatibility();
      Material iconMaterial = getPlugin().getConfiguration().getMenuIconGenerationMaterial();
      int iconModel = 0;

      ItemFactory icon;
      try {
         icon = ItemFactory.of(iconMaterial);
      } catch (IllegalArgumentException e) {
         icon = ItemFactory.of(Material.PAPER);
      }

      if (hasIconSectionOptions(config)) {
         String itemConfig = getIconOption(config, "Item");
         if (itemConfig != null) {
            AbstractItemStack aItem = MythicProvider.get().getBootstrap().createItem(itemConfig);
            ItemStack bukkitItem = BukkitAdapter.adapt(aItem);
            icon = ItemFactory.of(bukkitItem);
         }

         String itemMaterialConfig = getIconOption(config, "Material");
         if (itemMaterialConfig != null) {
            iconMaterial = Material.valueOf(itemMaterialConfig);
            icon.type(iconMaterial);
         }

         String model = getIconOption(config, "Model");
         if (model != null) {
            OptionalInt maybeNumber = Numbers.parseInteger(model);
            if (maybeNumber.isPresent()) {
               iconModel = maybeNumber.getAsInt();
               icon.customModelData(maybeNumber.getAsInt());
            } else if (MythicBukkit.inst().getCompatibility().getCrucible().isPresent()
               && MythicBukkit.inst().getCompatibility().getCrucible().get().isImplicitlyGenerating()) {
               CrucibleSupport.ItemGenerationFeedback generationFeedback = MythicBukkit.inst()
                  .getCompatibility()
                  .getCrucible()
                  .get()
                  .addItemToGeneration(iconable.getPack(), model);
               if (generationFeedback.getItemModel().isPresent()) {
                  NamespacedKey itemModelKey = generationFeedback.getItemModel().get();
                  icon.itemModel(itemModelKey);
               }
            } else {
               icon.itemModel(NamespacedKey.fromString(model));
            }
         } else {
            iconModel = getPlugin().getConfiguration().getIconGenerationModel();
            icon.model(iconModel);
         }

         if (config.isSet("Icon.Generation")) {
            if (compatManager.getCrucible().isPresent()) {
               compatManager.getCrucible().get().addItemToGeneration(iconable.getPack(), iconMaterial, iconModel, config.getNestedConfig("Icon"));
            } else {
               MythicLogger.error("Spell Icon pack generation requires Crucible.");
            }
         }
      } else if (config.isSet("Icon")) {
         String modelx = config.getString("Icon");
         OptionalInt maybeNumber = Numbers.parseInteger(modelx);
         if (maybeNumber.isPresent()) {
            icon.model(maybeNumber.getAsInt());
            iconModel = getPlugin().getConfiguration().getIconGenerationModel();
            if (compatManager.getCrucible().isPresent() && MythicBukkit.inst().getCompatibility().getCrucible().get().isImplicitlyGenerating()) {
               compatManager.getCrucible().get().addItemToGeneration(iconable.getPack(), iconMaterial, iconModel, config.getNestedConfig("Icon"));
            } else {
               MythicLogger.error("Spell Icon pack generation requires Crucible.");
            }
         } else if (MythicBukkit.inst().getCompatibility().getCrucible().isPresent()
            && MythicBukkit.inst().getCompatibility().getCrucible().get().isImplicitlyGenerating()) {
            CrucibleSupport.ItemGenerationFeedback generationFeedback = MythicBukkit.inst()
               .getCompatibility()
               .getCrucible()
               .get()
               .addItemToGeneration(iconable.getPack(), modelx);
            if (generationFeedback.getItemModel().isPresent()) {
               NamespacedKey itemModelKey = generationFeedback.getItemModel().get();
               icon.itemModel(itemModelKey);
            }
         } else {
            icon.itemModel(NamespacedKey.fromString(modelx));
         }
      }

      icon.name(iconable.getDisplayName());
      icon.lore(iconable.getDescription());
      return icon;
   }

   static ItemFactory get(Iconable iconable, MythicConfig config, ItemFactory icon) {
      CompatibilityManager compatManager = MythicBukkit.inst().getCompatibility();
      if (hasIconSectionOptions(config)) {
         String itemConfig = getIconOption(config, "Item");
         if (itemConfig != null) {
            AbstractItemStack aItem = MythicProvider.get().getBootstrap().createItem(itemConfig);
            ItemStack bukkitItem = BukkitAdapter.adapt(aItem);
            icon = ItemFactory.of(bukkitItem);
         }

         String itemMaterialConfig = getIconOption(config, "Material");
         if (itemMaterialConfig != null) {
            Material iconMaterial = Material.valueOf(itemMaterialConfig);
            icon.type(iconMaterial);
         }

         String model = getIconOption(config, "Model");
         if (model != null) {
            OptionalInt maybeNumber = Numbers.parseInteger(model);
            if (maybeNumber.isPresent()) {
               icon.customModelData(maybeNumber.getAsInt());
            } else if (MythicBukkit.inst().getCompatibility().getCrucible().isPresent()
               && MythicBukkit.inst().getCompatibility().getCrucible().get().isImplicitlyGenerating()) {
               CrucibleSupport.ItemGenerationFeedback generationFeedback = MythicBukkit.inst()
                  .getCompatibility()
                  .getCrucible()
                  .get()
                  .addItemToGeneration(iconable.getPack(), model);
               if (generationFeedback.getItemModel().isPresent()) {
                  NamespacedKey itemModelKey = generationFeedback.getItemModel().get();
                  icon.itemModel(itemModelKey);
               }
            } else {
               icon.itemModel(NamespacedKey.fromString(model));
            }
         } else {
            int iconModel = getPlugin().getConfiguration().getIconGenerationModel();
            icon.model(iconModel);
         }

         if (config.isSet("Icon.Generation")) {
            if (compatManager.getCrucible().isPresent()) {
               compatManager.getCrucible()
                  .get()
                  .addItemToGeneration(
                     iconable.getPack(), icon.peek().getType(), icon.peek().getItemMeta().getCustomModelData(), config.getNestedConfig("Icon")
                  );
            } else {
               MythicLogger.error("Spell Icon pack generation requires Crucible.");
            }
         }
      } else if (config.isSet("Icon")) {
         String modelx = config.getString("Icon");
         OptionalInt maybeNumber = Numbers.parseInteger(modelx);
         if (maybeNumber.isPresent()) {
            icon.model(maybeNumber.getAsInt());
            if (compatManager.getCrucible().isPresent() && MythicBukkit.inst().getCompatibility().getCrucible().get().isImplicitlyGenerating()) {
               compatManager.getCrucible()
                  .get()
                  .addItemToGeneration(iconable.getPack(), icon.peek().getType(), maybeNumber.getAsInt(), config.getNestedConfig("Icon"));
            } else {
               MythicLogger.error("Spell Icon pack generation requires Crucible.");
            }
         } else if (MythicBukkit.inst().getCompatibility().getCrucible().isPresent()
            && MythicBukkit.inst().getCompatibility().getCrucible().get().isImplicitlyGenerating()) {
            CrucibleSupport.ItemGenerationFeedback generationFeedback = MythicBukkit.inst()
               .getCompatibility()
               .getCrucible()
               .get()
               .addItemToGeneration(iconable.getPack(), modelx);
            if (generationFeedback.getItemModel().isPresent()) {
               NamespacedKey itemModelKey = generationFeedback.getItemModel().get();
               icon.itemModel(itemModelKey);
            }
         } else {
            icon.itemModel(NamespacedKey.fromString(modelx));
         }
      }

      icon.name(iconable.getDisplayName());
      icon.lore(iconable.getDescription());
      return icon;
   }
}
