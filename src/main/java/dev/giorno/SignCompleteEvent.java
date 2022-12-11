package dev.giorno;

import net.minecraft.core.BlockPos;
import org.bukkit.entity.Player;

import java.beans.ConstructorProperties;

public final class SignCompleteEvent {

    private final Player player;
    private final BlockPos location;
    private final String[] lines;

    @ConstructorProperties({"player", "location", "lines"})
    public SignCompleteEvent(Player player, BlockPos location, String[] lines)
    {
        this.player = player;
        this.location = location;
        this.lines = lines;
    }

    public final Player getPlayer()
    {
        return this.player;
    }

    public final BlockPos getLocation()
    {
        return this.location;
    }

    public final String[] getLines()
    {
        return this.lines;
    }
}