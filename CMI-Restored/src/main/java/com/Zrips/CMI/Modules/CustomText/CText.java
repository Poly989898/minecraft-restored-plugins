package com.Zrips.CMI.Modules.CustomText;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Components.CMIComponentActionType;
import com.Zrips.CMI.Components.CMIComponentBase;
import com.Zrips.CMI.Components.CMIComponentText;
import com.Zrips.CMI.Containers.CommandAlias;
import com.Zrips.CMI.Containers.Snd;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.commands.CommandsHandler;
import com.Zrips.CMI.commands.list.getbook;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.Zrips.CMILib.Colors.CMIChatColor;
import net.Zrips.CMILib.RawMessages.RawMessage;
import net.Zrips.CMILib.Shadow.ShadowCommand;
import net.Zrips.CMILib.Shadow.ShadowCommandType;
import net.Zrips.CMILib.Version.Version;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

public class CText {
  private HashMap<Integer, CTextPage> pages = new HashMap<>();
  private String name;
  private boolean autoPage = true;
  private boolean hidden = false;
  private boolean autoAlias = false;
  private boolean requirePermission = false;
  private String path = "";
  static final Pattern patern =
      Pattern.compile(
          "((http|https|ftp|ftps)\\:\\/\\/)?[a-zA-Z0-9\\-]+\\.[a-zA-Z]{2,3}(\\/\\S*)?([^\\s|^\\)]+)");

  public CText(String string) {
    this.name = string;
  }

  public String getName() {
    return this.name;
  }

  public int getTotalPages() {
    return this.pages.size();
  }

  public int getTotalLines() {
    int value = 0;

    for (var entry : this.pages.entrySet()) {
      value += ((CTextPage) entry.getValue()).getLines().size();
    }

    return value;
  }

  public void setName(String string) {
    this.name = string;
  }

  @Deprecated
  public HashMap<Integer, List<String>> getPages() {
    HashMap<Integer, List<String>> hashMap = new HashMap();
    this.pages.forEach((integer, cTextPage) -> hashMap.put(integer, cTextPage.getLines()));
    return hashMap;
  }

  public HashMap<Integer, CTextPage> getPagesMap() {
    return this.pages;
  }

  public CTextPage getPageInfo(int value) {
    return this.pages.get(value);
  }

  @Deprecated
  public List<String> getPage(Integer integer) {
    return this.pages.get(integer).getLines();
  }

  @Deprecated
  public void setPages(HashMap<Integer, List<String>> hashMap) {
    hashMap.forEach((integer, list) -> this.pages.put(integer, new CTextPage((List<String>) list)));
  }

  public void setPagesMap(HashMap<Integer, CTextPage> hashMap) {
    this.pages = hashMap;
  }

  @Deprecated
  public void addPage(Integer integer, List<String> list) {
    this.pages.put(integer, new CTextPage(list));
  }

  public void addPage(int value, CTextPage cTextPage) {
    this.pages.put(value, cTextPage);
  }

  @Deprecated
  public void removePage(Integer integer) {
    this.removePage(integer.intValue());
  }

  public void removePage(int value) {
    this.pages.remove(value);
    var hashMap = new HashMap<>(this.pages);
    this.pages.clear();
    int lineCount = 0;

    for (Entry entry : hashMap.entrySet()) {
      this.pages.put(++lineCount, (CTextPage) entry.getValue());
    }
  }

  @Deprecated
  public void removeLine(Integer integer, Integer endLine) {
    this.removeLine(integer.intValue(), endLine.intValue());
  }

  public void removeLine(int value, int endLine) {
    CTextPage cTextPage = this.pages.get(value);
    if (cTextPage != null && cTextPage.getLines().size() >= endLine) {
      cTextPage.getLines().remove(endLine);
      this.pages.put(value, cTextPage);
    }
  }

  @Deprecated
  public void replaceLine(Integer integer, Integer endLine, String string) {
    this.replaceLine(integer.intValue(), endLine.intValue(), string);
  }

  public void replaceLine(int value, int endLine, String string) {
    CTextPage cTextPage = this.pages.get(value);
    if (cTextPage != null && cTextPage.getLines().size() >= endLine) {
      cTextPage.getLines().set(endLine, string);
      this.pages.put(value, cTextPage);
    }
  }

  @Deprecated
  public void addLine(Integer integer, String string) {
    this.addLine(integer.intValue(), string);
  }

  public void addLine(int value, String string) {
    CTextPage cTextPage = this.pages.getOrDefault(value, new CTextPage());
    cTextPage.getLines().add(string);
    this.pages.put(value, cTextPage);
  }

