package com.Zrips.CMI.Containers;

import com.Zrips.CMI.CMI;
import net.Zrips.CMILib.Container.CMINumber;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Locale.LC;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CMIMail {
  private static long mailExpiresIn = 2592000000L;
  private static long mailToAllxpiresIn = 2592000000L;
  private static int maxMail = 30;
  private static String consoleMailSender = LC.info_Console.getLocale(new Object[0]);

  public static void loadConfig() {
    ConfigReader configReader = CMI.getInstance().getConfigManager().getConfig();
    configReader.addComment(
        "Command.Mail.ExpiresIn", new String[] {"For how long to keep players mail in days"});
    mailExpiresIn =
        configReader.get(
                "Command.Mail.ExpiresIn", configReader.getC().getInt("PlayerMail.ExpiresIn", 30))
            * 24L
            * 60L
            * 60L
            * 1000L;
    configReader.addComment(
        "Command.Mail.mailAllDays",
        new String[] {
          "Mailing to all players will send mails to players who loged into server in last x days"
        });
    mailToAllxpiresIn =
        configReader.get(
                "Command.Mail.mailAllDays", configReader.getC().getInt("PlayerMail.mailAllDays", 7))
            * 24L
            * 60L
            * 60L
            * 1000L;
    configReader.addComment(
        "Command.Mail.MaxMails",
        new String[] {
          "How many mails we should keep for each player",
          "When email limit is reached oldest one will be removed"
        });
    maxMail = CMINumber.clamp(configReader.get("Command.Mail.MaxMails", 50), 1, 9999);
    configReader.addComment(
        "Command.Mail.ConsoleMailSender",
        new String[] {
          "Name of mail sender when command is performed from console",
          "This name will be used as indicator who sent it and who should receive reply if player"
              + " responds to it",
          "For best experience set this value to your staff member responsible for communications"
              + " with players"
        });
    consoleMailSender = configReader.get("Command.Mail.ConsoleMailSender", "server");
  }

  public static long getMailExpiresInMS() {
    return mailExpiresIn;
  }

  public static long getMailToAllExpirationMS() {
    return mailToAllxpiresIn;
  }

  public static int getMaxMail() {
    return maxMail;
  }

  public static String getConsoleMailSender() {
    return consoleMailSender;
  }

  public static String getSendersName(CommandSender commandSender) {
    return commandSender instanceof Player ? commandSender.getName() : getConsoleMailSender();
  }
}
