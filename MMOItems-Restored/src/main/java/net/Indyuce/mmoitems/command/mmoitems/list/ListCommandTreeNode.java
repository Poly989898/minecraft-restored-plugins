package net.Indyuce.mmoitems.command.mmoitems.list;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.command.api.CommandTreeNode;
import io.lumine.mythic.lib.command.api.CommandTreeNode.CommandResult;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ListCommandTreeNode extends CommandTreeNode {
   public ListCommandTreeNode(CommandTreeNode var1) {
      super(var1, "list");
      this.addChild(new AbilityCommandTreeNode(this));
      this.addChild(new LuteAttackCommandTreeNode(this));
      this.addChild(new TypeCommandTreeNode(this));
   }

   public CommandResult execute(CommandSender var1, String[] var2) {
      var1.sendMessage(
         ChatColor.DARK_GRAY
            + ""
            + ChatColor.STRIKETHROUGH
            + "-----------------["
            + ChatColor.LIGHT_PURPLE
            + " MMOItems: lists "
            + ChatColor.DARK_GRAY
            + ""
            + ChatColor.STRIKETHROUGH
            + "]-----------------"
      );
      var1.sendMessage(ChatColor.LIGHT_PURPLE + "/mi list type " + ChatColor.WHITE + "shows all item types (sword, axe...)");
      var1.sendMessage(ChatColor.LIGHT_PURPLE + "/mi list lute " + ChatColor.WHITE + "shows all available lute attack effects");
      var1.sendMessage(ChatColor.LIGHT_PURPLE + "/mi list ability " + ChatColor.WHITE + "shows all available abilities");
      if (var1 instanceof Player) {
         var1.sendMessage("");
         var1.sendMessage("Spigot Javadoc Links:");
         MythicLib.plugin
            .getVersion()
            .getWrapper()
            .sendJson(
               (Player)var1,
               "[{\"text\":\""
                  + ChatColor.UNDERLINE
                  + ChatColor.GREEN
                  + "Materials/Blocks\",\"clickEvent\":{\"action\":\"open_url\",\"value\":\"https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Material.html\"},\"hoverEvent\":{\"action\":\"show_text\",\"value\":{\"text\":\"\",\"extra\":[{\"text\":\""
                  + ChatColor.GREEN
                  + "Click to open webpage.\"}]}}},{\"text\":\" "
                  + ChatColor.LIGHT_PURPLE
                  + "- \"},{\"text\":\""
                  + ChatColor.UNDERLINE
                  + ChatColor.GREEN
                  + "Potion Effects\",\"clickEvent\":{\"action\":\"open_url\",\"value\":\"https://hub.spigotmc.org/javadocs/spigot/org/bukkit/potion/PotionEffectType.html\"},\"hoverEvent\":{\"action\":\"show_text\",\"value\":{\"text\":\"\",\"extra\":[{\"text\":\""
                  + ChatColor.GREEN
                  + "Click to open webpage.\"}]}}},{\"text\":\" "
                  + ChatColor.LIGHT_PURPLE
                  + "- \"},{\"text\":\""
                  + ChatColor.UNDERLINE
                  + ChatColor.GREEN
                  + "Sounds\",\"clickEvent\":{\"action\":\"open_url\",\"value\":\"https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Sound.html\"},\"hoverEvent\":{\"action\":\"show_text\",\"value\":{\"text\":\"\",\"extra\":[{\"text\":\""
                  + ChatColor.GREEN
                  + "Click to open webpage.\"}]}}}]"
            );
         MythicLib.plugin
            .getVersion()
            .getWrapper()
            .sendJson(
               (Player)var1,
               "[{\"text\":\""
                  + ChatColor.UNDERLINE
                  + ChatColor.GREEN
                  + "Entities/Mobs\",\"clickEvent\":{\"action\":\"open_url\",\"value\":\"https://hub.spigotmc.org/javadocs/spigot/org/bukkit/entity/EntityType.html\"},\"hoverEvent\":{\"action\":\"show_text\",\"value\":{\"text\":\"\",\"extra\":[{\"text\":\""
                  + ChatColor.GREEN
                  + "Click to open webpage.\"}]}}},{\"text\":\" "
                  + ChatColor.LIGHT_PURPLE
                  + "- \"},{\"text\":\""
                  + ChatColor.UNDERLINE
                  + ChatColor.GREEN
                  + "Enchantments\",\"clickEvent\":{\"action\":\"open_url\",\"value\":\"https://hub.spigotmc.org/javadocs/spigot/org/bukkit/enchantments/Enchantment.html\"},\"hoverEvent\":{\"action\":\"show_text\",\"value\":{\"text\":\"\",\"extra\":[{\"text\":\""
                  + ChatColor.GREEN
                  + "Click to open webpage.\"}]}}},{\"text\":\" "
                  + ChatColor.LIGHT_PURPLE
                  + "- \"},{\"text\":\""
                  + ChatColor.UNDERLINE
                  + ChatColor.GREEN
                  + "Particles\",\"clickEvent\":{\"action\":\"open_url\",\"value\":\"https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Particles.html\"},\"hoverEvent\":{\"action\":\"show_text\",\"value\":{\"text\":\"\",\"extra\":[{\"text\":\""
                  + ChatColor.GREEN
                  + "Click to open webpage.\"}]}}}]"
            );
      }

      return CommandResult.SUCCESS;
   }
}
