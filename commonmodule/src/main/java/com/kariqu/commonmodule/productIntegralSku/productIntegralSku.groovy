import com.kariqu.productcenter.domain.ProductActivityType
import com.kariqu.usercenter.domain.User

def execute(context, params) {
    String activityType = context.get("activityType")
    Integer activityId = context.get("activityId")
    //当前用户信息
    Integer userId = context.get("curUserId")
    User curUser = null

    boolean userLogin = false
    if (userId != null) {
        curUser = userService.getUserById(userId)
        userLogin = true
    }

    Map<String, Object> activityMap = null
    //加入积分活动信息
    if (ProductActivityType.IntegralConversion.toString().equalsIgnoreCase(activityType)) {//积分兑换
        activityMap = integralActivityService.fetchIntegralConversionById(activityId)
    } else if ("SuperConversion".equalsIgnoreCase(activityType)) { //积分优惠购
        activityMap = integralActivityService.fetchSuperConversionById(activityId)
    }

    [
        "activityMap" : activityMap,
        "activityType": activityType,
        "userLogin"   : userLogin,
        "curUser"     : curUser
    ]
}

def executeForm(context, params) {
    new HashMap()
}
