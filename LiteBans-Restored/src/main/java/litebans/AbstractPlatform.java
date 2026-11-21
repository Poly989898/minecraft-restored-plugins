package litebans;

import java.util.List;

public abstract class AbstractPlatform implements Platform {
   public static PluginModule[] computePluginModuleArray(Platform platform) {
      List list = platform.getClassUtil().computeList(PluginModule.class, new Class[]{Platform.class}, new Object[]{platform});
      list.removeIf(obj -> !platform.registerModule(((PluginModule) obj)));
      return (PluginModule[]) list.toArray(new PluginModule[list.size()]);
   }

}
