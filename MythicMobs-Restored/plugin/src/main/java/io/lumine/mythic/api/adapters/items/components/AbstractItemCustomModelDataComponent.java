package io.lumine.mythic.api.adapters.items.components;

import io.lumine.mythic.api.adapters.AbstractItemComponent;
import io.lumine.mythic.api.adapters.AbstractItemStack;
import io.lumine.mythic.api.config.MythicConfig;
import io.lumine.mythic.api.drops.DropMetadata;
import io.lumine.mythic.bukkit.utils.numbers.Numbers;
import io.lumine.mythic.bukkit.utils.serialize.Chroma;
import io.lumine.mythic.core.items.MythicItem;
import io.lumine.mythic.core.logging.MythicLogger;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import lombok.Generated;
import lombok.Getter;
import lombok.ToString;
import org.bukkit.Color;

@Getter
@ToString
public class AbstractItemCustomModelDataComponent implements AbstractItemComponent {
   public static final String SECTION_KEY = "CustomModelData";
   public static final String SECTION_OPTIONS_KEY = "Options.CustomModelData";
   private final List<Float> floats = new ArrayList<>();
   private final List<Boolean> flags = new ArrayList<>();
   private final List<String> strings = new ArrayList<>();
   private final List<Color> colors = new ArrayList<>();

   public AbstractItemCustomModelDataComponent(MythicItem mythicItem, MythicConfig config, String key) {
      List<?> entries = config.getList(key);
      if (entries != null) {
         for (Object entry : entries) {
            if (entry instanceof Map<?, ?> mapEntry) {
               this.parseMapEntry(mythicItem, config, mapEntry);
            } else if (entry != null) {
               this.parseTypedStringEntry(mythicItem, config, entry.toString());
            }
         }
      } else {
         String scalar = config.getString(key, null);
         if (scalar != null) {
            this.parseScalarStringEntry(mythicItem, config, scalar);
         }
      }
   }

   public static String resolveStructuredKey(MythicConfig config) {
      if (config.isSet("CustomModelData")) {
         return isStructuredValue(config, "CustomModelData") ? "CustomModelData" : null;
      } else {
         return isStructuredValue(config, "Options.CustomModelData") ? "Options.CustomModelData" : null;
      }
   }

   public boolean hasValues() {
      return !this.floats.isEmpty() || !this.flags.isEmpty() || !this.strings.isEmpty() || !this.colors.isEmpty();
   }

   @Override
   public void apply(MythicItem mythicItem, DropMetadata dropMetadata, AbstractItemStack itemStack) {
      itemStack.applyCustomModelDataComponent(dropMetadata, this);
   }

   private static boolean isStructuredValue(MythicConfig config, String key) {
      if (!config.isSet(key)) {
         return false;
      } else if (config.isList(key)) {
         return true;
      } else if (!config.isInt(key) && !config.isDouble(key)) {
         String value = config.getString(key, null);
         return value != null && Numbers.parseInteger(value.trim()).isEmpty();
      } else {
         return false;
      }
   }

   private void parseTypedStringEntry(MythicItem mythicItem, MythicConfig config, String entry) {
      String[] split = entry.split("/", 2);
      if (split.length != 2) {
         MythicLogger.errorItemConfig(mythicItem, config, "CustomModelData list entries must use type/value syntax. Line=" + entry);
      } else {
         this.addValue(mythicItem, config, split[0], split[1], entry);
      }
   }

   private void parseScalarStringEntry(MythicItem mythicItem, MythicConfig config, String entry) {
      String[] split = entry.split("/", 2);
      if (split.length == 2 && this.isKnownType(split[0])) {
         this.addValue(mythicItem, config, split[0], split[1], entry);
      } else {
         this.strings.add(entry);
      }
   }

   private boolean isKnownType(String rawType) {
      if (rawType == null) {
         return false;
      } else {
         String str = rawType.trim().toUpperCase(Locale.ROOT);

         return switch (str) {
            case "FLOAT", "NUMBER", "DOUBLE", "INTEGER", "INT", "BOOLEAN", "BOOL", "FLAG", "STRING", "TEXT", "COLOR", "COLOUR" -> true;
            default -> false;
         };
      }
   }

   private void parseMapEntry(MythicItem mythicItem, MythicConfig config, Map<?, ?> entry) {
      Object type = this.findMapValue(entry, "Type");
      Object value = this.findMapValue(entry, "Value");
      Object values = value != null ? value : this.findMapValue(entry, "Values");
      if (type != null && values != null) {
         if (values instanceof Iterable) {
            for (Object element : (Iterable)values) {
               this.addValue(mythicItem, config, type.toString(), element, entry.toString());
            }
         } else {
            this.addValue(mythicItem, config, type.toString(), values, entry.toString());
         }
      } else {
         MythicLogger.errorItemConfig(mythicItem, config, "CustomModelData map entries must define Type and Value");
      }
   }

