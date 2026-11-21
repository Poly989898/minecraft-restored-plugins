package litebans;

import java.sql.SQLException;
import java.io.Closeable;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PreparedQuery implements Closeable {
   private final DatabaseSession databaseSession;
   private final PreparedStatement statement;
   private final String sql;
   private byte byteValue;

   private PreparedQuery(DatabaseSession databaseSession, PreparedStatement preparedStatement, String text) {
      this.databaseSession = databaseSession;
      this.statement = preparedStatement;
      this.sql = text;
   }

   public final DatabaseSession getSession() {
      return this.databaseSession;
   }

   public final PreparedStatement getStatement() {
      return this.statement;
   }

   public final String getSql() {
      return this.sql;
   }

   public PreparedQuery bindBoolean(boolean flag) {
      try {
         PreparedStatement preparedStatement = this.statement;
         this.byteValue = UByte.constructorImpl((byte)(this.byteValue + 1));
         preparedStatement.setBoolean(this.byteValue & 255, flag);
         return this;
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   public final PreparedQuery bindBytes(@NotNull byte[] bytes) {
      try {
         PreparedStatement preparedStatement = this.statement;
         this.byteValue = UByte.constructorImpl((byte)(this.byteValue + 1));
         preparedStatement.setBytes(this.byteValue & 255, bytes);
         return this;
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   public final PreparedQuery bindInt(byte b) {
      try {
         PreparedStatement preparedStatement = this.statement;
         this.byteValue = UByte.constructorImpl((byte)(this.byteValue + 1));
         preparedStatement.setInt(this.byteValue & 255, b & 255);
         return this;
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   public final PreparedQuery bindInt(int value) {
      try {
         PreparedStatement preparedStatement = this.statement;
         this.byteValue = UByte.constructorImpl((byte)(this.byteValue + 1));
         preparedStatement.setInt(this.byteValue & 255, value);
         return this;
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   public final PreparedQuery bindLong(long value) {
      try {
         PreparedStatement preparedStatement = this.statement;
         this.byteValue = UByte.constructorImpl((byte)(this.byteValue + 1));
         preparedStatement.setLong(this.byteValue & 255, value);
         return this;
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   public final PreparedQuery bindLong(@NotNull Number number) {
      try {
         PreparedStatement preparedStatement = this.statement;
         this.byteValue = UByte.constructorImpl((byte)(this.byteValue + 1));
         preparedStatement.setLong(this.byteValue & 255, number.longValue());
         return this;
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   public final PreparedQuery bindString(@Nullable String text) {
      try {
         PreparedStatement preparedStatement = this.statement;
         this.byteValue = UByte.constructorImpl((byte)(this.byteValue + 1));
         preparedStatement.setString(this.byteValue & 255, text);
         return this;
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   public final PreparedQuery bindTimestamp(@NotNull Timestamp timestamp) {
      try {
         PreparedStatement preparedStatement = this.statement;
         this.byteValue = UByte.constructorImpl((byte)(this.byteValue + 1));
         preparedStatement.setTimestamp(this.byteValue & 255, timestamp);
         return this;
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   public final PreparedQuery bindUuid(@NotNull UUID uuid) {
      return this.bindBytes(KotlinUtil22.computeBytes(uuid));
   }

   @Override
   public void close() {
      try {
         this.statement.close();
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   public PreparedQuery(DatabaseSession databaseSession, PreparedStatement preparedStatement, String text, DefaultConstructorMarker defaultConstructorMarker) {
      this(databaseSession, preparedStatement, text);
   }
}
