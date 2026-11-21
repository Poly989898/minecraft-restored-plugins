package com.ticxo.modelengine.v1_21_R5.network.utils;

import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.nms.network.ProtectedPacket;
import com.ticxo.modelengine.api.utils.config.ConfigProperty;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBundlePacket;

public class Bundler {
   private static int BUNDLE_SIZE;
   private final List<List<Packet<? super ClientGamePacketListener>>> bundles = new ArrayList<>();

   public synchronized void appendPacket(Packet<? super ClientGamePacketListener> packet) {
      if (this.bundles.isEmpty()) {
         this.bundles.add(new ArrayList<>());
      }

      List<Packet<? super ClientGamePacketListener>> bundleList = this.bundles.getLast();
      if (bundleList.size() == BUNDLE_SIZE) {
         bundleList = new ArrayList<>();
         this.bundles.add(bundleList);
      }

      bundleList.add(packet);
   }

   public synchronized void appendPacket(Collection<Packet<? super ClientGamePacketListener>> packets) {
      if (!packets.isEmpty()) {
         List<Packet<? super ClientGamePacketListener>> bundleList = this.bundles.isEmpty() ? null : this.bundles.getLast();
         if (bundleList == null || bundleList.size() + packets.size() > BUNDLE_SIZE) {
            bundleList = new ArrayList<>();
            this.bundles.add(bundleList);
         }

         for (Packet<? super ClientGamePacketListener> packet : packets) {
            if (bundleList.size() == BUNDLE_SIZE) {
               bundleList = new ArrayList<>();
               this.bundles.add(bundleList);
            }

            bundleList.add(packet);
         }
      }
   }

   public void clear() {
      this.bundles.clear();
   }

   public void bundle(Consumer<Object> send) {
      for (List<Packet<? super ClientGamePacketListener>> list : this.bundles) {
         ProtectedPacket wrapped = new ProtectedPacket(new ClientboundBundlePacket(list));
         send.accept(wrapped);
      }
   }

   static {
      ModelEngineAPI.getAPI().getConfigManager().registerReferenceUpdate(() -> BUNDLE_SIZE = Math.min(ConfigProperty.BUNDLE_SIZE.getInt(), 4096));
   }
}
