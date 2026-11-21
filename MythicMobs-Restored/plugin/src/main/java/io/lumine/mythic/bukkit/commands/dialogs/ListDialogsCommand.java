package io.lumine.mythic.bukkit.commands.dialogs;

import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.commands.CommandHelper;
import io.lumine.mythic.bukkit.utils.commands.Command;
import io.lumine.mythic.bukkit.utils.text.Text;
import io.lumine.mythic.core.dialogs.DialogDefinition;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;

public class ListDialogsCommand extends Command<MythicBukkit> {
   public ListDialogsCommand(Command<MythicBukkit> parent) {
      super(parent);
   }

   @Override
   public boolean onCommand(CommandSender sender, String[] args) {
      Collection<String> dialogNames = this.getPlugin().getDialogManager().getDialogNames();
      if (dialogNames.isEmpty()) {
         CommandHelper.sendError(sender, "No dialog definitions loaded.");
         return true;
      } else {
         ArrayList<Component> messages = new ArrayList<>();
         messages.add(Text.parse("&eLoaded Dialogs &7(&f" + dialogNames.size() + "&7)"));

         for (String name : dialogNames) {
            DialogDefinition def = this.getPlugin().getDialogManager().getDialog(name).orElse(null);
            if (def != null) {
               int buttonCount = def.getButtons().size();
               int inputCount = def.getInputs().size();
               messages.add(Text.parse("&7 - &f" + name + " &7[&a" + buttonCount + " btn&7, &b" + inputCount + " input&7]"));
            }
         }

         CommandHelper.sendCommandMessage(sender, messages.toArray(new Component[0]));
         return true;
      }
   }

   @Override
   public List<String> onTabComplete(CommandSender sender, String[] args) {
      return null;
   }

   @Override
   public String getPermissionNode() {
      return "mythicmobs.command.dialogs.list";
   }

   @Override
   public boolean isConsoleFriendly() {
      return true;
   }

   @Override
   public String getName() {
      return "list";
   }

   @Override
   public String[] getAliases() {
      return new String[]{"l"};
   }
}
