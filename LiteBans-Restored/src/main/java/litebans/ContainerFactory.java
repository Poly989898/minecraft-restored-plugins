package litebans;

import java.util.List;
import java.util.Map;

public interface ContainerFactory {
   Map createObjectContainer();

   List createArrayContainer();
}
