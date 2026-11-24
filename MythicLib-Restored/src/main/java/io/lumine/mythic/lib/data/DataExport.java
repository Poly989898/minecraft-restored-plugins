package io.lumine.mythic.lib.data;

import io.lumine.mythic.lib.api.player.MMOPlayerData;
import io.lumine.mythic.lib.util.FileUtils;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.stream.Collectors;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

public class DataExport<H extends SynchronizedDataHolder, O extends OfflineDataHolder> {
   private final SynchronizedDataManager<H, O> manager;
   private final CommandSender output;
   private static final int BATCH_AMOUNT = 50;
   private static final int BATCH_PERIOD = 20;
   private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("0.#");

   public DataExport(@NotNull SynchronizedDataManager<H, O> manager, @NotNull CommandSender output) {
      this.manager = manager;
      this.output = output;
   }

   public boolean start(@NotNull Supplier<SynchronizedDataHandler<H, O>> source, @NotNull Supplier<SynchronizedDataHandler<H, O>> target) {
      if (!this.manager.getLoaded().isEmpty()) {
         this.output
            .sendMessage(
               "Please make sure no players are logged in when using this command. If you are still seeing this message, please restart your server and execute this command before any player logs in."
            );
         return false;
      }

      final List<UUID> playerIds = Arrays.stream(FileUtils.getFile(this.manager.getOwningPlugin(), "userdata").listFiles())
         .map(file -> UUID.fromString(file.getName().split("\\.", 2)[0]))
         .collect(Collectors.toList());

      final SynchronizedDataHandler<H, O> targetHandler;
      final SynchronizedDataHandler<H, O> sourceHandler;
      try {
         targetHandler = target.get();
         sourceHandler = source.get();
         sourceHandler.setup();
         targetHandler.setup();
      } catch (RuntimeException exception) {
         this.output.sendMessage("Could not initialize SQL/YAML provider (see console for stack trace): " + exception.getMessage());
         exception.printStackTrace();
         return false;
      }

      double timeEstimation = playerIds.size() / 50.0 * 20.0 / 20.0;
      this.output.sendMessage("Exporting " + playerIds.size() + " player data(s).. See console for details");
      this.output.sendMessage("Minimum Expected Time: " + DECIMAL_FORMAT.format(timeEstimation) + "s");
      (new BukkitRunnable() {
            int errorCount = 0;
            int batchCounter = 0;

            public void run() {
               for (int i = 0; i < 50; i++) {
                  int index = 50 * this.batchCounter + i;
                  if (index >= playerIds.size()) {
                     this.cancel();
                     sourceHandler.close();
                     targetHandler.close();
                     DataExport.this.manager
                        .getOwningPlugin()
                        .getLogger()
                        .log(Level.WARNING, "Exported " + playerIds.size() + " player data(s) to SQL database. Total errors: " + this.errorCount);
                     return;
                  }

                  try {
                     UUID playerId = playerIds.get(index);
                     H offlinePlayerData = DataExport.this.manager.newPlayerData(new MMOPlayerData(playerId));
                     sourceHandler.loadData(offlinePlayerData);
                     targetHandler.saveData(offlinePlayerData, false);
                  } catch (RuntimeException exception) {
                     this.errorCount++;
                     exception.printStackTrace();
                  }
               }

               this.batchCounter++;
            }
         })
         .runTaskTimerAsynchronously(this.manager.getOwningPlugin(), 0L, 20L);
      return true;
   }
}
