package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import org.bukkit.command.CommandSender;

public class servertime implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get("time", "{gcp}Server time: {gcs}[time]");
    configReader.get("format", "dd/MM/YYYY HH:mm:ss");
    configReader.get("timeZone", "{gcp}Time zone name: {gcs}[name]");
    configReader.get("timeZoneId", "{gcp}Time zone id: {gcs}[id]");
    configReader.get("timeZoneOffset", "{gcp}Time zone offset: {gcs}[offset]");
  }

  @CAnnotation(info = "Show server time", args = "", regVar = 0, consoleVar = 0, others = false)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    Date date = new Date();
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(CMILC.getIM(this, "format"));
    String string = simpleDateFormat.format(date);
    CMILC.info(this, commandSender, "time", "[time]", string);
    TimeZone timeZone = Calendar.getInstance().getTimeZone();
    CMILC.info(this, commandSender, "timeZone", "[name]", timeZone.getDisplayName());
    CMILC.info(this, commandSender, "timeZoneId", "[id]", timeZone.getID());
    int value = timeZone.getOffset(new Date().getTime()) / 1000 / 60 / 60;
    CMILC.info(this, commandSender, "timeZoneOffset", "[offset]", value > 0 ? "+" + value : value);
    return true;
  }
}
