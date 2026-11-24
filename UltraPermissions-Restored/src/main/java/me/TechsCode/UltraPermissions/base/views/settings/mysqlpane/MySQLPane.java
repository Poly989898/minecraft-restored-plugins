package me.TechsCode.UltraPermissions.base.views.settings.mysqlpane;

import me.TechsCode.UltraPermissions.base.SpigotTechPlugin;
import me.TechsCode.UltraPermissions.base.dialog.UserInput;
import me.TechsCode.UltraPermissions.base.gui.ActionType;
import me.TechsCode.UltraPermissions.base.gui.Button;
import me.TechsCode.UltraPermissions.base.gui.Model;
import me.TechsCode.UltraPermissions.base.item.XMaterial;
import me.TechsCode.UltraPermissions.base.legacy.Tools;
import me.TechsCode.UltraPermissions.base.misc.Getter;
import me.TechsCode.UltraPermissions.base.misc.Setter;
import me.TechsCode.UltraPermissions.base.mysql.ConnectionFactory;
import me.TechsCode.UltraPermissions.base.mysql.ConnectionTestResult;
import me.TechsCode.UltraPermissions.base.mysql.MySQLCredentials;
import me.TechsCode.UltraPermissions.base.mysql.MySQLRegistry;
import me.TechsCode.UltraPermissions.base.translations.Phrase;
import me.TechsCode.UltraPermissions.base.translations.Words;
import me.TechsCode.UltraPermissions.base.views.RestartView;
import me.TechsCode.UltraPermissions.base.views.settings.SettingsPane;
import me.TechsCode.UltraPermissions.base.views.settings.SettingsView;
import me.TechsCode.UltraPermissions.base.visual.Animation;
import me.TechsCode.UltraPermissions.base.visual.Colors;
import org.bukkit.entity.Player;

