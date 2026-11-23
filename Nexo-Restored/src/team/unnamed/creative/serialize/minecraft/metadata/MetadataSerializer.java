package team.unnamed.creative.serialize.minecraft.metadata;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import team.unnamed.creative.metadata.Metadata;
import team.unnamed.creative.metadata.MetadataPart;
import team.unnamed.creative.metadata.pack.PackFormat;
import team.unnamed.creative.serialize.minecraft.io.JsonResourceSerializer;

public class MetadataSerializer implements JsonResourceSerializer<Metadata> {
   public static final MetadataSerializer INSTANCE = new MetadataSerializer();
   private static final Map<String, MetadataPartCodec<?>> CODECS = new LinkedHashMap();

   public void serializeToJson(Metadata metadata, JsonWriter writer, PackFormat packFormat) throws IOException {
      writer.beginObject();

      for(MetadataPart part : metadata.parts()) {
         MetadataPartCodec codec = null;

         for(MetadataPartCodec<?> c : CODECS.values()) {
            if (c.type().isInstance(part)) {
               codec = c;
               break;
            }
         }

         if (codec == null) {
            throw new IllegalStateException("Cannot find codec for metadata part: " + String.valueOf(part));
         }

         writer.name(codec.name());
         codec.write(writer, part, packFormat);
      }

      writer.endObject();
   }

   public Metadata readFromTree(JsonElement element) {
      JsonObject object = element.getAsJsonObject();
      Metadata.Builder builder = Metadata.metadata();

      for(Map.Entry<String, JsonElement> entry : object.entrySet()) {
         String partName = (String)entry.getKey();
         if (((JsonElement)entry.getValue()).isJsonObject()) {
            JsonObject partObject = ((JsonElement)entry.getValue()).getAsJsonObject();
            MetadataPartCodec<?> codec = (MetadataPartCodec)CODECS.get(partName);
            if (codec == null) {
               // stdout: unknown parts are expected (newer pack formats) and skipped; stderr would show up as ERROR lines in the server log
               System.out.println("Warning: Unknown metadata part name: " + partName + ". Skipping...");
            } else {
               this.deserializeAndAdd(builder, codec, partObject);
            }
         }
      }

      return builder.build();
   }

   private <T extends MetadataPart> void deserializeAndAdd(Metadata.Builder builder, MetadataPartCodec<T> codec, JsonObject node) {
      builder.addPart(codec.read(node));
   }

   private static void registerCodec(MetadataPartCodec<?> codec) {
      CODECS.put(codec.name(), codec);
   }

   static {
      registerCodec(AnimationMetaCodec.INSTANCE);
      registerCodec(new FilterMetaCodec());
      registerCodec(new LanguageMetaCodec());
      registerCodec(PackMetaCodec.INSTANCE);
      registerCodec(new TextureMetaCodec());
      registerCodec(new VillagerMetaCodec());
      registerCodec(OverlaysMetaCodec.INSTANCE);
      registerCodec(SodiumMetaCodec.INSTANCE);
      registerCodec(new GuiMetaCodec());
   }
}
