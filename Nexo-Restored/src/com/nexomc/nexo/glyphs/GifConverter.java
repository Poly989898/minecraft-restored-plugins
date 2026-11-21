package com.nexomc.nexo.glyphs;

import com.nexomc.nexo.decomp.ResultShim;
import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.awt.image.RenderedImage;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import javax.imageio.ImageIO;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.Result;
import kotlin.ResultKt;
import kotlin.TuplesKt;
import kotlin.collections.CollectionsKt;
import kotlin.collections.IntIterator;
import kotlin.io.CloseableKt;
import kotlin.ranges.RangesKt;
import org.jetbrains.annotations.NotNull;
import team.unnamed.creative.ResourcePack;
import team.unnamed.creative.base.Writable;
import team.unnamed.creative.metadata.animation.AnimationFrame;
import team.unnamed.creative.metadata.animation.AnimationMeta;
import team.unnamed.creative.texture.Texture;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000>\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\r\n\u0002\u0018\u0002\n\u0000\u0018\u00002\u00020\u0001B\u0017\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005¢\u0006\u0004\b\u0006\u0010\u0007J\u0016\u0010\f\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\u0011J&\u0010\u0012\u001a\u00020\u00112\f\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\u00150\u00142\u000e\b\u0002\u0010\u0016\u001a\b\u0012\u0004\u0012\u00020\u000f0\u0014H\u0002J(\u0010\u0017\u001a\u00020\u00152\u0006\u0010\u0018\u001a\u00020\u00152\u0006\u0010\u0019\u001a\u00020\u000f2\u0006\u0010\u001a\u001a\u00020\u000f2\u0006\u0010\u001b\u001a\u00020\u000fH\u0002J(\u0010\u001c\u001a\u00020\u000f2\u0006\u0010\u001d\u001a\u00020\u000f2\u0006\u0010\u001e\u001a\u00020\u000f2\u0006\u0010\u001f\u001a\u00020\u000f2\u0006\u0010 \u001a\u00020\u000fH\u0002J\u0010\u0010!\u001a\u00020\u00152\u0006\u0010\"\u001a\u00020#H\u0002R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000b¨\u0006$"},
   d2 = {"Lcom/nexomc/nexo/glyphs/GifConverter;", "", "animatedGlyph", "Lcom/nexomc/nexo/glyphs/AnimatedGlyph;", "resourcePack", "Lteam/unnamed/creative/ResourcePack;", "<init>", "(Lcom/nexomc/nexo/glyphs/AnimatedGlyph;Lteam/unnamed/creative/ResourcePack;)V", "getAnimatedGlyph", "()Lcom/nexomc/nexo/glyphs/AnimatedGlyph;", "getResourcePack", "()Lteam/unnamed/creative/ResourcePack;", "splitGif", "", "frameCount", "", "gif", "Lteam/unnamed/creative/texture/Texture;", "createSpritesheet", "frames", "", "Ljava/awt/image/BufferedImage;", "frameDelays", "generateFrame", "image", "start", "stop", "total", "compact", "b1", "b2", "b3", "b4", "toBufferedImage", "img", "Ljava/awt/Image;", "com.nexomc:core"}
)
public final class GifConverter {
   @NotNull
   private final AnimatedGlyph animatedGlyph;
   @NotNull
   private final ResourcePack resourcePack;

   public GifConverter(@NotNull AnimatedGlyph animatedGlyph, @NotNull ResourcePack resourcePack) {
      super();
      this.animatedGlyph = animatedGlyph;
      this.resourcePack = resourcePack;
   }

   @NotNull
   public final AnimatedGlyph getAnimatedGlyph() {
      return this.animatedGlyph;
   }

   @NotNull
   public final ResourcePack getResourcePack() {
      return this.resourcePack;
   }

