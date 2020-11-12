package de.luzifer.core.checks;

import de.luzifer.core.Core;
import de.luzifer.core.api.check.Check;
import de.luzifer.core.api.enums.ViolationType;
import de.luzifer.core.api.log.Log;
import de.luzifer.core.api.player.User;
import de.luzifer.core.utils.Variables;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.*;

public class AverageCheck extends Check {

    @Override
    public void onEnable() {

    }

    @Override
    public void execute(User user) {

        if(Core.getInstance().getConfig().getBoolean("AntiAC.AverageCheck")) {

            user.getClicksAverageCheckList().add(user.getAverage());

            if(user.getClicks() >= Core.getInstance().getConfig().getInt("AntiAC.AverageCheckNeededClicks")) {

                boolean averageCheck = false;
                if(user.getClicksAverageCheckList().size() >= Core.getInstance().getConfig().getInt("AntiAC.AverageCheckAtEntries")) {

                    averageCheck = true;

                }

                double d;
                boolean isSame = true;
                if(averageCheck) {
                    d = user.getClicksAverageCheckList().get(0);
                    for(double db : user.getClicksAverageCheckList()) {

                        if(d != db) {
                            isSame = false;
                            break;
                        }

                    }

                }

                if(isSame) {

                    user.addViolation(ViolationType.HARD);

                    if(Core.getInstance().getConfig().getBoolean("AntiAC.ConsoleNotification")) {

                        Variables.TEAM_NOTIFY.forEach(var -> Bukkit.getConsoleSender().sendMessage(Core.prefix + var.replace("&", "§").replaceAll("%player%", user.getPlayer().getName())
                                .replaceAll("%clicks%", String.valueOf(user.getClicks()))
                                .replaceAll("%average%", String.valueOf(user.getAverage())).replaceAll("%VL%", String.valueOf(user.getViolations()))));
                    }

                    if(Core.getInstance().getConfig().getBoolean("AntiAC.Log")) {
                        if(!Log.isLogged(user.getPlayer())) {
                            Log.log(user.getPlayer(), user.getClicks(), user.getAverage(), Core.getInstance().getConfig().getInt("AntiAC.AllowedClicks"), "too equal average");
                        }
                    }

                    if(Core.getInstance().getConfig().getBoolean("AntiAC.PlayerBan")) {
                        if(Core.getInstance().getConfig().getBoolean("AntiAC.ShoutOutPunishment")) {
                            Objects.requireNonNull(user.getPlayer().getLocation().getWorld()).strikeLightningEffect(user.getPlayer().getLocation());
                            Bukkit.broadcastMessage("");
                            Variables.SHOUTOUT_PUNISHMENT.forEach(var -> Bukkit.broadcastMessage(Core.prefix + var.replace("&", "§").replaceAll("%player%", user.getPlayer().getName())));
                            Bukkit.broadcastMessage("");

                            for(Player others : Bukkit.getOnlinePlayers()) {
                                Objects.requireNonNull(others.getLocation().getWorld()).spawnEntity(others.getLocation(), EntityType.FIREWORK);
                            }

                        }

                        if(Core.getInstance().getConfig().getString("AntiAC.ExecuteBanCommand").equals("") ||
                                Core.getInstance().getConfig().getString("AntiAC.ExecuteBanCommand") == null) {

                            SimpleDateFormat format = new SimpleDateFormat("dd-MM-YYYY HH:mm:ss");
                            Date date = new Date();
                            Calendar calendar = new GregorianCalendar();
                            calendar.setTime(date);
                            calendar.add(Calendar.HOUR_OF_DAY, +Core.getInstance().getConfig().getInt("AntiAC.UnbanAfterHours"));
                            String date1 = format.format(calendar.getTime());
                            String bumper = org.apache.commons.lang.StringUtils.repeat("\n", 35);
                            ArrayList<String> reasonList = new ArrayList<>(Variables.BAN_REASON);
                            String reason = bumper + "§cAnti§4AC \n " + String.join("\n ", reasonList).replace("&", "§").replaceAll("%date%", date1) + bumper;
                            user.getPlayer().kickPlayer(reason);
                            Bukkit.getBanList(BanList.Type.NAME).addBan(user.getPlayer().getName(), reason, calendar.getTime() , null);

                        } else {

                            String execute = Core.getInstance().getConfig().getString("AntiAC.ExecuteBanCommand");
                            assert execute != null;
                            execute = execute.replaceAll("%player%", user.getPlayer().getName()).replace("&", "§");

                            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), execute);

                        }

                        if(Core.getInstance().getConfig().getBoolean("AntiAC.InformTeam")) {
                            for(Player team : Bukkit.getOnlinePlayers()) {
                                if(team.hasPermission(Objects.requireNonNull(Core.getInstance().getConfig().getString("AntiAC.NeededPermission")))) {
                                    if(User.get(team.getUniqueId()).isNotified()) {
                                        team.sendMessage(" ");
                                        Variables.TEAM_NOTIFY.forEach(var -> team.sendMessage(Core.prefix + var.replace("&", "§").replaceAll("%player%", user.getPlayer().getName())
                                                .replaceAll("%clicks%", String.valueOf(user.getClicks()))
                                                .replaceAll("%average%", String.valueOf(user.getAverage())).replaceAll("%VL%", String.valueOf(user.getViolations()))));
                                        team.sendMessage(" ");
                                    }
                                }
                            }
                        }
                        if(user.getClicksAverageList().size() >= Core.getInstance().getConfig().getInt("AntiAC.ClickAverageOfSeconds")) {
                            user.getClicksAverageList().remove(0);
                        }
                        user.setClicks(0);
                        return;
                    }

                    if(Core.getInstance().getConfig().getBoolean("AntiAC.PlayerKick")) {

                        if(Core.getInstance().getConfig().getString("AntiAC.ExecuteKickCommand").equals("") ||
                                Core.getInstance().getConfig().getString("AntiAC.ExecuteKickCommand") == null) {

                            ArrayList<String> reasonList = new ArrayList<>(Variables.KICK_REASON);
                            user.getPlayer().kickPlayer("§cAnti§4AC \n " + String.join("\n ", reasonList).replace("&", "§"));

                        } else {

                            String execute = Core.getInstance().getConfig().getString("AntiAC.ExecuteKickCommand");
                            assert execute != null;
                            execute = execute.replace("%player%", user.getPlayer().getName()).replace("&", "§");

                            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), execute);

                        }

                        if(Core.getInstance().getConfig().getBoolean("AntiAC.ShoutOutPunishment")) {
                            Objects.requireNonNull(user.getPlayer().getLocation().getWorld()).strikeLightningEffect(user.getPlayer().getLocation());
                            Bukkit.broadcastMessage("");
                            Variables.SHOUTOUT_PUNISHMENT.forEach(var -> Bukkit.broadcastMessage(Core.prefix + var.replace("&", "§").replaceAll("%player%", user.getPlayer().getName())));
                            Bukkit.broadcastMessage("");

                            for(Player others : Bukkit.getOnlinePlayers()) {
                                Objects.requireNonNull(others.getLocation().getWorld()).spawnEntity(others.getLocation(), EntityType.FIREWORK);
                            }

                        }
                        if(Core.getInstance().getConfig().getBoolean("AntiAC.InformTeam")) {
                            for(Player team : Bukkit.getOnlinePlayers()) {
                                if(team.hasPermission(Objects.requireNonNull(Core.getInstance().getConfig().getString("AntiAC.NeededPermission")))) {
                                    if(User.get(team.getUniqueId()).isNotified()) {
                                        team.sendMessage(" ");
                                        Variables.TEAM_NOTIFY.forEach(var -> team.sendMessage(Core.prefix + var.replace("&", "§").replaceAll("%player%", user.getPlayer().getName())
                                                .replaceAll("%clicks%", String.valueOf(user.getClicks()))
                                                .replaceAll("%average%", String.valueOf(user.getAverage())).replaceAll("%VL%", String.valueOf(user.getViolations()))));
                                        team.sendMessage(" ");
                                    }
                                }
                            }
                        }
                        if(user.getClicksAverageList().size() >= Core.getInstance().getConfig().getInt("AntiAC.ClickAverageOfSeconds")) {
                            user.getClicksAverageList().remove(0);
                        }
                        user.setClicks(0);
                        return;
                    }

