package net.Indyuce.mmoitems.api.crafting.trigger;

import io.lumine.mythic.lib.api.MMOLineConfig;
import net.Indyuce.mmoitems.api.player.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class CommandTrigger extends Trigger {
   private String command;
   private final String sender;

   public CommandTrigger(MMOLineConfig var1) {
      super("command");
      var1.validate(new String[]{"format"});
      this.sender = var1.getString("sender", "PLAYER").toUpperCase();
      this.command = var1.getString("format");
   }

   @Override
   public void whenCrafting(PlayerData var1) {
      if (var1.isOnline()) {
         this.dispatchCommand(var1.getPlayer(), this.sender.equals("CONSOLE"), this.sender.equals("OP"));
      }
   }

   private void dispatchCommand(Player var1, boolean var2, boolean var3) {
      String var4 = this.command.replaceAll("(?i)%player%", var1.getName());
      if (var2) {
         Bukkit.dispatchCommand(Bukkit.getConsoleSender(), var4);
      } else {
         if (var3 && !var1.isOp()) {
            var1.setOp(true);

            try {
               Bukkit.dispatchCommand(var1, var4);
            } catch (Exception var6) {
            }

            var1.setOp(false);
         } else {
            Bukkit.dispatchCommand(var1, var4);
         }
      }
   }
}
