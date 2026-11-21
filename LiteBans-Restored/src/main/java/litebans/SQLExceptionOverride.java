package litebans;

import java.sql.SQLException;

public interface SQLExceptionOverride {
   default EvictionResult computeEvictionResult(SQLException a) {
      return EvictionResult.CONTINUE_EVICT;
   }
}
