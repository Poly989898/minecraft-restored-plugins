package litebans;

import org.bukkit.entity.Player;

class LiteRunnableImpl3 implements Runnable {
   final Player player;
   final String kickReason;
   final LiteLiteSenderImpl liteLiteSender;

   LiteRunnableImpl3(LiteLiteSenderImpl liteLiteSender, Player player, String text) {
      this.player = player;
      this.kickReason = text;
      this.liteLiteSender = liteLiteSender;
   }

   @Override
   public void run() {
      this.player.kickPlayer(this.kickReason);
   }
}
