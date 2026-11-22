package com.Zrips.CMI.Modules.Holograms;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import org.bukkit.entity.Player;

public class CMIHologramPages {
  private static Pattern pagePatern = Pattern.compile("(?i)(!nextpage!)");
  private CMIHologram hologram;
  private List<String> lines = new ArrayList<>();
  private ConcurrentHashMap<Integer, CMIHologramPage> pages = new ConcurrentHashMap<>();

  public CMIHologramPages(CMIHologram cmiHologram) {
    this.hologram = cmiHologram;
  }

  public void clear() {
    this.pages.clear();
    this.lines.clear();
  }

  public ConcurrentHashMap<Integer, CMIHologramPage> getPages() {
    return this.pages;
  }

  public void setPages(ConcurrentHashMap<Integer, CMIHologramPage> concurrentHashMap) {
    this.pages = concurrentHashMap;
  }

  public CMIHologramPage getPage(int value) {
    return this.pages.get(value);
  }

  @Nullable
  public CMIHologramPage getPlayerPage(Player player) {
    return player == null ? this.getPage(0) : this.getPlayerPage(player.getUniqueId());
  }

  @Nullable
  public CMIHologramPage getPlayerPage(UUID uuid) {
    CMIHologramUserData cmiHologramUserData = this.hologram.getData(uuid);
    int value = cmiHologramUserData.getPage();
    value = this.pages.size() < value ? 1 : value;
    return this.pages.isEmpty() ? null : this.pages.get(value);
  }

  public List<CMIHologramLine> getLinesByPlayerPage(UUID uuid) {
    CMIHologramPage cmiHologramPage = this.getPlayerPage(uuid);
    if (cmiHologramPage == null) {
      return this.pages.values().iterator().hasNext()
          ? this.pages.values().iterator().next().getLines()
          : new ArrayList<>();
    } else {
      return cmiHologramPage.getLines();
    }
  }

  public void addPage(int value, CMIHologramPage cmiHologramPage) {
    this.pages.put(value, cmiHologramPage);
  }

  public synchronized void setLines(List<String> list) {
    ArrayList arrayList = new ArrayList(list);

    for (int value = 0; value < arrayList.size(); value++) {
      if (arrayList.get(value) == null || ((String) arrayList.get(value)).isEmpty()) {
        arrayList.set(value, "_");
      }
    }

    var previousLines = new ArrayList<>(this.lines);
    this.lines.clear();
    this.lines.addAll(arrayList);
    if (!previousLines.equals(this.lines)) {
      this.recalculateChangedLines(previousLines, this.lines);
    }
  }

  public void addLine(String string) {
    var arrayList = new ArrayList<>(this.lines);
    this.lines.add(string);
    if (!arrayList.equals(this.lines)) {
      this.recalculateChangedLines(arrayList, this.lines);
    }
  }

  public void setLine(int value, String string) {
    var arrayList = new ArrayList<>(this.lines);

    while (this.lines.size() <= value) {
      this.lines.add("");
    }

    if (string == null) {
      this.lines.remove(value);
    } else {
      this.lines.set(value, string);
    }

    if (!arrayList.equals(this.lines)) {
      this.recalculateChangedLines(arrayList, this.lines);
    }
  }

  public void removeLine(int value) {
    var arrayList = new ArrayList<>(this.lines);
    if (this.lines.size() >= value) {
      try {
        this.lines.remove(value - 1);
      } catch (Throwable throwable) {
        throwable.printStackTrace();
      }
    }

    if (!arrayList.equals(this.lines)) {
      this.recalculateChangedLines(arrayList, this.lines);
    }
  }

  public List<String> getLines() {
    return this.lines;
  }

  public void recheckLines() {
    this.recalculateChangedLines(this.lines, this.lines);
  }

