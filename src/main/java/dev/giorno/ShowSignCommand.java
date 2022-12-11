package dev.giorno;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * example on how to use the gui
 */
public class ShowSignCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return true;
        Player $player = (Player) sender;
        SignGUI signGUI = new SignGUI(SignInputMain.instance.signManager, event -> {
            $player.sendMessage("Line 2: " + event.getLines()[1]);
            $player.sendMessage("Line 3: " + event.getLines()[2]);
            $player.sendMessage("Line 4: " + event.getLines()[3]);
        }).withLines("Line 1", "Line 2", "Line 3", "Line 4");

        signGUI.open($player);

        $player.sendRawMessage("opened sign editor");
        return true;
    }


}
