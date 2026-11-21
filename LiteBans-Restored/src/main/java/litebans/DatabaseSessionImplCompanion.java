package litebans;

import org.jetbrains.annotations.NotNull;

public final class DatabaseSessionImplCompanion {
   private DatabaseSessionImplCompanion() {
   }

   public final Function1 obtainFunction() {
      return DatabaseSessionImpl.obtainFunction();
   }

   public final int obtainIntValue() {
      return DatabaseSessionImpl.obtainIntValue();
   }

   public final void handleIntValue(int value) {
      DatabaseSessionImpl.handleIntValue(value);
   }

   public final Function1 obtainFunction2() {
      return this.obtainFunction();
   }

   public final Function1 computeFunction(@NotNull Function1 callback) {
      return callback;
   }

   public final Function1 computeFunction(@NotNull Function1 callback, @NotNull Function1 callback2) {
      return this.computeFunction(obj -> {
         PreparedQuery preparedQuery = (PreparedQuery) obj;
         callback.computeObj(preparedQuery);
         return (PreparedQuery)callback2.computeObj(preparedQuery);
      });
   }

   public final Function1 computeFunction2(@NotNull Function1 callback, @NotNull Function1 callback2) {
      return Intrinsics.areEqual(callback, this.obtainFunction()) ? callback2 : this.computeFunction(callback, callback2);
   }

   public final String computeText(@NotNull DatabaseSessionImpl databaseSession, @NotNull String text) {
      String text2 = text;
      boolean areEqual2 = Intrinsics.areEqual(text, "name");
      StringBuilder builder = new StringBuilder().append(text2);
      CharSequence message;
      if (areEqual2) {
         CharSequence message2 = databaseSession.getBroadcastModule().getSqlLikeOperator();
         CharSequence message3 = "?";
         message = "" + message2 + message3;
      } else {
         message = "=?";
      }

      CharSequence string = builder.append((Object)message).toString();
      if (areEqual2) {
         string = String.valueOf(string) + databaseSession.getBroadcastModule().getCollation();
      }

      return (String) string;
   }

   public DatabaseSessionImplCompanion(DefaultConstructorMarker defaultConstructorMarker) {
      this();
   }
}
