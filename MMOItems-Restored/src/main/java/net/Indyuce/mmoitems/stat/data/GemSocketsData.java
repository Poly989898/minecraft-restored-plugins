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

   public GemSocketsData(@NotNull List<String> var1) {
      this.emptySlots = var1;
   }

   public GemSocketsData(@NotNull JsonArray var1) {
      this.emptySlots = new ArrayList<>();
      var1.forEach(var1x -> this.emptySlots.add(var1x.getAsString()));
   }

   public boolean canReceive(@NotNull String var1) {
      return this.getEmptySocket(var1) != null;
   }

   @Nullable
   public String getEmptySocket(@NotNull String var1) {
      for (String var3 : this.emptySlots) {
         if (var1.isEmpty() || var3.equals(getUncoloredGemSlot()) || var1.equals(var3)) {
            return var3;
         }
      }

      return null;
   }

   @NotNull
   public static String getUncoloredGemSlot() {
      String var0 = MMOItems.plugin.getConfig().getString("gem-sockets.uncolored");
      return var0 == null ? "Uncolored" : var0;
   }

   public void add(GemstoneData var1) {
      this.gems.add(var1);
   }

   public boolean apply(String var1, GemstoneData var2) {
      String var3 = this.getEmptySocket(var1);
      if (var3 == null) {
         return false;
      }

      this.emptySlots.remove(var3);
      this.gems.add(var2);
      return true;
   }

   public void addEmptySlot(@NotNull String var1) {
      this.emptySlots.add(var1);
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

   public boolean removeGem(@NotNull UUID var1, @Nullable String var2) {
      for (GemstoneData var4 : this.getGemstones()) {
         if (var4.getHistoricUUID().equals(var1)) {
            if (var2 != null) {
               this.addEmptySlot(var2);
            }

            this.gems.remove(var4);
            return true;
         }
      }

      return false;
   }

   public JsonObject toJson() {
      JsonObject var1 = new JsonObject();
      JsonArray var2 = new JsonArray();
      this.getEmptySlots().forEach(var2::add);
      var1.add("EmptySlots", var2);
      JsonArray var3 = new JsonArray();
      this.gems.forEach(var1x -> var3.add(var1x.toJson()));
      var1.add("Gemstones", var3);
      return var1;
   }

   public void mergeWith(GemSocketsData var1) {
      this.emptySlots.addAll(var1.emptySlots);
      this.gems.addAll(var1.gems);
   }

   public GemSocketsData clone() {
      GemSocketsData var1 = new GemSocketsData(new ArrayList<>(this.emptySlots));

      for (GemstoneData var3 : this.getGemstones()) {
         var1.add(var3.cloneGem());
      }

      return var1;
   }

   @Override
   public boolean isEmpty() {
      return this.gems.isEmpty() && this.emptySlots.isEmpty();
   }

   public GemSocketsData randomize(MMOItemBuilder var1) {
      return new GemSocketsData(new ArrayList<>(this.emptySlots));
   }

   @Override
   public String toString() {
      return "Empty:§b " + this.getEmptySlots().size() + "§7, Gems:§b " + this.getGemstones().size();
   }

   @Override
   public boolean equals(Object var1) {
      if (!(var1 instanceof GemSocketsData)) {
         return false;
      }

      if (((GemSocketsData)var1).getEmptySlots().size() != this.getEmptySlots().size()) {
         return false;
      }

      if (((GemSocketsData)var1).getGemstones().size() != this.getGemstones().size()) {
         return false;
      }

      if (!SilentNumbers.hasAll(((GemSocketsData)var1).getEmptySlots(), this.getEmptySlots())) {
         return false;
      }

      for (GemstoneData var3 : ((GemSocketsData)var1).getGemstones()) {
         if (var3 != null) {
            boolean var4 = true;

            for (GemstoneData var6 : this.getGemstones()) {
               if (var3.equals(var6)) {
                  var4 = false;
                  break;
               }
            }

            if (var4) {
               return false;
            }
         }
      }

      return true;
   }
}
