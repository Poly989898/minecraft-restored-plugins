package it.ytnoos.lpx.check.checks.command;

import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import it.ytnoos.lpx.api.check.CheckResult;
import it.ytnoos.lpx.api.check.FlagInfo;
import it.ytnoos.lpx.api.check.options.CheckOptions;
import it.ytnoos.lpx.api.player.ProtocolPlayer;
import it.ytnoos.lpx.common.check.PlayerCheck;
import it.ytnoos.lpx.utils.string.StringUtils;
import java.util.List;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class CommandA extends PlayerCheck<PlayerCommandPreprocessEvent> {
   private static final String[] DEFAULT_COMMANDS = new String[]{
      "//calc",
      "//calculate",
      "//eval",
      "//evaluate",
      "//solve",
      "/hd readtext",
      "/holo readtext",
      "/hologram readtext",
      "/holograms readtext",
      "/holographicdisplays readtext",
      "/pex promote",
      "/pex demote",
      "/promote",
      "/demote",
      "/execute",
      "/mv ^",
      "/mv help ^",
      "/mvhelp ^",
      "/mv <",
      "/mv help <",
      "/mvhelp <",
      "/$"
   };
   private List<String> commands;

   public CommandA(ProtocolPlayer protocolPlayer, CheckOptions checkOptions) {
      super(protocolPlayer, checkOptions);
   }

   @Override
   public void loadOptions() {
      this.commands = this.getStringList("commands", DEFAULT_COMMANDS);
   }

   public CheckResult check(ProtocolPlayer protocolPlayer, PlayerCommandPreprocessEvent playerCommandPreprocessEvent) {
      String string = playerCommandPreprocessEvent.getMessage();
      Object object = null;
      if (string != null && this.commands != null) {
         for (String string2 : this.commands) {
            if (StringUtils.startsWithIgnoreCase(string, string2)) {
               playerCommandPreprocessEvent.setCancelled(true);
               this.raiseViolation(playerCommandPreprocessEvent, new FlagInfo[]{FlagInfo.of("command", string)});
               return CheckResult.DENY;
            }

            if (object != null) {
               PacketWrapper[] packetWrappers = new PacketWrapper[1];
               break;
            }
         }

         return CheckResult.ALLOW;
      } else {
         return CheckResult.ALLOW;
      }
   }

}
