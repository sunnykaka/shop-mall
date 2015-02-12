package com.kariqu.categorycenter.client.sync;

/**
 *
 * @author Tiger
 * @version 1.0.0
 * @since 11-7-13 下午1:15
 */
public enum SyncType {

    /**
     * 全同步
     */
    all {
        @Override
        public boolean equals(String syncType) {
            return this.toString().equalsIgnoreCase(syncType);

        }
    },

    /**
     * 增量同步
     */
    inc {
        @Override
        public boolean equals(String syncType) {
            return this.toString().equalsIgnoreCase(syncType);

        }
    };

    public abstract boolean equals(String syncType);
}
