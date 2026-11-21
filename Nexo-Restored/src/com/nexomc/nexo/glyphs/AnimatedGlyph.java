package com.nexomc.nexo.glyphs;

import com.nexomc.nexo.decomp.ResultShim;
import com.nexomc.nexo.utils.KeyUtils;
import com.nexomc.nexo.utils.NexoYamlKt;
import com.nexomc.nexo.utils.TuplesKt;
import com.nexomc.nexo.utils.UtilsKt;
import com.nexomc.nexo.utils.logs.Logs;
import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import kotlin.Metadata;
import kotlin.Result;
import kotlin.ResultKt;
import kotlin.Triple;
import kotlin.Unit;
import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import kotlin.io.CloseableKt;
import net.kyori.adventure.key.Key;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import team.unnamed.creative.ResourcePack;
import team.unnamed.creative.texture.Texture;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000L\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010!\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\n\n\u0002\u0010\u0007\n\u0002\b\u0005\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\b&\u0018\u00002\u00020\u0001:\u0001*B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\u000e\u0010\"\u001a\u00020#2\u0006\u0010$\u001a\u00020%J\u0010\u0010&\u001a\u00020\u00122\u0006\u0010'\u001a\u00020(H\u0002J\u0010\u0010)\u001a\u00020#2\u0006\u0010\u0011\u001a\u00020\u0012H\u0014R\u0011\u0010\u0006\u001a\u00020\u0007¢\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR\u0014\u0010\n\u001a\u00020\u0007X\u0096\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\tR\u001a\u0010\f\u001a\b\u0012\u0004\u0012\u00020\u000e0\rX\u0096\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010R\u001a\u0010\u0011\u001a\u00020\u0012X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0013\u0010\u0014\"\u0004\b\u0015\u0010\u0016R\u001e\u0010\u0018\u001a\u00020\u00122\u0006\u0010\u0017\u001a\u00020\u0012@BX\u0086\u000e¢\u0006\b\n\u0000\u001a\u0004\b\u0019\u0010\u0014R\u001e\u0010\u001a\u001a\u00020\u00122\u0006\u0010\u0017\u001a\u00020\u0012@BX\u0086\u000e¢\u0006\b\n\u0000\u001a\u0004\b\u001b\u0010\u0014R\u001a\u0010\u001c\u001a\u00020\u001dX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u001e\u0010\u001f\"\u0004\b \u0010!¨\u0006+"},
   d2 = {"Lcom/nexomc/nexo/glyphs/AnimatedGlyph;", "Lcom/nexomc/nexo/glyphs/Glyph;", "section", "Lorg/bukkit/configuration/ConfigurationSection;", "<init>", "(Lorg/bukkit/configuration/ConfigurationSection;)V", "gifKey", "Lnet/kyori/adventure/key/Key;", "getGifKey", "()Lnet/kyori/adventure/key/Key;", "texture", "getTexture", "unicodes", "", "", "getUnicodes", "()Ljava/util/List;", "frameCount", "", "getFrameCount", "()I", "setFrameCount", "(I)V", "value", "imageWidth", "getImageWidth", "imageHeight", "getImageHeight", "aspectRatio", "", "getAspectRatio", "()F", "setAspectRatio", "(F)V", "generateSplitGif", "", "resourcePack", "Lteam/unnamed/creative/ResourcePack;", "calculateFramecount", "gifTexture", "Lteam/unnamed/creative/texture/Texture;", "onFrameCountReady", "Type", "com.nexomc:core"}
)
public abstract class AnimatedGlyph extends Glyph {
   @NotNull
   private final Key gifKey;
   @NotNull
   private final Key texture;
   @NotNull
   private final List<String> unicodes;
   private int frameCount;
   private int imageWidth;
   private int imageHeight;
   private float aspectRatio;

   public AnimatedGlyph(@NotNull ConfigurationSection section) {
      super(section);
      KeyUtils keyUtils = KeyUtils.INSTANCE;
      Key key = NexoYamlKt.key(section, "gif", this.getId());
      this.gifKey = keyUtils.appendSuffix(key, ".gif");
      this.texture = KeyUtils.INSTANCE.replace(this.gifKey, ".gif", ".png");
      this.unicodes = (List)(new ArrayList());
      this.frameCount = section.getInt("frame_count");
      this.aspectRatio = 1.0F;
   }

   @NotNull
   public final Key getGifKey() {
      return this.gifKey;
   }

   @NotNull
   public Key getTexture() {
      return this.texture;
   }

   @NotNull
   public List<String> getUnicodes() {
      return this.unicodes;
   }

   public final int getFrameCount() {
      return this.frameCount;
   }

