package it.ytnoos.lpx.common.utils;

import com.github.retrooper.packetevents.netty.buffer.ByteBufHelper;
import com.github.retrooper.packetevents.protocol.item.ItemStack;
import com.github.retrooper.packetevents.protocol.nbt.NBT;
import com.github.retrooper.packetevents.protocol.nbt.NBTByte;
import com.github.retrooper.packetevents.protocol.nbt.NBTByteArray;
import com.github.retrooper.packetevents.protocol.nbt.NBTCompound;
import com.github.retrooper.packetevents.protocol.nbt.NBTDouble;
import com.github.retrooper.packetevents.protocol.nbt.NBTFloat;
import com.github.retrooper.packetevents.protocol.nbt.NBTInt;
import com.github.retrooper.packetevents.protocol.nbt.NBTIntArray;
import com.github.retrooper.packetevents.protocol.nbt.NBTList;
import com.github.retrooper.packetevents.protocol.nbt.NBTLong;
import com.github.retrooper.packetevents.protocol.nbt.NBTLongArray;
import com.github.retrooper.packetevents.protocol.nbt.NBTShort;
import com.github.retrooper.packetevents.protocol.nbt.NBTString;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import java.nio.charset.StandardCharsets;
import java.util.Map.Entry;

public class NettyUtils {

   private NettyUtils() {
   }

   public static boolean containsString(NBTCompound nbtCompound, String string) {
      for (NBT nbt : nbtCompound.getTags().values()) {
         if (containsString(nbt, string)) {
            return true;
         }
      }

      return false;
   }

   public static boolean containsString(NBT nbt, String string) {
      if (nbt instanceof NBTString) {
         return ((NBTString)nbt).getValue().contains(string);
      }

      if (nbt instanceof NBTCompound) {
         return containsString((NBTCompound)nbt, string);
      }

      if (nbt instanceof NBTList) {
         for (Object var12_o : ((NBTList)nbt).getTags()) { NBT nbt2 = (NBT) var12_o;
            if (containsString(nbt2, string)) {
               return true;
            }
         }
      }

      return false;
   }

   public static void closeChannel(ChannelHandlerContext channelHandlerContext) {
      channelHandlerContext.channel().close();
      channelHandlerContext.channel().flush();
   }

   public static void closeChannel(Channel channel) {
      channel.close();
      channel.flush();
   }

   public static void discardReadable(ByteBuf byteBuf) {
      byteBuf.readBytes(byteBuf.readableBytes());
   }

   public static void releaseOnce(ByteBuf byteBuf) {
      ByteBuf byteBuf2 = byteBuf;
      if (byteBuf2.refCnt() >= 1) {
         byteBuf2 = byteBuf2.readBytes(byteBuf2.readableBytes());
      }

      releaseAll(byteBuf2);
   }

   public static void releaseAll(ByteBuf byteBuf) {
      if (byteBuf.refCnt() > 0) {
         byteBuf.release(byteBuf.refCnt());
      }
   }

   public static long getSize(Object object) {
      return ByteBufHelper.capacity(object);
   }
}
