package litebans;

import java.io.InputStream;
import java.util.Set;

public interface ArchiveAInterface {
   ArchiveInputStream createArchiveInputStream(String text, InputStream inputStream, String text2);

   Set getInputStreamArchiveNames();
}
