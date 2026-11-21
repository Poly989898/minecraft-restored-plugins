package litebans;

import java.util.ResourceBundle;
import java.util.function.Supplier;
import java.util.logging.Filter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class PoolLogger extends Logger {
   public static boolean flag2 = false;
   private static Logger value = Logger.getLogger("HikariLogger");
   private static PoolLogger return2 = new PoolLogger("HikariLogger");

   protected PoolLogger(String text) {
      super(text, null);
   }

   public static PoolLogger computePoolLogger(Class class2) {
      return return2;
   }

   public static void handleLogger(Logger logger) {
      value = logger;
   }

   @Override
   public void addHandler(Handler handler) {
      value.addHandler(handler);
   }

   @Override
   public void config(String text) {
      value.config(text);
   }

   public void handleText(String text, Object[] objs) {
   }

   @Override
   public void entering(String text, String text2) {
      value.entering(text, text2);
   }

   @Override
   public void entering(String text, String text2, Object obj) {
      value.entering(text, text2, obj);
   }

   @Override
   public void entering(String text, String text2, Object[] objs) {
      value.entering(text, text2, objs);
   }

   public void handleText2(String text, Object[] objs) {
      value.log(Level.SEVERE, String.format(text, objs));
   }

   @Override
   public void exiting(String text, String text2) {
      value.exiting(text, text2);
   }

   @Override
   public void exiting(String text, String text2, Object obj) {
      value.exiting(text, text2, obj);
   }

   @Override
   public void fine(String text) {
      value.fine(text);
   }

   @Override
   public void fine(Supplier supplier) {
      value.fine(supplier);
   }

   @Override
   public void finer(String text) {
      value.finer(text);
   }

   @Override
   public void finer(Supplier supplier) {
      value.finer(supplier);
   }

   @Override
   public void finest(String text) {
   }

   @Override
   public void finest(Supplier supplier) {
   }

   @Override
   public Filter getFilter() {
      return value.getFilter();
   }

   @Override
   public void setFilter(Filter filter) {
      value.setFilter(filter);
   }

   @Override
   public Handler[] getHandlers() {
      return value.getHandlers();
   }

   @Override
   public Level getLevel() {
      return value.getLevel();
   }

   @Override
   public void setLevel(Level level) {
      value.setLevel(level);
   }

   @Override
   public String getName() {
      return value.getName();
   }

   @Override
   public Logger getParent() {
      return value.getParent();
   }

   @Override
   public void setParent(Logger logger) {
      value.setParent(logger);
   }

   @Override
   public ResourceBundle getResourceBundle() {
      return value.getResourceBundle();
   }

   @Override
   public void setResourceBundle(ResourceBundle resourceBundle) {
      value.setResourceBundle(resourceBundle);
   }

   @Override
   public String getResourceBundleName() {
      return value.getResourceBundleName();
   }

   @Override
   public boolean getUseParentHandlers() {
      return value.getUseParentHandlers();
   }

   @Override
   public void setUseParentHandlers(boolean flag) {
      value.setUseParentHandlers(flag);
   }

   public void handleText3(String text, Object[] objs) {
      this.log(Level.INFO, String.format(text, objs));
   }

   @Override
   public void info(String text) {
      value.info(text);
   }

   @Override
   public void info(Supplier supplier) {
      value.info(supplier);
   }

   public boolean obtainFlag() {
      return false;
   }

   @Override
   public boolean isLoggable(Level level) {
      return value.isLoggable(level);
   }

   @Override
   public void log(LogRecord logRecord) {
      value.log(logRecord);
   }

   @Override
   public void log(Level level, String text) {
      value.log(level, text);
   }

   @Override
   public void log(Level level, Supplier supplier) {
      value.log(level, supplier);
   }

   @Override
   public void log(Level level, String text, Object obj) {
      value.log(level, text, obj);
   }

   @Override
   public void log(Level level, String text, Object[] objs) {
      value.log(level, text, objs);
   }

   @Override
   public void log(Level level, String text, Throwable error) {
      value.log(level, text, error);
   }

   @Override
   public void log(Level level, Throwable error, Supplier supplier) {
      value.log(level, error, supplier);
   }

   @Override
   public void logp(Level level, String text, String text2, String text3) {
      value.logp(level, text, text2, text3);
   }

   @Override
   public void logp(Level level, String text, String text2, Supplier supplier) {
      value.logp(level, text, text2, supplier);
   }

   @Override
   public void logp(Level level, String text, String text2, String text3, Object obj) {
      value.logp(level, text, text2, text3, obj);
   }

   @Override
   public void logp(Level level, String text, String text2, String text3, Object[] objs) {
      value.logp(level, text, text2, text3, objs);
   }

   @Override
   public void logp(Level level, String text, String text2, String text3, Throwable error) {
      value.logp(level, text, text2, text3, error);
   }

   @Override
   public void logp(Level level, String text, String text2, Throwable error, Supplier supplier) {
      value.logp(level, text, text2, error, supplier);
   }

   @Override
   public void logrb(Level level, String text, String text2, ResourceBundle resourceBundle, String text3, Object[] objs) {
      value.logrb(level, text, text2, resourceBundle, text3, objs);
   }

   @Override
   public void logrb(Level level, String text, String text2, ResourceBundle resourceBundle, String text3, Throwable error) {
      value.logrb(level, text, text2, resourceBundle, text3, error);
   }

   @Override
   public void removeHandler(Handler handler) {
      value.removeHandler(handler);
   }

   @Override
   public void severe(String text) {
      value.severe(text);
   }

   @Override
   public void severe(Supplier supplier) {
      value.severe(supplier);
   }

   @Override
   public void throwing(String text, String text2, Throwable error) {
      value.throwing(text, text2, error);
   }

   public void handleText4(String text, Object[] objs) {
      this.log(Level.WARNING, String.format(text, objs));
   }

   @Override
   public void warning(String text) {
      value.warning(text);
   }

   @Override
   public void warning(Supplier supplier) {
      value.warning(supplier);
   }
}
