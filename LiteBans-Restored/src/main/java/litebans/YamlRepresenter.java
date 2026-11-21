package litebans;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.representer.Representer;

class YamlRepresenter extends Representer {
   final YamlThreadLocal yamlThreadLocal;

   YamlRepresenter(YamlThreadLocal yamlThreadLocal, DumperOptions dumperOptions) {
      super(dumperOptions);
      this.yamlThreadLocal = yamlThreadLocal;
      this.representers.put(ConfigSection.class, new YamlRepresent(this));
   }
}