   private Object findMapValue(Map<?, ?> map, String key) {
      for (Entry<?, ?> entry : map.entrySet()) {
         if (entry.getKey() != null && key.equalsIgnoreCase(entry.getKey().toString())) {
            return entry.getValue();
         }
      }

      return null;
   }

   private void addValue(MythicItem mythicItem, MythicConfig config, String rawType, Object value, String source) {
      if (rawType != null && value != null) {
         String type = rawType.trim().toUpperCase(Locale.ROOT);
         switch (type) {
            case "FLOAT":
            case "NUMBER":
            case "DOUBLE":
            case "INTEGER":
            case "INT":
               this.addFloatValues(mythicItem, config, value, source);
               break;
            case "BOOLEAN":
            case "BOOL":
            case "FLAG":
               this.addFlagValues(mythicItem, config, value, source);
               break;
            case "STRING":
            case "TEXT":
               this.strings.add(value.toString());
               break;
            case "COLOR":
            case "COLOUR":
               this.addColorValue(mythicItem, config, value, source);
               break;
            default:
               MythicLogger.errorItemConfig(mythicItem, config, "Unknown CustomModelData type '" + rawType + "'. Line=" + source);
         }
      }
   }

   private void addFloatValues(MythicItem mythicItem, MythicConfig config, Object value, String source) {
      if (value instanceof Number number) {
         this.floats.add(number.floatValue());
      } else {
         for (String part : this.splitCommaValues(value.toString())) {
            try {
               this.floats.add(Float.parseFloat(part));
            } catch (NumberFormatException e) {
               MythicLogger.errorItemConfig(mythicItem, config, "Invalid CustomModelData FLOAT value '" + part + "'. Line=" + source);
            }
         }
      }
   }

   private void addFlagValues(MythicItem mythicItem, MythicConfig config, Object value, String source) {
      if (value instanceof Boolean bool) {
         this.flags.add(bool);
      } else {
         for (String part : this.splitCommaValues(value.toString())) {
            String normalized = part.toLowerCase(Locale.ROOT);
            switch (normalized) {
               case "true":
               case "yes":
               case "on":
                  this.flags.add(true);
                  break;
               case "false":
               case "no":
               case "off":
                  this.flags.add(false);
                  break;
               default:
                  MythicLogger.errorItemConfig(mythicItem, config, "Invalid CustomModelData BOOLEAN value '" + part + "'. Line=" + source);
            }
         }
      }
   }

   private void addColorValue(MythicItem mythicItem, MythicConfig config, Object value, String source) {
      if (value instanceof Color color) {
         this.colors.add(color);
      } else {
         try {
            this.colors.add(Chroma.of(value.toString()).toBukkitColor());
         } catch (Throwable e) {
            MythicLogger.errorItemConfig(mythicItem, config, "Invalid CustomModelData COLOR value '" + value + "'. Line=" + source);
         }
      }
   }

   private List<String> splitCommaValues(String value) {
      ArrayList<String> values = new ArrayList<>();

      for (String part : value.split(",")) {
         String trimmed = part.trim();
         if (!trimmed.isEmpty()) {
            values.add(trimmed);
         }
      }

      return values;
   }

   @Generated
   @Override
   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof AbstractItemCustomModelDataComponent other)) {
         return false;
      } else if (!other.canEqual(this)) {
         return false;
      } else {
         Object this$floats = this.getFloats();
         Object other$floats = other.getFloats();
         if (this$floats == null ? other$floats == null : this$floats.equals(other$floats)) {
            Object this$flags = this.getFlags();
            Object other$flags = other.getFlags();
            if (this$flags == null ? other$flags == null : this$flags.equals(other$flags)) {
               Object this$strings = this.getStrings();
               Object other$strings = other.getStrings();
               if (this$strings == null ? other$strings == null : this$strings.equals(other$strings)) {
                  Object this$colors = this.getColors();
                  Object other$colors = other.getColors();
                  return this$colors == null ? other$colors == null : this$colors.equals(other$colors);
               } else {
                  return false;
               }
            } else {
               return false;
            }
         } else {
            return false;
         }
      }
   }

   @Generated
   protected boolean canEqual(Object other) {
      return other instanceof AbstractItemCustomModelDataComponent;
   }

   @Generated
   @Override
   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      Object $floats = this.getFloats();
      result = result * 59 + ($floats == null ? 43 : $floats.hashCode());
      Object $flags = this.getFlags();
      result = result * 59 + ($flags == null ? 43 : $flags.hashCode());
      Object $strings = this.getStrings();
      result = result * 59 + ($strings == null ? 43 : $strings.hashCode());
      Object $colors = this.getColors();
      return result * 59 + ($colors == null ? 43 : $colors.hashCode());
   }
}
