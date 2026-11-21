package io.lumine.mythic.bukkit.commands.utility;

import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.utils.commands.Command;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntitySnapshot;
import org.bukkit.entity.Player;

public class TestEntityNBTCommand extends Command<MythicBukkit> {
   public TestEntityNBTCommand(Command<MythicBukkit> parent) {
      super(parent);
   }

   @Override
   public boolean onCommand(CommandSender sender, String[] args) {
      Player p = (Player)sender;
      String entityString = "\n{id:\"minecraft:skeleton\",BukkitValues:{\"mythicmobs:type\":\"TestingDummy\"}}\n\n\n";
      EntitySnapshot snapshot = Bukkit.getEntityFactory()
         .createEntitySnapshot("\n{id:\"minecraft:skeleton\",BukkitValues:{\"mythicmobs:type\":\"TestingDummy\"}}\n\n\n");
      snapshot.createEntity(p.getLocation());
      return true;
   }

   @Override
   public List<String> onTabComplete(CommandSender sender, String[] args) {
      return null;
   }

   @Override
   public String getPermissionNode() {
      return "mythicmobs.command.utilities.addentitynbt";
   }

   @Override
   public boolean isConsoleFriendly() {
      return false;
   }

   @Override
   public String getName() {
      return "testspawn";
   }
}
