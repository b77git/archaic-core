package me.b77.archaiccore;

import me.b77.archaiccore.Commands.ArchaicCommand;
import me.b77.archaiccore.EventListeners.JoinEvent;
import me.b77.archaiccore.EventListeners.KillEvent;
import me.b77.archaiccore.beacon.BeaconEvents;
import me.b77.archaiccore.database.BeaconDatabase;
import me.b77.archaiccore.database.Database;
import me.b77.archaiccore.database.PlayerDatabase;
import me.b77.archaiccore.database.TeamDatabase;
import me.b77.archaiccore.team.TeamEvents;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.bukkit.plugin.java.JavaPlugin;

public final class ArchaicCore extends JavaPlugin {
    private static ArchaicCore plugin;


    @Override
    public void onEnable() {
        plugin = this;
        Database database = new Database();

        if (!getDataFolder().exists()) {
            getDataFolder().mkdir();
            database.createNewDatabase();
            PlayerDatabase.createNewTable();
            TeamDatabase.createNewTable();
            BeaconDatabase.createNewTable();
        }

        Discord.start_bot();
        getServer().getPluginManager().registerEvents(new BeaconEvents(), this);
        getServer().getPluginManager().registerEvents(new TeamEvents(), this);
        getServer().getPluginManager().registerEvents(new JoinEvent(), this);
        getServer().getPluginManager().registerEvents(new KillEvent(), this);
        getCommand("archaic").setExecutor(new ArchaicCommand());
    }

    @Override
    public void onDisable() {
    }

    public static ArchaicCore getPlugin(){
        return plugin;
    }
}
