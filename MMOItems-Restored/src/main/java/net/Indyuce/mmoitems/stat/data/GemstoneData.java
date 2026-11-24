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
      GemstoneData var1 = new GemstoneData(this.getName(), this.getMMOItemType(), this.getMMOItemID(), this.getSocketColor(), this.getHistoricUUID());

      for (AbilityData var3 : this.abilities) {
         var1.addAbility(var3);
      }

      for (PotionEffectData var6 : this.effects) {
         var1.addPermanentEffect(var6);
      }

      for (ItemStat var7 : this.stats.keySet()) {
         var1.setStat(var7, this.stats.get(var7));
      }

      var1.setLevel(this.getLevel());
      return var1;
   }

   @Override
   public boolean equals(Object var1) {
      return !(var1 instanceof GemstoneData) ? false : ((GemstoneData)var1).getHistoricUUID().equals(this.getHistoricUUID());
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

   public GemstoneData(@NotNull JsonObject var1) {
      this.name = var1.get("Name").getAsString();
      JsonElement var2 = var1.get("History");
      if (var2 != null) {
         String var3 = var2.getAsString();
         UUID var4 = MMOUtils.UUIDFromString(var3);
         if (var4 != null) {
            this.historicUUID = var4;
         } else {
            this.historicUUID = UUID.randomUUID();
         }

         JsonElement var5 = var1.get("Type");
         JsonElement var6 = var1.get("Id");
         if (var5 != null) {
            this.mmoitemType = var5.getAsString();
         } else {
            this.mmoitemType = null;
         }

         if (var6 != null) {
            this.mmoitemID = var6.getAsString();
         } else {
            this.mmoitemID = null;
         }

         JsonElement var7 = var1.get("Level");
         if (var7 != null && var7.isJsonPrimitive()) {
            this.levelPut = var7.getAsJsonPrimitive().getAsInt();
         } else {
            this.levelPut = null;
         }

         JsonElement var8 = var1.get("Color");
         if (var8 != null && var8.isJsonPrimitive()) {
            this.socketColor = var8.getAsJsonPrimitive().getAsString();
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

   public GemstoneData(@NotNull LiveMMOItem var1, @Nullable String var2) {
      this(var1, var2, UUID.randomUUID());
   }

   public GemstoneData(@NotNull LiveMMOItem var1, @Nullable String var2, @NotNull UUID var3) {
      this.name = MMOUtils.getDisplayName(var1.getNBT().getItem());
      if (var1.hasData(ItemStats.ABILITIES)) {
         this.abilities.addAll(((AbilityListData)var1.getData(ItemStats.ABILITIES)).getAbilities());
      }

      if (var1.hasData(ItemStats.PERM_EFFECTS)) {
         this.effects.addAll(((PotionEffectListData)var1.getData(ItemStats.PERM_EFFECTS)).getEffects());
      }

      this.historicUUID = var3;
      this.mmoitemID = var1.getId();
      this.mmoitemType = var1.getType().getId();
      this.socketColor = var2;
   }

   /** @deprecated */
   public GemstoneData(@NotNull String var1) {
      this.name = var1;
      this.mmoitemID = null;
      this.mmoitemType = null;
      this.socketColor = null;
      this.historicUUID = UUID.randomUUID();
   }

   public void setLevel(@Nullable Integer var1) {
      this.levelPut = var1;
   }

   @Nullable
   public Integer getLevel() {
      return this.levelPut;
   }

   public boolean isScaling() {
      return this.levelPut != null;
   }

   public GemstoneData(@NotNull String var1, @Nullable String var2, @Nullable String var3, @Nullable String var4) {
      this.name = var1;
      this.mmoitemID = var3;
      this.mmoitemType = var2;
      this.socketColor = var4;
      this.historicUUID = UUID.randomUUID();
   }

   public GemstoneData(@NotNull String var1, @Nullable String var2, @Nullable String var3, @Nullable String var4, @NotNull UUID var5) {
      this.name = var1;
      this.mmoitemID = var3;
      this.mmoitemType = var2;
      this.socketColor = var4;
      this.historicUUID = var5;
   }

   public void addAbility(@NotNull AbilityData var1) {
      this.abilities.add(var1);
   }

   public void addPermanentEffect(@NotNull PotionEffectData var1) {
      this.effects.add(var1);
   }

   public void setStat(@NotNull ItemStat var1, double var2) {
      this.stats.put(var1, var2);
   }

   @NotNull
   public String getName() {
      return this.name;
   }

   public void setColour(@Nullable String var1) {
      this.socketColor = var1;
   }

   @NotNull
   public UUID getHistoricUUID() {
      return this.historicUUID;
   }

   @NotNull
   public JsonObject toJson() {
      JsonObject var1 = new JsonObject();
      var1.addProperty("Name", this.name);
      var1.addProperty("History", this.historicUUID.toString());
      if (this.mmoitemID != null) {
         var1.addProperty("Id", this.mmoitemID);
      }

      if (this.mmoitemType != null) {
         var1.addProperty("Type", this.mmoitemType);
      }

      if (this.levelPut != null) {
         var1.addProperty("Level", this.levelPut);
      }

      var1.addProperty("Color", this.socketColor);
      return var1;
   }
}
