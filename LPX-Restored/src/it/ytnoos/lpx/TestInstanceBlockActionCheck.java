package it.ytnoos.lpx;

import com.github.retrooper.packetevents.protocol.packettype.PacketType.Play.Client;
import it.ytnoos.lpx.api.check.options.CheckOptions;
import it.ytnoos.lpx.api.player.ProtocolPlayer;

public class TestInstanceBlockActionCheck extends CreativeOnlyPacketCheck {
   public TestInstanceBlockActionCheck(ProtocolPlayer protocolPlayer, CheckOptions checkOptions) {
      super(protocolPlayer, checkOptions, Client.TEST_INSTANCE_BLOCK_ACTION);
   }
}
