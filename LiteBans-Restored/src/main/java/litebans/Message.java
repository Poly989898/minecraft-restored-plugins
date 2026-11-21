package litebans;

import java.util.Locale;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum Message implements CharSequence {
   CMD("command"),
   C_BAN(CMD, "ban"),
   C_MUTE(CMD, "mute"),
   C_WARN(CMD, "warn"),
   C_KICK(CMD, "kick"),
   C_HISTORY(CMD, "history"),
   C_STAFFHISTORY(CMD, "staffhistory"),
   C_WARNINGS(CMD, "warnings"),
   C_PRUNEHISTORY(CMD, "prunehistory"),
   C_STAFFROLLBACK(CMD, "staffrollback"),
   C_BANLIST(CMD, "banlist"),
   C_IPHISTORY(CMD, "iphistory"),
   C_NAMEHISTORY(CMD, "namehistory"),
   C_DUPEIP(CMD, "dupeip"),
   C_IPREPORT(CMD, "ipreport"),
   C_CHECKBAN(CMD, "checkban"),
   C_CHECKMUTE(CMD, "checkmute"),
   C_CHECKWARN(CMD, "checkwarn"),
   C_LASTUUID(CMD, "lastuuid"),
   C_LOCKDOWN(CMD, "lockdown"),
   C_GEOIP(CMD, "geoip"),
   C_LITEBANS(CMD, "litebans"),
   HEX_FORMATS("hex_formats", "&%s\n<%s>\n{%s}"),
   DATE_FORMAT("time_format", "yyyy-MM-dd"),
   DEFAULT_BAN_REASON("default_ban_reason", "The Ban Hammer has spoken!"),
   DEFAULT_MUTE_REASON("default_mute_reason", "Spamming"),
   DEFAULT_UNBAN_REASON("default_unban_reason", "No reason specified."),
   DEFAULT_UNMUTE_REASON("default_unmute_reason", "No reason specified."),
   DEFAULT_UNWARN_REASON("default_unwarn_reason", "No reason specified."),
   c5_f(
      "banned_message_base",
      LiteColor.RED + "You are banned from this server!" + LiteColor.WHITE + "\n\nBanned on: $dateStart\nBanned by: $executor\nReason: $reason" + LiteColor.WHITE + '\n'
   ),
   BANNED_MESSAGE("banned_message", "$base\nExpires in: $duration\n$appealMessage"),
   BANNED_MESSAGE_PERMANENT("banned_message_permanent", "$base\nYou are permanently banned!\n$appealMessage"),
   BANNED_MESSAGE_APPEAL("banned_message_appeal_message", ""),
   BANNED_MESSAGE_GEOIP_BLACKLIST("banned_message_geoip_blacklist", LiteColor.RED + "You are banned from this server!" + LiteColor.WHITE + "\n\nYour location is blacklisted: $geoip"),
   PROXY_SWITCH_BANNED("proxy_switch_banned", LiteColor.RED + "You are banned on $serverScope! Reason:\n$reason"),
   PROXY_SWITCH_TEMPBANNED("proxy_switch_tempbanned", LiteColor.RED + "You are banned on $serverScope! Reason:\n$reason\nExpires in: $duration"),
   MUTED("muted", LiteColor.RED + "You are muted! ($duration remaining)!\n" + LiteColor.RED + "Reason: " + LiteColor.YELLOW + "$reason"),
   MUTED_PERMANENT("muted_permanent", LiteColor.RED + "You are permanently muted!\n" + LiteColor.RED + "Reason: " + LiteColor.YELLOW + "$reason"),
   WARNED_JOIN("warned_join", LiteColor.RED + "You have new warnings:"),
   az_f(
      "warned_join_entry",
      LiteColor.DARK_RED
         + " - Warned by "
         + LiteColor.RED
         + "$executor"
         + LiteColor.DARK_RED
         + ": "
         + LiteColor.RED
         + "$reason\n   "
         + LiteColor.DARK_RED
         + "("
         + LiteColor.RED
         + "$timeSince ago"
         + LiteColor.DARK_RED
         + ")"
   ),
   NOTIFY("notify"),
   BANNED_PLAYER_JOIN(NOTIFY, "banned_player_join", LiteColor.RED + "$player" + LiteColor.WHITE + " tried to join, but is banned ($duration)!"),
   BANNED_GEOIP_BLACKLIST(NOTIFY, "banned_geoip_blacklist", LiteColor.RED + "$player" + LiteColor.WHITE + " tried to join, but is blacklisted ($geoip)!"),
   PERMISSION_ERROR("permission_error", LiteColor.RED + "You don't have permission!"),
   ERROR_NO_TEMPLATE_PROVIDED("error_no_template_provided", LiteColor.RED + "You must provide a template for this punishment!"),
   ERROR_NO_REASON_PROVIDED("error_no_reason_provided", LiteColor.RED + "You must provide a reason for this punishment!"),
   ERROR_NO_REASON_REMOVAL("error_no_reason_removal", LiteColor.RED + "You must provide a reason to remove this punishment!"),
   ERROR_NO_SQL_CONNECTION("error_no_sql_connection", LiteColor.RED + "LiteBans is not connected to a database!"),
   ERROR_NO_UUID_FOUND("error_no_uuid_found", LiteColor.RED + "Player does not exist."),
   ERROR_NO_DURATION("error_no_duration", LiteColor.RED + "No valid time specification detected!"),
   ERROR_STACK_OVERFLOW("error_stack_overflow", LiteColor.RED + "Stack overflow detected."),
   ERROR_CONSOLE_ONLY("error_console_only", LiteColor.RED + "This command can only be used from console."),
   DURATION_LIMIT_ERROR("duration_limit_error", LiteColor.RED + "Maximum duration allowed: $duration"),
   INTERNAL_ERROR("internal_error", LiteColor.RED + "An internal error occurred while attempting to perform this command."),
   BAN_USAGE(C_BAN, "usage", LiteColor.RED + "Usage: $command [-s] <player> [duration] [reason]"),
   EXAMPLE(C_BAN, "example", LiteColor.RED + "Example: $command Player 7d [reason]"),
   SILENT_PREFIX(C_BAN, "silent_prefix", LiteColor.WHITE + "[" + LiteColor.GRAY + "Silent" + LiteColor.WHITE + "] "),
   BROADCAST_BAN(
      C_BAN,
      "broadcast_ban",
      LiteColor.green("$executor ") + LiteColor.WHITE + "banned " + LiteColor.GREEN + "$playerDisplayName " + LiteColor.WHITE + "for '" + LiteColor.GREEN + "$reason" + LiteColor.WHITE + '\''
   ),
   BROADCAST_TEMPBAN(
      C_BAN,
      "broadcast_tempban",
      LiteColor.green("$executor ")
         + LiteColor.WHITE
         + "tempbanned "
         + LiteColor.GREEN
         + "$playerDisplayName "
         + LiteColor.WHITE
         + "for $tempDuration for '"
         + LiteColor.GREEN
         + "$reason"
         + LiteColor.WHITE
         + '\''
   ),
   BROADCAST_IP_BAN(
      C_BAN,
      "broadcast_ip_ban",
      LiteColor.green("$executor ") + LiteColor.WHITE + "IP-banned " + LiteColor.GREEN + "$playerDisplayName " + LiteColor.WHITE + "for '" + LiteColor.GREEN + "$reason" + LiteColor.WHITE + '\''
   ),
   BROADCAST_TEMP_IP_BAN(
      C_BAN,
      "broadcast_temp_ip_ban",
      LiteColor.green("$executor ")
         + LiteColor.WHITE
         + "temp IP-banned "
         + LiteColor.GREEN
         + "$playerDisplayName "
         + LiteColor.WHITE
         + "for $tempDuration for '"
         + LiteColor.GREEN
         + "$reason"
         + LiteColor.WHITE
         + '\''
   ),
   BAN_PREVIOUS_BAN_REMOVED(C_BAN, "previous_ban_removed", LiteColor.green("Previous ban for $playerName removed.")),
   BAN_PREVIOUS_BAN_EXISTING(C_BAN, "previous_ban_existing", LiteColor.red("$playerName is already banned, and you do not have permission to replace existing bans.")),
   BAN_ERROR_UNBAN_FAIL(C_BAN, "unban_fail", LiteColor.red("Target is not banned!")),
   BAN_ERROR_NO_OVERRIDE(C_BAN, "no_override", LiteColor.red("Player is already banned!")),
   ALTS_GUARDRAIL_DISCLAIMER(
      C_BAN,
      "alts_guardrail_disclaimer",
      LiteColor.yellow("// WARNING WARNING WARNING WARNING WARNING //\n")
         + LiteColor.gold(
            "Automatically banning accounts can result in unintended and unexpected outcomes, such as causing innocent players to be banned automatically.\n"
         )
         + LiteColor.gold("If you want to enable this feature, please check and ensure the following:\n")
         + LiteColor.red(
            "1) Your players are not joining from a limited range of IP addresses belonging to one specific host, such as a DDoS protection service forwarding players to your server.\n"
         )
         + LiteColor.red(
            "2) All of your servers have IP forwarding set up correctly (please consult BungeeCord or Velocity documentation to set up IP forwarding, if you have not done so).\n"
         )
         + LiteColor.red("3) None of your user accounts are being randomly associated when checking /alts or /ipreport to view linked accounts on the same IP.\n")
         + LiteColor.red(
            "4) If you have an offline-mode network or permit non-premium users to join, please add your authentication or lobby server to ignored_iphistory_servers to prevent falsely associated accounts (since anyone can join any account).\n"
         )
         + LiteColor.yellow("// WARNING WARNING WARNING WARNING WARNING //\n")
         + LiteColor.green("If you acknowledge and accept this disclaimer, please type " + LiteColor.lightPurple("litebans accept") + LiteColor.green(" to enable automatic bans."))
   ),
   BAN_ALTS_GUARDRAIL_BAN_SELF(
      C_BAN,
      "alts_guardrail_ban_self",
      LiteColor.red("Error: Banning associated accounts for $playerName would also ban your account!\nNo additional players will be banned.")
   ),
   BAN_ALTS_GUARDRAIL_BAN_MANY(
      C_BAN,
      "alts_guardrail_ban_many",
      LiteColor.yellow(
         "Warning: Banning this player will also ban $num players!\nThese players will be banned if you click here to confirm:\n$list\n {run: $command $commandArgs --confirm}"
      )
   ),
   BAN_EXEMPT(C_BAN, "exempt", LiteColor.red("You can't ban $player!")),
   COOLDOWN(C_BAN, "cooldown", LiteColor.red("You need to wait " + LiteColor.yellow("$duration") + LiteColor.red(" before you can use this command again."))),
   BAN_RESPONSE(C_BAN, "response", ""),
   BAN_MODIFY_SUCCESS(C_BAN, "modify_success", LiteColor.green("Punishment modified successfully.")),
   BAN_DELETE_SUCCESS(C_BAN, "delete_success", LiteColor.green("Punishment deleted successfully.")),
   BAN_HIDDEN_IP(C_BAN, "ip_hidden", "[IP Address]"),
   UNBAN_USAGE(C_BAN, "unban_usage", LiteColor.RED + "Usage: $command <player>"),
   BROADCAST_UNBAN(
      C_BAN,
      "broadcast_unban",
      LiteColor.GREEN + "$executor " + LiteColor.WHITE + "unbanned " + LiteColor.GREEN + "$playerName " + LiteColor.WHITE + "for '" + LiteColor.GREEN + "$removalReason" + LiteColor.WHITE + '\''
   ),
   UNBAN_QUEUED(C_BAN, "unban_queued", LiteColor.RED + "Target is not banned!" + LiteColor.GOLD + " If they try to reconnect, they will be unbanned."),
   MUTE_USAGE(C_MUTE, "usage", LiteColor.RED + "Usage: $command [-s] <player> [time spec] [reason]"),
   EXAMPLE_2(C_MUTE, "example", LiteColor.RED + "Example: $command Player 7d [reason]"),
   BROADCAST(
      C_MUTE,
      "broadcast",
      LiteColor.GREEN + "$executor " + LiteColor.WHITE + "muted " + LiteColor.GREEN + "$playerDisplayName " + LiteColor.WHITE + "for '" + LiteColor.GREEN + "$reason" + LiteColor.WHITE + '\''
   ),
   BROADCAST_TEMPMUTE(
      C_MUTE,
      "broadcast_tempmute",
      LiteColor.GREEN
         + "$executor "
         + LiteColor.WHITE
         + "tempmuted "
         + LiteColor.GREEN
         + "$playerDisplayName "
         + LiteColor.WHITE
         + "for $tempDuration for '"
         + LiteColor.GREEN
         + "$reason"
         + LiteColor.WHITE
         + '\''
   ),
   BROADCAST_IP_MUTE(
      C_MUTE,
      "broadcast_ip_mute",
      LiteColor.GREEN + "$executor " + LiteColor.WHITE + "IP-muted " + LiteColor.GREEN + "$playerDisplayName " + LiteColor.WHITE + "for '" + LiteColor.GREEN + "$reason" + LiteColor.WHITE + '\''
   ),
   BROADCAST_TEMP_IP_MUTE(
      C_MUTE,
      "broadcast_temp_ip_mute",
      LiteColor.GREEN
         + "$executor "
         + LiteColor.WHITE
         + "temp IP-muted "
         + LiteColor.GREEN
         + "$playerDisplayName "
         + LiteColor.WHITE
         + "for $tempDuration for '"
         + LiteColor.GREEN
         + "$reason"
         + LiteColor.WHITE
         + '\''
   ),
   MUTE_MESSAGE(
      C_MUTE,
      "message",
      LiteColor.DARK_RED
         + "You have been muted by $executor"
         + LiteColor.DARK_RED
         + " for "
         + LiteColor.RED
         + '\''
         + LiteColor.DARK_RED
         + "$reason"
         + LiteColor.RED
         + "'"
         + LiteColor.DARK_RED
         + ".\n"
         + LiteColor.DARK_RED
         + "This mute will expire in $duration."
   ),
   MESSAGE_PERMANENT(
      C_MUTE,
      "message_permanent",
      LiteColor.DARK_RED
         + "You have been permanently muted by $executor"
         + LiteColor.DARK_RED
         + " for "
         + LiteColor.RED
         + '\''
         + LiteColor.DARK_RED
         + "$reason"
         + LiteColor.RED
         + "'"
         + LiteColor.DARK_RED
         + ".\n"
         + LiteColor.DARK_RED
         + "This mute will not expire."
   ),
   UNMUTE_FAIL(C_MUTE, "unmute_fail", LiteColor.RED + "Target is not muted!"),
   NO_OVERRIDE(C_MUTE, "no_override", LiteColor.RED + "Player is already muted!"),
   PREVIOUS_MUTE_REMOVED(C_MUTE, "previous_mute_removed", LiteColor.GREEN + "Previous mute for $playerName removed."),
   PREVIOUS_MUTE_EXISTING(C_MUTE, "previous_mute_existing", LiteColor.RED + "$playerName is already muted, and you do not have permission to replace existing mutes."),
   EXEMPT(C_MUTE, "exempt", LiteColor.RED + "You can't mute $player!"),
   NOTIFICATION(C_MUTE, "notification", LiteColor.RED + "$playerDisplayName tried to speak, but is muted."),
   ERROR_NOT_ENABLED(C_MUTE, "error_not_enabled", LiteColor.RED + "Muting is not enabled in the configuration!"),
   MUTE_RESPONSE(C_MUTE, "response", ""),
   UNMUTE_USAGE(C_MUTE, "unmute_usage", LiteColor.RED + "Usage: $command <player>"),
   BROADCAST_UNMUTE(
      C_MUTE,
      "broadcast_unmute",
      LiteColor.GREEN + "$executor " + LiteColor.WHITE + "unmuted " + LiteColor.GREEN + "$playerDisplayName " + LiteColor.WHITE + "for '" + LiteColor.GREEN + "$removalReason" + LiteColor.WHITE + '\''
   ),
   WARN_USAGE(C_WARN, "usage", LiteColor.RED + "Usage: $command [-s] <player> [reason]"),
   WARN_EXAMPLE(C_WARN, "example", ""),
   WARN_BROADCAST(
      C_WARN,
      "broadcast",
      LiteColor.GREEN + "$executor " + LiteColor.WHITE + "warned " + LiteColor.GREEN + "$playerDisplayName " + LiteColor.WHITE + "for '" + LiteColor.GREEN + "$reason" + LiteColor.WHITE + '\''
   ),
   WARN_MESSAGE(
      C_WARN,
      "message",
      LiteColor.DARK_RED
         + "You have been warned by $executor"
         + LiteColor.DARK_RED
         + " for "
         + LiteColor.RED
         + '\''
         + LiteColor.DARK_RED
         + "$reason"
         + LiteColor.RED
         + '\''
         + LiteColor.DARK_RED
         + ".\n"
         + LiteColor.DARK_RED
         + "This warning will expire in $duration."
   ),
   LIST_ENTRY(C_WARN, "list_entry", LiteColor.WHITE + " \\- Warned by $executor: '$reason" + LiteColor.WHITE + "'"),
   UNWARN_RESPONSE(C_WARN, "unwarn_response", LiteColor.GREEN + "Last warning removed for $player."),
   UNWARN_FAIL(C_WARN, "unwarn_fail", LiteColor.RED + "Target has no warnings!"),
   EXEMPT_2(C_WARN, "exempt", LiteColor.RED + "You can't warn $player!"),
   WARN_RESPONSE(C_WARN, "response", ""),
   UNWARN_USAGE(C_WARN, "unwarn_usage", LiteColor.RED + "Usage: $command <player>"),
   BROADCAST_UNWARN(C_WARN, "broadcast_unwarn", LiteColor.GREEN + "$executor " + LiteColor.WHITE + "unwarned " + LiteColor.GREEN + "$playerDisplayName"),
   KICK_USAGE(C_KICK, "usage", LiteColor.RED + "$command <player> [reason]"),
   NO_MATCH(C_KICK, "no_match", LiteColor.RED + "Error: " + LiteColor.DARK_RED + "Player not found."),
   KICK_REQUESTED(C_KICK, "kick_requested", LiteColor.GOLD + "Player $player is not online on this server. Cross-server kick has been requested."),
   KICK_REASON(C_KICK, "message", "Kicked by $executor: $reason"),
   KICK_REASON_EMPTY(C_KICK, "message_no_reason", "Kicked by $executor."),
   RESPONSE(C_KICK, "response", LiteColor.GOLD + "$playerDisplayName has been kicked."),
   KICK_BROADCAST(
      C_KICK,
      "broadcast",
      LiteColor.GREEN + "$playerDisplayName" + LiteColor.WHITE + " was kicked by " + LiteColor.GREEN + "$executor" + LiteColor.WHITE + " for '" + LiteColor.GREEN + "$reason" + LiteColor.WHITE + "'."
   ),
   BROADCAST_NO_REASON(C_KICK, "broadcast_no_reason", LiteColor.GREEN + "$playerDisplayName" + LiteColor.WHITE + " was kicked by " + LiteColor.GREEN + "$executor" + LiteColor.WHITE + '.'),
   EXEMPT_3(C_KICK, "exempt", LiteColor.RED + "You can't kick $player!"),
   HISTORY_USAGE(C_HISTORY, "usage", LiteColor.RED + "$command <player> [entries=10]"),
   START(C_HISTORY, "start", LiteColor.GREEN + "History for $target (Limit: $limit):"),
   return2(
      C_HISTORY,
      "ban_entry",
      LiteColor.GREEN
         + " -- ["
         + LiteColor.WHITE
         + "$timeSince ago"
         + LiteColor.GREEN
         + "] --"
         + LiteColor.WHITE
         + "\n"
         + LiteColor.WHITE
         + " $name was "
         + LiteColor.RED
         + "banned "
         + LiteColor.WHITE
         + "by $executor: '"
         + LiteColor.GREEN
         + "$reason"
         + LiteColor.WHITE
         + '\''
   ),
   return3(
      C_HISTORY,
      "mute_entry",
      LiteColor.GREEN
         + " -- ["
         + LiteColor.WHITE
         + "$timeSince ago"
         + LiteColor.GREEN
         + "] --"
         + LiteColor.WHITE
         + "\n"
         + LiteColor.WHITE
         + " $name was "
         + LiteColor.GRAY
         + "muted "
         + LiteColor.WHITE
         + "by $executor: '"
         + LiteColor.GREEN
         + "$reason"
         + LiteColor.WHITE
         + '\''
   ),
   return4(
      C_HISTORY,
      "warn_entry",
      LiteColor.GREEN
         + " -- ["
         + LiteColor.WHITE
         + "$timeSince ago"
         + LiteColor.GREEN
         + "] --"
         + LiteColor.WHITE
         + "\n"
         + LiteColor.WHITE
         + " $name was "
         + LiteColor.GOLD
         + "warned "
         + LiteColor.WHITE
         + "by $executor: '"
         + LiteColor.GREEN
         + "$reason"
         + LiteColor.WHITE
         + '\''
   ),
   return5(
      C_HISTORY,
      "kick_entry",
      LiteColor.GREEN
         + " -- ["
         + LiteColor.WHITE
         + "$timeSince ago"
         + LiteColor.GREEN
         + "] --"
         + LiteColor.WHITE
         + "\n"
         + LiteColor.WHITE
         + " $name was "
         + LiteColor.YELLOW
         + "kicked "
         + LiteColor.WHITE
         + "by $executor: '"
         + LiteColor.GREEN
         + "$reason"
         + LiteColor.WHITE
         + '\''
   ),
   UNBAN_ENTRY(
      C_HISTORY,
      "unban_entry",
      "\n " + LiteColor.WHITE + "$name was " + LiteColor.GRAY + "unbanned " + LiteColor.WHITE + "by $executor: '" + LiteColor.GRAY + "$removalReason" + LiteColor.WHITE + '\''
   ),
   UNMUTE_ENTRY(
      C_HISTORY,
      "unmute_entry",
      "\n " + LiteColor.WHITE + "$name was " + LiteColor.GRAY + "unmuted " + LiteColor.WHITE + "by $executor: '" + LiteColor.GRAY + "$removalReason" + LiteColor.WHITE + '\''
   ),
   UNWARN_ENTRY(
      C_HISTORY,
      "unwarn_entry",
      "\n " + LiteColor.WHITE + "$name was " + LiteColor.GRAY + "unwarned " + LiteColor.WHITE + "by $executor: '" + LiteColor.GRAY + "$removalReason" + LiteColor.WHITE + '\''
   ),
   ACTIVE_SUFFIX(C_HISTORY, "active_suffix", LiteColor.WHITE + " [" + LiteColor.RED + "Active" + LiteColor.WHITE + ']'),
   ACTIVE_SUFFIX_TEMP(C_HISTORY, "active_suffix_temp", LiteColor.WHITE + " [" + LiteColor.RED + "Active" + LiteColor.WHITE + "]\n" + LiteColor.WHITE + "Expires in $duration."),
   EXPIRED_SUFFIX(C_HISTORY, "expired_suffix", LiteColor.WHITE + " [" + LiteColor.DARK_GRAY + "Expired" + LiteColor.WHITE + ']'),
   ERROR_NO_LOGINRECORD(C_HISTORY, "error_no_loginrecord", LiteColor.RED + "User has not joined before."),
   ERROR_NO_USER(C_HISTORY, "error_no_user", LiteColor.RED + "User not found."),
   WARNINGS_USAGE(C_WARNINGS, "usage", LiteColor.RED + "$command <player>"),
   START_2(C_WARNINGS, "start", LiteColor.GREEN + "Active warnings for $target:"),
   PRUNEHISTORY_USAGE(C_PRUNEHISTORY, "usage", LiteColor.RED + "$command <player> [duration]"),
   MESSAGE(C_PRUNEHISTORY, "message", LiteColor.GREEN + "History pruned."),
   STAFFHISTORY_USAGE(C_STAFFHISTORY, "usage", LiteColor.RED + "$command <player> [entries=10]"),
   START_3(C_STAFFHISTORY, "start", LiteColor.GREEN + "Staff history for $target (Limit: $limit):"),
   STAFFROLLBACK_USAGE(C_STAFFROLLBACK, "usage", LiteColor.RED + "$command <player> [duration]"),
   MESSAGE_2(C_STAFFROLLBACK, "message", LiteColor.GREEN + "Rollback completed, total entries removed: $amount"),
   START_4(C_BANLIST, "start", LiteColor.WHITE + "=== " + LiteColor.GREEN + "Page " + LiteColor.GOLD + "$page" + LiteColor.GREEN + " out of " + LiteColor.GOLD + "$total" + LiteColor.WHITE + " ==="),
   IPHISTORY_USAGE(C_IPHISTORY, "usage", LiteColor.RED + "$command <player> [entries=10]"),
   START_5(C_IPHISTORY, "start", LiteColor.GREEN + "Login history for $target (Limit: $limit):"),
   ENTRY(C_IPHISTORY, "entry", LiteColor.GREEN + " - [" + LiteColor.WHITE + "$date" + LiteColor.GREEN + ']' + LiteColor.WHITE + " $name" + LiteColor.GREEN + ':' + LiteColor.WHITE + " $ip"),
   ERROR_NO_HISTORY(C_IPHISTORY, "error_no_history", LiteColor.RED + "No history found."),
   START_6(C_NAMEHISTORY, "start", LiteColor.GREEN + "Name history for $target (Limit: $limit):"),
   NAMEHISTORY_USAGE(C_NAMEHISTORY, "usage", LiteColor.RED + "$command <player> [entries=10]"),
   ENTRY_2(C_NAMEHISTORY, "entry", LiteColor.GREEN + " - [" + LiteColor.WHITE + "$playerLoginDate" + LiteColor.GREEN + ']' + LiteColor.GOLD + " $playerName"),
   DUPEIP_USAGE(C_DUPEIP, "usage", LiteColor.RED + "$command <player>"),
   DUPEIP_START(
      C_DUPEIP,
      "start",
      LiteColor.WHITE
         + "Scanning "
         + LiteColor.GREEN
         + "$name"
         + LiteColor.WHITE
         + " on "
         + LiteColor.GREEN
         + "$ip"
         + LiteColor.WHITE
         + ".\n"
         + LiteColor.WHITE
         + '['
         + LiteColor.GREEN
         + "Online"
         + LiteColor.WHITE
         + "] ["
         + LiteColor.GRAY
         + "Offline"
         + LiteColor.WHITE
         + "] ["
         + LiteColor.RED
         + "Banned"
         + LiteColor.WHITE
         + "] ["
         + LiteColor.YELLOW
         + "IPBanned"
         + LiteColor.WHITE
         + ']'
   ),
   START_NO_IP(
      C_DUPEIP,
      "start_no_ip",
      LiteColor.WHITE
         + "Scanning "
         + LiteColor.GRAY
         + "$name"
         + LiteColor.WHITE
         + ".\n"
         + LiteColor.WHITE
         + '['
         + LiteColor.GREEN
         + "Online"
         + LiteColor.WHITE
         + "] ["
         + LiteColor.GRAY
         + "Offline"
         + LiteColor.WHITE
         + "] ["
         + LiteColor.RED
         + "Banned"
         + LiteColor.WHITE
         + "] ["
         + LiteColor.YELLOW
         + "IPBanned"
         + LiteColor.WHITE
         + ']'
   ),
   DUPEIP_END(C_DUPEIP, "end", ""),
   DUPEIP_MULTIPLE_ADDRESSES(C_DUPEIP, "multiple_addresses", "[$num addresses]"),
   SEPARATOR(C_DUPEIP, "separator", LiteColor.WHITE + ", "),
   DUPEIP_ONLINE(C_DUPEIP, "online", LiteColor.GREEN),
   DUPEIP_OFFLINE(C_DUPEIP, "offline", LiteColor.GRAY),
   DUPEIP_BANNED(C_DUPEIP, "banned", LiteColor.RED),
   DUPEIP_IP_BANNED(C_DUPEIP, "ip_banned", LiteColor.YELLOW),
   DUPEIP_MUTED(C_DUPEIP, "muted", LiteColor.GOLD),
   IPREPORT_START(
      C_IPREPORT,
      "start",
      LiteColor.WHITE
         + "Scanning "
         + LiteColor.GREEN
         + "$num"
         + LiteColor.WHITE
         + " online players.. "
         + '['
         + LiteColor.GREEN
         + "Online"
         + LiteColor.WHITE
         + "] ["
         + LiteColor.GRAY
         + "Offline"
         + LiteColor.WHITE
         + "] ["
         + LiteColor.RED
         + "Banned"
         + LiteColor.WHITE
         + ']'
   ),
   ENTRY_3(C_IPREPORT, "entry", LiteColor.WHITE + "$player" + LiteColor.GREEN + ": $result"),
   CHECKBAN_USAGE(C_CHECKBAN, "usage", LiteColor.RED + "$command <player>"),
   NO_BAN(C_CHECKBAN, "no_ban", LiteColor.RED + "Target is not banned!"),
   BANNED(
      C_CHECKBAN,
      "banned",
      LiteColor.GREEN
         + "Target "
         + LiteColor.WHITE
         + '['
         + LiteColor.GREEN
         + "$target"
         + LiteColor.WHITE
         + ']'
         + LiteColor.GREEN
         + " is banned:\n"
         + LiteColor.GREEN
         + "Banned by: $executor\n"
         + LiteColor.GREEN
         + "Reason: $reason"
         + LiteColor.GREEN
         + "\n"
         + LiteColor.GREEN
         + "Banned on: $dateStart\n"
         + LiteColor.GREEN
         + "Banned until: $dateEnd ($duration)\n&aBanned on server &6&a$serverOrigin&a, server scope: &6$serverScope\n"
         + LiteColor.GREEN
         + "IP ban: $ipban, silent: $silent, permanent: $permanent"
   ),
   CHECKMUTE_USAGE(C_CHECKMUTE, "usage", LiteColor.RED + "$command <player>"),
   NO_MUTE(C_CHECKMUTE, "no_mute", LiteColor.RED + "Target is not muted!"),
   CHECKMUTE_MUTED(
      C_CHECKMUTE,
      "muted",
      LiteColor.GREEN
         + "Target &f[&a$target&f]&a is muted:\n&aMuted by: $executor\n&aReason: $reason&r\n&aMuted on: $dateStart\n&aMuted until: $dateEnd ($duration)\n&aMuted on server &6$serverOrigin&a, server scope: &6$serverScope\n&aIP mute: $ipban, silent: $silent, permanent: $permanent"
   ),
   CHECKWARN_USAGE(C_CHECKWARN, "usage", LiteColor.RED + "$command <player>"),
   NO_WARN(C_CHECKWARN, "no_warn", LiteColor.RED + "Target is not warned!"),
   WARNED(
      C_CHECKWARN,
      "warned",
      LiteColor.GREEN
         + "Target &f[&a$target&f]&a is warned:\n&aWarned by: $executor\n&aReason: $reason&r\n&aWarned on: $dateStart\n&aWarned until: $dateEnd ($duration)\n&aWarned on server &6$serverOrigin&a, server scope: &6$serverScope\n&aIP warning: $ipban, silent: $silent, permanent: $permanent"
   ),
   LASTUUID_USAGE(C_LASTUUID, "usage", LiteColor.RED + "$command <player|IP>"),
   MESSAGE_3(C_LASTUUID, "message", LiteColor.WHITE + "Last UUID for " + LiteColor.GREEN + "$name" + LiteColor.WHITE + ": " + LiteColor.GREEN + "$uuid"),
   GEOIP_USAGE(C_GEOIP, "usage", LiteColor.RED + "$command <player|IP>"),
   MESSAGE_4(C_GEOIP, "message", LiteColor.GREEN + "$target" + LiteColor.WHITE + " is from: " + LiteColor.GREEN + "$result"),
   ERROR_DISABLED(C_GEOIP, "error_disabled", LiteColor.RED + "GeoIP support is not enabled in the configuration!"),
   ERROR_UNAVAILABLE(C_GEOIP, "error_unavailable", LiteColor.RED + "GeoIP support is currently unavailable, has it been downloaded yet?"),
   ERROR_NOT_FOUND(C_GEOIP, "error_not_found", LiteColor.RED + "GeoIP information for $target not found."),
   LOCKDOWN_USAGE(C_LOCKDOWN, "usage", LiteColor.RED + "$command <reason> | $command end"),
   MESSAGE_5(C_LOCKDOWN, "message", LiteColor.RED + "Server lockdown activated (reason: \"$reason" + LiteColor.RED + "\")"),
   STOPPED(C_LOCKDOWN, "stopped", LiteColor.GREEN + "Lockdown has been deactivated."),
   LOCKDOWN_KICK_MESSAGE(C_LOCKDOWN, "kick_message", "Server lockdown active, try again later.\\nReason: $reason"),
   LOCKDOWN_KICK_MESSAGE_GLOBAL(C_LOCKDOWN, "kick_message_global", "Network lockdown active, try again later.\\nReason: $reason"),
   ERROR_NOT_ACTIVE(C_LOCKDOWN, "error_not_active", LiteColor.RED + "Lockdown is not active!"),
   RELOAD_SUCCESS(C_LITEBANS, "reload_success", LiteColor.GREEN + "LiteBans reloaded successfully."),
   RELOAD_FAIL(C_LITEBANS, "reload_fail", LiteColor.RED + "Reload failed."),
   RELOAD_FAIL_CONNECT(C_LITEBANS, "reload_fail_connect", LiteColor.GREEN + "Litebans reloaded. " + LiteColor.RED + "Failed to connect to database."),
   RELOAD_FAIL_CONFIG(
      C_LITEBANS,
      "reload_fail_config",
      LiteColor.RED
         + "[LiteBans] "
         + LiteColor.DARK_RED
         + "config.yml is not valid and could not be loaded, the default configuration will be used.\\n"
         + LiteColor.RED
         + "Please check the server console for more information."
   ),
   RELOAD_FAIL_MESSAGES(
      C_LITEBANS,
      "reload_fail_messages",
      LiteColor.RED
         + "[LiteBans] "
         + LiteColor.DARK_RED
         + "messages.yml is not valid and could not be loaded, default messages will be used.\\n"
         + LiteColor.RED
         + "Please check the server console for more information."
   ),
   RELOAD_FAIL_TEMPLATES(
      C_LITEBANS,
      "reload_fail_templates",
      LiteColor.RED
         + "[LiteBans] "
         + LiteColor.DARK_RED
         + "templates.yml is not valid and could not be loaded. Configured templates will be unavailable until this has been fixed.\\n"
         + LiteColor.RED
         + "Please check the server console for more information."
   ),
   FIX_HISTORY(C_LITEBANS, "fix_history", LiteColor.GREEN + "Fixing history for table $table..."),
   FIX_HISTORY_RESULT(C_LITEBANS, "fix_history_result", LiteColor.GREEN + "Added $amount entries."),
   FIX_HISTORY_OFFLINE_UUIDS(C_LITEBANS, "fix_history_offline_uuids", LiteColor.RED + "$amount UUIDs were not fetched from Mojang since they are offline-mode UUIDs."),
   IMPORT_USAGE(C_LITEBANS, "import_usage", LiteColor.RED + "$command import start"),
   IMPORT_START(C_LITEBANS, "import_start", LiteColor.GREEN + "Importing from $db, this might take a while..."),
   IMPORT_FINISH(C_LITEBANS, "import_finish", LiteColor.GREEN + "Import finished successfully. $bans bans imported, $ipbans IP-bans."),
   IMPORT_FINISH_LITEBANS(C_LITEBANS, "import_finish_litebans", LiteColor.GREEN + "Import finished successfully. Added $amount entries to the database."),
   IMPORT_FAIL(C_LITEBANS, "import_fail", LiteColor.RED + "Import failed. Check console."),
   IMPORT_UNSUPPORTED(C_LITEBANS, "import_unsupported", LiteColor.RED + "Importing from '$name' is not supported yet!"),
   ALLOW_USAGE(C_LITEBANS, "allow_usage", LiteColor.RED + "Usage: /litebans allow <add/remove/check> <user>"),
   ALLOW_ADDED(
      C_LITEBANS,
      "allow_added",
      LiteColor.YELLOW + "Player " + LiteColor.GOLD + "$playerName" + LiteColor.YELLOW + " has been " + LiteColor.GREEN + "allowed to join the server" + LiteColor.YELLOW + "."
   ),
   ALLOW_REMOVED(
      C_LITEBANS,
      "allow_removed",
      LiteColor.YELLOW + "Player " + LiteColor.GOLD + "$playerName" + LiteColor.YELLOW + " has been " + LiteColor.RED + "removed from the allow list" + LiteColor.YELLOW + "."
   ),
   ALLOW_CHECK_TRUE(C_LITEBANS, "allow_check_true", LiteColor.GREEN + "Player " + LiteColor.GOLD + "$playerName" + LiteColor.GREEN + " is allowed to join the server."),
   ALLOW_CHECK_FALSE(C_LITEBANS, "allow_check_false", LiteColor.RED + "Player " + LiteColor.GOLD + "$playerName" + LiteColor.RED + " is not allowed to join the server."),
   ALLOW_ERROR_ADD(C_LITEBANS, "allow_error_add", LiteColor.RED + "Error: $playerName is already allowed to join the server."),
   ALLOW_ERROR_REMOVE(C_LITEBANS, "allow_error_remove", LiteColor.RED + "Error: $playerName is not allowed to join the server."),
   RESET_DATABASE_CONFIRM_QUOTED(
      C_LITEBANS,
      "reset_database_confirm_quoted",
      "\n"
         + LiteColor.RED
         + "WARNING: "
         + LiteColor.YELLOW
         + "This command will reset your database, removing all active and inactive punishments permanently.\n"
         + LiteColor.RED
         + "WARNING: "
         + LiteColor.UNDERLINE
         + "This operation is irreversible without restoring a database backup.\n"
         + LiteColor.YELLOW
         + "Run '"
         + LiteColor.RED
         + "litebans $commandArgs confirm"
         + LiteColor.YELLOW
         + "' to continue."
   ),
   RESET_DATABASE_RESULT(
      C_LITEBANS,
      "reset_database_result",
      LiteColor.GREEN + "Removed " + LiteColor.YELLOW + "$amount " + LiteColor.GREEN + "$type from " + LiteColor.YELLOW + "server:$serverScope" + LiteColor.GREEN + "."
   ),
   DURATION("duration"),
   EXPIRED(DURATION, "expired", "expired"),
   FOREVER(DURATION, "forever", "forever"),
   YEAR(DURATION, "year", "year"),
   YEARS(DURATION, "years", "years"),
   MONTH(DURATION, "month", "month"),
   MONTHS(DURATION, "months", "months"),
   WEEK(DURATION, "week", "week"),
   WEEKS(DURATION, "weeks", "weeks"),
   DAY(DURATION, "day", "day"),
   DAYS(DURATION, "days", "days"),
   HOUR(DURATION, "hour", "hour"),
   HOURS(DURATION, "hours", "hours"),
   MINUTE(DURATION, "minute", "minute"),
   MINUTES(DURATION, "minutes", "minutes"),
   SECOND(DURATION, "second", "second"),
   SECONDS(DURATION, "seconds", "seconds"),
   DURATION_FORMAT(DURATION, "format", "%d %s"),
   DURATION_SEPARATOR(DURATION, "separator", ", "),
   STRINGS("strings"),
   STRINGS_GLOBAL(STRINGS, "global", "global"),
   STRINGS_NULL(STRINGS, "null", "undefined"),
   STRINGS_TRUE(STRINGS, "true", "yes"),
   STRINGS_FALSE(STRINGS, "false", "no"),
   WEB("webhook"),
   WEB_EVENT(WEB, "events"),
   WEB_BAN(WEB_EVENT, "embed_ban"),
   WEB_BAN_TITLE(WEB_BAN, "title", "Banned"),
   WEB_BAN_COLOR(WEB_BAN, "color", "#CC2200"),
   WEB_BAN_CONTENT(WEB_BAN, "content", "$playerName has been banned!\n• Banned by: $executor\n• Duration: $duration\n• Reason: $reason"),
   WEB_BAN_FIELDS(WEB_BAN, "fields", ""),
   WEB_BAN_THUMB(WEB_BAN, "thumbnail", ""),
   WEB_UNBAN(WEB_EVENT, "embed_unban"),
   WEB_UNBAN_TITLE(WEB_UNBAN, "title", "Unbanned"),
   WEB_UNBAN_COLOR(WEB_UNBAN, "color", "#727374"),
   WEB_UNBAN_CONTENT(WEB_UNBAN, "content", "$playerName has been unbanned!\n• Unbanned by: $executor\n• Reason: $removalReason"),
   WEB_UNBAN_FIELDS(WEB_UNBAN, "fields", ""),
   WEB_UNBAN_THUMB(WEB_UNBAN, "thumbnail", ""),
   WEB_MUTE(WEB_EVENT, "embed_mute"),
   WEB_MUTE_TITLE(WEB_MUTE, "title", "Muted"),
   WEB_MUTE_COLOR(WEB_MUTE, "color", "#BB9000"),
   WEB_MUTE_CONTENT(WEB_MUTE, "content", "$playerName has been muted!\n• Muted by: $executor\n• Duration: $duration\n• Reason: $reason"),
   WEB_MUTE_FIELDS(WEB_MUTE, "fields", ""),
   WEB_MUTE_THUMB(WEB_MUTE, "thumbnail", ""),
   WEB_UNMUTE(WEB_EVENT, "embed_unmute"),
   WEB_UNMUTE_TITLE(WEB_UNMUTE, "title", "Unmuted"),
   WEB_UNMUTE_COLOR(WEB_UNMUTE, "color", "#727374"),
   WEB_UNMUTE_CONTENT(WEB_UNMUTE, "content", "$playerName has been unmuted!\n• Unmuted by: $executor\n• Reason: $removalReason"),
   WEB_UNMUTE_FIELDS(WEB_UNMUTE, "fields", ""),
   WEB_UNMUTE_THUMB(WEB_UNMUTE, "thumbnail", ""),
   WEB_WARN(WEB_EVENT, "embed_warn"),
   WEB_WARN_TITLE(WEB_WARN, "title", "Warned"),
   WEB_WARN_COLOR(WEB_WARN, "color", "#FFAA11"),
   WEB_WARN_CONTENT(WEB_WARN, "content", "$playerName has been warned!\n• Warned by: $executor\n• Duration: $duration\n• Reason: $reason"),
   WEB_WARN_FIELDS(WEB_WARN, "fields", ""),
   WEB_WARN_THUMB(WEB_WARN, "thumbnail", ""),
   WEB_UNWARN(WEB_EVENT, "embed_unwarn"),
   WEB_UNWARN_TITLE(WEB_UNWARN, "title", "Unwarned"),
   WEB_UNWARN_COLOR(WEB_UNWARN, "color", "#727374"),
   WEB_UNWARN_CONTENT(WEB_UNWARN, "content", "$playerName has been unwarned!\n• Unwarned by: $executor\n• Reason: $removalReason"),
   WEB_UNWARN_FIELDS(WEB_UNWARN, "fields", ""),
   WEB_UNWARN_THUMB(WEB_UNWARN, "thumbnail", ""),
   WEB_KICK(WEB_EVENT, "embed_kick"),
   WEB_KICK_TITLE(WEB_KICK, "title", "Kicked"),
   WEB_KICK_COLOR(WEB_KICK, "color", "#999900"),
   WEB_KICK_CONTENT(WEB_KICK, "content", "$playerName has been kicked!\n• Kicked by: $executor\n• Reason: $reason"),
   WEB_KICK_FIELDS(WEB_KICK, "fields", ""),
   WEB_KICK_THUMB(WEB_KICK, "thumbnail", ""),
   WEB_ALTS(WEB_EVENT, "embed_alts"),
   WEB_ALTS_TITLE(WEB_ALTS, "title", "Scanned $playerName"),
   WEB_ALTS_COLOR(WEB_ALTS, "color", "#999999"),
   WEB_ALTS_THUMB(WEB_ALTS, "thumbnail", "");

   public final String text3;
   public @Nullable CharSequence c3_f;

   public static Message computeMessage(String text) {
      return Enum.valueOf(Message.class, text);
   }

   Message(Message message, String text) {
      this(message, text, null);
   }

   Message(Message message, String text, @Nullable CharSequence message2) {
      this(message.obtainText() + '.' + text, message2);
   }

   Message(String text) {
      this.text3 = text;
   }

   Message(String text, @Nullable CharSequence message) {
      this(text);
      this.handleMessage(message);
   }

   public static String computeText(Object obj, boolean flag, boolean flag2) {
      if (obj instanceof String) {
         return (String)obj;
      } else if (obj instanceof CharSequence) {
         return obj.toString();
      } else if (obj instanceof Character) {
         return new String(new char[]{(Character)obj});
      } else if (flag) {
         return flag2 ? obj.toString().trim() : obj.toString();
      } else {
         return null;
      }
   }

   public static Message computeMessage2(String text) {
      String upperCase = text.toUpperCase(Locale.ROOT);

      for (Message message : values()) {
         if (message.name().equals(upperCase)) {
            return message;
         }
      }

      throw new AssertionError(upperCase);
   }

   public static String computeText(String text) {
      return computeMessage2(text).toString();
   }

   public static @NotNull CharSequence computeMessage(@NotNull PunishmentType punishmentType) {
      switch (MessageSwitchMap.INTS[punishmentType.obtainPunishmentType().ordinal()]) {
         case 1:
            return return2;
         case 2:
            return return3;
         case 3:
            return return4;
         case 4:
            return return5;
         default:
            throw new AssertionError();
      }
   }

   public static @NotNull String computeText(CharSequence message, boolean flag) {
      String translateAlternateColorCodes2 = LiteColor.translateAlternateColorCodes('&', message.toString().replace("\\n", "\n"));
      if (flag) {
         translateAlternateColorCodes2 = JsonMessageModule.KOTLIN_DATA.computeText(translateAlternateColorCodes2);
      }

      if (translateAlternateColorCodes2.contains("\n{hoverText:")) {
         translateAlternateColorCodes2 = translateAlternateColorCodes2.replace("\n{hoverText:", "\n {hoverText:");
      }

      if (translateAlternateColorCodes2.contains("\n{run:")) {
         translateAlternateColorCodes2 = translateAlternateColorCodes2.replace("\n{run:", "\n {run:");
      }

      if (translateAlternateColorCodes2.contains("\n{url:")) {
         translateAlternateColorCodes2 = translateAlternateColorCodes2.replace("\n{url:", "\n {url:");
      }

      return translateAlternateColorCodes2;
   }

   public static @Nullable String computeText(Message message, @Nullable CharSequence message2) {
      if (message2 == null) {
         return null;
      }

      if (!message.text3.startsWith("webhook") && message2.toString().contains("#$id")) {
         message2 = message2.toString().replace("#$id", "#&b$id");
      }

      return Placeholders.removeExecutorPlaceholder(message2);
   }

   public String computeText(String text, @Nullable Object obj) {
      return this.computeText(Placeholders.placeholder(text), obj);
   }

   public String computeText(CharSequence message) {
      return this + "\n" + message;
   }

   @Override
   public char charAt(int value) {
      return this.c3_f == null ? '?' : this.c3_f.charAt(value);
   }

   @Override
   public boolean isEmpty() {
      return this.length() == 0;
   }

   @Override
   public int length() {
      return this.c3_f == null ? 0 : this.c3_f.length();
   }

   public @NotNull String computeText(CharSequence message, @Nullable Object obj) {
      Object obj2 = obj;
      if (obj2 == null) {
         obj2 = "";
      }

      return this.c3_f.toString().replace(message.toString(), obj2.toString());
   }

   public void handleMessage(@Nullable CharSequence message) {
      String text = message == null ? null : computeText(message.toString(), false);
      String text2 = computeText(this, text);
      this.c3_f = text2;
   }

   @Override
   public CharSequence subSequence(int value, int value2) {
      return this.c3_f.subSequence(value, value2);
   }

   @Override
   public String toString() {
      return String.valueOf(this.c3_f);
   }

   public String obtainText() {
      return this.text3;
   }

   private static final Message[] obtainMessageArray() {
      return new Message[]{
         CMD,
         C_BAN,
         C_MUTE,
         C_WARN,
         C_KICK,
         C_HISTORY,
         C_STAFFHISTORY,
         C_WARNINGS,
         C_PRUNEHISTORY,
         C_STAFFROLLBACK,
         C_BANLIST,
         C_IPHISTORY,
         C_NAMEHISTORY,
         C_DUPEIP,
         C_IPREPORT,
         C_CHECKBAN,
         C_CHECKMUTE,
         C_CHECKWARN,
         C_LASTUUID,
         C_LOCKDOWN,
         C_GEOIP,
         C_LITEBANS,
         HEX_FORMATS,
         DATE_FORMAT,
         DEFAULT_BAN_REASON,
         DEFAULT_MUTE_REASON,
         DEFAULT_UNBAN_REASON,
         DEFAULT_UNMUTE_REASON,
         DEFAULT_UNWARN_REASON,
         c5_f,
         BANNED_MESSAGE,
         BANNED_MESSAGE_PERMANENT,
         BANNED_MESSAGE_APPEAL,
         BANNED_MESSAGE_GEOIP_BLACKLIST,
         PROXY_SWITCH_BANNED,
         PROXY_SWITCH_TEMPBANNED,
         MUTED,
         MUTED_PERMANENT,
         WARNED_JOIN,
         az_f,
         NOTIFY,
         BANNED_PLAYER_JOIN,
         BANNED_GEOIP_BLACKLIST,
         PERMISSION_ERROR,
         ERROR_NO_TEMPLATE_PROVIDED,
         ERROR_NO_REASON_PROVIDED,
         ERROR_NO_REASON_REMOVAL,
         ERROR_NO_SQL_CONNECTION,
         ERROR_NO_UUID_FOUND,
         ERROR_NO_DURATION,
         ERROR_STACK_OVERFLOW,
         ERROR_CONSOLE_ONLY,
         DURATION_LIMIT_ERROR,
         INTERNAL_ERROR,
         BAN_USAGE,
         EXAMPLE,
         SILENT_PREFIX,
         BROADCAST_BAN,
         BROADCAST_TEMPBAN,
         BROADCAST_IP_BAN,
         BROADCAST_TEMP_IP_BAN,
         BAN_PREVIOUS_BAN_REMOVED,
         BAN_PREVIOUS_BAN_EXISTING,
         BAN_ERROR_UNBAN_FAIL,
         BAN_ERROR_NO_OVERRIDE,
         ALTS_GUARDRAIL_DISCLAIMER,
         BAN_ALTS_GUARDRAIL_BAN_SELF,
         BAN_ALTS_GUARDRAIL_BAN_MANY,
         BAN_EXEMPT,
         COOLDOWN,
         BAN_RESPONSE,
         BAN_MODIFY_SUCCESS,
         BAN_DELETE_SUCCESS,
         BAN_HIDDEN_IP,
         UNBAN_USAGE,
         BROADCAST_UNBAN,
         UNBAN_QUEUED,
         MUTE_USAGE,
         EXAMPLE_2,
         BROADCAST,
         BROADCAST_TEMPMUTE,
         BROADCAST_IP_MUTE,
         BROADCAST_TEMP_IP_MUTE,
         MUTE_MESSAGE,
         MESSAGE_PERMANENT,
         UNMUTE_FAIL,
         NO_OVERRIDE,
         PREVIOUS_MUTE_REMOVED,
         PREVIOUS_MUTE_EXISTING,
         EXEMPT,
         NOTIFICATION,
         ERROR_NOT_ENABLED,
         MUTE_RESPONSE,
         UNMUTE_USAGE,
         BROADCAST_UNMUTE,
         WARN_USAGE,
         WARN_EXAMPLE,
         WARN_BROADCAST,
         WARN_MESSAGE,
         LIST_ENTRY,
         UNWARN_RESPONSE,
         UNWARN_FAIL,
         EXEMPT_2,
         WARN_RESPONSE,
         UNWARN_USAGE,
         BROADCAST_UNWARN,
         KICK_USAGE,
         NO_MATCH,
         KICK_REQUESTED,
         KICK_REASON,
         KICK_REASON_EMPTY,
         RESPONSE,
         KICK_BROADCAST,
         BROADCAST_NO_REASON,
         EXEMPT_3,
         HISTORY_USAGE,
         START,
         return2,
         return3,
         return4,
         return5,
         UNBAN_ENTRY,
         UNMUTE_ENTRY,
         UNWARN_ENTRY,
         ACTIVE_SUFFIX,
         ACTIVE_SUFFIX_TEMP,
         EXPIRED_SUFFIX,
         ERROR_NO_LOGINRECORD,
         ERROR_NO_USER,
         WARNINGS_USAGE,
         START_2,
         PRUNEHISTORY_USAGE,
         MESSAGE,
         STAFFHISTORY_USAGE,
         START_3,
         STAFFROLLBACK_USAGE,
         MESSAGE_2,
         START_4,
         IPHISTORY_USAGE,
         START_5,
         ENTRY,
         ERROR_NO_HISTORY,
         START_6,
         NAMEHISTORY_USAGE,
         ENTRY_2,
         DUPEIP_USAGE,
         DUPEIP_START,
         START_NO_IP,
         DUPEIP_END,
         DUPEIP_MULTIPLE_ADDRESSES,
         SEPARATOR,
         DUPEIP_ONLINE,
         DUPEIP_OFFLINE,
         DUPEIP_BANNED,
         DUPEIP_IP_BANNED,
         DUPEIP_MUTED,
         IPREPORT_START,
         ENTRY_3,
         CHECKBAN_USAGE,
         NO_BAN,
         BANNED,
         CHECKMUTE_USAGE,
         NO_MUTE,
         CHECKMUTE_MUTED,
         CHECKWARN_USAGE,
         NO_WARN,
         WARNED,
         LASTUUID_USAGE,
         MESSAGE_3,
         GEOIP_USAGE,
         MESSAGE_4,
         ERROR_DISABLED,
         ERROR_UNAVAILABLE,
         ERROR_NOT_FOUND,
         LOCKDOWN_USAGE,
         MESSAGE_5,
         STOPPED,
         LOCKDOWN_KICK_MESSAGE,
         LOCKDOWN_KICK_MESSAGE_GLOBAL,
         ERROR_NOT_ACTIVE,
         RELOAD_SUCCESS,
         RELOAD_FAIL,
         RELOAD_FAIL_CONNECT,
         RELOAD_FAIL_CONFIG,
         RELOAD_FAIL_MESSAGES,
         RELOAD_FAIL_TEMPLATES,
         FIX_HISTORY,
         FIX_HISTORY_RESULT,
         FIX_HISTORY_OFFLINE_UUIDS,
         IMPORT_USAGE,
         IMPORT_START,
         IMPORT_FINISH,
         IMPORT_FINISH_LITEBANS,
         IMPORT_FAIL,
         IMPORT_UNSUPPORTED,
         ALLOW_USAGE,
         ALLOW_ADDED,
         ALLOW_REMOVED,
         ALLOW_CHECK_TRUE,
         ALLOW_CHECK_FALSE,
         ALLOW_ERROR_ADD,
         ALLOW_ERROR_REMOVE,
         RESET_DATABASE_CONFIRM_QUOTED,
         RESET_DATABASE_RESULT,
         DURATION,
         EXPIRED,
         FOREVER,
         YEAR,
         YEARS,
         MONTH,
         MONTHS,
         WEEK,
         WEEKS,
         DAY,
         DAYS,
         HOUR,
         HOURS,
         MINUTE,
         MINUTES,
         SECOND,
         SECONDS,
         DURATION_FORMAT,
         DURATION_SEPARATOR,
         STRINGS,
         STRINGS_GLOBAL,
         STRINGS_NULL,
         STRINGS_TRUE,
         STRINGS_FALSE,
         WEB,
         WEB_EVENT,
         WEB_BAN,
         WEB_BAN_TITLE,
         WEB_BAN_COLOR,
         WEB_BAN_CONTENT,
         WEB_BAN_FIELDS,
         WEB_BAN_THUMB,
         WEB_UNBAN,
         WEB_UNBAN_TITLE,
         WEB_UNBAN_COLOR,
         WEB_UNBAN_CONTENT,
         WEB_UNBAN_FIELDS,
         WEB_UNBAN_THUMB,
         WEB_MUTE,
         WEB_MUTE_TITLE,
         WEB_MUTE_COLOR,
         WEB_MUTE_CONTENT,
         WEB_MUTE_FIELDS,
         WEB_MUTE_THUMB,
         WEB_UNMUTE,
         WEB_UNMUTE_TITLE,
         WEB_UNMUTE_COLOR,
         WEB_UNMUTE_CONTENT,
         WEB_UNMUTE_FIELDS,
         WEB_UNMUTE_THUMB,
         WEB_WARN,
         WEB_WARN_TITLE,
         WEB_WARN_COLOR,
         WEB_WARN_CONTENT,
         WEB_WARN_FIELDS,
         WEB_WARN_THUMB,
         WEB_UNWARN,
         WEB_UNWARN_TITLE,
         WEB_UNWARN_COLOR,
         WEB_UNWARN_CONTENT,
         WEB_UNWARN_FIELDS,
         WEB_UNWARN_THUMB,
         WEB_KICK,
         WEB_KICK_TITLE,
         WEB_KICK_COLOR,
         WEB_KICK_CONTENT,
         WEB_KICK_FIELDS,
         WEB_KICK_THUMB,
         WEB_ALTS,
         WEB_ALTS_TITLE,
         WEB_ALTS_COLOR,
         WEB_ALTS_THUMB
      };
   }
}
