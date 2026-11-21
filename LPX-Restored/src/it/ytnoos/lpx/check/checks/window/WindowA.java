package it.ytnoos.lpx.check.checks.window;

import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientClickWindow;
import it.ytnoos.lpx.api.check.CheckResult;
import it.ytnoos.lpx.api.check.FlagInfo;
import it.ytnoos.lpx.api.check.options.CheckOptions;
import it.ytnoos.lpx.api.player.ProtocolPlayer;
import it.ytnoos.lpx.common.check.PlayerCheck;
import net.minecraft.server.v1_8_R3.Container;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryView;

public class WindowA extends PlayerCheck<WrapperPlayClientClickWindow> {
   public WindowA(ProtocolPlayer protocolPlayer, CheckOptions checkOptions) {
      super(protocolPlayer, checkOptions);
   }

   public CheckResult check(ProtocolPlayer protocolPlayer, WrapperPlayClientClickWindow wrapperPlayClientClickWindow) {
      Player player2 = protocolPlayer.getPlayer().orElse(null);
      if (player2 == null) {
         return CheckResult.ALLOW;
      } else {
         Container container = ((CraftPlayer)player2).getHandle().activeContainer;
         if (container == null) {
            return CheckResult.ALLOW;
         } else {
            int value = wrapperPlayClientClickWindow.getSlot();
            int value2 = container.c.size();
            if (value < value2 && (value >= 0 || value == -999 || value == -1)) {
               this.decayBuffer();
               return CheckResult.ALLOW;
            } else {
               InventoryView inventoryView = container.getBukkitView();
               this.incrementBuffer(
                  wrapperPlayClientClickWindow,
                  new FlagInfo[]{
                     FlagInfo.of("slot", value),
                     FlagInfo.of("size", value2),
                     FlagInfo.of("inventory", inventoryView.getType().name()),
                     FlagInfo.of("top-inventory", inventoryView.getTopInventory().getType().name()),
                     FlagInfo.of("bottom-inventory", inventoryView.getBottomInventory().getType().name())
                  }
               );
               return CheckResult.DENY;
            }
         }
      }
   }

}
