package me.b77.archaiccore.beacon;

import me.b77.archaiccore.database.BeaconDatabase;
import me.b77.archaiccore.database.PlayerDatabase;
import me.b77.archaiccore.database.TeamDatabase;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public class BeaconEvents implements Listener {
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) throws InterruptedException {
        Block block = event.getBlock();
        if (block.getType() == Material.BEACON){
            int x = block.getX();
            int y = block.getY();
            int z = block.getZ();
            Player player = event.getPlayer();
            TeamDatabase Teamdatabase = new TeamDatabase();
            PlayerDatabase Playerdatabase = new PlayerDatabase();

            String broken = "Unknown Team";
            int broken_id = 0;

            BeaconDatabase beacondatabase = new BeaconDatabase();
            for (int id : beacondatabase.getTeams()){
                if(beacondatabase.compareCoords(id, x, y, z)){
                    broken = Teamdatabase.getTeamname(id);
                    broken_id = id;
                }
            }

            String breaker = ChatColor.stripColor(player.getDisplayName());
            int breakingTeamid = Playerdatabase.getTeamId(breaker);
            String breakingTeam = Teamdatabase.getTeamname(breakingTeamid);
            if (broken.equals(breakingTeam)) {
                event.setCancelled(true);
                player.sendMessage("You cannot break your own beacon!");
            } else {
                block.setType(Material.AIR);
                BeaconMessage sender = new BeaconMessage();
                sender.send_embed(sender.create_embed(x, y, z, breakingTeam, broken));
                Teamdatabase.set_Broken(broken_id, 1);
            }
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) throws InterruptedException {
        Block block = event.getBlock();
        if (block.getType() == Material.BEACON){
            Player player = event.getPlayer();
            String username = ChatColor.stripColor(event.getPlayer().getDisplayName());
            PlayerDatabase database = new PlayerDatabase();
            TeamDatabase teamdatabase = new TeamDatabase();
            int placingTeam = database.getTeamId(username);

            if (placingTeam == 0) {
                event.setCancelled(true);
                player.sendMessage("You must be in a team to place a beacon.");
                return;
            }

            int x = block.getX();
            int y = block.getY();
            int z = block.getZ();

            BeaconDatabase beacondatabase = new BeaconDatabase();

            if (!beacondatabase.beaconExists(placingTeam)){
                beacondatabase.addBeacon(x,y,z,placingTeam);
                teamdatabase.set_placed(placingTeam, 1);
            } else {
                event.setCancelled(true);
                player.sendMessage("You should not have gotten another beacon");
            }
        }
    }
}
