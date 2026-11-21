package litebans;

import java.util.Collection;
import java.util.List;

public interface ConfigValueReader {
   boolean contains(String text);

   Object get(String text);

   Object get(String text, Object obj);

   boolean getBoolean(String text);

   boolean getBoolean(String text, boolean flag);

   double getDouble(String text);

   int getInt(String text, int value);

   Collection getKeys(boolean flag);

   long getLong(String text, long value);

   String getString(String text);

   String getString(String text, String text2);

   List getStringList(String text);

   boolean isSet(String text);

   ConfigFile set(String text, Object obj);
}
