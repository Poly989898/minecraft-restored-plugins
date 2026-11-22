package com.Zrips.CMI.Modules.ConsoleFilter;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.events.CMIAsyncConsoleMessageEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import net.Zrips.CMILib.Version.Version;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Filter.Result;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.filter.AbstractFilter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

public class ConsoleFilterManager {
  private CMI plugin;
  private final List<CMIConsoleRecord> cache;
  private final int maxCapacity = 50;
  private boolean failed = false;
  static String regex = "^\\w+\\s+issued\\s+server\\s+command:\\s+/w\\b.*";
  static Pattern pattern = Pattern.compile(regex);

  public ConsoleFilterManager(CMI cmi) {
    this.plugin = cmi;
    this.cache = Collections.synchronizedList(new ArrayList<>());
  }

  public synchronized void add(CMIConsoleRecord cmiConsoleRecord) {
    if (this.cache.size() >= 50) {
      this.cache.remove(0);
    }

    this.cache.add(cmiConsoleRecord);
  }

  public synchronized CMIConsoleRecord get(int value) {
    return this.cache.get(value);
  }

  public synchronized int size() {
    return this.cache.size();
  }

  public synchronized List<CMIConsoleRecord> getCachedMessages() {
    return new ArrayList<>(this.cache);
  }

  public static Plugin getPluginFromLogEvent(LogEvent logEvent) {
    Throwable throwable = logEvent.getThrown();
    Plugin foundPlugin = null;
    if (throwable != null) {
      foundPlugin = getPluginFromThrowableRecursive(throwable);
    }

    if (foundPlugin != null) {
      return foundPlugin;
    }

    String string = logEvent.getMessage().getFormattedMessage();
    if (string != null) {
      foundPlugin = getPluginFromMessage(string);
    }

    return foundPlugin;
  }

  private static Plugin getPluginFromThrowableRecursive(Throwable throwable) {
    while (throwable != null) {
      Plugin foundPlugin = getPluginFromThrowable(throwable);
      if (foundPlugin != null) {
        return foundPlugin;
      }

      throwable = throwable.getCause();
    }

    return null;
  }

  private static Plugin getPluginFromThrowable(Throwable throwable) {
    StackTraceElement[] stackTraceElements;
    int value = (stackTraceElements = throwable.getStackTrace()).length;

    for (int index = 0; index < value; index++) {
      StackTraceElement stackTraceElement = stackTraceElements[index];
      String string = stackTraceElement.getClassName();
      Plugin[] plugins;
      int pluginCount = (plugins = Bukkit.getPluginManager().getPlugins()).length;

      for (int pluginIndex = 0; pluginIndex < pluginCount; pluginIndex++) {
        Plugin candidatePlugin = plugins[pluginIndex];
        if (isClassFromPlugin(candidatePlugin, string)) {
          return candidatePlugin;
        }
      }
    }

    return null;
  }

  private static boolean isClassFromPlugin(Plugin candidate, String string) {
    try {
      return string.startsWith(
          candidate
              .getDescription()
              .getMain()
              .substring(0, candidate.getDescription().getMain().lastIndexOf(46)));
    } catch (Exception exception) {
      exception.printStackTrace();
      return false;
    }
  }

  private static Plugin getPluginFromMessage(String string) {
    Plugin[] plugins;
    int value = (plugins = Bukkit.getPluginManager().getPlugins()).length;

    for (int index = 0; index < value; index++) {
      Plugin candidatePlugin = plugins[index];
      String pluginName = candidatePlugin.getName();
      if (string.contains(pluginName)) {
        return candidatePlugin;
      }
    }

    return null;
  }

  private void initFilter() {
    AbstractFilter abstractFilter =
        new AbstractFilter() {
          public Result filter(LogEvent logEvent) {
            if (logEvent.getLevel().equals(Level.ERROR)) {
              String string = logEvent.getLevel().toString();
              String threadName = logEvent.getThreadName();
              String pluginLabel = null;
              Plugin sourcePlugin = ConsoleFilterManager.getPluginFromLogEvent(logEvent);
              if (sourcePlugin != null) {
                pluginLabel =
                    sourcePlugin.getName()
                        + " ("
                        + sourcePlugin.getDescription().getVersion()
                        + ")";
              }

              CMIConsoleRecord cmiConsoleRecord =
                  new CMIConsoleRecord(
                      "{#red}[" + string + "] " + pluginLabel, logEvent.getTimeMillis());
              ConsoleFilterManager.this.add(cmiConsoleRecord);
            } else {
              ConsoleFilterManager.this.add(
                  new CMIConsoleRecord(
                      logEvent.getMessage().getFormattedMessage(), logEvent.getTimeMillis()));
            }

            if (ConsoleFilterManager.this.plugin.isFullyLoaded()
                && !ConsoleFilterManager.this.failed) {
              CMIScheduler.runTaskAsynchronously(
                  ConsoleFilterManager.this.plugin,
                  () -> {
                    try {
                      Bukkit.getPluginManager()
                          .callEvent(
                              new CMIAsyncConsoleMessageEvent(
                                  logEvent.getMessage().getFormattedMessage(),
                                  logEvent.getTimeMillis()));
                    } catch (Throwable throwable) {
                      throwable.printStackTrace();
                      ConsoleFilterManager.this.failed = true;
                    }
                  });
            }

            return ConsoleFilterManager.shouldHideMessage(
                    logEvent.getMessage().getFormattedMessage())
                ? Result.DENY
                : Result.NEUTRAL;
          }
        };
    ((Logger) LogManager.getRootLogger())
        .addFilter((org.apache.logging.log4j.core.Filter) abstractFilter);
  }

  private static boolean shouldHideMessage(String string) {
    return false;
  }

  public void load() {
    if (Version.isCurrentEqualOrHigher(Version.v1_8_R1)) {
      this.initFilter();
      PluginManager pluginManager = this.plugin.getServer().getPluginManager();
      pluginManager.registerEvents(new ConsoleMessageListener(this.plugin), this.plugin);
    }
  }
}
