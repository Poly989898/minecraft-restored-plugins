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

   public UpgradeTemplate(@NotNull ConfigurationSection section) {
      Validate.notNull(section, FriendlyFeedbackProvider.quickForConsole(FFPMMOItems.get(), "You must specify a config section.", new String[0]));
      this.id = section.getName().toLowerCase().replace("_", "-").replace(" ", "-");
      FriendlyFeedbackProvider friendlyFeedbackProvider = new FriendlyFeedbackProvider(FFPMMOItems.get());
      friendlyFeedbackProvider.activatePrefix(true, "Upgrade Template $i&o" + section.getName());

      for (String key : section.getKeys(false)) {
         String text = key.toUpperCase().replace("-", "_");
         ItemStat itemStat = MMOItems.plugin.getStats().get(text);
         if (itemStat == null) {
            friendlyFeedbackProvider.log(FriendlyFeedbackCategory.ERROR, "Stat '$r{0}$b' $fnot found$b.", new String[]{text});
         } else if (!(itemStat instanceof Upgradable)) {
            friendlyFeedbackProvider.log(FriendlyFeedbackCategory.ERROR, "Stat $r{0}$b is $fnot upgradeable$b.", new String[]{itemStat.getId()});
         } else if (!(itemStat.getClearStatData() instanceof Mergeable)) {
            friendlyFeedbackProvider.log(
               FriendlyFeedbackCategory.ERROR,
               "Stat Data used by $r{0}$b is $fnot mergeable$b, and thus it cannot be upgradeable. Contact the dev of this ItemStat.",
               new String[]{itemStat.getId()}
            );
         } else {
            try {
               this.perStatUpgradeInfos.put(itemStat, ((Upgradable)itemStat).loadUpgradeInfo(section.get(key)));
            } catch (IllegalArgumentException exception) {
               friendlyFeedbackProvider.log(FriendlyFeedbackCategory.ERROR, exception.getMessage(), new String[0]);
            }
         }
      }

      friendlyFeedbackProvider.sendTo(FriendlyFeedbackCategory.ERROR, MMOItems.getConsole());
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
   public UpgradeInfo getUpgradeInfo(@NotNull ItemStat itemStat) {
      return this.perStatUpgradeInfos.get(itemStat);
   }

   public void upgrade(@NotNull MMOItem mmoItem) {
      this.upgradeTo(mmoItem, mmoItem.getUpgradeLevel() + 1);
   }

   public void upgradeTo(@NotNull MMOItem mmoItem, int count) {
      Enchants.separateEnchantments(mmoItem);
      UpgradeData upgradeData;
      if (mmoItem.hasData(ItemStats.UPGRADE)) {
         upgradeData = (UpgradeData)mmoItem.getData(ItemStats.UPGRADE);
      } else {
         upgradeData = new UpgradeData(null, null, false, false, 0, 0, 100.0);
      }

      upgradeData.setLevel(count);
      mmoItem.setData(ItemStats.UPGRADE, upgradeData);

      for (ItemStat key : this.perStatUpgradeInfos.keySet()) {
         ((Upgradable)key).preprocess(mmoItem);
         StatHistory statHistory = mmoItem.computeStatHistory(key);
         ((Upgradable)key).midprocess(mmoItem);
         mmoItem.setData(key, statHistory.recalculate(count));
         ((Upgradable)key).postprocess(mmoItem);
      }
   }

   public static boolean isDisplayingUpgrades() {
      return MMOItems.plugin.getConfig().getBoolean("item-upgrading.display-stat-changes", false);
   }

   @NotNull
   public static String getUpgradeChangeSuffix(@NotNull String text, boolean flag) {
      String changeSuffix = Objects.requireNonNull(MMOItems.plugin.getConfig().getString("item-upgrading.stat-change-suffix", " &8(<p>#stat#&8)"));
      String positivePrefix = Objects.requireNonNull(MMOItems.plugin.getConfig().getString("item-upgrading.stat-change-positive", "&a"));
      String negativePrefix = Objects.requireNonNull(MMOItems.plugin.getConfig().getString("item-upgrading.stat-change-negative", "&c"));
      return flag ? changeSuffix.replace("<p>", negativePrefix).replace("#stat#", text) : changeSuffix.replace("<p>", positivePrefix).replace("#stat#", text);
   }
}
