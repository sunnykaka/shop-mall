package com.kariqu.tradecenter.domain;

/**
 * 不能参加活动的原因
 * Created by Canal.wen on 2014/7/24 16:41.
 */
public enum CanntJoinActivityResult {
    NoEnoughKaMoney, /*没有足够的积分*/
    JoinTooManyTime, /*参加次数过多*/
    OtherReason,  /*其它原因*/
    OK; /*成功, 可以参加活动*/

}