  public void addPageLabel(int value, String string) {
    this.pages.put(value, this.pages.getOrDefault(value, new CTextPage()).setLabel(string));
  }

  public boolean isAutoPage() {
    return this.autoPage;
  }

  public void setAutoPage(boolean flag) {
    this.autoPage = flag;
  }

  public boolean isAutoAlias() {
    return this.autoAlias;
  }

  public void setAutoAlias(boolean flag, boolean autoAliasOn) {
    this.autoAlias = flag;
    if (!flag) {
      CMI.getInstance().getAliasManager().removeCustom(this.getName());
      if (autoAliasOn) {
        CMI.getInstance().getAliasManager().saveNew();
      }
    } else {
      CMI.getInstance()
          .getAliasManager()
          .addCustom(
              new CommandAlias(
                  this.getName().toLowerCase(),
                  Arrays.asList("cmi ctext " + this.getName()),
                  true));
      if (autoAliasOn) {
        CMI.getInstance().getAliasManager().saveNew();
      }
    }
  }

  public boolean isRequirePermission() {
    return this.requirePermission;
  }

  public void setRequirePermission(boolean flag) {
    this.requirePermission = flag;
  }

  public ItemStack convertToBook(Player player) {
    ArrayList<com.Zrips.CMI.Modules.CustomText.CText.bookPage> arrayList = new ArrayList();
    Snd snd = new Snd().setSender(player).setTarget(player);

    for (var entry : this.getPages().entrySet()) {
      StringBuilder stringBuilder = new StringBuilder();
      boolean flag = false;

      for (String string : (List<String>) entry.getValue()) {
        if (string.startsWith("<T>") || string.startsWith("<t>")) {
          flag = true;
          break;
        }
      }

      if (!flag) {
        for (String line : (List<String>) entry.getValue()) {
          if (!stringBuilder.toString().isEmpty()) {
            stringBuilder.append(" \n");
          }

          String lineText = line;
          lineText = CMI.getInstance().getLM().updateSnd(snd, lineText);
          if (player != null) {
            lineText =
                CMI.getInstance().getPlaceholderAPIManager().updatePlaceHolders(player, lineText);
          }

          stringBuilder.append(CMIChatColor.translate(lineText));
        }
      } else {
        RawMessage rawMessage = new RawMessage();

        for (String secondLine : (List<String>) entry.getValue()) {
          String secondLineText = secondLine;
          secondLineText = CMI.getInstance().getLM().updateSnd(snd, secondLineText);
          if (player != null) {
            secondLineText =
                CMI.getInstance()
                    .getPlaceholderAPIManager()
                    .updatePlaceHolders(player, secondLineText);
          }

          if (!secondLine.startsWith("<T>") && !secondLine.startsWith("<t>")) {
            secondLineText = "<T>" + secondLineText + "</T>";
          }

          translateRaw(player, rawMessage, secondLineText, false);
          rawMessage.addText("\n");
        }

        stringBuilder.append(rawMessage.getRaw());
      }

      arrayList.add(new CText.bookPage(flag, CMIChatColor.translate(stringBuilder.toString())));
    }

    ItemStack itemStack = new ItemStack(Material.WRITTEN_BOOK);
    BookMeta bookMeta = (BookMeta) itemStack.getItemMeta();
    String plainName = this.getName().replace("_", " ");
    bookMeta.setTitle(
        CMILC.getIM(
            getbook.class,
            "title",
            "[title]",
            plainName.substring(0, 1).toUpperCase() + plainName.substring(1, plainName.length())));
    bookMeta.setAuthor(CMI.getInstance().getConfigManager().getBooksDefaultAuthor());
    if (Version.isCurrentEqualOrHigher(Version.v1_20_R1)) {
      for (CText.bookPage page : arrayList) {
        if (page.isJson()) {
          bookMeta
              .spigot()
              .addPage(new BaseComponent[][] {ComponentSerializer.parse(page.getLines())});
        } else {
          bookMeta.addPage(new String[] {page.getLines()});
        }
      }
    }

    itemStack.setItemMeta(bookMeta);
    if (Version.isCurrentLower(Version.v1_20_R1)) {
      ArrayList<String> pages = new ArrayList();

      for (CText.bookPage page : arrayList) {
        pages.add(page.getLines());
      }

      itemStack = CMI.getInstance().getNMS().setNBTList(itemStack, "pages", pages);
    }

    return itemStack;
  }

