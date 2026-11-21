package com.nexomc.nexo.glyphs;

import com.nexomc.nexo.decomp.ResultShim;
import com.nexomc.nexo.utils.UtilsKt;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferInt;
import java.awt.image.WritableRaster;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import kotlin.Metadata;
import kotlin.Result;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.io.CloseableKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.StringsKt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.unnamed.creative.base.Writable;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000j\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\u0015\n\u0002\b\u000e\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0012\n\u0002\b\u0007\n\u0002\u0010\u0017\n\u0002\b\u0004\n\u0002\u0010!\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0012\u0018\u0000 N2\u00020\u0001:\u0002NOB\u0007¢\u0006\u0004\b\u0002\u0010\u0003J\u000e\u00103\u001a\u00020\u00072\u0006\u00104\u001a\u00020\u0007J\u0006\u00105\u001a\u00020\u0007J\u000e\u00106\u001a\u0002072\u0006\u00102\u001a\u00020\u0007J\b\u00108\u001a\u000207H\u0002J\u0010\u00109\u001a\u0004\u0018\u00010 2\u0006\u00104\u001a\u00020\u0007J\u000e\u0010:\u001a\u00020\u00072\u0006\u0010;\u001a\u00020<J\u0010\u0010:\u001a\u00020\u00072\b\u0010=\u001a\u0004\u0018\u00010>J\b\u0010?\u001a\u000207H\u0002J\b\u0010@\u001a\u00020\u000bH\u0002J\b\u0010A\u001a\u000207H\u0002J\b\u0010:\u001a\u00020\u0007H\u0002J\b\u0010B\u001a\u00020\u0007H\u0002J\u0012\u0010C\u001a\u0004\u0018\u00010\u000f2\u0006\u0010D\u001a\u00020\u0007H\u0002J\b\u0010E\u001a\u000207H\u0002J\b\u0010F\u001a\u000207H\u0002J\b\u0010G\u001a\u000207H\u0002J\b\u0010H\u001a\u000207H\u0002J\b\u0010I\u001a\u000207H\u0002J\b\u0010J\u001a\u000207H\u0002J\b\u0010K\u001a\u00020\u0007H\u0002J\b\u0010L\u001a\u000207H\u0002J\b\u0010M\u001a\u000207H\u0002R\u0010\u0010\u0004\u001a\u0004\u0018\u00010\u0005X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\u0007X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\u0007X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u000bX\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\u0007X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\r\u001a\u00020\u0007X\u0082\u000e¢\u0006\u0002\n\u0000R\u0010\u0010\u000e\u001a\u0004\u0018\u00010\u000fX\u0082\u000e¢\u0006\u0002\n\u0000R\u0010\u0010\u0010\u001a\u0004\u0018\u00010\u000fX\u0082\u000e¢\u0006\u0002\n\u0000R\u0010\u0010\u0011\u001a\u0004\u0018\u00010\u000fX\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u0012\u001a\u00020\u0007X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u0013\u001a\u00020\u0007X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u0014\u001a\u00020\u0007X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u0015\u001a\u00020\u0007X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u0016\u001a\u00020\u000bX\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u0017\u001a\u00020\u000bX\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u0018\u001a\u00020\u0007X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u0019\u001a\u00020\u0007X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u001a\u001a\u00020\u0007X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u001b\u001a\u00020\u0007X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u001c\u001a\u00020\u0007X\u0082\u000e¢\u0006\u0002\n\u0000R\u0010\u0010\u001d\u001a\u0004\u0018\u00010\u001eX\u0082\u000e¢\u0006\u0002\n\u0000R\u0010\u0010\u001f\u001a\u0004\u0018\u00010 X\u0082\u000e¢\u0006\u0002\n\u0000R\u0010\u0010!\u001a\u0004\u0018\u00010 X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\"\u001a\u00020#X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010$\u001a\u00020\u0007X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010%\u001a\u00020\u0007X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010&\u001a\u00020\u0007X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010'\u001a\u00020\u000bX\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010(\u001a\u00020\u0007X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010)\u001a\u00020\u0007X\u0082\u000e¢\u0006\u0002\n\u0000R\u0010\u0010*\u001a\u0004\u0018\u00010+X\u0082\u000e¢\u0006\u0002\n\u0000R\u0010\u0010,\u001a\u0004\u0018\u00010#X\u0082\u000e¢\u0006\u0002\n\u0000R\u0010\u0010-\u001a\u0004\u0018\u00010#X\u0082\u000e¢\u0006\u0002\n\u0000R\u0010\u0010.\u001a\u0004\u0018\u00010#X\u0082\u000e¢\u0006\u0002\n\u0000R\u0014\u0010/\u001a\b\u0012\u0004\u0012\u00020100X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u00102\u001a\u00020\u0007X\u0082\u000e¢\u0006\u0002\n\u0000¨\u0006P"},
   d2 = {"Lcom/nexomc/nexo/glyphs/GifDecoder;", "", "<init>", "()V", "input", "Ljava/io/BufferedInputStream;", "status", "", "width", "height", "gctFlag", "", "gctSize", "loopCount", "gct", "", "lct", "act", "bgIndex", "bgColor", "lastBgColor", "pixelAspect", "lctFlag", "interlace", "lctSize", "ix", "iy", "iw", "ih", "lastRect", "Ljava/awt/Rectangle;", "image", "Ljava/awt/image/BufferedImage;", "lastImage", "block", "", "blockSize", "dispose", "lastDispose", "transparency", "delay", "transIndex", "prefix", "", "suffix", "pixelStack", "pixels", "frames", "", "Lcom/nexomc/nexo/glyphs/GifDecoder$GifFrame;", "frameCount", "getDelay", "n", "getFrameCount", "setFrameCount", "", "setPixels", "getFrame", "read", "writable", "Lteam/unnamed/creative/base/Writable;", "inputStream", "Ljava/io/InputStream;", "decodeImageData", "err", "init", "readBlock", "readColorTable", "ncolors", "readContents", "readGraphicControlExt", "readHeader", "readImage", "readLSD", "readNetscapeExt", "readShort", "resetFrame", "skip", "Companion", "GifFrame", "com.nexomc:core"}
)
public final class GifDecoder {
   @NotNull
   public static final Companion Companion = new Companion();
   @Nullable
   private BufferedInputStream input;
   private int status;
   private int width;
   private int height;
   private boolean gctFlag;
   private int gctSize;
   private int loopCount = 1;
   @Nullable
   private int[] gct;
   @Nullable
   private int[] lct;
   @Nullable
   private int[] act;
   private int bgIndex;
   private int bgColor;
   private int lastBgColor;
   private int pixelAspect;
   private boolean lctFlag;
   private boolean interlace;
   private int lctSize;
   private int ix;
   private int iy;
   private int iw;
   private int ih;
   @Nullable
   private Rectangle lastRect;
   @Nullable
   private BufferedImage image;
   @Nullable
   private BufferedImage lastImage;
   @NotNull
   private byte[] block = new byte[256];
   private int blockSize;
   private int dispose;
   private int lastDispose;
   private boolean transparency;
   private int delay;
   private int transIndex;
   @Nullable
   private short[] prefix;
   @Nullable
   private byte[] suffix;
   @Nullable
   private byte[] pixelStack;
   @Nullable
   private byte[] pixels;
   @NotNull
   private List<GifFrame> frames = (List)(new ArrayList());
   private int frameCount;
   public static final int STATUS_FORMAT_ERROR = 1;
   public static final int STATUS_OPEN_ERROR = 2;
   private static final int MAX_STACK_SIZE = 4096;

