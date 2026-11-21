package it.ytnoos.lpx.check.checks.tab;

import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientTabComplete;
import it.ytnoos.lpx.api.check.CheckResult;
import it.ytnoos.lpx.api.check.FlagInfo;
import it.ytnoos.lpx.api.check.options.CheckOptions;
import it.ytnoos.lpx.api.player.ProtocolPlayer;
import it.ytnoos.lpx.common.check.PlayerCheck;
import it.ytnoos.lpx.utils.string.StringUtils;
import java.util.List;
import java.util.function.Predicate;

public class TabA extends PlayerCheck<WrapperPlayClientTabComplete> {
   private static final String[] DEFAULT_STARTS = new String[]{"/to ", "//to", "/calc ", "//calc"};
   private static final String[] DEFAULT_CONTAINS = new String[]{"targetoffset", "for(", "^(.", "*."};
   private List<String> starts;
   private List<String> contains;

   public TabA(ProtocolPlayer protocolPlayer, CheckOptions checkOptions) {
      super(protocolPlayer, checkOptions);
   }

   @Override
   public void loadOptions() {
      this.starts = this.getStringList("starts", DEFAULT_STARTS);
      this.contains = this.getStringList("contains", DEFAULT_CONTAINS);
   }

   protected CheckResult check(ProtocolPlayer protocolPlayer, WrapperPlayClientTabComplete wrapperPlayClientTabComplete) {
      String string = wrapperPlayClientTabComplete.getText();
      boolean flag = this.findWord(this.starts, object -> StringUtils.startsWithIgnoreCase(string, (CharSequence)object));
      if (!flag) {
         flag = this.findWord(this.contains, object2 -> StringUtils.containsIgnoreCase(string, (CharSequence)object2));
      }

      if (flag) {
         this.raiseViolation(wrapperPlayClientTabComplete, new FlagInfo[]{FlagInfo.of("text", string)});
         return CheckResult.DENY;
      } else {
         return CheckResult.ALLOW;
      }
   }

   private boolean findWord(List list, Predicate predicate) {
      for (Object var9_o : list) { String string = (String) var9_o;
         if (predicate.test(string)) {
            return true;
         }
      }

      return false;
   }

}
