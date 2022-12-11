package dev.giorno;

import com.google.common.base.Preconditions;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundOpenSignEditorPacket;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_19_R1.block.CraftSign;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_19_R1.util.CraftMagicNumbers;
import org.bukkit.entity.Player;

import java.beans.ConstructorProperties;

public final class SignGUI {

    private final SignManager signManager;
    private final SignClickCompleteHandler completeHandler;
    private Player player;
    private String[] lines;

    @ConstructorProperties({"signManager", "completeHandler"})
    public SignGUI(SignManager signManager, SignClickCompleteHandler completeHandler) {
        this.signManager = signManager;
        this.completeHandler = completeHandler;
        this.lines = new String[4];
        this.player = null;
    }

    public SignGUI withLines(String... lines) {
        if (lines.length != 4) {
            throw new IllegalArgumentException("Must have at least 4 lines");
        }

        this.lines = lines;
        return this;
    }

    public void open(Player player) {
        open(player, Material.OAK_SIGN);
    }
    public void open(Player player, Material signType) {
        this.player = player;

        final BlockPos blockPosition = new BlockPos(player.getLocation().getBlockX(), 1, player.getLocation().getBlockZ());

        ClientboundBlockUpdatePacket packet = new ClientboundBlockUpdatePacket(blockPosition, CraftMagicNumbers.getBlock(signType, (byte) 0));
        sendPacket(packet);

        Component[] components = CraftSign.sanitizeLines(lines);
        SignBlockEntity sign = new SignBlockEntity(blockPosition, CraftMagicNumbers.getBlock(signType, (byte) 0).rotate(Rotation.NONE));
        sign.setColor(DyeColor.BLACK);

        for (int i = 0; i < components.length; i++)
            sign.setMessage(i, components[i]);

        sendPacket(sign.getUpdatePacket());

        ClientboundOpenSignEditorPacket outOpenSignEditor = new ClientboundOpenSignEditorPacket(blockPosition);
        sendPacket(outOpenSignEditor);
        this.signManager.addGui(player.getUniqueId(), this);
    }

    private void sendPacket(Packet<?> packet) {
        Preconditions.checkNotNull(this.player);
        ((CraftPlayer) this.player).getHandle().connection.send(packet);
    }

    SignClickCompleteHandler getCompleteHandler() {
        return this.completeHandler;
    }
}