public class MySQLPane extends SettingsPane {
   private static final Phrase NAME = Phrase.create("mysqlPane.name", "MySQL Database");
   private static final Phrase HOSTNAME = Phrase.create("mysqlPane.hostname", "hostname");
   private static final Phrase PORT = Phrase.create("mysqlPane.port", "port");
   private static final Phrase DATABASE = Phrase.create("mysqlPane.database", "database");
   private static final Phrase USERNAME = Phrase.create("mysqlPane.username", "username");
   private static final Phrase PASSWORD = Phrase.create("mysqlPane.password", "password");
   private static final Phrase CREDENTIALS_TITLE = Phrase.create("mysqlPane.credentials.title", "Credentials");
   private static final Phrase CREDENTIALS_DESC = Phrase.create(
      "mysqlPane.credentials.desc", "These must be the same for **every** Server", Colors.GRAY, Colors.GREEN
   );
   private static final Phrase CREDENTIALS_CLEAR_ACTION = Phrase.create(
      "mysqlPane.credentials.clearAction", "**Click** to **clear** MySQL Credentials", Colors.GRAY, Colors.AQUA, Colors.RED
   );
   private static final Phrase CREDENTIALS_ENTER_ACTION = Phrase.create(
      "mysqlPane.credentials.enterAction", "**Click** to **enter** MySQL Credentials", Colors.GRAY, Colors.AQUA, Colors.GOLD
   );
   private static final Phrase CREDENTIALS_CONNECTION_URL = Phrase.create("mysqlPane.credentials.connectionsUrl", "Connection Url:", Colors.GRAY);
   private static final Phrase CREDENTIALS_USERNAME = Phrase.create("mysqlPane.credentials.username", "Username: **%**", Colors.GRAY, Colors.YELLOW);
   private static final Phrase CREDENTIALS_PASSWORD = Phrase.create("mysqlPane.credentials.password", "Password: **%**", Colors.GRAY, Colors.YELLOW);
   private static final Phrase ENCRYPTION_TITLE = Phrase.create("mysqlPane.encryption.title", "SSL");
   private static final Phrase ENCRYPTION_ACTION_ENABLE = Phrase.create(
      "mysqlPane.encryption.enableAction", "**Click** to **enable** encryption", Colors.GRAY, Colors.AQUA, Colors.RED
   );
   private static final Phrase ENCRYPTION_ACTION_DISABLE = Phrase.create(
      "mysqlPane.encryption.disableAction", "**Click** to **disable** encryption", Colors.GRAY, Colors.AQUA, Colors.GREEN
   );
   private static final Phrase ENCRYPTION_SSL = Phrase.create("mysqlPane.encryption.ssl", "SSL: **%**", Colors.GRAY, Colors.YELLOW);
   private static final Phrase TEST_TITLE = Phrase.create("mysqlPane.test.title", "Test Changes");
   private static final Phrase TEST_ACTION = Phrase.create("mysqlPane.test.action", "Click to test **changes**", Colors.GRAY, Colors.YELLOW);
   private static final Phrase TEST_HOSTNAME = Phrase.create("mysqlPane.test.hostname", "Hostname: **%**", Colors.GRAY, Colors.WHITE);
   private static final Phrase TEST_PORT = Phrase.create("mysqlPane.test.port", "Port: **%**", Colors.GRAY, Colors.WHITE);
   private static final Phrase TEST_DATABASE = Phrase.create("mysqlPane.test.database", "Database: **%**", Colors.GRAY, Colors.WHITE);
   private static final Phrase TEST_USERNAME = Phrase.create("mysqlPane.test.username", "Username: **%**", Colors.GRAY, Colors.WHITE);
   private static final Phrase TEST_PASSWORD = Phrase.create("mysqlPane.test.password", "Password: **%**", Colors.GRAY, Colors.WHITE);
   private static final Phrase TEST_SERVER_NAME = Phrase.create("mysqlPane.test.serverName", "Server Name: **%**", Colors.GRAY, Colors.WHITE);
   private static final Phrase TEST_SSL = Phrase.create("mysqlPane.test.ssl", "SSL: **%**", Colors.GRAY, Colors.WHITE);
   private static final Phrase TEST_MINIMUM_IDLE = Phrase.create("mysqlPane.test.minimumIdle", "Minimum Idle: **%**", Colors.GRAY, Colors.WHITE);
   private static final Phrase TEST_MAXIMUM_POOL_SIZE = Phrase.create("mysqlPane.test.maximumPoolSize", "Maximum Pool Size: **%**", Colors.GRAY, Colors.WHITE);
   private static final Phrase SAVE_TITLE = Phrase.create("mysqlPane.save.title", "Save");
   private static final Phrase SAVE_ACTION = Phrase.create("mysqlPane.save.action", "Click to **save** changes", Colors.GRAY, Colors.GREEN);
   private static final Phrase LOADING_TITLE = Phrase.create("mysqlPane.loading.title", "Testing Connection");
   private static final Phrase LOADING_DESC = Phrase.create("mysqlPane.loading.desc", "Please wait while the plugin runs all connection tests", Colors.GRAY);
   private static final Phrase COULD_NOT_CONNECT_TITLE = Phrase.create("mysqlPane.couldNotConnect.title", "Could not connect");
   private static final Phrase COULD_NOT_CONNECT_DESC = Phrase.create(
      "mysqlPane.couldNotConnect.desc", "Check if your settings are correct & if the database is accessible", Colors.GRAY
   );
   private static final Phrase COULD_NOT_CONNECT_CONNECTION_ERROR = Phrase.create("mysqlPane.couldNotConnect.connectionError", "Connection Error:", Colors.GRAY);
   private static final Phrase EVERYTHING_SAVED_TITLE = Phrase.create("mysqlPane.everythingSaved.title", "Everything Saved");
   private static final Phrase EVERYTHING_SAVED_DESC = Phrase.create("mysqlPane.everythingSaved.desc", "There are no changes to be saved", Colors.GRAY);
   private static final Phrase CONNECTION_LIMITS_TITLE = Phrase.create("mysqlPane.connectionLimits.title", "Connection Limits");
   private static final Phrase CONNECTION_LIMITS_DESC = Phrase.create(
      "mysqlPane.connectionLimits.desc", "Do not change these limits if you do not know what you are doing", Colors.GRAY
   );
   private static final Phrase CONNECTION_LIMITS_INCREASE_ACTION = Phrase.create(
      "mysqlPane.connectionLimits.increaseAction", "**Left Click** to **increase**", Colors.GRAY, Colors.AQUA, Colors.GREEN
   );
   private static final Phrase CONNECTION_LIMITS_DECREASE_ACTION = Phrase.create(
      "mysqlPane.connectionLimits.decreaseAction", "**Right Click** to **decrease**", Colors.GRAY, Colors.AQUA, Colors.RED
   );
   private static final Phrase CONNECTION_LIMITS_SWITCH_ACTION = Phrase.create(
      "mysqlPane.connectionLimits.switchAction", "**Press Q** to switch option", Colors.GRAY, Colors.AQUA
   );
   private static final Phrase CONNECTION_LIMITS_SETTING_MINIMUM_IDLE_CONNECTIONS = Phrase.create(
      "mysqlPane.connectionLimits.setting.minimumIdleConnections", "Minimum Idle Connections"
   );
   private static final Phrase CONNECTION_LIMITS_SETTING_MAXIMUM_ACTIVE_CONNECTIONS = Phrase.create(
      "mysqlPane.connectionLimits.setting.maximumActiveConnections", "Maximum Active Connections"
   );
   private static final Phrase DISABLE_TITLE = Phrase.create("mysqlPane.disable.title", "Deactivate");
   private static final Phrase DISABLE_ACTION = Phrase.create("mysqlPane.disable.action", "Click to disable MySQL", Colors.GRAY);
   private static final Phrase DISABLE_DESC = Phrase.create("mysqlPane.disable.desc", "You will be asked to restart to apply the changes");
   private static final Phrase SETUP_TITLE = Phrase.create("mysqlPane.setup.title", "Setup MySQL");
   private static final Phrase SETUP_ACTION = Phrase.create("mysqlPane.setup.action", "Click to enable MySQL", Colors.GRAY);
   private static final Phrase SETUP_DESC = Phrase.create("mysqlPane.setup.desc", "This requires an external MySQL Database to connect to", Colors.GRAY);
   private static final Phrase SERVER_NAME_TITLE = Phrase.create("mysqlPane.serverName.title", "Server Name");
   private static final Phrase SERVER_NAME_CLICK = Phrase.create(
      "mysqlPane.serverName.click", "**Click** to **change** the server name", Colors.GRAY, Colors.AQUA, Colors.GOLD
   );
   private static final Phrase SERVER_NAME_SET_SERVER_NAME = Phrase.create("mysqlPane.serverName.setServerName", "Server Name: %setServerName%", Colors.GRAY);
   private static final Phrase SERVER_NAME_DESC = Phrase.create(
      "mysqlPane.serverName.des1c", "Name this server the **exact same** as configured in", Colors.GRAY, Colors.RED
   );
   private static final Phrase SERVER_NAME_DESC2 = Phrase.create(
      "mysqlPane.serverName.desc2", "your **proxy settings** to identify it in your network", Colors.GRAY, Colors.WHITE
   );
   private static final Phrase SERVER_NAME_ACTION_TITLE_MAIN = Phrase.create("mysqlPane.serverName.action.title.main", "Server Name", Colors.AQUA);
   private static final Phrase SERVER_NAME_ACTION_TITLE_SUB = Phrase.create(
      "mysqlPane.serverName.action.title.sub", "Enter the name of this server", Colors.GRAY
   );
   private static final Phrase SERVER_NAME_ACTION_TITLE_ACTIONBAR = Phrase.create(
      "mysqlPane.serverName.action.title.actionbar",
      "The server name **must be identical** to the server name assigned in the **proxy configuration**",
      Colors.GRAY,
      Colors.RED,
      Colors.WHITE
   );
   private final SpigotTechPlugin plugin;
   private final MySQLRegistry registry;
   private MySQLCredentials credentials;
   private boolean ssl;
   private int minimumIdle;
   private int maximumPoolSize;
   private String serverName;
   private boolean intro;
   private boolean loading;
   private int poolConfigurationNum;
   private MySQLPane.SaveState state;
   private String error;

