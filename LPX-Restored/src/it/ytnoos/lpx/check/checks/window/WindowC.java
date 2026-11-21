package it.ytnoos.lpx.check.checks.window;

import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientClickWindow;
import it.ytnoos.lpx.api.check.CheckResult;
import it.ytnoos.lpx.api.check.FlagInfo;
import it.ytnoos.lpx.api.check.options.CheckOptions;
import it.ytnoos.lpx.api.player.ProtocolPlayer;
import it.ytnoos.lpx.common.check.PlayerCheck;
import org.bukkit.entity.Player;

public class WindowC extends PlayerCheck<WrapperPlayClientClickWindow> {
   private static final String SLOT = "slot";

   public WindowC(ProtocolPlayer protocolPlayer, CheckOptions checkOptions) {
      super(protocolPlayer, checkOptions);
   }

   public CheckResult check(ProtocolPlayer protocolPlayer, WrapperPlayClientClickWindow wrapperPlayClientClickWindow) {
      Player player2 = protocolPlayer.getPlayer().orElse(null);
      if (player2 == null) {
         return CheckResult.ALLOW;
      } else if (protocolPlayer.lpx().getInventoryViewAdapter(player2).isNull()) {
         this.raiseViolation(wrapperPlayClientClickWindow, new FlagInfo[]{FlagInfo.of(SLOT, wrapperPlayClientClickWindow.getSlot())});
         return CheckResult.DENY;
      } else {
         return CheckResult.ALLOW;
      }
   }

}
