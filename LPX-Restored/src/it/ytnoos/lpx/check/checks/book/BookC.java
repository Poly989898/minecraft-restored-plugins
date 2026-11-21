package it.ytnoos.lpx.check.checks.book;

import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientEditBook;
import it.ytnoos.lpx.api.check.CheckResult;
import it.ytnoos.lpx.api.check.FlagInfo;
import it.ytnoos.lpx.api.check.options.CheckOptions;
import it.ytnoos.lpx.api.player.ProtocolPlayer;
import it.ytnoos.lpx.common.check.PlayerCheck;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class BookC extends PlayerCheck<WrapperPlayClientEditBook> {
   public BookC(ProtocolPlayer protocolPlayer, CheckOptions checkOptions) {
      super(protocolPlayer, checkOptions);
   }

   public CheckResult check(ProtocolPlayer protocolPlayer, WrapperPlayClientEditBook wrapperPlayClientEditBook) {
      Player player2 = protocolPlayer.getPlayer().orElse(null);
      if (player2 == null) {
         return CheckResult.ALLOW;
      } else {
         ItemStack itemStack = player2.getInventory().getItemInHand();
         if (itemStack == null || !itemStack.getType().name().contains("QUILL") && !itemStack.getType().name().contains("WRITABLE")) {
            this.raiseViolation(wrapperPlayClientEditBook, new FlagInfo[]{FlagInfo.of("hand", itemStack == null ? "null" : itemStack.getType().name())});
            return CheckResult.DENY;
         } else {
            return CheckResult.ALLOW;
         }
      }
   }

   @Override
   public boolean isPreVia() {
      return true;
   }

}
