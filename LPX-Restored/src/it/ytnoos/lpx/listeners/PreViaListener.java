package it.ytnoos.lpx.listeners;

import com.github.retrooper.packetevents.event.PacketEvent;
import com.github.retrooper.packetevents.event.PacketListenerAbstract;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;

public class PreViaListener extends PacketListenerAbstract {
   private final LPXListener lpxListener;

   public PreViaListener(LPXListener lpxListener2) {
      this.lpxListener = lpxListener2;
   }

   public void onPacketReceive(PacketReceiveEvent packetReceiveEvent) {
      this.lpxListener.handleReceive(packetReceiveEvent, true);
   }

   public void onPacketEventExternal(PacketEvent packetEvent) {
      this.lpxListener.handleDecode(packetEvent, true);
   }

   public boolean isPreVia() {
      return true;
   }
}
