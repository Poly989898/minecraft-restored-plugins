package litebans;

import java.util.Collection;
import java.util.HashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface LiteAInterface3 {
   CharSequence obtainMessage();

   int computeIntValue(@NotNull Platform platform, @Nullable String[] texts, @NotNull Collection collection, int value);

   void handleDatabaseSession(@NotNull DatabaseSession databaseSession, @NotNull Collection collection, @Nullable LoginRecord loginRecord, @Nullable String[] texts, @NotNull AbstractCommand abstractCommand, int value);

   LoginRecord computeLoginRecord(@NotNull DatabaseSession databaseSession, @NotNull HashMap map, @Nullable String text);
}