                    if(Core.getInstance().getConfig().getBoolean("AntiAC.PlayerKill")) {

                        user.getPlayer().setHealth(0);
                        Variables.PUNISHED.forEach(var -> user.getPlayer().sendMessage(Core.prefix + var.replace("&", "§")));

                        if(Core.getInstance().getConfig().getBoolean("AntiAC.ShoutOutPunishment")) {
                            Objects.requireNonNull(user.getPlayer().getLocation().getWorld()).strikeLightningEffect(user.getPlayer().getLocation());
                            Bukkit.broadcastMessage("");
                            Variables.SHOUTOUT_PUNISHMENT.forEach(var -> Bukkit.broadcastMessage(Core.prefix + var.replace("&", "§").replaceAll("%player%", user.getPlayer().getName())));
                            Bukkit.broadcastMessage("");

                            for(Player others : Bukkit.getOnlinePlayers()) {
                                Objects.requireNonNull(others.getLocation().getWorld()).spawnEntity(others.getLocation(), EntityType.FIREWORK);
                            }

                        }
                        if(Core.getInstance().getConfig().getBoolean("AntiAC.InformTeam")) {
                            for(Player team : Bukkit.getOnlinePlayers()) {
                                if(team.hasPermission(Objects.requireNonNull(Core.getInstance().getConfig().getString("AntiAC.NeededPermission")))) {
                                    if(User.get(team.getUniqueId()).isNotified()) {
                                        team.sendMessage(" ");
                                        Variables.TEAM_NOTIFY.forEach(var -> team.sendMessage(Core.prefix + var.replace("&", "§").replaceAll("%player%", user.getPlayer().getName())
                                                .replaceAll("%clicks%", String.valueOf(user.getClicks()))
                                                .replaceAll("%average%", String.valueOf(user.getAverage())).replaceAll("%VL%", String.valueOf(user.getViolations()))));
                                        team.sendMessage(" ");
                                    }
                                }
                            }
                        }
                        if(user.getClicksAverageList().size() >= Core.getInstance().getConfig().getInt("AntiAC.ClickAverageOfSeconds")) {
                            user.getClicksAverageList().remove(0);
                        }
                        user.setClicks(0);
                        return;
                    }

