package me.TechsCode.UltraPermissions.base.legacy.task;

import java.util.Arrays;
import me.TechsCode.UltraPermissions.base.SpigotTechPlugin;
import org.bukkit.Bukkit;

public class TaskManager {
   public TaskManager(SpigotTechPlugin spigotTechPlugin) {
      Arrays.stream(UpdateTime.values())
         .forEach(updateTime -> spigotTechPlugin.getScheduler().runTaskTimer(() -> Bukkit.getPluginManager().callEvent(new UpdateEvent(updateTime)), 0L, updateTime.getTime()));
   }
}
