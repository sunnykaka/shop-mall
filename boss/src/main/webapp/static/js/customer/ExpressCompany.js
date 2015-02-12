var EJS = EJS || {};

EJS.ExpressCompanySet = {};
EJS.ExpressCompanySet.shunfeng = '顺丰';
EJS.ExpressCompanySet.zhongtong = '中通';
EJS.ExpressCompanySet.yuantong = '圆通';
EJS.ExpressCompanySet.shentong = '申通';
EJS.ExpressCompanySet.yunda = '韵达';
EJS.ExpressCompanySet.zhaijisong = '宅急送';
EJS.ExpressCompanySet.quanritongkuaidi = '全日通';
EJS.ExpressCompanySet.kuaijiesudi = '快捷';
EJS.ExpressCompanySet.huitongkuaidi = '汇通';
EJS.ExpressCompanySet.ems = '邮政速递';
EJS.ExpressCompanySet.guotongkuaidi = '国通快递';
EJS.ExpressCompanySet.lianbangkuaidi = '联邦快递';
EJS.ExpressCompanySet.quanfengkuaidi = '全峰快递';
EJS.ExpressCompanySet.suer = '速尔';
EJS.ExpressCompanySet.tiantian = '天天';
EJS.ExpressCompanySet.youshuwuliu = '优速物流';

EJS.companyData = [];
for (var p in EJS.ExpressCompanySet) {
    EJS.companyData.push([p, EJS.ExpressCompanySet[p]]);
}


EJS.companyRender = function (v) {
    return  EJS.ExpressCompanySet[v];
};

