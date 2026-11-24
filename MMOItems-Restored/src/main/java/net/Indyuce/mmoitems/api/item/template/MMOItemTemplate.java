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
      var1x -> {
         FriendlyFeedbackProvider var2x = new FriendlyFeedbackProvider(FFPMMOItems.get());
         var2x.activatePrefix(true, this.getType().getId() + " " + this.getId());
         if (var1x.contains("option")) {
            if (var1x.contains("option.capacity", true)) {
               try {
                  this.modifierCapacity = new NumericStatFormula(var1x.getConfigurationSection("option.capacity"));
               } catch (RuntimeException var10) {
                  var2x.log(FriendlyFeedbackCategory.ERROR, "Could not load modifier capacity: {0}", new String[]{var10.getMessage()});
               }
            }

            for (MMOItemTemplate.TemplateOption var6 : MMOItemTemplate.TemplateOption.values()) {
               if (var1x.getBoolean("option." + var6.name().toLowerCase().replace("_", "-"))) {
                  this.options.add(var6);
               }
            }
         }

         try {
            this.modifierGroup = var1x.contains("modifiers") ? new ModifierNode(this.getId(), var1x) : null;
            if (this.modifierGroup != null) {
               this.modifierGroup.getPostLoadAction().performAction();
            }
         } catch (Exception var9) {
            var2x.log(FriendlyFeedbackCategory.ERROR, "Could not load modifier group: {0}", new String[]{var9.getMessage()});
         }

         Validate.notNull(
            var1x.getConfigurationSection("base"), FriendlyFeedbackProvider.quickForConsole(FFPMMOItems.get(), "Could not find base item data", new String[0])
         );

         for (String var12 : var1x.getConfigurationSection("base").getKeys(false)) {
            try {
               String var13 = UtilityMethods.enumName(var12);
               ItemStat var14 = MMOItems.plugin.getStats().get(var13);
               Validate.notNull(
                  var14, FriendlyFeedbackProvider.quickForConsole(FFPMMOItems.get(), "Could not find stat with ID '$i{0}$b'", new String[]{var13})
               );
               RandomStatData var7 = var14.whenInitialized(var1x.get("base." + var12));
               if (var7 != null) {
                  this.base.put(var14, var7);
               }
            } catch (IllegalArgumentException var8) {
               var2x.log(FriendlyFeedbackCategory.INFORMATION, "Could not load base item data '$f{0}$b': {1}", new String[]{var12, var8.getMessage()});
            }
         }

         var2x.sendTo(FriendlyFeedbackCategory.INFORMATION, MMOItems.getConsole());
      }
   );

   public MMOItemTemplate(Type var1, String var2) {
      this.type = var1;
      this.id = var2;
      this.revId = 1;
   }

   public MMOItemTemplate(Type var1, ConfigurationSection var2) {
      Validate.notNull(var2, "Could not load template config");
      this.postLoadAction.cacheConfig(var2);
      this.type = var1;
      this.id = var2.getName().toUpperCase().replace("-", "_").replace(" ", "_");
      this.revId = var2.getInt("base.revision-id", 1);
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
      HashMap var1 = new HashMap();
      if (this.modifierGroup != null) {
         this.exploreMap(var1, this.modifierGroup);
      }

      return var1;
   }

   private void exploreMap(Map<String, ModifierNode> var1, ModifierNode var2) {
      var2.getChildren().forEach(var2x -> this.exploreMap(var1, var2x));
      var1.put(var2.getId(), var2);
   }

   @Deprecated
   public boolean hasModifier(String var1) {
      return this.getModifiers().containsKey(var1);
   }

   @Nullable
   @Deprecated
   public ModifierNode getModifier(String var1) {
      return this.getModifiers().get(var1);
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

   public boolean hasOption(MMOItemTemplate.TemplateOption var1) {
      return this.options.contains(var1);
   }

   public MMOItemBuilder newBuilder() {
      return this.newBuilder((RPGPlayer)null);
   }

   public MMOItemBuilder newBuilder(@Nullable Player var1) {
      return this.newBuilder(var1 == null ? null : PlayerData.get(var1).getRPG());
   }

   public MMOItemBuilder newBuilder(@Nullable PlayerData var1) {
      return this.newBuilder(var1 == null ? null : var1.getRPG());
   }

   public MMOItemBuilder newBuilder(@Nullable RPGPlayer var1) {
      return this.newBuilder(var1, false);
   }

   @NotNull
   public MMOItemBuilder newBuilder(@Nullable RPGPlayer var1, boolean var2) {
      if (var1 == null) {
         return this.newBuilder(0, null);
      }

      int var3 = this.hasOption(MMOItemTemplate.TemplateOption.LEVEL_ITEM) ? MMOItems.plugin.getTemplates().rollLevel(var1.getLevel()) : 0;
      ItemTier var4 = this.hasOption(MMOItemTemplate.TemplateOption.TIERED) ? MMOItems.plugin.getTemplates().rollTier() : null;
      return new MMOItemBuilder(this, var3, var4, var2);
   }

   @NotNull
   public MMOItemBuilder newBuilder(int var1, @Nullable ItemTier var2) {
      return new MMOItemBuilder(this, var1, var2);
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
