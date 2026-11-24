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
   public boolean equals(Object obj) {
      if (!(obj instanceof StringListData)) {
         return false;
      } else {
         return ((StringListData)obj).getList().size() != this.getList().size()
            ? false
            : SilentNumbers.hasAll(((StringListData)obj).getList(), this.getList());
      }
   }

   public StringListData() {
   }

   public StringListData(@NotNull String[] texts) {
      for (String text : texts) {
         this.list.add(text);
      }
   }

   public StringListData(@NotNull JsonArray jsonArray) {
      jsonArray.forEach(var1x -> this.list.add(var1x.getAsString()));
   }

   public StringListData(@NotNull List<String> texts) {
      this.list.addAll(texts);
   }

   @NotNull
   public List<String> getList() {
      return this.list;
   }

   public StringListData randomize(MMOItemBuilder mmoItemBuilder) {
      return new StringListData(new ArrayList<>(this.list));
   }

   public void mergeWith(StringListData stringListData) {
      this.list.addAll(stringListData.list);
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
      StringBuilder builder = new StringBuilder("§7");

      for (String text : this.getList()) {
         if (builder.length() > 0) {
            builder.append("§8;§7 ");
         }

         builder.append(text);
      }

      return builder.toString();
   }

   @Deprecated
   public boolean remove(@Nullable String text) {
      return this.list.remove(text);
   }
}
