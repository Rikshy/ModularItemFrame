package de.shyrik.modularitemframe.common.upgrade;

import de.shyrik.modularitemframe.api.UpgradeBase;

public class Upgrades {
    public class UpgradeSpeed extends UpgradeBase {
        @Override
        public int getMaxCount() {
            return 5;
        }
    }

    public class UpgradeRange extends UpgradeBase {
        @Override
        public int getMaxCount() {
            return 5;
        }
    }

    public class UpgradeCapacity extends UpgradeBase {
        @Override
        public int getMaxCount() {
            return 5;
        }
    }

    public class UpgradeBlastResist extends UpgradeBase {

        @Override
        public int getMaxCount() {
            return 1;
        }
    }
}
