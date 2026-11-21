package io.lumine.mythic.core.skills.auras;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import lombok.Getter;

public class AuraRegistry {
   @Getter
   private final UUID holder;
   @Getter
   private final Map<String, Queue<Aura.AuraTracker>> auras = Maps.newConcurrentMap();
   private long lastAction = System.currentTimeMillis();

   public AuraRegistry(UUID uuid) {
      this.holder = uuid;
   }

   public void registerAura(String name, Aura.AuraTracker buff) {
      this.auras.compute(name, (key, value) -> {
         if (value == null) {
            value = new ConcurrentLinkedQueue<>();
         }

         value.add(buff);
         return value;
      });
   }

   public boolean mergeAura(String name, Aura.AuraTracker buff, boolean sameCasterOnly) {
      Aura.AuraTracker[] mergeTarget = new Aura.AuraTracker[1];
      this.auras.compute(name, (key, value) -> {
         if (value == null) {
            value = new ConcurrentLinkedQueue<>();
         }

         if (!value.isEmpty()) {
            if (sameCasterOnly) {
               for (Aura.AuraTracker t : value) {
                  if (t.getCasterUUID().equals(buff.getCasterUUID())) {
                     mergeTarget[0] = t;
                     break;
                  }
               }
            } else {
               mergeTarget[0] = value.peek();
            }
         }

         if (mergeTarget[0] == null) {
            value.add(buff);
         }

         return value;
      });
      if (mergeTarget[0] != null) {
         mergeTarget[0].merge(buff);
         return true;
      } else {
         return false;
      }
   }

   public void unregisterAura(String name, Aura.AuraTracker buff) {
      this.auras.compute(name, (key, queue) -> {
         if (queue == null) {
            return null;
         } else {
            queue.remove(buff);
            return queue.isEmpty() ? null : queue;
         }
      });
   }

   public boolean hasAura(String name) {
      Queue<Aura.AuraTracker> registry = this.auras.get(name);
      return registry == null ? false : !registry.isEmpty();
   }

   public boolean hasAuraType(String name) {
      for (Queue<Aura.AuraTracker> aura : this.auras.values()) {
         for (Aura.AuraTracker entry : aura) {
            if (entry.getGroup().isPresent() && entry.getGroup().get().equals(name)) {
               return true;
            }
         }
      }

      return false;
   }

   public boolean hasAuraTag(String tag) {
      if (tag == null) {
         return false;
      } else {
         String normalized = tag.trim().toLowerCase();

         for (Queue<Aura.AuraTracker> aura : this.auras.values()) {
            for (Aura.AuraTracker entry : aura) {
               if (entry.hasTag(normalized)) {
                  return true;
               }
            }
         }

         return false;
      }
   }

   public List<Aura.AuraTracker> getAurasByTag(String tag) {
      List<Aura.AuraTracker> found = Lists.newArrayList();
      if (tag == null) {
         return found;
      } else {
         String normalized = tag.trim().toLowerCase();

         for (Queue<Aura.AuraTracker> aura : this.auras.values()) {
            for (Aura.AuraTracker entry : aura) {
               if (entry.hasTag(normalized)) {
                  found.add(entry);
               }
            }
         }

         return found;
      }
   }

   public int getStacks(String name) {
      Queue<Aura.AuraTracker> q = this.auras.get(name);
      if (q != null && !q.isEmpty()) {
         int sum = 0;

         for (Aura.AuraTracker tracker : q) {
            sum += tracker.getStacks();
         }

         return sum;
      } else {
         return 0;
      }
   }

   public void removeStack(String buffName, int a) {
      Queue<Aura.AuraTracker> queue = this.auras.get(buffName);
      if (queue != null) {
         int leftToRemove = a;
         Collection<Aura.AuraTracker> toRemove = Lists.newArrayList();

         for (Aura.AuraTracker tracker : queue) {
            if (tracker.getStacks() <= leftToRemove) {
               leftToRemove -= tracker.getStacks();
               tracker.terminateFromRegistry();
               toRemove.add(tracker);
            } else {
               tracker.setStacks(tracker.getStacks() - leftToRemove);
               leftToRemove = 0;
            }

            if (leftToRemove <= 0) {
               break;
            }
         }

         queue.removeAll(toRemove);
      }
   }

   public void removeStacksOfType(String type, int a) {
      for (Queue<Aura.AuraTracker> queue : this.auras.values()) {
         int leftToRemove = a;
         Collection<Aura.AuraTracker> toRemove = Lists.newArrayList();
         Iterator iterator = queue.iterator();

         while (true) {
            if (iterator.hasNext()) {
               Aura.AuraTracker tracker = (Aura.AuraTracker)iterator.next();
               if (tracker.getGroup().isPresent() && tracker.getGroup().get().equals(type)) {
                  if (tracker.getStacks() <= leftToRemove) {
                     leftToRemove -= tracker.getStacks();
                     tracker.terminateFromRegistry();
                     toRemove.add(tracker);
                  } else {
                     tracker.setStacks(tracker.getStacks() - leftToRemove);
                     leftToRemove = 0;
                  }
               }

               if (leftToRemove > 0) {
                  continue;
               }
            }

            queue.removeAll(toRemove);
            if (leftToRemove <= 0) {
               return;
            }
            break;
         }
      }
   }

   public void removeAll(String buffName) {
      Queue<Aura.AuraTracker> queue = this.auras.get(buffName);
      if (queue != null) {
         queue.forEach(tracker -> tracker.terminateFromRegistry());
         queue.clear();
      }
   }

   public void removeAllOfType(String type) {
      for (Queue<Aura.AuraTracker> aura : this.auras.values()) {
         aura.removeIf(entry -> {
            if (entry.getGroup().isPresent() && entry.getGroup().get().equals(type)) {
               entry.terminateFromRegistry();
               return true;
            } else {
               return false;
            }
         });
      }
   }
}