   public final void setFrameCount(int value) {
      this.frameCount = value;
   }

   public final int getImageWidth() {
      return this.imageWidth;
   }

   public final int getImageHeight() {
      return this.imageHeight;
   }

   public final float getAspectRatio() {
      return this.aspectRatio;
   }

   public final void setAspectRatio(float value) {
      this.aspectRatio = value;
   }

   public final void generateSplitGif(@NotNull ResourcePack resourcePack) {
      AnimatedGlyph animatedGlyph = this;

      Object obj2;
      try {
         AnimatedGlyph animatedGlyph2 = animatedGlyph;
         int value = 0;
         Texture texture2 = resourcePack.texture(animatedGlyph2.gifKey);
         if (texture2 == null) {
            Logs.logWarn("No .gif found for " + animatedGlyph2.getId() + " at " + animatedGlyph2.gifKey.asString(), false);
            return;
         }

         Texture gifTexture = texture2;
         int frameCount = animatedGlyph2.calculateFramecount(gifTexture);
         (new GifConverter(animatedGlyph2, resourcePack)).splitGif(frameCount, gifTexture);
         obj2 = ResultShim.ok(Unit.INSTANCE);
      } catch (Throwable e) {
         obj2 = ResultShim.ok(ResultKt.createFailure(e));
      }

      Object obj = obj2;
      Throwable throwable = ResultShim.exceptionOrNull(obj2);
      if (throwable != null) {
         Throwable it = throwable;
         int value2 = 0;
         String id = this.getId();
         Logs.logError("Could not generate split gif for " + id + ".gif: " + it.getMessage(), false);
      }

      UtilsKt.printOnFailure(obj, true);
   }

   private final int calculateFramecount(Texture gifTexture) {
      AnimatedGlyph animatedGlyph = this;

      Object obj2;
      try {
         AnimatedGlyph animatedGlyph2 = animatedGlyph;
         int value = 0;
         ImageReader reader = (ImageReader)ImageIO.getImageReadersByFormatName("gif").next();
         byte[] toByteArray = gifTexture.data().toByteArray();
         byte[] p0 = toByteArray;
         Closeable p0_2 = (Closeable)(new ByteArrayInputStream(p0));
         Throwable throwable = null;

         ImageInputStream p0_1;
         try {
            int value2 = 0;
            p0_1 = ImageIO.createImageInputStream(p0_2);
         } catch (Throwable e) {
            throwable = e;
            throw e;
         } finally {
            CloseableKt.closeFinally(p0_2, throwable);
         }

         reader.setInput(p0_1);
         obj2 = ResultShim.ok(TuplesKt.to(kotlin.TuplesKt.to(reader.getNumImages(true), reader.getWidth(0)), reader.getHeight(0)));
      } catch (Throwable ex) {
         obj2 = ResultShim.ok(ResultKt.createFailure(ex));
      }

      if (ResultShim.exceptionOrNull(obj2) != null) {
         int value3 = 0;
         Logs.logError("Could not get frame count for " + this.getId() + ".gif", false);
      }

      Object obj = UtilsKt.printOnFailure(obj2, true);
      Triple triple = TuplesKt.to(kotlin.TuplesKt.to(0, 0), 0);
      Triple triple2 = (Triple)(ResultShim.isFailure(obj) ? triple : obj);
      int fc = ((Number)triple2.component1()).intValue();
      int w = ((Number)triple2.component2()).intValue();
      int h = ((Number)triple2.component3()).intValue();
      if (this.frameCount <= 0) {
         this.frameCount = fc;
      }

      if (this.imageWidth <= 0) {
         this.imageWidth = w;
      }

      if (this.imageHeight <= 0) {
         this.imageHeight = h;
      }

      if (!(Math.abs(this.aspectRatio) <= Float.MAX_VALUE) || this.aspectRatio <= 0.0F) {
         this.aspectRatio = (float)w / (float)h;
      }

      this.onFrameCountReady(this.frameCount);
      return this.frameCount;
   }

   protected void onFrameCountReady(int frameCount) {
   }

   @Metadata(
      mv = {2, 4, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0002\b\u0005\b\u0086\u0081\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003j\u0002\b\u0004j\u0002\b\u0005¨\u0006\u0006"},
      d2 = {"Lcom/nexomc/nexo/glyphs/AnimatedGlyph$Type;", "", "<init>", "(Ljava/lang/String;I)V", "SHADER", "SPRITE", "com.nexomc:core"}
   )
   public static enum Type {
      SHADER,
      SPRITE;

      @NotNull
      public static EnumEntries<Type> getEntries() {
         return EnumEntriesKt.enumEntries(values());
      }
   }
}
