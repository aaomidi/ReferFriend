package co.mccn.referfriend;

import co.mccn.mccnsql.MCCNSQL;
import co.mccn.tokenapi.TokenAPI;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;


public class ReferFriend extends JavaPlugin {
    public TokenAPI tokenAPI;
    public MCCNSQL mccnSql;
    private CommandsManager commandsManager;

    @Override
    public final void onDisable() {
    }

    @Override
    public final void onLoad() {
        //Makes a config.yml file before the server starts.
        if (!new File(this.getDataFolder(), "config.yml").exists()) {
            //You don't necessarily have to use this.blah
            this.saveDefaultConfig();
            //  this.commandsManager = new CommandsManager(this);

        }

    }

    @Override
    public final void onEnable() {
        getServer().getPluginManager().registerEvents(new Events(this), this);
        Plugin plugin = getServer().getPluginManager().getPlugin("TokenAPI");
        Plugin plugin2 = getServer().getPluginManager().getPlugin("MCCNSQL");
        if (plugin == null) {
            getLogger().log(Level.SEVERE, "TokenAPI not found, Disabling...");
            getServer().getPluginManager().disablePlugin(this);
        } else {
            tokenAPI = (TokenAPI) plugin;

        }
        if (plugin2 == null) {
            getLogger().log(Level.SEVERE, "MCCNSQL not found, Disabling...");
            getServer().getPluginManager().disablePlugin(this);
        } else {
            mccnSql = (MCCNSQL) plugin2;
        }

    /*    for (Command command : PluginCommandYamlParser.parse(this)) {
            this.getCommand(command.getName()).setExecutor(this.commandsManager);
      }           */
        getCommand("referredby").setExecutor(new CommandsManager(this));
        mccnSql.setDatabase(getConfig().getString("database"));
        mccnSql.connect();
        createDatabase();

    }

    private void createDatabase() {
        mccnSql.executeUpdate("CREATE TABLE IF NOT EXISTS `referrals` (`referred` VARCHAR(16) NOT NULL, `joinDate` LONG NOT NULL, `ipaddress` VARBINARY(16) NOT NULL, `commandDate` LONG DEFAULT NULL, `referrer` VARCHAR(16) DEFAULT NULL, `commandUsed` BOOLEAN NOT NULL DEFAULT FALSE, PRIMARY KEY(`referred`)) ENGINE=InnoDB DEFAULT CHARSET=latin1;");
    }

    public void initializePlayer(Player player, long unixTime, String ip) {
        this.initializePlayer(player.getName(), unixTime, ip);
    }

    public void initializePlayer(String playerName, long unixTime, String ip) {
        String query = "INSERT IGNORE INTO `referrals` (`referred`,`joinDate`,`ipaddress`) VALUES(?, ?, INET_ATON(?));";
        mccnSql.executeUpdate(query, playerName, unixTime, ip);

    }

    public boolean checkCommand(Player player) {
        return this.checkCommand(player.getName());
    }

    public boolean checkCommand(String playerName) {
        String query = "SELECT `commandUsed` FROM `referrals` WHERE `referred`=?;";
        ResultSet resultSet = mccnSql.executeQuery(query, playerName);
        try {
            if (resultSet.next()) {
                do {
                    if (resultSet.getBoolean("commandUsed") == false) {
                        return false;
                    } else if (resultSet.getBoolean("commandUsed") == true) {
                        return true;
                    }
                } while (resultSet.next());
            }
        } catch (SQLException ex) {
            getLogger().log(Level.SEVERE, ex.getMessage());
            return true;
        }

        return true;
    }

    public long getDate(Player player) {
        return this.getDate(player.getName());
    }

    public long getDate(String playerName) {
        String query = "SELECT `joinDate` FROM `referrals` WHERE `referred`=?;";
        ResultSet resultSet = mccnSql.executeQuery(query, playerName);
        try {
            if (resultSet.next()) {
                do {
                    long joinDate = resultSet.getLong("joinDate");
                    return joinDate;
                } while (resultSet.next());
            } else {
                return -1;

            }

        } catch (SQLException ex) {
            getLogger().log(Level.SEVERE, ex.getMessage());
            return -1;
        }

    }

    public void updateDatabase(long unixTime, String referred, String referrer) {
        String query = "UPDATE `referrals` SET `commandDate`=?,`referrer`=?,`commandUsed`=? WHERE `referred`=?;";
        mccnSql.executeUpdate(query, unixTime, referrer, true, referred);
    }

    public String getIP(String playerName) {
        String query = "SELECT INET_NTOA(`ipaddress`) FROM `referrals` WHERE `referred`=?;";
        ResultSet resultSet = mccnSql.executeQuery(query, playerName);
        try {
            if (resultSet.next()) {
                do {
                    String ip = resultSet.getString(1);
                    return ip;
                } while (resultSet.next());

            } else {
                return "0.0.0.0";
            }
        } catch (SQLException ex) {
            getLogger().log(Level.SEVERE, ex.getMessage());
            return "0.0.0.0";
        }
    }

    public void updateIP(String playerName, String ip) {
        String query = "UPDATE `referrals` SET `ipaddress`=INET_ATON(?) WHERE `referred`=?;";
        mccnSql.executeUpdate(query, ip, playerName);
    }
}
