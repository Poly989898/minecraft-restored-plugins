package litebans;

import java.io.Reader;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;
import org.jetbrains.annotations.NotNull;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.DumperOptions.FlowStyle;
import org.yaml.snakeyaml.constructor.BaseConstructor;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.representer.Representer;

public final class ArchiveSupplierImpl implements Supplier {
   private final Reader reader;
   private final Map map;

   public ArchiveSupplierImpl(@NotNull Reader reader2) {
      this.reader = reader2;
      DumperOptions dumperOptions = new DumperOptions();
      dumperOptions.setDefaultFlowStyle(FlowStyle.BLOCK);
      Representer archiveRepresenterSub = new ArchiveRepresenterSub(dumperOptions);
      Yaml yaml = new Yaml((BaseConstructor)(new Constructor(new LoaderOptions())), archiveRepresenterSub, dumperOptions);
      Map as = (Map)yaml.loadAs(this.reader, LinkedHashMap.class);
      if (as == null) {
         as = new LinkedHashMap();
      }

      this.map = as;
   }

   public final Reader getReader() {
      return this.reader;
   }

   public Map obtainMap() {
      return this.map;
   }

   @Override
   public Object get() {
      return this.obtainMap();
   }
}
