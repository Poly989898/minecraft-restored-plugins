package team.unnamed.creative.serialize.minecraft.font;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.kyori.adventure.key.Key;
import team.unnamed.creative.base.Vector2Float;
import team.unnamed.creative.font.BitMapFontProvider;
import team.unnamed.creative.font.Font;
import team.unnamed.creative.font.FontProvider;
import team.unnamed.creative.font.ReferenceFontProvider;
import team.unnamed.creative.font.SpaceFontProvider;
import team.unnamed.creative.font.TrueTypeFontProvider;
import team.unnamed.creative.font.UnihexFontProvider;
import team.unnamed.creative.metadata.pack.PackFormat;
import team.unnamed.creative.overlay.ResourceContainer;
import team.unnamed.creative.serialize.minecraft.GsonUtil;
import team.unnamed.creative.serialize.minecraft.ResourceCategoryImpl;
import team.unnamed.creative.serialize.minecraft.base.KeySerializer;
import team.unnamed.creative.serialize.minecraft.io.JsonResourceDeserializer;
import team.unnamed.creative.serialize.minecraft.io.JsonResourceSerializer;

public final class FontSerializer implements JsonResourceSerializer<Font>, JsonResourceDeserializer<Font> {
   public static final FontSerializer INSTANCE = new FontSerializer();
   public static final ResourceCategoryImpl<Font> CATEGORY;

   public void serializeToJson(Font font, JsonWriter writer, PackFormat packFormat) throws IOException {
      writer.beginObject().name("providers").beginArray();

      for(FontProvider provider : font.providers()) {
         byte var7 = 0;
         //$FF: var7->value
         //0->team/unnamed/creative/font/BitMapFontProvider
         //1->team/unnamed/creative/font/SpaceFontProvider
         //2->team/unnamed/creative/font/TrueTypeFontProvider
         //3->team/unnamed/creative/font/ReferenceFontProvider
         //4->team/unnamed/creative/font/UnihexFontProvider
         switch (com.nexomc.nexo.decomp.TypeSwitch.index(provider, team.unnamed.creative.font.BitMapFontProvider.class, team.unnamed.creative.font.SpaceFontProvider.class, team.unnamed.creative.font.TrueTypeFontProvider.class, team.unnamed.creative.font.ReferenceFontProvider.class, team.unnamed.creative.font.UnihexFontProvider.class)) {
            case -1:
            default:
               throw new IllegalStateException("Unknown font provider type: " + String.valueOf(provider));
            case 0:
               BitMapFontProvider bitMapFontProvider = (BitMapFontProvider)provider;
               writeBitMap(writer, bitMapFontProvider);
               break;
            case 1:
               SpaceFontProvider spaceFontProvider = (SpaceFontProvider)provider;
               writeSpace(writer, spaceFontProvider);
               break;
            case 2:
               TrueTypeFontProvider trueTypeFontProvider = (TrueTypeFontProvider)provider;
               writeTrueType(writer, trueTypeFontProvider);
               break;
            case 3:
               ReferenceFontProvider referenceFontProvider = (ReferenceFontProvider)provider;
               writeReference(writer, referenceFontProvider);
               break;
            case 4:
               UnihexFontProvider unihexFontProvider = (UnihexFontProvider)provider;
               writeUnihex(writer, unihexFontProvider);
         }
      }

      writer.endArray().endObject();
   }

   public Font deserializeFromJson(JsonElement node, Key key, PackFormat packFormat) {
      JsonObject objectNode = node.getAsJsonObject();
      List<FontProvider> providers = new ArrayList();

      for(JsonElement providerNode : objectNode.getAsJsonArray("providers")) {
         JsonObject providerObjectNode = providerNode.getAsJsonObject();
         switch (providerObjectNode.get("type").getAsString()) {
            case "bitmap" -> providers.add(readBitMap(providerObjectNode));
            case "space" -> providers.add(readSpace(providerObjectNode));
            case "ttf" -> providers.add(readTrueType(providerObjectNode));
            case "reference" -> providers.add(readReference(providerObjectNode));
            case "unihex" -> providers.add(readUnihex(providerObjectNode));
            default -> throw new IllegalStateException("Unknown font provider type: " + providerObjectNode.get("type").getAsString());
         }
      }

      return Font.font(key, providers);
   }

   private static void writeBitMap(JsonWriter writer, BitMapFontProvider provider) throws IOException {
      writer.beginObject().name("type").value("bitmap").name("file").value(KeySerializer.toString(provider.file()));
      int height = provider.height();
      if (height != 8) {
         writer.name("height").value((long)height);
      }

      writer.name("ascent").value((long)provider.ascent()).name("chars").beginArray();

      for(String row : provider.characters()) {
         writer.value(row);
      }

      writer.endArray().endObject();
   }

