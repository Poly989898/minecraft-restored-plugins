package litebans;

import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Map.Entry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class KotlinData5 {
   public static final KotlinData5Companion KOTLIN_DATA5_COMPANION = new KotlinData5Companion(null);
   private final long longValue;
   private final KotlinRandom kotlinRandom;
   private final byte[] bytes5;
   private final Bitflipper bitflipper;
   private final boolean flag4;
   private long longValue2;
   private int intValue6;
   private int intValue7;
   private final byte[] bytes6;
   private final byte[] bytes7;
   private final byte[] bytes8;
   private List list2;
   private List list3;
   private byte[][] bytesArray;
   private boolean flag5;
   private boolean flag6;
   public static final boolean FLAG = false;
   public static final int VALUE_44 = 44;
   public static final int VALUE_80 = 80;
   public static final boolean FLAG_2 = false;
   public static final char CHAR_VALUE = '-';
   public static final byte VALUE_79 = 79;
   public static final int VALUE_12 = 12;
   public static final int VALUE_40 = 40;
   public static final byte VALUE_127 = 127;
   public static final int VALUE_128 = 128;
   public static final int VALUE_1500 = 1500;
   public static final int VALUE_16 = 16;
   public static final int VALUE_7 = 7;
   public static final int VALUE_MINUS_7 = -7;
   public static final int VALUE_140 = 140;
   public static final int VALUE_4 = 4;
   public static final int VALUE_28 = 28;
   public static final boolean FLAG_3 = false;
   public static final int VALUE_4096 = 4096;
   public static final int VALUE_1024 = 1024;
   public static final int VALUE_512 = 512;
   public static final int VALUE_2400 = 2400;
   public static final int VALUE_1 = 1;
   public static final boolean FLAG_4 = false;
   public static final boolean FLAG_5 = false;
   public static final boolean FLAG_6 = false;
   public static final boolean FLAG_7 = false;
   public static final boolean FLAG_8 = false;
   public static final boolean FLAG_9 = false;
   public static final boolean FLAG_10 = false;
   public static final boolean FLAG_11 = false;
   public static final boolean FLAG_12 = false;

   private KotlinData5(long value, KotlinRandom kotlinRandom2, byte[] bytes, Bitflipper bitflipper2, boolean flag, long value2, int value3, int value4) {
      this.longValue = value;
      this.kotlinRandom = kotlinRandom2;
      this.bytes5 = bytes;
      this.bitflipper = bitflipper2;
      this.flag4 = flag;
      this.longValue2 = value2;
      this.intValue6 = value3;
      this.intValue7 = value4;
      this.bytes6 = new byte[80];
      this.bytes7 = new byte[80];
      byte[] bytes2 = new byte[]{32, 16, 8, 4, 2, 1};
      this.bytes8 = bytes2;
      this.list2 = CollectionsKt.emptyList();
      this.list3 = CollectionsKt.emptyList();
      this.perform();
   }

   public KotlinData5(long value, KotlinRandom kotlinRandom2, byte[] bytes, Bitflipper bitflipper2, boolean flag, long value2, int value3, int value4, int value5, DefaultConstructorMarker defaultConstructorMarker) {
      if ((value5 & 8) != 0) {
         bitflipper2 = new Bitflipper(kotlinRandom2, null, 2, null);
      }

      if ((value5 & 16) != 0) {
         flag = false;
      }

      if ((value5 & 32) != 0) {
         value2 = 0L;
      }

      if ((value5 & 64) != 0) {
         value3 = 0;
      }

      if ((value5 & 128) != 0) {
         value4 = 0;
      }

      this(value, kotlinRandom2, bytes, bitflipper2, flag, value2, value3, value4, null);
   }

   public final long getLongValue() {
      return this.longValue;
   }

   public final KotlinRandom getKotlinRandom() {
      return this.kotlinRandom;
   }

   public final Bitflipper getBitflipper() {
      return this.bitflipper;
   }

   public final boolean isFlag4() {
      return this.flag4;
   }

   public final byte[] getBytes6() {
      return this.bytes6;
   }

   public final byte[] getBytes7() {
      return this.bytes7;
   }

   public final List getList2() {
      return this.list2;
   }

   public final void setList2(@NotNull List list) {
      this.list2 = list;
   }

   public final List getList3() {
      return this.list3;
   }

   public final void setList3(@NotNull List list) {
      this.list3 = list;
   }

   public final byte[][] getBytesArray() {
      return this.bytesArray;
   }

   public final void setBytesArray(@Nullable byte[][] bytesArray) {
      this.bytesArray = bytesArray;
   }

   public final boolean isFlag5() {
      return this.flag5;
   }

   public final void setFlag5(boolean flag) {
      this.flag5 = flag;
   }

   public final void setFlag6(boolean flag) {
      this.flag6 = flag;
   }

   public final StringBuilder computeStringBuilder(@NotNull byte[] bytes) {
      StringBuilder stringBuilder = new StringBuilder(bytes.length + 1);
      byte[] bytes2 = bytes;
      int value = 0;

      for (int i = bytes2.length; value < i; value++) {
         byte b = bytes2[value];
         stringBuilder.append((char)(this.getBytes7()[b] + 44));
      }

      return stringBuilder;
   }

   public final void perform() {
      this.bytes6[53] = 1;
      this.bytes7[1] = 53;
      this.bytes6[57] = 2;
      this.bytes7[2] = 57;
      this.bytes6[71] = 3;
      this.bytes7[3] = 71;
      this.bytes6[64] = 4;
      this.bytes7[4] = 64;
      this.bytes6[70] = 5;
      this.bytes7[5] = 70;
      this.bytes6[61] = 6;
      this.bytes7[6] = 61;
      this.bytes6[65] = 7;
      this.bytes7[7] = 65;
      this.bytes6[66] = 8;
      this.bytes7[8] = 66;
      this.bytes6[67] = 9;
      this.bytes7[9] = 67;
      this.bytes6[72] = 10;
      this.bytes7[10] = 72;
      this.bytes6[63] = 11;
      this.bytes7[11] = 63;
      this.bytes6[55] = 12;
      this.bytes7[12] = 55;
      this.bytes6[56] = 13;
      this.bytes7[13] = 56;
      this.bytes6[73] = 14;
      this.bytes7[14] = 73;
      this.bytes6[51] = 15;
      this.bytes7[15] = 51;
      this.bytes6[5] = 16;
      this.bytes7[16] = 5;
      this.bytes6[54] = 17;
      this.bytes7[17] = 54;
      this.bytes6[68] = 18;
      this.bytes7[18] = 68;
      this.bytes6[60] = 19;
      this.bytes7[19] = 60;
      this.bytes6[78] = 20;
      this.bytes7[20] = 78;
      this.bytes6[77] = 21;
      this.bytes7[21] = 77;
      this.bytes6[6] = 22;
      this.bytes7[22] = 6;
      this.bytes6[59] = 23;
      this.bytes7[23] = 59;
      this.bytes6[7] = 24;
      this.bytes7[24] = 7;
      this.bytes6[74] = 25;
      this.bytes7[25] = 74;
      this.bytes6[4] = 26;
      this.bytes7[26] = 4;
      this.bytes6[76] = 27;
      this.bytes7[27] = 76;
      this.bytes6[62] = 28;
      this.bytes7[28] = 62;
      this.bytes6[58] = 29;
      this.bytes7[29] = 58;
      this.bytes6[8] = 30;
      this.bytes7[30] = 8;
      this.bytes6[9] = 31;
      this.bytes7[31] = 9;
      this.bytes6[13] = 32;
      this.bytes7[32] = 13;
      this.bytes6[12] = 33;
      this.bytes7[33] = 12;
      this.bytes6[10] = 34;
      this.bytes7[34] = 10;
      this.bytes6[11] = 35;
      this.bytes7[35] = 11;
      this.bytes6[75] = 36;
      this.bytes7[36] = 75;
      this.bytes6[69] = 37;
      this.bytes7[37] = 69;
      this.bytes6[1] = 79;
      this.bytes7[79] = 1;
   }

   public final int computeIntValue(char c) {
      return this.getBytes6()[c - 44] << 16;
   }

   public final void handleKotlinUtil(@NotNull KotlinUtil26 kotlinUtil) {
      List list = this.list2;
      Intrinsics.checkNotNullExpressionValue(this.list2, "");
      KotlinUtil30.asMutableList(list).add(kotlinUtil);
   }

   public final void perform2() {
      Iterable iterable = this.list2;
      var arrayList = new ArrayList();

      for (Object obj : iterable) {
         KotlinUtil26 kotlinUtil26 = (KotlinUtil26)obj;
         if (kotlinUtil26.getByteValue() != 0) {
            arrayList.add(obj);
         }
      }

      this.list2 = arrayList;
   }

   public final void handleBytes(@NotNull byte[] bytes, @NotNull ByteArrayOutputStream byteArrayOutputStream) {
      try {
         int value = 0;

         for (int i = bytes.length; value < i; value++) {
            byte b = bytes[value];
            if (b >= 128 || b < -8) {
               Object obj;
               label32: {
                  for (Object obj2 : this.list2) {
                     KotlinUtil26 kotlinUtil26 = (KotlinUtil26)obj2;
                     if (kotlinUtil26.getByteValue() == b) {
                        obj = obj2;
                        break label32;
                     }
                  }

                  obj = null;
               }

               KotlinUtil26 kotlinUtil262 = (KotlinUtil26)obj;
               if (kotlinUtil262 != null) {
                  byteArrayOutputStream.write(kotlinUtil262.getBytes2());
                  continue;
               }
            }

            byteArrayOutputStream.write(b);
         }
      } catch (IOException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   public final byte[] computeBytes(@NotNull byte[] bytes, @NotNull KotlinUtil26 kotlinUtil) {
      byte[] bytes2 = kotlinUtil.getBytes2();
      byte[] bytes3 = new byte[bytes.length];
      int value = 0;
      int value2 = bytes.length - bytes2.length;
      int value3 = Integer.MIN_VALUE;
      boolean flag = false;
      int value4 = 0;
      byte[] bytes4 = bytes;
      int value5 = 0;
      int value6 = 0;

      for (int i = bytes4.length; value6 < i; value6++) {
         byte b = bytes4[value6];
         int value7 = value5++;
         if (value7 >= value3) {
            if (value7 < value2 && StringMatching.bytesEqual(bytes2, bytes, value7, value7 + bytes2.length)) {
               flag = true;
               int value8 = value;
               value = value8 + 1;
               bytes3[value8] = kotlinUtil.getByteValue();
               value4++;
               value3 = value7 + bytes2.length;
               continue;
            }

            flag = false;
         }

         if (!flag) {
            int value9 = value;
            value = value9 + 1;
            bytes3[value9] = b;
         }
      }

      return value == bytes.length ? bytes : ArraysKt.computeBytes(bytes3, 0, value);
   }

   public final byte[] computeBytes(@NotNull byte[] bytes, int value, int value2, @NotNull Function1 callback) {
      byte[] bytes2 = bytes;
      byte byte2 = (byte)value;
      List list = this.list2;
      int value3 = 0;
      Iterable iterable = list;

      for (Object obj : iterable) {
         KotlinUtil26 kotlinUtil26 = (KotlinUtil26)obj;
         if (++value3 > value2) {
            return bytes2;
         }

         boolean flag = kotlinUtil26.getByteValue() == 0;
         if (flag) {
            byte b = byte2;
            byte2 = (byte)(b + 1);
            kotlinUtil26.setByteValue(b);
         } else {
            byte2 = (byte)(kotlinUtil26.getByteValue() + 1);
         }

         byte[] bytes3 = this.computeBytes(bytes2, kotlinUtil26);
         if (bytes3 != bytes2) {
            bytes2 = bytes3;
            callback.computeObj(kotlinUtil26);
         } else if (flag) {
            kotlinUtil26.setByteValue((byte)0);
            byte2 += -1;
            value3 += -1;
         }
      }

      CharSequence string = bytes.toString();
      return bytes2;
   }

   public static byte[] computeBytes(KotlinData5 kotlinData, byte[] bytes, int value, int value2, Function1 callback, int value3, Object obj) {
      if ((value3 & 2) != 0) {
         value = 128;
      }

      if ((value3 & 4) != 0) {
         value2 = 70;
      }

      if ((value3 & 8) != 0) {
         callback = obj2 -> Unit.UNIT;
      }

      return kotlinData.computeBytes(bytes, value, value2, callback);
   }

   public final void handleBytes(@NotNull byte[] bytes, @NotNull byte[] bytes2, boolean flag, boolean flag2) {
      if (flag2) {
         this.kotlinRandom.setSeed(this.longValue);
      }

      if (flag) {
         byte[] bytes3 = bytes2;
         int value = 0;
         int value2 = 0;

         for (int i = bytes3.length; value2 < i; value2++) {
            byte b2 = bytes3[value2];
            int value3 = value++;
            bytes2[value3] = this.computeByteValue(bytes[value3]);
         }
      } else {
         byte[] bytes4 = bytes2;
         int value4 = 0;
         int value5 = 0;

         for (int i2 = bytes4.length; value5 < i2; value5++) {
            byte b3 = bytes4[value5];
            int value6 = value4++;
            bytes2[value6] = this.computeByteValue2(bytes[value6]);
         }
      }
   }

   public static void handleKotlinData(KotlinData5 kotlinData, byte[] bytes, byte[] bytes2, boolean flag, boolean flag2, int value, Object obj) {
      if ((value & 4) != 0) {
         flag = true;
      }

      if ((value & 8) != 0) {
         flag2 = true;
      }

      kotlinData.handleBytes(bytes, bytes2, flag, flag2);
   }

   public final void handleBytes(@NotNull byte[] bytes, int value, boolean flag, boolean flag2) {
      for (int i = 0; i < value; i++) {
         int value2 = i;
         int value3 = value2 * 2;
         byte b = KotlinUtil21.computeByteValue2(this.bytes5, this.kotlinRandom);
         if (flag2) {
            b += KotlinUtil21.computeByteValue2(this.bytes5, this.kotlinRandom);
         }

         bytes[value3] = b;
         byte b2 = KotlinUtil21.computeByteValue2(this.bitflipper.getBytes2(), this.kotlinRandom);
         if (flag) {
            bytes[value3 + 1] = b2;
         }
      }
   }

   public final int computeIntValue(@NotNull byte[] bytes, int value, @NotNull CharSequence message, boolean flag) {
      int value2 = 0;

      for (int i = 0; i < value; i++) {
         int value3 = i;
         int value4 = value3 * 2;
         byte b = bytes[value4];
         char charAt2 = message.charAt(value3);
         byte byte2 = (byte)(this.getBytes6()[charAt2 - ','] - b);
         if (flag) {
            byte b2 = bytes[value4 + 1];
            byte2 ^= b2;
         }

         value2 += this.computeIntValue(byte2);
         if (value2 <= -7) {
            return value2;
         }
      }

      return value2;
   }

   public final byte[] obtainBytes() {
      byte[] bytes = this.bitflipper.getBytes2();
      int value = this.list3.size();
      KotlinHelper7 kotlinHelper7 = new KotlinHelper7(Math.max(1, value / 4096), Math.max(1, value / 1024), Math.max(1, value / 512));
      HashMap hashMap = new HashMap(32);
      HashMap hashMap2 = new HashMap(this.bitflipper.getBytes2().length);

      for (Pair pair : (Iterable<Pair>) this.list3) {
         int intValue2 = ((Number)pair.getFirst()).intValue();
         byte byteValue2 = ((Number)pair.getSecond()).byteValue();
         byte b = 0;
         byte[] bytes2 = this.bytes8;
         int value2 = 0;

         for (int i = bytes2.length; value2 < i; value2++) {
            byte b2 = bytes2[value2];
            if ((byte)(byteValue2 ^ b2) < byteValue2) {
               b |= b2;
            }
         }

         Map map = hashMap2;
         Integer boxed = intValue2;
         Object obj = map.get(boxed);
         Object obj2;
         if (obj == null) {
            ArrayList arrayList = new ArrayList(32);
            map.put(boxed, arrayList);
            obj2 = arrayList;
         } else {
            obj2 = obj;
         }

         ArrayList arrayList2 = (ArrayList)obj2;
         arrayList2.add((byte)b);
      }

      Map map2 = hashMap2;

      for (Entry entry : (Iterable<Entry>) map2.entrySet()) {
         ArrayList value3 = (ArrayList)entry.getValue();
         Iterator iterator2 = value3.iterator();

         while (iterator2.hasNext()) {
            byte byteValue3 = ((Number)iterator2.next()).byteValue();
            Map map3 = hashMap;
            Byte byte2 = byteValue3;
            Object obj3 = map3.get(byte2);
            Object obj4;
            if (obj3 != null) {
               obj4 = obj3;
            } else {
               int value4;
               if (value3 instanceof Collection && value3.isEmpty()) {
                  value4 = 0;
               } else {
                  int value5 = 0;

                  for (Object obj5 : value3) {
                     byte byteValue4 = ((Number)obj5).byteValue();
                     if (byteValue4 == byteValue3) {
                        if (++value5 < 0) {
                           CollectionsKt.throwCountOverflow();
                        }
                     }
                  }

                  value4 = value5;
               }

               Integer boxed2 = value4;
               map3.put(byte2, boxed2);
               obj4 = boxed2;
            }

            int intValue3 = ((Number)obj4).intValue();
            this.handleIntValue(intValue3, byteValue3, ((Number)entry.getKey()).intValue(), kotlinHelper7);
         }

         Iterable iterable = hashMap.entrySet();
         Iterator iterator3 = iterable.iterator();
         if (!iterator3.hasNext()) {
            throw new NoSuchElementException();
         }

         Object obj6 = iterator3.next();
         Object obj7;
         if (!iterator3.hasNext()) {
            obj7 = obj6;
         } else {
            Entry entry2 = (Entry)obj6;
            int intValue4 = ((Number)entry2.getValue()).intValue();

            do {
               Object obj8 = iterator3.next();
               Entry entry3 = (Entry)obj8;
               int intValue5 = ((Number)entry3.getValue()).intValue();
               if (intValue4 < intValue5) {
                  obj6 = obj8;
                  intValue4 = intValue5;
               }
            } while (iterator3.hasNext());

            obj7 = obj6;
         }

         Entry entry4 = (Entry)obj7;
         this.handleIntValue(((Number)entry4.getValue()).intValue(), ((Number)entry4.getKey()).byteValue(), ((Number)entry.getKey()).intValue(), kotlinHelper7);
         hashMap.clear();
      }

      this.list3 = CollectionsKt.emptyList();
      return bytes;
   }

   private final void handleIntValue(int value, byte b, int value2, KotlinHelper7 kotlinHelper) {
      if (b <= 7 && value >= kotlinHelper.getIntValue() || b <= 31 && value >= kotlinHelper.getIntValue2() || value > kotlinHelper.getIntValue3()) {
         this.bitflipper.getBytes2()[value2] = b;
      }
   }

   private final int computeIntValue(byte b) {
      byte b2;
      switch (b) {
         case -3:
         case -1:
         case 1:
         case 13:
            b2 = 3;
            break;
         case 0:
            b2 = 7;
            break;
         default:
            b2 = -1;
      }

      return b2;
   }

   public final byte computeByteValue(byte b) {
      if (b == 79) {
         this.flag6 = !this.flag6;
         return b;
      } else {
         byte b2 = KotlinUtil21.computeByteValue2(this.bytes5, this.kotlinRandom);
         int value = b - b2;
         int value2 = this.bitflipper.computeIntValue(this, (byte)value);
         return (byte)(value ^ value2);
      }
   }

   public final byte computeByteValue2(byte b) {
      if (b == 79) {
         this.flag6 = !this.flag6;
         return 0;
      } else {
         byte b2 = KotlinUtil21.computeByteValue2(this.bytes5, this.kotlinRandom);
         byte byte2 = (byte)this.bitflipper.computeIntValue(this, b);
         return (byte)((byte)(b ^ byte2) + b2);
      }
   }

   public final byte[] computeBytes(@Nullable Platform platform, @NotNull String text, boolean flag, boolean flag2, @NotNull KotlinRandom kotlinRandom2, @NotNull byte[] bytes) {
      this.kotlinRandom.setSeed(this.longValue);
      CharSequence message = text;
      char[] chars = new char[]{','};
      List mutableList = CollectionsKt.toMutableList(StringsKt.splitWithDefaults(message, chars, false, 0, 6, null));
      if (mutableList.size() <= 1) {
         if (platform != null) {
            ConfigModule module = (ConfigModule)platform.getModule(ConfigModule.class);
            if (module != null) {
               CommandArguments commandArguments;
               String string = text.toString();
               String[] texts;
               if (Intrinsics.areEqual(string, "sort")) {
                  String[] texts2 = new String[]{"00"};
                  texts = texts2;
               } else {
                  String[] texts3 = new String[]{"01:" + string};
                  texts = texts3;
               }

               String[] texts4 = texts;
               commandArguments = new CommandArguments(module, Arrays.copyOf(texts4, texts4.length));
               if (commandArguments.obtainIntValue() == module.hashCode()) {
                  throw new IllegalArgumentException();
               }
            }
         }

         byte[] bytes2 = text.getBytes(KotlinCharsets.CHARSET_5);
         int value = 0;
         int value2 = 0;

         for (int i = bytes2.length; value2 < i; value2++) {
            byte b2 = bytes2[value2];
            int value3 = value++;
            bytes2[value3] = this.getBytes6()[b2 - 44];
         }

         return bytes2;
      } else {
         StringBuilder stringBuilder = new StringBuilder(16);
         StringBuilder stringBuilder2 = new StringBuilder(text.length());
         int value4 = 1500;
         String string2 = (String)mutableList.get(0);

         while (mutableList.size() != 1) {
            int value5 = string2.length() + 1;
            int value6 = value4 / (value5 + 4);
            long value7 = this.getKotlinRandom().getState()[0];
            long value8 = this.getKotlinRandom().getState()[1];
            boolean flag3 = false;
            this.handleBytes(bytes, value5, flag, flag3);
            StringsKt.computeStringBuilder(stringBuilder).append(string2).append(',');
            int value9 = this.computeIntValue(bytes, value5, stringBuilder, flag);
            if (value9 >= Math.min(7 * value5, value6)) {
               value4 += 4 * value5;
               mutableList.remove(string2);
               stringBuilder2.append(string2).append(',');
               string2 = (String)mutableList.get(0);
            } else {
               value4--;
               this.getKotlinRandom().setState(value7, value8);
               if (mutableList.size() < 10) {
                  value4 -= 128;
               }

               string2 = (String)KotlinUtil21.access$size(mutableList, kotlinRandom2);
            }
         }

         stringBuilder2.append((String)mutableList.get(0));
         byte[] bytes3 = TextUtil.access$toString(stringBuilder2, KotlinCharsets.CHARSET_5);
         int value10 = 0;
         int value11 = 0;

         for (int i2 = bytes3.length; value11 < i2; value11++) {
            byte b3 = bytes3[value11];
            int value12 = value10++;
            bytes3[value12] = this.getBytes6()[b3 - 44];
         }

         return bytes3;
      }
   }

   public final void handleBytes2(@NotNull byte[] bytes, int value, boolean flag, boolean flag2) {
      int value2 = value;
      int value3 = 10;
      if (!(this.list2 instanceof ArrayList)) {
         this.list2 = new ArrayList(512);
      }

      if (flag) {
         value3 = 10;
      }

      while (value2-- > 0 && value3 >= 2) {
         int value4 = value3;
         int value5 = Math.max(1, this.computeIntValue(value4));
         Pair pair = KotlinUtil26.KOTLIN_HELPER.computePair(bytes, value4, value5, this, !flag);
         if (pair == KotlinUtil26.KOTLIN_HELPER.obtainPair()) {
            value3 = value4 - 1;
         } else {
            short unbox2 = ((ULong)pair.getFirst()).unbox();
            byte[] bytes2 = (byte[])pair.getSecond();
            this.handleKotlinUtil(new KotlinUtil26(bytes2, (byte)0, unbox2, 2, null));
         }
      }

      this.perform3();
   }

   public static void handleKotlinData(KotlinData5 kotlinData, byte[] bytes, int value, boolean flag, boolean flag2, int value2, Object obj) {
      if ((value2 & 2) != 0) {
         value = 16000;
      }

      if ((value2 & 4) != 0) {
         flag = false;
      }

      if ((value2 & 8) != 0) {
         flag2 = false;
      }

      kotlinData.handleBytes2(bytes, value, flag, flag2);
   }

   private final int computeIntValue(int value) {
      return 30 / value / value - 2;
   }

   public final void perform3() {
      if (!this.list2.isEmpty()) {
         List list = this.list2;
         Intrinsics.checkNotNullExpressionValue(this.list2, "");
         List mutableList = KotlinUtil30.asMutableList(list);
         if (mutableList.size() > 1) {
            CollectionsKt.handleList(mutableList, new KotlinComparatorImpl4());
         }
      }
   }

   public KotlinData5(long value, KotlinRandom kotlinRandom2, byte[] bytes, Bitflipper bitflipper2, boolean flag, long value2, int value3, int value4, DefaultConstructorMarker defaultConstructorMarker) {
      this(value, kotlinRandom2, bytes, bitflipper2, flag, value2, value3, value4);
   }
}
