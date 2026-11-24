package net.Indyuce.mmoitems.stat.type;

import io.lumine.mythic.lib.api.item.ItemTag;
import io.lumine.mythic.lib.api.util.ui.FriendlyFeedbackCategory;
import io.lumine.mythic.lib.api.util.ui.FriendlyFeedbackProvider;
import io.lumine.mythic.lib.gson.JsonArray;
import io.lumine.mythic.lib.gson.JsonElement;
import io.lumine.mythic.lib.gson.JsonObject;
import io.lumine.mythic.lib.gson.JsonParser;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;
import java.util.Map.Entry;
import net.Indyuce.mmoitems.ItemStats;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.item.mmoitem.MMOItem;
import net.Indyuce.mmoitems.api.util.message.FFPMMOItems;
import net.Indyuce.mmoitems.stat.data.EnchantListData;
import net.Indyuce.mmoitems.stat.data.GemSocketsData;
import net.Indyuce.mmoitems.stat.data.GemstoneData;
import net.Indyuce.mmoitems.stat.data.type.Mergeable;
import net.Indyuce.mmoitems.stat.data.type.StatData;
import net.Indyuce.mmoitems.stat.data.type.UpgradeInfo;
import net.Indyuce.mmoitems.util.MMOUtils;
import org.apache.commons.lang.Validate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class StatHistory {
   private final ItemStat itemStat;
   private StatData originalData;
   private MMOItem parent;
   private HashMap<UUID, StatData> perModifierBonus = new HashMap<>();
   private ArrayList<StatData> perExternalData = new ArrayList<>();
   private HashMap<UUID, StatData> perGemstoneData = new HashMap<>();
   private static final String ENC_STAT = "Stat";
   private static final String ENC_OGS = "OGStory";
   private static final String ENC_GSS = "Gemstory";
   private static final String ENC_EXS = "Exstory";
   private static final String ENC_MOD = "Mod";

   public StatHistory(@NotNull MMOItem mmoItem, @NotNull ItemStat itemStat, @NotNull StatData statData) {
      this.itemStat = itemStat;
      this.originalData = statData;
      this.parent = mmoItem;
   }

   @NotNull
   public ItemStat getItemStat() {
      return this.itemStat;
   }

   @NotNull
   public MMOItem getMMOItem() {
      return this.parent;
   }

   @NotNull
   public StatData getOriginalData() {
      return this.originalData;
   }

   public boolean isEmpty() {
      if (this.getOriginalData() instanceof EnchantListData && ((EnchantListData)this.getOriginalData()).getEnchants().size() != 0) {
         return false;
      } else if (this.getAllGemstones().size() <= 0 && this.getExternalData().size() <= 0 && this.getAllModifiers().size() <= 0) {
         return !this.getOriginalData().isEmpty() || this.isUpgradeable() && this.getMMOItem().getUpgradeLevel() != 0
            ? this.getOriginalData().equals(this.getMMOItem().getData(this.getItemStat()))
            : true;
      } else {
         return false;
      }
   }

   @NotNull
   public StatHistory setParent(@NotNull MMOItem mmoItem) {
      this.parent = mmoItem;
      return this;
   }

   public void setOriginalData(@NotNull StatData statData) {
      Validate.notNull(statData, "Original data cannot be null");
      this.originalData = statData;
   }

   @Nullable
   public StatData getModifiersBonus(@NotNull UUID uuid) {
      return this.perModifierBonus.get(uuid);
   }

   public void registerModifierBonus(@NotNull UUID uuid, @NotNull StatData statData) {
      Validate.notNull(uuid, "Modifier UUID cannot be null");
      Validate.notNull(statData, "Stat data cannot be null");
      if (!statData.isEmpty()) {
         this.perModifierBonus.put(uuid, statData);
      }
   }

   public void removeModifierBonus(@NotNull UUID uuid) {
      this.perModifierBonus.remove(uuid);
   }

   @NotNull
   public ArrayList<UUID> getAllModifiers() {
      return new ArrayList<>(this.perModifierBonus.keySet());
   }

   public void clearModifiersBonus() {
      this.perModifierBonus.clear();
   }

   @Nullable
   public StatData getGemstoneData(@NotNull UUID uuid) {
      return this.perGemstoneData.get(uuid);
   }

   public void removeGemData(@NotNull UUID uuid) {
      this.perGemstoneData.remove(uuid);
   }

   @NotNull
   public ArrayList<UUID> getAllGemstones() {
      return new ArrayList<>(this.perGemstoneData.keySet());
   }

   public void registerGemstoneData(@NotNull UUID uuid, @NotNull StatData statData) {
      Validate.notNull(uuid, "Gemstone ID cannot be null");
      Validate.notNull(statData, "Stat data cannot be null");
      if (!statData.isEmpty()) {
         this.perGemstoneData.put(uuid, statData);
      }
   }

   public void clearGemstones() {
      this.perGemstoneData.clear();
   }

   @NotNull
   public ArrayList<StatData> getExternalData() {
      return this.perExternalData;
   }

   public void fuseExternalData() {
      StatData clearStatData = this.getItemStat().getClearStatData();

      for (StatData statData : this.getExternalData()) {
         ((Mergeable)clearStatData).mergeWith((Mergeable)statData);
      }

      this.getExternalData().clear();
      this.registerExternalData(clearStatData);
   }

   public void registerExternalData(@NotNull StatData statData) {
      Validate.notNull(statData, "Stat data cannot be null");
      if (!statData.isEmpty()) {
         this.perExternalData.add(statData);
      }
   }

   public void clearExternalData() {
      this.perExternalData.clear();
   }

   public void purgeGemstones() {
      GemSocketsData data = (GemSocketsData)this.getMMOItem().getData(ItemStats.GEM_SOCKETS);
      if (data == null) {
         this.perGemstoneData.clear();
      } else {
         HashMap map = new HashMap();

         for (GemstoneData gemstone : data.getGemstones()) {
            StatData statData = this.perGemstoneData.get(gemstone.getHistoricUUID());
            if (statData != null) {
               map.put(gemstone.getHistoricUUID(), statData);
            }
         }

         this.perGemstoneData = map;
      }
   }

   @NotNull
   public StatData recalculate(int count) {
      return this.recalculate(true, count);
   }

   @NotNull
   public StatData recalculateUnupgraded() {
      return this.recalculate(true, null);
   }

   private int findLevel(int count, UUID uuid) {
      for (GemstoneData gemstone : this.getMMOItem().getGemstones()) {
         if (gemstone.getHistoricUUID().equals(uuid)) {
            return gemstone.isScaling() ? gemstone.getLevel() : count;
         }
      }

      throw new IllegalArgumentException("Could not find level of gem " + uuid);
   }

   @NotNull
   public StatData recalculate(boolean flag, @Nullable Integer value) {
      if (flag) {
         this.purgeGemstones();
      }

      UpgradeInfo upgradeInfo = value != null && value != 0 && this.getItemStat() instanceof Upgradable
         ? this.getMMOItem().getUpgradeTemplate().getUpgradeInfo(this.getItemStat())
         : null;
      Mergeable mergeable = ((Mergeable)this.originalData).clone();

      for (StatData statData : this.perModifierBonus.values()) {
         mergeable.mergeWith((Mergeable)statData);
      }

      if (upgradeInfo != null) {
         mergeable = (Mergeable)((Upgradable)this.getItemStat()).apply(mergeable, upgradeInfo, value);
      }

      for (UUID key : this.perGemstoneData.keySet()) {
         Mergeable gemstoneData = (Mergeable)this.getGemstoneData(key);
         if (upgradeInfo != null) {
            int count = value - this.findLevel(value, key);
            gemstoneData = (Mergeable)((Upgradable)this.getItemStat()).apply(gemstoneData.clone(), upgradeInfo, count);
         }

         mergeable.mergeWith(gemstoneData);
      }

      for (StatData statData : this.getExternalData()) {
         mergeable.mergeWith((Mergeable)statData);
      }

      return mergeable;
   }

   @NotNull
   public JsonObject toJson() {
      JsonObject jsonObject = new JsonObject();
      jsonObject.addProperty("Stat", this.getItemStat().getId());
      if (!this.getOriginalData().isEmpty() || this.getItemStat() == ItemStats.ENCHANTS) {
         jsonObject.add("OGStory", ItemTag.compressTags(this.getItemStat().getAppliedNBT(this.getOriginalData())));
      }

      JsonArray jsonArray = new JsonArray();

      for (UUID allGemstone : this.getAllGemstones()) {
         JsonObject gemstoneJson = new JsonObject();
         JsonArray gemstoneTags = ItemTag.compressTags(this.getItemStat().getAppliedNBT(this.getGemstoneData(allGemstone)));
         gemstoneJson.add(allGemstone.toString(), gemstoneTags);
         jsonArray.add(gemstoneJson);
      }

      if (jsonArray.size() > 0) {
         jsonObject.add("Gemstory", jsonArray);
      }

      JsonArray modifiersJson = new JsonArray();

      for (StatData statData : this.getExternalData()) {
         if (!statData.isEmpty()) {
            modifiersJson.add(ItemTag.compressTags(this.getItemStat().getAppliedNBT(statData)));
         }
      }

      if (!modifiersJson.isEmpty()) {
         jsonObject.add("Exstory", modifiersJson);
      }

      JsonArray uuidModifiersArray = new JsonArray();

      for (UUID allModifier : this.getAllModifiers()) {
         JsonObject modifierJson = new JsonObject();
         JsonArray modifierTags = ItemTag.compressTags(this.getItemStat().getAppliedNBT(this.getModifiersBonus(allModifier)));
         modifierJson.add(allModifier.toString(), modifierTags);
         uuidModifiersArray.add(modifierJson);
      }

      if (uuidModifiersArray.size() > 0) {
         jsonObject.add("Mod", uuidModifiersArray);
      }

      return jsonObject;
   }

   @NotNull
   public String toNBTString() {
      return this.toJson().toString();
   }

   public boolean isUpgradeable() {
      return this.getMMOItem().hasUpgradeTemplate() && this.getMMOItem().getUpgradeTemplate().getUpgradeInfo(this.getItemStat()) != null;
   }

   @Nullable
   public static StatHistory fromJson(@NotNull MMOItem mmoItem, @NotNull JsonObject jsonObject) {
      JsonElement jsonElement = null;
      JsonElement gemstonesElement = null;
      JsonElement modifiersElement = null;
      JsonElement externalElement = null;
      if (jsonObject.has("Stat")) {
         JsonElement stat = jsonObject.get("Stat");
         if (jsonObject.has("OGStory")) {
            jsonElement = jsonObject.get("OGStory");
         }

         if (jsonObject.has("Gemstory")) {
            gemstonesElement = jsonObject.get("Gemstory");
         }

         if (jsonObject.has("Exstory")) {
            modifiersElement = jsonObject.get("Exstory");
         }

         if (jsonObject.has("Mod")) {
            externalElement = jsonObject.get("Mod");
         }

         if (!stat.isJsonPrimitive()) {
            return null;
         }

         if (jsonElement != null && !jsonElement.isJsonArray()) {
            return null;
         }

         if (gemstonesElement != null && !gemstonesElement.isJsonArray()) {
            return null;
         }

         if (modifiersElement != null && !modifiersElement.isJsonArray()) {
            return null;
         }

         if (externalElement != null && !externalElement.isJsonArray()) {
            return null;
         }

         String asString = stat.getAsJsonPrimitive().getAsString();
         ItemStat itemStat = MMOItems.plugin.getStats().get(asString);
         if (itemStat == null) {
            return null;
         }

         StatData statData;
         if (jsonElement != null) {
            ArrayList list = ItemTag.decompressTags(jsonElement.getAsJsonArray());
            statData = itemStat.getLoadedNBT(list);
         } else {
            statData = itemStat.getClearStatData();
         }

         if (statData == null) {
            return null;
         }

         StatHistory statHistory = new StatHistory(mmoItem, itemStat, statData);
         if (gemstonesElement != null) {
            for (JsonElement asJson : gemstonesElement.getAsJsonArray()) {
               if (asJson.isJsonObject()) {
                  JsonObject asJsonObject = asJson.getAsJsonObject();

                  for (Entry entry : asJsonObject.entrySet()) {
                     String key = (String)entry.getKey();
                     UUID uuid = MMOUtils.UUIDFromString(key);
                     JsonElement entryElement = (JsonElement)entry.getValue();
                     if (entryElement.isJsonArray() && uuid != null) {
                        ArrayList list = ItemTag.decompressTags(entryElement.getAsJsonArray());
                        StatData loadedNBT = itemStat.getLoadedNBT(list);
                        if (loadedNBT != null) {
                           statHistory.registerGemstoneData(uuid, loadedNBT);
                        }
                     }
                  }
               }
            }
         }

         if (modifiersElement != null) {
            for (JsonElement asJson : modifiersElement.getAsJsonArray()) {
               if (asJson.isJsonArray()) {
                  ArrayList list = ItemTag.decompressTags(asJson.getAsJsonArray());
                  StatData loadedNBT = itemStat.getLoadedNBT(list);
                  if (loadedNBT != null) {
                     statHistory.registerExternalData(loadedNBT);
                  }
               }
            }
         }

         if (externalElement != null) {
            for (JsonElement asJson : externalElement.getAsJsonArray()) {
               if (asJson.isJsonObject()) {
                  JsonObject asJsonObject = asJson.getAsJsonObject();

                  for (Entry entry : asJsonObject.entrySet()) {
                     String key = (String)entry.getKey();
                     UUID uuid = MMOUtils.UUIDFromString(key);
                     JsonElement entryElement = (JsonElement)entry.getValue();
                     if (entryElement.isJsonArray() && uuid != null) {
                        ArrayList list = ItemTag.decompressTags(entryElement.getAsJsonArray());
                        StatData loadedNBT = itemStat.getLoadedNBT(list);
                        if (loadedNBT != null) {
                           statHistory.registerModifierBonus(uuid, loadedNBT);
                        }
                     }
                  }
               }
            }
         }

         return statHistory;
      } else {
         return null;
      }
   }

   @Nullable
   public static StatHistory fromNBTString(@NotNull MMOItem mmoItem, @NotNull String text) {
      try {
         JsonParser jsonParser = new JsonParser();
         JsonObject asJsonObject = jsonParser.parse(text).getAsJsonObject();
         return fromJson(mmoItem, asJsonObject);
      } catch (Throwable exception) {
         FriendlyFeedbackProvider friendlyFeedbackProvider = new FriendlyFeedbackProvider(FFPMMOItems.get());
         friendlyFeedbackProvider.activatePrefix(true, "Stat History");
         friendlyFeedbackProvider.log(
            FriendlyFeedbackCategory.ERROR, "Could not get stat history: $f{0}$b at $f{1}", new String[]{exception.getMessage(), exception.getStackTrace()[0].toString()}
         );
         friendlyFeedbackProvider.sendTo(FriendlyFeedbackCategory.ERROR, MMOItems.getConsole());
         return null;
      }
   }

   @NotNull
   public StatHistory clone() {
      StatHistory statHistory = new StatHistory(this.getMMOItem(), this.getItemStat(), ((Mergeable)this.getOriginalData()).clone());
      this.perGemstoneData.forEach((var1x, statData) -> statHistory.perGemstoneData.put(var1x, ((Mergeable)statData).clone()));
      this.perExternalData.forEach(var1x -> statHistory.perExternalData.add(((Mergeable)var1x).clone()));
      this.perModifierBonus.forEach((var1x, statData) -> statHistory.perModifierBonus.put(var1x, ((Mergeable)statData).clone()));
      return statHistory;
   }

   public void assimilate(@NotNull StatHistory statHistory) {
      if (statHistory.getItemStat().getNBTPath().equals(this.getItemStat().getNBTPath())) {
         for (UUID allGemstone : statHistory.getAllGemstones()) {
            this.registerGemstoneData(allGemstone, statHistory.getGemstoneData(allGemstone));
         }

         for (StatData statData : statHistory.getExternalData()) {
            this.registerExternalData(statData);
         }

         for (UUID allModifier : statHistory.getAllModifiers()) {
            this.registerModifierBonus(allModifier, statHistory.getModifiersBonus(allModifier));
         }
      }
   }

   @Deprecated
   public StatHistory clone(@NotNull MMOItem mmoItem) {
      StatHistory statHistory = this.clone();
      statHistory.setParent(mmoItem);
      return statHistory;
   }

   @Deprecated
   @NotNull
   public static StatHistory from(@NotNull MMOItem mmoItem, @NotNull ItemStat itemStat) {
      return mmoItem.computeStatHistory(itemStat);
   }

   @Deprecated
   @NotNull
   public static StatHistory from(@NotNull MMOItem mmoItem, @NotNull ItemStat itemStat, boolean flag) {
      if (flag) {
         StatHistory statHistory = new StatHistory(mmoItem, itemStat, Objects.requireNonNull(mmoItem.getData(itemStat)));
         mmoItem.setStatHistory(itemStat, statHistory);
         return statHistory;
      } else {
         return mmoItem.computeStatHistory(itemStat);
      }
   }

   @Deprecated
   @NotNull
   public StatData recalculateUnupgraded(boolean flag) {
      return this.recalculate(flag, null);
   }

   @Deprecated
   public boolean isClear() {
      return this.isEmpty();
   }

   @Deprecated
   public void consolidateEXSH() {
      this.fuseExternalData();
   }
}
