package team.unnamed.creative.serialize.minecraft.atlas;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.kyori.adventure.key.Key;
import team.unnamed.creative.atlas.AtlasSource;
import team.unnamed.creative.atlas.DirectoryAtlasSource;
import team.unnamed.creative.atlas.FilterAtlasSource;
import team.unnamed.creative.atlas.PalettedPermutationsAtlasSource;
import team.unnamed.creative.atlas.SingleAtlasSource;
import team.unnamed.creative.atlas.UnstitchAtlasSource;
import team.unnamed.creative.base.KeyPattern;
import team.unnamed.creative.base.Vector2Float;
import team.unnamed.creative.serialize.minecraft.base.KeyPatternSerializer;
import team.unnamed.creative.serialize.minecraft.base.KeySerializer;

final class AtlasSourceSerializer {
   private static final String TYPE_FIELD = "type";
   private static final Key SINGLE_TYPE = Key.key("single");
   private static final Key DIRECTORY_TYPE = Key.key("directory");
   private static final Key FILTER_TYPE = Key.key("filter");
   private static final Key UNSTITCH_TYPE = Key.key("unstitch");
   private static final Key PALETTED_PERMUTATIONS_TYPE = Key.key("paletted_permutations");

   static void serialize(AtlasSource source, JsonWriter writer) throws IOException {
      writer.beginObject();
      byte var3 = 0;
      //$FF: var3->value
      //0->team/unnamed/creative/atlas/SingleAtlasSource
      //1->team/unnamed/creative/atlas/DirectoryAtlasSource
      //2->team/unnamed/creative/atlas/FilterAtlasSource
      //3->team/unnamed/creative/atlas/UnstitchAtlasSource
      //4->team/unnamed/creative/atlas/PalettedPermutationsAtlasSource
      switch (com.nexomc.nexo.decomp.TypeSwitch.index(source, team.unnamed.creative.atlas.SingleAtlasSource.class, team.unnamed.creative.atlas.DirectoryAtlasSource.class, team.unnamed.creative.atlas.FilterAtlasSource.class, team.unnamed.creative.atlas.UnstitchAtlasSource.class, team.unnamed.creative.atlas.PalettedPermutationsAtlasSource.class)) {
         case -1:
         default:
            throw new IllegalArgumentException("Unknown atlas source type: '" + String.valueOf(source) + "'.");
         case 0:
            SingleAtlasSource singleSource = (SingleAtlasSource)source;
            Key resource = singleSource.resource();
            Key sprite = singleSource.sprite();
            writer.name("type").value(KeySerializer.toString(SINGLE_TYPE)).name("resource").value(KeySerializer.toString(resource));
            if (sprite != null && !sprite.equals(resource)) {
               writer.name("sprite").value(KeySerializer.toString(sprite));
            }
            break;
         case 1:
            DirectoryAtlasSource dirSource = (DirectoryAtlasSource)source;
            writer.name("type").value(KeySerializer.toString(DIRECTORY_TYPE)).name("source").value(dirSource.source()).name("prefix").value(dirSource.prefix());
            break;
         case 2:
            FilterAtlasSource filterSource = (FilterAtlasSource)source;
            writer.name("type").value(KeySerializer.toString(FILTER_TYPE)).name("pattern");
            KeyPatternSerializer.serialize(filterSource.pattern(), writer);
            break;
         case 3:
            UnstitchAtlasSource unstitchSource = (UnstitchAtlasSource)source;
            writer.name("type").value(KeySerializer.toString(UNSTITCH_TYPE)).name("resource").value(KeySerializer.toString(unstitchSource.resource()));
            double divisorX = (double)unstitchSource.divisor().x();
            if (divisorX != (double)UnstitchAtlasSource.DEFAULT_DIVISOR.x()) {
               writer.name("divisor_x").value(divisorX);
            }

            double divisorY = (double)unstitchSource.divisor().y();
            if (divisorY != (double)UnstitchAtlasSource.DEFAULT_DIVISOR.y()) {
               writer.name("divisor_y").value(divisorY);
            }

            writer.name("regions").beginArray();

            for(UnstitchAtlasSource.Region region : unstitchSource.regions()) {
               writer.beginObject().name("sprite").value(KeySerializer.toString(region.sprite())).name("x").value(region.position().x()).name("y").value(region.position().y()).name("width").value(region.dimensions().x()).name("height").value(region.dimensions().y()).endObject();
            }

            writer.endArray();
            break;
         case 4:
            PalettedPermutationsAtlasSource ppSource = (PalettedPermutationsAtlasSource)source;
            writer.name("type").value(KeySerializer.toString(PALETTED_PERMUTATIONS_TYPE)).name("textures").beginArray();

            for(Key texture : ppSource.textures()) {
               writer.value(KeySerializer.toString(texture));
            }

            writer.endArray();
            writer.name("palette_key").value(KeySerializer.toString(ppSource.paletteKey()));
            writer.name("permutations").beginObject();

            for(Map.Entry<String, Key> entry : ppSource.permutations().entrySet()) {
               writer.name((String)entry.getKey()).value(KeySerializer.toString((Key)entry.getValue()));
            }

            writer.endObject();
      }

      writer.endObject();
   }

