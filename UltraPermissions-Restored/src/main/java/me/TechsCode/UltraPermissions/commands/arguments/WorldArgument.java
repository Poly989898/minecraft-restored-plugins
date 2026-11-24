package me.TechsCode.UltraPermissions.commands.arguments;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import me.TechsCode.UltraPermissions.UltraPermissions;
import me.TechsCode.UltraPermissions.base.command.Argument;
import me.TechsCode.UltraPermissions.base.command.ArgumentValue;
import me.TechsCode.UltraPermissions.base.command.EmptyReason;
import org.bukkit.Bukkit;
import org.bukkit.World;

public class WorldArgument extends Argument<World> {
   private UltraPermissions plugin;
   private EmptyReason emptyReason;

   public WorldArgument(UltraPermissions ultraPermissions, EmptyReason emptyReason) {
      this.plugin = ultraPermissions;
      this.emptyReason = emptyReason;
   }

   @Override
   public ArgumentValue<World> get(String text) {
      Optional<World> optional = Optional.ofNullable(Bukkit.getWorld(text));
      return text.equalsIgnoreCase("all")
         ? ArgumentValue.of(text, null)
         : optional.<ArgumentValue<World>>map(world -> ArgumentValue.of(text, world)).orElseGet(() -> ArgumentValue.empty(text, this.emptyReason));
   }

   @Override
   public String getUsage() {
      return "<World>";
   }

   @Override
   public List<String> getTabCompletions() {
      ArrayList list = new ArrayList<>(Arrays.asList("all"));
      Bukkit.getWorlds().stream().forEach(world -> list.add(world.getName()));
      return list;
   }
}
