package litebans;

import java.io.File;

public interface Bootstrap {
   ConfigFile loadConfigFile(Platform platform, File file);
}
