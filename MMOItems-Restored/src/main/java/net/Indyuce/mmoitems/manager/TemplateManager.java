package net.Indyuce.mmoitems.manager;

import io.lumine.mythic.lib.api.item.NBTItem;
import io.lumine.mythic.lib.api.util.ui.FriendlyFeedbackCategory;
import io.lumine.mythic.lib.api.util.ui.FriendlyFeedbackProvider;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.ConfigFile;
import net.Indyuce.mmoitems.api.ItemTier;
import net.Indyuce.mmoitems.api.Type;
import net.Indyuce.mmoitems.api.item.template.MMOItemTemplate;
import net.Indyuce.mmoitems.api.item.template.ModifierNode;
import net.Indyuce.mmoitems.api.util.TemplateMap;
import net.Indyuce.mmoitems.api.util.message.FFPMMOItems;
import org.apache.commons.lang.Validate;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TemplateManager implements Reloadable {
   private final TemplateMap<MMOItemTemplate> templates = new TemplateMap<>();
   private final Map<String, ModifierNode> modifierNodes = new HashMap<>();
   private static final Random RANDOM = new Random();

   public boolean hasTemplate(@Nullable Type type, @Nullable String text) {
      return type != null && text != null ? this.templates.hasValue(type, text) : false;
   }

   public boolean hasTemplate(@Nullable NBTItem nbtItem) {
      return nbtItem == null ? false : this.hasTemplate(Type.get(nbtItem.getType()), nbtItem.getString("MMOITEMS_ITEM_ID"));
   }

   @Nullable
   public MMOItemTemplate getTemplate(@Nullable Type type, @Nullable String text) {
      return type != null && text != null ? this.templates.getValue(type, text) : null;
   }

   @Nullable
   public MMOItemTemplate getTemplate(@Nullable NBTItem nbtItem) {
      return nbtItem == null ? null : this.getTemplate(Type.get(nbtItem.getType()), nbtItem.getString("MMOITEMS_ITEM_ID"));
   }

   @NotNull
   public MMOItemTemplate getTemplateOrThrow(@Nullable Type type, @Nullable String text) {
      Validate.isTrue(type != null && this.hasTemplate(type, text), "Could not find a template with ID '" + text + "'");
      return this.templates.getValue(type, text);
   }

   @NotNull
   public Collection<MMOItemTemplate> getTemplates(@NotNull Type type) {
      return this.templates.collectValues(type);
   }

   @NotNull
   public ArrayList<String> getTemplateNames(@NotNull Type type) {
      ArrayList list = new ArrayList();

      for (MMOItemTemplate collectValue : this.templates.collectValues(type)) {
         list.add(collectValue.getId());
      }

      return list;
   }

   public void registerTemplate(@NotNull MMOItemTemplate mmoItemTemplate) {
      Validate.notNull(mmoItemTemplate, "MMOItem template cannot be null");
      this.templates.setValue(mmoItemTemplate.getType(), mmoItemTemplate.getId(), mmoItemTemplate);
   }

   public void unregisterTemplate(@NotNull Type type, @NotNull String text) {
      this.templates.removeValue(type, text);
   }

   public void deleteTemplate(@NotNull Type type, @NotNull String text) {
      this.unregisterTemplate(type, text);
      ConfigFile configFile = type.getConfigFile();
      configFile.getConfig().set(text, null);
      configFile.save();
   }

   public MMOItemTemplate requestTemplateUpdate(@NotNull Type type, @NotNull String text) {
      this.templates.removeValue(type, text);

      try {
         MMOItemTemplate mmoItemTemplate = new MMOItemTemplate(type, type.getConfigFile().getConfig().getConfigurationSection(text));
         mmoItemTemplate.getPostLoadAction().performAction();
         this.registerTemplate(mmoItemTemplate);
         return mmoItemTemplate;
      } catch (IllegalArgumentException exception) {
         MMOItems.plugin.getLogger().log(Level.SEVERE, "An error occurred while trying to reload item gen template '" + text + "': " + exception.getMessage());
         return null;
      }
   }

   public Collection<MMOItemTemplate> collectTemplates() {
      return this.templates.collectValues();
   }

   @Deprecated
   public boolean hasModifier(String text) {
      return this.modifierNodes.get(text) != null;
   }

   @Deprecated
   @Nullable
   public ModifierNode getModifier(String text) {
      return this.modifierNodes.get(text);
   }

   @Deprecated
   public Collection<ModifierNode> getModifiers() {
      return this.modifierNodes.values();
   }

   public boolean hasModifierNode(@NotNull String text) {
      return this.modifierNodes.containsKey(text);
   }

   @Nullable
   public ModifierNode getModifierNode(@NotNull String text) {
      return this.modifierNodes.get(text);
   }

   @NotNull
   public Collection<ModifierNode> getModifierNodes() {
      return this.modifierNodes.values();
   }

   @Nullable
   public ItemTier rollTier() {
      double value = 0.0;

      for (ItemTier itemTier : MMOItems.plugin.getTiers().getAll()) {
         if (value >= 1.0 || RANDOM.nextDouble() < itemTier.getGenerationChance() / (1.0 - value)) {
            return itemTier;
         }

         value += itemTier.getGenerationChance();
      }

      return null;
   }

   public int rollLevel(int count) {
      double levelSpread = MMOItems.plugin.getLanguage().levelSpread;
      double value = RANDOM.nextGaussian() * levelSpread * 0.7 + count;
      value = Math.max(Math.min(value, count + levelSpread), Math.max(1.0, count - levelSpread));
      return (int)value;
   }

   @Deprecated
   public void preloadTemplates() {
      this.preloadObjects();
   }

   @Deprecated
   public void postloadTemplates() {
      this.postloadObjects();
   }

   public void preloadObjects() {
      FriendlyFeedbackProvider friendlyFeedbackProvider = new FriendlyFeedbackProvider(FFPMMOItems.get());
      friendlyFeedbackProvider.activatePrefix(true, "Template Modifiers");
      friendlyFeedbackProvider.log(FriendlyFeedbackCategory.INFORMATION, "Preloading template modifiers, please wait..", new String[0]);

      for (File listFile : new File(MMOItems.plugin.getDataFolder() + "/modifiers").listFiles()) {
         YamlConfiguration config = YamlConfiguration.loadConfiguration(listFile);
         friendlyFeedbackProvider.activatePrefix(true, "Modifiers §8($r" + listFile.getPath() + "§8)");

         for (String key : config.getKeys(false)) {
            try {
               ModifierNode modifierNode = ModifierNode.fromConfig(key, config.get(key));
               this.modifierNodes.put(modifierNode.getId(), modifierNode);
            } catch (RuntimeException exception) {
               friendlyFeedbackProvider.log(FriendlyFeedbackCategory.INFORMATION, "Could not load '{0}': {1}", new String[]{key, exception.getMessage()});
            }
         }
      }

      friendlyFeedbackProvider.activatePrefix(true, "Item Templates");
      friendlyFeedbackProvider.log(FriendlyFeedbackCategory.INFORMATION, "Preloading item templates, please wait..", new String[0]);

      for (Type type : MMOItems.plugin.getTypes().getAll()) {
         FileConfiguration config = type.getConfigFile().getConfig();

         for (String key : config.getKeys(false)) {
            try {
               this.registerTemplate(new MMOItemTemplate(type, config.getConfigurationSection(key)));
            } catch (IllegalArgumentException exception) {
               friendlyFeedbackProvider.log(FriendlyFeedbackCategory.ERROR, "Could not preload item template '{0}': {1}", new String[]{key, exception.getMessage()});
            }
         }
      }

      friendlyFeedbackProvider.sendAllTo((org.bukkit.command.CommandSender) MMOItems.getConsole());
   }

   public void postloadObjects() {
      FriendlyFeedbackProvider friendlyFeedbackProvider = new FriendlyFeedbackProvider(FFPMMOItems.get());
      friendlyFeedbackProvider.activatePrefix(true, "Template Modifiers");
      friendlyFeedbackProvider.log(FriendlyFeedbackCategory.INFORMATION, "Loading template modifiers, please wait..", new String[0]);

      for (ModifierNode modifierNode : this.modifierNodes.values()) {
         try {
            modifierNode.getPostLoadAction().performAction();
         } catch (Exception exception) {
            friendlyFeedbackProvider.log(FriendlyFeedbackCategory.INFORMATION, "Could not post-load template modifier '{0}': {1}", new String[]{modifierNode.getId(), exception.getMessage()});
         }
      }

      friendlyFeedbackProvider.activatePrefix(true, "Item Templates");
      friendlyFeedbackProvider.log(FriendlyFeedbackCategory.INFORMATION, "Loading item templates, please wait...", new String[0]);
      this.templates.forEach(template -> {
         try {
            template.getPostLoadAction().performAction();
         } catch (Exception exception) {
            friendlyFeedbackProvider.activatePrefix(true, "Item Templates §8($r" + template.getType().getId() + "§8)");
            friendlyFeedbackProvider.log(FriendlyFeedbackCategory.INFORMATION, "Could not post-load item template '" + template.getId() + "': " + exception.getMessage(), new String[0]);
         }
      });
      friendlyFeedbackProvider.sendAllTo((org.bukkit.command.CommandSender) MMOItems.getConsole());
   }

   @Override
   public void reload() {
      this.templates.clear();
      this.modifierNodes.clear();
      this.preloadObjects();
      this.postloadObjects();
   }
}