   public final void splitGif(int frameCount, @NotNull Texture gif) {
      GifDecoder decoder = new GifDecoder();
      Writable data = gif.data();
      decoder.read(data);
      decoder.setFrameCount(frameCount);
      int time = 0;
      Iterable iterable = (Iterable)RangesKt.until(0, decoder.getFrameCount());
      int value = 0;

      int value2;
      for(Iterator iterator = iterable.iterator(); iterator.hasNext(); value += value2) {
         int nextInt = ((IntIterator)iterator).nextInt();
         int value3 = 0;
         value2 = decoder.getDelay(nextInt);
      }

      int totalTime = value;
      List frameDelays = (List)(new ArrayList());
      int value4 = decoder.getFrameCount();
      ArrayList arrayList = new ArrayList(value4);

      for(int p0 = 0; p0 < value4; ++p0) {
         BufferedImage bufferedImage;
         block: {
            int value5 = 0;
            bufferedImage = decoder.getFrame(p0);
            if (bufferedImage != null) {
               Image p0_6 = (Image)bufferedImage;
               int value6 = 0;
               bufferedImage = this.toBufferedImage(p0_6);
               if (bufferedImage != null) {
                  BufferedImage image = bufferedImage;
                  if (this.animatedGlyph instanceof SpriteAnimatedGlyph) {
                     frameDelays.add(decoder.getDelay(p0));
                     bufferedImage = image;
                  } else {
                     int start = time;
                     time += decoder.getDelay(p0);
                     bufferedImage = this.generateFrame(image, start, time, totalTime);
                  }
                  break block;
               }
            }

            bufferedImage = null;
         }

         arrayList.add(bufferedImage);
      }

      List frames = CollectionsKt.filterNotNull((Iterable)((List)arrayList));
      this.resourcePack.texture(this.createSpritesheet(frames, frameDelays));
      this.resourcePack.removeTexture(this.animatedGlyph.getGifKey());
   }

   private final Texture createSpritesheet(List<? extends BufferedImage> frames, List<Integer> frameDelays) {
      BufferedImage it = (BufferedImage)CollectionsKt.first(frames);
      int value = 0;
      Pair pair = TuplesKt.to(it.getWidth(), it.getHeight());
      int width = ((Number)pair.component1()).intValue();
      int height = ((Number)pair.component2()).intValue();
      BufferedImage spritesheet = new BufferedImage(width, frames.size() * height, 2);
      Graphics2D g = spritesheet.createGraphics();
      g.setComposite((Composite)AlphaComposite.Src);
      Iterable iterable = (Iterable)frames;
      int index = 0;

      for(Object item : iterable) {
         int i = index++;
         if (i < 0) {
            CollectionsKt.throwIndexOverflow();
         }

         BufferedImage frame = (BufferedImage)item;
         int value2 = 0;
         g.drawImage((Image)frame, 0, i * height, width, height, (ImageObserver)null);
      }

      g.dispose();
      GifConverter metadata = this;

      Object animFrames;
      try {
         GifConverter gifConverter = metadata;
         int value3 = 0;
         Closeable i = (Closeable)(new ByteArrayOutputStream());
         Throwable throwable2 = null;

         byte[] out;
         try {
            ByteArrayOutputStream out_5 = (ByteArrayOutputStream)i;
            int value4 = 0;
            ImageIO.write((RenderedImage)spritesheet, "png", (OutputStream)out_5);
            out = out_5.toByteArray();
         } catch (Throwable e) {
            throwable2 = e;
            throw e;
         } finally {
            CloseableKt.closeFinally(i, throwable2);
         }

         animFrames = ResultShim.ok(out);
      } catch (Throwable ex) {
         animFrames = ResultShim.ok(ResultKt.createFailure(ex));
      }

      Throwable throwable = ResultShim.exceptionOrNull(animFrames);
      if (throwable != null) {
         Throwable it_4 = throwable;
         int value5 = 0;
         it_4.printStackTrace();
      }

      byte[] it_3 = new byte[0];
      Writable writable = Writable.bytes((byte[])(ResultShim.isFailure(animFrames) ? it_3 : animFrames));
      Writable data = writable;
      team.unnamed.creative.metadata.Metadata metadata2;
      if (this.animatedGlyph instanceof SpriteAnimatedGlyph) {
         ((SpriteAnimatedGlyph)this.animatedGlyph).handleSpriteAtlas(this.resourcePack);
         Iterable iterable2 = (Iterable)frameDelays;
         Collection destination = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault(iterable2, 10)));
         int value8 = 0;

