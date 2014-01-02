package co.mccn.referfriend;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.logging.Level;


public class Events implements Listener {

    private final ReferFriend _plugin;

    public Events(ReferFriend plugin) {
        _plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Long unixTime = System.currentTimeMillis() / 1000L;
        String ip = player.getAddress().getAddress().getHostAddress();
        _plugin.initializePlayer(player, unixTime, ip);
        if (player.hasPlayedBefore()) {
            String oldIP = _plugin.getIP(player.getName());
            if (!(oldIP.equals(ip))) {
                _plugin.updateIP(player.getName(), ip);
            }
        }
    }
    @EventHandler
    public void onPlayerQuir(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        //plugin.updateDatabase(player.getName());
        //plugin.getTokenMap().remove(player.getName());  I don't know what this be doing yet.

    }

}
