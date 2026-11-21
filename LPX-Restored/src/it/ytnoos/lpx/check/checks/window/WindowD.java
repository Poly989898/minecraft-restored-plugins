package it.ytnoos.lpx.check.checks.window;

import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientClickWindow;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientClickWindow.WindowClickType;
import it.ytnoos.lpx.api.adapter.InventoryViewAdapter;
import it.ytnoos.lpx.api.check.CheckResult;
import it.ytnoos.lpx.api.check.FlagInfo;
import it.ytnoos.lpx.api.check.options.CheckOptions;
import it.ytnoos.lpx.api.player.ProtocolPlayer;
import it.ytnoos.lpx.common.check.PlayerCheck;
import org.bukkit.entity.Player;

public class WindowD extends PlayerCheck<WrapperPlayClientClickWindow> {
   public WindowD(ProtocolPlayer protocolPlayer, CheckOptions checkOptions) {
      super(protocolPlayer, checkOptions);
   }

   public CheckResult check(ProtocolPlayer protocolPlayer, WrapperPlayClientClickWindow wrapperPlayClientClickWindow) {
      Player player2 = protocolPlayer.getPlayer().orElse(null);
      if (player2 == null) {
         return CheckResult.ALLOW;
      } else {
         InventoryViewAdapter inventoryViewAdapter = protocolPlayer.lpx().getInventoryViewAdapter(player2);
         if (inventoryViewAdapter.isNull()) {
            return CheckResult.ALLOW;
         } else {
            String string = inventoryViewAdapter.getType();
            WindowClickType windowClickType = wrapperPlayClientClickWindow.getWindowClickType();
            if (string.contains("LECTERN") && windowClickType == WindowClickType.QUICK_MOVE) {
               this.raiseViolation(wrapperPlayClientClickWindow, new FlagInfo[]{FlagInfo.of("inventory", string), FlagInfo.of("click-type", windowClickType.name())});
               return CheckResult.DENY;
            } else {
               return CheckResult.ALLOW;
            }
         }
      }
   }

}
