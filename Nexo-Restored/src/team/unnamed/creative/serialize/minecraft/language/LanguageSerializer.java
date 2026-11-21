package team.unnamed.creative.serialize.minecraft.language;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import net.kyori.adventure.key.Key;
import team.unnamed.creative.lang.Language;
import team.unnamed.creative.metadata.pack.PackFormat;
import team.unnamed.creative.overlay.ResourceContainer;
import team.unnamed.creative.serialize.minecraft.ResourceCategoryImpl;
import team.unnamed.creative.serialize.minecraft.io.JsonResourceDeserializer;
import team.unnamed.creative.serialize.minecraft.io.JsonResourceSerializer;

public final class LanguageSerializer implements JsonResourceSerializer<Language>, JsonResourceDeserializer<Language> {
   public static final LanguageSerializer INSTANCE = new LanguageSerializer();
   public static final ResourceCategoryImpl<Language> CATEGORY;

   public void serializeToJson(Language language, JsonWriter writer, PackFormat packFormat) throws IOException {
      writer.beginObject();

      for(Map.Entry<String, String> entry : language.translations().entrySet()) {
         writer.name((String)entry.getKey()).value((String)entry.getValue());
      }

      writer.endObject();
   }

   public Language deserializeFromJson(JsonElement node, Key key, PackFormat packFormat) {
      JsonObject objectNode = node.getAsJsonObject();
      Map<String, String> translations = new LinkedHashMap();

      for(Map.Entry<String, JsonElement> translationEntry : objectNode.entrySet()) {
         String translationKey = (String)translationEntry.getKey();
         JsonElement translationValue = (JsonElement)translationEntry.getValue();
         if (translationValue.isJsonPrimitive()) {
            translations.put(translationKey, translationValue.getAsString());
         }
      }

      return Language.language(key, translations);
   }

   static {
      CATEGORY = new ResourceCategoryImpl<Language>("lang", ".json", ResourceContainer::languages, INSTANCE);
   }
}
