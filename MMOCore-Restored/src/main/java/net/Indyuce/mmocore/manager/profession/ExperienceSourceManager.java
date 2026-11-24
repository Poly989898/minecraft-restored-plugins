package net.Indyuce.mmocore.manager.profession;

import io.lumine.mythic.lib.util.Closeable;
import java.util.HashSet;
import java.util.Set;
import net.Indyuce.mmocore.MMOCore;
import net.Indyuce.mmocore.experience.source.type.ExperienceSource;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

public abstract class ExperienceSourceManager<T extends ExperienceSource> implements Listener, Closeable {
   private final Set<T> sources = new HashSet<>();
   private boolean open = true;

   public ExperienceSourceManager() {
      Bukkit.getPluginManager().registerEvents(this, MMOCore.plugin);
   }

   public void registerSource(T source) {
      this.sources.add(source);
   }

   public void whenClosed() {
   }

   public void close() {
      Validate.isTrue(this.open, "Manager is already closed");
      this.open = false;
      HandlerList.unregisterAll(this);
      this.whenClosed();
   }

   public Set<T> getSources() {
      return this.sources;
   }
}
