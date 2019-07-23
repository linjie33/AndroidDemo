package com.planetwalk.ponion.db;

import com.planetwalk.ponion.db.Entity.BuddyConstants;
import com.planetwalk.ponion.db.Entity.BuddyEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DataGenerator {
    public static BuddyEntity generateMeBuddy() {
        BuddyEntity buddyEntity = new BuddyEntity();
        buddyEntity.coins = 3000;
        buddyEntity.type = BuddyConstants.TYPE_ME;
        return buddyEntity;
    }
}
