package com.nexomc.nexo.pack.shaders;

import com.nexomc.nexo.NexoPlugin;
import com.nexomc.nexo.glyphs.AnimatedGlyph;
import com.nexomc.nexo.glyphs.ShaderAnimatedGlyph;
import com.nexomc.nexo.glyphs.SpriteAnimatedGlyph;
import com.nexomc.nexo.pack.NexoOverlay;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.text.StringsKt;
import org.jetbrains.annotations.NotNull;
import team.unnamed.creative.ResourcePack;
import team.unnamed.creative.base.Writable;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000,\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\u0006\u0010\t\u001a\u00020\nJ\u0014\u0010\u000b\u001a\u00020\n*\u00020\f2\u0006\u0010\r\u001a\u00020\u000eH\u0002R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u0016\u0010\u0006\u001a\n \b*\u0004\u0018\u00010\u00070\u0007X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u000f"},
   d2 = {"Lcom/nexomc/nexo/pack/shaders/GifGenerator;", "", "resourcePack", "Lteam/unnamed/creative/ResourcePack;", "<init>", "(Lteam/unnamed/creative/ResourcePack;)V", "classLoader", "Ljava/lang/ClassLoader;", "kotlin.jvm.PlatformType", "generateGifFiles", "", "writables", "Lcom/nexomc/nexo/pack/NexoOverlay;", "version", "", "com.nexomc:core"}
)
public final class GifGenerator {
   @NotNull
   private final ResourcePack resourcePack;
   private final ClassLoader classLoader;

   public GifGenerator(@NotNull ResourcePack resourcePack) {
      super();
      this.resourcePack = resourcePack;
      this.classLoader = NexoPlugin.Companion.instance().getClass().getClassLoader();
   }

   public final void generateGifFiles() {
      Iterable glyphs = (Iterable)NexoPlugin.Companion.instance().fontManager().glyphs();
      Collection destination = (Collection)(new ArrayList());

      for(Object element : glyphs) {
         if (element instanceof AnimatedGlyph) {
            destination.add(element);
         }
      }

      glyphs = (Iterable)((List)destination);

      for(Object obj : glyphs) {
         AnimatedGlyph glyph = (AnimatedGlyph)obj;
         int value = 0;
         glyph.generateSplitGif(this.resourcePack);
      }

      List it = (List)glyphs;
      int value4 = 0;
      Iterable iterable = (Iterable)it;
      boolean flag;
      if (iterable instanceof Collection && ((Collection)iterable).isEmpty()) {
         flag = false;
      } else {
         Iterator iterator = iterable.iterator();

         while(true) {
            if (!iterator.hasNext()) {
               flag = false;
               break;
            }

            Object obj2 = iterator.next();
            AnimatedGlyph g = (AnimatedGlyph)obj2;
            int value2 = 0;
            if (g instanceof ShaderAnimatedGlyph) {
               flag = true;
               break;
            }
         }
      }

      if (flag) {
         it = (List)glyphs;
         value4 = 0;
         this.writables(NexoOverlay.V1_21_1, "v1_21_1");
         this.writables(NexoOverlay.V1_21_3, "v1_21_3");
         this.writables(NexoOverlay.V1_21_4, "v1_21_3");
         this.writables(NexoOverlay.V1_21_5, "v1_21_3");
         this.writables(NexoOverlay.V1_21_6, "v1_21_6");
         this.writables(NexoOverlay.V1_21_11, "v1_21_6");
         this.writables(NexoOverlay.V26_1, "v26_1");
      }

      it = (List)glyphs;
      value4 = 0;
      iterable = (Iterable)it;
      if (iterable instanceof Collection && ((Collection)iterable).isEmpty()) {
         flag = false;
      } else {
         Iterator iterator2 = iterable.iterator();

         while(true) {
            if (!iterator2.hasNext()) {
               flag = false;
               break;
            }

            Object obj3 = iterator2.next();
            AnimatedGlyph g = (AnimatedGlyph)obj3;
            int value3 = 0;
            if (g instanceof SpriteAnimatedGlyph) {
               flag = true;
               break;
            }
         }
      }

      if (flag) {
         it = (List)glyphs;
         value4 = 0;
      }

   }

   private final void writables(NexoOverlay nexoOverlay, String version) {
      try {
      String str;
      block: {
         String basePath = "assets/minecraft/shaders/core";
         String baseName = "shaders/gifs/" + version;
         writables$writeShaderFiles(basePath, baseName, nexoOverlay, this, "rendertype_text");
         writables$writeShaderFiles(basePath, baseName, nexoOverlay, this, "rendertype_text_see_through");
         InputStream resourceAsStream = this.classLoader.getResourceAsStream("shaders/gifs/nexo_gif_utils.glsl");
         if (resourceAsStream != null) {
            byte[] readAllBytes = resourceAsStream.readAllBytes();
            if (readAllBytes != null) {
               str = StringsKt.decodeToString(readAllBytes);
               if (str != null) {
                  break block;
               }
            }
         }

         str = "";
      }

      String gifText = str;
      Writable writable = Writable.stringUtf8(StringsKt.replace(gifText, "%PACK_FORMAT%", String.valueOf(nexoOverlay.getFormat()), false));
      Writable gifGlsl = writable;
      nexoOverlay.getOverlay().unknownFile("assets/minecraft/shaders/include/nexo_gif_utils.glsl", gifGlsl);
      } catch (Exception e) {
         throw com.nexomc.nexo.utils.SneakyThrow.sneaky(e);
      }
   }

   private static final void writables$writeShaderFiles(String basePath, String baseName, NexoOverlay nexoOverlay, GifGenerator gifGenerator, String suffix) {
      String fullPath = basePath + "/" + suffix;
      String fullName = baseName + "/" + suffix;
      String[] strs = new String[]{"json", "vsh", "fsh"};
      Iterable iterable = (Iterable)CollectionsKt.listOf(strs);

      for(Object element : iterable) {
         String ext = (String)element;
         int value = 0;
         nexoOverlay.getOverlay().unknownFile(fullPath + "." + ext, Writable.resource(gifGenerator.classLoader, fullName + "." + ext));
      }

   }
}
