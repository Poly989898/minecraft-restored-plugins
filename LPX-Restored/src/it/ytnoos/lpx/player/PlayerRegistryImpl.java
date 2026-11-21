package it.ytnoos.lpx.player;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.player.User;
import io.netty.channel.Channel;
import io.netty.channel.EventLoop;
import it.ytnoos.lpx.LPXBukkit;
import it.ytnoos.lpx.api.player.ProtocolPlayer;
import it.ytnoos.lpx.api.protocol.PlayerRegistry;
import it.ytnoos.lpx.listeners.InjectionListener;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public class PlayerRegistryImpl implements PlayerRegistry {
   private final LPXBukkit lpxBukkit;
   private final Map<User, ProtocolPlayer> players = new ConcurrentHashMap<>();

   public PlayerRegistryImpl(LPXBukkit lpxBukkit2) {
      this.lpxBukkit = lpxBukkit2;
      PacketEvents.getAPI().getEventManager().registerListener(new InjectionListener(lpxBukkit2, this));
   }

   @Override
   public void uninjectAll() {
      for (User user : new HashSet<>(this.players.keySet())) {
         this.uninject(user);
      }
   }

   @Override
   public void uninject(User user) {
      ProtocolPlayer protocolPlayer = this.players.remove(user);
      if (protocolPlayer != null) {
         UUID uuid = user.getUUID();
         if (uuid != null) {
            this.lpxBukkit.getAlertManager().enableAlerts(uuid);
            Object object = user.getChannel();
            if (object != null) {
               EventLoop eventLoop = ((Channel)object).eventLoop();
               if (eventLoop != null) {
                  eventLoop.execute(() -> this.lpxBukkit.getPacketTickMonitor().removePlayer(uuid, Thread.currentThread(), eventLoop));
               }
            }
         }
      }
   }

   @Override
   public Optional<ProtocolPlayer> getPlayer(Player player) {
      User user = PacketEvents.getAPI().getPlayerManager().getUser(player);
      return user == null ? Optional.empty() : this.getPlayer(user);
   }

   @Override
   public Optional<ProtocolPlayer> getPlayer(User user) {
      return Optional.ofNullable(this.players.get(user));
   }

   @Override
   public Map<User, ProtocolPlayer> getPlayers() {
      return Collections.unmodifiableMap(this.players);
   }

   @Nullable
   @Override
   public ProtocolPlayer addPlayer(User user, ProtocolPlayer protocolPlayer) {
      return this.players.put(user, protocolPlayer);
   }

}
