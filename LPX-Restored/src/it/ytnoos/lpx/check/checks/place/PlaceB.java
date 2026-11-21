package it.ytnoos.lpx.check.checks.place;

import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientUpdateSign;
import it.ytnoos.lpx.api.check.CheckResult;
import it.ytnoos.lpx.api.check.FlagInfo;
import it.ytnoos.lpx.api.check.options.CheckOptions;
import it.ytnoos.lpx.api.player.ProtocolPlayer;
import it.ytnoos.lpx.common.check.PlayerCheck;
import java.nio.charset.StandardCharsets;

public class PlaceB extends PlayerCheck<WrapperPlayClientUpdateSign> {
   private double max;

   public PlaceB(ProtocolPlayer protocolPlayer, CheckOptions checkOptions) {
      super(protocolPlayer, checkOptions);
   }

   @Override
   public void loadOptions() {
      this.max = this.getDouble("max", 70.0);
   }

   public CheckResult check(ProtocolPlayer protocolPlayer, WrapperPlayClientUpdateSign wrapperPlayClientUpdateSign) {
      for (String string : wrapperPlayClientUpdateSign.getTextLines()) {
         long longValue = string.getBytes(StandardCharsets.UTF_8).length;
         if (longValue > this.max) {
            this.raiseViolation(wrapperPlayClientUpdateSign, new FlagInfo[]{FlagInfo.of("line", string), FlagInfo.of("line-size", longValue)});
            return CheckResult.DENY;
         }
      }

      return CheckResult.ALLOW;
   }

}
