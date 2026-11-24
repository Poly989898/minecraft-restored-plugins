package net.Indyuce.mmoitems.command.mmoitems;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.command.api.CommandTreeNode;
import io.lumine.mythic.lib.command.api.CommandTreeNode.CommandResult;
import java.util.function.Consumer;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.event.MMOItemsReloadEvent;
import net.Indyuce.mmoitems.api.util.MMOItemReforger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class ReloadCommandTreeNode extends CommandTreeNode {
   public ReloadCommandTreeNode(CommandTreeNode var1) {
      super(var1, "reload");
      this.addChild(new ReloadCommandTreeNode.SubReloadCommandTreeNode("recipes", this, this::reloadRecipes));
      this.addChild(new ReloadCommandTreeNode.SubReloadCommandTreeNode("stations", this, this::reloadStations));
      this.addChild(new ReloadCommandTreeNode.SubReloadCommandTreeNode("skills", this, this::reloadSkills));
      this.addChild(new ReloadCommandTreeNode.SubReloadCommandTreeNode("all", this, var1x -> {
         this.reloadMain(var1x);
         this.reloadRecipes(var1x);
         this.reloadStations(var1x);
         this.reloadSkills(var1x);
      }));
   }

   public CommandResult execute(CommandSender var1, String[] var2) {
      this.reloadMain(var1);
      return CommandResult.SUCCESS;
   }

   public void reloadSkills(CommandSender var1) {
      MythicLib.plugin.getSkills().initialize(true);
      MMOItems.plugin.getSkills().initialize(true);
      var1.sendMessage(MMOItems.plugin.getPrefix() + "Successfully reloaded " + MMOItems.plugin.getSkills().getAll().size() + " skills.");
   }

   public void reloadMain(CommandSender var1) {
      Bukkit.getPluginManager().callEvent(new MMOItemsReloadEvent());
      MMOItems.plugin.getLanguage().reload();
      MMOItems.plugin.getDropTables().reload();
      MMOItems.plugin.getTypes().reload(true);
      MMOItems.plugin.getTiers().reload();
      MMOItems.plugin.getSets().reload();
      MMOItems.plugin.getUpgrades().reload();
      MMOItems.plugin.getWorldGen().reload();
      MMOItems.plugin.getCustomBlocks().reload();
      MMOItems.plugin.getLayouts().reload();
      MMOItems.plugin.getLore().reload();
      MMOItems.plugin.getTemplates().reload();
      MMOItems.plugin.getStats().reload(true);
      var1.sendMessage(MMOItems.plugin.getPrefix() + MMOItems.plugin.getName() + " " + MMOItems.plugin.getDescription().getVersion() + " reloaded.");
      var1.sendMessage(MMOItems.plugin.getPrefix() + "- " + ChatColor.RED + MMOItems.plugin.getTypes().getAll().size() + ChatColor.GRAY + " Item Types");
      var1.sendMessage(MMOItems.plugin.getPrefix() + "- " + ChatColor.RED + MMOItems.plugin.getTiers().getAll().size() + ChatColor.GRAY + " Item Tiers");
      var1.sendMessage(MMOItems.plugin.getPrefix() + "- " + ChatColor.RED + MMOItems.plugin.getSets().getAll().size() + ChatColor.GRAY + " Item Sets");
      var1.sendMessage(
         MMOItems.plugin.getPrefix() + "- " + ChatColor.RED + MMOItems.plugin.getUpgrades().getAll().size() + ChatColor.GRAY + " Upgrade Templates"
      );
      MMOItemReforger.reload();
   }

   public void reloadRecipes(CommandSender var1) {
      MMOItems.plugin.getRecipes().reload();
      var1.sendMessage(MMOItems.plugin.getPrefix() + "Successfully reloaded recipes.");
      var1.sendMessage(
         MMOItems.plugin.getPrefix()
            + "- "
            + ChatColor.RED
            + (
               MMOItems.plugin.getRecipes().getBukkitRecipes().size()
                  + MMOItems.plugin.getRecipes().getBooklessRecipes().size()
                  + MMOItems.plugin.getRecipes().getCustomRecipes().size()
            )
            + ChatColor.GRAY
            + " Recipes"
      );
   }

   public void reloadStations(CommandSender var1) {
      MMOItems.plugin.getLayouts().reload();
      MMOItems.plugin.getCrafting().reload();
      var1.sendMessage(MMOItems.plugin.getPrefix() + "Successfully reloaded the crafting stations..");
      var1.sendMessage(
         MMOItems.plugin.getPrefix() + "- " + ChatColor.RED + MMOItems.plugin.getCrafting().getAll().size() + ChatColor.GRAY + " Crafting Stations"
      );
      var1.sendMessage(MMOItems.plugin.getPrefix() + "- " + ChatColor.RED + MMOItems.plugin.getCrafting().countRecipes() + ChatColor.GRAY + " Recipes");
   }

   public class SubReloadCommandTreeNode extends CommandTreeNode {
      private final Consumer<CommandSender> action;

      public SubReloadCommandTreeNode(String nullx, CommandTreeNode nullxx, Consumer<CommandSender> nullxxx) {
         super(nullxx, nullx);
         this.action = nullxxx;
      }

      public CommandResult execute(CommandSender var1, String[] var2) {
         this.action.accept(var1);
         return CommandResult.SUCCESS;
      }
   }
}
