import com.kariqu.om.domain.Const
import com.kariqu.usercenter.domain.User
import org.apache.commons.lang.math.NumberUtils

def execute(context, params) {
    //当前用户信息
    Integer userId = context.get("curUserId")
    User curUser = null

    boolean userLogin = false
    if (userId != null) {
        curUser = userService.getUserById(userId)
        userLogin = true
    }

    Const constInfo = constService.getConstByKey("signInRule")

    //当前用户是否签到, 连续签到几天
    Map<String, Object> signInfo = userPointService.todaySignPointInfo(userId, constInfo.constValue ?: "")

    //取推荐的换购的商品的信息
    String recommendActivityType = params.get("activityType")  //推荐的活动类型
    String recommendActivityId = params.get("recommendActivityId") //推荐的活动id
    Map<String, Object> recommendItem = null
    if (recommendActivityType == 'IntegralConversion') {
        recommendItem = integralActivityService.fetchIntegralConversionById(NumberUtils.toInt(recommendActivityId))
    } else {
        recommendItem = integralActivityService.fetchSuperConversionById(NumberUtils.toInt(recommendActivityId))
    }

    //积分兑换 活动
    List<Map<String, Object>> integralList = integralActivityService.fetchIntegralConversions()
    //积分优惠购
    List<Map<String, Object>> superList = integralActivityService.fetchSuperConversion()

    //活动推荐图
    String activityImgUrl = params.get("activityImgUrl")

    String activitySmallImgUrl = params.get("activitySmallImgUrl")

    [
        "integralList"         : integralList,
        "superList"            : superList,
        "curUser"              : curUser,
        "recommendItem"        : recommendItem,
        "greeting"             : generateGreeting(),
        "signInfo"             : signInfo,
        "userLogin"            : userLogin,
        "recommendActivityType": recommendActivityType,
        "activityImgUrl"       : activityImgUrl,
        "activitySmallImgUrl"  : activitySmallImgUrl
    ]
}

def executeForm(context, params) {
    new HashMap()
}

def generateGreeting() {
    String info = ""
    switch (new Date().getAt(Calendar.HOUR_OF_DAY)) {
        case 0..6:
            info = "深夜好"
            break
        case 7..9:
            info = "早上好"
            break
        case 10..12:
            info = "上午好"
            break
        case 12..13:
            info = "中午好"
            break
        case 14..18:
            info = "下午好"
            break
        case 19..23:
            info = "晚上好"
            break
    }
    info
}