                    if(Core.getInstance().getConfig().getBoolean("AntiAC.PlayerFreeze")) {
                        if(!user.isFrozen()) {
                            user.setFrozen(true);

                            Variables.PUNISHED.forEach(var -> user.getPlayer().sendMessage(Core.prefix + var.replace("&", "§")));

                            if(Core.getInstance().getConfig().getBoolean("AntiAC.ShoutOutPunishment")) {
                                Objects.requireNonNull(user.getPlayer().getLocation().getWorld()).strikeLightningEffect(user.getPlayer().getLocation());
                                Bukkit.broadcastMessage("");
                                Variables.SHOUTOUT_PUNISHMENT.forEach(var -> Bukkit.broadcastMessage(Core.prefix + var.replace("&", "§").replaceAll("%player%", user.getPlayer().getName())));
                                Bukkit.broadcastMessage("");

                                for(Player others : Bukkit.getOnlinePlayers()) {
                                    Objects.requireNonNull(others.getLocation().getWorld()).spawnEntity(others.getLocation(), EntityType.FIREWORK);
                                }

                            }

                            Bukkit.getScheduler().runTaskLater(Core.getInstance(), () -> user.setFrozen(false), 20* Core.getInstance().getConfig().getInt("AntiAC.FreezeTimeInSeconds"));
                        }
                        if(Core.getInstance().getConfig().getBoolean("AntiAC.InformTeam")) {
                            for(Player team : Bukkit.getOnlinePlayers()) {
                                if(team.hasPermission(Objects.requireNonNull(Core.getInstance().getConfig().getString("AntiAC.NeededPermission")))) {
                                    if(User.get(team.getUniqueId()).isNotified()) {
                                        team.sendMessage(" ");
                                        Variables.TEAM_NOTIFY.forEach(var -> team.sendMessage(Core.prefix + var.replace("&", "§").replaceAll("%player%", user.getPlayer().getName())
                                                .replaceAll("%clicks%", String.valueOf(user.getClicks()))
                                                .replaceAll("%average%", String.valueOf(user.getAverage())).replaceAll("%VL%", String.valueOf(user.getViolations()))));
                                        team.sendMessage(" ");
                                    }
                                }
                            }
                        }
                        if(user.getClicksAverageList().size() >= Core.getInstance().getConfig().getInt("AntiAC.ClickAverageOfSeconds")) {
                            user.getClicksAverageList().remove(0);
                        }
                        user.setClicks(0);
                        return;
                    }