   public final int getDelay(int n) {
      this.delay = -1;
      if (0 <= n ? n < this.frameCount : false) {
         this.delay = ((GifFrame)this.frames.get(n)).getDelay();
      }

      return this.delay;
   }

   public final int getFrameCount() {
      return this.frameCount;
   }

   public final void setFrameCount(int frameCount) {
      this.frameCount = frameCount;
   }

   private final void setPixels() {
      DataBuffer dataBuffer;
      block: {
         BufferedImage bufferedImage = this.image;
         if (bufferedImage != null) {
            WritableRaster raster = bufferedImage.getRaster();
            if (raster != null) {
               dataBuffer = raster.getDataBuffer();
               break block;
            }
         }

         dataBuffer = null;
      }

      int[] dest = ((DataBufferInt)dataBuffer).getData();
      if (this.lastDispose > 0) {
         if (this.lastDispose == 3) {
            int n = this.frameCount - 2;
            this.lastImage = n > 0 ? this.getFrame(n - 1) : null;
         }

         BufferedImage bufferedImage2 = this.lastImage;
         if (bufferedImage2 != null) {
            BufferedImage last = bufferedImage2;
            int value = 0;
            DataBuffer dataBuffer2 = last.getRaster().getDataBuffer();
            int[] prev = ((DataBufferInt)dataBuffer2).getData();
            System.arraycopy(prev, 0, dest, 0, this.width * this.height);
            if (this.lastDispose == 2) {
               BufferedImage bufferedImage3 = this.image;
               Graphics2D g = bufferedImage3.createGraphics();
               Color c = this.transparency ? new Color(0, 0, 0, 0) : new Color(this.lastBgColor);
               g.setColor(c);
               g.setComposite((Composite)AlphaComposite.Src);
               g.fill((Shape)this.lastRect);
               g.dispose();
            }
         }
      }

      int pass = 1;
      int inc = 8;
      int iline = 0;
      int i = 0;

      for(int j = this.ih; i < j; ++i) {
         int line = i;
         if (this.interlace) {
            if (iline >= this.ih) {
               ++pass;
               switch (pass) {
                  case 2:
                     iline = 4;
                     break;
                  case 3:
                     iline = 2;
                     inc = 4;
                     break;
                  case 4:
                     iline = 1;
                     inc = 2;
               }
            }

            line = iline;
            iline += inc;
         }

         line += this.iy;
         if (line < this.height) {
            int k = line * this.width;
            int dx = k + this.ix;
            int dlim = dx + this.iw;
            if (k + this.width < dlim) {
               dlim = k + this.width;
            }

            for(int sx = i * this.iw; dx < dlim; ++dx) {
               byte[] bs = this.pixels;
               int index = bs[sx++] & 255;
               int[] ints = this.act;
               int c = ints[index];
               if (c != 0) {
                  dest[dx] = c;
               }
            }
         }
      }

   }

