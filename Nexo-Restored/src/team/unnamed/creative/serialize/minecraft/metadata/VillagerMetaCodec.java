package team.unnamed.creative.serialize.minecraft.metadata;

import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.util.Locale;
import org.jetbrains.annotations.NotNull;
import team.unnamed.creative.metadata.villager.VillagerMeta;

final class VillagerMetaCodec implements MetadataPartCodec<VillagerMeta> {
   public @NotNull Class<VillagerMeta> type() {
      return VillagerMeta.class;
   }

   public @NotNull String name() {
      return "villager";
   }

   public @NotNull VillagerMeta read(@NotNull JsonObject node) {
      String hatName = node.get("hat").getAsString();
      VillagerMeta.Hat hat = VillagerMeta.Hat.valueOf(hatName.toUpperCase(Locale.ROOT));
      return VillagerMeta.villager(hat);
   }

   public void write(@NotNull JsonWriter writer, @NotNull VillagerMeta villager) throws IOException {
      writer.beginObject();
      VillagerMeta.Hat hat = villager.hat();
      if (hat != VillagerMeta.Hat.NONE) {
         writer.name("hat").value(hat.name().toLowerCase(Locale.ROOT));
      }

      writer.endObject();
   }
}
