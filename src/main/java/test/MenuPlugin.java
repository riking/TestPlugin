package test;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class MenuPlugin extends JavaPlugin implements Listener {
    public Map<String, PlayerInfo> playerinfo = new HashMap<String, PlayerInfo>();

    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        getCommand("showmenu").setExecutor(this);

    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        return false;
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        PlayerInfo info = playerinfo.get(event.getWhoClicked().getName());
        if (info == null) return;
        if (info.inv == null) return;
        if (!info.inv.equals(event.getView().getTopInventory())) return;
        System.out.println("Detected click in shop");
    }

    public class PlayerInfo {
        public String name;
        public int credit;
        public Inventory inv;
    }

    public class ShopItem {
        public ItemStack item;
        public int cost;
    }
}
