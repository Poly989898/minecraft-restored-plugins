package team.unnamed.creative.serialize.minecraft.metadata;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import team.unnamed.creative.metadata.language.LanguageEntry;
import team.unnamed.creative.metadata.language.LanguageMeta;
import team.unnamed.creative.serialize.minecraft.GsonUtil;

final class LanguageMetaCodec implements MetadataPartCodec<LanguageMeta> {
   public @NotNull Class<LanguageMeta> type() {
      return LanguageMeta.class;
   }

   public @NotNull String name() {
      return "language";
   }

   public @NotNull LanguageMeta read(@NotNull JsonObject node) {
      Map<String, LanguageEntry> languages = new LinkedHashMap();

      for(Map.Entry<String, JsonElement> entry : node.entrySet()) {
         String code = (String)entry.getKey();
         JsonObject languageEntryNode = ((JsonElement)entry.getValue()).getAsJsonObject();
         LanguageEntry languageEntry = LanguageEntry.languageEntry().name(languageEntryNode.get("name").getAsString()).region(languageEntryNode.get("region").getAsString()).bidirectional(GsonUtil.getBoolean(languageEntryNode, "bidirectional", false)).build();
         languages.put(code, languageEntry);
      }

      return LanguageMeta.language(languages);
   }

   public void write(@NotNull JsonWriter writer, @NotNull LanguageMeta language) throws IOException {
      writer.beginObject();

      for(Map.Entry<String, LanguageEntry> entry : language.languages().entrySet()) {
         LanguageEntry value = (LanguageEntry)entry.getValue();
         writer.name((String)entry.getKey()).beginObject().name("name").value(value.name()).name("region").value(value.region());
         boolean bidirectional = value.bidirectional();
         if (bidirectional) {
            writer.name("bidirectional").value(bidirectional);
         }

         writer.endObject();
      }

      writer.endObject();
   }
}
