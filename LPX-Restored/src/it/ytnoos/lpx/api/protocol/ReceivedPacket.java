package it.ytnoos.lpx.api.protocol;

import com.github.retrooper.packetevents.protocol.component.ComponentType;
import com.github.retrooper.packetevents.protocol.item.ItemStack;
import com.github.retrooper.packetevents.protocol.nbt.NBT;
import com.github.retrooper.packetevents.protocol.nbt.NBTByteArray;
import com.github.retrooper.packetevents.protocol.nbt.NBTCompound;
import com.github.retrooper.packetevents.protocol.nbt.NBTIntArray;
import com.github.retrooper.packetevents.protocol.nbt.NBTList;
import com.github.retrooper.packetevents.protocol.nbt.NBTLongArray;
import com.github.retrooper.packetevents.protocol.nbt.NBTNumber;
import com.github.retrooper.packetevents.protocol.nbt.NBTString;
import com.github.retrooper.packetevents.protocol.packettype.PacketTypeCommon;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;

public class ReceivedPacket {
   private static final ThreadLocal<NumberFormat> FORMAT = ThreadLocal.withInitial(() -> new DecimalFormat("0.000"));
   private static final int MAX_ORIGINAL_NBT = 1000;
   private final String playerName;
   private final ClientVersion clientVersion;
   private final long createdAtNanos;
   private final long receivedAtTick;
   private final PacketTypeCommon packetType;
   private final long size;
   private Object packet;
   private long elapsedTime;
   private String packetString;
   private List<NBT> originalNbtData;

   public ReceivedPacket(String string, ClientVersion clientVersion2, long longValue, PacketTypeCommon packetTypeCommon, long longValue2, Object object) {
      this.playerName = string;
      this.clientVersion = clientVersion2;
      this.receivedAtTick = longValue;
      this.packetType = packetTypeCommon;
      this.createdAtNanos = System.nanoTime();
      this.size = longValue2;
      this.packet = object;
   }

   public String getPlayerName() {
      return this.playerName;
   }

   public long getReceivedAtTick() {
      return this.receivedAtTick;
   }

   public ClientVersion getClientVersion() {
      return this.clientVersion;
   }

   public String getClientVersionName() {
      return this.clientVersion.getReleaseName();
   }

   public PacketTypeCommon getPacketType() {
      return this.packetType;
   }

   public Object getPacket() {
      return this.packet;
   }

   public void setPacket(Object object) {
      this.packet = object;
   }

   public long getSize() {
      return this.size;
   }

   public void addOriginalNbt(NBT nbt) {
      if (this.originalNbtData == null) {
         this.originalNbtData = new ArrayList<>();
      }

      if (this.originalNbtData.size() < 1000) {
         this.originalNbtData.add(nbt);
      }
   }

   public long getElapsedTime() {
      if (this.elapsedTime == 0L) {
         this.elapsedTime = System.nanoTime() - this.createdAtNanos;
      }

      return this.elapsedTime;
   }

   public String getFormattedElapsedTime() {
      return FORMAT.get().format(this.getElapsedTime() / 1000000.0);
   }

   @Override
   public String toString() {
      if (this.packetString == null) {
         this.packetString = this.buildPacketString().replace('\n', ' ');
         this.originalNbtData = null;
      }

      return this.packetString;
   }

   private String buildPacketString() {
      if (this.packet == null) {
         return "";
      }

      StringBuilder stringBuilder = new StringBuilder();

      for (Field field : this.packet.getClass().getDeclaredFields()) {
         if (field.getDeclaringClass().equals(this.packet.getClass())) {
            try {
               field.setAccessible(true);
               Object object = field.get(this.packet);
               stringBuilder.append(field.getName()).append("=");
               appendValue(object, stringBuilder);
               stringBuilder.append(" ");
            } catch (IllegalAccessException illegalAccessException) {
            }
         }
      }

      if (this.originalNbtData != null && !this.originalNbtData.isEmpty()) {
         stringBuilder.append("originalNbt=[");
         boolean flag = true;

         for (NBT nbt : this.originalNbtData) {
            if (!flag) {
               stringBuilder.append(", ");
            }

            flag = false;
            appendNbt(nbt, stringBuilder);
         }

         stringBuilder.append("] ");
      }

      return stringBuilder.toString();
   }

