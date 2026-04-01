package net.azox.utils.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bukkit.inventory.ItemStack;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Kit {
    private String name;
    private ItemStack[] contents;
    private long cooldown; // In seconds
}