         for(Object obj : iterable2) {
            int value6 = value8++;
            if (value6 < 0) {
               CollectionsKt.throwIndexOverflow();
            }

            int delayMs = ((Number)obj).intValue();
            int value7 = 0;
            destination.add(AnimationFrame.frame(value6, Math.max(1, delayMs / 20)));
         }

         List animFrames_2 = (List)destination;
         AnimationMeta animationMeta = AnimationMeta.animation().width(width).height(height).frames(animFrames_2).interpolate(((SpriteAnimatedGlyph)this.animatedGlyph).getInterpolate()).build();
         AnimationMeta animation = animationMeta;
         metadata2 = team.unnamed.creative.metadata.Metadata.metadata().addPart(animation).build();
      } else {
         metadata2 = team.unnamed.creative.metadata.Metadata.empty();
      }

      team.unnamed.creative.metadata.Metadata metadata_1 = metadata2;
      Texture texture = Texture.texture(this.animatedGlyph.getTexture(), data, metadata_1);
      return texture;
   }

   private final BufferedImage generateFrame(BufferedImage image, int start, int stop, int total) {
      Pair pair = TuplesKt.to(Math.min(image.getWidth() + 2, 256), Math.min(image.getHeight() + 2, 256));
      int iWidth = ((Number)pair.component1()).intValue();
      int iHeight = ((Number)pair.component2()).intValue();
      BufferedImage frame = new BufferedImage(iWidth, iHeight, 2);
      frame.setRGB(1, 1, iWidth - 2, iHeight - 2, image.getRGB(0, 0, iWidth - 2, iHeight - 2, (int[])null, 0, iWidth - 2), 0, iWidth - 2);
      int width = frame.getWidth() - 1;
      int height = frame.getHeight() - 1;
      int[] info = new int[]{total >> 8 & 255, total & 255, start >> 8 & 255, start & 255, stop >> 8 & 255, stop & 255, 0, 0};
      frame.setRGB(width, 0, this.compact(0, 0, 0, 149));
      frame.setRGB(0, 0, this.compact(1, 0, 0, 149));
      frame.setRGB(0, height, this.compact(2, 0, 0, 149));
      frame.setRGB(width, height, this.compact(3, 0, 0, 149));
      frame.setRGB(width - 1, 0, this.compact(info[0], info[1], info[2], info[3]));
      frame.setRGB(width, 1, this.compact(info[4], info[5], info[6], info[7]));
      frame.setRGB(1, 0, this.compact(info[0], info[1], info[2], info[3]));
      frame.setRGB(0, 1, this.compact(info[4], info[5], info[6], info[7]));
      frame.setRGB(1, height, this.compact(info[0], info[1], info[2], info[3]));
      frame.setRGB(0, height - 1, this.compact(info[4], info[5], info[6], info[7]));
      frame.setRGB(width - 1, height, this.compact(info[0], info[1], info[2], info[3]));
      frame.setRGB(width, height - 1, this.compact(info[4], info[5], info[6], info[7]));
      return frame;
   }

   private final int compact(int b1, int b2, int b3, int b4) {
      return (b4 & 255) << 24 | (b1 & 255) << 16 | (b2 & 255) << 8 | b3 & 255;
   }

   private final BufferedImage toBufferedImage(Image img) {
      if (img instanceof BufferedImage && ((BufferedImage)img).getType() == 2) {
         return (BufferedImage)img;
      } else {
         BufferedImage bimage = new BufferedImage(img.getWidth((ImageObserver)null), img.getHeight((ImageObserver)null), 2);
         Graphics2D bGr = bimage.createGraphics();
         bGr.drawImage(img, 0, 0, (ImageObserver)null);
         bGr.dispose();
         return bimage;
      }
   }
}
