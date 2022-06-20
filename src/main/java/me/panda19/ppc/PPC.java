package me.panda19.ppc;

import me.panda19.ppc.ppc.Files.DataManager;
import me.panda19.ppc.enchants.general.testenchant;
import me.panda19.ppc.player_level.mining.PlayerMiningLevelManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Logger;

public final class PPC extends JavaPlugin implements Listener {

    public static PPC INSTANCE;
    public static Logger log;

    public DataManager data;

    final NamespacedKey testKey = new NamespacedKey(PPC.this, "101");
    public testenchant ench = new testenchant(testKey);

    private HashMap<UUID, PlayerMiningLevelManager> levelManagerHashMap;


    @Override
    public void onEnable() {
        // Plugin startup logic
        INSTANCE = this;

        log = getLogger();

        log.info("PPC: ENABLED, V.1");

        this.data = new DataManager(this);

        LoadEnchantments();
        this.getServer().getPluginManager().registerEvents(this, this);
        this.getServer().getPluginManager().registerEvents(ench, this);
        this.levelManagerHashMap = new HashMap<>();
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {

        Player player = event.getPlayer();

        if (!player.hasPlayedBefore()) {
            player.sendMessage("§bWelcome, your level is §a0");

            this.levelManagerHashMap.put(player.getUniqueId(), new PlayerMiningLevelManager(0, 0));
            data.getConfig().set("PlayerLevels." + player.getUniqueId() + ".level", 0);
            data.getConfig().set("PlayerLevels." + player.getUniqueId() + ".xp", 0);
            data.saveConfig();

            this.setscore(player, 0, 0);
        } else {
            int level = data.getConfig().getInt("PlayerLevels." + player.getUniqueId() + ".level");
            int xp = data.getConfig().getInt("PlayerLevels." + player.getUniqueId() + ".xp");
            levelManagerHashMap.put(player.getUniqueId(), new PlayerMiningLevelManager(level, xp));
            setscore(player, level, xp);
        }

        ItemStack ExploAxe = new ItemStack(Material.DIAMOND_AXE);
        ItemMeta meta = ExploAxe.getItemMeta();
        ArrayList<String> lore = new ArrayList<String>();
        lore.add(ChatColor.GRAY + ench.getName() + " I");
        meta.setDisplayName(ChatColor.GOLD + "Explosive Axe");
        meta.setLore(lore);
        ExploAxe.setItemMeta(meta);
        ExploAxe.addEnchantment(ench, 1);
        if(!player.getInventory().contains(ExploAxe)) {
            player.getInventory().addItem(ExploAxe);
            log.info("Gave " + player.getDisplayName() + "the explosive axe!");
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onDisable() {

        this.levelManagerHashMap.clear();
        try {
            Field byIdField = Enchantment.class.getDeclaredField("byKey");
            Field byNameField = Enchantment.class.getDeclaredField("byName");

            byIdField.setAccessible(true);
            byNameField.setAccessible(true);

            HashMap<Integer, Enchantment> byId = (HashMap<Integer, Enchantment>) byIdField.get(null);
            HashMap<Integer, Enchantment> byName = (HashMap<Integer, Enchantment>) byNameField.get(null);

            byId.remove(ench.getId());
            byName.remove(ench.getName());

        } catch (Exception ignored) {
        }
    }

    private void LoadEnchantments() {
        try {
            try {
                Field f = Enchantment.class.getDeclaredField("acceptingNew");
                f.setAccessible(true);
                f.set(null, true);
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                Enchantment.registerEnchantment(ench);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Block break
    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        Player player = e.getPlayer();
        int amount = 0;

        if(this.data.getConfig().contains("players." + player.getUniqueId() + ".total")) {
            amount = this.data.getConfig().getInt("players." + player.getUniqueId() + ".total");
        }
        data.getConfig().set("players." + player.getUniqueId() + ".total", amount + 1);

        PlayerMiningLevelManager playerMiningLevelManager = this.levelManagerHashMap.get(player.getUniqueId());
        Block block = e.getBlock();

        if (block.getType() == Material.STONE) {
            playerMiningLevelManager.setXp(playerMiningLevelManager.getXp() + 3);
            player.sendMessage("§a+3 §bExperience");
            xpcheck(player, playerMiningLevelManager);
            setscore(player, playerMiningLevelManager.getLevel(), playerMiningLevelManager.getXp());
            data.getConfig().set("PlayerLevels." + player.getUniqueId() + ".level", playerMiningLevelManager.getLevel());
            data.getConfig().set("PlayerLevels." + player.getUniqueId() + ".xp", playerMiningLevelManager.getXp());
            player.sendTitle("test", ChatColor.AQUA + "" + playerMiningLevelManager.getXp(), 10, 30, 10);  //FIXA SÖK HJÄLP AV NÅGON FÖR ATT GÖRA DET MINDRE OCH ÄNDRA KORDINATER
        }
        data.saveConfig();
    }


    @EventHandler
    public void quit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        PlayerMiningLevelManager playerMiningLevelManager = this.levelManagerHashMap.get(player.getUniqueId());

        if (this.levelManagerHashMap.containsKey(player.getUniqueId())) {
            data.getConfig().set("PlayerLevels." + player.getUniqueId() + ".level", playerMiningLevelManager.getLevel());
            data.getConfig().set("PlayerLevels." + player.getUniqueId() + ".xp", playerMiningLevelManager.getXp());
            data.saveConfig();
            this.levelManagerHashMap.remove(player.getUniqueId());
        }
    }


    private void xpcheck(Player player, PlayerMiningLevelManager playerMiningLevelManager) {

        //int level = data.getConfig().getInt("PlayerLevels." + player.getUniqueId() + ".level");

        int xpneeded = this.getConfig().getInt("PlayerLevels." + player.getUniqueId() + ".level") * 15;

        int xp = playerMiningLevelManager.getXp();

        if (xp >= xpneeded) {
            log.info("XPNeeded: " + xpneeded);
            player.sendMessage("§6Leveled UP!");
            playerMiningLevelManager.setLevel(playerMiningLevelManager.getLevel() + 1);
            player.sendTitle("Level up", ChatColor.AQUA + "" + playerMiningLevelManager.getLevel(), 10, 30, 10); //FIXA SÖK HJÄLP AV NÅGON FÖR ATT GÖRA DET MINDRE OCH ÄNDRA KORDINATER
            data.getConfig().set("PlayerLevels." + player.getUniqueId() + ".level", playerMiningLevelManager.getLevel());
            data.getConfig().set("PlayerLevels." + player.getUniqueId() + ".xp", playerMiningLevelManager.getXp());
        }
        data.saveConfig();
    }

    private void setscore(Player player, int level, int xp) {
        Scoreboard scoreboard = this.getServer().getScoreboardManager().getNewScoreboard();
        Objective objective = scoreboard.registerNewObjective("test", "dummy");

        objective.setDisplayName("§cPlayer Level");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        Score lvl = objective.getScore("Level: §a" + level);
        lvl.setScore(1);
        Score exp = objective.getScore("XP: §a" + xp);
        exp.setScore(0);

        player.setScoreboard(scoreboard);
    }

}