  private static RawMessage translateRaw(
      CommandSender commandSender, RawMessage rawMessage, String string, boolean flag) {
    if (rawMessage == null) {
      rawMessage = new RawMessage();
    }

    string = CMIChatColor.deColorize(string);
    string = string.replace("\n", "\\n");
    ArrayList<String> arrayList = new ArrayList();
    if (string.contains("<Next>")) {
      arrayList.addAll(Arrays.asList(string.split("<Next>")));
    } else {
      arrayList.add(string);
    }

    ArrayList<String> pageLines = new ArrayList<>(arrayList);
    arrayList.clear();

    for (String line : pageLines) {
      if (line.split("<T>").length > 2) {
        String[] strings;
        int value = (strings = line.split("<T>")).length;

        for (int index = 0; index < value; index++) {
          String arg = strings[index];
          if (arg != null && !arg.isEmpty()) {
            arrayList.add("<T>" + arg);
          }
        }
      } else {
        arrayList.add(line);
      }
    }

    for (String pageLine : arrayList) {
      String matchedText = null;
      if (pageLine.contains("<T>")) {
        matchedText = pageLine.replaceAll(".*\\<T>|\\</T>.*", "");
        Matcher matcher = patern.matcher(matchedText);
        if (matcher.find()) {
          String matchedFull = matcher.group();
          String[] matchParts = matchedText.split(Pattern.quote(matchedFull));
          if (matchParts.length > 0) {
            if (matchParts[0].endsWith("&")) {
              rawMessage.addText(
                  CMIChatColor.translate(matchParts[0]) + matchedFull.substring(0, 1));
              matchedFull = matchedFull.substring(1);
            } else {
              rawMessage.addText(CMIChatColor.translate(matchParts[0]));
            }
          }

          rawMessage.addText(CMIChatColor.translate(matchedFull));
          rawMessage.addUrl(matchedFull);
          if (matchParts.length > 1) {
            rawMessage.addText(CMIChatColor.translate(matchParts[1]));
          }
        } else {
          rawMessage.addText(CMIChatColor.translate(matchedText));
        }
      }

      if (pageLine.contains("<H>")) {
        rawMessage.addHover(pageLine.replaceAll(".*\\<H>|\\</H>.*", ""));
      }

      String beforeText = null;
      if (pageLine.contains("<C>")) {
        beforeText = pageLine.replaceAll(".*\\<C>|\\</C>.*", "");
      }

      if (pageLine.contains("<URL>")) {
        rawMessage.addUrl(pageLine.replaceAll(".*\\<URL>|\\</URL>.*", ""));
      }

      String afterText = null;
      if (pageLine.contains("<CC>") && commandSender != null && commandSender instanceof Player) {
        Player player = (Player) commandSender;
        afterText = pageLine.replaceAll(".*\\<CC>|\\</CC>.*", "");
        String replacement =
            ShadowCommand.addShadowCmd(player, afterText, false, ShadowCommandType.Console);
        beforeText =
            CommandsHandler.getLabel() + " shadowcmd " + replacement + " (" + afterText + ")";
        if (flag) {
          beforeText = afterText;
        }
      }

      if (pageLine.contains("<CCI>") && commandSender != null && commandSender instanceof Player) {
        Player player = (Player) commandSender;
        afterText = pageLine.replaceAll(".*\\<CCI>|\\</CCI>.*", "");
        String replaced =
            ShadowCommand.addShadowCmd(player, afterText, true, ShadowCommandType.Console);
        beforeText = CommandsHandler.getLabel() + " shadowcmd " + replaced + " (" + afterText + ")";
        if (flag) {
          beforeText = afterText;
        }
      }

      if (pageLine.contains("<SC>")) {
        rawMessage.addSuggestion(pageLine.replaceAll(".*\\<SC>|\\</SC>.*", ""));
      }

      if (pageLine.contains("<SI>")) {
        rawMessage.addInsertion(pageLine.replaceAll(".*\\<SI>|\\</SI>.*", ""));
      }

      rawMessage.addCommand(beforeText);
    }

    return rawMessage;
  }

