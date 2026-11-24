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

   public StatHistory(@NotNull MMOItem var1, @NotNull ItemStat var2, @NotNull StatData var3) {
      this.itemStat = var2;
      this.originalData = var3;
      this.parent = var1;
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
   public StatHistory setParent(@NotNull MMOItem var1) {
      this.parent = var1;
      return this;
   }

   public void setOriginalData(@NotNull StatData var1) {
      Validate.notNull(var1, "Original data cannot be null");
      this.originalData = var1;
   }

   @Nullable
   public StatData getModifiersBonus(@NotNull UUID var1) {
      return this.perModifierBonus.get(var1);
   }

   public void registerModifierBonus(@NotNull UUID var1, @NotNull StatData var2) {
      Validate.notNull(var1, "Modifier UUID cannot be null");
      Validate.notNull(var2, "Stat data cannot be null");
      if (!var2.isEmpty()) {
         this.perModifierBonus.put(var1, var2);
      }
   }

   public void removeModifierBonus(@NotNull UUID var1) {
      this.perModifierBonus.remove(var1);
   }

   @NotNull
   public ArrayList<UUID> getAllModifiers() {
      return new ArrayList<>(this.perModifierBonus.keySet());
   }

   public void clearModifiersBonus() {
      this.perModifierBonus.clear();
   }

   @Nullable
   public StatData getGemstoneData(@NotNull UUID var1) {
      return this.perGemstoneData.get(var1);
   }

   public void removeGemData(@NotNull UUID var1) {
      this.perGemstoneData.remove(var1);
   }

   @NotNull
   public ArrayList<UUID> getAllGemstones() {
      return new ArrayList<>(this.perGemstoneData.keySet());
   }

   public void registerGemstoneData(@NotNull UUID var1, @NotNull StatData var2) {
      Validate.notNull(var1, "Gemstone ID cannot be null");
      Validate.notNull(var2, "Stat data cannot be null");
      if (!var2.isEmpty()) {
         this.perGemstoneData.put(var1, var2);
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
      StatData var1 = this.getItemStat().getClearStatData();

      for (StatData var3 : this.getExternalData()) {
         ((Mergeable)var1).mergeWith((Mergeable)var3);
      }

      this.getExternalData().clear();
      this.registerExternalData(var1);
   }

   public void registerExternalData(@NotNull StatData var1) {
      Validate.notNull(var1, "Stat data cannot be null");
      if (!var1.isEmpty()) {
         this.perExternalData.add(var1);
      }
   }

   public void clearExternalData() {
      this.perExternalData.clear();
   }

   public void purgeGemstones() {
      GemSocketsData var1 = (GemSocketsData)this.getMMOItem().getData(ItemStats.GEM_SOCKETS);
      if (var1 == null) {
         this.perGemstoneData.clear();
      } else {
         HashMap var2 = new HashMap();

         for (GemstoneData var4 : var1.getGemstones()) {
            StatData var5 = this.perGemstoneData.get(var4.getHistoricUUID());
            if (var5 != null) {
               var2.put(var4.getHistoricUUID(), var5);
            }
         }

         this.perGemstoneData = var2;
      }
   }

   @NotNull
   public StatData recalculate(int var1) {
      return this.recalculate(true, var1);
   }

   @NotNull
   public StatData recalculateUnupgraded() {
      return this.recalculate(true, null);
   }

   private int findLevel(int var1, UUID var2) {
      for (GemstoneData var4 : this.getMMOItem().getGemstones()) {
         if (var4.getHistoricUUID().equals(var2)) {
            return var4.isScaling() ? var4.getLevel() : var1;
         }
      }

      throw new IllegalArgumentException("Could not find level of gem " + var2);
   }

   @NotNull
   public StatData recalculate(boolean var1, @Nullable Integer var2) {
      if (var1) {
         this.purgeGemstones();
      }

      UpgradeInfo var3 = var2 != null && var2 != 0 && this.getItemStat() instanceof Upgradable
         ? this.getMMOItem().getUpgradeTemplate().getUpgradeInfo(this.getItemStat())
         : null;
      Mergeable var4 = ((Mergeable)this.originalData).clone();

      for (StatData var6 : this.perModifierBonus.values()) {
         var4.mergeWith((Mergeable)var6);
      }

      if (var3 != null) {
         var4 = (Mergeable)((Upgradable)this.getItemStat()).apply(var4, var3, var2);
      }

      for (UUID var11 : this.perGemstoneData.keySet()) {
         Mergeable var7 = (Mergeable)this.getGemstoneData(var11);
         if (var3 != null) {
            int var8 = var2 - this.findLevel(var2, var11);
            var7 = (Mergeable)((Upgradable)this.getItemStat()).apply(var7.clone(), var3, var8);
         }

         var4.mergeWith(var7);
      }

      for (StatData var12 : this.getExternalData()) {
         var4.mergeWith((Mergeable)var12);
      }

      return var4;
   }

   @NotNull
   public JsonObject toJson() {
      JsonObject var1 = new JsonObject();
      var1.addProperty("Stat", this.getItemStat().getId());
      if (!this.getOriginalData().isEmpty() || this.getItemStat() == ItemStats.ENCHANTS) {
         var1.add("OGStory", ItemTag.compressTags(this.getItemStat().getAppliedNBT(this.getOriginalData())));
      }

      JsonArray var2 = new JsonArray();

      for (UUID var4 : this.getAllGemstones()) {
         JsonObject var5 = new JsonObject();
         JsonArray var6 = ItemTag.compressTags(this.getItemStat().getAppliedNBT(this.getGemstoneData(var4)));
         var5.add(var4.toString(), var6);
         var2.add(var5);
      }

      if (var2.size() > 0) {
         var1.add("Gemstory", var2);
      }

      JsonArray var9 = new JsonArray();

      for (StatData var12 : this.getExternalData()) {
         if (!var12.isEmpty()) {
            var9.add(ItemTag.compressTags(this.getItemStat().getAppliedNBT(var12)));
         }
      }

      if (!var9.isEmpty()) {
         var1.add("Exstory", var9);
      }

      JsonArray var11 = new JsonArray();

      for (UUID var14 : this.getAllModifiers()) {
         JsonObject var7 = new JsonObject();
         JsonArray var8 = ItemTag.compressTags(this.getItemStat().getAppliedNBT(this.getModifiersBonus(var14)));
         var7.add(var14.toString(), var8);
         var11.add(var7);
      }

      if (var11.size() > 0) {
         var1.add("Mod", var11);
      }

      return var1;
   }

   @NotNull
   public String toNBTString() {
      return this.toJson().toString();
   }

   public boolean isUpgradeable() {
      return this.getMMOItem().hasUpgradeTemplate() && this.getMMOItem().getUpgradeTemplate().getUpgradeInfo(this.getItemStat()) != null;
   }

   @Nullable
   public static StatHistory fromJson(@NotNull MMOItem var0, @NotNull JsonObject var1) {
      JsonElement var3 = null;
      JsonElement var4 = null;
      JsonElement var5 = null;
      JsonElement var6 = null;
      if (var1.has("Stat")) {
         JsonElement var2 = var1.get("Stat");
         if (var1.has("OGStory")) {
            var3 = var1.get("OGStory");
         }

         if (var1.has("Gemstory")) {
            var4 = var1.get("Gemstory");
         }

         if (var1.has("Exstory")) {
            var5 = var1.get("Exstory");
         }

         if (var1.has("Mod")) {
            var6 = var1.get("Mod");
         }

         if (!var2.isJsonPrimitive()) {
            return null;
         }

         if (var3 != null && !var3.isJsonArray()) {
            return null;
         }

         if (var4 != null && !var4.isJsonArray()) {
            return null;
         }

         if (var5 != null && !var5.isJsonArray()) {
            return null;
         }

         if (var6 != null && !var6.isJsonArray()) {
            return null;
         }

         String var7 = var2.getAsJsonPrimitive().getAsString();
         ItemStat var8 = MMOItems.plugin.getStats().get(var7);
         if (var8 == null) {
            return null;
         }

         StatData var9;
         if (var3 != null) {
            ArrayList var10 = ItemTag.decompressTags(var3.getAsJsonArray());
            var9 = var8.getLoadedNBT(var10);
         } else {
            var9 = var8.getClearStatData();
         }

         if (var9 == null) {
            return null;
         }

         StatHistory var22 = new StatHistory(var0, var8, var9);
         if (var4 != null) {
            for (JsonElement var12 : var4.getAsJsonArray()) {
               if (var12.isJsonObject()) {
                  JsonObject var13 = var12.getAsJsonObject();

                  for (Entry var16 : var13.entrySet()) {
                     String var17 = (String)var16.getKey();
                     UUID var18 = MMOUtils.UUIDFromString(var17);
                     JsonElement var19 = (JsonElement)var16.getValue();
                     if (var19.isJsonArray() && var18 != null) {
                        ArrayList var20 = ItemTag.decompressTags(var19.getAsJsonArray());
                        StatData var21 = var8.getLoadedNBT(var20);
                        if (var21 != null) {
                           var22.registerGemstoneData(var18, var21);
                        }
                     }
                  }
               }
            }
         }

         if (var5 != null) {
            for (JsonElement var25 : var5.getAsJsonArray()) {
               if (var25.isJsonArray()) {
                  ArrayList var27 = ItemTag.decompressTags(var25.getAsJsonArray());
                  StatData var29 = var8.getLoadedNBT(var27);
                  if (var29 != null) {
                     var22.registerExternalData(var29);
                  }
               }
            }
         }

         if (var6 != null) {
            for (JsonElement var26 : var6.getAsJsonArray()) {
               if (var26.isJsonObject()) {
                  JsonObject var28 = var26.getAsJsonObject();

                  for (Entry var32 : var28.entrySet()) {
                     String var33 = (String)var32.getKey();
                     UUID var34 = MMOUtils.UUIDFromString(var33);
                     JsonElement var35 = (JsonElement)var32.getValue();
                     if (var35.isJsonArray() && var34 != null) {
                        ArrayList var36 = ItemTag.decompressTags(var35.getAsJsonArray());
                        StatData var37 = var8.getLoadedNBT(var36);
                        if (var37 != null) {
                           var22.registerModifierBonus(var34, var37);
                        }
                     }
                  }
               }
            }
         }

         return var22;
      } else {
         return null;
      }
   }

   @Nullable
   public static StatHistory fromNBTString(@NotNull MMOItem var0, @NotNull String var1) {
      try {
         JsonParser var2 = new JsonParser();
         JsonObject var5 = var2.parse(var1).getAsJsonObject();
         return fromJson(var0, var5);
      } catch (Throwable var4) {
         FriendlyFeedbackProvider var3 = new FriendlyFeedbackProvider(FFPMMOItems.get());
         var3.activatePrefix(true, "Stat History");
         var3.log(
            FriendlyFeedbackCategory.ERROR, "Could not get stat history: $f{0}$b at $f{1}", new String[]{var4.getMessage(), var4.getStackTrace()[0].toString()}
         );
         var3.sendTo(FriendlyFeedbackCategory.ERROR, MMOItems.getConsole());
         return null;
      }
   }

   @NotNull
   public StatHistory clone() {
      StatHistory var1 = new StatHistory(this.getMMOItem(), this.getItemStat(), ((Mergeable)this.getOriginalData()).clone());
      this.perGemstoneData.forEach((var1x, var2) -> var1.perGemstoneData.put(var1x, ((Mergeable)var2).clone()));
      this.perExternalData.forEach(var1x -> var1.perExternalData.add(((Mergeable)var1x).clone()));
      this.perModifierBonus.forEach((var1x, var2) -> var1.perModifierBonus.put(var1x, ((Mergeable)var2).clone()));
      return var1;
   }

   public void assimilate(@NotNull StatHistory var1) {
      if (var1.getItemStat().getNBTPath().equals(this.getItemStat().getNBTPath())) {
         for (UUID var3 : var1.getAllGemstones()) {
            this.registerGemstoneData(var3, var1.getGemstoneData(var3));
         }

         for (StatData var6 : var1.getExternalData()) {
            this.registerExternalData(var6);
         }

         for (UUID var7 : var1.getAllModifiers()) {
            this.registerModifierBonus(var7, var1.getModifiersBonus(var7));
         }
      }
   }

   @Deprecated
   public StatHistory clone(@NotNull MMOItem var1) {
      StatHistory var2 = this.clone();
      var2.setParent(var1);
      return var2;
   }

   @Deprecated
   @NotNull
   public static StatHistory from(@NotNull MMOItem var0, @NotNull ItemStat var1) {
      return var0.computeStatHistory(var1);
   }

   @Deprecated
   @NotNull
   public static StatHistory from(@NotNull MMOItem var0, @NotNull ItemStat var1, boolean var2) {
      if (var2) {
         StatHistory var3 = new StatHistory(var0, var1, Objects.requireNonNull(var0.getData(var1)));
         var0.setStatHistory(var1, var3);
         return var3;
      } else {
         return var0.computeStatHistory(var1);
      }
   }

   @Deprecated
   @NotNull
   public StatData recalculateUnupgraded(boolean var1) {
      return this.recalculate(var1, null);
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
