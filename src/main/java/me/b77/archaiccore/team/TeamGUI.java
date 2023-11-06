package me.b77.archaiccore.team;

import me.b77.archaiccore.ArchaicCore;
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
import java.util.UUID;

public class TeamGUI {

    public void displayGUI(Player player){
        Inventory inventory = Bukkit.createInventory(player, 54, ChatColor.RED + "Teams");

        ItemStack borderItem = new ItemStack(Material.STAINED_GLASS_PANE);
//        ItemStack leaveItem = createLeaveItem(); leave item

        setBorder(inventory, borderItem);
        setTeams(inventory);
//        inventory.setItem(49, leaveItem); leave item

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
        for (int index = 1; index <= teamCount; index++) {
            int slot = getSlotForIndex(index);
            inventory.setItem(slot, generateTeamItem(index));
        }
    }

    private int getSlotForIndex(int index) {
        if (index <= 7) return 9 + index;
        if (index <= 14) return 18 + index - 7;
        if (index <= 21) return 27 + index - 14;
        if (index <= 28) return 36 + index - 21;
        return -1; // Return a default value or handle this case as needed
    }

    private ItemStack generateTeamItem(int teamId){
        TeamDatabase teamDatabase = new TeamDatabase();
        String teamLeader = teamDatabase.getTeamleader(teamId);
        String teamName = teamDatabase.getTeamname(teamId);
        int memberCount = teamDatabase.getTeamCount(teamId);

        ItemStack skull = getSkull(teamLeader);
        ItemMeta meta = skull.getItemMeta();

        ArrayList<String> lore = new ArrayList<>();
        lore.add("The team leader is " + teamLeader);
        lore.add("This team has " + memberCount + (memberCount == 1 ? " member" : " members"));

        meta.setDisplayName(teamName);
        meta.setLore(lore);

        skull.setItemMeta(meta);
        return skull;
    }

    private ItemStack createLeaveItem() {
        ItemStack leaveItem = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 14);
        ItemMeta meta = leaveItem.getItemMeta();
        meta.setDisplayName(ChatColor.RED + "Leave your current team");
        leaveItem.setItemMeta(meta);
        return leaveItem;
    }

    public ItemStack getSkull(String username){
        UUID uuid = ArchaicCore.getPlugin().getServer().getPlayer(username).getUniqueId();
        ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (byte) 3);
        SkullMeta meta = (SkullMeta) skull.getItemMeta();
        meta.setOwningPlayer(Bukkit.getOfflinePlayer(uuid));
        skull.setItemMeta(meta);
        return skull;
    }
}
