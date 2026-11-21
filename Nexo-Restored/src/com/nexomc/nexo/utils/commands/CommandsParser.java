package com.nexomc.nexo.utils.commands;

import com.nexomc.nexo.decomp.ResultShim;
import com.nexomc.nexo.utils.AdventureUtils;
import com.nexomc.nexo.utils.NexoYamlKt;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import kotlin.Metadata;
import kotlin.Result;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.text.StringsKt;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u00000\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010 \n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\u0018\u00002\u00020\u0001B\u0019\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\b\u0010\u0004\u001a\u0004\u0018\u00010\u0005¢\u0006\u0004\b\u0006\u0010\u0007J\u000e\u0010\r\u001a\u00020\u000e2\u0006\u0010\u000f\u001a\u00020\u0010R\u0014\u0010\b\u001a\b\u0012\u0004\u0012\u00020\n0\tX\u0082\u000e¢\u0006\u0002\n\u0000R\u0014\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\n0\tX\u0082\u000e¢\u0006\u0002\n\u0000R\u0014\u0010\f\u001a\b\u0012\u0004\u0012\u00020\n0\tX\u0082\u000e¢\u0006\u0002\n\u0000¨\u0006\u0011"},
   d2 = {"Lcom/nexomc/nexo/utils/commands/CommandsParser;", "", "section", "Lorg/bukkit/configuration/ConfigurationSection;", "tagResolver", "Lnet/kyori/adventure/text/minimessage/tag/resolver/TagResolver;", "<init>", "(Lorg/bukkit/configuration/ConfigurationSection;Lnet/kyori/adventure/text/minimessage/tag/resolver/TagResolver;)V", "consoleCommands", "", "", "playerCommands", "oppedPlayerCommands", "perform", "", "player", "Lorg/bukkit/entity/Player;", "com.nexomc:core"}
)
public final class CommandsParser {
   @NotNull
   private List<String> consoleCommands;
   @NotNull
   private List<String> playerCommands;
   @NotNull
   private List<String> oppedPlayerCommands;

   public CommandsParser(@NotNull ConfigurationSection section, @Nullable TagResolver tagResolver) {
      super();
      Iterable iterable = (Iterable)NexoYamlKt.stringListOrSingle(section, "console", (String)null);
      Collection destination = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault(iterable, 10)));

      for(Object item : iterable) {
         String it = (String)item;
         int value = 0;
         destination.add(AdventureUtils.parseMiniMessage(it, tagResolver));
      }

      this.consoleCommands = (List)destination;
      this.playerCommands = NexoYamlKt.stringListOrSingle(section, "player", (String)null);
      this.oppedPlayerCommands = NexoYamlKt.stringListOrSingle(section, "opped_player", (String)null);
   }

   public final void perform(@NotNull Player player) {
      String name = player.getName();
      String playerName = name;
      Iterable iterable = (Iterable)this.consoleCommands;

      for(Object element : iterable) {
         String command = (String)element;
         int value = 0;
         Bukkit.dispatchCommand((CommandSender)Bukkit.getConsoleSender(), StringsKt.replace(command, "%p%", playerName, false));
      }

      iterable = (Iterable)this.playerCommands;

      for(Object obj3 : iterable) {
         String command = (String)obj3;
         int value2 = 0;
         player.performCommand(StringsKt.replace(command, "%p", playerName, false));
      }

      boolean originalOp = player.isOp();
      player.setOp(true);

      try {
         int value3 = 0;
         Iterable iterable2 = (Iterable)this.oppedPlayerCommands;

         for(Object obj4 : iterable2) {
            String command = (String)obj4;
            int value4 = 0;
            player.performCommand(StringsKt.replace(command, "%p%", playerName, false));
         }

         Object obj = ResultShim.ok(Unit.INSTANCE);
      } catch (Throwable e) {
         Object obj2 = ResultShim.ok(ResultKt.createFailure(e));
      }

      player.setOp(originalOp);
   }
}
