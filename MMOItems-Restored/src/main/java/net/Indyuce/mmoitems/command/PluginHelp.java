package net.Indyuce.mmoitems.command;

import io.lumine.mythic.lib.MythicLib;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PluginHelp {
   private static final int commandsPerPage = 8;
   private final CommandSender sender;

   public PluginHelp(CommandSender sender) {
      this.sender = sender;
   }

   public void open(int count) {
      if (count >= 1 && (count - 1) * 8 < PluginHelp.PluginCommand.values().length) {
         for (int i = 0; i < 10; i++) {
            this.sender.sendMessage("");
         }

         this.sender
            .sendMessage(
               ""
                  + ChatColor.DARK_GRAY
                  + ChatColor.STRIKETHROUGH
                  + "--------------------"
                  + ChatColor.DARK_GRAY
                  + "["
                  + ChatColor.LIGHT_PURPLE
                  + " MMOItems Help "
                  + ChatColor.DARK_GRAY
                  + "]"
                  + ChatColor.STRIKETHROUGH
                  + "-------------------"
            );
         if (this.sender instanceof Player) {
            int count2 = (count - 1) * 8;
            int count3 = count * 8;

            int count4;
            for (count4 = 0; count2 + count4 < count3 && count2 + count4 < PluginHelp.PluginCommand.values().length && count2 + count4 > -1; count4++) {
               PluginHelp.PluginCommand.values()[count2 + count4].sendAsJson((Player)this.sender);
            }

            while (count4++ < 8) {
               this.sender.sendMessage("");
            }

            MythicLib.plugin
               .getVersion()
               .getWrapper()
               .sendJson(
                  (Player)this.sender,
                  "[{\"text\":\""
                     + ChatColor.DARK_GRAY
                     + ChatColor.STRIKETHROUGH
                     + "------------------"
                     + ChatColor.DARK_GRAY
                     + "[\"},{\"text\":\""
                     + ChatColor.RED
                     + "««\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/mi help "
                     + (count - 1)
                     + "\"},\"hoverEvent\":{\"action\":\"show_text\",\"value\":{\"text\":\"Previous Page\"}}},{\"text\":\""
                     + ChatColor.DARK_GRAY
                     + "]"
                     + ChatColor.STRIKETHROUGH
                     + "---"
                     + ChatColor.DARK_GRAY
                     + "("
                     + ChatColor.GREEN
                     + count
                     + ChatColor.DARK_GRAY
                     + "/"
                     + ChatColor.GREEN
                     + getMaxPage()
                     + ChatColor.DARK_GRAY
                     + ")"
                     + ChatColor.STRIKETHROUGH
                     + "---"
                     + ChatColor.DARK_GRAY
                     + "[\"},{\"text\":\""
                     + ChatColor.GREEN
                     + "»»\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/mi help "
                     + (count + 1)
                     + "\"},\"hoverEvent\":{\"action\":\"show_text\",\"value\":{\"text\":\"Next Page\"}}},{\"text\":\""
                     + ChatColor.DARK_GRAY
                     + "]"
                     + ChatColor.STRIKETHROUGH
                     + "-----------------\"}]"
               );
         } else {
            for (PluginHelp.PluginCommand pluginCommand : PluginHelp.PluginCommand.values()) {
               pluginCommand.sendAsMessage(this.sender);
            }
         }
      }
   }

   public static int getMaxPage() {
      return (int)Math.max(0.0, Math.ceil(PluginHelp.PluginCommand.values().length / 8.0));
   }

   public enum PluginCommand {
      OPTIONAL("()" + ChatColor.WHITE + " = Optional"),
      REQUIRED("<>" + ChatColor.WHITE + " = Required"),
      MULTIPLE_ARGS("..." + ChatColor.WHITE + " = Multiple Args"),
      HOVER_COMMAND(ChatColor.WHITE + "Hover a command to see its description."),
      TAB_INFO(ChatColor.WHITE + "Press [tab] while typing a command to auto-complete."),
      SPACE(""),
      HELP("mi help <page>", "Shows the help page."),
      GIVE(
         "mi give <type> <item> (player) (min-max) (unident-chance) (drop-chance)",
         "&a/mi <type> <item> (player) (min-max) (unidentification-chance) (drop-chance)\\n&fGives an item to a player.\\nSupports drop chance, unidentification chance & random amounts."
      ),
      GIVEALL("mi giveall <type> <item> <min-max> <unident-chance>", "Gives an item to all online players on the server."),
      BROWSE("mi browse (type)", "Allows you to browse through all the items you created."),
      GENERATE("mi generate <player> (extra-args)", "Generates a random item.\\nUse /mi generate to see all available parameters."),
      ABILITY("mi ability <ability> (player) (mod1) (val1) (mod2) (val2)...", "Forces a player to cast an ability."),
      DROP(
         "mi drop <type> <item-id> <world-name> <x> <y> <z>...",
         "&a/mi drop <type> <item-id> <world-name> <x> <y> <z> <drop-chance> <[min]-[max]> <unidentified-chance>\\n&fDrops an item at the target location."
      ),
      HEAL("mi heal", "Heals you & remove negative potion effects."),
      IDENTIFY("mi item identifify", "Identifies the item you are holding."),
      UNIDENTIFY("mi item unidentifify", "Unidentifies the item you are holding."),
      REPAIR("mi item repair", "Repairs the item you are holding."),
      DECONSTRUCT("mi item deconstruct", "Deconstructs the item you are holding."),
      INFO("mi debug info (player)", "Displays information about the specified player."),
      LIST("mi list", "Some useful things when editing items."),
      RELOAD("mi reload (adv-recipes/stations)", "Reloads a specific/every plugin system."),
      CREATE("mi create <type> <id>", "Creates a new item."),
      COPY("mi copy <type> <target-id> <new-item-id>", "Duplicates an existing item."),
      DELETE("mi delete <type> <id>", "Removes an item from the plugin."),
      EDIT("mi edit <type> <id>", "Brings up the item edition menu."),
      ITEMLIST("mi itemlist <type>", "Lists all items from a specific item type."),
      ALLITEMS("mi allitems", "Lists items from every type."),
      STATIONS_LIST("mi stations list", "Lists available crafting stations."),
      STATIONS_OPEN("mi stations open <station> (player)", "Opens a crafting station to a player"),
      UPDATEITEM("updateitem", "Updates the item you are holding."),
      UPDATEITEM_ITEM("updateitem <type> <id>", "Enables the item updater for a specific item.");

      private final String usage;
      private final String help;

      PluginCommand(String nullxx) {
         this(nullxx, null);
      }

      PluginCommand(String nullxx, String nullxxx) {
         this.usage = nullxx;
         this.help = nullxxx == null ? null : ChatColor.translateAlternateColorCodes('&', nullxxx);
      }

      private boolean isCommand() {
         return this.help != null;
      }

      private void sendAsJson(Player player) {
         if (this.isCommand()) {
            MythicLib.plugin
               .getVersion()
               .getWrapper()
               .sendJson(
                  player,
                  "{\"text\":\""
                     + ChatColor.LIGHT_PURPLE
                     + "►"
                     + ChatColor.GRAY
                     + " /"
                     + this.usage
                     + "\",\"hoverEvent\":{\"action\":\"show_text\",\"value\":{\"text\":\""
                     + this.help
                     + "\"}}}"
               );
         } else {
            player.sendMessage(ChatColor.LIGHT_PURPLE + this.usage);
         }
      }

      private void sendAsMessage(CommandSender sender) {
         if (this.isCommand()) {
            sender.sendMessage(ChatColor.LIGHT_PURPLE + "- /" + this.usage + ChatColor.WHITE + " | " + this.help);
         } else {
            sender.sendMessage(ChatColor.LIGHT_PURPLE + this.usage);
         }
      }
   }
}
