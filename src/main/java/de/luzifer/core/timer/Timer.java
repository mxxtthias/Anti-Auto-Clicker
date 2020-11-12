package de.luzifer.core.timer;

import de.luzifer.core.Core;
import de.luzifer.core.api.check.Check;
import de.luzifer.core.api.manager.CheckManager;
import de.luzifer.core.api.player.User;
import de.luzifer.core.utils.Variables;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Timer implements Runnable {

    private final int maxAllowedClicks = Core.getInstance().getConfig().getInt("AntiAC.AllowedClicks");
    private final int highestAllowedPing = Core.getInstance().getConfig().getInt("AntiAC.HighestAllowedPing");
    private final int averageCheckHowOften = Core.getInstance().getConfig().getInt("AntiAC.AverageCheckHowOften");
    private final int clickAverageOfSeconds = Core.getInstance().getConfig().getInt("AntiAC.ClickAverageOfSeconds");

    private final boolean pingChecker = Core.getInstance().getConfig().getBoolean("AntiAC.PingChecker");
    private final boolean bypass = Core.getInstance().getConfig().getBoolean("AntiAC.Bypass");

    public void run() {

        for(Player all : Bukkit.getOnlinePlayers()) {

            User user = User.get(all.getUniqueId());

            if(user.isFrozen()) {
                Variables.PUNISHED.forEach(var -> all.sendMessage(Core.prefix + var.replace("&", "§")));
            }

            User.get(all.getUniqueId()).getClicksAverageList().add(User.get(all.getUniqueId()).getClicks());
            User.get(all.getUniqueId()).getClicksAverageCheckList().add(User.get(all.getUniqueId()).getAverage());

            if(user.getChecked() != null) {

                String message1 = "§4§l" + user.getChecked().getName();
                String message2;

                if(maxAllowedClicks - user.getChecked().getClicks() <= 8) {
                    if(!(maxAllowedClicks - user.getChecked().getClicks() <= 0)) {
                        message2 = " §e§l-> §cClicks : §c§l" + user.getChecked().getClicks() + " §6Average : §6§l" + user.getChecked().getAverage();
                    } else {
                        message2 = " §e§l-> §cClicks : §4§l" + user.getChecked().getClicks()+ " §6Average : §6§l" + user.getChecked().getAverage();
                    }
                } else {
                    message2 = " §e§l-> §cClicks : §a§l" + user.getChecked().getClicks()+ " §6Average : §6§l" + user.getChecked().getAverage();
                }

                message2 = message2 + " §6VL: §e" + user.getViolations();

                if(Core.lowTPS) {
                    message2 = " §e§l-> §c§lCannot be checked -> §4§lLowTPS";
                }

                if(pingChecker) {
                    if(user.getChecked().getPing() >= highestAllowedPing) {
                        message2 = " §e§l-> §c§lCannot be checked -> §4§lPing §8(§4" + user.getChecked().getPing() + "§8)";
                    }
                }

                if(bypass) {
                    if(user.getChecked().isBypassed()) {
                        message2 = " §e§l-> §c§lCannot be checked -> §4§lBypassed";
                    }
                }

                Core.sendActionBar(all, message1 + message2);
            }

            for(Check check : CheckManager.getChecks()) {
                check.execute(user);
            }

            if(user.clearViolations != 60*5) {
                user.clearViolations++;
            } else {
                user.clearViolations();
                user.clearViolations = 0;
            }

            if(user.isRestricted()) {
                user.setRestricted(false);
            }

            if(user.getClicksAverageCheckList().size() >= averageCheckHowOften) {

                user.getClicksAverageCheckList().remove(0);

            }
            if(User.get(all.getUniqueId()).getClicksAverageList().size() >= clickAverageOfSeconds) {
                User.get(all.getUniqueId()).getClicksAverageList().remove(0);
            }
            User.get(all.getUniqueId()).setClicks(0);
        }
        Core.deleteLogs();

    }
}
