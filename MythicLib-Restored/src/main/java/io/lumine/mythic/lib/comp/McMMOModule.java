package io.lumine.mythic.lib.comp;

import com.gmail.nossr50.events.fake.FakeEvent;
import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.api.event.fake.FakeEventCaller;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class McMMOModule implements Listener, FakeEventCaller<EntityDamageEvent> {
   public McMMOModule() {
      MythicLib.plugin.getFakeEvents().registerFakeEventCaller(EntityDamageEvent.class, this);
   }

   public boolean isFake(EntityDamageEvent event) {
      return event instanceof FakeEvent;
   }
}
