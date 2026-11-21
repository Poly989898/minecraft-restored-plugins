package team.unnamed.creative.serialize.minecraft.blockstate;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.kyori.adventure.key.Key;
import team.unnamed.creative.blockstate.BlockState;
import team.unnamed.creative.blockstate.Condition;
import team.unnamed.creative.blockstate.MultiVariant;
import team.unnamed.creative.blockstate.Selector;
import team.unnamed.creative.blockstate.Variant;
import team.unnamed.creative.metadata.pack.PackFormat;
import team.unnamed.creative.overlay.ResourceContainer;
import team.unnamed.creative.serialize.minecraft.GsonUtil;
import team.unnamed.creative.serialize.minecraft.ResourceCategoryImpl;
import team.unnamed.creative.serialize.minecraft.base.KeySerializer;
import team.unnamed.creative.serialize.minecraft.io.JsonResourceDeserializer;
import team.unnamed.creative.serialize.minecraft.io.JsonResourceSerializer;

public final class BlockStateSerializer implements JsonResourceSerializer<BlockState>, JsonResourceDeserializer<BlockState> {
   public static final BlockStateSerializer INSTANCE = new BlockStateSerializer();
   public static final ResourceCategoryImpl<BlockState> CATEGORY;

   public void serializeToJson(BlockState state, JsonWriter writer, PackFormat packFormat) throws IOException {
      writer.beginObject();
      Map<String, MultiVariant> variants = state.variants();
      if (!variants.isEmpty()) {
         writer.name("variants").beginObject();

         for(Map.Entry<String, MultiVariant> entry : variants.entrySet()) {
            writer.name((String)entry.getKey());
            writeMultiVariant(writer, (MultiVariant)entry.getValue());
         }

         writer.endObject();
      }

      List<Selector> multipart = state.multipart();
      if (!multipart.isEmpty()) {
         writer.name("multipart").beginArray();

         for(Selector selector : multipart) {
            writeSelector(writer, selector);
         }

         writer.endArray();
      }

      writer.endObject();
   }

   public BlockState deserializeFromJson(JsonElement node, Key key, PackFormat packFormat) {
      JsonObject objectNode = node.getAsJsonObject();
      Map<String, MultiVariant> variants = new LinkedHashMap();
      List<Selector> multipart = new ArrayList();
      if (objectNode.has("variants")) {
         JsonObject variantsNode = objectNode.getAsJsonObject("variants");

         for(Map.Entry<String, JsonElement> variantEntry : variantsNode.entrySet()) {
            String variantKey = (String)variantEntry.getKey();
            variants.put(variantKey, readMultiVariant((JsonElement)variantEntry.getValue()));
         }
      }

      if (objectNode.has("multipart")) {
         for(JsonElement selectorNode : objectNode.getAsJsonArray("multipart")) {
            multipart.add(readSelector(selectorNode));
         }
      }

      return BlockState.of(key, variants, multipart);
   }

   private static void writeMultiVariant(JsonWriter writer, MultiVariant multiVariant) throws IOException {
      List<Variant> variants = multiVariant.variants();
      if (variants.size() == 1) {
         writeVariant(writer, (Variant)variants.getFirst());
      } else {
         writer.beginArray();

         for(Variant variant : variants) {
            writeVariant(writer, variant);
         }

         writer.endArray();
      }

   }

   private static MultiVariant readMultiVariant(JsonElement node) {
      if (node.isJsonObject()) {
         return MultiVariant.of(readVariant(node.getAsJsonObject()));
      } else {
         List<Variant> variants = new ArrayList();

         for(JsonElement variantNode : node.getAsJsonArray()) {
            variants.add(readVariant(variantNode.getAsJsonObject()));
         }

         return MultiVariant.of(variants);
      }
   }

   private static void writeVariant(JsonWriter writer, Variant variant) throws IOException {
      writer.beginObject().name("model").value(KeySerializer.toString(variant.model()));
      int x = variant.x();
      if (x != 0) {
         writer.name("x").value((long)x);
      }

      int y = variant.y();
      if (y != 0) {
         writer.name("y").value((long)y);
      }

      boolean uvLock = variant.uvLock();
      if (uvLock) {
         writer.name("uvlock").value(uvLock);
      }

      int weight = variant.weight();
      if (weight != 1) {
         writer.name("weight").value((long)weight);
      }

      writer.endObject();
   }

