package io.lumine.mythic.lib.data;

import io.lumine.mythic.lib.util.Closeable;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;

public interface SynchronizedDataHandler<H extends SynchronizedDataHolder, O extends OfflineDataHolder> extends Closeable {
   void setup();

   void saveData(@NotNull H value, boolean flag);

   boolean loadData(@NotNull H value);

   O getOffline(@NotNull UUID uuid);
}