   @Nullable
   public final BufferedImage getFrame(int n) {
      return (0 <= n ? n < this.frameCount : false) ? ((GifFrame)this.frames.get(n)).getImage() : null;
   }

   public final int read(@NotNull Writable writable) {
      GifDecoder gifDecoder = this;

      Object obj2;
      try {
         GifDecoder gifDecoder2 = gifDecoder;
         int value = 0;
         byte[] toByteArray = writable.toByteArray();
         byte[] bs = toByteArray;
         Closeable closeable = (Closeable)(new ByteArrayInputStream(bs));
         Throwable throwable = null;

         int value2;
         try {
            InputStream p0 = (InputStream)closeable;
            int value3 = 0;
            value2 = gifDecoder2.read(p0);
         } catch (Throwable e) {
            throwable = e;
            throw e;
         } finally {
            CloseableKt.closeFinally(closeable, throwable);
         }

         obj2 = ResultShim.ok(value2);
      } catch (Throwable ex) {
         obj2 = ResultShim.ok(ResultKt.createFailure(ex));
      }

      Object obj = UtilsKt.printOnFailure(obj2, false);
      Integer number = 2;
      return ((Number)(ResultShim.isFailure(obj) ? number : obj)).intValue();
   }

   public final int read(@Nullable InputStream inputStream) {
      this.init();
      if (inputStream != null) {
         BufferedInputStream bufferedInputStream = inputStream instanceof BufferedInputStream ? (BufferedInputStream)inputStream : null;
         if ((inputStream instanceof BufferedInputStream ? (BufferedInputStream)inputStream : null) == null) {
            bufferedInputStream = new BufferedInputStream(inputStream);
         }

         BufferedInputStream bufferedInput = bufferedInputStream;
         this.input = bufferedInput;
         this.readHeader();
         if (!this.err()) {
            this.readContents();
            if (this.frameCount < 0) {
               this.status = 1;
            }
         }
      } else {
         this.status = 2;
      }

      GifDecoder gifDecoder = this;

      try {
         GifDecoder gifDecoder2 = gifDecoder;
         int value = 0;
         Unit unit;
         if (inputStream != null) {
            inputStream.close();
            unit = Unit.INSTANCE;
         } else {
            unit = null;
         }

         Object obj = ResultShim.ok(unit);
      } catch (Throwable e) {
         Object obj2 = ResultShim.ok(ResultKt.createFailure(e));
      }

      return this.status;
   }

