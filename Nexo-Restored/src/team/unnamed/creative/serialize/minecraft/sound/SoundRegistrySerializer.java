package team.unnamed.creative.serialize.minecraft.sound;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import net.kyori.adventure.key.Key;
import org.intellij.lang.annotations.Subst;
import team.unnamed.creative.metadata.pack.PackFormat;
import team.unnamed.creative.serialize.minecraft.GsonUtil;
import team.unnamed.creative.serialize.minecraft.base.KeySerializer;
import team.unnamed.creative.serialize.minecraft.io.JsonResourceSerializer;
import team.unnamed.creative.sound.SoundEntry;
import team.unnamed.creative.sound.SoundEvent;
import team.unnamed.creative.sound.SoundRegistry;

public final class SoundRegistrySerializer implements JsonResourceSerializer<SoundRegistry> {
   public static final SoundRegistrySerializer INSTANCE = new SoundRegistrySerializer();

   public void serializeToJson(SoundRegistry registry, JsonWriter writer, PackFormat packFormat) throws IOException {
      writer.beginObject();

      for(SoundEvent event : registry.sounds()) {
         writer.name(event.key().value()).beginObject();
         boolean replace = event.replace();
         if (replace) {
            writer.name("replace").value(replace);
         }

         String subtitle = event.subtitle();
         if (subtitle != null) {
            writer.name("subtitle").value(subtitle);
         }

         List<SoundEntry> sounds = event.sounds();
         writer.name("sounds").beginArray();

         for(SoundEntry sound : sounds) {
            if (sound.allDefault()) {
               writer.value(KeySerializer.toString(sound.key()));
            } else {
               writer.beginObject().name("name").value(KeySerializer.toString(sound.key()));
               float volume = sound.volume();
               if (volume != 1.0F) {
                  writer.name("volume").value(volume);
               }

               float pitch = sound.pitch();
               if (pitch != 1.0F) {
                  writer.name("pitch").value(pitch);
               }

               float weight = (float)sound.weight();
               if (weight != 1.0F) {
                  writer.name("weight").value(weight);
               }

               boolean stream = sound.stream();
               if (stream) {
                  writer.name("stream").value(stream);
               }

               int attenuationDistance = sound.attenuationDistance();
               if (attenuationDistance != 16) {
                  writer.name("attenuation_distance").value((long)attenuationDistance);
               }

               boolean preload = sound.preload();
               if (preload) {
                  writer.name("preload").value(preload);
               }

               SoundEntry.Type type = sound.type();
               if (type != SoundEntry.DEFAULT_TYPE) {
                  writer.name("type").value(type.name().toLowerCase(Locale.ROOT));
               }

               writer.endObject();
            }
         }

         writer.endArray();
         writer.endObject();
      }

      writer.endObject();
   }

   public SoundRegistry readFromTree(JsonElement node, @Subst("minecraft") String namespace) {
      Set<SoundEvent> soundEvents = new LinkedHashSet();
      JsonObject objectNode = node.getAsJsonObject();

      for(Map.Entry<String, JsonElement> soundEventEntry : objectNode.entrySet()) {
         String eventKey = (String)soundEventEntry.getKey();
         JsonObject eventNode = ((JsonElement)soundEventEntry.getValue()).getAsJsonObject();
         SoundEvent.Builder event = SoundEvent.soundEvent().key(Key.key(namespace, eventKey));
         event.replace(GsonUtil.getBoolean(eventNode, "replace", false));
         if (!GsonUtil.isNullOrAbsent(eventNode, "subtitle")) {
            event.subtitle(eventNode.get("subtitle").getAsString());
         }

         if (eventNode.has("sounds")) {
            List<SoundEntry> sounds = new ArrayList();

            for(JsonElement soundNode : eventNode.getAsJsonArray("sounds")) {
               if (soundNode.isJsonObject()) {
                  JsonObject soundObjectNode = soundNode.getAsJsonObject();
                  SoundEntry.Builder sound = SoundEntry.soundEntry().key(Key.key(soundObjectNode.get("name").getAsString())).volume(GsonUtil.getFloat(soundObjectNode, "volume", 1.0F)).pitch(GsonUtil.getFloat(soundObjectNode, "pitch", 1.0F)).weight(GsonUtil.getInt(soundObjectNode, "weight", 1)).stream(GsonUtil.getBoolean(soundObjectNode, "stream", false)).attenuationDistance(GsonUtil.getInt(soundObjectNode, "attenuation_distance", 16)).preload(GsonUtil.getBoolean(soundObjectNode, "preload", false));
                  if (soundObjectNode.has("type")) {
                     String typeName = soundObjectNode.get("type").getAsString();
                     SoundEntry.Type type = SoundEntry.Type.valueOf(typeName.toUpperCase(Locale.ROOT));
                     sound.type(type);
                  }

                  sounds.add(sound.build());
               } else {
                  sounds.add(SoundEntry.soundEntry().key(Key.key(soundNode.getAsString())).type(SoundEntry.Type.FILE).build());
               }
            }

            event.sounds(sounds);
         }

         soundEvents.add(event.build());
      }

      return SoundRegistry.soundRegistry(namespace, soundEvents);
   }
}