   public MySQLPane(Player player, SettingsView settingsView, SpigotTechPlugin spigotTechPlugin) {
      super(player, settingsView);
      this.plugin = spigotTechPlugin;
      this.registry = spigotTechPlugin.getMySQLManager().getRegistry();
      this.serverName = this.registry.getServerName();
      this.credentials = this.registry.getCredentials();
      this.ssl = this.registry.hasSSL();
      this.minimumIdle = this.registry.getMinimumIdle();
      this.maximumPoolSize = this.registry.getMaximumPoolSize();
      this.intro = this.credentials == null && this.serverName == null;
      this.state = MySQLPane.SaveState.SAVED;
   }

   @Override
   public String getName() {
      return NAME.get();
   }

   @Override
   public XMaterial getIcon() {
      return XMaterial.ENDER_CHEST;
   }

   @Override
   public void construct(Model model) {
      if (this.intro) {
         model.button(23, this::StartSetupButton);
      } else {
         model.button(20, this::CredentialsButton);
         model.button(21, this::SSLButton);
         model.button(22, this::ConnectionLimitsButton);
         model.button(23, this::ServerNameButton);
         boolean isEnabled = this.plugin.getMySQLManager().isEnabled();
         model.button(isEnabled ? 25 : 26, this.loading ? this::LoadingButton : this::ActionButton);
         if (isEnabled) {
            model.button(26, this::DisableButton);
         }
      }
   }

