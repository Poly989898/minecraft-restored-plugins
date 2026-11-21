package it.ytnoos.lpx.check.checks.window;

import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientClickWindow;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientClickWindow.WindowClickType;
import it.ytnoos.lpx.api.adapter.InventoryViewAdapter;
import it.ytnoos.lpx.api.check.CheckResult;
import it.ytnoos.lpx.api.check.FlagInfo;
import it.ytnoos.lpx.api.check.options.CheckOptions;
import it.ytnoos.lpx.api.player.ProtocolPlayer;
import it.ytnoos.lpx.common.check.PlayerCheck;
import org.bukkit.entity.Player;

public class WindowB extends PlayerCheck<WrapperPlayClientClickWindow> {
   private boolean cancelOnNewVersions;
   private ServerVersion serverVersion;
   private int newVersionMaxVl;

   public WindowB(ProtocolPlayer protocolPlayer, CheckOptions checkOptions) {
      super(protocolPlayer, checkOptions);
   }

   @Override
   public void loadOptions() {
      this.cancelOnNewVersions = this.getBoolean("cancel-on-new-versions", false);
      this.serverVersion = ServerVersion.V_1_21_5;
      this.newVersionMaxVl = Math.max(1, this.getInt("new-version-max-vl", 8));
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
            int value = wrapperPlayClientClickWindow.getSlot();
            int value2 = wrapperPlayClientClickWindow.getButton();
            WindowClickType windowClickType = wrapperPlayClientClickWindow.getWindowClickType();
            int value3 = inventoryViewAdapter.countSlots();
            if (value >= value3 || windowClickType == WindowClickType.SWAP && (value < 0 || value2 < 0)) {
               boolean flag = this.incrementBuffer(
                  wrapperPlayClientClickWindow,
                  new FlagInfo[]{
                     FlagInfo.of("action-number", wrapperPlayClientClickWindow.getActionNumber().orElse(null)),
                     FlagInfo.of("action", windowClickType.name()),
                     FlagInfo.of("button", wrapperPlayClientClickWindow.getButton()),
                     FlagInfo.of("slot", value),
                     FlagInfo.of("converted-slot", inventoryViewAdapter.convertSlot(value)),
                     FlagInfo.of("size", value3),
                     FlagInfo.of("inventory", inventoryViewAdapter.getType()),
                     FlagInfo.of("top-inventory", inventoryViewAdapter.getTopInventory().getType().name()),
                     FlagInfo.of("bottom-inventory", inventoryViewAdapter.getBottomInventory().getType().name())
                  }
               );
               return this.shouldCancel(protocolPlayer) && !flag ? CheckResult.ALLOW : CheckResult.DENY;
            } else {
               this.decayBuffer();
               return CheckResult.ALLOW;
            }
         }
      }
   }

   private boolean shouldCancel(ProtocolPlayer protocolPlayer) {
      if (!this.cancelOnNewVersions) {
         return false;
      }

      ServerVersion serverVersion2 = protocolPlayer.getUser().getClientVersion().toServerVersion();
      return serverVersion2 == null ? false : serverVersion2.isNewerThanOrEquals(this.serverVersion) && this.getOptions().getVl() < this.newVersionMaxVl;
   }

}
