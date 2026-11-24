package fr.phoenixdevt.profiles;

import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public interface ProfileDataModule extends Listener {
   @NotNull
   JavaPlugin getOwningPlugin();

   @NotNull
   String getIdentifier();
}