   private void StartSetupButton(Button button) {
      button.material(XMaterial.EMERALD_BLOCK).name(Animation.wave(SETUP_TITLE.get(), Colors.Gold, Colors.YELLOW)).lore(SETUP_ACTION.get(), "");
      button.item().appendLore(SETUP_DESC.split(25));
      button.action(actionType -> this.intro = false);
   }

   private void CredentialsButton(Button button) {
      button.material(XMaterial.PAPER)
         .name(Animation.wave(CREDENTIALS_TITLE.get(), Colors.Gold, Colors.YELLOW))
         .lore(this.credentials != null ? CREDENTIALS_CLEAR_ACTION.get() : CREDENTIALS_ENTER_ACTION.get(), "");
      if (this.credentials != null) {
         String hostname = this.credentials.getHostname();
         String port = this.credentials.getPort();
         String database = this.credentials.getDatabase();
         String username = this.credentials.getUsername();
         String secretPassword = Tools.getSecretPassword(this.credentials.getPassword());
         button.item()
            .appendLore(
               CREDENTIALS_CONNECTION_URL.get(),
               Colors.WHITE + hostname + ":" + port + "/" + database,
               "",
               CREDENTIALS_USERNAME.get().replace("%", username),
               CREDENTIALS_PASSWORD.get().replace("%", secretPassword),
               ""
            );
      }

      button.item().appendLore(CREDENTIALS_DESC.get());
      button.action(
         actionType -> {
            if (this.credentials != null) {
               this.credentials = null;
               this.state = MySQLPane.SaveState.UNSAVED_CHANGES;
            } else {
               final MySQLCredentials mySQLCredentials = new MySQLCredentials("", "", "", "", "");
               new MySQLSetup(this.p, this.plugin) {
                  @Override
                  public void onClose() {
                     MySQLPane.this.reopen();
                  }

                  @Override
                  public void onCompletion() {
                     MySQLPane.this.credentials = mySQLCredentials;
                     MySQLPane.this.state = MySQLPane.SaveState.UNSAVED_CHANGES;
                     MySQLPane.this.reopen();
                  }

                  @Override
                  public SetupStage[] getStages() {
                     return new SetupStage[]{
                        new SetupStage(MySQLPane.HOSTNAME.firstUpper(), "localhost", mySQLCredentials::setHostname),
                        new SetupStage(MySQLPane.PORT.firstUpper(), "3306", mySQLCredentials::setPort),
                        new SetupStage(MySQLPane.DATABASE.firstUpper(), "database", mySQLCredentials::setDatabase),
                        new SetupStage(MySQLPane.USERNAME.firstUpper(), "root", mySQLCredentials::setUsername),
                        new SetupStage(MySQLPane.PASSWORD.firstUpper(), "yourpassword", mySQLCredentials::setPassword)
                     };
                  }
               };
            }
         }
      );
   }

   private void SSLButton(Button button) {
      button.material(XMaterial.REPEATER)
         .name(Animation.wave(ENCRYPTION_TITLE.get(), Colors.Gold, Colors.YELLOW))
         .lore(
            (this.ssl ? ENCRYPTION_ACTION_DISABLE : ENCRYPTION_ACTION_ENABLE).get(),
            "",
            ENCRYPTION_SSL.get().replace("%", (this.ssl ? Words.ENABLED : Words.DISABLED).get())
         );
      button.action(actionType -> {
         this.ssl = !this.ssl;
         this.state = MySQLPane.SaveState.UNSAVED_CHANGES;
      });
   }

