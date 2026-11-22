package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import net.Zrips.CMILib.Equations.Parser;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import org.bukkit.command.CommandSender;

public class solve implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get("result", "{gcp}Result: {gcs}[res]");
    configReader.get("incorrect", "{gcw}Incorrect input: {gcs}[input]");
  }

  @CAnnotation(
      info = "Solve equation",
      args = "[equation]",
      explanation = {},
      regVar = 1,
      consoleVar = 1)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    try {
      Parser parser = new Parser(strings[0]);

      try {
        CMILC.info(this, commandSender, "result", "[res]", parser.getValue());
      } catch (Exception | Error value) {
        CMILC.info(this, commandSender, "incorrect", "[input]", strings[0]);
      }
    } catch (Throwable throwable) {
      return false;
    }

    return true;
  }
}