  private synchronized void recalculateChangedLines(List<String> list, List<String> newLines) {
    int value = 0;
    this.pages.clear();
    CMIHologramPage cmiHologramPage = new CMIHologramPage();
    this.pages.put(1, cmiHologramPage);
    int pageNumber = 1;

    for (String string : new ArrayList<>(this.getLines())) {
      Matcher matcher = pagePatern.matcher(string);
      if (matcher.find()) {
        cmiHologramPage.trimToLimit(value);
        cmiHologramPage =
            this.pages.computeIfAbsent(++pageNumber, integer -> new CMIHologramPage());
        value = 0;
      } else {
        CMIHologramLine cmiHologramLine = cmiHologramPage.getLine(value);
        if (cmiHologramLine != null && cmiHologramLine.getText().equals(string)) {
          value++;
        } else {
          cmiHologramPage.addLine(
              value, new CMIHologramLine(this.hologram.getTextSettings(), string));
          value++;
        }
      }
    }

    for (CMIHologramPage pageEntry : this.pages.values()) {
      pageEntry.setHeight(this.getHeight(pageEntry.getLines()));
      pageEntry.calculateWidth();
      if ((pageEntry.getWidth() == 0.0
              || this.hologram.getAutoPageLine() != null
                  && this.hologram.getAutoPageLine().getWidth() > pageEntry.getWidth())
          && this.hologram.getPageSettings().isAutoPagination()
          && this.hologram.getAutoPageLine() != null) {
        pageEntry.setWidth(this.hologram.getAutoPageLine().getWidth());
      }
    }

    this.hologram.resetIconCache();
    this.hologram.requestUpdate(
        CMIHologramUpdateType.Text, CMIHologramUpdateType.Icon, CMIHologramUpdateType.Billboard);
  }

  private double getHeight(List<CMIHologramLine> list) {
    double number = 0.125;

    for (CMIHologramLine cmiHologramLine : list) {
      String string = cmiHologramLine.getText();
      if (string.equalsIgnoreCase("!nextpage!")) {
        break;
      }

      if (string.equals("_")) {
        string = "";
      }

      number += 0.25;
    }

    if (this.hologram.getPageSettings().isAutoPagination()) {
      number += 0.25;
    }

    return number;
  }

  public double getHeight() {
    double number = 0.0;

    for (String string : this.lines) {
      String text = string;
      if (text.equalsIgnoreCase("!nextpage!")) {
        break;
      }

      if (text.equals("_")) {
        text = "";
      }

      number += 0.25;
    }

    return number;
  }

  public void goToNextPage(Player player) {
    this.goToNextPage(player.getUniqueId());
  }

  public void goToNextPage(UUID uuid) {
    CMIHologramUserData cmiHologramUserData = this.hologram.getData(uuid);
    int value = cmiHologramUserData.getPage();
    if (++value > this.pages.size()) {
      value = 1;
    }

    cmiHologramUserData.setPage(value);
    this.callPageChange(uuid);
    cmiHologramUserData.setSkipPageChange(true);
  }

  private void callPageChange(UUID uuid) {
    for (CMIHologramProcessor cmiHologramProcessor : this.hologram.processors.values()) {
      cmiHologramProcessor.onPageChange(uuid);
    }

    this.hologram.requestUpdate(uuid, CMIHologramUpdateType.Text, CMIHologramUpdateType.Icon);
  }

  public void goToNextPageAuto(UUID uuid) {
    CMIHologramUserData cmiHologramUserData = this.hologram.getData(uuid);
    if (cmiHologramUserData.isSkipPageChange()) {
      cmiHologramUserData.setSkipPageChange(false);
    } else {
      int value = cmiHologramUserData.getPage();
      if (++value > this.pages.size()) {
        value = 1;
      }

      cmiHologramUserData.setPage(value);
      this.hologram.requestUpdate(uuid, CMIHologramUpdateType.Billboard);
      this.callPageChange(uuid);
    }
  }

  public void goToPrevPage(Player player) {
    this.goToPrevPage(player.getUniqueId());
  }

  public void goToPrevPage(UUID uuid) {
    CMIHologramUserData cmiHologramUserData = this.hologram.getData(uuid);
    int value = cmiHologramUserData.getPage();
    if (--value <= 0) {
      value = this.pages.size();
    }

    cmiHologramUserData.setPage(value);
    this.callPageChange(uuid);
    cmiHologramUserData.setSkipPageChange(true);
  }

  public void goToPage(UUID uuid, int value) {
    CMIHologramUserData cmiHologramUserData = this.hologram.getData(uuid);
    if (value <= 0) {
      value = this.pages.size();
    } else if (value > this.pages.size()) {
      value = 1;
    }

    cmiHologramUserData.setPage(value);
    this.callPageChange(uuid);
    cmiHologramUserData.setSkipPageChange(true);
  }

  public int getPageCount() {
    return this.pages.size();
  }
}
