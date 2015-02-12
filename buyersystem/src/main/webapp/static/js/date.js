
// 当前日期
var dt = new Date();

// 设置 最小年份
DateSelector.prototype.MinYear = 1949;

// 设置 最大年份
DateSelector.prototype.MaxYear = dt.getFullYear();

/**
 * 年月日 三级联动
 * @param 年的 id
 * @param 月的 id
 * @param 日的 id
 */
function DateSelector(selYear, selMonth, selDay) {
    this.selYear = document.getElementById(selYear);
    this.selMonth = document.getElementById(selMonth);
    this.selDay = document.getElementById(selDay);
    this.selYear.Group = this;
    this.selMonth.Group = this;
    // 给年份、月份下拉菜单添加处理 onchange 事件的函数
    if (window.document.all != null) {
        // IE
        this.selYear.attachEvent("onchange", DateSelector.Onchange);
        this.selMonth.attachEvent("onchange", DateSelector.Onchange);
    } else {
        // FireFox
        this.selYear.addEventListener("change", DateSelector.Onchange, false);
        this.selMonth.addEventListener("change", DateSelector.Onchange, false);
    }
    if (arguments.length == 4)
        // 如果传入参数个数为4，最后一个参数必须为Date对象
        this.InitSelector(arguments[3].getFullYear(),
                arguments[3].getMonth() + 1, arguments[3].getDate());
    else if (arguments.length == 6)
        // 如果传入参数个数为6，最后三个参数必须为初始的年月日数值
        this.InitSelector(arguments[3], arguments[4], arguments[5]);
    else {
        // 默认使用当前日期
        this.InitSelector(dt.getFullYear(), dt.getMonth() + 1, dt.getDate());
    }
}

// 初始化年份
DateSelector.prototype.InitYearSelect = function() {
    // 循环添加 option 元素到年份 select 对象中
    for(var i = this.MaxYear; i >= this.MinYear; i--) {
        // 新建一个 option 对象
        var op = window.document.createElement("option");
        // 设置 option 对象的值
        op.value = i;
        // 设置 option 对象的内容
        op.innerHTML = i;
        // 添加到年份 select 对象
        this.selYear.appendChild(op);
    }
};
// 初始化月份
DateSelector.prototype.InitMonthSelect = function() {
    // 循环添加 option 元素到月份 select 对象中
    for(var i = 1; i < 13; i++) {
        // 新建一个 option对象
        var op = window.document.createElement("option");
        // 设置 option 对象的值
        op.value = i;
        // 设置 option 对象的内容
        op.innerHTML = i;
        // 添加到月份select对象
        this.selMonth.appendChild(op);
    }
};
// 根据年份与月份获取当月的天数
DateSelector.DaysInMonth = function(year, month) {
    var date = new Date(year, month, 0);
    return date.getDate();
};
// 初始化天数
DateSelector.prototype.InitDaySelect = function() {
    // 使用 parseInt 函数获取当前的年份和月份
    var year = parseInt(this.selYear.value);
    var month = parseInt(this.selMonth.value);
    // 获取当月的天数
    var daysInMonth = DateSelector.DaysInMonth(year, month);
    // 清空原有的选项
    this.selDay.options.length = 0;
    // 循环添加 option 元素到天数select对象中
    for(var i = 1; i <= daysInMonth; i++) {
        // 新建一个 option 对象
        var op = window.document.createElement("option");
        // 设置 option对象的值
        op.value = i;
        // 设置 option 对象的内容
        op.innerHTML = i;
        // 添加到天数 select 对象
        this.selDay.appendChild(op);
    }
};
// 处理年份和月份 onchange 事件的方法，它获取事件来源对象（即 selYear 或 selMonth）
// 并调用它的 Group 对象（即 DateSelector 实例，请见构造函数）
// 提供的 InitDaySelect 方法重新初始化天数
// 参数 e 为 event 对象
DateSelector.Onchange = function(e) {
    var selector = window.document.all != null ? e.srcElement : e.target;
    selector.Group.InitDaySelect();
};
// 根据参数初始化下拉菜单选项
DateSelector.prototype.InitSelector = function(year, month, day) {
    // 由于外部是可以调用这个方法，因此我们在这里也要将 selYear 和 selMonth 的选项清空掉
    // 另外因为 InitDaySelect 方法已经有清空天数下拉菜单，因此这里就不用重复工作了
    this.selYear.options.length = 0;
    this.selMonth.options.length = 0;
    // 初始化年、月
    this.InitYearSelect();
    this.InitMonthSelect();
    // 设置年、月初始值
    this.selYear.selectedIndex = this.MaxYear - year;
    this.selMonth.selectedIndex = month - 1;
    // 初始化天数
    this.InitDaySelect();
    // 设置天数初始值
    this.selDay.selectedIndex = day - 1;
};
