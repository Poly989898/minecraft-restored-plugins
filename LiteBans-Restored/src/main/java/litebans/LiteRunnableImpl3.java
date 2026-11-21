package litebans;

import org.bukkit.entity.Player;

class LiteRunnableImpl3 implements Runnable {
   final Player player;
   final String text2;
   final LiteLiteSenderImpl liteLiteSender;

   LiteRunnableImpl3(LiteLiteSenderImpl liteLiteSender, Player player, String text) {
      this.player = player;
      this.text2 = text;
      this.liteLiteSender = liteLiteSender;
   }

   @Override
   public void run() {
      this.player.kickPlayer(this.text2);
   }
}
