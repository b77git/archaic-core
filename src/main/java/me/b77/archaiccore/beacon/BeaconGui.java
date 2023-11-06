package me.b77.archaiccore.beacon;

import me.b77.archaiccore.database.BeaconDatabase;
import me.b77.archaiccore.database.TeamDatabase;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;

public class BeaconGui {

    public void displayGUI(Player player){
        Inventory inventory = Bukkit.createInventory(player, 54, ChatColor.BLUE + "Beacons");

        ItemStack borderItem = new ItemStack(Material.STAINED_GLASS_PANE);
        setBorder(inventory, borderItem);
        setTeams(inventory);

        player.openInventory(inventory);
    }

    private void setBorder(Inventory inventory, ItemStack item ){
        int size = inventory.getSize();
        int rowCount = size / 9;

        for(int index = 0; index < size; index++) {
            int row = index / 9;
            int column = (index % 9) + 1;

            if(row == 0 || row == rowCount-1 || column == 1 || column == 9)
                inventory.setItem(index, item);
        }
    }

    private void setTeams(Inventory inventory) {
        TeamDatabase teamDatabase = new TeamDatabase();
        int teamCount = teamDatabase.getCount();
        int count = 0;
        for (int index = 1; index <= teamCount; index++) {
            if (count <= 7) {
                if (teamDatabase.get_placed(index) == 1) {
                    count += 1;
                    inventory.setItem(9 + count, generateTeamItem(index));
                }
            }
            if (count > 7 && count <= 14) {
                if (teamDatabase.get_placed(index) == 1) {
                    count += 1;
                    inventory.setItem(18 + count - 7, generateTeamItem(index));
                }
            }
            if (count > 14 && index <= 21) {
                if (teamDatabase.get_placed(index) == 1) {
                    count += 1;
                    inventory.setItem(27 + count - 14, generateTeamItem(index));
                }
            }
            if (count > 21 && count <= 28) {
                if (teamDatabase.get_placed(index) == 1) {
                    count += 1;
                    inventory.setItem(36 + count - 21, generateTeamItem(index));
                }
            }
        }
    }

    private ItemStack generateTeamItem(int teamId){
        TeamDatabase teamDatabase = new TeamDatabase();
        BeaconDatabase beacons = new BeaconDatabase();
        String teamLeader = teamDatabase.getTeamleader(teamId);
        String teamName = teamDatabase.getTeamname(teamId);
        int memberCount = teamDatabase.getTeamCount(teamId);

        ItemStack beacon = new ItemStack(Material.BEACON);
        ItemMeta meta = beacon.getItemMeta();
        ArrayList<Integer> coords = beacons.getCoords(teamId);

        ArrayList<String> lore = new ArrayList<String>();
        lore.add("Coords: " + coords.get(0) + "," + coords.get(1) + "," + coords.get(2));

        meta.setLore(lore);
        meta.setDisplayName(teamName);

        beacon.setItemMeta(meta);
        return beacon;
    }
}
