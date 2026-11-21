package io.lumine.mythic.core.skills.variables.types;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractItemStack;
import io.lumine.mythic.api.drops.DropMetadata;
import io.lumine.mythic.api.mobs.GenericCaster;
import io.lumine.mythic.api.skills.SkillCaster;
import io.lumine.mythic.api.skills.placeholders.PolymorphicPlaceholder;
import io.lumine.mythic.api.volatilecode.VolatileCodeHandler;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.adapters.BukkitItemStack;
import io.lumine.mythic.bukkit.utils.serialize.InventorySerialization;
import io.lumine.mythic.core.drops.DropMetadataImpl;
import io.lumine.mythic.core.drops.DropTable;
import io.lumine.mythic.core.drops.EquipSlot;
import io.lumine.mythic.core.drops.LootBag;
import io.lumine.mythic.core.items.MythicItem;
import io.lumine.mythic.core.skills.placeholders.PlaceholderContext;
import io.lumine.mythic.core.skills.variables.Variable;
import io.lumine.mythic.core.skills.variables.VariableInfo;
import io.lumine.mythic.core.skills.variables.VariableType;
import io.lumine.mythic.core.skills.variables.VariableUtils;
import io.lumine.mythic.core.utils.MythicGson;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ItemVariable extends Variable {
   BukkitItemStack value;
   public static final VariableInfo<BukkitItemStack> variableInfo = VariableInfo.<BukkitItemStack>builder()
      .clazz(ItemVariable.class)
      .type(VariableType.ITEM)
      .typeName("Item")
      .valueClass(BukkitItemStack.class)
      .objectConstructor(ItemVariable::ofObject)
      .polymorphicConstructor(ItemVariable::of)
      .build();
   private static final Map<String, BiFunction<String, PlaceholderContext, BukkitItemStack>> itemPrefixConversionMap = new ConcurrentHashMap<>();

   public ItemVariable(BukkitItemStack value, long expirationTime) {
      super(expirationTime);
      if (value == null) {
         this.value = BukkitAdapter.adapt(new ItemStack(Material.AIR));
      } else {
         this.value = value;
      }
   }

   public ItemVariable(String value) {
      this.value = ItemVariable.Utils.deserializeFromString(value);
   }

   public ItemVariable(String value, long expirationTime) {
      super(expirationTime);
      this.value = ItemVariable.Utils.deserializeFromString(value);
   }

   @Override
   public VariableInfo<BukkitItemStack> getVariableInfo() {
      return variableInfo;
   }

   public static ItemVariable of(PolymorphicPlaceholder data, PlaceholderContext context, long expirationTime) {
      return new ItemVariable(fromStringWithContext(data.getString(context), context), expirationTime);
   }

   public static String toString(BukkitItemStack value) {
      return ItemVariable.Utils.serializeToString(value);
   }

   public static BukkitItemStack fromString(String value) {
      return fromStringWithContext(value, null);
   }

   public static BukkitItemStack fromStringWithContext(String value, PlaceholderContext context) {
      if (value.startsWith("{")) {
         return ItemVariable.Utils.deserializeFromString(value);
      } else {
         String key = value.trim();

         for (Entry<String, BiFunction<String, PlaceholderContext, BukkitItemStack>> entry : itemPrefixConversionMap.entrySet()) {
            if (key.startsWith(entry.getKey())) {
               return entry.getValue().apply(value.substring(entry.getKey().length() + 1), context);
            }
         }

         return ItemVariable.Utils.deserializeFromString(value);
      }
   }

   public static String formatted(BukkitItemStack value) {
      return value.toString();
   }

   public static ItemVariable ofObject(Object o, Long expirationTime) {
      if (o instanceof ItemStack stack) {
         return new ItemVariable(BukkitAdapter.adapt(stack), expirationTime);
      } else if (o instanceof BukkitItemStack stack) {
         return new ItemVariable(stack, expirationTime);
      } else {
         return o instanceof MythicItem mi
            ? new ItemVariable((BukkitItemStack)mi.generateItemStack(1), expirationTime)
            : new ItemVariable(String.valueOf(o), expirationTime);
      }
   }

   public BukkitItemStack get() {
      return this.value;
   }

   public BukkitItemStack getValueClone() {
      return (BukkitItemStack)this.value.copy();
   }

   @Override
   public boolean executeAddition(PolymorphicPlaceholder data, PlaceholderContext context) {
      return false;
   }

   @Override
   public boolean executeSubtraction(PolymorphicPlaceholder data, PlaceholderContext context) {
      return false;
   }

   @Override
   public boolean checkEquality(PolymorphicPlaceholder data, PlaceholderContext context) {
      String parsed = ItemVariable.Utils.serializeToString(this.value);
      return parsed.equals(data.getString(context));
   }

   @Override
   public String toString() {
      return toString(this.value);
   }

   public static boolean registerItemPrefixConversion(String prefix, BiFunction<String, PlaceholderContext, BukkitItemStack> itemPrefixConversion) {
      return VariableUtils.registerOnMapUtil(itemPrefixConversionMap, prefix, itemPrefixConversion);
   }

   static {
      registerItemPrefixConversion("mythic", (string, context) -> {
         Optional<MythicItem> maybeItem = VolatileCodeHandler.getPlugin().getItemManager().getItem(string);
         if (maybeItem.isPresent()) {
            MythicItem item = maybeItem.get();
            return (BukkitItemStack)item.generateItemStack(1);
         } else {
            return null;
         }
      });
      registerItemPrefixConversion(
         "material",
         (string, context) -> ItemVariable.Utils.materialItemStackCache
            .computeIfAbsent(string, key -> BukkitAdapter.adapt(new ItemStack(Material.valueOf(key.toUpperCase()))))
      );
      registerItemPrefixConversion("slot", (string, context) -> {
         if (context == null) {
            throw new NullPointerException("PlaceholderContext cannot be null");
         } else {
            String parsedSlot = string.trim();
            AbstractEntity entity = null;
            if (context.entity() != null) {
               entity = context.entity();
            } else if (context.meta() != null) {
               if (context.meta().getCaster() != null) {
                  entity = context.meta().getCaster().getEntity();
               } else if (context.meta().getTrigger() != null) {
                  entity = context.meta().getTrigger();
               }
            }

            if (entity == null) {
               throw new NullPointerException("PlaceholderContext is missing entity or trigger");
            } else {
               Player player = BukkitAdapter.adapt(entity.asPlayer());
               EquipSlot tempSlot = EquipSlot.of(parsedSlot);
               ItemStack item;
               if (tempSlot != EquipSlot.NONE) {
                  Optional<AbstractItemStack> maybeNamedSlotItemStack = tempSlot.get(entity);
                  if (maybeNamedSlotItemStack.isEmpty() || !(maybeNamedSlotItemStack.get() instanceof BukkitItemStack bukkitItemStack)) {
                     return BukkitAdapter.adapt(ItemVariable.Utils.defaultMaterial.clone());
                  }

                  item = bukkitItemStack.getItemStack();
               } else {
                  int slot = Integer.parseInt(parsedSlot);
                  item = player.getInventory().getItem(slot < 41 ? slot : 0).clone();
               }

               return item == null ? BukkitAdapter.adapt(ItemVariable.Utils.defaultMaterial.clone()) : BukkitAdapter.adapt(item);
            }
         }
      });
      registerItemPrefixConversion("drop", (string, context) -> {
         DropTable droptable = new DropTable("ItemVariable", "ItemVariable", List.of(string));
         SkillCaster caster = Objects.requireNonNullElse(context.meta() != null ? context.meta().getCaster() : null, new GenericCaster(context.entity()));
         DropMetadata dropMeta = new DropMetadataImpl(caster, context.entity(), context.meta(), null);
         LootBag bag = droptable.generate(dropMeta);
         return (BukkitItemStack)bag.getSingleItem();
      });
   }

   public static class Serializer implements JsonSerializer<ItemVariable>, JsonDeserializer<ItemVariable> {
      public JsonElement serialize(ItemVariable src, Type typeOfSrc, JsonSerializationContext context) {
         JsonObject obj = new JsonObject();
         String serialized = ItemVariable.Utils.serializeToString(src.value);
         if (serialized == null || serialized.isBlank()) {
            serialized = ItemVariable.Utils.getDefaultMaterialSerialized();
         }

         JsonElement valueJson;
         try {
            valueJson = (JsonElement)MythicGson.get().fromJson(serialized, JsonElement.class);
            if (valueJson == null || valueJson.isJsonNull()) {
               valueJson = new JsonPrimitive(serialized);
            }
         } catch (Exception e) {
            valueJson = new JsonPrimitive(serialized);
         }

         obj.add("value", valueJson);
         obj.addProperty("expirationTime", src.getExpirationTime());
         obj.addProperty("type", src.getTypeName());
         return obj;
      }

      public ItemVariable deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
         JsonObject obj = json.getAsJsonObject();
         JsonElement valueElement = obj.get("value");
         String value = ItemVariable.Utils.getDefaultMaterialSerialized();
         if (valueElement != null && !valueElement.isJsonNull()) {
            value = valueElement.isJsonPrimitive() && valueElement.getAsJsonPrimitive().isString()
               ? valueElement.getAsString()
               : MythicGson.get().toJson(valueElement);
         }

         long expirationTime = obj.has("expirationTime") ? obj.get("expirationTime").getAsLong() : 0L;

         try {
            return new ItemVariable(ItemVariable.Utils.deserializeFromString(value), expirationTime);
         } catch (Exception e) {
            return new ItemVariable(BukkitAdapter.adapt(ItemVariable.Utils.defaultMaterial.clone()), expirationTime);
         }
      }
   }

   public static class Utils {
      public static ItemStack defaultMaterial = new ItemStack(Material.AIR);
      private static String defaultMaterialSerialized;
      public static final Map<String, BukkitItemStack> materialItemStackCache = new ConcurrentHashMap<>();
      private static final Type mapType = (new TypeToken<Map<String, Object>>() {}).getType();

      public static String getDefaultMaterialSerialized() {
         if (defaultMaterialSerialized == null) {
            defaultMaterialSerialized = serializeToString(BukkitAdapter.adapt(defaultMaterial));
         }

         return defaultMaterialSerialized;
      }

      public static String serializeRawToString(ItemStack item) {
         return item == null ? null : InventorySerialization.encodeItemStackToString(item);
      }

      public static String serializeToString(BukkitItemStack item) {
         return serializeRawToString(item.getItemStack());
      }

      public static ItemStack deserializeRawFromString(String data) {
         return data != null && !data.isEmpty() ? InventorySerialization.decodeItemStack(data) : null;
      }

      public static BukkitItemStack deserializeFromString(String data) {
         return BukkitAdapter.adapt(deserializeRawFromString(data));
      }
   }
}