   private static BitMapFontProvider readBitMap(JsonObject node) {
      List<String> characters = new ArrayList();

      for(JsonElement line : node.getAsJsonArray("chars")) {
         characters.add(line.getAsString());
      }

      return FontProvider.bitMap().file(Key.key(node.get("file").getAsString())).height(GsonUtil.getInt(node, "height", 8)).ascent(node.get("ascent").getAsInt()).characters(characters).build();
   }

   private static void writeSpace(JsonWriter writer, SpaceFontProvider provider) throws IOException {
      writer.beginObject().name("type").value("space").name("advances").beginObject();

      for(Map.Entry<String, Integer> entry : provider.advances().entrySet()) {
         writer.name((String)entry.getKey()).value((Number)entry.getValue());
      }

      writer.endObject().endObject();
   }

   private static SpaceFontProvider readSpace(JsonObject node) {
      JsonObject advancesNode = node.getAsJsonObject("advances");
      Map<String, Integer> advances = new LinkedHashMap();

      for(Map.Entry<String, JsonElement> advanceEntryNode : advancesNode.entrySet()) {
         String character = (String)advanceEntryNode.getKey();
         int advance = ((JsonElement)advanceEntryNode.getValue()).getAsInt();
         advances.put(character, advance);
      }

      return FontProvider.space(advances);
   }

   private static void writeUnihex(JsonWriter writer, UnihexFontProvider provider) throws IOException {
      writer.beginObject().name("type").value("unihex").name("hex_file").value(KeySerializer.toString(provider.file())).name("size_overrides").beginArray();

      for(UnihexFontProvider.SizeOverride sizeOverride : provider.sizes()) {
         writer.beginObject().name("from").value((new StringBuilder()).appendCodePoint(sizeOverride.from()).toString()).name("to").value((new StringBuilder()).appendCodePoint(sizeOverride.to()).toString()).name("left").value((long)sizeOverride.left()).name("right").value((long)sizeOverride.right()).endObject();
      }

      writer.endArray().endObject();
   }

   private static UnihexFontProvider readUnihex(JsonObject node) {
      List<UnihexFontProvider.SizeOverride> sizes = new ArrayList();

      for(JsonElement element : node.getAsJsonArray("size_overrides")) {
         JsonObject overrideNode = element.getAsJsonObject();
         sizes.add(UnihexFontProvider.SizeOverride.override(overrideNode.get("from").getAsString(), overrideNode.get("to").getAsString(), overrideNode.get("left").getAsInt(), overrideNode.get("right").getAsInt()));
      }

      return FontProvider.unihex().file(Key.key(node.get("hex_file").getAsString())).sizes(sizes).build();
   }

   private static void writeTrueType(JsonWriter writer, TrueTypeFontProvider provider) throws IOException {
      writer.beginObject().name("type").value("ttf").name("file").value(KeySerializer.toString(provider.file()));
      Vector2Float shift = provider.shift();
      if (!shift.equals(Vector2Float.ZERO)) {
         writer.name("shift").beginArray().value(shift.x()).value(shift.y()).endArray();
      }

      List<String> skip = provider.skip();
      if (!skip.isEmpty()) {
         writer.name("skip");
         if (skip.size() == 1) {
            writer.value((String)skip.getFirst());
         } else {
            writer.beginArray();

            for(String row : skip) {
               writer.value(row);
            }

            writer.endArray();
         }
      }

      float size = provider.size();
      if (size != 11.0F) {
         writer.name("size").value(size);
      }

      float oversample = provider.oversample();
      if (oversample != 1.0F) {
         writer.name("oversample").value(oversample);
      }

      writer.endObject();
   }

   private static TrueTypeFontProvider readTrueType(JsonObject node) {
      Vector2Float shift = Vector2Float.ZERO;
      if (node.has("shift")) {
         JsonArray shiftNode = node.getAsJsonArray("shift");
         shift = new Vector2Float((float)shiftNode.get(0).getAsDouble(), (float)shiftNode.get(1).getAsDouble());
      }

      List<String> skip = new ArrayList();
      if (node.has("skip")) {
         JsonElement skipNode = node.get("skip");
         if (skipNode.isJsonArray()) {
            for(JsonElement skipped : skipNode.getAsJsonArray()) {
               skip.add(skipped.getAsString());
            }
         } else {
            skip.add(skipNode.getAsString());
         }
      }

      return FontProvider.trueType().file(Key.key(node.get("file").getAsString())).shift(shift).skip(skip).size(GsonUtil.getFloat(node, "size", 11.0F)).oversample(GsonUtil.getFloat(node, "oversample", 1.0F)).build();
   }

   private static void writeReference(JsonWriter writer, ReferenceFontProvider provider) throws IOException {
      writer.beginObject();
      writer.name("type").value("reference");
      writer.name("id").value(KeySerializer.toString(provider.id()));
      writer.endObject();
   }

   private static ReferenceFontProvider readReference(JsonObject node) {
      return FontProvider.reference(Key.key(node.get("id").getAsString()));
   }

   static {
      CATEGORY = new ResourceCategoryImpl<Font>("font", ".json", ResourceContainer::fonts, INSTANCE);
   }
}
