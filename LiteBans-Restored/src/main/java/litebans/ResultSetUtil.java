package litebans;

public final class ResultSetUtil {
   private static final ProxyResultSet return2 = new ProxyResultSet();

   public static final ProxyResultSet obtainProxyResultSet() {
      return return2;
   }
}
