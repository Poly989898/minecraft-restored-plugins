package net.Indyuce.mmoitems.api;

import io.lumine.mythic.lib.api.util.ui.FriendlyFeedbackCategory;
import io.lumine.mythic.lib.api.util.ui.FriendlyFeedbackProvider;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import net.Indyuce.mmoitems.ItemStats;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.item.mmoitem.MMOItem;
import net.Indyuce.mmoitems.api.util.message.FFPMMOItems;
import net.Indyuce.mmoitems.stat.Enchants;
import net.Indyuce.mmoitems.stat.data.UpgradeData;
import net.Indyuce.mmoitems.stat.data.type.Mergeable;
import net.Indyuce.mmoitems.stat.data.type.UpgradeInfo;
import net.Indyuce.mmoitems.stat.type.ItemStat;
import net.Indyuce.mmoitems.stat.type.StatHistory;
import net.Indyuce.mmoitems.stat.type.Upgradable;
import org.apache.commons.lang.Validate;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class UpgradeTemplate {
   @NotNull
   private final String id;
   @NotNull
   private final Map<ItemStat, UpgradeInfo> perStatUpgradeInfos = new HashMap<>();

   public UpgradeTemplate(@NotNull ConfigurationSection var1) {
      Validate.notNull(var1, FriendlyFeedbackProvider.quickForConsole(FFPMMOItems.get(), "You must specify a config section.", new String[0]));
      this.id = var1.getName().toLowerCase().replace("_", "-").replace(" ", "-");
      FriendlyFeedbackProvider var2 = new FriendlyFeedbackProvider(FFPMMOItems.get());
      var2.activatePrefix(true, "Upgrade Template $i&o" + var1.getName());

      for (String var4 : var1.getKeys(false)) {
         String var5 = var4.toUpperCase().replace("-", "_");
         ItemStat var6 = MMOItems.plugin.getStats().get(var5);
         if (var6 == null) {
            var2.log(FriendlyFeedbackCategory.ERROR, "Stat '$r{0}$b' $fnot found$b.", new String[]{var5});
         } else if (!(var6 instanceof Upgradable)) {
            var2.log(FriendlyFeedbackCategory.ERROR, "Stat $r{0}$b is $fnot upgradeable$b.", new String[]{var6.getId()});
         } else if (!(var6.getClearStatData() instanceof Mergeable)) {
            var2.log(
               FriendlyFeedbackCategory.ERROR,
               "Stat Data used by $r{0}$b is $fnot mergeable$b, and thus it cannot be upgradeable. Contact the dev of this ItemStat.",
               new String[]{var6.getId()}
            );
         } else {
            try {
               this.perStatUpgradeInfos.put(var6, ((Upgradable)var6).loadUpgradeInfo(var1.get(var4)));
            } catch (IllegalArgumentException var8) {
               var2.log(FriendlyFeedbackCategory.ERROR, var8.getMessage(), new String[0]);
            }
         }
      }

      var2.sendTo(FriendlyFeedbackCategory.ERROR, MMOItems.getConsole());
   }

   @NotNull
   public String getId() {
      return this.id;
   }

   @NotNull
   public Set<ItemStat> getKeys() {
      return this.perStatUpgradeInfos.keySet();
   }

   @Nullable
   public UpgradeInfo getUpgradeInfo(@NotNull ItemStat var1) {
      return this.perStatUpgradeInfos.get(var1);
   }

   public void upgrade(@NotNull MMOItem var1) {
      this.upgradeTo(var1, var1.getUpgradeLevel() + 1);
   }

   public void upgradeTo(@NotNull MMOItem var1, int var2) {
      Enchants.separateEnchantments(var1);
      UpgradeData var3;
      if (var1.hasData(ItemStats.UPGRADE)) {
         var3 = (UpgradeData)var1.getData(ItemStats.UPGRADE);
      } else {
         var3 = new UpgradeData(null, null, false, false, 0, 0, 100.0);
      }

      var3.setLevel(var2);
      var1.setData(ItemStats.UPGRADE, var3);

      for (ItemStat var5 : this.perStatUpgradeInfos.keySet()) {
         ((Upgradable)var5).preprocess(var1);
         StatHistory var6 = var1.computeStatHistory(var5);
         ((Upgradable)var5).midprocess(var1);
         var1.setData(var5, var6.recalculate(var2));
         ((Upgradable)var5).postprocess(var1);
      }
   }

   public static boolean isDisplayingUpgrades() {
      return MMOItems.plugin.getConfig().getBoolean("item-upgrading.display-stat-changes", false);
   }

   @NotNull
   public static String getUpgradeChangeSuffix(@NotNull String var0, boolean var1) {
      String var2 = Objects.requireNonNull(MMOItems.plugin.getConfig().getString("item-upgrading.stat-change-suffix", " &8(<p>#stat#&8)"));
      String var3 = Objects.requireNonNull(MMOItems.plugin.getConfig().getString("item-upgrading.stat-change-positive", "&a"));
      String var4 = Objects.requireNonNull(MMOItems.plugin.getConfig().getString("item-upgrading.stat-change-negative", "&c"));
      return var1 ? var2.replace("<p>", var4).replace("#stat#", var0) : var2.replace("<p>", var3).replace("#stat#", var0);
   }
}
