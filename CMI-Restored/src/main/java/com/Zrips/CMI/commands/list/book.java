package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import com.Zrips.CMI.commands.CommandsHandler;
import com.Zrips.CMI.utils.DateFormat;
import net.Zrips.CMILib.Colors.CMIChatColor;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Items.CMIItemStack;
import net.Zrips.CMILib.Items.CMIMaterial;
import net.Zrips.CMILib.NBT.CMINBT;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

public class book implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get("noitem", "{gcw}Not holding any item in hand.");
    configReader.get("notBook", "{gcw}Item is not written book.");
    configReader.get("wrongValue", "{gcw}Item is not closed written book.");
    configReader.get("inccorectAction", "{gcp}Incorrect action name.");
    configReader.get("authorChanged", "{gcp}Author changed to: {gcs}[author]");
    configReader.get("titleChanged", "{gcp}Title changed to: {gcs}[title]");
    configReader.get("unlocked", "{gcp}Book was unlocked");
  }

  @CAnnotation(
      info = "Book editing",
      args = "[Author/Title/Unlock] [value]",
      tab = "Author%%Title%%Unlock",
      regVar = -100,
      consoleVar = 666,
      others = false)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    book.Action action = book.Action.None;
    String string = "";
    String[] args = strings;
    int value = strings.length;

    for (int argIndex = 0; argIndex < value; argIndex++) {
      String arg = args[argIndex];
      if (!string.isEmpty()) {
        string = string + " ";
      }

      if (action == book.Action.None) {
        if (arg.equalsIgnoreCase("Author")) {
          action = book.Action.Author;
          continue;
        }

        if (arg.equalsIgnoreCase("Title")) {
          action = book.Action.Title;
          continue;
        }

        if (arg.equalsIgnoreCase("Unlock")) {
          action = book.Action.Unlock;
          continue;
        }
      }

      string = string + arg;
    }

    Player player = cmi.getTarget(commandSender, null, this);
    if (player == null) {
      return null;
    }

    ItemStack itemStack = CMIItemStack.getItemInMainHand(player);
    if (action == book.Action.None) {
      CMILC.info(this, commandSender, "inccorectAction");
      return null;
    }

    if (itemStack == null || itemStack.getType() == Material.AIR) {
      CMILC.info(this, commandSender, "noitem");
      return null;
    }

    if (itemStack.getType() != Material.WRITTEN_BOOK) {
      CMILC.info(this, commandSender, "notBook");
      return null;
    }

    BookMeta bookMeta = (BookMeta) itemStack.getItemMeta();
    switch (action) {
      case Author:
        bookMeta.setAuthor(CMIChatColor.translate(string));
        if (Version.isCurrentEqualOrHigher(Version.v1_20_R4)) {
          itemStack.setItemMeta(bookMeta);
        }

        CMILC.info(this, commandSender, "authorChanged", "[author]", bookMeta.getAuthor());
        break;
      case Title:
        if (string.length() > 32) {
          string = string.substring(0, 31) + "~";
        }

        bookMeta.setTitle(CMIChatColor.translate(string));
        if (Version.isCurrentEqualOrHigher(Version.v1_20_R4)) {
          itemStack.setItemMeta(bookMeta);
        }

        CMILC.info(this, commandSender, "titleChanged", "[title]", bookMeta.getTitle());
        break;
      case Unlock:
        CMILC.info(this, commandSender, "unlocked");
        if (Version.isCurrentEqualOrLower(Version.v1_20_R3)) {
          itemStack = (ItemStack) new CMINBT(itemStack).setInt("resolved", 0);
        }

        var list = bookMeta.getLore();
        if (list != null) {
          for (int lineIndex = list.size() - 1; lineIndex >= 0; lineIndex--) {
            String lineText = (String) list.get(lineIndex);
            int textDistance =
                CommandsHandler.distance(
                    CMILC.info_bookDate.getLocale(
                        "[date]", DateFormat.MiliToDate(System.currentTimeMillis())),
                    lineText);
            if (textDistance <= 5) {
              list.remove(lineIndex);
            }
          }

          bookMeta.setLore(list);
        }

        itemStack.setType(CMIMaterial.WRITABLE_BOOK.getMaterial());
        if (Version.isCurrentEqualOrHigher(Version.v1_20_R4)) {
          BookMeta updatedMeta = (BookMeta) itemStack.getItemMeta();
          updatedMeta.setPages(bookMeta.getPages());
          itemStack.setItemMeta(updatedMeta);
        }
        break;
      case None:
        CMILC.info(this, commandSender, "inccorectId");
    }

    if (Version.isCurrentEqualOrLower(Version.v1_20_R3)) {
      itemStack.setItemMeta(bookMeta);
    }

    CMIItemStack.setItemInMainHand(player, itemStack);
    player.updateInventory();
    return true;
  }

  private enum Action {
    Author,
    Title,
    Unlock,
    None;
  }
}
