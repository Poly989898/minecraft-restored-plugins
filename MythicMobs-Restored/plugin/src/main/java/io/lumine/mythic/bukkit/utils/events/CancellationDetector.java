package io.lumine.mythic.bukkit.utils.events;

import com.google.common.collect.Lists;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.EventException;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.IllegalPluginAccessException;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredListener;

public class CancellationDetector<TEvent extends Event> {
   private final Class<TEvent> eventClazz;
   private final List<CancellationDetector.CancelListener<TEvent>> listeners = Lists.newArrayList();
   private EnumMap<EventPriority, ArrayList<RegisteredListener>> backup;

   public CancellationDetector(Class<TEvent> eventClazz) {
      this.eventClazz = eventClazz;
      this.injectProxy();
   }

   public void addListener(CancellationDetector.CancelListener<TEvent> listener) {
      this.listeners.add(listener);
   }

   public void removeListener(CancellationDetector.CancelListener<Event> listener) {
      this.listeners.remove(listener);
   }

   private EnumMap<EventPriority, ArrayList<RegisteredListener>> getSlots(HandlerList list) {
      try {
         return (EnumMap<EventPriority, ArrayList<RegisteredListener>>)this.getSlotsField(list).get(list);
      } catch (Exception e) {
         throw new RuntimeException("Unable to retrieve slots.", e);
      }
   }

   private Field getSlotsField(HandlerList list) {
      if (list == null) {
         throw new IllegalStateException("Detected a NULL handler list.");
      } else {
         try {
            Field slotField = list.getClass().getDeclaredField("handlerslots");
            slotField.setAccessible(true);
            return slotField;
         } catch (Exception e) {
            throw new IllegalStateException("Unable to intercept 'handlerslot' in " + list.getClass(), e);
         }
      }
   }

   private void injectProxy() {
      HandlerList list = getHandlerList(this.eventClazz);
      EnumMap<EventPriority, ArrayList<RegisteredListener>> slots = this.getSlots(list);
      this.backup = slots.clone();
      synchronized (list) {
         for (EventPriority p : slots.keySet().toArray(new EventPriority[0])) {
            final EventPriority priority = p;
            ArrayList<RegisteredListener> proxyList = new ArrayList<RegisteredListener>() {
               private static final long serialVersionUID = 7869505892922082581L;

               public boolean add(RegisteredListener e) {
                  super.add(CancellationDetector.this.injectRegisteredListener(e));
                  return CancellationDetector.this.backup.get(priority).add(e);
               }

               @Override
               public boolean remove(Object listener) {
                  Iterator<RegisteredListener> it = this.iterator();

                  while (it.hasNext()) {
                     CancellationDetector.DelegatedRegisteredListener delegated = (CancellationDetector.DelegatedRegisteredListener)it.next();
                     if (delegated.delegate == listener) {
                        it.remove();
                        break;
                     }
                  }

                  return CancellationDetector.this.backup.get(priority).remove(listener);
               }
            };
            slots.put(priority, proxyList);

            for (RegisteredListener listener : this.backup.get(priority)) {
               proxyList.add(listener);
            }
         }
      }
   }

   private RegisteredListener injectRegisteredListener(final RegisteredListener listener) {
      return new CancellationDetector.DelegatedRegisteredListener(listener) {
         @Override
         public void callEvent(Event event) throws EventException {
            if (event instanceof Cancellable) {
               boolean prior = CancellationDetector.this.getCancelState(event);
               listener.callEvent(event);
               if (!prior && CancellationDetector.this.getCancelState(event)) {
                  CancellationDetector.this.invokeCancelled(this.getPlugin(), (TEvent)event);
               }
            } else {
               listener.callEvent(event);
            }
         }
      };
   }

   private void invokeCancelled(Plugin plugin, TEvent event) {
      for (CancellationDetector.CancelListener<TEvent> listener : this.listeners) {
         listener.onCancelled(plugin, event);
      }
   }

   private boolean getCancelState(Event event) {
      return ((Cancellable)event).isCancelled();
   }

   public void close() {
      if (this.backup != null) {
         try {
            HandlerList list = getHandlerList(this.eventClazz);
            this.getSlotsField(list).set(list, this.backup);
            Field handlers = list.getClass().getDeclaredField("handlers");
            handlers.setAccessible(true);
            handlers.set(list, null);
         } catch (Exception e) {
            throw new RuntimeException("Unable to clean up handler list.", e);
         }

         this.backup = null;
      }
   }

   private static HandlerList getHandlerList(Class<? extends Event> clazz) {
      while (clazz.getSuperclass() != null && Event.class.isAssignableFrom(clazz.getSuperclass())) {
         try {
            Method method = clazz.getDeclaredMethod("getHandlerList");
            method.setAccessible(true);
            return (HandlerList)method.invoke(null);
         } catch (NoSuchMethodException e) {
            clazz = clazz.getSuperclass().asSubclass(Event.class);
         } catch (Exception ex) {
            throw new IllegalPluginAccessException(ex.getMessage());
         }
      }

      throw new IllegalPluginAccessException("Unable to find handler list for event " + clazz.getName());
   }

   public interface CancelListener<TEvent extends Event> {
      void onCancelled(Plugin plugin, TEvent tEvent);
   }

   private static class DelegatedRegisteredListener extends RegisteredListener {
      private final RegisteredListener delegate;

      public DelegatedRegisteredListener(RegisteredListener delegate) {
         super(delegate.getListener(), null, delegate.getPriority(), delegate.getPlugin(), false);
         this.delegate = delegate;
      }

      public void callEvent(Event event) throws EventException {
         this.delegate.callEvent(event);
      }

      public Listener getListener() {
         return this.delegate.getListener();
      }

      public Plugin getPlugin() {
         return this.delegate.getPlugin();
      }

      public EventPriority getPriority() {
         return this.delegate.getPriority();
      }

      public boolean isIgnoringCancelled() {
         return this.delegate.isIgnoringCancelled();
      }
   }
}
