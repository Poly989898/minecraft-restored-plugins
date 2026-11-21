package litebans;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.DumperOptions.FlowStyle;
import org.yaml.snakeyaml.constructor.Constructor;

class YamlThreadLocal extends ThreadLocal {
   final ArchiveHelper3 archiveHelper;

   YamlThreadLocal(ArchiveHelper3 archiveHelper) {
      this.archiveHelper = archiveHelper;
   }

   protected Yaml obtainYaml() {
      DumperOptions dumperOptions = new DumperOptions();
      dumperOptions.setDefaultFlowStyle(FlowStyle.BLOCK);
      YamlRepresenter yamlRepresenter = new YamlRepresenter(this, dumperOptions);
      return new Yaml(new Constructor(new LoaderOptions()), yamlRepresenter, dumperOptions);
   }

   @Override
   protected Object initialValue() {
      return this.obtainYaml();
   }
}
