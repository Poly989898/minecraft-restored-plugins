package net.Zrips.CMILib;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;
import java.util.function.Consumer;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import org.bukkit.plugin.java.JavaPlugin;

public class updateChecker {
  private JavaPlugin plugin;
  private int resourceId;

  public updateChecker(JavaPlugin plugin, int resourceId) {
    this.plugin = plugin;
    this.resourceId = resourceId;
  }

  public void getVersion(Consumer<String> consumer) {
    CMIScheduler.get()
        .runTaskAsynchronously(
            () -> {
              try {
                InputStream inputStream =
                    new URL(
                            "https://api.spigotmc.org/legacy/update.php?resource="
                                + this.resourceId)
                        .openStream();

                try {
                  Scanner scanner = new Scanner(inputStream);

                  try {
                    if (scanner.hasNext()) {
                      consumer.accept(scanner.next());
                    }
                  } catch (Throwable t$) {
                    try {
                      scanner.close();
                    } catch (Throwable x2) {
                      t$.addSuppressed(x2);
                    }

                    throw t$;
                  }

                  scanner.close();
                } catch (Throwable t$) {
                  if (inputStream != null) {
                    try {
                      inputStream.close();
                    } catch (Throwable x2) {
                      t$.addSuppressed(x2);
                    }
                  }

                  throw t$;
                }

                if (inputStream != null) {
                  inputStream.close();
                }
              } catch (IOException exception) {
                this.plugin.getLogger().info("Cannot look for updates: " + exception.getMessage());
              }
            });
  }
}
