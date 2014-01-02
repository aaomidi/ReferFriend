package co.mccn.referfriend;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandsManager implements CommandExecutor {
    private final ReferFriend _plugin;

    public CommandsManager(ReferFriend plugin) {
        _plugin = plugin;

    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (cmd.getName().equalsIgnoreCase("referredby")) {
            if (sender.hasPermission("referfriend.use")) {
                if (sender instanceof Player) {
                    switch (args.length) {
                        case 0:
                            return false;
                        case 1:
                            //Check for the time
                            long joinDate = _plugin.getDate(sender.getName());
                            long commandDate = System.currentTimeMillis() / 1000L;
                            boolean checkComamnd = _plugin.checkCommand(sender.getName());
                            int waitingTime = _plugin.getConfig().getInt("WaitingTime");
                            System.out.println(joinDate);
                            if (!checkComamnd) {
                                if (joinDate != -1) {
                                    if (commandDate - joinDate >= (long) waitingTime) {
                                        if (_plugin.getServer().getOfflinePlayer(args[0]).hasPlayedBefore()) {
                                            OfflinePlayer player = _plugin.getServer().getOfflinePlayer(args[0]);
                                            String offlinePlayerIP=_plugin.getIP(player.getName());
                                            if(!(((Player) sender).getAddress().getAddress().getHostAddress()).equals(offlinePlayerIP)){


                                            if (player.getPlayer() != ((Player) sender).getPlayer()) {
                                                //Get number of Tokens from the config
                                                int tokens = _plugin.getConfig().getInt("Tokens");
                                                sendMessage(sender, "&aYour friend just got &b" + tokens + " &atokens!");
                                                sendMessage(sender, "&aYou can refer your friends to the server and win tokens!");
                                                _plugin.tokenAPI.updateTokens(player.getName(), tokens);
                                                //Update the database
                                                _plugin.updateDatabase(commandDate, sender.getName(), player.getName());
                                                if (player.isOnline()) {

                                                    sendMessage(player, "&b" + sender.getName() + " &asaid you referred him/her! You get&b " + tokens + "&a Tokens! Horray!");
                                                    sendMessage(player, "&aRefer more people and get more tokens!");
                                                    return true;
                                                } else {
                                                    //Do nothing
                                                    return true;
                                                }

                                            } else {
                                                sendMessage(sender, "&cYou can't run that command on yourself.");
                                                return true;
                                            }
                                            }else{
                                                sendMessage(sender, "&cThat player has the same IP as you do, you cannot be referred by someone who uses your IP!");
                                                return true;
                                            }
                                        } else {
                                            sendMessage(sender, "&b" + args[0] + " &ahas never joined this server! Try at &b/hub &ainstead");
                                            return true;
                                        }
                                    } else {
                                        sendMessage(sender, "&cYou need to play at least &4" + waitingTime + " &cminutes to be able to use that command!");
                                        return true;

                                    }
                                } else {
                                    sendMessage(sender, "&cThere is a problem with the plugin! Inform an admin ASAP");
                                }
                            } else {
                                sendMessage(sender, "&cYou've used this command before.");
                                return true;
                            }

                        default:
                            return false;
                    }
                } else {
                    sendMessage(sender, "&cCommand is in-game only");
                    return true;
                }

            }
            sendMessage(sender, "&cYou do not have permission to do this command");
            return true;

        }


        return false;
    }

    private void sendMessage(CommandSender sender, String message) {

        this.sendMessage(sender.getName(), message);
    }

    private void sendMessage(OfflinePlayer player, String message) {
        this.sendMessage(player.getName(), message);
    }

    private void sendMessage(Player player, String message) {
        this.sendMessage(player.getName(), message);
    }

    private void sendMessage(String playerName, String message) {
        String prefix = "&7[&6Refer A Friend&7]&f ";
        Bukkit.getServer().getPlayer(playerName).sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + message));


    }

}
