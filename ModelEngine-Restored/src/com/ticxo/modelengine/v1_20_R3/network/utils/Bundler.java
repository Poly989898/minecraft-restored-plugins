package com.ticxo.modelengine.v1_20_R3.network.utils;

import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.nms.network.ProtectedPacket;
import com.ticxo.modelengine.api.utils.config.ConfigProperty;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundBundlePacket;
import net.minecraft.network.protocol.game.PacketListenerPlayOut;

public class Bundler {
   private static int BUNDLE_SIZE;
   private final List<List<Packet<PacketListenerPlayOut>>> bundles = new ArrayList<>();

   public synchronized void appendPacket(Packet<PacketListenerPlayOut> packet) {
      if (this.bundles.isEmpty()) {
         this.bundles.add(new ArrayList<>());
      }

      List<Packet<PacketListenerPlayOut>> bundleList = this.bundles.get(this.bundles.size() - 1);
      if (bundleList.size() == BUNDLE_SIZE) {
         bundleList = new ArrayList<>();
         this.bundles.add(bundleList);
      }

      bundleList.add(packet);
   }

   public synchronized void appendPacket(Collection<? extends Packet> packets) {
      if (!packets.isEmpty()) {
         List<Packet<PacketListenerPlayOut>> bundleList = this.bundles.isEmpty() ? null : this.bundles.get(this.bundles.size() - 1);
         if (bundleList == null || bundleList.size() + packets.size() > BUNDLE_SIZE) {
            bundleList = new ArrayList<>();
            this.bundles.add(bundleList);
         }

         for (Packet p : packets) {
            if (bundleList.size() == BUNDLE_SIZE) {
               bundleList = new ArrayList<>();
               this.bundles.add(bundleList);
            }

            bundleList.add(p);
         }
      }
   }

   public void clear() {
      this.bundles.clear();
   }

   public void bundle(Consumer<Object> send) {
      for (List<Packet<PacketListenerPlayOut>> list : this.bundles) {
         ProtectedPacket wrapped = new ProtectedPacket(new ClientboundBundlePacket(list));
         send.accept(wrapped);
      }
   }

   static {
      ModelEngineAPI.getAPI().getConfigManager().registerReferenceUpdate(() -> BUNDLE_SIZE = Math.min(ConfigProperty.BUNDLE_SIZE.getInt(), 4096));
   }
}
