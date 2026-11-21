package it.ytnoos.lpx;

import com.github.retrooper.packetevents.wrapper.PacketWrapper;

public enum IterationType {
   HORIZONTAL,
   VERTICAL;

   private static PacketWrapper[] packetWrappers;

   IterationType() {
   }

   public static PacketWrapper[] getPacketWrappers() {
      return packetWrappers;
   }

}