                    if(Core.getInstance().getConfig().getBoolean("AntiAC.RestrictPlayer")) {
                        if(!user.isRestricted()) {
                            user.setRestricted(true);

                            Variables.PUNISHED.forEach(var -> user.getPlayer().sendMessage(Core.prefix + var.replace("&", "§")));

                            if(Core.getInstance().getConfig().getBoolean("AntiAC.ShoutOutPunishment")) {
                                Objects.requireNonNull(user.getPlayer().getLocation().getWorld()).strikeLightningEffect(user.getPlayer().getLocation());
                                Bukkit.broadcastMessage("");
                                Variables.SHOUTOUT_PUNISHMENT.forEach(var -> Bukkit.broadcastMessage(Core.prefix + var.replace("&", "§").replaceAll("%player%", user.getPlayer().getName())));
                                Bukkit.broadcastMessage("");

                                for(Player others : Bukkit.getOnlinePlayers()) {
                                    Objects.requireNonNull(others.getLocation().getWorld()).spawnEntity(others.getLocation(), EntityType.FIREWORK);
                                }

                            }
                        }
                        if(Core.getInstance().getConfig().getBoolean("AntiAC.InformTeam")) {
                            for(Player team : Bukkit.getOnlinePlayers()) {
                                if(team.hasPermission(Objects.requireNonNull(Core.getInstance().getConfig().getString("AntiAC.NeededPermission")))) {
                                    if(User.get(team.getUniqueId()).isNotified()) {

                                        team.sendMessage(" ");
                                        Variables.TEAM_NOTIFY.forEach(var -> team.sendMessage(Core.prefix + var.replace("&", "§").replaceAll("%player%", user.getPlayer().getName())
                                                .replaceAll("%clicks%", String.valueOf(user.getClicks()))
                                                .replaceAll("%average%", String.valueOf(user.getAverage())).replaceAll("%VL%", String.valueOf(user.getViolations()))));
                                        team.sendMessage(" ");
                                    }
                                }
                            }
                        }
                        if(user.getClicksAverageList().size() >= Core.getInstance().getConfig().getInt("AntiAC.ClickAverageOfSeconds")) {
                            user.getClicksAverageList().remove(0);
                        }
                        user.setClicks(0);
                        return;
                    }

                    if(Core.getInstance().getConfig().getBoolean("AntiAC.InformTeam")) {
                        for(Player team : Bukkit.getOnlinePlayers()) {
                            if(team.hasPermission(Objects.requireNonNull(Core.getInstance().getConfig().getString("AntiAC.NeededPermission")))) {
                                if(User.get(team.getUniqueId()).isNotified()) {
                                    team.sendMessage(" ");
                                    Variables.TEAM_NOTIFY.forEach(var -> team.sendMessage(Core.prefix + var.replace("&", "§").replaceAll("%player%", user.getPlayer().getName())
                                            .replaceAll("%clicks%", String.valueOf(user.getClicks()))
                                            .replaceAll("%average%", String.valueOf(user.getAverage()))));
                                    team.sendMessage(" ");
                                }
                            }
                        }
                    }

                }
            }

        }

    }
}
