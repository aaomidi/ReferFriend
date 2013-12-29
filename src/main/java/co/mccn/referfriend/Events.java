package co.mccn.referfriend;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;


public class Events implements Listener {

    private final ReferFriend _plugin;

    public Events(ReferFriend plugin) {
        _plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        Long unixTime = System.currentTimeMillis() / 1000L;
        String ip = player.getAddress().toString();
        _plugin.initializePlayer(player, unixTime, ip);
        if (player.hasPlayedBefore()) {
            String oldIP = _plugin.getIP(player.getName());
            if (!(oldIP.equals(ip))) {
                _plugin.updateIP(player.getName(), player.getAddress().toString());
            }
        }
    }

    public void onPlayerQuir(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        //plugin.updateDatabase(player.getName());
        //plugin.getTokenMap().remove(player.getName());  I don't know what this be doing yet.

    }
}
