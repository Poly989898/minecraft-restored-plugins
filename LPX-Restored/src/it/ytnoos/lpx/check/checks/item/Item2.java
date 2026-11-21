package it.ytnoos.lpx.check.checks.item;

import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPickItemFromBlock;
import it.ytnoos.lpx.api.check.CheckResult;
import it.ytnoos.lpx.api.check.options.CheckOptions;
import it.ytnoos.lpx.api.player.ProtocolPlayer;
import it.ytnoos.lpx.common.check.PlayerCheck;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

public class Item2 extends PlayerCheck<WrapperPlayClientPickItemFromBlock> {
   private DisableDecodingType disableDecodingType;
   private static final String DISABLE_DECODING_PATH = "options.disable-item-decoding";

   public Item2(ProtocolPlayer protocolPlayer, CheckOptions checkOptions) {
      super(protocolPlayer, checkOptions);
   }

   @Override
   public void loadOptions() {
      this.disableDecodingType = DisableDecodingType.CD(
         this.getOptions().getCheckOptions().getConfig().getString(DISABLE_DECODING_PATH, DisableDecodingType.DEFAULT.name())
      );
   }

   public CheckResult check(ProtocolPlayer protocolPlayer, WrapperPlayClientPickItemFromBlock wrapperPlayClientPickItemFromBlock) {
      Player player2 = protocolPlayer.getPlayer().orElse(null);
      if (player2 == null) {
         return CheckResult.ALLOW;
      } else if (this.disableDecodingType == DisableDecodingType.CREATIVE && player2.getGameMode() == GameMode.CREATIVE) {
         wrapperPlayClientPickItemFromBlock.setIncludeData(false);
         return CheckResult.CHANGE;
      } else {
         return CheckResult.ALLOW;
      }
   }

   @Override
   public boolean isPreVia() {
      return true;
   }

}
