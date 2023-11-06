package me.b77.archaiccore.EventListeners;

import me.b77.archaiccore.database.PlayerDatabase;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinEvent implements Listener {
    @EventHandler
    public void onJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();
        String playername = player.getDisplayName();
        String uuid = player.getUniqueId().toString();
        PlayerDatabase database = new PlayerDatabase();
        if (!database.player_exists(playername)) {
            database.addPlayer(playername, uuid, 0, 0);
        }
    }
}
