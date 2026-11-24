package net.Indyuce.mmoitems.stat.type;

import io.lumine.mythic.lib.api.item.ItemTag;
import java.util.ArrayList;
import java.util.Objects;
import net.Indyuce.mmoitems.stat.data.StringData;
import net.Indyuce.mmoitems.stat.data.type.Mergeable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class NameData extends StringData implements Mergeable<NameData> {
   @NotNull
   ArrayList<String> prefixes = new ArrayList<>();
   @NotNull
   ArrayList<String> suffixes = new ArrayList<>();

   public NameData(@Nullable String var1) {
      super(var1);
   }

   public void readPrefixes(@Nullable ItemTag var1) {
      if (var1 != null) {
         for (String var3 : ItemTag.getStringListFromTag(var1)) {
            if (var3 != null) {
               this.addPrefix(var3);
            }
         }
      }
   }

   public void readSuffixes(@Nullable ItemTag var1) {
      if (var1 != null) {
         for (String var3 : ItemTag.getStringListFromTag(var1)) {
            if (var3 != null) {
               this.addSuffix(var3);
            }
         }
      }
   }

   public boolean hasMainName() {
      return this.getString() != null && !this.getString().isEmpty();
   }

   @NotNull
   public String getMainName() {
      return Objects.requireNonNullElse(this.getString(), "");
   }

   public boolean hasPrefixes() {
      return this.prefixes.size() > 0;
   }

   public boolean hasSuffixes() {
      return this.suffixes.size() > 0;
   }

   @NotNull
   public String bake() {
      StringBuilder var1 = new StringBuilder();

      for (String var3 : this.getPrefixes()) {
         if (var1.length() > 0) {
            var1.append(" ");
         }

         var1.append(var3);
      }

      if (var1.length() > 0) {
         var1.append(" ");
      }

      var1.append(this.getMainName());

      for (String var5 : this.getSuffixes()) {
         if (var1.length() > 0) {
            var1.append(" ");
         }

         var1.append(var5);
      }

      return var1.toString();
   }

   @NotNull
   public String bakePrefix() {
      StringBuilder var1 = new StringBuilder();

      for (String var3 : this.getPrefixes()) {
         if (var1.length() > 0) {
            var1.append(" ");
         }

         var1.append(var3);
      }

      return var1.toString();
   }

   @NotNull
   public String bakeSuffix() {
      StringBuilder var1 = new StringBuilder();

      for (String var3 : this.getSuffixes()) {
         if (var1.length() > 0) {
            var1.append(" ");
         }

         var1.append(var3);
      }

      return var1.toString();
   }

   @Override
   public String toString() {
      return this.bake();
   }

   @NotNull
   public ItemTag compressPrefixes(@NotNull String var1) {
      return ItemTag.fromStringList(var1, this.getPrefixes());
   }

   @NotNull
   public ItemTag compressSuffixes(@NotNull String var1) {
      return ItemTag.fromStringList(var1, this.getSuffixes());
   }

   public void addPrefix(@NotNull String var1) {
      this.prefixes.add(var1);
   }

   public void clearPrefixes() {
      this.prefixes.clear();
   }

   @NotNull
   public ArrayList<String> getPrefixes() {
      return this.prefixes;
   }

   public void addSuffix(@NotNull String var1) {
      this.suffixes.add(var1);
   }

   public void clearSuffixes() {
      this.suffixes.clear();
   }

   @NotNull
   public ArrayList<String> getSuffixes() {
      return this.suffixes;
   }

   public void mergeWith(NameData var1) {
      if (var1.hasMainName()) {
         this.setString(var1.getString());
      }

      for (String var3 : var1.getPrefixes()) {
         this.addPrefix(var3);
      }

      for (String var5 : var1.getSuffixes()) {
         this.addSuffix(var5);
      }
   }

   @NotNull
   public NameData clone() {
      NameData var1 = new NameData(this.getString());

      for (String var3 : this.getPrefixes()) {
         var1.addPrefix(var3);
      }

      for (String var5 : this.getSuffixes()) {
         var1.addSuffix(var5);
      }

      return var1;
   }

   @Override
   public boolean isEmpty() {
      return super.isEmpty() && this.prefixes.isEmpty() && this.suffixes.isEmpty();
   }
}
