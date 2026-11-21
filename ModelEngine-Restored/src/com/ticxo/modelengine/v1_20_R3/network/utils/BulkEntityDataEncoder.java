package com.ticxo.modelengine.v1_20_R3.network.utils;

import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketDataSerializer;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class BulkEntityDataEncoder {
   private static final byte PACKET_TYPE_BULK_DATA = 0;
   public static final int FIELD_TRANSLATION = 0;
   public static final int FIELD_LEFT_ROTATION = 1;
   public static final int FIELD_SCALE = 2;
   public static final int FIELD_RIGHT_ROTATION = 3;
   public static final int FIELD_TRANSFORM_DURATION = 4;
   public static final int FIELD_GLOW_DATA = 5;
   public static final int FIELD_BRIGHTNESS = 6;
   public static final int FIELD_RENDER_DATA = 7;
   private final PacketDataSerializer buf = new PacketDataSerializer(Unpooled.buffer(256));
   private int entryCount;
   private final int entryCountIndex;

   public BulkEntityDataEncoder() {
      this.buf.k(0);
      this.entryCountIndex = this.buf.writerIndex();
      this.buf.c(0);
      this.entryCount = 0;
   }

   public BulkEntityDataEncoder.EntryWriter startEntry(int entityId) {
      this.entryCount++;
      this.buf.c(entityId);
      return new BulkEntityDataEncoder.EntryWriter(this.buf);
   }

   public byte[] build() {
      int endIndex = this.buf.writerIndex();
      this.buf.h(this.entryCountIndex);
      this.buf.c(this.entryCount);
      this.buf.h(endIndex);
      byte[] data = new byte[this.buf.readableBytes()];
      this.buf.a(this.buf.readerIndex(), data);
      this.buf.release();
      return data;
   }

   public int getEntryCount() {
      return this.entryCount;
   }

   public static short floatToHalf(float f) {
      int bits = Float.floatToRawIntBits(f);
      int sign = bits >>> 16 & 32768;
      int exp = (bits >>> 23 & 0xFF) - 127;
      int mantissa = bits & 8388607;
      if (exp >= 16) {
         return (short)(sign | 31744);
      } else if (exp >= -14) {
         return (short)(sign | exp + 15 << 10 | mantissa >>> 13);
      } else if (exp >= -24) {
         int shift = -14 - exp;
         return (short)(sign | (8388608 | mantissa) >>> 13 + shift);
      } else {
         return (short)sign;
      }
   }

   private static void writeHalf(PacketDataSerializer buf, float f) {
      buf.l(floatToHalf(f));
   }

   public static class EntryWriter {
      private final PacketDataSerializer buf;
      private final int bitmaskIndex;
      private byte bitmask;

      EntryWriter(PacketDataSerializer buf) {
         this.buf = buf;
         this.bitmaskIndex = buf.writerIndex();
         buf.k(0);
         this.bitmask = 0;
      }

      public BulkEntityDataEncoder.EntryWriter translation(Vector3f pos) {
         this.bitmask = (byte)(this.bitmask | 1);
         BulkEntityDataEncoder.writeHalf(this.buf, pos.x);
         BulkEntityDataEncoder.writeHalf(this.buf, pos.y);
         BulkEntityDataEncoder.writeHalf(this.buf, pos.z);
         return this;
      }

      public BulkEntityDataEncoder.EntryWriter leftRotation(Quaternionf rot) {
         this.bitmask = (byte)(this.bitmask | 2);
         BulkEntityDataEncoder.writeHalf(this.buf, rot.x);
         BulkEntityDataEncoder.writeHalf(this.buf, rot.y);
         BulkEntityDataEncoder.writeHalf(this.buf, rot.z);
         BulkEntityDataEncoder.writeHalf(this.buf, rot.w);
         return this;
      }

      public BulkEntityDataEncoder.EntryWriter scale(Vector3f scale) {
         this.bitmask = (byte)(this.bitmask | 4);
         BulkEntityDataEncoder.writeHalf(this.buf, scale.x);
         BulkEntityDataEncoder.writeHalf(this.buf, scale.y);
         BulkEntityDataEncoder.writeHalf(this.buf, scale.z);
         return this;
      }

      public BulkEntityDataEncoder.EntryWriter rightRotation(Quaternionf rot) {
         this.bitmask = (byte)(this.bitmask | 8);
         BulkEntityDataEncoder.writeHalf(this.buf, rot.x);
         BulkEntityDataEncoder.writeHalf(this.buf, rot.y);
         BulkEntityDataEncoder.writeHalf(this.buf, rot.z);
         BulkEntityDataEncoder.writeHalf(this.buf, rot.w);
         return this;
      }

      public BulkEntityDataEncoder.EntryWriter transformDuration(int duration) {
         this.bitmask = (byte)(this.bitmask | 16);
         this.buf.c(duration);
         return this;
      }

      public BulkEntityDataEncoder.EntryWriter glowData(byte sharedData, int glowColor) {
         this.bitmask = (byte)(this.bitmask | 32);
         this.buf.k(sharedData);
         this.buf.p(glowColor);
         return this;
      }

      public BulkEntityDataEncoder.EntryWriter brightness(int brightness) {
         this.bitmask = (byte)(this.bitmask | 64);
         this.buf.p(brightness);
         return this;
      }

      public BulkEntityDataEncoder.EntryWriter renderData(byte billboard, float viewRange, byte displayType) {
         this.bitmask = (byte)(this.bitmask | 128);
         this.buf.k(billboard);
         this.buf.a(viewRange);
         this.buf.k(displayType);
         return this;
      }

      public void finish() {
         this.buf.b(this.bitmaskIndex, this.bitmask);
      }
   }
}
