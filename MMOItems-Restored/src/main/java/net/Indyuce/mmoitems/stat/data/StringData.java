package net.Indyuce.mmoitems.stat.data;

import net.Indyuce.mmoitems.api.item.build.MMOItemBuilder;
import net.Indyuce.mmoitems.stat.data.random.RandomStatData;
import net.Indyuce.mmoitems.stat.data.type.StatData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class StringData implements StatData, RandomStatData<StringData> {
   @Nullable
   private String value;

   public StringData(@Nullable String text) {
      this.value = text;
   }

   @Nullable
   public String getString() {
      return this.value;
   }

   public void setString(@Nullable String text) {
      this.value = text;
   }

   @Override
   public String toString() {
      return String.valueOf(this.value);
   }

   public StringData randomize(MMOItemBuilder mmoItemBuilder) {
      return this;
   }

   @Override
   public boolean isEmpty() {
      return this.value == null || this.value.isEmpty();
   }

   @NotNull
   public StringData clone() {
      return new StringData(this.value);
   }
}
