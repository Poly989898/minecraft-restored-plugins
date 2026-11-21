package com.ticxo.modelengine.core.command.sub;

import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.command.AbstractCommand;
import java.util.List;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ResyncCommand extends AbstractCommand {
   public ResyncCommand(AbstractCommand parent) {
      super(parent);
   }

   @Override
   public boolean onCommand(CommandSender sender, String[] args) {
      if (sender instanceof Player player) {
         ModelEngineAPI.getNetworkHandler().getPipeline(player.getUniqueId()).ifPresent(wrapper -> {
            if (args.length >= 1) {
               wrapper.setDelay(Long.parseLong(args[0]));
            } else {
               wrapper.getDesyncMonitor().startTest();
            }
         });
      }

      return true;
   }

   @Override
   public List<String> onTabComplete(CommandSender sender, String[] args) {
      return null;
   }

   @Override
   public String getPermissionNode() {
      return "modelengine.command.resync";
   }

   @Override
   public boolean isConsoleFriendly() {
      return true;
   }

   @Override
   public String getName() {
      return "resync";
   }
}
