package me.kubaw208.fakenameapi.events;

import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerInfoUpdate;
import me.kubaw208.fakenameapi.FakeNameAPI;
import org.bukkit.entity.Player;

public class PlayerInfoUpdateListener implements PacketListener {

    private final FakeNameAPI fakeNameAPI;

    public PlayerInfoUpdateListener(FakeNameAPI fakeNameAPI) {
        this.fakeNameAPI = fakeNameAPI;
    }

    @Override
    public void onPacketSend(PacketSendEvent event) {
        if(!event.getPacketType().equals(PacketType.Play.Server.PLAYER_INFO_UPDATE)) return;
        if(!(event.getPlayer() instanceof Player player)) return;

        var packet = new WrapperPlayServerPlayerInfoUpdate(event);

        for(var entry : packet.getEntries()) {
            if(entry.getGameProfile() == null) continue;
            if(!fakeNameAPI.getFakeNames().containsKey(entry.getGameProfile().getUUID())) continue;
            if(!fakeNameAPI.getFakeNames().get(entry.getGameProfile().getUUID()).receivers().contains(player)) continue;

            entry.getGameProfile().setName(fakeNameAPI.getFakeNames().get(entry.getGameProfile().getUUID()).fakeName());
        }
    }

}