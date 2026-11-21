package litebans;

import java.util.Collection;
import litebans.api.Events$Listener;

public final class KotlinRunnableImpl5 implements Runnable {
   final Collection collection;
   final DatabaseSession databaseSession2;
   final CharSequence message2;
   final String text2;

   public KotlinRunnableImpl5(Collection collection, DatabaseSession databaseSession, CharSequence message, String text) {
      this.collection = collection;
      this.databaseSession2 = databaseSession;
      this.message2 = message;
      this.text2 = text;
   }

   @Override
   public final void run() {
      Iterable iterable = this.collection;
      DatabaseSession databaseSession = this.databaseSession2;

      for (Object obj : iterable) {
         Events$Listener events$Listener = (Events$Listener)obj;

         try {
            events$Listener.broadcastSent(this.message2.toString(), this.text2);
         } catch (Throwable e) {
            KotlinHelper29.KOTLIN_HELPER.handleDatabaseSession(databaseSession, e);
         }
      }
   }
}
