package io.lumine.mythic.api.adapters.items.components;

import io.lumine.mythic.api.adapters.AbstractItemComponent;
import io.lumine.mythic.api.adapters.AbstractItemStack;
import io.lumine.mythic.api.config.MythicConfig;
import io.lumine.mythic.api.drops.DropMetadata;
import io.lumine.mythic.api.skills.placeholders.PlaceholderColor;
import io.lumine.mythic.core.items.MythicItem;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.bukkit.DyeColor;

@Getter
@Setter
@ToString
public class AbstractItemBannerComponent implements AbstractItemComponent {
   public static final String KEY_BANNER_LAYERS = "BannerLayers";
   public static final String KEY_BANNER_LAYERS_OPTIONS = "Options.BannerLayers";
   private static final List<String> BANNER_LAYER_KEYS = List.of("BannerLayers", "Options.BannerLayers");
   private PlaceholderColor color;
   private Collection<String> bannerLayers;

   public AbstractItemBannerComponent(MythicItem item, MythicConfig config) {
      this.color = config.getPlaceholderColor("Color", null);
      this.bannerLayers = this.resolvePatternConfig(config);
   }

   public static boolean isConfigured(MythicConfig config) {
      for (String key : BANNER_LAYER_KEYS) {
         if (config.isSet(key)) {
            return true;
         }
      }

      return false;
   }

   public static Optional<AbstractItemBannerComponent.BannerPatternEntry> parsePatternEntry(String line) {
      if (line == null) {
         return Optional.empty();
      } else {
         String trimmedLine = line.trim();
         if (trimmedLine.isEmpty()) {
            return Optional.empty();
         } else {
            String[] splitLine = trimmedLine.split("\\s+");
            return splitLine.length < 2 ? Optional.empty() : parseEntry(splitLine[0], splitLine[1]);
         }
      }
   }

   public static String normalizePatternId(String patternId) {
      String normalizedPattern = normalizeToken(patternId).toLowerCase(Locale.ROOT);
      if (normalizedPattern.isEmpty()) {
         return normalizedPattern;
      } else {
         if (!normalizedPattern.contains(":")) {
            normalizedPattern = "minecraft:" + normalizedPattern;
         }

         return normalizedPattern;
      }
   }

   private static Optional<AbstractItemBannerComponent.BannerPatternEntry> parseEntry(String color, String patternId) {
      String normalizedColor = normalizeToken(color).toUpperCase(Locale.ROOT);
      String normalizedPatternId = normalizeToken(patternId);
      if (!normalizedColor.isEmpty() && !normalizedPatternId.isEmpty()) {
         try {
            return Optional.of(new AbstractItemBannerComponent.BannerPatternEntry(DyeColor.valueOf(normalizedColor), normalizedPatternId));
         } catch (IllegalArgumentException e) {
            return Optional.empty();
         }
      } else {
         return Optional.empty();
      }
   }

   private Collection<String> resolvePatternConfig(MythicConfig config) {
      for (String key : BANNER_LAYER_KEYS) {
         if (config.isSet(key)) {
            ArrayList<String> list = new ArrayList<>(config.getStringList(key));
            if (!list.isEmpty()) {
               return list;
            }

            String single = config.getString(key, null);
            if (single != null && !single.isEmpty()) {
               list.add(single);
            }

            return list;
         }
      }

      return List.of();
   }

   private static String normalizeToken(String value) {
      if (value == null) {
         return "";
      } else {
         String normalized = value.trim();
         if (normalized.length() >= 2 && (normalized.startsWith("\"") && normalized.endsWith("\"") || normalized.startsWith("'") && normalized.endsWith("'"))) {
            normalized = normalized.substring(1, normalized.length() - 1).trim();
         }

         return normalized;
      }
   }

   @Override
   public void apply(MythicItem mythicItem, DropMetadata dropMetadata, AbstractItemStack itemStack) {
      itemStack.applyBannerComponent(dropMetadata, this);
   }

   @Generated
   @Override
   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof AbstractItemBannerComponent other)) {
         return false;
      } else if (!other.canEqual(this)) {
         return false;
      } else {
         Object this$color = this.getColor();
         Object other$color = other.getColor();
         if (this$color == null ? other$color == null : this$color.equals(other$color)) {
            Object this$bannerLayers = this.getBannerLayers();
            Object other$bannerLayers = other.getBannerLayers();
            return this$bannerLayers == null ? other$bannerLayers == null : this$bannerLayers.equals(other$bannerLayers);
         } else {
            return false;
         }
      }
   }

   @Generated
   protected boolean canEqual(Object other) {
      return other instanceof AbstractItemBannerComponent;
   }

   @Generated
   @Override
   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      Object $color = this.getColor();
      result = result * 59 + ($color == null ? 43 : $color.hashCode());
      Object $bannerLayers = this.getBannerLayers();
      return result * 59 + ($bannerLayers == null ? 43 : $bannerLayers.hashCode());
   }

   public record BannerPatternEntry(DyeColor color, String patternId) {
   }
}