   static AtlasSource deserialize(JsonObject node) {
      Key type = Key.key(node.get("type").getAsString());
      if (type.equals(SINGLE_TYPE)) {
         String resourceStr = node.get("resource").getAsString();
         String spriteStr = node.has("sprite") ? node.get("sprite").getAsString() : null;
         Key resource = Key.key(resourceStr);
         Key sprite = spriteStr == null ? null : Key.key(spriteStr);
         return AtlasSource.single(resource, sprite);
      } else if (type.equals(DIRECTORY_TYPE)) {
         String source = node.get("source").getAsString();
         String prefix = node.get("prefix").getAsString();
         return AtlasSource.directory(source, prefix);
      } else if (type.equals(FILTER_TYPE)) {
         KeyPattern pattern = KeyPatternSerializer.deserialize(node.getAsJsonObject("pattern"));
         return AtlasSource.filter(pattern);
      } else if (type.equals(UNSTITCH_TYPE)) {
         String resourceStr = node.get("resource").getAsString();
         Key resource = Key.key(resourceStr);
         float xDivisor = node.has("divisor_x") ? node.get("divisor_x").getAsFloat() : UnstitchAtlasSource.DEFAULT_DIVISOR.x();
         float yDivisor = node.has("divisor_y") ? node.get("divisor_y").getAsFloat() : UnstitchAtlasSource.DEFAULT_DIVISOR.y();
         List<UnstitchAtlasSource.Region> regions = new ArrayList();

         for(JsonElement regionElement : node.getAsJsonArray("regions")) {
            JsonObject regionNode = regionElement.getAsJsonObject();
            String spriteStr = regionNode.get("sprite").getAsString();
            regions.add(UnstitchAtlasSource.Region.region(Key.key(spriteStr), new Vector2Float(regionNode.get("x").getAsFloat(), regionNode.get("y").getAsFloat()), new Vector2Float(regionNode.get("width").getAsFloat(), regionNode.get("height").getAsFloat())));
         }

         return AtlasSource.unstitch(resource, regions, new Vector2Float(xDivisor, yDivisor));
      } else if (!type.equals(PALETTED_PERMUTATIONS_TYPE)) {
         throw new IllegalArgumentException("Unknown atlas source type: '" + String.valueOf(type) + "'.");
      } else {
         List<Key> textures = new ArrayList();

         for(JsonElement keyElement : node.getAsJsonArray("textures")) {
            String key = keyElement.getAsString();
            textures.add(Key.key(key));
         }

         String paletteKeyStr = node.get("palette_key").getAsString();
         Key paletteKey = Key.key(paletteKeyStr);
         Map<String, Key> permutations = new LinkedHashMap();

         for(Map.Entry<String, JsonElement> entry : node.getAsJsonObject("permutations").entrySet()) {
            String value = ((JsonElement)entry.getValue()).getAsString();
            permutations.put((String)entry.getKey(), Key.key(value));
         }

         return AtlasSource.palettedPermutations(textures, paletteKey, permutations);
      }
   }
}
