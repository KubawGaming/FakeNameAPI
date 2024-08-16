package me.kubaw208.fakenameapi;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import lombok.Getter;
import me.kubaw208.fakenameapi.events.PlayerInfoUpdateListener;
import me.kubaw208.fakenameapi.events.PlayerQuitListener;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public final class FakeNameAPI {

    private final JavaPlugin plugin;
    @Getter private static FakeNameAPI instance;
    @Getter private final HashMap<UUID, HashMap<UUID, String>> fakeNames = new HashMap<>();

    public FakeNameAPI(JavaPlugin plugin) {
        this.plugin = plugin;
        instance = this;

        PacketEvents.getAPI().getEventManager().registerListener(new PlayerInfoUpdateListener(this), PacketListenerPriority.HIGH);
        plugin.getServer().getPluginManager().registerEvents(new PlayerQuitListener(this), plugin);
    }

    /**
     * Sends packet with fake name for given player to given packet receivers.
     * @param playerToChange player that nick will be changed
     * @param newFakeName new fake name for `playerToChange`
     * @param packetReceivers players that will see new fake name for `playerToChange`
     */
    public void setFakeName(Player playerToChange, String newFakeName, Player... packetReceivers) {
        fakeNames.put(playerToChange.getUniqueId(), new HashMap<>() {{
            for(Player receiver : packetReceivers) {
                put(receiver.getUniqueId(), newFakeName);
            }
        }});

        for(Player receiver : packetReceivers) {
            if(!receiver.canSee(playerToChange)) continue;

            receiver.hidePlayer(plugin, playerToChange);
            receiver.showPlayer(plugin, playerToChange);
        }
    }

    /**
     * Sends packet with fake name for given player to given packet receivers.
     * @param playerToChange player that nick will be changed
     * @param newFakeName new fake name for `playerToChange`
     * @param packetReceivers players that will see new fake name for `playerToChange`
     */
    public void setFakeName(Player playerToChange, String newFakeName, Collection<? extends Player> packetReceivers) {
        setFakeName(playerToChange, newFakeName, packetReceivers.toArray(new Player[0]));
    }

    /**
     * Sends packet with fake name for given player to given packet receivers.
     * @param playerToChange player that nick will be changed
     * @param newFakeName new fake name for `playerToChange`
     * @param packetReceivers players that will see new fake name for `playerToChange`
     */
    public void setFakeName(Player playerToChange, String newFakeName, List<Player> packetReceivers) {
        setFakeName(playerToChange, newFakeName, packetReceivers.toArray(new Player[0]));
    }

    /**
     * Sends packet with real name for given player to given packet receivers.
     * @param playerWithFakeNick player that nick is changed (fake)
     * @param packetReceivers players who will see the 'playerWithFakeNick' real nick
     */
    public void unsetFakeName(Player playerWithFakeNick, Player... packetReceivers) {
        setFakeName(playerWithFakeNick, playerWithFakeNick.getName(), packetReceivers);

        for(Player packetReceiver : packetReceivers) {
            fakeNames.getOrDefault(playerWithFakeNick.getUniqueId(), new HashMap<>()).remove(packetReceiver.getUniqueId());
        }
    }

    /**
     * Sends packet with real name for given player to given packet receivers.
     * @param playerWithFakeNick player that nick is changed (fake)
     * @param packetReceivers players who will see the 'playerWithFakeNick' real nick
     */
    public void unsetFakeName(Player playerWithFakeNick, Collection<? extends Player> packetReceivers) {
        setFakeName(playerWithFakeNick, playerWithFakeNick.getName(), packetReceivers);

        for(Player packetReceiver : packetReceivers) {
            fakeNames.getOrDefault(playerWithFakeNick.getUniqueId(), new HashMap<>()).remove(packetReceiver.getUniqueId());
        }
    }

    /**
     * Sends packet with real name for given player to given packet receivers.
     * @param playerWithFakeNick player that nick is changed (fake)
     * @param packetReceivers players who will see the 'playerWithFakeNick' real nick
     */
    public void unsetFakeName(Player playerWithFakeNick, List<Player> packetReceivers) {
        setFakeName(playerWithFakeNick, playerWithFakeNick.getName(), packetReceivers);

        for(Player packetReceiver : packetReceivers) {
            fakeNames.getOrDefault(playerWithFakeNick.getUniqueId(), new HashMap<>()).remove(packetReceiver.getUniqueId());
        }
    }

    /**
     * Checks if the player had a fake nickname set up.
     * @param player player to check
     */
    public boolean hasFakeNick(Player player) {
        return fakeNames.containsKey(player.getUniqueId());
    }

    /**
     * Gets target name that player sees.
     * @param player player seeing target
     * @param target target being seen by player
     */
    public String getSeeingNick(Player player, Player target) {
        return fakeNames.get(target.getUniqueId()).getOrDefault(player.getUniqueId(), target.getName());
    }

    /**
     * Resets fake name to default player name for all players on the server.
     * @param playerToChange player that nick will be reset
     */
    public void resetName(Player playerToChange) {
        setFakeName(playerToChange, playerToChange.getName(), Bukkit.getOnlinePlayers());
        fakeNames.remove(playerToChange.getUniqueId());
    }

}