package litebans;

import java.util.Map;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.representer.Represent;
import org.yaml.snakeyaml.representer.Representer;

public final class ArchiveRepresenterSub extends Representer {
   ArchiveRepresenterSub(DumperOptions dumperOptions) {
      super(dumperOptions);
      Map representers2 = this.representers;
      Class<ConfigSection> class2 = ConfigSection.class;
      Represent represent = obj -> {
         Intrinsics.checkNotNullExpressionValue(obj, "");
         return this.represent(((ConfigSection)obj).keys);
      };
      representers2.put(class2, represent);
   }

}
