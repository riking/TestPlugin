package test;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

public class CreditCommandExecutor implements TabExecutor {
    private final MenuPlugin plugin;

    public CreditCommandExecutor(MenuPlugin plugin) {
        this.plugin = plugin;
    }

    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        // TODO Auto-generated method stub
        return null;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName() == "addcredits") {
            // Usage: /addcredits <amount> [player=you]
            if (args.length == 0) return false;
            if (args.length > 2) return false;

            // Select player

            Player target;
            if (args.length == 2) {
                target = plugin.getServer().getPlayer(args[1]);
                if (target == null) return false;
            } else {
                if (!(sender instanceof Player)) return false;
                target = (Player) sender;
            }

            // Select amount
            int amount;
            try {
                amount = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                return false;
            }
        }
        return false;
    }

}
