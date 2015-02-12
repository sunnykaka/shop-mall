$(function(){
    $('.changeShow:eq(0)').show();
    $('.my_title ul li').click(function(){
        $('.my_title ul li').removeClass('curr');
        $(this).addClass('curr');
        var idx = $(this).index();
        $('.changeShow').hide();
        $('.changeShow').eq(idx).show();
    })
})