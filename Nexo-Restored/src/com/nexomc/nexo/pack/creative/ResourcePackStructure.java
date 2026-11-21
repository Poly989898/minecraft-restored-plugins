package com.nexomc.nexo.pack.creative;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Queue;
import java.util.Set;
import kotlin.Metadata;
import kotlin.collections.ArraysKt;
import kotlin.collections.CollectionsKt;
import kotlin.collections.SetsKt;
import kotlin.jvm.JvmField;
import kotlin.jvm.functions.Function1;
import kotlin.text.StringsKt;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.key.Keyed;
import org.jetbrains.annotations.NotNull;
import team.unnamed.creative.sound.SoundRegistry;
import team.unnamed.creative.texture.Texture;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000J\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0011\n\u0002\u0010#\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0011\n\u0000\n\u0002\u0010\u001c\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\bÀ\u0002\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u000e\u0010\u001c\u001a\u00020\u00052\u0006\u0010\u001d\u001a\u00020\u001eJ\u000e\u0010\u001c\u001a\u00020\u00052\u0006\u0010\u001f\u001a\u00020 J\u000e\u0010!\u001a\u00020\u00052\u0006\u0010\u001f\u001a\u00020 J\u0014\u0010\"\u001a\b\u0012\u0004\u0012\u00020\u00050#2\u0006\u0010$\u001a\u00020\u0005J\u001f\u0010$\u001a\u00020\u00052\u0012\u0010$\u001a\n\u0012\u0006\b\u0001\u0012\u00020\u00050%\"\u00020\u0005¢\u0006\u0002\u0010&J\u0016\u0010$\u001a\u00020\u00052\u000e\u0010$\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u00050'J\u001e\u0010(\u001a\u00020\u00052\u0006\u0010)\u001a\u00020\u00052\u0006\u0010*\u001a\u00020+2\u0006\u0010,\u001a\u00020\u0005R\u000e\u0010\u0004\u001a\u00020\u0005X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0005X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0005X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\u0005X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\u0005X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u0005X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\u0005X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\u0005X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\r\u001a\u00020\u0005X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\u000e\u001a\u00020\u0005X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\u000f\u001a\u00020\u0005X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\u0010\u001a\u00020\u0005X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\u0011\u001a\u00020\u0005X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\u0012\u001a\u00020\u0005X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\u0013\u001a\u00020\u0005X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\u0014\u001a\u00020\u0005X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\u0015\u001a\u00020\u0005X\u0086T¢\u0006\u0002\n\u0000R\u001b\u0010\u0016\u001a\b\u0012\u0004\u0012\u00020\u00050\u00178\u0006X\u0087\u0004\u0092\u0002\u0002\b\u0018¢\u0006\u0002\n\u0000R\u000e\u0010\u0019\u001a\u00020\u0005X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\u001a\u001a\u00020\u0005X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\u001b\u001a\u00020\u0005X\u0086T¢\u0006\u0002\n\u0000¨\u0006-"},
   d2 = {"Lcom/nexomc/nexo/pack/creative/ResourcePackStructure;", "", "<init>", "()V", "TEXTURE_EXTENSION", "", "METADATA_EXTENSION", "OBJECT_EXTENSION", "FILE_SEPARATOR", "PACK_METADATA_FILE", "PACK_ICON_FILE", "ASSETS_FOLDER", "OPTIFINE_FOLDER", "SOUNDS_FILE", "TEXTURES_FOLDER", "TEXTS_FOLDER", "PACK_ZIP", "EXTERNAL_PACKS_FOLDER", "TEMPLATE_PACKS_FOLDER", "ASSET_CACHE_FOLDER", "DEOBF_CACHE_FOLDER", "PACKSQUASH_FOLDER", "IGNORED_NEXO_FOLDERS", "", "Lkotlin/jvm/JvmField;", "DS_STORE_EXTENSION", "DB_EXTENSION", "TXT_EXTENSION", "pathOf", "registry", "Lteam/unnamed/creative/sound/SoundRegistry;", "texture", "Lteam/unnamed/creative/texture/Texture;", "pathOfMeta", "tokenize", "Ljava/util/Queue;", "path", "", "([Ljava/lang/String;)Ljava/lang/String;", "", "withCategory", "categoryFolder", "resource", "Lnet/kyori/adventure/key/Keyed;", "extension", "com.nexomc:core"}
)
public final class ResourcePackStructure {
   @NotNull
   public static final ResourcePackStructure INSTANCE = new ResourcePackStructure();
   @NotNull
   public static final String TEXTURE_EXTENSION = ".png";
   @NotNull
   public static final String METADATA_EXTENSION = ".mcmeta";
   @NotNull
   public static final String OBJECT_EXTENSION = ".json";
   @NotNull
   public static final String FILE_SEPARATOR = "/";
   @NotNull
   public static final String PACK_METADATA_FILE = "pack.mcmeta";
   @NotNull
   public static final String PACK_ICON_FILE = "pack.png";
   @NotNull
   public static final String ASSETS_FOLDER = "assets";
   @NotNull
   public static final String OPTIFINE_FOLDER = "optifine";
   @NotNull
   public static final String SOUNDS_FILE = "sounds.json";
   @NotNull
   public static final String TEXTURES_FOLDER = "textures";
   @NotNull
   public static final String TEXTS_FOLDER = "texts";
   @NotNull
   public static final String PACK_ZIP = "pack.zip";
   @NotNull
   public static final String EXTERNAL_PACKS_FOLDER = "external_packs";
   @NotNull
   public static final String TEMPLATE_PACKS_FOLDER = "template_packs";
   @NotNull
   public static final String ASSET_CACHE_FOLDER = ".assetCache";
   @NotNull
   public static final String DEOBF_CACHE_FOLDER = ".deobfCachedPacks";
   @NotNull
   public static final String PACKSQUASH_FOLDER = "packsquash";
   @JvmField
   @NotNull
   public static final Set<String> IGNORED_NEXO_FOLDERS;
   @NotNull
   public static final String DS_STORE_EXTENSION = ".DS_Store";
   @NotNull
   public static final String DB_EXTENSION = ".db";
   @NotNull
   public static final String TXT_EXTENSION = ".txt";

