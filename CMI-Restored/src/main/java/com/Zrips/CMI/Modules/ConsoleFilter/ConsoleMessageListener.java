package com.Zrips.CMI.Modules.ConsoleFilter;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Modules.Display.CMIBillboard;
import com.Zrips.CMI.Modules.Display.CMITextAlignment;
import com.Zrips.CMI.Modules.Holograms.CMIHologram;
import com.Zrips.CMI.events.CMIAsyncConsoleMessageEvent;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import net.Zrips.CMILib.Container.CMILocation;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import net.Zrips.CMILib.Version.Schedulers.CMITask;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class ConsoleMessageListener implements Listener {
  private CMI plugin;
  CMITask task;
  DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");

  public ConsoleMessageListener(CMI cmi) {
    this.plugin = cmi;
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onCMIAsyncConsoleMessageEvent(
      CMIAsyncConsoleMessageEvent cmiAsyncConsoleMessageEvent) {
    if (Version.isTestServer()) {
      CMIScheduler.runTask(
          this.plugin,
          () -> {
            if (this.task == null) {
              this.task =
                  CMIScheduler.runLaterAsync(
                      this.plugin,
                      () -> {
                        this.showHolo();
                        this.task = null;
                      },
                      2L);
            }
          });
    }
  }

  private void showHolo() {
    CMIHologram cmiHologram = this.plugin.getHologramManager().getByName("CMIConsoleLogger");
    if (cmiHologram == null) {
      cmiHologram =
          new CMIHologram(
              "CMIConsoleLogger",
              new CMILocation((World) Bukkit.getWorlds().get(0), 0.0, 85.0, 0.0));
      cmiHologram.setNewDisplayMethod(true);
      cmiHologram.setTextAlignment(CMITextAlignment.LEFT);
      cmiHologram.setBillboard(CMIBillboard.FIXED);
      cmiHologram.setBackgroundAlpha(150);
      cmiHologram.setShowRange(64);
      this.plugin.getHologramManager().addHologram(cmiHologram);
    }

    var list = this.plugin.getConsoleFilterManager().getCachedMessages();
    ArrayList<String> arrayList = new ArrayList();
    StringBuilder stringBuilder = new StringBuilder();

    for (int value = 0; value < 250; value++) {
      stringBuilder.append(" ");
    }

    arrayList.add(stringBuilder.toString());

    for (int index = list.size() - 50; index < list.size() && index >= 0; index++) {
      String string = ((CMIConsoleRecord) list.get(index)).getMessage();
      string = string.length() > 250 ? string.substring(0, 250) : string;
      LocalDateTime localDateTime =
          LocalDateTime.ofInstant(
              Instant.ofEpochMilli(((CMIConsoleRecord) list.get(index)).getTime()),
              ZoneId.systemDefault());
      String timestamp = "[" + localDateTime.format(this.formatter) + "]";
      arrayList.add(" [" + timestamp + "] " + string.replaceAll("\u001b\\[[;\\d]*m", ""));
    }

    arrayList.add(stringBuilder.toString());
    cmiHologram.setLines(arrayList);
    this.plugin.getHologramManager().getByName("CMIConsoleLogger").update();
  }
}
