i32* @i.addr = global i32 0
i32* @k.addr = global i32 0
i32* @n.addr = global i32 0
i32* @p.addr = global i32 0
[3 x i8]* @str.addr = global c">> "

define i32 @main() {
entry:
    i32* %retval.addr = alloca i32
    store i32 0, i32* %retval.addr
    i32 %n = load i32, i32* @n.addr
    i32 %call = call i32 @getInt()
    store i32 %call, i32* @n.addr
    i32 %p = load i32, i32* @p.addr
    i32 %call1 = call i32 @getInt()
    store i32 %call1, i32* @p.addr
    i32 %k = load i32, i32* @k.addr
    i32 %call2 = call i32 @getInt()
    store i32 %call2, i32* @k.addr
    i32 %p1 = load i32, i32* @p.addr
    i32 %k1 = load i32, i32* @k.addr
    i32 %sub = sub i32 %p1 i32 %k1
    i1 %gt = icmp sgt i32 %sub, i32 1
    br i1 %gt, label %if.then, label %if.end

if.then:                                    ; preds = %entry
    i8* %string = getelementptr [3 x i8], [3 x i8]* @str.addr, i32 0, i32 0
    call void @print(i8* %string)
    br label %if.end

if.end:                                    ; preds = %entry, %if.then
    i32 %i = load i32, i32* @i.addr
    i32 %p2 = load i32, i32* @p.addr
    i32 %k2 = load i32, i32* @k.addr
    i32 %sub1 = sub i32 %p2 i32 %k2
    store i32 %sub1, i32* @i.addr
    br label %for.cond

for.cond:                                    ; preds = %if.end, %for.inc
    i32 %i1 = load i32, i32* @i.addr
    i32 %p3 = load i32, i32* @p.addr
    i32 %k3 = load i32, i32* @k.addr
    i32 %add = add i32 %p3 i32 %k3
    i1 %le = icmp sle i32 %i1, i32 %add
    br i1 %le, label %for.body, label %for.end

for.body:                                    ; preds = %for.cond
    i32 %i2 = load i32, i32* @i.addr
    i1 %le1 = icmp sle i32 1, i32 %i2
    br i1 %le1, label %land.rhs, label %land.end

land.rhs:                                    ; preds = %for.body
    i32 %i3 = load i32, i32* @i.addr
    i32 %n1 = load i32, i32* @n.addr
    i1 %le2 = icmp sle i32 %i3, i32 %n1
    br label %land.end

land.end:                                    ; preds = %for.body, %land.rhs
    i1 %land = phi [ i1 false, %for.body ], [ i1 %le2, %land.rhs ]
    br i1 %land, label %if.then, label %if.end

if.then1:                                    ; preds = %land.end
    i32 %i4 = load i32, i32* @i.addr
    i32 %p4 = load i32, i32* @p.addr
    i1 %eq = icmp eq i32 %i4, i32 %p4
    br i1 %eq, label %if.then, label %if.else

if.end1:                                    ; preds = %land.end, %if.end
    br label %for.inc

if.then2:                                    ; preds = %if.then1
    i8* %string1 = getelementptr [1 x i8], [1 x i8]* @str.addr, i32 0, i32 0
    call void @print(i8* %string1)
    i8* %string2 = getelementptr [2 x i8], [2 x i8]* @str.addr, i32 0, i32 0
    call void @print(i8* %string2)
    br label %if.end

if.end2:                                    ; preds = %if.then2, %if.else
    br label %if.end1

if.else:                                    ; preds = %if.then1
    i32 %i5 = load i32, i32* @i.addr
    call void @printInt(i32 %i5)
    i8* %string3 = getelementptr [1 x i8], [1 x i8]* @str.addr, i32 0, i32 0
    call void @print(i8* %string3)
    br label %if.end2

for.inc:                                    ; preds = %if.end1
    i32 %i6 = load i32, i32* @i.addr
    i32 %inc = add i32 %i6 i32 1
    store i32 %inc, i32* @i.addr
    br label %for.cond

for.end:                                    ; preds = %for.cond
    i32 %p5 = load i32, i32* @p.addr
    i32 %k4 = load i32, i32* @k.addr
    i32 %add1 = add i32 %p5 i32 %k4
    i32 %n2 = load i32, i32* @n.addr
    i1 %lt = icmp slt i32 %add1, i32 %n2
    br i1 %lt, label %if.then, label %if.end

if.then3:                                    ; preds = %for.end
    i8* %string4 = getelementptr [3 x i8], [3 x i8]* @str.addr, i32 0, i32 0
    call void @print(i8* %string4)
    br label %if.end

if.end3:                                    ; preds = %for.end, %if.then3
    store i32 0, i32* %retval.addr
    br label %return

return:                                    ; preds = %if.end3
    i32 %retval = load i32, i32* %retval.addr
    ret i32 %retval
}

define void @__init__() {
entry1:
    i32 %call_main = call i32 @main()
    br label %return

return1:                                    ; preds = %entry1
    ret void
}

