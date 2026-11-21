package it.ytnoos.lpx.check.checks.payload;

import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPluginMessage;
import it.ytnoos.lpx.api.check.CheckResult;
import it.ytnoos.lpx.api.check.FlagInfo;
import it.ytnoos.lpx.api.check.options.CheckOptions;
import it.ytnoos.lpx.api.player.ProtocolPlayer;
import it.ytnoos.lpx.common.check.PlayerCheck;

public class PayloadA extends PlayerCheck<WrapperPlayClientPluginMessage> {
   public PayloadA(ProtocolPlayer protocolPlayer, CheckOptions checkOptions) {
      super(protocolPlayer, checkOptions);
   }

   public CheckResult check(ProtocolPlayer protocolPlayer, WrapperPlayClientPluginMessage wrapperPlayClientPluginMessage) {
      if (wrapperPlayClientPluginMessage.getChannelName() != null && !wrapperPlayClientPluginMessage.getChannelName().isEmpty()) {
         return CheckResult.ALLOW;
      }

      this.raiseViolation(wrapperPlayClientPluginMessage, new FlagInfo[0]);
      return CheckResult.DENY;
   }

}
