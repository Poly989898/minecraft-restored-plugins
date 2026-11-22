package com.Zrips.CMI.Modules.Packets;

import com.Zrips.CMI.CMI;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import org.bukkit.entity.Player;

public class PacketInjector {
  public void addPlayer(Player player) {
    try {
      if (!CMI.getInstance().isEnabled()) {
        return;
      }

      Channel channelValue = this.getChannel(player);
      if (channelValue == null) {
        return;
      }

      if (channelValue.pipeline().get("CMIPacketInjector") == null) {
        PacketHandler packetHandler = new PacketHandler(player);
        if (!CMI.getInstance().isEnabled()) {
          return;
        }

        try {
          channelValue.pipeline().addBefore("packet_handler", "CMIPacketInjector", packetHandler);
        } catch (Throwable throwable) {
          throwable.printStackTrace();
        }
      } else {
        PacketHandler packetHandlerValue = new PacketHandler(player);
        ChannelHandler channelHandler = channelValue.pipeline().get("CMIPacketInjector");
        channelValue.pipeline().replace(channelHandler, "CMIPacketInjector", packetHandlerValue);
      }
    } catch (Throwable throwable2) {
      throwable2.printStackTrace();
    }
  }

  private Channel getChannel(Player player) {
    if (!CMI.getInstance().isEnabled()) {
      return null;
    }

    return CMI.getInstance().getNMS().getPlayerChannel(player);
  }

  public void removePlayer(Player player) {
    if (player != null) {
      try {
        if (CMI.getInstance().isEnabled()) {
          Channel channelValue = this.getChannel(player);
          if (channelValue != null
              && channelValue.pipeline().get("CMIPacketInjector") != null
              && CMI.getInstance().isEnabled()) {
            channelValue.pipeline().remove("CMIPacketInjector");
          }
        }
      } catch (Throwable throwable) {
        throwable.printStackTrace();
      }
    }
  }
}
