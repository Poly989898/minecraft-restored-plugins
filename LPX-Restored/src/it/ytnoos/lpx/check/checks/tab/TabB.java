package it.ytnoos.lpx.check.checks.tab;

import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientTabComplete;
import it.ytnoos.lpx.api.check.CheckResult;
import it.ytnoos.lpx.api.check.FlagInfo;
import it.ytnoos.lpx.api.check.options.CheckOptions;
import it.ytnoos.lpx.api.player.ProtocolPlayer;
import it.ytnoos.lpx.common.check.PlayerCheck;
import java.util.ArrayList;
import java.util.List;

public class TabB extends PlayerCheck<WrapperPlayClientTabComplete> {
   private static final String[] DEFAULT_CHARACTERS = new String[]{"[", "]", "{", "}", "@", "=", "nbt"};
   private List<String> characters = new ArrayList<>();
   private int maxLength;

   public TabB(ProtocolPlayer protocolPlayer, CheckOptions checkOptions) {
      super(protocolPlayer, checkOptions);
   }

   @Override
   public void loadOptions() {
      this.maxLength = this.getInt("max-length", 256);
      this.characters = this.getStringList("characters", DEFAULT_CHARACTERS);
   }

   public CheckResult check(ProtocolPlayer protocolPlayer, WrapperPlayClientTabComplete wrapperPlayClientTabComplete) {
      String string = wrapperPlayClientTabComplete.getText();
      byte b = 81;
      int value = string.length();
      if (value > this.maxLength) {
         return CheckResult.DENY;
      }

      for (String string2 : this.characters) {
         if (string.contains(string2)) {
            this.raiseViolation(wrapperPlayClientTabComplete, new FlagInfo[]{FlagInfo.of("text", string), FlagInfo.of("invalid-char", string2)});
            return CheckResult.DENY;
         }

         if (b == 0) {
            break;
         }
      }

      return CheckResult.ALLOW;
   }

}
