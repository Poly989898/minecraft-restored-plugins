package litebans;

import java.io.Closeable;
import java.sql.PreparedStatement;
import org.jetbrains.annotations.NotNull;

public final class PoolStatementWrapperSub extends StatementWrapper {
   private final Closeable closeable2;

   public PoolStatementWrapperSub(@NotNull PreparedStatement preparedStatement, @NotNull Closeable closeable) {
      super(preparedStatement);
      this.closeable2 = closeable;
   }

   @Override
   public void close() {
      try {
         super.close();
      } finally {
         Closeable closeable = this.closeable2;

         try {
            closeable.close();
         } catch (Exception e) {
            throw new RuntimeException(e);
         }
      }
   }
}