   private final void decodeImageData() {
      int nullCode;
      int npix;
      block: {
         nullCode = -1;
         npix = this.iw * this.ih;
         if (this.pixels != null) {
            byte[] bs = this.pixels;
            if (bs.length >= npix) {
               break block;
            }
         }

         this.pixels = new byte[npix];
      }

      if (this.prefix == null) {
         this.prefix = new short[4096];
      }

      if (this.suffix == null) {
         this.suffix = new byte[4096];
      }

      if (this.pixelStack == null) {
         this.pixelStack = new byte[4097];
      }

      int dataSize = this.read();
      int clear = 1 << dataSize;
      int endOfInformation = clear + 1;
      int available = clear + 2;
      int oldCode = nullCode;
      int codeSize = dataSize + 1;
      int codeMask = (1 << codeSize) - 1;

      for(int code = 0; code < clear; ++code) {
         short[] shorts = this.prefix;
         shorts[code] = 0;
         byte[] bs2 = this.suffix;
         bs2[code] = (byte)code;
      }

      int bi = 0;
      int pi = 0;
      int top = 0;
      int first = 0;
      int count = 0;
      int bits = 0;
      int datum = 0;
      int i = 0;

      while(i < npix) {
         if (top == 0) {
            if (bits < codeSize) {
               if (count == 0) {
                  count = this.readBlock();
                  if (count <= 0) {
                     break;
                  }

                  bi = 0;
               }

               datum += (this.block[bi] & 255) << bits;
               bits += 8;
               ++bi;
               --count;
               continue;
            }

            int code = datum & codeMask;
            datum >>= codeSize;
            bits -= codeSize;
            if (code > available || code == endOfInformation) {
               break;
            }

            if (code == clear) {
               codeSize = dataSize + 1;
               codeMask = (1 << codeSize) - 1;
               available = clear + 2;
               oldCode = nullCode;
               continue;
            }

            if (oldCode == nullCode) {
               byte[] bs3 = this.pixelStack;
               int value = top++;
               byte[] bs4 = this.suffix;
               bs3[value] = bs4[code];
               oldCode = code;
               first = code;
               continue;
            }

            int inCode = code;
            if (code == available) {
               byte[] bs5 = this.pixelStack;
               bs5[top++] = (byte)first;
               code = oldCode;
            }

            while(code > clear) {
               byte[] bs6 = this.pixelStack;
               int value2 = top++;
               byte[] bs7 = this.suffix;
               bs6[value2] = bs7[code];
               short[] shorts2 = this.prefix;
               code = shorts2[code];
            }

            byte[] bs8 = this.suffix;
            first = bs8[code] & 255;
            if (available >= 4096) {
               bs8 = this.pixelStack;
               bs8[top++] = (byte)first;
               continue;
            }

            bs8 = this.pixelStack;
            bs8[top++] = (byte)first;
            short[] shorts3 = this.prefix;
            shorts3[available] = (short)oldCode;
            byte[] bs9 = this.suffix;
            bs9[available] = (byte)first;
            ++available;
            if ((available & codeMask) == 0 && available < 4096) {
               ++codeSize;
               codeMask += available;
            }

            oldCode = inCode;
         }

         --top;
         byte[] bs10 = this.pixels;
         int value3 = pi++;
         byte[] bs11 = this.pixelStack;
         bs10[value3] = bs11[top];
         ++i;
      }

      for(int j = pi; j < npix; ++j) {
         byte[] bs12 = this.pixels;
         bs12[j] = 0;
      }

   }

   private final boolean err() {
      return this.status != 0;
   }

   private final void init() {
      this.status = 0;
      this.frameCount = 0;
      this.frames = (List)(new ArrayList());
      this.gct = null;
      this.lct = null;
   }

   private final int read() {
      GifDecoder gifDecoder = this;

      Object obj2;
      try {
         GifDecoder gifDecoder2 = gifDecoder;
         int value = 0;
         BufferedInputStream bufferedInputStream = gifDecoder2.input;
         obj2 = ResultShim.ok(bufferedInputStream != null ? bufferedInputStream.read() : 0);
      } catch (Throwable e) {
         obj2 = ResultShim.ok(ResultKt.createFailure(e));
      }

      Object obj = obj2;
      if (ResultShim.exceptionOrNull(obj2) != null) {
         int value2 = 0;
         this.status = 1;
      }

      Integer number = 0;
      return ((Number)(ResultShim.isFailure(obj) ? number : obj)).intValue();
   }

   private final int readBlock() {
      this.blockSize = this.read();
      int n = 0;
      if (this.blockSize > 0) {
         GifDecoder gifDecoder = this;

         try {
            GifDecoder gifDecoder2 = gifDecoder;
            int value = 0;

            int count;
            for(count = 0; n < gifDecoder2.blockSize; n += count) {
               BufferedInputStream bufferedInputStream = gifDecoder2.input;
               count = bufferedInputStream != null ? bufferedInputStream.read(gifDecoder2.block, n, gifDecoder2.blockSize - n) : -1;
               if (count == -1) {
                  break;
               }
            }

            Object obj = ResultShim.ok(Unit.INSTANCE);
         } catch (Throwable e) {
            Object obj2 = ResultShim.ok(ResultKt.createFailure(e));
         }

         if (n < this.blockSize) {
            this.status = 1;
         }
      }

      return n;
   }