   private ResourcePackStructure() {
   }

   @NotNull
   public final String pathOf(@NotNull SoundRegistry registry) {
      String[] strs = new String[]{"assets", null, null};
      String namespace = registry.namespace();
      strs[1] = namespace;
      strs[2] = "sounds.json";
      return this.path(strs);
   }

   @NotNull
   public final String pathOf(@NotNull Texture texture) {
      Key key2 = texture.key();
      Key key = key2;
      String[] strs = new String[4];
      strs[0] = "assets";
      String namespace = key.namespace();
      strs[1] = namespace;
      strs[2] = "textures";
      namespace = key.value();
      strs[3] = namespace;
      return this.path(strs);
   }

   @NotNull
   public final String pathOfMeta(@NotNull Texture texture) {
      return this.withCategory("textures", texture, ".mcmeta");
   }

   @NotNull
   public final Queue<String> tokenize(@NotNull String path) {
      List list;
      block: {
         CharSequence charSequence = (CharSequence)path;
         String[] strs = new String[]{"/"};
         List list2 = StringsKt.split(charSequence, strs, false, 0);
         if (!list2.isEmpty()) {
            ListIterator listIterator = list2.listIterator(list2.size());

            while(listIterator.hasPrevious()) {
               String p0 = (String)listIterator.previous();
               int value = 0;
               if (((CharSequence)p0).length() != 0) {
                  list = CollectionsKt.take((Iterable)list2, listIterator.nextIndex() + 1);
                  break block;
               }
            }
         }

         list = CollectionsKt.emptyList();
      }

      Collection collection = (Collection)list;
      return (Queue)(new LinkedList(collection));
   }

   @NotNull
   public final String path(@NotNull String... path) {
      return ArraysKt.joinToString(path, (CharSequence)"/", (CharSequence)"", (CharSequence)"", -1, (CharSequence)"...", (Function1)null);
   }

   @NotNull
   public final String path(@NotNull Iterable<String> path) {
      return CollectionsKt.joinToString(path, (CharSequence)"/", (CharSequence)"", (CharSequence)"", -1, (CharSequence)"...", (Function1)null);
   }

   @NotNull
   public final String withCategory(@NotNull String categoryFolder, @NotNull Keyed resource, @NotNull String extension) {
      Key key2 = resource.key();
      Key key = key2;
      String[] strs = new String[4];
      strs[0] = "assets";
      String namespace = key.namespace();
      strs[1] = namespace;
      strs[2] = categoryFolder;
      namespace = key.value();
      strs[3] = namespace + extension;
      return this.path(strs);
   }

   static {
      String[] strs = new String[]{"pack.zip", "external_packs", "template_packs", ".assetCache", ".deobfCachedPacks", "packsquash"};
      IGNORED_NEXO_FOLDERS = SetsKt.mutableSetOf(strs);
   }
}