  public static CMIComponentBase translateToComponent(
      CommandSender commandSender, String string, boolean flag) {
    CMIComponentBase cmiComponentBase = CMIComponentBase.builder();
    string = CMIChatColor.deColorize(string);
    string = string.replace("\n", "\\n");
    if (!string.contains("<T>")) {
      CMIComponentText cmiComponentText = new CMIComponentText(string);
      cmiComponentText.setText(string);
      return cmiComponentBase;
    }

    ArrayList<String> arrayList = new ArrayList();
    if (string.contains("<Next>")) {
      arrayList.addAll(Arrays.asList(string.split("<Next>")));
    } else {
      arrayList.add(string);
    }

    ArrayList<String> pageLines = new ArrayList<>(arrayList);
    arrayList.clear();

    for (String line : pageLines) {
      if (line.split("<T>").length > 2) {
        String[] strings;
        int value = (strings = line.split("<T>")).length;

        for (int index = 0; index < value; index++) {
          String arg = strings[index];
          if (arg != null && !arg.isEmpty()) {
            arrayList.add("<T>" + arg);
          }
        }
      } else {
        arrayList.add(line);
      }
    }

    for (String pageLine : arrayList) {
      String matchedText = null;
      CMIComponentText componentText = new CMIComponentText();
      if (pageLine.contains("<T>")) {
        matchedText = pageLine.replaceAll(".*\\<T>|\\</T>.*", "");
        Matcher matcher = patern.matcher(matchedText);
        if (matcher.find()) {
          String matchedFull = matcher.group();
          String[] matchParts = matchedText.split(Pattern.quote(matchedFull));
          if (matchParts.length > 0) {
            if (matchParts[0].endsWith("&")) {
              componentText.setText(
                  CMIChatColor.translate(matchParts[0]) + matchedFull.substring(0, 1));
              matchedFull = matchedFull.substring(1);
            } else {
              componentText.setText(CMIChatColor.translate(matchParts[0]));
            }
          }

          cmiComponentBase.add(componentText);
          componentText = new CMIComponentText();
          componentText.setText(CMIChatColor.translate(matchedFull));
          componentText.setClickAction(CMIComponentActionType.open_url, matchedFull);
          cmiComponentBase.add(componentText);
          componentText = new CMIComponentText();
          if (matchParts.length > 1) {
            componentText.setText(CMIChatColor.translate(matchParts[1]));
            cmiComponentBase.add(componentText);
            componentText = new CMIComponentText();
          }
        } else {
          componentText.setText(CMIChatColor.translate(matchedText));
        }
      }

      if (pageLine.contains("<H>")) {
        componentText.setHoverText(pageLine.replaceAll(".*\\<H>|\\</H>.*", ""));
      }

      String beforeText = null;
      if (pageLine.contains("<C>")) {
        beforeText = pageLine.replaceAll(".*\\<C>|\\</C>.*", "");
      }

      if (pageLine.contains("<URL>")) {
        componentText.setClickAction(
            CMIComponentActionType.open_url, pageLine.replaceAll(".*\\<URL>|\\</URL>.*", ""));
      }

      String afterText = null;
      if (pageLine.contains("<CC>") && commandSender != null && commandSender instanceof Player) {
        Player player = (Player) commandSender;
        afterText = pageLine.replaceAll(".*\\<CC>|\\</CC>.*", "");
        String replacement =
            ShadowCommand.addShadowCmd(player, afterText, false, ShadowCommandType.Console);
        beforeText =
            CommandsHandler.getLabel() + " shadowcmd " + replacement + " (" + afterText + ")";
        if (flag) {
          beforeText = afterText;
        }
      }

      if (pageLine.contains("<CCI>") && commandSender != null && commandSender instanceof Player) {
        Player player = (Player) commandSender;
        afterText = pageLine.replaceAll(".*\\<CCI>|\\</CCI>.*", "");
        String replaced =
            ShadowCommand.addShadowCmd(player, afterText, true, ShadowCommandType.Console);
        beforeText = CommandsHandler.getLabel() + " shadowcmd " + replaced + " (" + afterText + ")";
        if (flag) {
          beforeText = afterText;
        }
      }

      if (pageLine.contains("<SC>")) {
        componentText.setClickAction(
            CMIComponentActionType.suggest_command, pageLine.replaceAll(".*\\<SC>|\\</SC>.*", ""));
      }

      if (pageLine.contains("<SI>")) {
        componentText.setClickAction(
            CMIComponentActionType.copy_to_clipboard,
            pageLine.replaceAll(".*\\<SI>|\\</SI>.*", ""));
      }

      if (beforeText != null) {
        componentText.setClickAction(CMIComponentActionType.run_command, beforeText);
      }

      cmiComponentBase.add(componentText);
    }

    return cmiComponentBase;
  }

  public boolean isHidden() {
    return this.hidden;
  }

  public void setHidden(boolean flag) {
    this.hidden = flag;
  }

  public String getPath() {
    return this.path;
  }

  public void setPath(String string) {
    if (string.startsWith("/")) {
      string = string.substring(1);
    }

    this.path = string;
  }

  public class bookPage {
    private boolean json = false;
    private String lines;

    public bookPage(String string) {
      this.lines = string;
    }

    public bookPage(boolean flag, String string) {
      this.json = flag;
      this.lines = string;
    }

    public boolean isJson() {
      return this.json;
    }

    public void setJson(boolean flag) {
      this.json = flag;
    }

    public String getLines() {
      return this.lines;
    }

    public void setLines(String string) {
      this.lines = string;
    }
  }
}
