package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.Zrips.CMILib.Container.CMINumber;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.RawMessages.RawMessage;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class tps implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get("tps", "{gcp}TPS: ");
    configReader.addComment(
        "tpsList",
        new String[] {
          "Custom tps range can be defined and you can have as many of them as you want"
        });
    configReader.get("tpsList", "{gcs}[3] [10] [30] [60] [600]");
    configReader.get(
        "explanation", "{gcp}One block {gcs}60 {gcp}seconds. Max {gcs}600 {gcp}seconds");
    configReader.get("minMax", "{gcp}Max: {gcs}[max] {gcp}Min: {gcs}[min]");
    configReader.get("hover", Arrays.asList("[tpscolor][tps]", "{gcs}[time]"));
  }

  @CAnnotation(
      info = "Check servers tps status",
      args = "(-spikes)",
      tab = "-spikes",
      explanation = {},
      regVar = {0, 1},
      consoleVar = {0, 1},
      others = false)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    boolean flag = true;
    String[] argsCopy = strings;
    int value = strings.length;

    for (int argIndex = 0; argIndex < value; argIndex++) {
      String string = argsCopy[argIndex];
      if (string.equalsIgnoreCase("-spikes")) {
        flag = false;
      }
    }

    var list = cmi.getLagMeter().getLastTimes(601);
    RawMessage rawMessage = new RawMessage();
    rawMessage.addText(CMILC.getIM(this, "tps"));
    boolean flagCopy = flag;
    CMIScheduler.runTaskAsynchronously(
        cmi,
        () -> {
          Pattern pattern = Pattern.compile("(\\[)(\\d+)(\\])");
          String tpsHeader = CMILC.getIM(this, "tpsList");
          Matcher matcher = pattern.matcher(tpsHeader);
          ArrayList<Double> arrayList = new ArrayList();
          if (!list.isEmpty()) {
            for (int lineIndex = 0; lineIndex < list.size() - 1; lineIndex++) {
              double number = (Long) list.get(lineIndex) - (Long) list.get(lineIndex + 1);
              if (number <= 1001.0) {
                arrayList.add(20.0);
              } else {
                arrayList.add(CMINumber.clamp(20.0 / (number * 20.0 / 1001.0 / 20.0), 0.0, 20));
              }
            }
          }

          while (matcher.find()) {
            try {
              int count = Integer.parseInt(matcher.group(2));
              String[] headerParts = tpsHeader.split("\\[" + matcher.group(2) + "\\]");
              if (headerParts.length > 0) {
                String headerText = headerParts[0];
                if (!headerText.isEmpty()) {
                  rawMessage.addText(headerText);
                }

                if (tpsHeader.length() > headerText.length() + 2 + matcher.group(2).length()) {
                  tpsHeader =
                      tpsHeader.substring(headerText.length() + 2 + matcher.group(2).length());
                }
              }

              double maxTps = 20.0;
              double tpsSum = 0.0;

              for (int sampleIndex = 0;
                  sampleIndex < count - 1 && sampleIndex < list.size() - 1;
                  sampleIndex++) {
                double tpsSample =
                    ((Long) list.get(sampleIndex) - (Long) list.get(sampleIndex + 1)) / 50.0;
                if (tpsSample < maxTps) {
                  maxTps = tpsSample;
                }

                if (tpsSample > tpsSum) {
                  tpsSum = tpsSample;
                }
              }

              tpsSum = Double.max(maxTps, tpsSum);
              tpsSum = Double.max(0.0, tpsSum);
              tpsSum = Double.min(20.0, tpsSum);
              double average = 0.0;
              int counted = 0;

              for (int sampleScan = 0;
                  sampleScan <= count && sampleScan <= arrayList.size() - 1;
                  sampleScan++) {
                average += arrayList.get(sampleScan);
                counted++;
              }

              double averageTps = average / counted;
              averageTps = averageTps > 20.0 ? 20.0 : averageTps;
              rawMessage
                  .addText(
                      cmi.getUtilManager().getTpsColor(averageTps).getBukkitColorCode()
                          + String.valueOf((int) (averageTps * 100.0) / 100.0))
                  .addHover(
                      LC.info_sec.getLocale(new Object[] {"[secs]", count})
                          + CMILC.getIM(
                              this,
                              "minMax",
                              "[max]",
                              cmi.getUtilManager().getTpsColor(tpsSum).getBukkitColorCode()
                                  + tpsSum,
                              "[min]",
                              cmi.getUtilManager().getTpsColor(maxTps).getBukkitColorCode()
                                  + maxTps));
            } catch (Exception exception) {
              exception.printStackTrace();
            }
          }

          rawMessage.show(commandSender);
          if (flagCopy && !list.isEmpty() && commandSender instanceof Player) {
            String lineText = "";
            CMILC.info(this, commandSender, "explanation");
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis((Long) list.get(0));
            int secondsLeft = 60 - calendar.get(13);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
            int step = 1;
            RawMessage message = new RawMessage();

            for (int lineIdx = 0; lineIdx < list.size() - 1; lineIdx++) {
              String separator = "|";
              if (secondsLeft >= 60) {
                secondsLeft = 0;
                separator = "| ";
              }

              double lineValue =
                  CMINumber.clamp(
                      1000.0 / (((Long) list.get(lineIdx) - (Long) list.get(lineIdx + 1)) / 20.0),
                      0.0,
                      20);
              Calendar now = Calendar.getInstance();
              now.setTimeInMillis((Long) list.get(lineIdx));
              message.addText(
                  cmi.getUtilManager().getTpsColor(lineValue).getBukkitColorCode() + separator);
              message.addHover(
                  CMILC.getIML(
                      this,
                      "hover",
                      "[tpscolor]",
                      cmi.getUtilManager().getTpsColor(lineValue).getBukkitColorCode(),
                      "[tps]",
                      String.valueOf((int) (lineValue * 100.0) / 100.0),
                      "[time]",
                      simpleDateFormat.format(now.getTime())));
              lineText =
                  lineText
                      + cmi.getUtilManager().getTpsColor(lineValue).getBukkitColorCode()
                      + separator;
              if (step >= 120) {
                message.show(commandSender);
                message = new RawMessage();
                lineText = "";
                step = 0;
              }

              step++;
              secondsLeft++;
            }

            if (!lineText.isEmpty()) {
              message.show(commandSender);
            }
          }
        });
    return true;
  }
}
