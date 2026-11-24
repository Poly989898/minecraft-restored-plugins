package net.Indyuce.mmoitems.stat.data;

import io.lumine.mythic.lib.api.util.ui.SilentNumbers;
import io.lumine.mythic.lib.gson.JsonArray;
import io.lumine.mythic.lib.gson.JsonObject;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.item.build.MMOItemBuilder;
import net.Indyuce.mmoitems.stat.data.random.RandomStatData;
import net.Indyuce.mmoitems.stat.data.type.Mergeable;
import net.Indyuce.mmoitems.stat.data.type.StatData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GemSocketsData implements StatData, Mergeable<GemSocketsData>, RandomStatData<GemSocketsData> {
   @NotNull
   private final List<GemstoneData> gems = new ArrayList<>();
   @NotNull
   private final List<String> emptySlots;

   public GemSocketsData(@NotNull List<String> texts) {
      this.emptySlots = texts;
   }

   public GemSocketsData(@NotNull JsonArray jsonArray) {
      this.emptySlots = new ArrayList<>();
      jsonArray.forEach(var1x -> this.emptySlots.add(var1x.getAsString()));
   }

   public boolean canReceive(@NotNull String text) {
      return this.getEmptySocket(text) != null;
   }

   @Nullable
   public String getEmptySocket(@NotNull String text) {
      for (String emptySlot : this.emptySlots) {
         if (text.isEmpty() || emptySlot.equals(getUncoloredGemSlot()) || text.equals(emptySlot)) {
            return emptySlot;
         }
      }

      return null;
   }

   @NotNull
   public static String getUncoloredGemSlot() {
      String gemSocketsUncolored = MMOItems.plugin.getConfig().getString("gem-sockets.uncolored");
      return gemSocketsUncolored == null ? "Uncolored" : gemSocketsUncolored;
   }

   public void add(GemstoneData gemstoneData) {
      this.gems.add(gemstoneData);
   }

   public boolean apply(String text, GemstoneData gemstoneData) {
      String emptySocket = this.getEmptySocket(text);
      if (emptySocket == null) {
         return false;
      }

      this.emptySlots.remove(emptySocket);
      this.gems.add(gemstoneData);
      return true;
   }

   public void addEmptySlot(@NotNull String text) {
      this.emptySlots.add(text);
   }

   @NotNull
   public List<String> getEmptySlots() {
      return this.emptySlots;
   }

   @Deprecated
   public Set<GemstoneData> getGemstones() {
      return new HashSet<>(this.gems);
   }

   @NotNull
   public List<GemstoneData> getGems() {
      return this.gems;
   }

   public boolean removeGem(@NotNull UUID uuid, @Nullable String text) {
      for (GemstoneData gemstone : this.getGemstones()) {
         if (gemstone.getHistoricUUID().equals(uuid)) {
            if (text != null) {
               this.addEmptySlot(text);
            }

            this.gems.remove(gemstone);
            return true;
         }
      }

      return false;
   }

   public JsonObject toJson() {
      JsonObject jsonObject = new JsonObject();
      JsonArray jsonArray = new JsonArray();
      this.getEmptySlots().forEach(jsonArray::add);
      jsonObject.add("EmptySlots", jsonArray);
      JsonArray gemstonesJson = new JsonArray();
      this.gems.forEach(var1x -> gemstonesJson.add(var1x.toJson()));
      jsonObject.add("Gemstones", gemstonesJson);
      return jsonObject;
   }

   public void mergeWith(GemSocketsData gemSocketsData) {
      this.emptySlots.addAll(gemSocketsData.emptySlots);
      this.gems.addAll(gemSocketsData.gems);
   }

   public GemSocketsData clone() {
      GemSocketsData gemSocketsData = new GemSocketsData(new ArrayList<>(this.emptySlots));

      for (GemstoneData gemstone : this.getGemstones()) {
         gemSocketsData.add(gemstone.cloneGem());
      }

      return gemSocketsData;
   }

   @Override
   public boolean isEmpty() {
      return this.gems.isEmpty() && this.emptySlots.isEmpty();
   }

   public GemSocketsData randomize(MMOItemBuilder mmoItemBuilder) {
      return new GemSocketsData(new ArrayList<>(this.emptySlots));
   }

   @Override
   public String toString() {
      return "Empty:§b " + this.getEmptySlots().size() + "§7, Gems:§b " + this.getGemstones().size();
   }

   @Override
   public boolean equals(Object obj) {
      if (!(obj instanceof GemSocketsData)) {
         return false;
      }

      if (((GemSocketsData)obj).getEmptySlots().size() != this.getEmptySlots().size()) {
         return false;
      }

      if (((GemSocketsData)obj).getGemstones().size() != this.getGemstones().size()) {
         return false;
      }

      if (!SilentNumbers.hasAll(((GemSocketsData)obj).getEmptySlots(), this.getEmptySlots())) {
         return false;
      }

      for (GemstoneData gemstone : ((GemSocketsData)obj).getGemstones()) {
         if (gemstone != null) {
            boolean flag = true;

            for (GemstoneData gemstoneEntry : this.getGemstones()) {
               if (gemstone.equals(gemstoneEntry)) {
                  flag = false;
                  break;
               }
            }

            if (flag) {
               return false;
            }
         }
      }

      return true;
   }
}
