package io.lumine.mythic.bukkit.commands.pins;

import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.commands.CommandHelper;
import io.lumine.mythic.bukkit.utils.commands.Command;
import io.lumine.mythic.bukkit.utils.text.Text;
import io.lumine.mythic.core.skills.pins.PinRegion;
import java.util.Collections;
import java.util.List;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ListRegionsCommand extends Command<MythicBukkit> {
   public ListRegionsCommand(Command<MythicBukkit> parent) {
      super(parent);
   }

   @Override
   public boolean onCommand(CommandSender sender, String[] args) {
      Player player = (Player)sender;
      CommandHelper.sendEditorMessage(sender, "<gold><bold>Regions At This Location:");
      long startTime = System.nanoTime();
      List<PinRegion> regions = this.getPlugin().getPinManager().getRegions(player.getLocation());
      long endTime = System.nanoTime();

      for (PinRegion region : regions) {
         StringBuilder message = new StringBuilder();
         message.append("<red><click:run_command:/mm p removeregion " + region.getPack().getKey() + " " + region.getPropertyNode() + ">\u2715</click> ");
         message.append(
            "<white>Region <yellow><click:copy_to_clipboard:"
               + region.getPropertyNode()
               + ">"
               + region.getPropertyNode()
               + "</click> - <aqua>"
               + region.getRegion().getMin().toLocus().serializeShortForm()
               + " to "
               + region.getRegion().getMax().toLocus().serializeShortForm()
         );
         Text.sendMessage(sender, message.toString());
      }

      CommandHelper.send(player, "<gray>");
      CommandHelper.send(player, "<gray><italic>Region Lookup took " + (endTime - startTime) + "ns");
      CommandHelper.sendFooter(sender);
      return true;
   }

   @Override
   public List<String> onTabComplete(CommandSender sender, String[] args) {
      return Collections.emptyList();
   }

   @Override
   public String getPermissionNode() {
      return "mythicmobs.command.pins.list";
   }

   @Override
   public boolean isConsoleFriendly() {
      return false;
   }

   @Override
   public String getName() {
      return "listregions";
   }
}
