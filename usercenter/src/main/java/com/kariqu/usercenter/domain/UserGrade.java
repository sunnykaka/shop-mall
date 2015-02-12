package com.kariqu.usercenter.domain;

/**
 * 用户等级
 * <p/>
 * 和用户的消费金额有关，和积分无关
 * <p/>
 * User: Asion
 * Date: 13-3-18
 * Time: 上午11:17
 */
public enum UserGrade {

    A {
        @Override
        public UserGrade pre() {
            return null;
        }

        @Override
        public UserGrade next() {
            return B;
        }
    },

    B {
        @Override
        public UserGrade pre() {
            return A;
        }

        @Override
        public UserGrade next() {
            return C;
        }
    },

    C {
        @Override
        public UserGrade pre() {
            return B;
        }

        @Override
        public UserGrade next() {
            return D;
        }
    },

    D {
        @Override
        public UserGrade pre() {
            return C;
        }

        @Override
        public UserGrade next() {
            return E;
        }
    },

    E {
        @Override
        public UserGrade pre() {
            return D;
        }

        @Override
        public UserGrade next() {
            return F;
        }
    },

    F {
        @Override
        public UserGrade pre() {
            return E;
        }

        @Override
        public UserGrade next() {
            return null;
        }
    };


    /**
     * 判断自己是否小于传入的枚举
     *
     * @param grade
     * @return
     */
    public boolean lessThan(UserGrade grade) {
        return this.toString().charAt(0) < grade.toString().charAt((0));
    }

    public abstract UserGrade pre();

    public abstract UserGrade next();

}
