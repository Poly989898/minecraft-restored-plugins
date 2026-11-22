package com.Zrips.CMI.Modules.Search;

import com.Zrips.CMI.Containers.CMIFileAppender;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import net.Zrips.CMILib.Version.Schedulers.CMITask;
import org.bukkit.command.CommandSender;

public class CMIDataSearch {
  protected List<SearchTypeBase> searchingFor = new ArrayList<>();
  private CommandSender sender;
  boolean purge = false;
  protected CMITask task = null;
  protected long totalToCheck = 0L;
  private int totalChecked = 0;
  protected double searchSpeed = 10.0;
  protected int totalFound = 0;
  protected long lastInformed = 0L;
  protected CMIFileAppender appender = null;

  public CMIDataSearch(CommandSender commandSender) {
    this.sender = commandSender;
  }

  public CommandSender getInitiator() {
    return this.sender;
  }

  public List<SearchTypeBase> getSearchingFor() {
    return this.searchingFor;
  }

  public long getTotalToCheck() {
    return this.totalToCheck;
  }

  protected void addChecked() {
    this.totalChecked++;
  }

  public int getTotalChecked() {
    return this.totalChecked;
  }

  public void addSearchingFor(SearchTypeBase searchTypeBase) {
    this.searchingFor.add(searchTypeBase);
  }

  public boolean isPurge() {
    return this.purge;
  }

  public void setPurge(boolean flag) {
    this.purge = flag;
  }

  public void onCycle(int value) {}

  public void onFinish() {}

  public int getTotalFound() {
    return this.totalFound;
  }

  public boolean isRunning() {
    return this.task != null;
  }

  protected void createFileAppender(String string) {
    Date date = new Date();
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss ");
    String logFileName = simpleDateFormat.format(date) + string + ".log";
    this.appender = new CMIFileAppender("Logs" + File.separator + logFileName);
    this.appender.appendData("Started by " + this.getInitiator().getName());

    for (SearchTypeBase searchTypeBase : this.getSearchingFor()) {
      this.appender.appendData("Looking for " + searchTypeBase.getSearchTypeName());
    }
  }

  public void search() {}

  protected void initSearch() {}

  public void cancel() {
    if (this.task != null) {
      this.task.cancel();
      this.task = null;
      if (this.appender != null) {
        this.appender.close();
      }

      this.onFinish();
    }
  }
}
