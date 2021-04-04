	.text

	.globl	main
	.p2align	2
	.type	main,@function
main:
.LBB0_0:                                             # entry
	mv	ra_save, ra
	sw	zero, 0(String.addr)
	sw	zero, 0(g.addr)
	sw	zero, 0(retval.addr)
	addi	const_int, zero, 8
	mv	a0, const_int
	call	malloc
	mv	malloc, a0
	mv	arraysizeptr, malloc
	addi	const_int_1, zero, 8
	sw	const_int_1, 0(arraysizeptr)
	addi	arrayptr, arraysizeptr, 4
	mv	arrayptr1, arrayptr
	sw	arrayptr1, 0(ff.addr)
	addi	const_int_2, zero, 20
	mv	a0, const_int_2
	call	malloc
	mv	malloc1, a0
	mv	arraysizeptr1, malloc1
	addi	const_int_3, zero, 20
	sw	const_int_3, 0(arraysizeptr1)
	addi	arrayptr2, arraysizeptr1, 4
	mv	arrayptr3, arrayptr2
	sw	arrayptr3, 0(fg.addr)
	lw	String, 0(String.addr)
	call	getInt
	mv	call, a0
	mv	a0, call
	call	toString
	mv	call1, a0
	sw	call1, 0(String.addr)
	lw	String1, 0(String.addr)
	mv	a0, String1
	call	_string_length
	mv	call2, a0
	mv	a0, call2
	call	toString
	mv	call3, a0
	mv	a0, call3
	call	println
	j	.LBB0_1
.LBB0_1:                                             # return
	lw	retval, 0(retval.addr)
	mv	a0, retval
	mv	ra, ra_save
	ret

	.globl	A__aabc
	.p2align	2
	.type	A__aabc,@function
A__aabc:
.LBB1_0:                                             # entry1
	mv	ra_save, ra
	mv	this, a0
	mv	abc, a1
	sw	zero, 0(retval.addr1)
	sw	this, 0(this.addr)
	sw	abc, 0(abc.addr)
	lw	abc1, 0(abc.addr)
	sw	abc1, 0(retval.addr1)
	j	.LBB1_1
.LBB1_1:                                             # return1
	lw	retval1, 0(retval.addr1)
	mv	a0, retval1
	mv	ra, ra_save
	ret

	.globl	B__D
	.p2align	2
	.type	B__D,@function
B__D:
.LBB3_0:                                             # entry2
	mv	ra_save, ra
	mv	this1, a0
	sw	this1, 0(this.addr1)
	j	.LBB3_1
.LBB3_1:                                             # return2
	mv	ra, ra_save
	ret

	.globl	B__t
	.p2align	2
	.type	B__t,@function
B__t:
.LBB4_0:                                             # entry3
	mv	ra_save, ra
	mv	this2, a0
	sw	this2, 0(this.addr2)
	lw	this3, 0(this.addr2)
	mv	a0, this3
	call	B__D
	j	.LBB4_1
.LBB4_1:                                             # return3
	mv	ra, ra_save
	ret

	.globl	A__A
	.p2align	2
	.type	A__A,@function
A__A:
.LBB5_0:                                             # entry4
	mv	ra_save, ra
	mv	this4, a0
	sw	this4, 0(this.addr3)
	j	.LBB5_1
.LBB5_1:                                             # return4
	mv	ra, ra_save
	ret

	.globl	B__B
	.p2align	2
	.type	B__B,@function
B__B:
.LBB6_0:                                             # entry5
	mv	ra_save, ra
	mv	this5, a0
	sw	this5, 0(this.addr4)
	j	.LBB6_1
.LBB6_1:                                             # return5
	mv	ra, ra_save
	ret


	.section	.sdata,"aw",@progbits
