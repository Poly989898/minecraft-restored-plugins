package fr.phoenixdevt.mmoprofiles.bukkit.profile.placeholder;

import fr.phoenixdevt.mmoprofiles.bukkit.MMOProfiles;
import fr.phoenixdevt.mmoprofiles.bukkit.profile.PlayerProfileImpl;
import fr.phoenixdevt.mmoprofiles.bukkit.profile.ProfileListImpl;
import fr.phoenixdevt.profiles.placeholder.PlaceholderCache;
import fr.phoenixdevt.profiles.placeholder.PlaceholderProcessor;
import fr.phoenixdevt.mmoprofiles.bukkit.gui.lib.Placeholders;
import io.lumine.mythic.lib.util.Closeable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlaceholderCacheImpl implements PlaceholderCache, Closeable {
   private final Map<String, String> cachedPlaceholders = new ConcurrentHashMap<>();
   private boolean open = true;
   private boolean requestsSent;
   public static final String WAITING = "[...]";

   @Nullable
   public List<PlaceholderRequestImpl> sendRequests(ProfileListImpl player, PlayerProfileImpl profile) {
      Validate.isTrue(this.open, "Placeholder cache is cleared");
      if (this.requestsSent) {
         return null;
      }

      this.requestsSent = true;
      List<PlaceholderRequestImpl> requests = new ArrayList<>();

      for (PlaceholderProcessor processor : MMOProfiles.plugin.getPlaceholders()) {
         PlaceholderRequestImpl req = new PlaceholderRequestImpl(processor.getDataModule(), player, profile);
         processor.processPlaceholderRequest(req);
         requests.add(req);
      }

      return requests;
   }

   @Nullable
   @Override
   public String getPlaceholder(@NotNull String key) {
      Validate.isTrue(this.open, "Placeholder cache is cleared");
      String found = this.cachedPlaceholders.get(key);
      return found == null ? "[...]" : found;
   }

   @Override
   public void registerPlaceholder(@NotNull String key, @NotNull String value) {
      if (this.open) {
         this.cachedPlaceholders.put(key, value);
      }
   }

   public void fillPlaceholders(@NotNull Placeholders placeholders) {
      this.cachedPlaceholders.forEach(placeholders::register);
   }

   public boolean isOpen() {
      return this.open;
   }

   public void close() {
      Validate.isTrue(this.open, "Placeholder cache is cleared");
      this.open = false;
      this.cachedPlaceholders.clear();
   }
}
