package net.Indyuce.mmoitems.api.crafting.trigger;

import io.lumine.mythic.lib.api.MMOLineConfig;
import net.Indyuce.mmoitems.api.player.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class CommandTrigger extends Trigger {
   private String command;
   private final String sender;

   public CommandTrigger(MMOLineConfig mmoLineConfig) {
      super("command");
      mmoLineConfig.validate(new String[]{"format"});
      this.sender = mmoLineConfig.getString("sender", "PLAYER").toUpperCase();
      this.command = mmoLineConfig.getString("format");
   }

   @Override
   public void whenCrafting(PlayerData playerData) {
      if (playerData.isOnline()) {
         this.dispatchCommand(playerData.getPlayer(), this.sender.equals("CONSOLE"), this.sender.equals("OP"));
      }
   }

   private void dispatchCommand(Player player, boolean flag, boolean flag2) {
      String text = this.command.replaceAll("(?i)%player%", player.getName());
      if (flag) {
         Bukkit.dispatchCommand(Bukkit.getConsoleSender(), text);
      } else {
         if (flag2 && !player.isOp()) {
            player.setOp(true);

            try {
               Bukkit.dispatchCommand(player, text);
            } catch (Exception exception) {
            }

            player.setOp(false);
         } else {
            Bukkit.dispatchCommand(player, text);
         }
      }
   }
}
