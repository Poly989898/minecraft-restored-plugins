package com.Zrips.CMI.Modules.CustomText;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Config;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Containers.Snd;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.ChatFilter.ChatFilterRule;
import com.Zrips.CMI.commands.CommandsHandler;
import com.Zrips.CMI.commands.list.ctext;
import com.Zrips.CMI.commands.list.editctext;
import com.Zrips.CMI.utils.CMIThread;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.Zrips.CMILib.Chat.ChatMessageEdit;
import net.Zrips.CMILib.Chat.ChatMessageListEdit;
import net.Zrips.CMILib.Chat.ChatMessageListEdit.ChatEditType;
import net.Zrips.CMILib.Colors.CMIChatColor;
import net.Zrips.CMILib.Container.CMIText;
import net.Zrips.CMILib.Container.PageInfo;
import net.Zrips.CMILib.Images.CMIImage;
import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.RawMessages.RawMessage;
import net.Zrips.CMILib.RawMessages.RawMessageCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CTextManager {
  private Map<String, CText> map = Collections.synchronizedMap(new LinkedHashMap<>());
  private HashMap<UUID, String> chatMap = new HashMap<>();
  private CMI plugin;
  private ChatFilterRule pageLabelRegex =
      new ChatFilterRule().setPattern("^(?i)(<pageLabel)(:)?([^>]*)(>)$");
  public static final String directoryName = "CustomText";

  public CTextManager(CMI cmi) {
    this.plugin = cmi;
  }

  public Map<String, CText> getAll() {
    return this.map;
  }

  public void deleteCText(String string) {
    this.map.remove(string.toLowerCase());
  }

  public CText getCText(String string) {
    return string == null ? null : this.map.get(string.toLowerCase());
  }

  public boolean isChatEditing(Player player) {
    return this.chatMap.containsKey(player.getUniqueId());
  }

  public void addChatEditor(Player player, String string) {
    if (player != null) {
      if (!string.endsWith(" ")) {
        string = string + " ";
      }

      this.chatMap.put(player.getUniqueId(), string);
    }
  }

  public void removeChatEditor(Player player) {
    this.chatMap.remove(player.getUniqueId());
  }

  public String getChatEditorCmd(Player player) {
    return this.chatMap.get(player.getUniqueId());
  }

  public CText addNewCText(String string) {
    CText cText = new CText(string);
    this.map.put(string.toLowerCase(), cText);
    return cText;
  }

  public void load() {
    CompletableFuture.runAsync(
        () -> {
          Long startTime = System.currentTimeMillis();
          this.map.clear();
          File file = new File(this.plugin.getDataFolder(), "CustomText");
          if (!file.isDirectory()) {
            file.mkdir();
          } else {
            this.loadDirectory(file);
            if (!this.map.isEmpty()) {
              this.plugin.loadMessage(
                  this.map.size(), "custom text's", System.currentTimeMillis() - startTime);
            }
          }
        },
        CMIThread.getExecutor());
  }

  private void loadDirectory(File file) {
    String string = "";

    try {
      string =
          file.getPath()
              .substring(
                  ("plugins"
                          + File.separator
                          + CommandsHandler.getLabel()
                          + File.separator
                          + "CustomText")
                      .length());
    } catch (Throwable throwable) {
      throwable.printStackTrace();
    }

    File[] files = file.listFiles();
    File[] fileList = files;
    int value = files.length;

    for (int index = 0; index < value; index++) {
      File textFile = fileList[index];
      if (textFile.isDirectory()) {
        this.loadDirectory(textFile);
      } else {
        CText cText = this.getCTextFromFile(textFile);
        if (cText != null) {
          cText.setPath(string);
          String name = textFile.getName().replace(".txt", "");
          this.map.put(name.toLowerCase(), cText);
        }
      }
    }
  }

  public CText getCTextFromFile(File file) {
    if (!file.getName().endsWith(".txt") && !file.getName().endsWith(".yml")) {
      return null;
    }

    ArrayList<String> arrayList = new ArrayList();
    BufferedReader bufferedReader = null;

    try {
      bufferedReader =
          new BufferedReader(
              new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));

      for (String string = bufferedReader.readLine();
          string != null;
          string = bufferedReader.readLine()) {
        arrayList.add(string);
      }
    } catch (IOException ioException) {
      ioException.printStackTrace();
    } finally {
      if (bufferedReader != null) {
        try {
          bufferedReader.close();
        } catch (IOException ioException2) {
          ioException2.printStackTrace();
        }
      }
    }

    String name = file.getName().replace(".txt", "");
    CText cText = new CText(name);
    int value = 1;
    int lineCount = 0;
    Iterator iterator = arrayList.iterator();

    while (true) {
      String line;
      boolean flag;
      do {
        if (!iterator.hasNext()) {
          return cText;
        }

        line = (String) iterator.next();
        if (++lineCount != 1) {
          break;
        }

        flag = false;
        if (line.contains("<AutoPage>")) {
          cText.setAutoPage(true);
          flag = true;
        }

        if (line.contains("<Hidden>")) {
          cText.setHidden(true);
          flag = true;
        }

        if (line.contains("<NoAutoPage>")) {
          cText.setAutoPage(false);
          flag = true;
        }

        if (line.contains("<AutoAlias>")) {
          cText.setAutoAlias(true, false);
          flag = true;
        }

        if (line.contains("<ReqPermission>")) {
          cText.setRequirePermission(true);
          flag = true;
        }

        if (line.contains("<NoAutoAlias>")) {
          cText.setAutoAlias(false, false);
          flag = true;
        }
      } while (flag);

      if (line.equalsIgnoreCase("<NextPage>")) {
        value++;
      } else {
        Matcher matcher = this.pageLabelRegex.getMatcher(line);
        if (matcher != null) {
          while (matcher.find()) {
            String token = matcher.group(3);
            if (token != null && !token.isEmpty()) {
              cText.addPageLabel(value, token);
            }
          }
        } else {
          cText.addLine(value, line);
        }
      }
    }
  }

  private void createDirectories(String string) {
    if (!string.replace(File.separator, "").isEmpty()) {
      String[] strings = string.split(Pattern.quote(File.separator));
      string = "";
      String[] argsCopy = strings;
      int value = strings.length;

      for (int index = 0; index < value; index++) {
        String arg = argsCopy[index];
        if (!string.isEmpty()) {
          string = string + File.separator;
        }

        string = string + arg;
        File file = new File(this.plugin.getDataFolder(), "CustomText" + File.separator + string);
        if (!file.isDirectory()) {
          file.mkdir();
        }
      }
    }
  }

  public void save(CText cText) {
    File file = new File(this.plugin.getDataFolder(), "CustomText");
    if (!file.isDirectory()) {
      file.mkdir();
    }

    this.createDirectories(cText.getPath());
    File newFile =
        new File(
            file,
            (cText.getPath().isEmpty() ? "" : cText.getPath() + File.separator)
                + cText.getName()
                + ".txt");
    ArrayList<String> arrayList = new ArrayList();
    String string = "";
    if (cText.isAutoPage()) {
      string = string + "<AutoPage>";
    } else {
      string = string + "<NoAutoPage>";
    }

    if (cText.isHidden()) {
      string = string + "<Hidden>";
    }

    if (cText.isAutoAlias()) {
      string = string + "<AutoAlias>";
    } else {
      string = string + "<NoAutoAlias>";
    }

    if (cText.isRequirePermission()) {
      string = string + "<ReqPermission>";
    } else {
      string = string + "<NoReqPermission>";
    }

    arrayList.add(string);

    for (var entry : cText.getPagesMap().entrySet()) {
      if (arrayList.size() > 1) {
        arrayList.add("<NextPage>");
      }

      if (((CTextPage) entry.getValue()).getLabel() != null) {
        arrayList.add("<pageLabel:" + ((CTextPage) entry.getValue()).getLabel() + ">");
      }

      arrayList.addAll(((CTextPage) entry.getValue()).getLines());
    }

    try {
      Files.write(newFile.toPath(), arrayList, StandardCharsets.UTF_8);
    } catch (IOException ioException) {
    }
  }

  public void delete(CText cText) {
    if (cText.isAutoAlias()) {
      cText.setAutoAlias(false, true);
    }

    File file = new File(this.plugin.getDataFolder(), "CustomText");
    if (!file.isDirectory()) {
      file.mkdir();
    } else {
      File targetFile =
          new File(
              file,
              (cText.getPath().isEmpty() ? "" : cText.getPath() + File.separator)
                  + cText.getName()
                  + ".txt");
      if (targetFile.isFile()) {
        targetFile.delete();
      }
    }
  }

  public void removeCTextLine(CText cText, Integer integer, Integer endLine) {
    CTextPage cTextPage = cText.getPageInfo(integer);
    if (cTextPage != null && endLine >= 0 && endLine + 1 <= cTextPage.getLines().size()) {
      cTextPage.getLines().remove(endLine.intValue());
      cText.addPage(integer.intValue(), cTextPage);
    }
  }

  public List<String> move(List<String> list, int value, int to) {
    int from = value;
    int target = from + to;
    String string = "";
    String joined = "";
    if (list.size() >= target + 1 && target >= 0) {
      string = (String) list.get(target);
    }

    if (list.size() >= from + 1 && from >= 0) {
      joined = (String) list.get(from);
    }

    if (!string.isEmpty() && !joined.isEmpty()) {
      list.set(target, joined);
      list.set(from, string);
    }

    return list;
  }

  public void showCTextEditor(CommandSender commandSender, final CText cText, Integer integer) {
    if (integer < 1) {
      integer = 1;
    }

    CTextPage cTextPage = cText.getPageInfo(integer);
    if (cTextPage == null && integer > 1) {
      integer = cText.getTotalPages() - 1;
      cTextPage = cText.getPageInfo(integer);
    }

    RawMessage rawMessage = new RawMessage();
    rawMessage
        .addText(
            cText.isAutoPage()
                ? LC.modify_enabledSymbol.getLocale(new Object[0])
                : LC.modify_disabledSymbol.getLocale(new Object[0]))
        .addHover(CMILC.getIM(editctext.class, "autoPage", "[state]", cText.isAutoPage()))
        .addCommand("cmi editctext " + cText.getName() + " autopage " + !cText.isAutoPage());
    rawMessage.addText(" ");
    rawMessage
        .addText(
            cText.isAutoAlias()
                ? LC.modify_enabledSymbol.getLocale(new Object[0])
                : LC.modify_disabledSymbol.getLocale(new Object[0]))
        .addHover(CMILC.getIM(editctext.class, "autoAlias", "[state]", cText.isAutoAlias()))
        .addCommand("cmi editctext " + cText.getName() + " autoalias " + !cText.isAutoAlias());
    rawMessage.addText(" ");
    rawMessage
        .addText(
            cText.isRequirePermission()
                ? LC.modify_enabledSymbol.getLocale(new Object[0])
                : LC.modify_disabledSymbol.getLocale(new Object[0]))
        .addHover(
            CMILC.getIM(editctext.class, "permReq", "[state]", cText.isRequirePermission())
                + "\n"
                + "&ecmi.command.ctext."
                + cText.getName())
        .addCommand(
            "cmi editctext " + cText.getName() + " permreg " + !cText.isRequirePermission());
    rawMessage.addText(" ");
    rawMessage
        .addText(
            cText.isHidden()
                ? LC.modify_enabledSymbol.getLocale(new Object[0])
                : LC.modify_disabledSymbol.getLocale(new Object[0]))
        .addHover(CMILC.getIM(editctext.class, "hidden", "[state]", cText.isHidden()))
        .addCommand("cmi editctext " + cText.getName() + " hidden " + !cText.isHidden());
    rawMessage.addText(" ");
    rawMessage
        .addText(LC.modify_newPage.getLocale(new Object[0]))
        .addHover(LC.modify_newPageHover.getLocale(new Object[0]))
        .addCommand("cmi editctext " + cText.getName() + " " + integer + " newpage");
    if (cTextPage != null) {
      final CTextPage page = cTextPage;
      final Integer pageIndex = integer;
      RawMessageCommand rawMessageCommand =
          new RawMessageCommand() {
            public void run(final CommandSender sender) {
              ChatMessageEdit chatMessageEdit =
                  new ChatMessageEdit(sender, page.getLabel() == null ? "" : page.getLabel()) {
                    public void run(String string) {
                      if (CMIText.isValidString(string)) {
                        if (!string.isEmpty()
                            && !string.equals("_")
                            && !string.equalsIgnoreCase("delete")) {
                          page.setLabel(string);
                        } else {
                          page.setLabel(null);
                        }

                        CTextManager.this.showCTextEditor(sender, cText, pageIndex);
                        CTextManager.this.save(cText);
                      }
                    }

                    public void onCancel() {
                      CTextManager.this.showCTextEditor(sender, cText, pageIndex);
                    }
                  };
              chatMessageEdit.setCheckForCancel(true);
              chatMessageEdit.printMessage();
            }
          };
      rawMessage
          .addText(CMILC.getIM(editctext.class, "pageLabel"))
          .addHover(
              CMILC.getIM(
                  editctext.class,
                  "pageLabelHover",
                  "[value]",
                  cTextPage.getLabel() == null ? "N/A" : cTextPage.getLabel()))
          .addCommand(rawMessageCommand);
    }

    rawMessage.addText("  ");
    rawMessage
        .addText(LC.modify_removePage.getLocale(new Object[0]))
        .addHover(LC.modify_removePageHover.getLocale(new Object[0]))
        .addCommand("cmi editctext " + cText.getName() + " " + integer + " removepage");
    rawMessage.show(commandSender);
    if (cTextPage != null) {
      ChatMessageListEdit chatMessageListEdit =
          new ChatMessageListEdit(commandSender, cTextPage.getLines(), ChatEditType.String) {
            public void onUpdate() {
              CTextManager.this.save(cText);
            }
          };
      chatMessageListEdit.print();
    }

    PageInfo pageInfo =
        new PageInfo(
            cTextPage != null ? cTextPage.getLines().size() : 10,
            (cTextPage != null ? cTextPage.getLines().size() : 10) * cText.getTotalPages(),
            integer);

    try {
      pageInfo.setCustomNext(
          cText.getPageInfo(integer + 1 > cText.getTotalPages() ? 1 : integer + 1).getLabel());
      pageInfo.setCustomPrev(
          cText.getPageInfo(integer - 1 < 1 ? cText.getTotalPages() : integer - 1).getLabel());
    } catch (Throwable throwable) {
    }

    pageInfo.autoPagination(
        commandSender,
        CommandsHandler.getLabel() + " " + editctext.class.getSimpleName() + " " + cText.getName(),
        "-p:");
  }

  public void showCText(CommandSender commandSender, CText cText, Integer integer) {
    this.showCText(commandSender, null, cText, integer);
  }

  public void showCText(
      CommandSender commandSender, CMIUser cmiUser, CText cText, Integer integer) {
    if (commandSender instanceof Player) {
      HashSet<Player> hashSet = new HashSet();
      hashSet.add((Player) commandSender);
      this.showCText(hashSet, cmiUser, cText, integer);
    }
  }

  public void showCText(Set<Player> set, CMIUser cmiUser, CText cText, Integer integer) {
    CTextPage cTextPage = cText.getPageInfo(integer);
    if (cTextPage != null) {
      boolean flag = true;
      Boolean enabledFlag = null;

      for (Player player : set) {
        Snd snd = new Snd().setSender(player).setSource(cmiUser).setTarget(cmiUser);
        if (cmiUser == null) {
          snd.setSource(player);
          snd.setTarget(player);
        }

        ArrayList<String> arrayList = new ArrayList();

        for (String string : cTextPage.getLines()) {
          String text = string;
          text = this.plugin.getLM().updateSnd(snd, text);
          if (enabledFlag == null) {
            if (!string.isEmpty() && string.equals(text)) {
              enabledFlag = true;
            } else {
              enabledFlag = false;
            }
          }

          text =
              this.plugin
                  .getPlaceholderAPIManager()
                  .updatePlaceHolders(
                      cmiUser == null ? player.getUniqueId() : cmiUser.getUniqueId(), text);
          if (enabledFlag && !CMIChatColor.stripColor(string).isEmpty() && !string.equals(text)) {
            enabledFlag = false;
          }

          arrayList.add(text);
        }

        CompletableFuture<RawMessage> completableFuture =
            CMIImage.convertIntoRawMessage(player, Config.imageFolder, arrayList, false);
        completableFuture.thenApply(
            object -> {
              object.show(player);
              if (!cText.isAutoPage()) {
                return (RawMessage) object;
              }

              PageInfo pageInfo =
                  new PageInfo(1, cText.getTotalPages(), integer) {
                    public Boolean pageChange(int value) {
                      CTextManager.this.showCText(player, cmiUser, cText, value);
                      return true;
                    }
                  };

              try {
                pageInfo.setCustomNext(
                    cText
                        .getPageInfo(integer + 1 > cText.getTotalPages() ? 1 : integer + 1)
                        .getLabel());
                pageInfo.setCustomPrev(
                    cText
                        .getPageInfo(integer - 1 < 1 ? cText.getTotalPages() : integer - 1)
                        .getLabel());
              } catch (Throwable throwable) {
              }

              pageInfo.autoPagination(
                  player,
                  CommandsHandler.getLabel()
                      + " "
                      + ctext.class.getSimpleName()
                      + " "
                      + cText.getName(),
                  null);
              return (RawMessage) object;
            });
      }
    }
  }

  public static String clean(String string) {
    if (string != null && string.contains("<")) {
      string = string.replaceAll(".*\\<T>|\\</T>.*", "");
      string = string.replaceAll(".*\\<H>|\\</H>.*", "");
      string = string.replaceAll(".*\\<C>|\\</C>.*", "");
      string = string.replaceAll(".*\\<CC>|\\</CC>.*", "");
      return string.replaceAll(".*\\<CCI>|\\</CCI>.*", "");
    } else {
      return string;
    }
  }
}
