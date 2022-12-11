package dev.giorno;

import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ServerboundSignUpdatePacket;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import java.beans.ConstructorProperties;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class SignManager {

    private final Plugin plugin;
    private final Map<UUID, SignGUI> guiMap;
    private final PluginManager pluginManager;

    @ConstructorProperties({"plugin"})
    public SignManager(Plugin plugin) {
        this.plugin = plugin;
        this.guiMap = new HashMap<>();
        this.pluginManager = Bukkit.getPluginManager();
    }

    public void init() {
        this.pluginManager.registerEvents(new SignListener(), this.plugin);
    }

    private class SignListener implements Listener {

        @EventHandler()
        public void onPlayerJoin(PlayerJoinEvent event) {
            final Player player = event.getPlayer();
            ChannelDuplexHandler channelDuplexHandler = new ChannelDuplexHandler() {
                @Override
                public void channelRead(ChannelHandlerContext ctx, Object packet) throws Exception {
                    if (packet instanceof ServerboundSignUpdatePacket) {
                        ServerboundSignUpdatePacket inUpdateSign = (ServerboundSignUpdatePacket) packet;
                        if (guiMap.containsKey(player.getUniqueId())) {
                            SignGUI signGUI = guiMap.get(player.getUniqueId());

                            BlockPos blockPosition = inUpdateSign.getPos();
                            String[] lines = inUpdateSign.getLines();

                            signGUI.getCompleteHandler().onComplete(new SignCompleteEvent(player, blockPosition, lines));
                            guiMap.remove(player.getUniqueId());
                        }
                    }
                    super.channelRead(ctx, packet);
                }
            };
            final ChannelPipeline pipeline = ((CraftPlayer) player).getHandle().connection.getConnection().channel.pipeline();
            pipeline.addBefore("packet_handler", player.getName(), channelDuplexHandler);
        }

        @EventHandler()
        public void onPlayerQuit(PlayerQuitEvent event) {
            final Channel channel = ((CraftPlayer) event.getPlayer()).getHandle().connection.getConnection().channel;
            channel.eventLoop().submit(() -> channel.pipeline().remove(event.getPlayer().getName()));
            guiMap.remove(event.getPlayer().getUniqueId());
        }
    }

    /**
     * Add New gui
     *
     * @param uuid    - UUID of the player
     * @param signGUI - {@link SignGUI} instance
     */
    void addGui(UUID uuid, SignGUI signGUI) {
        this.guiMap.put(uuid, signGUI);
    }

    protected Map<UUID, SignGUI> getGUIMap() {
        return guiMap;
    }
}