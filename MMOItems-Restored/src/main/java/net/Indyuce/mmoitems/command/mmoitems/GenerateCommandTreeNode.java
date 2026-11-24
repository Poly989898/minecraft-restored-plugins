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

   public GenerateCommandTreeNode(CommandTreeNode var1) {
      super(var1, "generate");
      this.addParameter(Parameter.PLAYER);
      this.addParameter(
         new Parameter(
            "(extra-args)",
            (var0, var1x) -> var1x.addAll(Arrays.asList("-matchlevel", "-matchclass", "-level:", "-class:", "-type:", "-id:", "-tier:", "-tierset:", "-gimme"))
         )
      );
   }

   public CommandResult execute(CommandSender var1, String[] var2) {
      try {
         if (var2.length < 2) {
            return CommandResult.THROW_USAGE;
         }

         Player var3 = Bukkit.getPlayer(var2[1]);
         Validate.notNull(var3, "Could not find player called " + var2[1] + ".");
         GenerateCommandHandler var4 = new GenerateCommandHandler(var2);
         Player var5 = !var4.hasArgument("gimme") && !var4.hasArgument("giveme") ? var3 : (var1 instanceof Player ? (Player)var1 : null);
         Validate.notNull(var5, "You cannot use -gimme");
         RPGPlayer var6 = PlayerData.get(var3).getRPG();
         int var7 = var4.hasArgument("level:")
            ? Integer.parseInt(var4.getValue("level"))
            : (var4.hasArgument("matchlevel") ? MMOItems.plugin.getTemplates().rollLevel(var6.getLevel()) : 1 + random.nextInt(100));
         ItemTier var8 = var4.hasArgument("tierset")
            ? null
            : (
               var4.hasArgument("tier:")
                  ? MMOItems.plugin.getTiers().getOrThrow(var4.getValue("tier").toUpperCase().replace("-", "_"))
                  : MMOItems.plugin.getTemplates().rollTier()
            );
         TemplateExplorer var9 = new TemplateExplorer();
         if (var4.hasArgument("matchclass")) {
            var9.applyFilter(new ClassFilter(var6));
         }

         if (var4.hasArgument("class:")) {
            var9.applyFilter(new ClassFilter(var4.getValue("class").replace("-", " ").replace("_", " ")));
         }

         String var10 = null;
         if (var4.hasArgument("tierset:")) {
            String var11 = UtilityMethods.enumName(var4.getValue("tierset"));
            Validate.isTrue(MMOItems.plugin.getTiers().has(var11), "Could not find tier with ID '" + var11 + "'");
            var9.applyFilter(new TierFilter(var11));
         }

         if (var4.hasArgument("type:")) {
            var10 = var4.getValue("type");
            Validate.isTrue(Type.isValid(var10), "Could not find type with ID '" + var10 + "'");
            var9.applyFilter(new TypeFilter(Type.get(var10)));
         }

         if (var4.hasArgument("id:")) {
            Validate.isTrue(var10 != null, "You have to specify a type if using the id option!");
            var9.applyFilter(new IDFilter(var4.getValue("id")));
         }

         Optional var14 = var9.rollLoot();
         Validate.isTrue(var14.isPresent(), "No item matched your criterias.");
         ItemStack var12 = ((MMOItemTemplate)var14.get()).newBuilder(var7, var8).build().newBuilder().build();
         Validate.isTrue(var12 != null && var12.getType() != Material.AIR, "Could not generate item with ID '" + ((MMOItemTemplate)var14.get()).getId() + "'");
         new SmartGive(var5).give(new ItemStack[]{var12});
         return CommandResult.SUCCESS;
      } catch (IllegalArgumentException var13) {
         var1.sendMessage(ChatColor.RED + var13.getMessage());
         return CommandResult.FAILURE;
      }
   }
}
