package it.ytnoos.lpx.logger;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import it.ytnoos.lpx.LPXBukkit;
import it.ytnoos.lpx.api.check.FlagInfo;
import it.ytnoos.lpx.api.check.abstraction.Check;
import it.ytnoos.lpx.api.player.ProtocolPlayer;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.bukkit.plugin.Plugin;

public class LogWriter {
   private static final DateTimeFormatter ddMMYyyy = DateTimeFormatter.ofPattern("dd-MM-yyyy");
   private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy");
   private static final SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
   private final LPXBukkit lpxBukkit;
   private final Plugin plugin;
   private final ExecutorService executor = Executors.newSingleThreadExecutor(new ThreadFactoryBuilder().setNameFormat("lpx-log-writer-%d").build());
   private LocalDate today = LocalDate.now();
   private File file;
   private PrintWriter writer;
   private boolean active = false;

   public LogWriter(LPXBukkit lpxBukkit2) {
      this.lpxBukkit = lpxBukkit2;
      this.plugin = lpxBukkit2.getPlugin();
   }

   public void start() {
      this.deleteOldLogs();
      this.deleteOldZips();
   }

   public void writeFlag(ProtocolPlayer protocolPlayer, Check check, FlagInfo[] flagInfos) {
      FlagInfo[] flagInfos2 = flagInfos;
      if (this.lpxBukkit.getConfigManager().getOptionsConfig().isAlertsStore()) {
         this.executor.execute(() -> this.writeFlagThread(protocolPlayer, check, flagInfos2));
      }
   }

   public void writeString(Supplier supplier) {
      this.executor.execute(() -> this.writeLine(supplier));
   }

   public void execute(Runnable runnable) {
      this.executor.execute(runnable);
   }

   public void writeException(Supplier supplier, Throwable throwable) {
      this.executor.execute(() -> this.writeLineWithThrowable(supplier, throwable));
   }

   public void writeError(Supplier supplier, Throwable throwable) {
      this.writeLineWithThrowable(supplier, throwable);
   }

   private void writeFlagThread(ProtocolPlayer protocolPlayer, Check check, FlagInfo[] flagInfos) {
      FlagInfo[] flagInfos2 = flagInfos;
      this.writeLine(() -> {
         StringBuilder stringBuilder = new StringBuilder();
         boolean flag = true;

         for (FlagInfo flagInfo : flagInfos2) {
            if (!flag) {
               stringBuilder.append(", ");
            }

            flag = false;
            stringBuilder.append(flagInfo.getName()).append("=").append(flagInfo.getValue());
         }

         String string = protocolPlayer.getUsername();
         String string2 = check.getType().getCategory().getFormattedName();
         String string3 = check.getType().getId().name();
         int value = check.getOptions().getVl();
         int value2 = check.getOptions().getCheckOptions().getMaxVL();
         String string4 = String.valueOf(stringBuilder);
         int value3 = value2;
         int value4 = value;
         String string5 = string3;
         String string6 = string2;
         return string + " " + string6 + " " + string5 + " " + value4 + "/" + value3 + " [" + string4 + "]";
      });
   }

   private void writeLine(Supplier supplier) {
      this.setupFile();
      if (this.active && this.file != null) {
         this.writer.println("[" + DATE_TIME_FORMAT.format(System.currentTimeMillis()) + "] " + (String)supplier.get());
      }
   }

   private void writeLineWithThrowable(Supplier supplier, Throwable throwable) {
      this.setupFile();
      if (this.active && this.file != null) {
         String string = "[" + DATE_TIME_FORMAT.format(System.currentTimeMillis()) + "] ";
         this.writer.println(string + (String)supplier.get());
         StringWriter stringWriter = new StringWriter();
         throwable.printStackTrace(new PrintWriter(stringWriter));

         for (String string2 : stringWriter.toString().split("\\R")) {
            this.writer.println(string + string2);
         }
      }
   }

   private void setupFile() {
      boolean flag = this.today.getDayOfYear() == LocalDate.now().getDayOfYear();
      if (this.file == null || !flag) {
         if (!flag && this.file != null) {
            if (this.writer != null) {
               this.writer.close();
            }

            this.zipFile(this.file);
         }

         this.today = LocalDate.now();
         this.file = new File(this.plugin.getDataFolder(), "logs/" + DATE_FORMAT.format(System.currentTimeMillis()) + ".log");
         this.file.getParentFile().mkdirs();

         try {
            if (!this.file.exists()) {
               this.file.createNewFile();
            }

            this.writer = new PrintWriter(new FileWriter(this.file, true), true);
            this.active = true;
         } catch (Exception exception) {
            this.plugin.getLogger().log(Level.SEVERE, exception, () -> "Unable to create log file");
         }
      }
   }

   private void zipFile(File file2) {
      try (
         FileOutputStream fileOutputStream = new FileOutputStream(new File(this.plugin.getDataFolder(), "logs/" + file2.getName().split("\\.")[0] + ".zip"));
         ZipOutputStream zipOutputStream = new ZipOutputStream(fileOutputStream);
      ) {
         ZipEntry zipEntry = new ZipEntry(file2.getName());
         zipOutputStream.putNextEntry(zipEntry);

         try (FileInputStream fileInputStream = new FileInputStream(file2)) {
            byte[] bs = new byte[1024];

            int value;
            while ((value = fileInputStream.read(bs)) > 0) {
               zipOutputStream.write(bs, 0, value);
            }
         }

         zipOutputStream.closeEntry();
      } catch (Exception exception) {
         this.plugin.getLogger().log(Level.SEVERE, exception, () -> "Unable to zip file " + file2.getName());
      }

      file2.delete();
   }

   public void shutdown() {
      this.executor.shutdownNow();
      if (this.writer != null) {
         this.writer.close();
      }
   }

   private void deleteOldLogs() {
      File file2 = new File(this.plugin.getDataFolder(), "logs");
      if (file2.exists()) {
         File[] files = file2.listFiles();
         if (files != null) {
            for (File file3 : files) {
               if (file3.getName().endsWith(".log")) {
                  try {
                     LocalDate localDate = LocalDate.parse(file3.getName().split("\\.")[0], ddMMYyyy);
                     if (localDate.getDayOfYear() != LocalDate.now().getDayOfYear()) {
                        this.zipFile(file3);
                     }
                  } catch (Exception exception) {
                  }
               }
            }
         }
      }
   }

   private void deleteOldZips() {
      File file2 = new File(this.plugin.getDataFolder(), "logs");
      if (file2.exists()) {
         File[] files = file2.listFiles();
         if (files != null) {
            for (File file3 : files) {
               if (file3.getName().endsWith(".zip")) {
                  try {
                     LocalDate localDate = LocalDate.parse(file3.getName().split("\\.")[0], ddMMYyyy);
                     if (ChronoUnit.DAYS.between(localDate, LocalDate.now()) > this.lpxBukkit.getConfigManager().getOptionsConfig().getAlertsDays()) {
                        file3.delete();
                     }
                  } catch (DateTimeParseException dateTimeParseException) {
                  } catch (SecurityException securityException) {
                     this.plugin.getLogger().log(Level.SEVERE, securityException, () -> "Unable to delete file " + file3.getName());
                  }
               }
            }
         }
      }
   }
}
