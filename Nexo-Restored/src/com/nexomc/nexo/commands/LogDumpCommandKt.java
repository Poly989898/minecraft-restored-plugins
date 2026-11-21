package com.nexomc.nexo.commands;

import com.nexomc.nexo.decomp.ResultShim;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.nexomc.nexo.configs.Settings;
import com.nexomc.nexo.utils.FileUtils;
import com.nexomc.nexo.utils.FileUtilsKt;
import com.nexomc.nexo.utils.logs.Logs;
import gs.mclo.java.APIResponse;
import gs.mclo.java.Log;
import gs.mclo.java.MclogsAPI;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import java.io.File;
import java.nio.charset.Charset;
import kotlin.ExceptionsKt;
import kotlin.Metadata;
import kotlin.Result;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.io.FilesKt;
import kotlin.text.Regex;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 4, 0},
   k = 2,
   xi = 48,
   d1 = {"\u0000\u0012\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\u001a\u0016\u0010\u0000\u001a\u00020\u00012\f\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003H\u0000¨\u0006\u0005"},
   d2 = {"dumpLogCommand", "", "root", "Lcom/mojang/brigadier/builder/LiteralArgumentBuilder;", "Lio/papermc/paper/command/brigadier/CommandSourceStack;", "com.nexomc:core"}
)
public final class LogDumpCommandKt {
   public static final void dumpLogCommand(@NotNull LiteralArgumentBuilder<CommandSourceStack> root) {
      NexoCommand nexoCommand = NexoCommand.INSTANCE;
      NexoCommand nexoCommand2 = NexoCommand.INSTANCE;
      LiteralArgumentBuilder literalArgumentBuilder = Commands.literal("dumplog");
      LiteralArgumentBuilder literalArgumentBuilder2 = nexoCommand2.withPermission$com_nexomc_core(literalArgumentBuilder, "nexo.command.dumplog");
      root.then((ArgumentBuilder)nexoCommand.executesSender$com_nexomc_core(literalArgumentBuilder2, LogDumpCommandKt::dumpLogCommand$lambda$0));
   }

   private static final Unit dumpLogCommand$lambda$0(CommandSender sender) {
      java.lang.Object obj;
      File serverFolder = FileUtils.INSTANCE.getServerFolder();
      File logfile = FileUtilsKt.resolve(serverFolder, new String[]{"logs", "latest.log"});
      CharSequence charSequence = (CharSequence)FilesKt.readText(logfile, kotlin.text.Charsets.UTF_8);
      Regex it = new Regex("https://atlas\\..*\\.com:.*/");
      String str = "[REDACTED]";
      String logContent = it.replace(charSequence, str);

      try {
         int value = 0;
         APIResponse post = MclogsAPI.share(new Log(logContent));
         Logs.logSuccess("Logfile has been dumped to: " + post.url, false);
         obj = ResultShim.ok(Unit.INSTANCE);
      } catch (Throwable e) {
         obj = ResultShim.ok(ResultKt.createFailure(e));
      }

      Throwable throwable = ResultShim.exceptionOrNull(obj);
      if (throwable != null) {
         Throwable it_2 = throwable;
         int value2 = 0;
         Logs.logWarn("Failed to upload logfile to mclo.gs, attempting to using pastebin - s", false);
         if (Settings.DEBUG.toBool()) {
            Logs.logWarn(ExceptionsKt.stackTraceToString(it_2), false);
         }

         Object obj2;
         try {
            int value3 = 0;
            Logs.logSuccess("Logfile has been dumped to: " + LogDumpCommand.INSTANCE.postToPasteBin$com_nexomc_core(logContent), false);
            obj2 = ResultShim.ok(Unit.INSTANCE);
         } catch (Throwable ex) {
            obj2 = ResultShim.ok(ResultKt.createFailure(ex));
         }

         throwable = ResultShim.exceptionOrNull(obj2);
         if (throwable != null) {
            Throwable it_1 = throwable;
            int value4 = 0;
            Logs.logError("Failed to use backup solution with pastebin", false);
            if (Settings.DEBUG.toBool()) {
               Logs.logError(ExceptionsKt.stackTraceToString(it_1), false);
            }
         }
      }

      return Unit.INSTANCE;
   }
}
