package net.Indyuce.mmoitems.api.item.template;

import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.api.util.ui.FriendlyFeedbackCategory;
import io.lumine.mythic.lib.api.util.ui.FriendlyFeedbackProvider;
import io.lumine.mythic.lib.util.PostLoadAction;
import io.lumine.mythic.lib.util.PreloadedObject;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.ItemTier;
import net.Indyuce.mmoitems.api.Type;
import net.Indyuce.mmoitems.api.item.ItemReference;
import net.Indyuce.mmoitems.api.item.build.MMOItemBuilder;
import net.Indyuce.mmoitems.api.player.PlayerData;
import net.Indyuce.mmoitems.api.player.RPGPlayer;
import net.Indyuce.mmoitems.api.util.NumericStatFormula;
import net.Indyuce.mmoitems.api.util.message.FFPMMOItems;
import net.Indyuce.mmoitems.stat.data.random.RandomStatData;
import net.Indyuce.mmoitems.stat.type.ItemStat;
import org.apache.commons.lang.Validate;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class MMOItemTemplate implements ItemReference, PreloadedObject {
   private final Type type;
   private final String id;
   private final int revId;
   private final Map<ItemStat, RandomStatData> base = new HashMap<>();
   @Nullable
   private NumericStatFormula modifierCapacity;
   @Nullable
   private ModifierNode modifierGroup;
   private final Set<MMOItemTemplate.TemplateOption> options = new HashSet<>();
   private final PostLoadAction postLoadAction = new PostLoadAction(
      config -> {
         FriendlyFeedbackProvider feedback = new FriendlyFeedbackProvider(FFPMMOItems.get());
         feedback.activatePrefix(true, this.getType().getId() + " " + this.getId());
         if (config.contains("option")) {
            if (config.contains("option.capacity", true)) {
               try {
                  this.modifierCapacity = new NumericStatFormula(config.getConfigurationSection("option.capacity"));
               } catch (RuntimeException exception) {
                  feedback.log(FriendlyFeedbackCategory.ERROR, "Could not load modifier capacity: {0}", new String[]{exception.getMessage()});
               }
            }

            for (MMOItemTemplate.TemplateOption templateOption : MMOItemTemplate.TemplateOption.values()) {
               if (config.getBoolean("option." + templateOption.name().toLowerCase().replace("_", "-"))) {
                  this.options.add(templateOption);
               }
            }
         }

         try {
            this.modifierGroup = config.contains("modifiers") ? new ModifierNode(this.getId(), config) : null;
            if (this.modifierGroup != null) {
               this.modifierGroup.getPostLoadAction().performAction();
            }
         } catch (Exception exception) {
            feedback.log(FriendlyFeedbackCategory.ERROR, "Could not load modifier group: {0}", new String[]{exception.getMessage()});
         }

         Validate.notNull(
            config.getConfigurationSection("base"), FriendlyFeedbackProvider.quickForConsole(FFPMMOItems.get(), "Could not find base item data", new String[0])
         );

         for (String key : config.getConfigurationSection("base").getKeys(false)) {
            try {
               String text = UtilityMethods.enumName(key);
               ItemStat itemStat = MMOItems.plugin.getStats().get(text);
               Validate.notNull(
                  itemStat, FriendlyFeedbackProvider.quickForConsole(FFPMMOItems.get(), "Could not find stat with ID '$i{0}$b'", new String[]{text})
               );
               RandomStatData randomStatData = itemStat.whenInitialized(config.get("base." + key));
               if (randomStatData != null) {
                  this.base.put(itemStat, randomStatData);
               }
            } catch (IllegalArgumentException exception) {
               feedback.log(FriendlyFeedbackCategory.INFORMATION, "Could not load base item data '$f{0}$b': {1}", new String[]{key, exception.getMessage()});
            }
         }

         feedback.sendTo(FriendlyFeedbackCategory.INFORMATION, MMOItems.getConsole());
      }
   );

   public MMOItemTemplate(Type type, String text) {
      this.type = type;
      this.id = text;
      this.revId = 1;
   }

   public MMOItemTemplate(Type type, ConfigurationSection section) {
      Validate.notNull(section, "Could not load template config");
      this.postLoadAction.cacheConfig(section);
      this.type = type;
      this.id = section.getName().toUpperCase().replace("-", "_").replace(" ", "_");
      this.revId = section.getInt("base.revision-id", 1);
   }

   @NotNull
   public PostLoadAction getPostLoadAction() {
      return this.postLoadAction;
   }

   @NotNull
   public Map<ItemStat, RandomStatData> getBaseItemData() {
      return this.base;
   }

   @Nullable
   public ModifierNode getModifierGroup() {
      return this.modifierGroup;
   }

   public boolean hasModifierGroup() {
      return this.modifierGroup != null;
   }

   @Nullable
   public NumericStatFormula getModifierCapacity() {
      return this.modifierCapacity;
   }

   public boolean hasModifierCapacity() {
      return this.modifierCapacity != null;
   }

   @Deprecated
   @NotNull
   public Map<String, ModifierNode> getModifiers() {
      HashMap map = new HashMap();
      if (this.modifierGroup != null) {
         this.exploreMap(map, this.modifierGroup);
      }

      return map;
   }

   private void exploreMap(Map<String, ModifierNode> map, ModifierNode modifierNode) {
      modifierNode.getChildren().forEach(child -> this.exploreMap(map, child));
      map.put(modifierNode.getId(), modifierNode);
   }

   @Deprecated
   public boolean hasModifier(String text) {
      return this.getModifiers().containsKey(text);
   }

   @Nullable
   @Deprecated
   public ModifierNode getModifier(String text) {
      return this.getModifiers().get(text);
   }

   @Override
   public Type getType() {
      return this.type;
   }

   @Override
   public String getId() {
      return this.id;
   }

   public int getRevisionId() {
      return this.revId;
   }

   public boolean hasOption(MMOItemTemplate.TemplateOption templateOption) {
      return this.options.contains(templateOption);
   }

   public MMOItemBuilder newBuilder() {
      return this.newBuilder((RPGPlayer)null);
   }

   public MMOItemBuilder newBuilder(@Nullable Player player) {
      return this.newBuilder(player == null ? null : PlayerData.get(player).getRPG());
   }

   public MMOItemBuilder newBuilder(@Nullable PlayerData playerData) {
      return this.newBuilder(playerData == null ? null : playerData.getRPG());
   }

   public MMOItemBuilder newBuilder(@Nullable RPGPlayer rpgPlayer) {
      return this.newBuilder(rpgPlayer, false);
   }

   @NotNull
   public MMOItemBuilder newBuilder(@Nullable RPGPlayer rpgPlayer, boolean flag) {
      if (rpgPlayer == null) {
         return this.newBuilder(0, null);
      }

      int count = this.hasOption(MMOItemTemplate.TemplateOption.LEVEL_ITEM) ? MMOItems.plugin.getTemplates().rollLevel(rpgPlayer.getLevel()) : 0;
      ItemTier itemTier = this.hasOption(MMOItemTemplate.TemplateOption.TIERED) ? MMOItems.plugin.getTemplates().rollTier() : null;
      return new MMOItemBuilder(this, count, itemTier, flag);
   }

   @NotNull
   public MMOItemBuilder newBuilder(int count, @Nullable ItemTier itemTier) {
      return new MMOItemBuilder(this, count, itemTier);
   }

   @Deprecated
   public int getCraftedAmount() {
      return 1;
   }

   public enum TemplateOption {
      ROLL_MODIFIER_CHECK_ORDER,
      TIERED,
      LEVEL_ITEM;
   }
}
