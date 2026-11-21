package litebans;

import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.representer.Represent;

class YamlRepresent implements Represent {
   final YamlRepresenter yamlRepresenter;

   YamlRepresent(YamlRepresenter yamlRepresenter) {
      this.yamlRepresenter = yamlRepresenter;
   }

   public Node representData(Object obj) {
      return this.yamlRepresenter.represent(((ConfigSection)obj).keys);
   }
}
