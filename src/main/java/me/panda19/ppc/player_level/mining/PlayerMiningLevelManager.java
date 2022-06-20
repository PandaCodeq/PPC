package me.panda19.ppc.player_level.mining;

import me.panda19.ppc.player_level.SkillType;

public class PlayerMiningLevelManager {

    private int level;
    private int xp;

    public PlayerMiningLevelManager(int level, int xp) {
        this.level = level;
        this.xp = xp;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getXp() {
        return xp;
    }

    public void setXp(int xp) {
        this.xp = xp;
    }


    public SkillType.skillType getType() { //FIXA DEN HÄR ASAP

        return SkillType.skillType.MINING;
    }
}