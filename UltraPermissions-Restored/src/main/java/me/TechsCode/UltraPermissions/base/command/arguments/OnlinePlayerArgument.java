package me.TechsCode.UltraPermissions.base.command.arguments;

import java.util.List;
import java.util.stream.Collectors;
import me.TechsCode.UltraPermissions.base.command.Argument;
import me.TechsCode.UltraPermissions.base.command.ArgumentValue;
import me.TechsCode.UltraPermissions.base.command.EmptyReason;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class OnlinePlayerArgument extends Argument<Player> {
   private final String usage;
   private final EmptyReason emptyReason;

   public OnlinePlayerArgument(String text, EmptyReason emptyReason) {
      this.usage = text;
      this.emptyReason = emptyReason;
   }

   @Override
   public ArgumentValue<Player> get(String text) {
      Player player = Bukkit.getPlayer(text);
      return player != null && player.isOnline() ? ArgumentValue.of(text, player) : ArgumentValue.empty(text, this.emptyReason);
   }

   @Override
   public String getUsage() {
      return this.usage;
   }

   @Override
   public List<String> getTabCompletions() {
      return Bukkit.getOnlinePlayers().stream().<String>map(Player::getName).collect(Collectors.toList());
   }
}
