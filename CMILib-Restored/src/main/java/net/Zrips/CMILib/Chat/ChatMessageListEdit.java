package net.Zrips.CMILib.Chat;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.Zrips.CMILib.CMILib;
import net.Zrips.CMILib.Colors.CMIChatColor;
import net.Zrips.CMILib.Container.CMILocation;
import net.Zrips.CMILib.Container.CMIText;
import net.Zrips.CMILib.Container.PageInfo;
import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.RawMessages.RawMessage;
import net.Zrips.CMILib.RawMessages.RawMessageCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ChatMessageListEdit {
  private UUID uuid = null;
  Long time = 0L;
  private boolean delRequiresConfirmation = true;
  private boolean flatenLines = false;
  private boolean identifyEmptylines = false;
  private boolean fillWithEmpty = false;
  private List<Object> lines = new ArrayList<>();
  private List<String> modifyInfo = new ArrayList<>();
  private RawMessage topLine;
  private RawMessage bottomLine;
  private ChatMessageListEdit.ChatEditType type = ChatMessageListEdit.ChatEditType.String;
  private int maxSize = 0;
  private boolean addNew = true;
  private PageInfo pi = null;
  private int shortenLines = 0;

  public ChatMessageListEdit(
      CommandSender sender, Object lines, ChatMessageListEdit.ChatEditType type, int maxSize) {
    this(
        sender instanceof Player
            ? ((Player) sender).getUniqueId()
            : CMILib.getInstance().getServerUUID(),
        lines,
        type,
        maxSize);
  }

  public ChatMessageListEdit(
      CommandSender sender, Object lines, ChatMessageListEdit.ChatEditType type) {
    this(
        sender instanceof Player
            ? ((Player) sender).getUniqueId()
            : CMILib.getInstance().getServerUUID(),
        lines,
        type,
        0);
  }

  public ChatMessageListEdit(UUID uuid, Object lines, ChatMessageListEdit.ChatEditType type) {
    this(uuid, lines, type, 0);
  }

  public ChatMessageListEdit(
      UUID uuid, Object lines, ChatMessageListEdit.ChatEditType type, int maxSize) {
    this.uuid = uuid;
    this.lines = (List<Object>) lines;
    this.type = type;
    this.maxSize = maxSize;
    if (type == null && !this.lines.isEmpty()) {
      type =
          this.lines.get(0) instanceof String
              ? ChatMessageListEdit.ChatEditType.String
              : ChatMessageListEdit.ChatEditType.Location;
    }

    this.time = System.currentTimeMillis();
  }

  public void onUpdate() {}

  public void beforePrint() {}

  public void clicked(int place) {}

  public UUID getUuid() {
    return this.uuid;
  }

  public void setUuid(UUID uuid) {
    this.uuid = uuid;
  }

  public void print() {
    this.beforePrint();
    CommandSender sender = Bukkit.getPlayer(this.uuid);
    if (this.uuid == CMILib.getInstance().getServerUUID()) {
      sender = Bukkit.getConsoleSender();
    }

    if (this.topLine != null) {
      this.topLine.show(sender);
    }

    for (int i = 0; i < this.lines.size(); i++) {
      if (this.pi == null || this.pi.isEntryOk()) {
        if (this.pi != null && this.pi.isBreak()) {
          break;
        }

        RawMessage rm = new RawMessage();
        final int fxedI = i;
        RawMessageCommand rmc =
            new RawMessageCommand() {
              @Override
              public void run(CommandSender sender) {
                if (ChatMessageListEdit.this.delRequiresConfirmation) {
                  RawMessage confirmationRm = new RawMessage();
                  RawMessageCommand confirmationRmc =
                      new RawMessageCommand() {
                        @Override
                        public void run(CommandSender sender) {
                          ChatMessageListEdit.this.remove(fxedI);
                          ChatMessageListEdit.this.print();
                        }
                      };
                  confirmationRm.addText(
                      LC.info_ClickToConfirmDelete.getLocale("[name]", fxedI + 1));
                  confirmationRm.addHover(LC.info_Click.getLocale());
                  confirmationRm.addCommand(confirmationRmc);
                  confirmationRm.show(sender);
                } else {
                  ChatMessageListEdit.this.remove(fxedI);
                  ChatMessageListEdit.this.print();
                }
              }
            };
        rm.addText(LC.modify_deleteSymbol.getLocale() + " ")
            .addHover(LC.modify_deleteSymbolHover.getLocale("[text]", i + 1))
            .addCommand(rmc.getCommand());
        rm.addText(
            LC.modify_listNumbering.getLocale(
                "[number]",
                (i < 9
                        ? LC.modify_listAlign.getLocale()
                            + CMIChatColor.getLastColors(LC.modify_listNumbering.getLocale())
                        : "")
                    + (i + 1)));
        rmc =
            new RawMessageCommand() {
              @Override
              public void run(CommandSender sender) {
                ChatMessageListEdit.this.move(fxedI, ChatMessageListEdit.dir.up);
                ChatMessageListEdit.this.print();
              }
            };
        rm.addText(LC.modify_listUpSymbol.getLocale())
            .addHover(LC.modify_listUpSymbolHover.getLocale())
            .addCommand(rmc.getCommand());
        rmc =
            new RawMessageCommand() {
              @Override
              public void run(CommandSender sender) {
                ChatMessageListEdit.this.move(fxedI, ChatMessageListEdit.dir.down);
                ChatMessageListEdit.this.print();
              }
            };
        rm.addText(LC.modify_listDownSymbol.getLocale())
            .addHover(LC.modify_listDownSymbolHover.getLocale())
            .addCommand(rmc.getCommand());
        rm.addText(" ");
        final int ii = i;
        if (!this.type.equals(ChatMessageListEdit.ChatEditType.String)) {
          if (this.type.equals(ChatMessageListEdit.ChatEditType.Location)) {
            rm.addText(
                LC.modify_editLineColor.getLocale()
                    + CMILocation.toString((CMILocation) this.lines.get(i)));
            rm.addHover(LC.info_ClickToPaste.getLocale());
            rm.addSuggestion(CMILocation.toString((CMILocation) this.lines.get(i)));
          }
        } else {
          rmc =
              new RawMessageCommand() {
                @Override
                public void run(final CommandSender sender) {
                  ChatMessageEdit chatEdit =
                      new ChatMessageEdit(sender) {
                        @Override
                        public void run(String message) {
                          if (CMIText.isValidString(CMIChatColor.deColorize(message))) {
                            if (ChatMessageListEdit.this.maxSize > 0
                                && ChatMessageListEdit.this.lines.size()
                                    >= ChatMessageListEdit.this.maxSize
                                && ii + 1 > ChatMessageListEdit.this.maxSize) {
                              LC.modify_listLimit.sendMessage(
                                  sender, "[amount]", ChatMessageListEdit.this.maxSize);
                            } else {
                              while (ChatMessageListEdit.this.lines.size() <= ii) {
                                ChatMessageListEdit.this.lines.add("");
                              }

                              ChatMessageListEdit.this.lines.set(ii, message);
                              ChatMessageListEdit.this.onUpdate();
                            }
                          }
                        }

                        @Override
                        public void onDisable() {
                          ChatMessageListEdit.this.print();
                        }
                      };
                  chatEdit.setCheckForCancel(true);
                  RawMessage rm = new RawMessage();
                  String hover = LC.info_ClickToPaste.getLocale();

                  for (String one : ChatMessageListEdit.this.modifyInfo) {
                    hover = hover + "\n" + one;
                  }

                  rm.addText(LC.modify_commandEditInfo.getLocale()).addHover(hover);
                  if (ChatMessageListEdit.this.lines.size() > ii) {
                    rm.addSuggestion(
                        CMIChatColor.deColorize(
                            (String) ChatMessageListEdit.this.lines.get(ii), false));
                  }

                  rm.show(sender);
                }
              };
          String text = (String) this.lines.get(i);
          if (this.isFlatenLines()) {
            text = CMIChatColor.flaten(text, false);
          }

          if (this.getShortenLines() > 0) {
            text = CMIText.truncate(text, this.getShortenLines(), "...");
          }

          if (!this.isIdentifyEmptylines() || !text.isEmpty() && !text.equalsIgnoreCase(" ")) {
            rm.addText(LC.modify_editLineColor.getLocale() + text);
          } else {
            rm.addText(LC.modify_emptyLine.getLocale());
          }

          rm.addHover(LC.modify_editSymbolHover.getLocale("[text]", i + 1))
              .addCommand(rmc.getCommand());
        }

        rm.show(sender);
      }
    }

    if (this.isAddNew()) {
      RawMessage rm = new RawMessage();
      if (this.maxSize <= 0 || this.lines.size() < this.maxSize) {
        RawMessageCommand rmc =
            new RawMessageCommand() {
              @Override
              public void run(CommandSender sender) {
                if (ChatMessageListEdit.this.maxSize > 0
                    && ChatMessageListEdit.this.lines.size() >= ChatMessageListEdit.this.maxSize) {
                  LC.modify_listLimit.sendMessage(
                      sender, "[amount]", ChatMessageListEdit.this.maxSize);
                } else {
                  switch (ChatMessageListEdit.this.type) {
                    case String:
                      ChatMessageEdit chatEdit =
                          new ChatMessageEdit(sender) {
                            @Override
                            public void run(String message) {
                              if (CMIText.isValidString(CMIChatColor.deColorize(message))) {
                                ChatMessageListEdit.this.lines.add(message);
                                ChatMessageListEdit.this.onUpdate();
                              }
                            }

                            @Override
                            public void onDisable() {
                              ChatMessageListEdit.this.print();
                            }
                          };
                      chatEdit.setCheckForCancel(true);
                      LC.modify_lineAddInfo.sendMessage(sender);
                      break;
                    case Location:
                      Player player = Bukkit.getPlayer(ChatMessageListEdit.this.uuid);
                      ChatMessageListEdit.this.lines.add(new CMILocation(player.getLocation()));
                      ChatMessageListEdit.this.onUpdate();
                      ChatMessageListEdit.this.print();
                  }
                }
              }
            };
        rm.addText(" " + LC.modify_addSymbol.getLocale() + " ")
            .addHover(LC.modify_addSymbolHover.getLocale())
            .addCommand(rmc.getCommand());
        rm.addText(
            LC.modify_listNumbering.getLocale(
                "[number]",
                (this.lines.size() < 9
                        ? LC.modify_listAlign.getLocale()
                            + CMIChatColor.getLastColors(LC.modify_listNumbering.getLocale())
                        : "")
                    + (this.lines.size() + 1)));
      }

      rm.show(sender);
    }

    if (this.bottomLine != null) {
      this.bottomLine.show(sender);
    }

    if (this.pi != null) {
      this.pi.autoPagination(sender);
    }
  }

  private void remove(int place) {
    if (this.lines.size() >= place) {
      if (this.isFillWithEmpty()) {
        this.lines.set(place, "");
      } else {
        this.lines.remove(place);
      }

      this.onUpdate();
    }
  }

  private void move(int from, ChatMessageListEdit.dir direction) {
    int to = from + direction.getValue();
    Object tocmd = null;
    Object fromcmd = null;
    if (this.lines.size() >= to + 1 && to >= 0) {
      tocmd = this.lines.get(to);
    }

    if (this.lines.size() >= from + 1 && from >= 0) {
      fromcmd = this.lines.get(from);
    }

    if (tocmd != null && fromcmd != null) {
      this.lines.set(to, fromcmd);
      this.lines.set(from, tocmd);
    }

    this.onUpdate();
  }

  public List<Object> getLines() {
    return this.lines;
  }

  public void setLines(List<Object> lines) {
    this.lines = lines;
  }

  public RawMessage getTopLine() {
    return this.topLine;
  }

  public void setTopLine(RawMessage topLine) {
    this.topLine = topLine;
  }

  public List<String> getModifyInfo() {
    return this.modifyInfo;
  }

  public void setModifyInfo(List<String> modifyInfo) {
    this.modifyInfo = modifyInfo;
  }

  public boolean isFlatenLines() {
    return this.flatenLines;
  }

  public void setFlatenLines(boolean flatenLines) {
    this.flatenLines = flatenLines;
  }

  public boolean isAddNew() {
    return this.addNew;
  }

  public void setAddNew(boolean addNew) {
    this.addNew = addNew;
  }

  public RawMessage getBottomLine() {
    return this.bottomLine;
  }

  public void setBottomLine(RawMessage bottomLine) {
    this.bottomLine = bottomLine;
  }

  public boolean isIdentifyEmptylines() {
    return this.identifyEmptylines;
  }

  public void setIdentifyEmptylines(boolean identifyEmptylines) {
    this.identifyEmptylines = identifyEmptylines;
  }

  public boolean isFillWithEmpty() {
    return this.fillWithEmpty;
  }

  public void setFillWithEmpty(boolean fillWithEmpty) {
    this.fillWithEmpty = fillWithEmpty;
  }

  public PageInfo getPageInfo() {
    return this.pi;
  }

  public void setPageInfo(PageInfo pi) {
    this.pi = pi;
  }

  public int getShortenLines() {
    return this.shortenLines;
  }

  public void setShortenLines(int shortenLines) {
    this.shortenLines = shortenLines;
  }

  public enum ChatEditType {
    String,
    Location;
  }

  private enum dir {
    up(-1),
    down(1);

    private int val;

    dir(int val) {
      this.val = val;
    }

    public int getValue() {
      return this.val;
    }
  }
}
