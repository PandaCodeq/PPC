package me.panda19.ppc.enchants.general;

import io.papermc.paper.enchantments.EnchantmentRarity;
import me.panda19.ppc.PPC;
import net.kyori.adventure.text.Component;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.EntityCategory;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class testenchant extends Enchantment implements Listener {


    public testenchant(@NotNull NamespacedKey key) {
        super(key);
    }

    @EventHandler
    public void onHit(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player) {
            Player player = (Player) event.getDamager();

            ItemStack mainhand = player.getInventory().getItemInMainHand();

            if (mainhand.containsEnchantment(this)) {
                player.getWorld().createExplosion(event.getEntity().getLocation(), 1, false);
                if(player.getWorld().getViewDistance() > 2) {
                    player.setViewDistance(2);

                } else {
                    player.setViewDistance(4);
                }

            }
        }
    }

    public int getId() {
        return 101;
    }

    @Override
    public @NotNull NamespacedKey getKey() {
        return new NamespacedKey(PPC.INSTANCE, "101");
    }

    @Override
    public boolean canEnchantItem(@NotNull ItemStack arg0) {
        return true;
    }

    @Override
    public @NotNull Component displayName(int level) {
        return null;
    }

    @Override
    public boolean isTradeable() {
        return false;
    }

    @Override
    public boolean isDiscoverable() {
        return false;
    }

    @Override
    public @NotNull EnchantmentRarity getRarity() {
        return null;
    }

    @Override
    public float getDamageIncrease(int level, @NotNull EntityCategory entityCategory) {
        return 0;
    }

    @Override
    public @NotNull Set<EquipmentSlot> getActiveSlots() {
        return null;
    }

    @Override
    public boolean conflictsWith(@NotNull Enchantment arg0) {
        return false;
    }

    @Override
    public @NotNull EnchantmentTarget getItemTarget() {
        return null;
    }

    @Override
    public int getMaxLevel() {
        return 2;
    }

    @Override
    public @NotNull String getName() {
        return "Test Enchant";
    }

    @Override
    public int getStartLevel() {
        return 1;
    }

    @Override
    public boolean isCursed() {
        return false;
    }

    @Override
    public boolean isTreasure() {
        return false;
    }

    @Override
    public @NotNull String translationKey() {
        return null;
    }
}
