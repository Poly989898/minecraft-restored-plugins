package net.Zrips.CMILib.Container;

import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.Messages.CMIMessages;
import net.Zrips.CMILib.RawMessages.RawMessage;
import net.Zrips.CMILib.RawMessages.RawMessageCommand;
import net.Zrips.CMILib.commands.CommandsHandler;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PageInfo {
  private int totalEntries = 0;
  private int totalPages = 0;
  private int start = 0;
  private int end = 0;
  private int currentPage = 0;
  private int currentEntry = 0;
  private int perPage = 6;
  private String customPrev = null;
  private String customNext = null;
  private String cmd = null;
  private String pagePref = null;

  public PageInfo(int perPage, int totalEntries, int currentPage) {
    this.perPage = perPage;
    this.totalEntries = totalEntries;
    this.currentPage = currentPage < 1 ? 1 : currentPage;
    this.calculate();
  }

  public int getPositionForOutput() {
    return this.currentEntry;
  }

  public int getPositionForOutput(int place) {
    return this.start + place + 1;
  }

  private void calculate() {
    this.currentEntry = 0;
    this.start = (this.currentPage - 1) * this.perPage;
    this.end = this.start + this.perPage - 1;
    if (this.end + 1 > this.totalEntries) {
      this.end = this.totalEntries - 1;
    }

    this.totalPages = (int) Math.ceil((double) this.totalEntries / this.perPage);
  }

  public boolean isInRange(int place) {
    return place >= this.start && place <= this.end;
  }

  public boolean isEntryOk() {
    this.currentEntry++;
    return this.currentEntry - 1 >= this.start && this.currentEntry - 1 <= this.end;
  }

  public boolean isContinue() {
    return !this.isEntryOk();
  }

  public boolean isContinueNoAdd() {
    return this.currentEntry - 1 >= this.start && this.currentEntry - 1 <= this.end;
  }

  public boolean isBreak() {
    return this.currentEntry - 1 > this.end;
  }

  public boolean isPageOk() {
    return this.isPageOk(this.currentPage);
  }

  public boolean isPageOk(int page) {
    return this.totalPages < page ? false : page >= 1;
  }

  public int getStart() {
    return this.start;
  }

  public int getEnd() {
    return this.end;
  }

  public int getTotalPages() {
    return this.totalPages;
  }

  public void setTotalPages(int totalPages) {
    this.totalPages = totalPages;
  }

  public int getCurrentPage() {
    return this.currentPage;
  }

  public int getTotalEntries() {
    return this.totalEntries;
  }

  public int getNextPageNumber() {
    return this.getCurrentPage() + 1 > this.getTotalPages()
        ? this.getTotalPages()
        : this.getCurrentPage() + 1;
  }

  public int getPrevPageNumber() {
    return this.getCurrentPage() - 1 < 1 ? 1 : this.getCurrentPage() - 1;
  }

  public Boolean pageChange(int page) {
    return null;
  }

  public PageInfo setCurrentPage(int currentPage) {
    this.currentPage = currentPage;
    this.calculate();
    return this;
  }

  @Deprecated
  public void ShowPagination(CommandSender sender, String cmd) {
    this.ShowPagination(sender, cmd, null);
  }

  @Deprecated
  public void ShowPagination(CMICommandSender sender, String cmd) {
    this.ShowPagination(sender.getSender(), cmd, null);
  }

  @Deprecated
  public void ShowPagination(CommandSender sender, Object clas, String pagePref) {
    this.ShowPagination(
        sender, CommandsHandler.getLabel() + " " + clas.getClass().getSimpleName(), pagePref);
  }

  @Deprecated
  public void ShowPagination(CommandSender sender, String cmd, String pagePref) {
    this.autoPagination(sender, cmd, pagePref);
  }

  public void autoPagination(CMICommandSender sender, String cmd) {
    this.autoPagination(sender.getSender(), cmd, null);
  }

  public void autoPagination(CMICommandSender sender, String cmd, String pagePref) {
    this.autoPagination(sender.getSender(), cmd, pagePref);
  }

  public void autoPagination(CommandSender sender, String cmd) {
    this.autoPagination(sender, cmd, null);
  }

  public void autoPagination(CommandSender sender) {
    this.autoPagination(sender, null, null);
  }

  public void autoPagination(CommandSender sender, String cmd, String pagePref) {
    if (cmd == null) {
      cmd = this.getPageChangeCommand();
    }

    if (cmd != null) {
      if (this.getTotalPages() != 1) {
        if (pagePref == null) {
          pagePref = this.getPageChangeCommandPref();
        }

        String pagePrefix = pagePref == null ? "" : pagePref;
        final int nextPage =
            this.getCurrentPage() < this.getTotalPages() ? this.getCurrentPage() + 1 : 1;
        final int prevpage =
            this.getCurrentPage() > 1 ? this.getCurrentPage() - 1 : this.getTotalPages();
        if (!(sender instanceof Player)) {
          CMIMessages.sendMessage(
              sender,
              LC.info_nextPageConsole,
              "[command]",
              cmd.replace("/", "") + " " + pagePrefix + nextPage);
        } else {
          RawMessage rm = new RawMessage();
          RawMessageCommand rmcb =
              new RawMessageCommand() {
                @Override
                public void run(CommandSender sender) {
                  if (PageInfo.this.pageChange(prevpage) == null) {
                    String originalCmd = this.getOriginalCommand();
                    if (originalCmd != null) {
                      Bukkit.dispatchCommand(sender, originalCmd);
                    }
                  }
                }
              };
          rmcb.setOriginalCommand(cmd.replace("/", "") + " " + pagePrefix + prevpage);
          String prevText =
              this.getCustomPrev() == null
                  ? (this.getCurrentPage() > 1 ? LC.info_prevPage : LC.info_prevPageOff).getLocale()
                  : (this.getCurrentPage() > 1 ? LC.info_prevCustomPage : LC.info_prevCustomPageOff)
                      .getLocale("[value]", this.getCustomPrev());
          rm.addText(prevText)
              .addHover(
                  this.getCurrentPage() > 1
                      ? LC.info_prevPageHover.getLocale()
                      : LC.info_lastPageHover.getLocale())
              .addCommand(rmcb.getCommand());
          rm.addText(
                  LC.info_pageCount.getLocale(
                      "[current]", this.getCurrentPage(), "[total]", this.getTotalPages()))
              .addHover(LC.info_pageCountHover.getLocale("[totalEntries]", this.getTotalEntries()));
          RawMessageCommand rmcf =
              new RawMessageCommand() {
                @Override
                public void run(CommandSender sender) {
                  if (PageInfo.this.pageChange(nextPage) == null) {
                    String originalCmd = this.getOriginalCommand();
                    if (originalCmd != null) {
                      Bukkit.dispatchCommand(sender, originalCmd);
                    }
                  }
                }
              };
          rmcf.setOriginalCommand(cmd.replace("/", "") + " " + pagePrefix + nextPage);
          String nextText =
              this.getCustomNext() == null
                  ? (this.getTotalPages() > this.getCurrentPage()
                          ? LC.info_nextPage
                          : LC.info_nextPageOff)
                      .getLocale()
                  : (this.getTotalPages() > this.getCurrentPage()
                          ? LC.info_nextCustomPage
                          : LC.info_nextCustomPageOff)
                      .getLocale("[value]", this.getCustomNext());
          rm.addText(nextText)
              .addHover(
                  this.getTotalPages() > this.getCurrentPage()
                      ? LC.info_nextPageHover.getLocale()
                      : LC.info_firstPageHover.getLocale())
              .addCommand(rmcf.getCommand());
          if (this.getTotalPages() != 0) {
            rm.show(sender);
          }
        }
      }
    }
  }

  public String getCustomPrev() {
    return this.customPrev;
  }

  public void setCustomPrev(String CustomPrev) {
    this.customPrev = CustomPrev;
  }

  public String getCustomNext() {
    return this.customNext;
  }

  public void setCustomNext(String CustomNext) {
    this.customNext = CustomNext;
  }

  public String getPageChangeCommand() {
    return this.cmd;
  }

  public void setPageChangeCommand(String cmd) {
    this.cmd = cmd;
  }

  public String getPageChangeCommandPref() {
    return this.pagePref;
  }

  public void setPageChangeCommandPref(String pagePref) {
    this.pagePref = pagePref;
  }

  public void setTotalEntries(int totalEntries) {
    this.totalEntries = totalEntries;
    this.calculate();
  }
}