   private void ConnectionLimitsButton(Button button) {
      class PoolSetting {
         private final String name;
         private final Getter<Integer> getValue;
         private final Setter<Integer> setValue;

         private PoolSetting(String text, Getter<Integer> getter, Setter<Integer> setter) {
            this.name = text;
            this.getValue = getter;
            this.setValue = setter;
         }
      }

      PoolSetting[] poolSettings = new PoolSetting[]{
         new PoolSetting(CONNECTION_LIMITS_SETTING_MINIMUM_IDLE_CONNECTIONS.get(), () -> this.minimumIdle, value -> this.minimumIdle = value),
         new PoolSetting(CONNECTION_LIMITS_SETTING_MAXIMUM_ACTIVE_CONNECTIONS.get(), () -> this.maximumPoolSize, value -> this.maximumPoolSize = value)
      };
      PoolSetting poolSetting = poolSettings[this.poolConfigurationNum];
      button.material(XMaterial.WATER_BUCKET)
         .name(Animation.wave(CONNECTION_LIMITS_TITLE.get(), Colors.Gold, Colors.YELLOW))
         .lore(CONNECTION_LIMITS_INCREASE_ACTION.get(), CONNECTION_LIMITS_DECREASE_ACTION.get(), CONNECTION_LIMITS_SWITCH_ACTION.get(), "");

      for (PoolSetting candidate : poolSettings) {
         if (candidate.equals(poolSetting)) {
            button.item().appendLore(Colors.GREEN + "\u00a7l> " + Colors.WHITE + candidate.name + ": " + Colors.YELLOW + candidate.getValue.get());
         } else {
            button.item().appendLore(Colors.GRAY + " " + candidate.name + ": " + Colors.YELLOW + candidate.getValue.get());
         }
      }

      button.item().appendLore("");
      button.item().appendLore(CONNECTION_LIMITS_DESC.split(45));
      button.action(ActionType.LEFT, () -> {
         poolSetting.setValue.set(poolSetting.getValue.get() + 1);
         this.state = MySQLPane.SaveState.UNSAVED_CHANGES;
      });
      button.action(ActionType.RIGHT, () -> {
         poolSetting.setValue.set(poolSetting.getValue.get() == 0 ? 0 : poolSetting.getValue.get() - 1);
         this.state = MySQLPane.SaveState.UNSAVED_CHANGES;
      });
      button.action(ActionType.Q, () -> {
         if (this.poolConfigurationNum < 1) {
            this.poolConfigurationNum++;
         } else {
            this.poolConfigurationNum = 0;
         }
      });
   }

   private void ServerNameButton(Button button) {
      button.material(XMaterial.NAME_TAG)
         .name(Animation.wave(SERVER_NAME_TITLE.get(), Colors.GOLD, Colors.YELLOW))
         .lore(
            SERVER_NAME_CLICK.get(),
            "",
            SERVER_NAME_SET_SERVER_NAME.get()
               .replace("%setServerName%", this.serverName == null ? Colors.RED + Words.UNDEFINED.get() : Colors.WHITE + this.serverName),
            "",
            SERVER_NAME_DESC.get(),
            SERVER_NAME_DESC2.get()
         );
      button.action(
         actionType -> new UserInput(
            this.p, this.plugin, SERVER_NAME_ACTION_TITLE_MAIN.get(), SERVER_NAME_ACTION_TITLE_SUB.get(), SERVER_NAME_ACTION_TITLE_ACTIONBAR.get()
         ) {
            @Override
            public void onClose(Player player) {
               MySQLPane.this.reopen();
            }

            @Override
            public boolean onResult(String text) {
               MySQLPane.this.serverName = text;
               MySQLPane.this.state = MySQLPane.SaveState.UNSAVED_CHANGES;
               MySQLPane.this.reopen();
               return true;
            }
         }
      );
   }

   private void EverythingSavedButton(Button button) {
      button.material(XMaterial.STONE).name(Animation.wave(EVERYTHING_SAVED_TITLE.get(), Colors.Gray, Colors.WHITE)).lore(EVERYTHING_SAVED_DESC.get());
   }

