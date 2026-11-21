package team.unnamed.creative.serialize.minecraft;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;
import java.util.StringJoiner;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.key.Keyed;
import org.jetbrains.annotations.ApiStatus.Internal;
import team.unnamed.creative.sound.SoundRegistry;
import team.unnamed.creative.texture.Texture;

@Internal
public final class MinecraftResourcePackStructure {
   public static final String TEXTURE_EXTENSION = ".png";
   public static final String METADATA_EXTENSION = ".mcmeta";
   public static final String OBJECT_EXTENSION = ".json";
   public static final String FILE_SEPARATOR = "/";
   public static final String PACK_METADATA_FILE = "pack.mcmeta";
   public static final String PACK_ICON_FILE = "pack.png";
   public static final String ASSETS_FOLDER = "assets";
   public static final String OPTIFINE_FOLDER = "optifine";
   public static final String SOUNDS_FILE = "sounds.json";
   public static final String TEXTURES_FOLDER = "textures";
   public static final String TEXTS_FOLDER = "texts";

   private MinecraftResourcePackStructure() {
   }

   public static String pathOf(SoundRegistry registry) {
      return path("assets", registry.namespace(), "sounds.json");
   }

   public static String pathOf(Texture texture) {
      Key key = texture.key();
      return path("assets", key.namespace(), "textures", key.value());
   }

   public static String pathOfMeta(Texture texture) {
      return withCategory("textures", texture, ".mcmeta");
   }

   public static Queue<String> tokenize(String path) {
      return new LinkedList(Arrays.asList(path.split("/")));
   }

   public static String path(String... path) {
      StringJoiner joiner = new StringJoiner("/");

      for(String part : path) {
         joiner.add(part);
      }

      return joiner.toString();
   }

   public static String path(Iterable<String> path) {
      StringJoiner joiner = new StringJoiner("/");

      for(String part : path) {
         joiner.add(part);
      }

      return joiner.toString();
   }

   public static String withCategory(String categoryFolder, Keyed resource, String extension) {
      Key key = resource.key();
      String[] var10000 = new String[]{"assets", key.namespace(), categoryFolder, null};
      String var10003 = key.value();
      var10000[3] = var10003 + extension;
      return path(var10000);
   }
}
