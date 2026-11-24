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

   public NameData(@Nullable String text) {
      super(text);
   }

   public void readPrefixes(@Nullable ItemTag itemTag) {
      if (itemTag != null) {
         for (String text : ItemTag.getStringListFromTag(itemTag)) {
            if (text != null) {
               this.addPrefix(text);
            }
         }
      }
   }

   public void readSuffixes(@Nullable ItemTag itemTag) {
      if (itemTag != null) {
         for (String text : ItemTag.getStringListFromTag(itemTag)) {
            if (text != null) {
               this.addSuffix(text);
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
      StringBuilder builder = new StringBuilder();

      for (String prefix : this.getPrefixes()) {
         if (builder.length() > 0) {
            builder.append(" ");
         }

         builder.append(prefix);
      }

      if (builder.length() > 0) {
         builder.append(" ");
      }

      builder.append(this.getMainName());

      for (String suffix : this.getSuffixes()) {
         if (builder.length() > 0) {
            builder.append(" ");
         }

         builder.append(suffix);
      }

      return builder.toString();
   }

   @NotNull
   public String bakePrefix() {
      StringBuilder builder = new StringBuilder();

      for (String prefix : this.getPrefixes()) {
         if (builder.length() > 0) {
            builder.append(" ");
         }

         builder.append(prefix);
      }

      return builder.toString();
   }

   @NotNull
   public String bakeSuffix() {
      StringBuilder builder = new StringBuilder();

      for (String suffix : this.getSuffixes()) {
         if (builder.length() > 0) {
            builder.append(" ");
         }

         builder.append(suffix);
      }

      return builder.toString();
   }

   @Override
   public String toString() {
      return this.bake();
   }

   @NotNull
   public ItemTag compressPrefixes(@NotNull String text) {
      return ItemTag.fromStringList(text, this.getPrefixes());
   }

   @NotNull
   public ItemTag compressSuffixes(@NotNull String text) {
      return ItemTag.fromStringList(text, this.getSuffixes());
   }

   public void addPrefix(@NotNull String text) {
      this.prefixes.add(text);
   }

   public void clearPrefixes() {
      this.prefixes.clear();
   }

   @NotNull
   public ArrayList<String> getPrefixes() {
      return this.prefixes;
   }

   public void addSuffix(@NotNull String text) {
      this.suffixes.add(text);
   }

   public void clearSuffixes() {
      this.suffixes.clear();
   }

   @NotNull
   public ArrayList<String> getSuffixes() {
      return this.suffixes;
   }

   public void mergeWith(NameData nameData) {
      if (nameData.hasMainName()) {
         this.setString(nameData.getString());
      }

      for (String prefix : nameData.getPrefixes()) {
         this.addPrefix(prefix);
      }

      for (String suffix : nameData.getSuffixes()) {
         this.addSuffix(suffix);
      }
   }

   @NotNull
   public NameData clone() {
      NameData nameData = new NameData(this.getString());

      for (String prefix : this.getPrefixes()) {
         nameData.addPrefix(prefix);
      }

      for (String suffix : this.getSuffixes()) {
         nameData.addSuffix(suffix);
      }

      return nameData;
   }

   @Override
   public boolean isEmpty() {
      return super.isEmpty() && this.prefixes.isEmpty() && this.suffixes.isEmpty();
   }
}
