package com.ticxo.modelengine.api.nms.network;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;

public class ProtectedPacketUnpacker extends ChannelDuplexHandler {
   public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
      if (msg instanceof ProtectedPacket protectedPacket) {
         msg = protectedPacket.packet();
      }

      super.write(ctx, msg, promise);
   }
}
