package com.mineaurion.onlinetablist;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedServerPing;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class OnlinetablistSpigot extends JavaPlugin {

    private ProtocolManager protocolManager;

    @Override
    public void onEnable() {
        protocolManager = ProtocolLibrary.getProtocolManager();
        protocolManager.addPacketListener(
            new PacketAdapter(this, ListenerPriority.HIGHEST ,PacketType.Status.Server.SERVER_INFO) {
                @Override
                public void onPacketSending(PacketEvent event) {
                    WrappedServerPing ping = (WrappedServerPing)event.getPacket().getServerPings().read(0);
                    int number;
                    try {
                        number = getOnlinePlayers();
                    } catch (Exception e) {
                        number = Bukkit.getOnlinePlayers().size();
                    }
                    ping.setPlayersOnline(number);
                }
            }
        );
    }

    private int getOnlinePlayers() throws IOException {
        StringBuilder jsonS = new StringBuilder();
        URL url = new URL("https://api.mineaurion.com/v1/website/home");
        URLConnection conn = url.openConnection();
        conn.connect();
        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String inputLine;

        while((inputLine = in.readLine()) != null) {
            jsonS.append(inputLine);
        }
        Gson gson = new Gson();
        JsonObject jsonObject= gson.fromJson(jsonS.toString(), JsonObject.class);
        int joueurs = jsonObject.get("joueurs").getAsInt();
        in.close();
        return joueurs;
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