   private final int[] readColorTable(int ncolors) {
      int nbytes = 3 * ncolors;
      int[] tab = null;
      byte[] c = new byte[nbytes];
      GifDecoder gifDecoder = this;

      Object obj;
      try {
         GifDecoder gifDecoder2 = gifDecoder;
         int value = 0;
         BufferedInputStream bufferedInputStream = gifDecoder2.input;
         obj = ResultShim.ok(bufferedInputStream != null ? bufferedInputStream.read(c) : 0);
      } catch (Throwable e) {
         obj = ResultShim.ok(ResultKt.createFailure(e));
      }

      Integer number = 0;
      int n = ((Number)(ResultShim.isFailure(obj) ? number : obj)).intValue();
      if (n < nbytes) {
         this.status = 1;
      } else {
         tab = new int[256];
         int i = 0;

         int g;
         int b;
         int r;
         for(int j = 0; i < ncolors; tab[i++] = -16777216 | r << 16 | g << 8 | b) {
            r = c[j++] & 255;
            g = c[j++] & 255;
            b = c[j++] & 255;
         }
      }

      return tab;
   }

   private final void readContents() {
      boolean done = false;

      while(!done && !this.err()) {
         int code = this.read();
         switch (code) {
            case 0:
               break;
            case 33:
               switch (this.read()) {
                  case 249:
                     this.readGraphicControlExt();
                     continue;
                  case 255:
                     this.readBlock();
                     StringBuilder app = new StringBuilder();
                     byte b = 11;

                     for(int it = 0; it < b; ++it) {
                        int value = 0;
                        app.append((char)this.block[it]);
                     }

                     if (Intrinsics.areEqual(app.toString(), "NETSCAPE2.0")) {
                        this.readNetscapeExt();
                     } else {
                        this.skip();
                     }
                     continue;
                  default:
                     this.skip();
                     continue;
               }
            case 44:
               this.readImage();
               break;
            case 59:
               done = true;
               break;
            default:
               this.status = 1;
         }
      }

   }

   private final void readGraphicControlExt() {
      this.read();
      int packed = this.read();
      this.dispose = (packed & 28) >> 2;
      if (this.dispose == 0) {
         this.dispose = 1;
      }

      this.transparency = (packed & 1) != 0;
      this.delay = this.readShort() * 10;
      this.transIndex = this.read();
      this.read();
   }

   private final void readHeader() {
      StringBuilder id = new StringBuilder();

      for(int i = 0; i < 6; ++i) {
         id.append((char)this.read());
      }

      String str = id.toString();
      if (!StringsKt.startsWith(str, "GIF", false)) {
         this.status = 1;
      } else {
         this.readLSD();
         if (this.gctFlag && !this.err()) {
            this.gct = this.readColorTable(this.gctSize);
            int[] ints = this.gct;
            if (ints != null) {
               int[] it = ints;
               int value = 0;
               this.bgColor = it[this.bgIndex];
            }
         }
      }

   }

   private final void readImage() {
      this.ix = this.readShort();
      this.iy = this.readShort();
      this.iw = this.readShort();
      this.ih = this.readShort();
      int packed = this.read();
      this.lctFlag = (packed & 128) != 0;
      this.interlace = (packed & 64) != 0;
      this.lctSize = 2 << (packed & 7);
      if (this.lctFlag) {
         this.lct = this.readColorTable(this.lctSize);
         this.act = this.lct;
      } else {
         this.act = this.gct;
         if (this.bgIndex == this.transIndex) {
            this.bgColor = 0;
         }
      }

      int save = 0;
      if (this.transparency) {
         int[] ints = this.act;
         if (ints != null) {
            int[] it = ints;
            int value = 0;
            save = it[this.transIndex];
            it[this.transIndex] = 0;
         }
      }

      if (this.act == null) {
         this.status = 1;
      }

      if (!this.err()) {
         this.decodeImageData();
         this.skip();
         if (!this.err()) {
            int value2 = this.frameCount++;
            this.image = new BufferedImage(this.width, this.height, 2);
            this.setPixels();
            List list = this.frames;
            BufferedImage bufferedImage = this.image;
            list.add(new GifFrame(bufferedImage, this.delay));
            if (this.transparency) {
               int[] ints2 = this.act;
               if (ints2 != null) {
                  int[] it = ints2;
                  int value3 = 0;
                  it[this.transIndex] = save;
               }
            }

            this.resetFrame();
         }
      }

   }

