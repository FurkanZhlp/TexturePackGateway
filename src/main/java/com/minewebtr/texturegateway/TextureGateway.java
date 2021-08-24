package com.minewebtr.texturegateway;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.craftbukkit.libs.org.apache.commons.io.output.ByteArrayOutputStream;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.sqlite.util.StringUtils;

import java.io.DataOutputStream;
import java.util.List;

public class TextureGateway extends JavaPlugin implements Listener {

    FileConfiguration config = getConfig();

    private static TextureGateway instance;

    @Override
    public void onEnable() {
        instance = this;
        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        this.getServer().getPluginManager().registerEvents((Listener)this, (Plugin)this);
    }

    @EventHandler
    public void sendPackRequest(PlayerJoinEvent e){
        if(config.get("url") != ""){
            Bukkit.getConsoleSender().sendMessage(e.getPlayer().getName()+":"+config.getString("url"));
            e.getPlayer().setResourcePack(config.getString("url"));
        }
    }
    @EventHandler
    public void onPackRequest(PlayerResourcePackStatusEvent e){
        switch (e.getStatus()){
            case SUCCESSFULLY_LOADED:
                sendMessage(config.getStringList("success-message"),e.getPlayer());
                sendPlayerToServer(e.getPlayer(),config.getString("connect-server"));
                break;
            case DECLINED:
            case FAILED_DOWNLOAD:
                Bukkit.getConsoleSender().sendMessage(e.getPlayer().getName()+" : "+e.getStatus().toString());
                e.getPlayer().kickPlayer(ChatColor.translateAlternateColorCodes('&', StringUtils.join(config.getStringList("kick-message"),"\n")));
                break;
        }
    }

    public static void sendPlayerToServer(Player player, String server) {
        try {
            ByteArrayOutputStream b = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(b);
            out.writeUTF("Connect");
            out.writeUTF(server);
            player.sendPluginMessage(TextureGateway.getInstance(), "BungeeCord", b.toByteArray());
            b.close();
            out.close();
        }
        catch (Exception e) {
            Bukkit.broadcastMessage(e.getLocalizedMessage());
        }
    }


    public static void sendMessage(final List<String> msg, final Player player) {
        for (String s : msg) {
            final String msgcolor = ChatColor.translateAlternateColorCodes('&', (String) s);
            player.sendMessage(msgcolor);
        }
    }

    public static TextureGateway getInstance() { return instance; }
}
