package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import java.util.Arrays;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Locale.LC;
import org.bukkit.command.CommandSender;

public class scavenge implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get("enchantWorth", "{gcp}Enchantment worth: {gcs}[price]");
    configReader.get("enchantFailChance", "{gcw}Fail chance: {gcs}[chance]%");
    configReader.get(
        "enchantFailed",
        "{gcw}Failed. {gcs}[enchant]{gcw} level {gcs}[level] {gcw}With {gcs}[chance]% {gcw}fail"
            + " chance");
    configReader.get("enchantLevelChance", "{gcw}Level lower chance: {gcs}[chance]%");
    configReader.get("gotIngredients", "{gcp}You got [itemList]");
    configReader.get("gotIngredientsFormat", "{gcs}[material]{gcp}-{gcs}[amount]");
    configReader.get("itemBroke", "{gcw}Item broke with [chance]% break chance");
    configReader.get("confirm", "{gcu}Confirm scavenge. Cost: {gcs}[price]");
    configReader.get("itemBreakChance", "{gcw}Item break chance: {gcs}[chance]%");
    configReader.get(
        "itemBreakChanceExtra",
        "{gcw}[durability]% durability adds {gcs}[chance]% {gcw}break chance");
    configReader.get("itemBreak", "{gcw}Item will break on click!");
    configReader.get("ingredientReturnChance", "{gcp}Ingredient return chance: {gcs}[chance]%");
    configReader.get("cantScavenge", "{gcw}Can't scavenge");
    configReader.get("insertItem", "{gcu}Insert item in a slot above");
    configReader.get("scavengeCost", "{gcp}Scavenge cost: {gcs}[cost]");
    configReader.get("moreTitle", "{gcp}Incorrect amount: {gcs}[input]{gcp}/{gcs}[required]");
    configReader.get(
        "moreLore",
        Arrays.asList("{gcp}You need more of this item.", "{gcp}Requires atleast {gcs}[amount]"));
    configReader.get("title", "{gcp}Scavenger");
  }

  @CAnnotation(
      info = "Recycle item and its enchantments",
      args = "(playerName)",
      tab = "playername",
      explanation = {},
      regVar = {0, 1, 2},
      consoleVar = 1,
      others = true,
      alias = true,
      customAlias = {"salvage", "recycle"})
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    String string = null;
    String[] commandArgs = strings;
    int value = strings.length;

    for (int argIndex = 0; argIndex < value; argIndex++) {
      String arg = commandArgs[argIndex];
      string = arg;
    }

    CMIUser cmiUser = cmi.getUser(commandSender, string, this);
    if (cmiUser == null) {
      return false;
    } else if (!cmiUser.isOnline()) {
      LC.info_NotOnline.sendMessage(commandSender, new Object[0]);
      return null;
    } else {
      cmi.getScavengeManager().openExtractor(cmiUser.getPlayer());
      return true;
    }
  }
}
