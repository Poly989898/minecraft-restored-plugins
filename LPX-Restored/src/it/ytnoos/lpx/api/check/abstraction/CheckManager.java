package it.ytnoos.lpx.api.check.abstraction;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import it.ytnoos.lpx.api.LPXPlugin;
import it.ytnoos.lpx.api.check.RegisteredCheck;
import it.ytnoos.lpx.api.check.enums.CheckType;
import it.ytnoos.lpx.api.check.options.CheckOptions;
import it.ytnoos.lpx.api.player.ProtocolPlayer;
import it.ytnoos.lpx.api.protocol.CheckContainer;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class CheckManager {
   public static final CheckContainer<?> EMPTY_CONTAINER = new CheckContainer();
   private final LPXPlugin plugin;
   private final Map<Object, List<RegisteredCheck<?>>> checks = Maps.newHashMap();

   protected CheckManager(LPXPlugin lpxPlugin) {
      this.plugin = lpxPlugin;
   }

   public void register(Object object, CheckFunction checkFunction, CheckType checkType) {
      this.checks.computeIfAbsent(object, object2 -> Lists.newArrayList()).add(new RegisteredCheck(checkType, new CheckOptions(this.plugin, checkType), checkFunction));
   }

   public void sortByPriority() {
      for (List list : this.checks.values()) {
         list.sort(Comparator.comparingInt(object -> ((RegisteredCheck)object).getType().getPriority()));
      }
   }

   public Map<Object, CheckContainer<?>> createPlayerChecks(ProtocolPlayer protocolPlayer) {
      HashMap hashMap = Maps.newHashMap();
      this.checks.forEach((object, list) -> {
         CheckContainer checkContainer = new CheckContainer();

         for (RegisteredCheck registeredCheck : list) {
            checkContainer.add(registeredCheck.getFunction().apply(protocolPlayer, registeredCheck.getOptions()));
         }

         hashMap.put(object, checkContainer);
      });
      return hashMap;
   }

   public Map<Object, List<RegisteredCheck<?>>> getChecks() {
      return this.checks;
   }

   public abstract void registerChecks();

}
