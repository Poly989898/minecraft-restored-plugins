package io.lumine.mythic.lib.comp.protocollib;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.PacketType.Play.Server;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedParticle;
import io.lumine.mythic.lib.MythicLib;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

public class DamageParticleCap {
   private final Map<UUID, Integer> particles = new HashMap<>();
   private final int tickLimit;

   public DamageParticleCap(int tickLimit) {
      this.tickLimit = tickLimit;
      ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(MythicLib.plugin, Server.WORLD_PARTICLES) {
         public void onPacketSending(PacketEvent event) {
            DamageParticleCap.this.onSend(event);
         }
      });
   }

   private void onSend(PacketEvent event) {
      PacketContainer packet = event.getPacket();
      Player player = event.getPlayer();
      if (((WrappedParticle)packet.getNewParticles().read(0)).getParticle() == Particle.DAMAGE_INDICATOR) {
         int originalAmount = (Integer)packet.getIntegers().read(0);
         int storedAmount = this.particles.getOrDefault(player.getUniqueId(), 0);
         if (storedAmount < this.tickLimit) {
            int amountLeft = this.tickLimit - storedAmount;
            int amount = Math.min(amountLeft, (Integer)packet.getIntegers().read(0));
            if (amount <= 0) {
               event.setCancelled(true);
            } else {
               this.particles.put(player.getUniqueId(), storedAmount + amount);
               Bukkit.getScheduler().runTask(MythicLib.plugin, () -> {
                  int current = this.particles.getOrDefault(player.getUniqueId(), 0);
                  int safeNew = Math.max(0, Math.min(this.tickLimit, current - amount));
                  this.particles.put(player.getUniqueId(), safeNew);
               });
               if (amount != originalAmount) {
                  event.getPacket().getIntegers().write(0, amount);
               }
            }
         }
      }
   }
}
