package net.Indyuce.mmoitems.stat;

import io.lumine.mythic.lib.api.item.ItemTag;
import io.lumine.mythic.lib.api.item.SupportedNBTTagValues;
import io.lumine.mythic.lib.gson.JsonArray;
import io.lumine.mythic.lib.gson.JsonElement;
import io.lumine.mythic.lib.gson.JsonObject;
import io.lumine.mythic.lib.gson.JsonParser;
import io.lumine.mythic.lib.gson.JsonSyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.item.build.ItemStackBuilder;
import net.Indyuce.mmoitems.api.item.mmoitem.ReadMMOItem;
import net.Indyuce.mmoitems.gui.edition.CommandListEdition;
import net.Indyuce.mmoitems.gui.edition.EditionInventory;
import net.Indyuce.mmoitems.stat.data.CommandData;
import net.Indyuce.mmoitems.stat.data.CommandListData;
import net.Indyuce.mmoitems.stat.type.ItemStat;
import net.Indyuce.mmoitems.util.MMOUtils;
import org.apache.commons.lang.Validate;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Commands extends ItemStat<CommandListData, CommandListData> {
   private static final int MAXIMUM_COMMANDS = 15;

   public Commands() {
      super(
         "COMMANDS",
         Material.COMMAND_BLOCK_MINECART,
         "Commands",
         new String[]{"The commands your item", "performs when right clicked."},
         new String[]{"!armor", "!block", "!gem_stone", "all"}
      );
   }

   public CommandListData whenInitialized(Object var1) {
      Validate.isTrue(var1 instanceof ConfigurationSection, "Must specify a config section");
      ConfigurationSection var2 = (ConfigurationSection)var1;
      CommandListData var3 = new CommandListData();

      for (String var5 : var2.getKeys(false)) {
         ConfigurationSection var6 = var2.getConfigurationSection(var5);
         var3.add(new CommandData(var6.getString("format"), var6.getDouble("delay"), var6.getBoolean("console"), var6.getBoolean("op")));
      }

      return var3;
   }

   @Override
   public void whenClicked(@NotNull EditionInventory var1, @NotNull InventoryClickEvent var2) {
      new CommandListEdition(var1.getPlayer(), var1.getEdited()).open(var1);
   }

   @Override
   public void whenInput(@NotNull EditionInventory var1, @NotNull String var2, Object... var3) {
      if (var1.getEditedSection().contains("commands") && var1.getEditedSection().getConfigurationSection("commands").getKeys(false).size() >= 15) {
         var1.getPlayer().sendMessage(MMOItems.plugin.getPrefix() + "Your item has reached the " + 15 + " commands limit.");
      } else {
         double var4 = 0.0;
         boolean var6 = false;
         boolean var7 = false;
         String[] var8 = var2.split(" ");

         for (int var9 = 0; var9 < var8.length && var8[var9].startsWith("-"); var9++) {
            String var10 = var8[var9];
            if (var10.startsWith("-d:")) {
               var4 = MMOUtils.parseDouble(var10.substring(3));
               var2 = var2.replaceFirst(var10 + " ", "");
            } else if (var10.equalsIgnoreCase("-c")) {
               var6 = true;
               var2 = var2.replaceFirst(var10 + " ", "");
            } else if (var10.equalsIgnoreCase("-op")) {
               var7 = true;
               var2 = var2.replaceFirst(var10 + " ", "");
            }
         }

         ConfigurationSection var12 = var1.getEditedSection().getConfigurationSection("commands");
         String var13 = "cmd16";
         if (var12 == null) {
            var13 = "cmd0";
         } else {
            for (int var11 = 0; var11 < 15; var11++) {
               if (!var12.contains("cmd" + var11)) {
                  var13 = "cmd" + var11;
                  break;
               }
            }
         }

         var1.getEditedSection().set("commands." + var13 + ".format", var2);
         var1.getEditedSection().set("commands." + var13 + ".delay", var4);
         var1.getEditedSection().set("commands." + var13 + ".console", var6 ? var6 : null);
         var1.getEditedSection().set("commands." + var13 + ".op", var7 ? var7 : null);
         var1.registerTemplateEdition();
         var1.getPlayer().sendMessage(MMOItems.plugin.getPrefix() + "Command successfully registered.");
      }
   }

   @Override
   public void whenDisplayed(List<String> var1, Optional<CommandListData> var2) {
      var1.add(ChatColor.GRAY + "Current Commands: " + ChatColor.RED + (var2.isPresent() ? ((CommandListData)var2.get()).getCommands().size() : "0"));
      var1.add("");
      var1.add(ChatColor.YELLOW + "►" + " Click to edit item commands.");
   }

   @NotNull
   public CommandListData getClearStatData() {
      return new CommandListData();
   }

   public void whenApplied(@NotNull ItemStackBuilder var1, @NotNull CommandListData var2) {
      var1.addItemTag(this.getAppliedNBT(var2));
      ArrayList var3 = new ArrayList();
      String var4 = this.getGeneralStatFormat();
      var2.getCommands().forEach(var2x -> var3.add(var4.replace("{format}", "/" + var2x.getCommand()).replace("{cooldown}", String.valueOf(var2x.getDelay()))));
      var1.getLore().insert("commands", var3);
   }

   @Override
   public String getLegacyTranslationPath() {
      return "command";
   }

   @NotNull
   public ArrayList<ItemTag> getAppliedNBT(@NotNull CommandListData var1) {
      ArrayList var2 = new ArrayList();
      JsonArray var3 = new JsonArray();

      for (CommandData var5 : var1.getCommands()) {
         JsonObject var6 = new JsonObject();
         var6.addProperty("Command", var5.getCommand());
         var6.addProperty("Delay", var5.getDelay());
         var6.addProperty("Console", var5.isConsoleCommand());
         var6.addProperty("Op", var5.hasOpPerms());
         var3.add(var6);
      }

      var2.add(new ItemTag(this.getNBTPath(), var3.toString()));
      return var2;
   }

   @Override
   public void whenLoaded(@NotNull ReadMMOItem var1) {
      ArrayList var2 = new ArrayList();
      if (var1.getNBT().hasTag(this.getNBTPath())) {
         var2.add(ItemTag.getTagAtPath(this.getNBTPath(), var1.getNBT(), SupportedNBTTagValues.STRING));
      }

      CommandListData var3 = this.getLoadedNBT(var2);
      if (var3 != null) {
         var1.setData(this, var3);
      }
   }

   @Nullable
   public CommandListData getLoadedNBT(@NotNull ArrayList<ItemTag> var1) {
      ItemTag var2 = ItemTag.getTagAtPath(this.getNBTPath(), var1);
      if (var2 != null) {
         try {
            CommandListData var3 = new CommandListData();

            for (JsonElement var6 : new JsonParser().parse((String)var2.getValue()).getAsJsonArray()) {
               if (var6.isJsonObject()) {
                  JsonObject var7 = var6.getAsJsonObject();
                  CommandData var8 = new CommandData(
                     var7.get("Command").getAsString(), var7.get("Delay").getAsDouble(), var7.get("Console").getAsBoolean(), var7.get("Op").getAsBoolean()
                  );
                  var3.add(var8);
               }
            }

            return var3;
         } catch (JsonSyntaxException | IllegalStateException var9) {
         }
      }

      return null;
   }
}
