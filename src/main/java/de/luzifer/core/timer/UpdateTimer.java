package de.luzifer.core.timer;

import de.luzifer.core.Core;
import de.luzifer.core.utils.UpdateChecker;
import org.bukkit.Bukkit;

public class UpdateTimer implements Runnable{

    private Core core;
    public UpdateTimer(Core core) {
        this.core = core;
    }

    @Override
    public void run() {
        new UpdateChecker(core, 74933).getVersion(version -> {
            if (!core.getDescription().getVersion().equals(version)) {
                    Bukkit.broadcastMessage(" ");
                    Bukkit.broadcastMessage(
                            Core.prefix + "§6An update is available -> "
                    );
                    Bukkit.broadcastMessage("§ehttps://www.spigotmc.org/resources/anti-autoclicker-1-8-x-1-16-2.74933");
                    Bukkit.broadcastMessage("§6§lYour current version : §e" + core.getDescription().getVersion());
                    Bukkit.broadcastMessage("§6§lNewest version : §e" + version);
                    Bukkit.broadcastMessage(" ");
            }
        });
    }
}
