package de.luzifer.core.utils;

import de.luzifer.core.Core;
import org.apache.commons.io.IOUtils;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Objects;

public class Variables {

    static File file = new File("plugins/AntiAC", "messages.yml");

    public static ArrayList<String> PLAYER_OFFLINE = new ArrayList<>();
    public static ArrayList<String> NOTIFY_ACTIVATED = new ArrayList<>();
    public static ArrayList<String> NOTIFY_DEACTIVATED = new ArrayList<>();
    public static ArrayList<String> NOTIFY_ALREADY_ACTIVATED = new ArrayList<>();
    public static ArrayList<String> NOTIFY_ALREADY_DEACTIVATED = new ArrayList<>();
    public static ArrayList<String> ON_CLICK_CHECK = new ArrayList<>();
    public static ArrayList<String> ON_CLICK_CHECK_OFF = new ArrayList<>();
    public static ArrayList<String> NOT_CHECKING_ANYONE = new ArrayList<>();
    public static ArrayList<String> BAN_REASON = new ArrayList<>();
    public static ArrayList<String> KICK_REASON = new ArrayList<>();
    public static ArrayList<String> PLAYER_NOW_OFFLINE = new ArrayList<>();
    public static ArrayList<String> PUNISHED = new ArrayList<>();
    public static ArrayList<String> SHOUTOUT_PUNISHMENT = new ArrayList<>();
    public static ArrayList<String> TEAM_NOTIFY = new ArrayList<>();

    public static void init() {

        if(!file.exists()) {

            try {
                PrintWriter pw = new PrintWriter(file);

                pw.print(IOUtils.toString(Objects.requireNonNull(Core.getInstance().getResource("messages.yml"))));
                pw.flush();
                pw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);

        try {
            cfg.load(file);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }

        PLAYER_OFFLINE.addAll(cfg.getStringList("Player-Not-Online"));

        NOTIFY_ACTIVATED.addAll( cfg.getStringList("Activate-Notify"));

        NOTIFY_DEACTIVATED.addAll(cfg.getStringList("Deactivate-Notify"));

        NOTIFY_ALREADY_ACTIVATED.addAll(cfg.getStringList("Notify-Already-Activated")) ;

        NOTIFY_ALREADY_DEACTIVATED.addAll(cfg.getStringList("Notify-Already-Deactivated"));

        ON_CLICK_CHECK.addAll(cfg.getStringList("On-Click-Check"));

        ON_CLICK_CHECK_OFF.addAll(cfg.getStringList("On-Click-Check-Off"));

        NOT_CHECKING_ANYONE.addAll(cfg.getStringList("Not-Checking-Anyone"));

        BAN_REASON.addAll(cfg.getStringList("Ban-Reason"));

        KICK_REASON.addAll(cfg.getStringList("Kick-Reason"));

        PLAYER_NOW_OFFLINE.addAll(cfg.getStringList("Player-Now-Offline"));

        PUNISHED.addAll(cfg.getStringList("Punished"));

        SHOUTOUT_PUNISHMENT.addAll(cfg.getStringList("ShoutOut-Punishment"));

        TEAM_NOTIFY.addAll(cfg.getStringList("Team-Notify"));
    }

}
