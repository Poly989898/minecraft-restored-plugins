package net.Indyuce.mmoitems.stat.data;

import io.lumine.mythic.lib.api.util.ui.SilentNumbers;
import io.lumine.mythic.lib.gson.JsonArray;
import java.util.ArrayList;
import java.util.List;
import net.Indyuce.mmoitems.api.item.build.MMOItemBuilder;
import net.Indyuce.mmoitems.stat.data.random.RandomStatData;
import net.Indyuce.mmoitems.stat.data.type.Mergeable;
import net.Indyuce.mmoitems.stat.data.type.StatData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class StringListData implements StatData, Mergeable<StringListData>, RandomStatData<StringListData> {
   @NotNull
   private final List<String> list = new ArrayList<>();

   @Override
   public boolean equals(Object var1) {
      if (!(var1 instanceof StringListData)) {
         return false;
      } else {
         return ((StringListData)var1).getList().size() != this.getList().size()
            ? false
            : SilentNumbers.hasAll(((StringListData)var1).getList(), this.getList());
      }
   }

   public StringListData() {
   }

   public StringListData(@NotNull String[] var1) {
      for (String var5 : var1) {
         this.list.add(var5);
      }
   }

   public StringListData(@NotNull JsonArray var1) {
      var1.forEach(var1x -> this.list.add(var1x.getAsString()));
   }

   public StringListData(@NotNull List<String> var1) {
      this.list.addAll(var1);
   }

   @NotNull
   public List<String> getList() {
      return this.list;
   }

   public StringListData randomize(MMOItemBuilder var1) {
      return new StringListData(new ArrayList<>(this.list));
   }

   public void mergeWith(StringListData var1) {
      this.list.addAll(var1.list);
   }

   @NotNull
   public StringListData clone() {
      return new StringListData(this.list);
   }

   @Override
   public boolean isEmpty() {
      return this.list.isEmpty();
   }

   @Override
   public String toString() {
      StringBuilder var1 = new StringBuilder("§7");

      for (String var3 : this.getList()) {
         if (var1.length() > 0) {
            var1.append("§8;§7 ");
         }

         var1.append(var3);
      }

      return var1.toString();
   }

   @Deprecated
   public boolean remove(@Nullable String var1) {
      return this.list.remove(var1);
   }
}
