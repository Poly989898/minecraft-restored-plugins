package team.unnamed.creative.serialize.minecraft.equipment;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.ApiStatus.Internal;
import team.unnamed.creative.equipment.Equipment;
import team.unnamed.creative.equipment.EquipmentLayer;
import team.unnamed.creative.equipment.EquipmentLayerDye;
import team.unnamed.creative.equipment.EquipmentLayerType;
import team.unnamed.creative.metadata.pack.PackFormat;
import team.unnamed.creative.serialize.minecraft.base.KeySerializer;
import team.unnamed.creative.serialize.minecraft.io.JsonResourceDeserializer;
import team.unnamed.creative.serialize.minecraft.io.JsonResourceSerializer;

@Internal
public final class EquipmentSerializer implements JsonResourceSerializer<Equipment>, JsonResourceDeserializer<Equipment> {
   public static final EquipmentSerializer INSTANCE = new EquipmentSerializer();

   private EquipmentSerializer() {
   }

   public void serializeToJson(Equipment equipment, JsonWriter writer, PackFormat packFormat) throws IOException {
      writer.beginObject().name("layers").beginObject();

      for(Map.Entry<EquipmentLayerType, List<EquipmentLayer>> entry : equipment.layers().entrySet()) {
         writer.name(((EquipmentLayerType)entry.getKey()).name().toLowerCase(Locale.ENGLISH)).beginArray();

         for(EquipmentLayer layer : (List<EquipmentLayer>)entry.getValue()) {
            writer.beginObject().name("texture").value(KeySerializer.toString(layer.texture()));
            boolean usePlayerTexture = layer.usePlayerTexture();
            if (usePlayerTexture) {
               writer.name("use_player_texture").value(usePlayerTexture);
            }

            EquipmentLayerDye dye = layer.dye();
            if (dye != null) {
               writer.name("dyeable").beginObject();
               Integer colorWhenUndyed = dye.colorWhenUndyed();
               if (colorWhenUndyed != null) {
                  writer.name("color_when_undyed").value((Number)colorWhenUndyed);
               }

               writer.endObject();
            }

            writer.endObject();
         }

         writer.endArray();
      }

      writer.endObject().endObject();
   }

   public Equipment deserializeFromJson(JsonElement node, Key key, PackFormat packFormat) {
      JsonObject layers = node.getAsJsonObject().getAsJsonObject("layers");
      Equipment.Builder builder = Equipment.equipment().key(key);

      for(Map.Entry<String, JsonElement> entry : layers.entrySet()) {
         EquipmentLayerType type = EquipmentLayerType.valueOf(((String)entry.getKey()).toUpperCase(Locale.ENGLISH));

         for(JsonElement element : ((JsonElement)entry.getValue()).getAsJsonArray()) {
            JsonObject object = element.getAsJsonObject();
            Key texture = Key.key(object.get("texture").getAsString());
            boolean usePlayerTexture = object.has("use_player_texture") && object.get("use_player_texture").getAsBoolean();
            EquipmentLayerDye dye;
            if (object.has("dyeable")) {
               JsonObject dyeable = object.getAsJsonObject("dyeable");
               Integer colorWhenUndyed = dyeable.has("color_when_undyed") ? dyeable.get("color_when_undyed").getAsInt() : null;
               dye = EquipmentLayerDye.equipmentLayerDye(colorWhenUndyed);
            } else {
               dye = null;
            }

            builder.addLayer(type, EquipmentLayer.layer(texture, dye, usePlayerTexture));
         }
      }

      return builder.build();
   }
}
