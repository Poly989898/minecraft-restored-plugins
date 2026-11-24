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

   public CommandListData whenInitialized(Object obj) {
      Validate.isTrue(obj instanceof ConfigurationSection, "Must specify a config section");
      ConfigurationSection section = (ConfigurationSection)obj;
      CommandListData commandListData = new CommandListData();

      for (String key : section.getKeys(false)) {
         ConfigurationSection section2 = section.getConfigurationSection(key);
         commandListData.add(new CommandData(section2.getString("format"), section2.getDouble("delay"), section2.getBoolean("console"), section2.getBoolean("op")));
      }

      return commandListData;
   }

   @Override
   public void whenClicked(@NotNull EditionInventory editionInventory, @NotNull InventoryClickEvent event) {
      new CommandListEdition(editionInventory.getPlayer(), editionInventory.getEdited()).open(editionInventory);
   }

   @Override
   public void whenInput(@NotNull EditionInventory editionInventory, @NotNull String text, Object... objs) {
      if (editionInventory.getEditedSection().contains("commands") && editionInventory.getEditedSection().getConfigurationSection("commands").getKeys(false).size() >= 15) {
         editionInventory.getPlayer().sendMessage(MMOItems.plugin.getPrefix() + "Your item has reached the " + 15 + " commands limit.");
      } else {
         double value = 0.0;
         boolean flag = false;
         boolean flag2 = false;
         String[] texts = text.split(" ");

         for (int i = 0; i < texts.length && texts[i].startsWith("-"); i++) {
            String text2 = texts[i];
            if (text2.startsWith("-d:")) {
               value = MMOUtils.parseDouble(text2.substring(3));
               text = text.replaceFirst(text2 + " ", "");
            } else if (text2.equalsIgnoreCase("-c")) {
               flag = true;
               text = text.replaceFirst(text2 + " ", "");
            } else if (text2.equalsIgnoreCase("-op")) {
               flag2 = true;
               text = text.replaceFirst(text2 + " ", "");
            }
         }

         ConfigurationSection commands = editionInventory.getEditedSection().getConfigurationSection("commands");
         String text2 = "cmd16";
         if (commands == null) {
            text2 = "cmd0";
         } else {
            for (int i = 0; i < 15; i++) {
               if (!commands.contains("cmd" + i)) {
                  text2 = "cmd" + i;
                  break;
               }
            }
         }

         editionInventory.getEditedSection().set("commands." + text2 + ".format", text);
         editionInventory.getEditedSection().set("commands." + text2 + ".delay", value);
         editionInventory.getEditedSection().set("commands." + text2 + ".console", flag ? flag : null);
         editionInventory.getEditedSection().set("commands." + text2 + ".op", flag2 ? flag2 : null);
         editionInventory.registerTemplateEdition();
         editionInventory.getPlayer().sendMessage(MMOItems.plugin.getPrefix() + "Command successfully registered.");
      }
   }

   @Override
   public void whenDisplayed(List<String> texts, Optional<CommandListData> optional) {
      texts.add(ChatColor.GRAY + "Current Commands: " + ChatColor.RED + (optional.isPresent() ? ((CommandListData)optional.get()).getCommands().size() : "0"));
      texts.add("");
      texts.add(ChatColor.YELLOW + "►" + " Click to edit item commands.");
   }

   @NotNull
   public CommandListData getClearStatData() {
      return new CommandListData();
   }

   public void whenApplied(@NotNull ItemStackBuilder itemStackBuilder, @NotNull CommandListData commandListData) {
      itemStackBuilder.addItemTag(this.getAppliedNBT(commandListData));
      ArrayList list = new ArrayList();
      String generalStatFormat = this.getGeneralStatFormat();
      commandListData.getCommands().forEach(var2x -> list.add(generalStatFormat.replace("{format}", "/" + var2x.getCommand()).replace("{cooldown}", String.valueOf(var2x.getDelay()))));
      itemStackBuilder.getLore().insert("commands", list);
   }

   @Override
   public String getLegacyTranslationPath() {
      return "command";
   }

   @NotNull
   public ArrayList<ItemTag> getAppliedNBT(@NotNull CommandListData commandListData) {
      ArrayList list = new ArrayList();
      JsonArray jsonArray = new JsonArray();

      for (CommandData command : commandListData.getCommands()) {
         JsonObject jsonObject = new JsonObject();
         jsonObject.addProperty("Command", command.getCommand());
         jsonObject.addProperty("Delay", command.getDelay());
         jsonObject.addProperty("Console", command.isConsoleCommand());
         jsonObject.addProperty("Op", command.hasOpPerms());
         jsonArray.add(jsonObject);
      }

      list.add(new ItemTag(this.getNBTPath(), jsonArray.toString()));
      return list;
   }

   @Override
   public void whenLoaded(@NotNull ReadMMOItem readMMOItem) {
      ArrayList list = new ArrayList();
      if (readMMOItem.getNBT().hasTag(this.getNBTPath())) {
         list.add(ItemTag.getTagAtPath(this.getNBTPath(), readMMOItem.getNBT(), SupportedNBTTagValues.STRING));
      }

      CommandListData loadedNBT = this.getLoadedNBT(list);
      if (loadedNBT != null) {
         readMMOItem.setData(this, loadedNBT);
      }
   }

   @Nullable
   public CommandListData getLoadedNBT(@NotNull ArrayList<ItemTag> itemTags) {
      ItemTag tagAtPath = ItemTag.getTagAtPath(this.getNBTPath(), itemTags);
      if (tagAtPath != null) {
         try {
            CommandListData commandListData = new CommandListData();

            for (JsonElement asJson : new JsonParser().parse((String)tagAtPath.getValue()).getAsJsonArray()) {
               if (asJson.isJsonObject()) {
                  JsonObject asJsonObject = asJson.getAsJsonObject();
                  CommandData commandData = new CommandData(
                     asJsonObject.get("Command").getAsString(), asJsonObject.get("Delay").getAsDouble(), asJsonObject.get("Console").getAsBoolean(), asJsonObject.get("Op").getAsBoolean()
                  );
                  commandListData.add(commandData);
               }
            }

            return commandListData;
         } catch (JsonSyntaxException | IllegalStateException exception) {
         }
      }

      return null;
   }
}
