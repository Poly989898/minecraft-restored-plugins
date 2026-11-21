package it.ytnoos.lpx.listeners;

import com.github.retrooper.packetevents.event.PacketListenerAbstract;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.event.UserDisconnectEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType.Login.Server;
import com.github.retrooper.packetevents.protocol.player.User;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelPipeline;
import it.ytnoos.lpx.LPXBukkit;
import it.ytnoos.lpx.api.player.ProtocolPlayer;
import it.ytnoos.lpx.api.protocol.PlayerRegistry;
import it.ytnoos.lpx.player.BukkitProtocolPlayer;
import java.util.UUID;

public class InjectionListener extends PacketListenerAbstract {
   private static final String HANDLER_NAME = "lpx-handler";
   private final LPXBukkit lpxBukkit;
   private final PlayerRegistry registry;

   public InjectionListener(LPXBukkit lpxBukkit2, PlayerRegistry playerRegistry) {
      this.lpxBukkit = lpxBukkit2;
      this.registry = playerRegistry;
   }

   public void onUserDisconnect(UserDisconnectEvent userDisconnectEvent) {
      this.registry.uninject(userDisconnectEvent.getUser());
   }

   public void onPacketSend(PacketSendEvent packetSendEvent) {
      if (packetSendEvent.getPacketType() == Server.LOGIN_SUCCESS) {
         packetSendEvent.getTasksAfterSend().add(() -> this.setup(packetSendEvent.getUser(), (Channel)packetSendEvent.getChannel()));
      }
   }

   private void setup(User user, Channel channel) {
      UUID uuid = user.getUUID();
      this.lpxBukkit.debug(() -> Thread.currentThread().getName() + " Setting up player with uuid " + uuid);
      BukkitProtocolPlayer bukkitProtocolPlayer = new BukkitProtocolPlayer(this.lpxBukkit, user);
      ProtocolPlayer protocolPlayer = this.registry.addPlayer(user, bukkitProtocolPlayer);
      if (protocolPlayer != null) {
         this.lpxBukkit.getLogger().warning("Player " + user.getName() + " already injected, removed old instance.");
      }

      if (this.lpxBukkit.getConfigManager().getOptionsConfig().isGeyser() && uuid.getMostSignificantBits() == 0L) {
         bukkitProtocolPlayer.setBedrock(true);
      }

      this.inject(channel, user, bukkitProtocolPlayer);
   }

   public void inject(Channel channel, User user, ProtocolPlayer protocolPlayer) {
      this.lpxBukkit.debug(() -> Thread.currentThread().getName() + " Injecting player: " + user.getName() + " " + user.getUUID());
      ChannelPipeline channelPipeline = channel.pipeline();
      Object object = channelPipeline.get("lpx-handler");
      if (object != null) {
         channelPipeline.remove((ChannelHandler)object);
      } else {
         object = new PacketDecoder(this.lpxBukkit, protocolPlayer);
      }

      channelPipeline.addAfter("decoder", "lpx-handler", (ChannelHandler)object);
      this.lpxBukkit.debug(() -> user.getName() + " : " + channelPipeline.toMap());
      this.lpxBukkit.getPacketTickMonitor().addPlayer(protocolPlayer.getUniqueId(), Thread.currentThread(), channel.eventLoop());
   }

}
