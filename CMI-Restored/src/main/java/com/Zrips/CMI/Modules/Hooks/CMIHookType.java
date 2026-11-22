package com.Zrips.CMI.Modules.Hooks;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMILwc;
import com.Zrips.CMI.Modules.ModuleHandling.CMIModule;
import com.Zrips.CMI.Modules.Placeholders.MVdWPlaceholderAPIHook;
import com.Zrips.CMI.Modules.Placeholders.PlaceholderAPIHook;
import com.drtshock.playervaults.vaultmanagement.UUIDVaultManager;
import com.drtshock.playervaults.vaultmanagement.VaultManager;
import com.griefcraft.lwc.LWCPlugin;
import java.util.UUID;
import net.Zrips.CMILib.Messages.CMIMessages;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public enum CMIHookType {
  CMILib,
  BlueMap {
    @Override
    public void update() {
      if (CMI.getInstance().getServer().getPluginManager().getPlugin(this.toString()) != null) {
        CMIHookType.setPresentAndEnabled(this);
        CMI.getInstance().getBlueMapManager();
      }
    }
  },
  AureliumSkills,
  AuraSkills,
  DiscordSRV,
  dynmap,
  Towny,
  mcMMO {
    @Override
    public void update() {
      if (CMI.getInstance().getServer().getPluginManager().getPlugin(this.toString()) != null) {
        try {
          Class.forName("com.gmail.nossr50.api.ExperienceAPI");
          Class.forName("com.gmail.nossr50.api.SkillAPI");
        } catch (Throwable throwable) {
          CMI.getInstance()
              .consoleMessage(
                  this.toString() + " was found - but its not compatible with this CMI version");
          return;
        }

        CMIHookType.setPresentAndEnabled(this);
      }
    }
  },
  Jobs {
    @Override
    public void update() {
      if (CMI.getInstance().getServer().getPluginManager().getPlugin(this.toString()) != null) {
        try {
          Class.forName("com.gamingmesh.jobs.Jobs");
        } catch (Throwable throwable) {
          CMI.getInstance()
              .consoleMessage(
                  this.toString() + " was found - but its not compatible with this CMI version");
          return;
        }

        CMIHookType.setPresentAndEnabled(this);
      }
    }
  },
  Vault,
  LWC {
    @Override
    public void update() {
      if (CMI.getInstance().getServer().getPluginManager().getPlugin(this.toString()) != null) {
        try {
          Plugin plugin = Bukkit.getPluginManager().getPlugin(this.toString());
          if (plugin != null) {
            CMIScheduler.runTask(
                CMI.getInstance(), () -> CMILwc.lwc = ((LWCPlugin) plugin).getLWC());
          }
        } catch (Throwable throwable) {
          CMIMessages.consoleMessage(
              this.toString() + " detected but its outdated. Please update it to latest version.");
          throwable.printStackTrace();
          return;
        }

        CMIHookType.setPresentAndEnabled(this);
      }
    }
  },
  VaultPermissions("Vault") {
    @Override
    public void update() {
      if (CMIHookType.Vault.isPresent()) {
        CMI.getInstance()
            .setVaultPerm(
                (Permission)
                    CMI.getInstance()
                        .getServer()
                        .getServicesManager()
                        .getRegistration(Permission.class)
                        .getProvider());
        this.setSubType(CMIHookSubType.VaultPermissions);
        CMIHookType.setPresentAndEnabled(this);
      }
    }
  },
  PlayerVaults(false) {
    @Override
    public void update() {
      if (CMI.getInstance().getServer().getPluginManager().getPlugin(this.toString()) != null) {
        try {
          try {
            UUIDVaultManager.getInstance()
                .getClass()
                .getMethod("vaultExists", UUID.class, int.class);
            this.setSubType(CMIHookSubType.PlayerVaults);
          } catch (Throwable throwable) {
            try {
              VaultManager.getInstance().getClass().getMethod("vaultExists", UUID.class, int.class);
              this.setSubType(CMIHookSubType.PlayerVaultsX);
            } catch (Throwable throwable2) {
              this.setSubType(CMIHookSubType.PlayerVaultsNBT);
            }
          }
        } catch (Throwable throwable3) {
          CMI.getInstance()
              .consoleMessage(
                  "&7"
                      + this.toString()
                      + " was found - But is incompatible with current CMI version.");
          return;
        }

        CMIHookType.setPresentAndEnabled(this);
      }
    }
  },
  PlaceholderAPI {
    @Override
    public void update() {
      if (CMI.getInstance().getServer().getPluginManager().getPlugin(this.toString()) != null) {
        CMIScheduler.runTask(
            CMI.getInstance(),
            () -> {
              if (new PlaceholderAPIHook(CMI.getInstance()).register()) {
                CMI.getInstance().consoleMessage(this.toString() + " hooked.");
              }
            });
        CMIHookType.setPresentAndEnabled(this);
      }
    }
  },
  MVdWPlaceholderAPI {
    @Override
    public void update() {
      if (CMI.getInstance().getServer().getPluginManager().getPlugin(this.toString()) != null) {
        CMIScheduler.runTask(
            CMI.getInstance(),
            () -> {
              if (MVdWPlaceholderAPIHook.hook(CMI.getInstance())) {
                CMI.getInstance().consoleMessage(this.toString() + " hooked.");
              }
            });
        CMIHookType.setPresentAndEnabled(this);
      }
    }
  },
  Votifier {
    @Override
    public void update() {
      if (CMI.getInstance().getServer().getPluginManager().getPlugin(this.toString()) == null) {
        CMIModule.votifier.setEnabled(false);
      } else {
        CMIHookType.setPresentAndEnabled(this);
      }
    }
  },
  Citizens,
  NoCheatPlus;

  private boolean present = false;
  private boolean enabled = false;
  private boolean showFoundMsg = true;
  private String targetPlugin = null;
  private CMIHookSubType subType = null;

  CMIHookType() {}

  CMIHookType(boolean flag) {
    this.showFoundMsg = flag;
  }

  CMIHookType(String string) {
    this.targetPlugin = string;
  }

  public Plugin getPlugin() {
    return Bukkit.getPluginManager().getPlugin(this.toString());
  }

  @Deprecated
  private static void setPresent(CMIHookType cmiHookType) {
    setPresentAndEnabled(cmiHookType);
  }

  private static void setPresentAndEnabled(CMIHookType cmiHookType) {
    cmiHookType.setPresent(true);
    cmiHookType.setEnabled(
        CMI.getInstance()
            .getServer()
            .getPluginManager()
            .isPluginEnabled(
                cmiHookType.targetPlugin == null
                    ? cmiHookType.toString()
                    : cmiHookType.targetPlugin));
  }

  private static void message(CMIHookType cmiHookType) {
    if (cmiHookType.isPresent() && cmiHookType.showFoundMsg) {
      CMI.getInstance()
          .consoleMessage(
              "&f"
                  + (cmiHookType.getSubType() == null ? cmiHookType : cmiHookType.getSubType())
                      .toString()
                  + "&3 found. (Loaded: "
                  + cmiHookType.isEnabled()
                  + ")");
    }
  }

  public void init() {
    try {
      this.update();
      if (this.isPresent() && !this.isEnabled()) {
        CMIScheduler.runTaskLater(CMI.getInstance(), this::updateEnabled, 1L);
      }

      message(this);
    } catch (Throwable throwable) {
      throwable.printStackTrace();
    }
  }

  private void updateEnabled() {
    this.setEnabled(
        CMI.getInstance()
            .getServer()
            .getPluginManager()
            .isPluginEnabled(this.targetPlugin == null ? this.toString() : this.targetPlugin));
  }

  public void update() {
    if (CMI.getInstance().getServer().getPluginManager().getPlugin(this.toString()) != null) {
      setPresentAndEnabled(this);
    }
  }

  public boolean isPresent() {
    return this.present;
  }

  public boolean isEnabled() {
    return this.enabled;
  }

  public void setPresent(boolean flag) {
    this.present = flag;
  }

  public void setEnabled(boolean flag) {
    this.enabled = flag;
  }

  public CMIHookSubType getSubType() {
    return this.subType;
  }

  public boolean hasSubType() {
    return this.subType != null;
  }

  public void setSubType(CMIHookSubType cmiHookSubType) {
    this.subType = cmiHookSubType;
  }

  public boolean isShowFoundMessage() {
    return this.showFoundMsg;
  }
}
