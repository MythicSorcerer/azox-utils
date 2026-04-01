package net.azox.utils.storage;

import net.azox.utils.AzoxUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public abstract class BaseStorage {

    protected final String fileName;
    protected final File file;
    protected FileConfiguration config;

    protected BaseStorage(final String fileName) {
        this.fileName = Objects.requireNonNull(fileName, "File name cannot be null");
        final AzoxUtils plugin = AzoxUtils.getInstance();
        this.file = new File(plugin.getDataFolder(), fileName);
        if (!file.exists()) {
            final File parent = file.getParentFile();
            if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }
            try {
                file.createNewFile();
            } catch (final IOException exception) {
                plugin.getLogger().severe("Failed to create file: " + fileName);
                exception.printStackTrace();
            }
        }
        this.config = YamlConfiguration.loadConfiguration(file);
    }

    public void save() {
        try {
            this.config.save(file);
        } catch (final IOException exception) {
            AzoxUtils.getInstance().getLogger().severe("Failed to save file: " + fileName);
            exception.printStackTrace();
        }
    }

    public void reload() {
        this.config = YamlConfiguration.loadConfiguration(file);
    }
}
