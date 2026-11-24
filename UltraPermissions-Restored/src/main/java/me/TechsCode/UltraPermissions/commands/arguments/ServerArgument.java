package me.TechsCode.UltraPermissions.commands.arguments;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import me.TechsCode.UltraPermissions.UltraPermissions;
import me.TechsCode.UltraPermissions.base.command.Argument;
import me.TechsCode.UltraPermissions.base.command.ArgumentValue;
import me.TechsCode.UltraPermissions.base.command.EmptyReason;
import me.TechsCode.UltraPermissions.base.networking.NServer;
import me.TechsCode.UltraPermissions.base.translations.Phrase;

public class ServerArgument extends Argument<NServer> {
   private UltraPermissions plugin;
   private static final Phrase SERVER_ARGUMENT = Phrase.create("ServerArgument", "<Server>");

   public ServerArgument(UltraPermissions plugin) {
      this.plugin = plugin;
   }

   @Override
   public ArgumentValue<NServer> get(String text) {
      Optional<NServer> optional;
      if (text.equalsIgnoreCase("local")) {
         optional = this.plugin.getNetworkManager().getThisServer();
      } else {
         optional = this.plugin.getNetworkManager().getServerFromName(text);
      }

      return text.equalsIgnoreCase("all")
         ? ArgumentValue.of(text, null)
         : optional.<ArgumentValue<NServer>>map(server -> ArgumentValue.of(text, server)).orElseGet(() -> ArgumentValue.empty(text, EmptyReason.INVALID));
   }

   @Override
   public String getUsage() {
      return SERVER_ARGUMENT.get();
   }

   @Override
   public List<String> getTabCompletions() {
      ArrayList list = new ArrayList<>(Arrays.asList("local", "all"));
      this.plugin.getNetworkManager().getData().ifPresent(networkData -> networkData.getServerList().forEach(server -> list.add(server.getName())));
      return list;
   }
}
