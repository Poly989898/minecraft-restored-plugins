package io.lumine.mythic.bukkit.commands.utility;

import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.commands.CommandHelper;
import io.lumine.mythic.bukkit.utils.commands.Command;
import io.lumine.mythic.bukkit.utils.serialize.Chroma;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.bukkit.DyeColor;
import org.bukkit.command.CommandSender;

public class GetOptimalDyesCommand extends Command<MythicBukkit> {
   public GetOptimalDyesCommand(Command<MythicBukkit> parent) {
      super(parent);
   }

   @Override
   public boolean onCommand(CommandSender sender, String[] args) {
      Chroma in = Chroma.of(args[0]);
      Chroma out = Chroma.of(args[1]);
      int diff;
      if (args.length > 2) {
         diff = Integer.parseInt(args[2]);
      } else {
         diff = 3;
      }

      double threshold;
      if (args.length > 3) {
         threshold = Double.parseDouble(args[3]);
      } else {
         threshold = 0.9;
      }

      long start = System.currentTimeMillis();
      Map<DyeColor, Integer> needed = in.getDyesTowards(out, threshold, diff);
      long end = System.currentTimeMillis();
      CommandHelper.sendEditorMessage(sender, "Dyes needed:");

      for (Entry<DyeColor, Integer> entry : needed.entrySet()) {
         CommandHelper.send(sender, entry.getKey().toString() + " - " + entry.getValue());
      }

      CommandHelper.send(sender, "Took " + (end - start) + "ms");
      return true;
   }

   @Override
   public List<String> onTabComplete(CommandSender sender, String[] args) {
      return null;
   }

   @Override
   public String getPermissionNode() {
      return "mythicmobs.command.utilities.getoptimaldyes";
   }

   @Override
   public boolean isConsoleFriendly() {
      return true;
   }

   @Override
   public String getName() {
      return "getoptimaldyes";
   }

   @Override
   public String[] getAliases() {
      return new String[]{"god"};
   }
}
