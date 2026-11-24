package io.lumine.mythic.lib.data.json;

import io.lumine.mythic.lib.data.OfflineDataHolder;
import io.lumine.mythic.lib.data.SynchronizedDataHandler;
import io.lumine.mythic.lib.data.SynchronizedDataHolder;
import com.google.gson.JsonObject;
import io.lumine.mythic.lib.util.Jsonable;
import io.lumine.mythic.lib.util.config.JsonFile;
import java.util.Objects;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public abstract class JSONSynchronizedDataHandler<H extends SynchronizedDataHolder & Jsonable, O extends OfflineDataHolder>
   implements SynchronizedDataHandler<H, O> {
   private final Plugin owning;

   public JSONSynchronizedDataHandler(Plugin owning) {
      this.owning = Objects.requireNonNull(owning, "Plugin cannot be null");
   }

   @Deprecated
   public JSONSynchronizedDataHandler(Plugin owning, boolean profilePlugin) {
      this(owning);
   }

   @Override
   public void saveData(@NotNull H playerData, boolean autosave) {
      JsonFile file = this.getUserFile(playerData);
      file.setContent(playerData.toJson());
      file.save();
   }

   @Override
   public boolean loadData(@NotNull H playerData) {
      this.loadFromObject(playerData, this.getUserFile(playerData).getContent());
      return true;
   }

   public abstract void loadFromObject(@NotNull H value, @NotNull JsonObject jsonObject);

   private JsonFile getUserFile(H playerData) {
      return new JsonFile(this.owning, "/userdata", playerData.getEffectiveId().toString());
   }
}
