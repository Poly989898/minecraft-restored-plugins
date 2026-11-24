package net.Indyuce.mmoitems.command.mmoitems;

import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.api.util.SmartGive;
import io.lumine.mythic.lib.command.api.CommandTreeNode;
import io.lumine.mythic.lib.command.api.Parameter;
import io.lumine.mythic.lib.command.api.CommandTreeNode.CommandResult;
import java.util.Arrays;
import java.util.Optional;
import java.util.Random;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.ItemTier;
import net.Indyuce.mmoitems.api.Type;
import net.Indyuce.mmoitems.api.item.template.MMOItemTemplate;
import net.Indyuce.mmoitems.api.item.template.explorer.ClassFilter;
import net.Indyuce.mmoitems.api.item.template.explorer.IDFilter;
import net.Indyuce.mmoitems.api.item.template.explorer.TemplateExplorer;
import net.Indyuce.mmoitems.api.item.template.explorer.TierFilter;
import net.Indyuce.mmoitems.api.item.template.explorer.TypeFilter;
import net.Indyuce.mmoitems.api.player.PlayerData;
import net.Indyuce.mmoitems.api.player.RPGPlayer;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class GenerateCommandTreeNode extends CommandTreeNode {
   private static final Random random = new Random();

   public GenerateCommandTreeNode(CommandTreeNode commandTreeNode) {
      super(commandTreeNode, "generate");
      this.addParameter(Parameter.PLAYER);
      this.addParameter(
         new Parameter(
            "(extra-args)",
            (commandTreeExplorer, texts) -> texts.addAll(Arrays.asList("-matchlevel", "-matchclass", "-level:", "-class:", "-type:", "-id:", "-tier:", "-tierset:", "-gimme"))
         )
      );
   }

   public CommandResult execute(CommandSender sender, String[] texts) {
      try {
         if (texts.length < 2) {
            return CommandResult.THROW_USAGE;
         }

         Player player = Bukkit.getPlayer(texts[1]);
         Validate.notNull(player, "Could not find player called " + texts[1] + ".");
         GenerateCommandHandler generateCommandHandler = new GenerateCommandHandler(texts);
         Player receiver = !generateCommandHandler.hasArgument("gimme") && !generateCommandHandler.hasArgument("giveme") ? player : (sender instanceof Player ? (Player)sender : null);
         Validate.notNull(receiver, "You cannot use -gimme");
         RPGPlayer rPG = PlayerData.get(player).getRPG();
         int count = generateCommandHandler.hasArgument("level:")
            ? Integer.parseInt(generateCommandHandler.getValue("level"))
            : (generateCommandHandler.hasArgument("matchlevel") ? MMOItems.plugin.getTemplates().rollLevel(rPG.getLevel()) : 1 + random.nextInt(100));
         ItemTier itemTier = generateCommandHandler.hasArgument("tierset")
            ? null
            : (
               generateCommandHandler.hasArgument("tier:")
                  ? MMOItems.plugin.getTiers().getOrThrow(generateCommandHandler.getValue("tier").toUpperCase().replace("-", "_"))
                  : MMOItems.plugin.getTemplates().rollTier()
            );
         TemplateExplorer templateExplorer = new TemplateExplorer();
         if (generateCommandHandler.hasArgument("matchclass")) {
            templateExplorer.applyFilter(new ClassFilter(rPG));
         }

         if (generateCommandHandler.hasArgument("class:")) {
            templateExplorer.applyFilter(new ClassFilter(generateCommandHandler.getValue("class").replace("-", " ").replace("_", " ")));
         }

         String text = null;
         if (generateCommandHandler.hasArgument("tierset:")) {
            String tierName = UtilityMethods.enumName(generateCommandHandler.getValue("tierset"));
            Validate.isTrue(MMOItems.plugin.getTiers().has(tierName), "Could not find tier with ID '" + tierName + "'");
            templateExplorer.applyFilter(new TierFilter(tierName));
         }

         if (generateCommandHandler.hasArgument("type:")) {
            text = generateCommandHandler.getValue("type");
            Validate.isTrue(Type.isValid(text), "Could not find type with ID '" + text + "'");
            templateExplorer.applyFilter(new TypeFilter(Type.get(text)));
         }

         if (generateCommandHandler.hasArgument("id:")) {
            Validate.isTrue(text != null, "You have to specify a type if using the id option!");
            templateExplorer.applyFilter(new IDFilter(generateCommandHandler.getValue("id")));
         }

         Optional optional = templateExplorer.rollLoot();
         Validate.isTrue(optional.isPresent(), "No item matched your criterias.");
         ItemStack item = ((MMOItemTemplate)optional.get()).newBuilder(count, itemTier).build().newBuilder().build();
         Validate.isTrue(item != null && item.getType() != Material.AIR, "Could not generate item with ID '" + ((MMOItemTemplate)optional.get()).getId() + "'");
         new SmartGive(receiver).give(new ItemStack[]{item});
         return CommandResult.SUCCESS;
      } catch (IllegalArgumentException exception) {
         sender.sendMessage(ChatColor.RED + exception.getMessage());
         return CommandResult.FAILURE;
      }
   }
}