   private void TestChangesButton(Button button) {
      String text = this.credentials != null ? this.credentials.getHostname() : "-";
      String port = this.credentials != null ? this.credentials.getPort() : "-";
      String database = this.credentials != null ? this.credentials.getDatabase() : "-";
      String username = this.credentials != null ? this.credentials.getUsername() : "-";
      String password = this.credentials != null ? Tools.getSecretPassword(this.credentials.getPassword()) : "-";
      String serverName = this.serverName != null ? this.serverName : "-";
      button.material(XMaterial.EMERALD_ORE)
         .name(Animation.wave(TEST_TITLE.get(), Colors.GREEN, Colors.WHITE))
         .lore(
            TEST_ACTION.get(),
            "",
            TEST_HOSTNAME.get().replace("%", text),
            TEST_PORT.get().replace("%", port),
            TEST_DATABASE.get().replace("%", database),
            TEST_USERNAME.get().replace("%", username),
            TEST_PASSWORD.get().replace("%", password),
            "",
            TEST_SERVER_NAME.get().replace("%", serverName),
            "",
            TEST_SSL.get().replace("%", (this.ssl ? Words.ENABLED : Words.DISABLED).get()),
            "",
            TEST_MINIMUM_IDLE.get().replace("%", this.minimumIdle + ""),
            TEST_MAXIMUM_POOL_SIZE.get().replace("%", this.maximumPoolSize + "")
         );
      button.action(actionType -> this.plugin.getScheduler().runAsync(() -> {
         if (this.credentials == null) {
            this.state = MySQLPane.SaveState.INVALID_UNSAVED_CHANGES;
            this.error = "No credentials provided.";
         } else if (this.serverName == null) {
            this.state = MySQLPane.SaveState.INVALID_UNSAVED_CHANGES;
            this.error = "No server name provided.";
         } else {
            this.loading = true;
            ConnectionFactory connectionFactory = new ConnectionFactory(this.credentials, this.ssl, this.minimumIdle, this.maximumPoolSize);
            ConnectionTestResult connectionTestResult = connectionFactory.testConnection();
            connectionFactory.close();
            this.loading = false;
            if (connectionTestResult.isValid()) {
               this.state = MySQLPane.SaveState.CORRECT_UNSAVED_CHANGES;
            } else {
               this.state = MySQLPane.SaveState.INVALID_UNSAVED_CHANGES;
               this.error = connectionTestResult.getError();
            }
         }
      }));
   }

   private void SaveChangesButton(Button button) {
      button.material(XMaterial.EMERALD_BLOCK).name(Animation.wave(SAVE_TITLE.get(), Colors.GREEN, Colors.WHITE)).lore(SAVE_ACTION.get());
      button.action(actionType -> {
         this.registry.setCredentials(this.credentials);
         this.registry.setSSL(this.ssl);
         this.registry.setMinimumIdle(this.minimumIdle);
         this.registry.setMaximumPoolSize(this.maximumPoolSize);
         this.registry.setServerName(this.serverName);
         this.state = MySQLPane.SaveState.SAVED;
      });
   }

   private void CouldNotConnectButton(Button button) {
      button.material(XMaterial.REDSTONE_BLOCK)
         .name(Animation.wave(COULD_NOT_CONNECT_TITLE.get(), Colors.RED, Colors.WHITE))
         .lore(COULD_NOT_CONNECT_DESC.get(), "", COULD_NOT_CONNECT_CONNECTION_ERROR.get());

      for (String text : Tools.lineSplitter(this.error, 60)) {
         button.item().appendLore(Colors.WHITE + text);
      }
   }

   private void ActionButton(Button button) {
      switch (this.state) {
         case SAVED:
            this.EverythingSavedButton(button);
            break;
         case UNSAVED_CHANGES:
            this.TestChangesButton(button);
            break;
         case CORRECT_UNSAVED_CHANGES:
            this.SaveChangesButton(button);
            break;
         case INVALID_UNSAVED_CHANGES:
            this.CouldNotConnectButton(button);
      }
   }

   private void LoadingButton(Button button) {
      button.material(XMaterial.EXPERIENCE_BOTTLE).name(Animation.fading(LOADING_TITLE.get(), true, 3, 6, Colors.YELLOW, Colors.WHITE)).lore(LOADING_DESC.get());
   }

   private void DisableButton(Button button) {
      button.material(XMaterial.RED_STAINED_GLASS).name(Animation.wave(DISABLE_TITLE.get(), Colors.RED, Colors.WHITE)).lore(DISABLE_ACTION.get(), "");
      button.item().appendLore(DISABLE_DESC.get());
      button.action(actionType -> {
         this.credentials = null;
         this.registry.clearCredentials();
         this.state = MySQLPane.SaveState.SAVED;
         new RestartView(this.p, this.plugin);
      });
   }

   public enum SaveState {
      SAVED,
      UNSAVED_CHANGES,
      CORRECT_UNSAVED_CHANGES,
      INVALID_UNSAVED_CHANGES;

      SaveState() {
      }
   }
}