   private static void appendValue(Object object, StringBuilder stringBuilder) {
      if (object instanceof ItemStack itemStack) {
         appendItemStack(itemStack, stringBuilder);
      } else if (object instanceof Map map) {
         appendMap(map, stringBuilder);
      } else if (object instanceof Optional optional) {
         if (optional.isPresent()) {
            appendValue(optional.get(), stringBuilder);
         } else {
            stringBuilder.append("empty");
         }
      } else if (object instanceof Object[] objects) {
         appendArray(objects, stringBuilder);
      } else if (object instanceof NBT nbt) {
         appendNbt(nbt, stringBuilder);
      } else if (object instanceof byte[] bs) {
         stringBuilder.append(Arrays.toString(bs));
      } else {
         stringBuilder.append(object);
      }
   }

   private static void appendItemStack(ItemStack itemStack, StringBuilder stringBuilder) {
      stringBuilder.append("[type=").append(itemStack.getType().getName());
      NBTCompound nbtCompound = itemStack.getNBT();
      if (nbtCompound != null && !nbtCompound.isEmpty()) {
         stringBuilder.append(", nbt=");
         appendNbt(nbtCompound, stringBuilder);
      }

      if (itemStack.hasComponentPatches()) {
         appendComponentPatches(itemStack, stringBuilder);
      }

      stringBuilder.append("]");
   }

   private static void appendComponentPatches(ItemStack itemStack, StringBuilder stringBuilder) {
      ClientVersion clientVersion2 = itemStack.getVersion();

      for (Entry entry : itemStack.getComponents().getPatches().entrySet()) {
         stringBuilder.append(", ").append(((ComponentType)entry.getKey()).getName()).append("=");
         if (((Optional)entry.getValue()).isPresent()) {
            try {
               ComponentType componentType = (ComponentType)entry.getKey();
               appendNbt(componentType.encode(((Optional)entry.getValue()).get(), clientVersion2), stringBuilder);
            } catch (Exception exception) {
               stringBuilder.append(((Optional)entry.getValue()).get());
            }
         } else {
            stringBuilder.append("REMOVED");
         }
      }
   }

   private static void appendMap(Map<?, ?> map, StringBuilder stringBuilder) {
      stringBuilder.append("{");
      boolean flag = true;

      for (Entry entry : map.entrySet()) {
         if (!flag) {
            stringBuilder.append(", ");
         }

         flag = false;
         stringBuilder.append(entry.getKey()).append("=");
         appendValue(entry.getValue(), stringBuilder);
      }

      stringBuilder.append("}");
   }

   private static void appendArray(Object[] objects, StringBuilder stringBuilder) {
      if (objects.length != 0) {
         stringBuilder.append("[");
         boolean flag = true;

         for (Object object : objects) {
            if (!flag) {
               stringBuilder.append(", ");
            }

            flag = false;
            appendValue(object, stringBuilder);
         }

         stringBuilder.append("]");
      }
   }

   private static void appendNbt(NBT nbt, StringBuilder stringBuilder) {
      if (nbt instanceof NBTCompound nbtCompound) {
         if (nbtCompound.isEmpty()) {
            return;
         }

         stringBuilder.append("[");
         boolean flag = true;

         for (Entry entry : nbtCompound.getTags().entrySet()) {
            if (!flag) {
               stringBuilder.append(", ");
            }

            flag = false;
            stringBuilder.append((String)entry.getKey()).append("=");
            appendNbt((NBT)entry.getValue(), stringBuilder);
         }

         stringBuilder.append("]");
      } else if (nbt instanceof NBTList nbtList) {
         if (nbtList.isEmpty()) {
            stringBuilder.append("[]");
            return;
         }

         boolean flag2 = true;

         for (Object var15_o : nbtList.getTags()) { NBT nbt2 = (NBT) var15_o;
            if (!flag2) {
               stringBuilder.append(", ");
            }

            flag2 = false;
            appendNbt(nbt2, stringBuilder);
         }
      } else if (nbt instanceof NBTNumber nbtNumber) {
         stringBuilder.append(nbtNumber.getAsNumber());
      } else if (nbt instanceof NBTString nbtString) {
         stringBuilder.append(nbtString.getValue());
      } else if (nbt instanceof NBTByteArray nbtByteArray) {
         stringBuilder.append(Arrays.toString(nbtByteArray.getValue()));
      } else if (nbt instanceof NBTIntArray nbtIntArray) {
         stringBuilder.append(Arrays.toString(nbtIntArray.getValue()));
      } else if (nbt instanceof NBTLongArray nbtLongArray) {
         stringBuilder.append(Arrays.toString(nbtLongArray.getValue()));
      } else {
         stringBuilder.append(nbt);
      }
   }
}
