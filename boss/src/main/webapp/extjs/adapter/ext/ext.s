#程序由表达式组成，表达式可能是简单的值，也可能是值的运算，运算再返回值，有些运算不返回值也要返回nil
#表达式就是括号,多个表达式组成功能块，那么返回的值就是最后一个表达式的值
()
()
()

#算术运算,运算符号只是值的方法
(1) .+ (2) ./ (3) .% (4) .* (5) .- (6) .^ (2)

#列表相加
(1,2,3) .+ (4,5,6)

#函数定义和执行，内部用that引用函数本身,$1指向第一个参数,$2指向第二个参数，依次类推
(||=>()()()).closure()

#快速排序
(|a| => (that(a.select($1<a.at(0))) + a.select($1==a.at(0)) + that(a.select($1>a.at(0))))).closure((1,2,3,4,5))

#赋值和并行赋值 ，考虑是否用let，或者是否有赋值
(window.undefined = window.undefined)
(a,b,c = 1,2,3)

#分支，没有了if else,用cond可以做n路分支
(cond (true?())
      (true?())
      (true?())
      (go())
)

#循环可以表达得很抽象，比如for food in foods when food isnt 'chocolate' ，也可以表达得很机械，比如while,until,loop
(for p in (1,2,3))
(for k,v of (a=>1,b=>2))

#字典对象
Ext = (
        version => "3.4.0",
        detail => (major=>3,minor=>4,patch=>0),
        apply => (|o, c, defaults| => (that(o,defaults) if(defaults))(o.p = c.p for p of c if(o and c and c.object?))(o))
)

#匿名函数指定运行目标和传递闭包参数，如果不指定target就默认是全局
(|| =>
    (ua = navigator.userAgent.toLowerCase())
    (check = (|r|=> (r.test(ua)))
    (DOC.exeCommand("BackgroundImageCache")if(isIE6))
    (Ext.apply(Ext,(
                    isReady=>false,enableForcedBoxModel=>false,enableGarbageCollector=>true,
                    applyIf=>(|o,c|=> (o.p = c.p if(o.has(p)) for p of c if(o)) (o))
                    ))
    )

).target(window).closure()

