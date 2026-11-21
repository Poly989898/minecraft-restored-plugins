package io.lumine.mythic.core.skills.variables.referencemanagers;

import com.google.common.collect.Lists;
import io.lumine.mythic.core.skills.variables.Variable;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class VariableListReferenceManager<T extends Variable> {
   private final List<WeakReference<T>> list = Lists.newCopyOnWriteArrayList();

   public void register(T var) {
      this.list.add(new WeakReference<>(var));
   }

   public void cleanup() {
      this.list.removeIf(TWeakReference -> TWeakReference.get() == null);
   }

   public List<T> getAllAlive() {
      this.cleanup();
      List<T> alive = new ArrayList<>();

      for (WeakReference<T> ref : this.list) {
         T obj = ref.get();
         if (obj != null) {
            alive.add(obj);
         }
      }

      return alive;
   }

   public void reloadAll() {
   }
}
