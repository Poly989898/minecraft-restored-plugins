package litebans;

import org.jetbrains.annotations.Nullable;

public interface ConfigKeyReader extends ConfigValueReader {
   @Nullable ConfigKeyReader getSection(String text);
}
