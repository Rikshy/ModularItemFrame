package de.shyrik.modularitemframe.api;

public abstract class UpgradeBase {

    public abstract int getMaxCount();

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
