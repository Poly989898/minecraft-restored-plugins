package it.ytnoos.lpx;

import com.github.retrooper.packetevents.protocol.packettype.PacketType.Play.Client;
import it.ytnoos.lpx.api.check.options.CheckOptions;
import it.ytnoos.lpx.api.player.ProtocolPlayer;

public class SetTestBlockCheck extends CreativeOnlyPacketCheck {
   public SetTestBlockCheck(ProtocolPlayer protocolPlayer, CheckOptions checkOptions) {
      super(protocolPlayer, checkOptions, Client.SET_TEST_BLOCK);
   }
}