   private final void readLSD() {
      this.width = this.readShort();
      this.height = this.readShort();
      int packed = this.read();
      this.gctFlag = (packed & 128) != 0;
      this.gctSize = 2 << (packed & 7);
      this.bgIndex = this.read();
      this.pixelAspect = this.read();
   }

   private final void readNetscapeExt() {
      do {
         this.readBlock();
         if (this.block[0] == 1) {
            int b1 = this.block[1] & 255;
            int b2 = this.block[2] & 255;
            this.loopCount = b2 << 8 | b1;
         }
      } while(this.blockSize > 0 && !this.err());

   }

   private final int readShort() {
      return this.read() | this.read() << 8;
   }

   private final void resetFrame() {
      this.lastDispose = this.dispose;
      this.lastRect = new Rectangle(this.ix, this.iy, this.iw, this.ih);
      this.lastImage = this.image;
      this.lastBgColor = this.bgColor;
      this.dispose = 0;
      this.transparency = false;
      this.delay = 0;
      this.lct = null;
   }

   private final void skip() {
      do {
         this.readBlock();
      } while(this.blockSize > 0 && !this.err());

   }

   @Metadata(
      mv = {2, 4, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\u0003\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003R\u000e\u0010\u0004\u001a\u00020\u0005X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0005X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0005X\u0082T¢\u0006\u0002\n\u0000¨\u0006\b"},
      d2 = {"Lcom/nexomc/nexo/glyphs/GifDecoder$Companion;", "", "<init>", "()V", "STATUS_FORMAT_ERROR", "", "STATUS_OPEN_ERROR", "MAX_STACK_SIZE", "com.nexomc:core"}
   )
   public static final class Companion {
      private Companion() {
      }
   }

   @Metadata(
      mv = {2, 4, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\n\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\b\u0086\b\u0018\u00002\u00020\u0001B\u0017\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005¢\u0006\u0004\b\u0006\u0010\u0007J\t\u0010\f\u001a\u00020\u0003HÆ\u0003J\t\u0010\r\u001a\u00020\u0005HÆ\u0003J\u001d\u0010\u000e\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u0005HÆ\u0001J\u0014\u0010\u000f\u001a\u00020\u00102\b\u0010\u0011\u001a\u0004\u0018\u00010\u0001HÖ\u0083\u0004J\n\u0010\u0012\u001a\u00020\u0005HÖ\u0081\u0004J\n\u0010\u0013\u001a\u00020\u0014HÖ\u0081\u0004R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000b¨\u0006\u0015"},
      d2 = {"Lcom/nexomc/nexo/glyphs/GifDecoder$GifFrame;", "", "image", "Ljava/awt/image/BufferedImage;", "delay", "", "<init>", "(Ljava/awt/image/BufferedImage;I)V", "getImage", "()Ljava/awt/image/BufferedImage;", "getDelay", "()I", "component1", "component2", "copy", "equals", "", "other", "hashCode", "toString", "", "com.nexomc:core"}
   )
   public static final class GifFrame {
      @NotNull
      private final BufferedImage image;
      private final int delay;

      public GifFrame(@NotNull BufferedImage image, int delay) {
         super();
         this.image = image;
         this.delay = delay;
      }

      @NotNull
      public final BufferedImage getImage() {
         return this.image;
      }

      public final int getDelay() {
         return this.delay;
      }

      @NotNull
      public final BufferedImage component1() {
         return this.image;
      }

      public final int component2() {
         return this.delay;
      }

      @NotNull
      public final GifFrame copy(@NotNull BufferedImage image, int delay) {
         return new GifFrame(image, delay);
      }

      @NotNull
      public String toString() {
         return "GifFrame(image=" + this.image + ", delay=" + this.delay + ")";
      }

      public int hashCode() {
         int result = this.image.hashCode();
         result = result * 31 + Integer.hashCode(this.delay);
         return result;
      }

      public boolean equals(@Nullable Object other) {
         if (this == other) {
            return true;
         } else if (!(other instanceof GifFrame)) {
            return false;
         } else {
            GifFrame gifFrame = (GifFrame)other;
            if (!Intrinsics.areEqual(this.image, gifFrame.image)) {
               return false;
            } else {
               return this.delay == gifFrame.delay;
            }
         }
      }
   }
}
