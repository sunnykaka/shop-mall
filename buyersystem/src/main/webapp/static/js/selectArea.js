/**
 * 省市县区域联动Select
 * User: Hualei Du
 * Date: 13-9-13
 * Time: 下午2:33
 */
$(function ($) {

    if (typeof $.fn.selectArea !== 'undefined') {
        return;
    }

    $.fn.selectArea = function (options) {

        var provinceUrl = EJS.HomeUrl + '/my/province',
            cityUrl = EJS.HomeUrl + '/my/city?code=',
            districtsUrl = EJS.HomeUrl + '/my/area?code=',
            opts = $.extend({
                province: null,
                city: null,
                districts: null
            }, options || {}),
            self = this,
            province = self.find('select[name=province]'),
            city = self.find('select[name=city]'),
            districts = self.find('select[name=districts]');

        function resetSelect(element) {
            element.empty();
            element.append('<option value="0">\u8bf7\u9009\u62e9</option>');
        }

        function addToSelect(element, data, selected, jsonNode) {
            var isSelected,
                arr = data.data[jsonNode],
                items = '',
                code = '';

            if (arr.length > 0) {
                $.each(arr, function (i) {
                    isSelected = '';
                    if (arr[i].name === selected) {
                        isSelected = " selected";
                    }

                    if (element === districts) {
                        code = ' data-code=' + arr[i].zipCode;
                    }
                    items += '<option value="' + arr[i].name + '" ' + 'data-id="' +
                        arr[i].id + '" ' + isSelected + code + '>' + arr[i].name + '</option>';
                });
                element.attr('disabled', false);
            } else {
                element.attr('disabled', true);
            }

            element.append(items);

        }

        function loadData(type, selected, callback) {
            var element,
                jsonNode,
                url,
                code;

            if (type === 'province') {
                element = province;
                url = provinceUrl;
                jsonNode = 'provinceList';
                resetSelect(element);
                resetSelect(city);
                resetSelect(districts);
            } else if (type === 'city') {
                element = city;
                code = self.find('select[name=province]').find("option:selected").attr('data-id');
                url = cityUrl + code;
                jsonNode = 'cityList';
                resetSelect(city);
                resetSelect(districts);
            } else if (type === 'districts') {
                element = districts;
                code = self.find('select[name=city]').find("option:selected").attr('data-id');
                url = districtsUrl + code;
                jsonNode = 'areaList';
                resetSelect(districts);
            }

            if ((type === 'city' || type === 'districts') && !code) {
                return;
            }

            $.ajax({
                type: "GET",
                url: url,
                dataType: "json",
                success: function (data) {
                    if (data.success) {
                        addToSelect(element, data, selected, jsonNode);
                        if (typeof callback === "function") {
                            callback();
                        }
                    }
                }
            });
        }

        // 初始化表单
        (function () {
            city.attr('disabled', true);
            districts.attr('disabled', true);
            loadData('province', opts.province, function () {
                loadData('city', opts.city, function () {
                    loadData('districts', opts.districts);
                });
            });
        }());

        // 绑定事件
        province.on('change', function () {
            loadData('city');
            districts.attr('disabled', true);
        });

        city.on('change', function () {
            loadData('districts');
        });

        districts.on('change', function () {
            var zipCode = self.parents('form').find('input[name=zipCode]'),
                value = $(this).val(),
                selected = $(this).find("option[value='" + value + "']"),
                code = selected.attr('data-code');
            if (value !== 0) {
                zipCode.val(code);
                districts.siblings("span").remove();
                zipCode.siblings("span").remove();
            }
        });

        return self;
    };

}(jQuery));

/**
 *  必须保证 选定的DOM树下包含 3个select元素，且 name = province 和 city 和 districts 才能正常执行
 *  selectArea 共有三个属性可设置
 *  province {String} 默认省
 *  city {String} 默认市
 *  districts {String} 默认区
 *  例子:
 *  $('#test').selectArea({
 *      province: '广东',
 *      city: '深圳市',
 *      districts: '宝安区'
 *  });
 */
