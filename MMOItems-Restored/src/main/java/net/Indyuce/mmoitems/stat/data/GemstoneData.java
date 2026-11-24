package net.Indyuce.mmoitems.stat.data;

import io.lumine.mythic.lib.gson.JsonElement;
import io.lumine.mythic.lib.gson.JsonObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import net.Indyuce.mmoitems.ItemStats;
import net.Indyuce.mmoitems.api.item.mmoitem.LiveMMOItem;
import net.Indyuce.mmoitems.stat.type.ItemStat;
import net.Indyuce.mmoitems.util.MMOUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GemstoneData {
   private final Set<AbilityData> abilities = new HashSet<>();
   private final List<PotionEffectData> effects = new ArrayList<>();
   private final Map<ItemStat, Double> stats = new HashMap<>();
   private final String name;
   @NotNull
   private final UUID historicUUID;
   @Nullable
   private final String mmoitemType;
   @Nullable
   private final String mmoitemID;
   @Nullable
   private String socketColor;
   @Nullable
   private Integer levelPut;

   public GemstoneData cloneGem() {
      GemstoneData gemstoneData = new GemstoneData(this.getName(), this.getMMOItemType(), this.getMMOItemID(), this.getSocketColor(), this.getHistoricUUID());

      for (AbilityData ability : this.abilities) {
         gemstoneData.addAbility(ability);
      }

      for (PotionEffectData effect : this.effects) {
         gemstoneData.addPermanentEffect(effect);
      }

      for (ItemStat key : this.stats.keySet()) {
         gemstoneData.setStat(key, this.stats.get(key));
      }

      gemstoneData.setLevel(this.getLevel());
      return gemstoneData;
   }

   @Override
   public boolean equals(Object obj) {
      return !(obj instanceof GemstoneData) ? false : ((GemstoneData)obj).getHistoricUUID().equals(this.getHistoricUUID());
   }

   @Nullable
   public String getMMOItemType() {
      return this.mmoitemType;
   }

   @Nullable
   public String getMMOItemID() {
      return this.mmoitemID;
   }

   @Nullable
   public String getSocketColor() {
      return this.socketColor;
   }

   public GemstoneData(@NotNull JsonObject jsonObject) {
      this.name = jsonObject.get("Name").getAsString();
      JsonElement history = jsonObject.get("History");
      if (history != null) {
         String asString = history.getAsString();
         UUID uuid = MMOUtils.UUIDFromString(asString);
         if (uuid != null) {
            this.historicUUID = uuid;
         } else {
            this.historicUUID = UUID.randomUUID();
         }

         JsonElement type = jsonObject.get("Type");
         JsonElement id = jsonObject.get("Id");
         if (type != null) {
            this.mmoitemType = type.getAsString();
         } else {
            this.mmoitemType = null;
         }

         if (id != null) {
            this.mmoitemID = id.getAsString();
         } else {
            this.mmoitemID = null;
         }

         JsonElement level = jsonObject.get("Level");
         if (level != null && level.isJsonPrimitive()) {
            this.levelPut = level.getAsJsonPrimitive().getAsInt();
         } else {
            this.levelPut = null;
         }

         JsonElement color = jsonObject.get("Color");
         if (color != null && color.isJsonPrimitive()) {
            this.socketColor = color.getAsJsonPrimitive().getAsString();
         } else {
            this.socketColor = null;
         }
      } else {
         this.historicUUID = UUID.randomUUID();
         this.mmoitemID = null;
         this.mmoitemType = null;
         this.socketColor = null;
      }
   }

   public GemstoneData(@NotNull LiveMMOItem liveMMOItem, @Nullable String text) {
      this(liveMMOItem, text, UUID.randomUUID());
   }

   public GemstoneData(@NotNull LiveMMOItem liveMMOItem, @Nullable String text, @NotNull UUID uuid) {
      this.name = MMOUtils.getDisplayName(liveMMOItem.getNBT().getItem());
      if (liveMMOItem.hasData(ItemStats.ABILITIES)) {
         this.abilities.addAll(((AbilityListData)liveMMOItem.getData(ItemStats.ABILITIES)).getAbilities());
      }

      if (liveMMOItem.hasData(ItemStats.PERM_EFFECTS)) {
         this.effects.addAll(((PotionEffectListData)liveMMOItem.getData(ItemStats.PERM_EFFECTS)).getEffects());
      }

      this.historicUUID = uuid;
      this.mmoitemID = liveMMOItem.getId();
      this.mmoitemType = liveMMOItem.getType().getId();
      this.socketColor = text;
   }

   /** @deprecated */
   public GemstoneData(@NotNull String text) {
      this.name = text;
      this.mmoitemID = null;
      this.mmoitemType = null;
      this.socketColor = null;
      this.historicUUID = UUID.randomUUID();
   }

   public void setLevel(@Nullable Integer value) {
      this.levelPut = value;
   }

   @Nullable
   public Integer getLevel() {
      return this.levelPut;
   }

   public boolean isScaling() {
      return this.levelPut != null;
   }

   public GemstoneData(@NotNull String text, @Nullable String mmoitemType, @Nullable String mmoitemId, @Nullable String socketColor) {
      this.name = text;
      this.mmoitemID = mmoitemId;
      this.mmoitemType = mmoitemType;
      this.socketColor = socketColor;
      this.historicUUID = UUID.randomUUID();
   }

   public GemstoneData(@NotNull String text, @Nullable String mmoitemType, @Nullable String mmoitemId, @Nullable String socketColor, @NotNull UUID uuid) {
      this.name = text;
      this.mmoitemID = mmoitemId;
      this.mmoitemType = mmoitemType;
      this.socketColor = socketColor;
      this.historicUUID = uuid;
   }

   public void addAbility(@NotNull AbilityData abilityData) {
      this.abilities.add(abilityData);
   }

   public void addPermanentEffect(@NotNull PotionEffectData potionEffectData) {
      this.effects.add(potionEffectData);
   }

   public void setStat(@NotNull ItemStat itemStat, double value) {
      this.stats.put(itemStat, value);
   }

   @NotNull
   public String getName() {
      return this.name;
   }

   public void setColour(@Nullable String text) {
      this.socketColor = text;
   }

   @NotNull
   public UUID getHistoricUUID() {
      return this.historicUUID;
   }

   @NotNull
   public JsonObject toJson() {
      JsonObject jsonObject = new JsonObject();
      jsonObject.addProperty("Name", this.name);
      jsonObject.addProperty("History", this.historicUUID.toString());
      if (this.mmoitemID != null) {
         jsonObject.addProperty("Id", this.mmoitemID);
      }

      if (this.mmoitemType != null) {
         jsonObject.addProperty("Type", this.mmoitemType);
      }

      if (this.levelPut != null) {
         jsonObject.addProperty("Level", this.levelPut);
      }

      jsonObject.addProperty("Color", this.socketColor);
      return jsonObject;
   }
}
