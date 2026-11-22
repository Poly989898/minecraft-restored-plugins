package litebans;

import java.util.Collection;
import litebans.api.Entry;
import litebans.api.Events$Listener;

public final class KotlinRunnableImpl6 implements Runnable {
   final Collection collection;
   final DatabaseSession databaseSession;
   final Entry entry;

   public KotlinRunnableImpl6(Collection collection, DatabaseSession databaseSession, Entry entry) {
      this.collection = collection;
      this.databaseSession = databaseSession;
      this.entry = entry;
   }

   @Override
   public final void run() {
      Iterable iterable = this.collection;
      DatabaseSession databaseSession = this.databaseSession;

      for (Object obj : iterable) {
         Events$Listener events$Listener = (Events$Listener)obj;

         try {
            events$Listener.entryRemoved(this.entry);
         } catch (Throwable e) {
            KotlinHelper29.KOTLIN_HELPER.handleDatabaseSession(databaseSession, e);
         }
      }
   }
}
