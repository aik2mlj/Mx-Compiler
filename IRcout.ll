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
    i32 %call = call i32 @getInt()
    store i32 %call, i32* @p.addr
    i32 %k = load i32, i32* @k.addr
    i32 %call = call i32 @getInt()
    store i32 %call, i32* @k.addr
    i32 %p = load i32, i32* @p.addr
    i32 %k = load i32, i32* @k.addr
    i32 %sub = sub i32 %p i32 %k
    i1 %gt = icmp sgt i32 %sub, i32 1
    br i1 %gt, label %if.then, label %if.end

if.then:                                    ; preds = %entry
    i8* %string = getelementptr [3 x i8], [3 x i8]* @str.addr, i32 0, i32 0
    call void @print(i8* %string)
    br label %if.end

if.end:                                    ; preds = %entry, %if.then
    i32 %i = load i32, i32* @i.addr
    i32 %p = load i32, i32* @p.addr
    i32 %k = load i32, i32* @k.addr
    i32 %sub = sub i32 %p i32 %k
    store i32 %sub, i32* @i.addr
    br label %for.cond

for.cond:                                    ; preds = %if.end, %for.inc
    i32 %i = load i32, i32* @i.addr
    i32 %p = load i32, i32* @p.addr
    i32 %k = load i32, i32* @k.addr
    i32 %add = add i32 %p i32 %k
    i1 %le = icmp sle i32 %i, i32 %add
    br i1 %le, label %for.body, label %for.end

for.body:                                    ; preds = %for.cond
    i32 %i = load i32, i32* @i.addr
    i1 %le = icmp sle i32 1, i32 %i
    br i1 %le, label %land.rhs, label %land.end

land.rhs:                                    ; preds = %for.body
    i32 %i = load i32, i32* @i.addr
    i32 %n = load i32, i32* @n.addr
    i1 %le = icmp sle i32 %i, i32 %n
    br label %land.end

land.end:                                    ; preds = %for.body, %land.rhs
    i1 %land = phi [ i1 false, %for.body ], [ i1 %le, %land.rhs ]
    br i1 %land, label %if.then, label %if.end

if.then:                                    ; preds = %land.end
    i32 %i = load i32, i32* @i.addr
    i32 %p = load i32, i32* @p.addr
    i1 %eq = icmp eq i32 %i, i32 %p
    br i1 %eq, label %if.then, label %if.else

if.end:                                    ; preds = %land.end, %if.end
    br label %for.inc

if.then:                                    ; preds = %if.then
    i8* %string = getelementptr [1 x i8], [1 x i8]* @str.addr, i32 0, i32 0
    call void @print(i8* %string)
    i8* %string = getelementptr [2 x i8], [2 x i8]* @str.addr, i32 0, i32 0
    call void @print(i8* %string)
    br label %if.end

if.end:                                    ; preds = %if.then, %if.else
    br label %if.end

if.else:                                    ; preds = %if.then
    i32 %i = load i32, i32* @i.addr
    call void @printInt(i32 %i)
    i8* %string = getelementptr [1 x i8], [1 x i8]* @str.addr, i32 0, i32 0
    call void @print(i8* %string)
    br label %if.end

for.inc:                                    ; preds = %if.end
    i32 %i = load i32, i32* @i.addr
    i32 %inc = add i32 %i i32 1
    store i32 %inc, i32* @i.addr
    br label %for.cond

for.end:                                    ; preds = %for.cond
    i32 %p = load i32, i32* @p.addr
    i32 %k = load i32, i32* @k.addr
    i32 %add = add i32 %p i32 %k
    i32 %n = load i32, i32* @n.addr
    i1 %lt = icmp slt i32 %add, i32 %n
    br i1 %lt, label %if.then, label %if.end

if.then:                                    ; preds = %for.end
    i8* %string = getelementptr [3 x i8], [3 x i8]* @str.addr, i32 0, i32 0
    call void @print(i8* %string)
    br label %if.end

if.end:                                    ; preds = %for.end, %if.then
    store i32 0, i32* %retval.addr
    br label %return

return:                                    ; preds = %if.end
    i32 %retval = load i32, i32* %retval.addr
    ret i32 %retval
}

define void @__init__() {
entry:
    i32 %call_main = call i32 @main()
    br label %return

return:                                    ; preds = %entry
    ret void
}

