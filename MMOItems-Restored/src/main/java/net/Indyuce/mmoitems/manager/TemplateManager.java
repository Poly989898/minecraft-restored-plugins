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

   public boolean hasTemplate(@Nullable Type var1, @Nullable String var2) {
      return var1 != null && var2 != null ? this.templates.hasValue(var1, var2) : false;
   }

   public boolean hasTemplate(@Nullable NBTItem var1) {
      return var1 == null ? false : this.hasTemplate(Type.get(var1.getType()), var1.getString("MMOITEMS_ITEM_ID"));
   }

   @Nullable
   public MMOItemTemplate getTemplate(@Nullable Type var1, @Nullable String var2) {
      return var1 != null && var2 != null ? this.templates.getValue(var1, var2) : null;
   }

   @Nullable
   public MMOItemTemplate getTemplate(@Nullable NBTItem var1) {
      return var1 == null ? null : this.getTemplate(Type.get(var1.getType()), var1.getString("MMOITEMS_ITEM_ID"));
   }

   @NotNull
   public MMOItemTemplate getTemplateOrThrow(@Nullable Type var1, @Nullable String var2) {
      Validate.isTrue(var1 != null && this.hasTemplate(var1, var2), "Could not find a template with ID '" + var2 + "'");
      return this.templates.getValue(var1, var2);
   }

   @NotNull
   public Collection<MMOItemTemplate> getTemplates(@NotNull Type var1) {
      return this.templates.collectValues(var1);
   }

   @NotNull
   public ArrayList<String> getTemplateNames(@NotNull Type var1) {
      ArrayList var2 = new ArrayList();

      for (MMOItemTemplate var4 : this.templates.collectValues(var1)) {
         var2.add(var4.getId());
      }

      return var2;
   }

   public void registerTemplate(@NotNull MMOItemTemplate var1) {
      Validate.notNull(var1, "MMOItem template cannot be null");
      this.templates.setValue(var1.getType(), var1.getId(), var1);
   }

   public void unregisterTemplate(@NotNull Type var1, @NotNull String var2) {
      this.templates.removeValue(var1, var2);
   }

   public void deleteTemplate(@NotNull Type var1, @NotNull String var2) {
      this.unregisterTemplate(var1, var2);
      ConfigFile var3 = var1.getConfigFile();
      var3.getConfig().set(var2, null);
      var3.save();
   }

   public MMOItemTemplate requestTemplateUpdate(@NotNull Type var1, @NotNull String var2) {
      this.templates.removeValue(var1, var2);

      try {
         MMOItemTemplate var3 = new MMOItemTemplate(var1, var1.getConfigFile().getConfig().getConfigurationSection(var2));
         var3.getPostLoadAction().performAction();
         this.registerTemplate(var3);
         return var3;
      } catch (IllegalArgumentException var4) {
         MMOItems.plugin.getLogger().log(Level.SEVERE, "An error occurred while trying to reload item gen template '" + var2 + "': " + var4.getMessage());
         return null;
      }
   }

   public Collection<MMOItemTemplate> collectTemplates() {
      return this.templates.collectValues();
   }

   @Deprecated
   public boolean hasModifier(String var1) {
      return this.modifierNodes.get(var1) != null;
   }

   @Deprecated
   @Nullable
   public ModifierNode getModifier(String var1) {
      return this.modifierNodes.get(var1);
   }

   @Deprecated
   public Collection<ModifierNode> getModifiers() {
      return this.modifierNodes.values();
   }

   public boolean hasModifierNode(@NotNull String var1) {
      return this.modifierNodes.containsKey(var1);
   }

   @Nullable
   public ModifierNode getModifierNode(@NotNull String var1) {
      return this.modifierNodes.get(var1);
   }

   @NotNull
   public Collection<ModifierNode> getModifierNodes() {
      return this.modifierNodes.values();
   }

   @Nullable
   public ItemTier rollTier() {
      double var1 = 0.0;

      for (ItemTier var4 : MMOItems.plugin.getTiers().getAll()) {
         if (var1 >= 1.0 || RANDOM.nextDouble() < var4.getGenerationChance() / (1.0 - var1)) {
            return var4;
         }

         var1 += var4.getGenerationChance();
      }

      return null;
   }

   public int rollLevel(int var1) {
      double var2 = MMOItems.plugin.getLanguage().levelSpread;
      double var4 = RANDOM.nextGaussian() * var2 * 0.7 + var1;
      var4 = Math.max(Math.min(var4, var1 + var2), Math.max(1.0, var1 - var2));
      return (int)var4;
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
      FriendlyFeedbackProvider var1 = new FriendlyFeedbackProvider(FFPMMOItems.get());
      var1.activatePrefix(true, "Template Modifiers");
      var1.log(FriendlyFeedbackCategory.INFORMATION, "Preloading template modifiers, please wait..", new String[0]);

      for (File var5 : new File(MMOItems.plugin.getDataFolder() + "/modifiers").listFiles()) {
         YamlConfiguration var6 = YamlConfiguration.loadConfiguration(var5);
         var1.activatePrefix(true, "Modifiers §8($r" + var5.getPath() + "§8)");

         for (String var8 : var6.getKeys(false)) {
            try {
               ModifierNode var9 = ModifierNode.fromConfig(var8, var6.get(var8));
               this.modifierNodes.put(var9.getId(), var9);
            } catch (RuntimeException var11) {
               var1.log(FriendlyFeedbackCategory.INFORMATION, "Could not load '{0}': {1}", new String[]{var8, var11.getMessage()});
            }
         }
      }

      var1.activatePrefix(true, "Item Templates");
      var1.log(FriendlyFeedbackCategory.INFORMATION, "Preloading item templates, please wait..", new String[0]);

      for (Type var13 : MMOItems.plugin.getTypes().getAll()) {
         FileConfiguration var14 = var13.getConfigFile().getConfig();

         for (String var16 : var14.getKeys(false)) {
            try {
               this.registerTemplate(new MMOItemTemplate(var13, var14.getConfigurationSection(var16)));
            } catch (IllegalArgumentException var10) {
               var1.log(FriendlyFeedbackCategory.ERROR, "Could not preload item template '{0}': {1}", new String[]{var16, var10.getMessage()});
            }
         }
      }

      var1.sendAllTo((org.bukkit.command.CommandSender) MMOItems.getConsole());
   }

   public void postloadObjects() {
      FriendlyFeedbackProvider var1 = new FriendlyFeedbackProvider(FFPMMOItems.get());
      var1.activatePrefix(true, "Template Modifiers");
      var1.log(FriendlyFeedbackCategory.INFORMATION, "Loading template modifiers, please wait..", new String[0]);

      for (ModifierNode var3 : this.modifierNodes.values()) {
         try {
            var3.getPostLoadAction().performAction();
         } catch (Exception var5) {
            var1.log(FriendlyFeedbackCategory.INFORMATION, "Could not post-load template modifier '{0}': {1}", new String[]{var3.getId(), var5.getMessage()});
         }
      }

      var1.activatePrefix(true, "Item Templates");
      var1.log(FriendlyFeedbackCategory.INFORMATION, "Loading item templates, please wait...", new String[0]);
      this.templates.forEach(var1x -> {
         try {
            var1x.getPostLoadAction().performAction();
         } catch (Exception var3x) {
            var1.activatePrefix(true, "Item Templates §8($r" + var1x.getType().getId() + "§8)");
            var1.log(FriendlyFeedbackCategory.INFORMATION, "Could not post-load item template '" + var1x.getId() + "': " + var3x.getMessage(), new String[0]);
         }
      });
      var1.sendAllTo((org.bukkit.command.CommandSender) MMOItems.getConsole());
   }

   @Override
   public void reload() {
      this.templates.clear();
      this.modifierNodes.clear();
      this.preloadObjects();
      this.postloadObjects();
   }
}