   private static Variant readVariant(JsonObject node) {
      return Variant.builder().model(Key.key(node.get("model").getAsString())).x(GsonUtil.getInt(node, "x", 0)).y(GsonUtil.getInt(node, "y", 0)).uvLock(GsonUtil.getBoolean(node, "uvlock", false)).weight(GsonUtil.getInt(node, "weight", 1)).build();
   }

   private static void writeSelector(JsonWriter writer, Selector selector) throws IOException {
      writer.beginObject();
      Condition condition = selector.condition();
      if (condition != Condition.NONE) {
         writer.name("when").beginObject();
         writeCondition(writer, condition, true);
         writer.endObject();
      }

      writer.name("apply");
      writeMultiVariant(writer, selector.variant());
      writer.endObject();
   }

   private static Selector readSelector(JsonElement node) {
      JsonObject objectNode = node.getAsJsonObject();
      Condition condition = Condition.NONE;
      if (objectNode.has("when")) {
         JsonObject conditionNode = objectNode.getAsJsonObject("when");
         condition = readCondition(conditionNode);
      }

      MultiVariant multiVariant = readMultiVariant(objectNode.get("apply"));
      return Selector.of(condition, multiVariant);
   }

   private static void writeCondition(JsonWriter writer, Condition condition) throws IOException {
      writeCondition(writer, condition, false);
   }

   private static void writeCondition(JsonWriter writer, Condition condition, boolean topLevel) throws IOException {
      if (condition instanceof Condition.Match match) {
         writer.name(match.key()).value(match.value().toString());
      } else if (condition instanceof Condition.Or) {
         List<Condition> conditions = ((Condition.Or)condition).conditions();
         if (conditions.size() == 1) {
            writeCondition(writer, (Condition)conditions.getFirst());
            return;
         }

         writer.name("OR").beginArray();

         for(Condition orCondition : conditions) {
            writer.beginObject();
            writeCondition(writer, orCondition);
            writer.endObject();
         }

         writer.endArray();
      } else if (condition instanceof Condition.And) {
         List<Condition> conditions = ((Condition.And)condition).conditions();
         if (topLevel || conditions.size() == 1) {
            for(Condition child : conditions) {
               writeCondition(writer, child);
            }

            return;
         }

         writer.name("AND").beginArray();

         for(Condition child : conditions) {
            writer.beginObject();
            writeCondition(writer, child);
            writer.endObject();
         }

         writer.endArray();
      }

   }

   private static Condition readCondition(JsonObject node) {
      Set<Map.Entry<String, JsonElement>> entrySet = node.entrySet();
      if (entrySet.isEmpty()) {
         throw new IllegalStateException("No conditions");
      } else if (entrySet.size() == 1) {
         Map.Entry<String, JsonElement> entry = (Map.Entry)entrySet.iterator().next();
         JsonElement contentNode = (JsonElement)entry.getValue();
         switch ((String)entry.getKey()) {
            case "AND":
               List<Condition> conditions = new ArrayList();

               for(JsonElement element : contentNode.getAsJsonArray()) {
                  conditions.add(readCondition(element.getAsJsonObject()));
               }

               return Condition.and(conditions);
            case "OR":
               List<Condition> conditions = new ArrayList();

               for(JsonElement element : contentNode.getAsJsonArray()) {
                  conditions.add(readCondition(element.getAsJsonObject()));
               }

               return Condition.or(conditions);
            default:
               return Condition.match((String)entry.getKey(), contentNode.getAsString());
         }
      } else {
         List<Condition> conditions = new ArrayList();

         for(Map.Entry<String, JsonElement> entry : entrySet) {
            conditions.add(Condition.match((String)entry.getKey(), ((JsonElement)entry.getValue()).getAsString()));
         }

         return Condition.and(conditions);
      }
   }

   static {
      CATEGORY = new ResourceCategoryImpl<BlockState>("blockstates", ".json", ResourceContainer::blockStates, INSTANCE);
   }
}
