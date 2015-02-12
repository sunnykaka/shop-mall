var EJS = EJS || {};

/**
 * 资源集合
 * @type {{}}
 */
EJS.ResourceSet = {};
EJS.ResourceSet.Account = "账户";
EJS.ResourceSet.Role = "角色";
EJS.ResourceSet.Permission = "权限";
EJS.ResourceSet.Template = "模板";
EJS.ResourceSet.Page = "页面";
EJS.ResourceSet.Product = "商品";
EJS.ResourceSet.Supplier = "商家";
EJS.ResourceSet.NavigateCategory = "前台类目";
EJS.ResourceSet.Category = "后台类目";
EJS.ResourceSet.Order = "订单";
EJS.ResourceSet.User = "注册用户";
EJS.ResourceSet.Consultation = "咨询";
EJS.ResourceSet.Content = "内容";
EJS.ResourceSet.Picture = "图片";
EJS.ResourceSet.Log = "日志";
EJS.ResourceSet.Finance = "退款单";
EJS.ResourceSet.BackGoods = "退货单";
EJS.ResourceSet.Valuation = "评论";
EJS.ResourceSet.Feedback = "反馈";
EJS.ResourceSet.LimitTime = "限时折扣";
EJS.ResourceSet.Surveys = "问卷";
EJS.ResourceSet.Email = "邮件";
EJS.ResourceSet.MealSet = "套餐";
EJS.ResourceSet.Coupon = "优惠劵";
EJS.ResourceSet.Sms= "短信发送";
EJS.ResourceSet.Const = "常数";
EJS.ResourceSet.Rotary = "幸运抽奖";
EJS.ResourceSet.Trade = "交易记录";
EJS.ResourceSet.Exchange = "积分换购";
EJS.ResourceSet.Program = "设置终端首页图片";

EJS.Resource = [];
for (var p in EJS.ResourceSet) {
    EJS.Resource.push([EJS.ResourceSet[p], p]);
}

EJS.ResourceRenderFunction = function (value) {
    return EJS.ResourceSet[value];
};


/**
 * 功能集合
 * @type {{}}
 */
EJS.FunctionSet = {};
EJS.FunctionSet.Account = "账户";
EJS.FunctionSet.Cms = "内容";
EJS.FunctionSet.Template = "模版";
EJS.FunctionSet.Order = "订单";
EJS.FunctionSet.Page = "页面";
EJS.FunctionSet.Design = "装修";
EJS.FunctionSet.Module = "模块";
EJS.FunctionSet.Category = "后台类目";
EJS.FunctionSet.NavigateCategory = "前台类目";
EJS.FunctionSet.Supplier = "商家";
EJS.FunctionSet.PictureSpace = "图片";
EJS.FunctionSet.Log = "日志";
EJS.FunctionSet.User = "用户";
EJS.FunctionSet.Feedback = "反馈";
EJS.FunctionSet.Finance = "退款";
EJS.FunctionSet.BackGoods = "退单";
EJS.FunctionSet.Consultation = "咨询";
EJS.FunctionSet.LimitTime = "限时折扣";
EJS.FunctionSet.Product = "商品";
EJS.FunctionSet.Valuation = "商品评论";
EJS.FunctionSet.Surveys = "问卷调查";
EJS.FunctionSet.Email = "邮件发送";
EJS.FunctionSet.MealSet = "套餐搭配";
EJS.FunctionSet.Coupon = "优惠劵管理";
EJS.FunctionSet.Sms = "短信发送";
EJS.FunctionSet.Const = "常数设置";
EJS.FunctionSet.Rotary = "幸运抽奖";
EJS.FunctionSet.Trade = "交易记录";
EJS.FunctionSet.Exchange = "积分换购";
EJS.FunctionSet.Program = "设置终端首页图片";
