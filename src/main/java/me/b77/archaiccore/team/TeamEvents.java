package me.b77.archaiccore.team;

import me.b77.archaiccore.database.PlayerDatabase;
import me.b77.archaiccore.database.TeamDatabase;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class TeamEvents implements Listener {
    @EventHandler
    public void onClickEvent(InventoryClickEvent event){
        if (event.getInventory().getTitle().equalsIgnoreCase(ChatColor.RED + "Teams") || event.getInventory().getTitle().equalsIgnoreCase(ChatColor.BLUE + "Beacons")) {
            event.setCancelled(true);
            if (event.getCurrentItem().getType().equals(Material.SKULL_ITEM)){
                String player = ChatColor.stripColor(event.getWhoClicked().getName());
                PlayerDatabase playerDatabase = new PlayerDatabase();
                TeamDatabase teamDatabase = new TeamDatabase();
                if (playerDatabase.getTeamId(player) == 0) {
                    System.out.println(teamDatabase.get_team_id(event.getCurrentItem().getItemMeta().getDisplayName()));
                    playerDatabase.join_team(player, teamDatabase.get_team_id(event.getCurrentItem().getItemMeta().getDisplayName()));
                    event.getWhoClicked().sendMessage("You have joined " + event.getCurrentItem().getItemMeta().getDisplayName());
                } else {
                    event.getWhoClicked().sendMessage(ChatColor.RED + "You must leave your current team before joining a new one");
                }
            }
        }
    }
}
