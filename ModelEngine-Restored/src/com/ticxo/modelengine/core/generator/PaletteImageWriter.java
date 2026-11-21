package com.ticxo.modelengine.core.generator;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.CRC32;
import java.util.zip.Deflater;

final class PaletteImageWriter {
   private static final byte[] SIGNATURE = new byte[]{-119, 80, 78, 71, 13, 10, 26, 10};
   private static final int MAX_PALETTE = 256;
   private static final byte COLOR_TYPE_INDEXED = 3;
   private static final byte FILTER_NONE = 0;

   private PaletteImageWriter() {
   }

   static byte[] encode(BufferedImage image) throws IOException {
      if (image == null) {
         return null;
      }

      int width = image.getWidth();
      int height = image.getHeight();
      if (width > 0 && height > 0) {
         int[] pixels = image.getRGB(0, 0, width, height, null, 0, width);
         HashMap<Integer, Integer> distinct = new HashMap<>();

         for (int pixel : pixels) {
            if (distinct.size() > 256) {
               return null;
            }

            distinct.putIfAbsent(pixel, 0);
         }

         if (distinct.size() > 256) {
            return null;
         }

         int[] palette = distinct.keySet().stream().sorted((a, b) -> {
            int alpha = Integer.compare(a >>> 24, b >>> 24);
            return alpha != 0 ? alpha : Integer.compare(a & 16777215, b & 16777215);
         }).mapToInt(Integer::intValue).toArray();

         for (int i = 0; i < palette.length; i++) {
            distinct.put(palette[i], i);
         }

         int bitDepth = bitDepth(palette.length);
         ByteArrayOutputStream out = new ByteArrayOutputStream();
         out.write(SIGNATURE);
         writeChunk(out, "IHDR", header(width, height, bitDepth));
         writeChunk(out, "PLTE", palette(palette));
         int translucent = countTranslucent(palette);
         if (translucent > 0) {
            writeChunk(out, "tRNS", transparency(palette, translucent));
         }

         writeChunk(out, "IDAT", deflate(scanlines(pixels, distinct, width, height, bitDepth)));
         writeChunk(out, "IEND", new byte[0]);
         return out.toByteArray();
      } else {
         return null;
      }
   }

   private static int bitDepth(int colors) {
      if (colors <= 2) {
         return 1;
      } else if (colors <= 4) {
         return 2;
      } else {
         return colors <= 16 ? 4 : 8;
      }
   }

   private static int countTranslucent(int[] palette) {
      int count = 0;

      for (int i = 0; i < palette.length; i++) {
         if (palette[i] >>> 24 != 255) {
            count = i + 1;
         }
      }

      return count;
   }

   private static byte[] header(int width, int height, int bitDepth) {
      byte[] data = new byte[13];
      writeInt(data, 0, width);
      writeInt(data, 4, height);
      data[8] = (byte)bitDepth;
      data[9] = 3;
      return data;
   }

   private static byte[] palette(int[] palette) {
      byte[] data = new byte[palette.length * 3];

      for (int i = 0; i < palette.length; i++) {
         data[i * 3] = (byte)(palette[i] >>> 16);
         data[i * 3 + 1] = (byte)(palette[i] >>> 8);
         data[i * 3 + 2] = (byte)palette[i];
      }

      return data;
   }

   private static byte[] transparency(int[] palette, int count) {
      byte[] data = new byte[count];

      for (int i = 0; i < count; i++) {
         data[i] = (byte)(palette[i] >>> 24);
      }

      return data;
   }

   private static byte[] scanlines(int[] pixels, Map<Integer, Integer> indices, int width, int height, int bitDepth) {
      int perByte = 8 / bitDepth;
      int rowBytes = (width + perByte - 1) / perByte;
      byte[] raw = new byte[(rowBytes + 1) * height];
      int at = 0;

      for (int y = 0; y < height; y++) {
         raw[at++] = 0;
         int row = y * width;

         for (int x = 0; x < width; x++) {
            int index = indices.get(pixels[row + x]);
            if (bitDepth == 8) {
               raw[at + x] = (byte)index;
            } else {
               int shift = 8 - bitDepth * (x % perByte + 1);
               raw[at + x / perByte] = (byte)(raw[at + x / perByte] | (byte)(index << shift));
            }
         }

         at += rowBytes;
      }

      return raw;
   }

   private static byte[] deflate(byte[] raw) {
      Deflater deflater = new Deflater(9);

      try {
         deflater.setInput(raw);
         deflater.finish();
         ByteArrayOutputStream out = new ByteArrayOutputStream(raw.length / 2 + 64);
         byte[] buffer = new byte[8192];

         while (!deflater.finished()) {
            int read = deflater.deflate(buffer);
            out.write(buffer, 0, read);
         }

         return out.toByteArray();
      } finally {
         deflater.end();
      }
   }

   private static void writeChunk(ByteArrayOutputStream out, String type, byte[] data) throws IOException {
      byte[] header = new byte[4];
      writeInt(header, 0, data.length);
      out.write(header);
      byte[] name = type.getBytes(StandardCharsets.US_ASCII);
      out.write(name);
      out.write(data);
      CRC32 crc = new CRC32();
      crc.update(name);
      crc.update(data);
      byte[] trailer = new byte[4];
      writeInt(trailer, 0, (int)crc.getValue());
      out.write(trailer);
   }

   private static void writeInt(byte[] target, int offset, int value) {
      target[offset] = (byte)(value >>> 24);
      target[offset + 1] = (byte)(value >>> 16);
      target[offset + 2] = (byte)(value >>> 8);
      target[offset + 3] = (byte)value;
   }
}
