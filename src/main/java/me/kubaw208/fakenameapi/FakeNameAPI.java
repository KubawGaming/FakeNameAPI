package me.kubaw208.fakenameapi;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListenerCommon;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import lombok.Getter;
import me.kubaw208.fakenameapi.events.PlayerInfoUpdateListener;
import me.kubaw208.fakenameapi.events.PlayerQuitListener;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public final class FakeNameAPI {

    private final JavaPlugin plugin;
    @Getter private static FakeNameAPI instance;
    @Getter private final HashMap<UUID, HashMap<UUID, String>> fakeNames = new HashMap<>();
    private final PacketListenerCommon packetListener;

    public FakeNameAPI(JavaPlugin plugin) {
        this.plugin = plugin;
        instance = this;

        packetListener = PacketEvents.getAPI().getEventManager().registerListener(new PlayerInfoUpdateListener(this), PacketListenerPriority.HIGH);
        plugin.getServer().getPluginManager().registerEvents(new PlayerQuitListener(this), plugin);

        for(Player player : Bukkit.getOnlinePlayers()) {
            fakeNames.put(player.getUniqueId(), new HashMap<>());
        }
    }

    /**
     * Sends packet with fake name for given player to given packet receivers.
     * @param playerToChange player that nick will be changed
     * @param newFakeName new fake name for `playerToChange`
     * @param packetReceivers players that will see new fake name for `playerToChange`
     */
    public void setFakeName(Player playerToChange, String newFakeName, Player... packetReceivers) {
        HashMap<UUID, String> previousReceivers = fakeNames.getOrDefault(playerToChange.getUniqueId(), new HashMap<>());

        fakeNames.put(playerToChange.getUniqueId(), new HashMap<>() {{
            putAll(previousReceivers);

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
     * @param packetReceivers players who will see the 'playerWithFakeNick' real nick (if you send no packet receivers, this method does nothing!)
     */
    public void unsetFakeName(Player playerWithFakeNick, Player... packetReceivers) {
        if(packetReceivers.length == 0) return;

        setFakeName(playerWithFakeNick, playerWithFakeNick.getName(), packetReceivers);
    }

    /**
     * Sends packet with real name for given player to given packet receivers.
     * @param playerWithFakeNick player that nick is changed (fake)
     * @param packetReceivers players who will see the 'playerWithFakeNick' real nick
     */
    public void unsetFakeName(Player playerWithFakeNick, Collection<? extends Player> packetReceivers) {
        setFakeName(playerWithFakeNick, playerWithFakeNick.getName(), packetReceivers);
    }

    /**
     * Sends packet with real name for given player to given packet receivers.
     * @param playerWithFakeNick player that nick is changed (fake)
     * @param packetReceivers players who will see the 'playerWithFakeNick' real nick
     */
    public void unsetFakeName(Player playerWithFakeNick, List<Player> packetReceivers) {
        setFakeName(playerWithFakeNick, playerWithFakeNick.getName(), packetReceivers);
    }

    /**
     * Checks if the target has a fake nickname set for player perspective.
     * @param player player to check if he sees target fake nick
     * @param target target to check if player sees his fake nick
     */
    public boolean canSeeFakeNick(Player player, Player target) {
        return !fakeNames.getOrDefault(target.getUniqueId(), new HashMap<>())
                .getOrDefault(player.getUniqueId(), target.getName()).equals(target.getName());
    }

    /**
     * Gets target name that player sees. If target has not set fake name, then it returns target default name.
     * @param player player seeing target
     * @param target target being seen by player
     */
    public String getSeeingNick(Player player, Player target) {
        if(!canSeeFakeNick(player, target))
            return target.getName();

        return fakeNames.get(target.getUniqueId()).getOrDefault(player.getUniqueId(), target.getName());
    }

    /**
     * Resets fake name to default player name for all players on the server.
     * @param playerToReset player that nick will be reset
     */
    public void resetName(Player playerToReset) {
        setFakeName(playerToReset, playerToReset.getName(), Bukkit.getOnlinePlayers());
        fakeNames.get(playerToReset.getUniqueId()).clear();
    }

    /**
     * If you want to introduce compatibility with reloading your plugin when using FakeNameAPI
     * use this method to unregister events in onDisable() to prevent creation of more events and not closing previous ones
     */
    public void unregisterAllFakeNameAPIEvents() {
        PacketEvents.getAPI().getEventManager().unregisterListener(packetListener);
    }

}