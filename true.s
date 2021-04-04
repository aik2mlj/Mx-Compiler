	.text
	.file	"IRcout.ll"
	.globl	sometimes__make_money   # -- Begin function sometimes__make_money
	.p2align	2
	.type	sometimes__make_money,@function
sometimes__make_money:                  # @sometimes__make_money
	.cfi_startproc
# %bb.0:                                # %entry
	addi	sp, sp, -16
	.cfi_def_cfa_offset 16
	sw	a0, 12(sp)
	lw	a1, 0(a0)
	addi	a1, a1, 1
	sw	a1, 0(a0)
	j	.LBB0_1
.LBB0_1:                                # %return
	addi	sp, sp, 16
	ret
.Lfunc_end0:
	.size	sometimes__make_money, .Lfunc_end0-sometimes__make_money
	.cfi_endproc
                                        # -- End function
	.globl	main                    # -- Begin function main
	.p2align	2
	.type	main,@function
main:                                   # @main
	.cfi_startproc
# %bb.0:                                # %entry1
	addi	sp, sp, -16
	.cfi_def_cfa_offset 16
	mv	a0, zero
	sw	a0, 12(sp)
	j	.LBB1_1
.LBB1_1:                                # %return1
	lw	a0, 12(sp)
	addi	sp, sp, 16
	ret
.Lfunc_end1:
	.size	main, .Lfunc_end1-main
	.cfi_endproc
                                        # -- End function
	.section	".note.GNU-stack","",@progbits
