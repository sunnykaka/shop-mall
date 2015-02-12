(function () {
    var running = null;
    $(".grade_detail").each(function (i) {
        var url = EJS.GradeRule;
        if (i > 0) {
            url = EJS.GradeRule + "?rule=n";
        }
        $(this).on("mouseover", function () {
            var self = this;
            self.t = new Tips2({
                obj: $(this),
                content: "正在加载...",
                style: "grade_tips"
            });
            $.ajax({
                type: "POST",
                url: url,
                dataType: "html",
                success: function (data) {
                    self.t.changeContent(data);
                },
                error: function () {
                    self.t.changeContent("error");
                }
            });
        })
            .on("mouseout", function () {
                var self = this;
                if (running) {
                    clearTimeout(running);
                }
                running = setTimeout(function () {
                    $(document).on("mousemove click", function (e) {
                        if ($(e.target).parents(".grade_tips").length < 1 && !$(e.target).hasClass(".grade_detail")) {
                            self.t.remove();
                            $(document).off("mousemove click");
                        }
                    });
                }, 800);
            });
    });

}());