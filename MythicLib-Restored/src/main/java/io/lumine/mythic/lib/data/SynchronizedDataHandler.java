package io.lumine.mythic.lib.data;

import io.lumine.mythic.lib.util.Closeable;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;

public interface SynchronizedDataHandler<H extends SynchronizedDataHolder, O extends OfflineDataHolder> extends Closeable {
   void setup();

   void saveData(@NotNull H var1, boolean var2);

   boolean loadData(@NotNull H var1);

   O getOffline(@NotNull UUID var1);
}
