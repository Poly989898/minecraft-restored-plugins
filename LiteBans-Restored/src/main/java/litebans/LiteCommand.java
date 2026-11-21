package litebans;

import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@PluginMarker
public interface LiteCommand {
   void execute(@NotNull LiteSender liteSender, @NotNull String[] texts);

   String getName();

   @Nullable String getPermission();

   String[] getAliases();

   default @NotNull List suggest(@NotNull LiteSender liteSender, @NotNull String[] texts) {
      return java.util.Collections.emptyList();
   }
}
