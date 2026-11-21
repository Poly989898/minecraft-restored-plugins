package io.lumine.mythic.core.volatilecode.v26_3;

import io.lumine.mythic.api.volatilecode.VolatileCodeHandler;
import io.lumine.mythic.api.volatilecode.handlers.ParticleBundle;
import io.lumine.mythic.api.volatilecode.handlers.VolatileParticleHandler;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.utils.Events;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBundlePacket;
import net.minecraft.network.protocol.game.ClientboundLevelParticlesPacket;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.craftbukkit.CraftParticle;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerQuitEvent;

public class VolatileParticleHandlerImpl implements VolatileParticleHandler {
   private final ConcurrentHashMap<UUID, ConcurrentLinkedQueue<Packet<? super ClientGamePacketListener>>> queues = new ConcurrentHashMap<>();

   public VolatileParticleHandlerImpl(VolatileCodeHandler handler) {
      Events.subscribe(PlayerQuitEvent.class).handler(e -> this.queues.remove(e.getPlayer().getUniqueId()));
   }

   private static boolean tickBatch() {
      return MythicBukkit.inst().getConfiguration().isParticleTickBatchEnabled();
   }

   private static int bundleCap() {
      int cap = MythicBukkit.inst().getConfiguration().getParticleBundleSize();
      return Math.min(Math.max(cap, 1), 4096);
   }

   @Override
   public void sendParticle(
      Collection<? extends Player> audience,
      Particle particle,
      Object data,
      double x,
      double y,
      double z,
      int count,
      float offsetX,
      float offsetY,
      float offsetZ,
      float speed,
      boolean force
   ) {
      if (particle != null && !audience.isEmpty()) {
         ParticleOptions opts;
         try {
            opts = CraftParticle.createParticleParam(particle, data);
         } catch (Exception var20) {
            return;
         }

         ClientboundLevelParticlesPacket pkt = new ClientboundLevelParticlesPacket(opts, force, force, x, y, z, offsetX, offsetY, offsetZ, speed, count);
         if (tickBatch()) {
            for (Player p : audience) {
               if (VolatileParticleHandler.shouldDispatchTo(p)) {
                  this.queues.computeIfAbsent(p.getUniqueId(), k -> new ConcurrentLinkedQueue<>()).add(pkt);
               }
            }
         } else {
            for (Player px : audience) {
               if (VolatileParticleHandler.shouldDispatchTo(px)) {
                  ((CraftPlayer)px).getHandle().connection.send(pkt);
               }
            }
         }
      }
   }

   @Override
   public ParticleBundle openBundle(Collection<? extends Player> audience, int bundleSize) {
      if (tickBatch()) {
         return ParticleBundle.direct(this, audience);
      } else {
         int cap = bundleSize < 1 ? 1 : Math.min(bundleSize, 4096);
         return new VolatileParticleHandlerImpl.BundleImpl(audience, cap);
      }
   }

   @Override
   public void flushTick() {
      if (!this.queues.isEmpty()) {
         int cap = bundleCap();

         for (Entry<UUID, ConcurrentLinkedQueue<Packet<? super ClientGamePacketListener>>> entry : this.queues.entrySet()) {
            ConcurrentLinkedQueue<Packet<? super ClientGamePacketListener>> queue = entry.getValue();
            if (!queue.isEmpty()) {
               Player player = Bukkit.getPlayer(entry.getKey());
               if (player == null) {
                  queue.clear();
               } else {
                  ServerGamePacketListenerImpl connection = ((CraftPlayer)player).getHandle().connection;
                  List<Packet<? super ClientGamePacketListener>> chunk = new ArrayList<>(Math.min(cap, 64));

                  Packet<? super ClientGamePacketListener> pkt;
                  while ((pkt = queue.poll()) != null) {
                     chunk.add(pkt);
                     if (chunk.size() >= cap) {
                        connection.send(new ClientboundBundlePacket(chunk));
                        chunk = new ArrayList<>(Math.min(cap, 64));
                     }
                  }

                  if (!chunk.isEmpty()) {
                     connection.send(new ClientboundBundlePacket(chunk));
                  }
               }
            }
         }
      }
   }

   private static final class BundleImpl implements ParticleBundle {
      private final Collection<? extends Player> audience;
      private final int cap;
      private List<Packet<? super ClientGamePacketListener>> buffer;

      BundleImpl(Collection<? extends Player> audience, int cap) {
         this.audience = audience;
         this.cap = cap;
      }

      @Override
      public void add(
         Particle particle, Object data, double x, double y, double z, int count, float offsetX, float offsetY, float offsetZ, float speed, boolean force
      ) {
         if (particle != null && !this.audience.isEmpty()) {
            ParticleOptions opts;
            try {
               opts = CraftParticle.createParticleParam(particle, data);
            } catch (Exception var17) {
               return;
            }

            ClientboundLevelParticlesPacket pkt = new ClientboundLevelParticlesPacket(opts, force, force, x, y, z, offsetX, offsetY, offsetZ, speed, count);
            if (this.buffer == null) {
               this.buffer = new ArrayList<>(this.cap);
            }

            this.buffer.add(pkt);
            if (this.buffer.size() >= this.cap) {
               this.flush();
            }
         }
      }

      @Override
      public void close() {
         this.flush();
      }

      private void flush() {
         if (this.buffer != null && !this.buffer.isEmpty()) {
            ClientboundBundlePacket bundle = new ClientboundBundlePacket(this.buffer);
            this.buffer = null;

            for (Player p : this.audience) {
               if (VolatileParticleHandler.shouldDispatchTo(p)) {
                  ((CraftPlayer)p).getHandle().connection.send(bundle);
               }
            }
         } else {
            this.buffer = null;
         }
      }
   }
}
