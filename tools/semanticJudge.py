#!python3

import os, time


"""
    Modify following configurations to adapt to your environment.
"""
test_cases_dir = '../testcases/sema/'
# test_cases_dir = '../testcases/codegen/'
# test_cases_dir = '../testcases/optim/'
compile_cmd = "bash ../build.bash"
execute_cmd = "bash ../semantic.bash"
excluded_test_cases = ["foo.mx"]
ravel_path = "ravel --enable-cache"
builtin_path = "./builtin/builtin.s"
halt_on_3_fails = True
calculate_score = False
test_codegen = False
# When test_codegen && use_llvm is true, the output should be a .ll file, and we will use llc to
# compile it into asm. You can test the correctness of your IR-gen with this.
use_llvm = False
llc_cmd = 'llc-10'



color_red = "\033[0;31m"
color_green = "\033[0;32m"
color_none = "\033[0m"

def collect_test_cases():
    test_cases = []
    # for f in os.listdir(test_cases_dir):
    #     if os.path.splitext(f)[1] == '.mx':
    #         test_cases.append(f)
    # for s in excluded_test_cases:
    #     if s in test_cases: test_cases.remove(s)
    with open(test_cases_dir + "judgelist.txt") as f:
        test_cases = f.read().split('\n')
    test_cases.sort()
    test_cases.remove('')
    print(test_cases)
    return test_cases


def parse_test_case(test_case_path):
    with open(test_case_path, 'r') as f:
        lines = f.read().split('\n')
    src_start_idx = lines.index('*/', lines.index('/*')) + 1
    src_text = '\n'.join(lines[src_start_idx:])
    try:
        verdict_place = lines.index("Verdict: Success", lines.index('/*'));
    except Exception: verdict = 0;
    else: verdict = 1;

    return src_text, verdict

if calculate_score:
    import pandas as pd
    import numpy as np
    df = pd.read_csv('./optim.csv', index_col=['name'], thousands=',')

def main():
    if os.system(compile_cmd):
        print(color_red + "Fail when building your compiler...")
        return
    test_cases = collect_test_cases()
    os.system('cp %s ./builtin.s' % builtin_path)
    total = 0
    passed = 0
    continue_fail = 0
    score = []
    max_len = max(len(i) for i in test_cases)
    for t in test_cases:
        if halt_on_3_fails and (continue_fail > 2):
            exit(1)
        total += 1
        src_text, verdict = parse_test_case(test_cases_dir + t)
        case_name = t[:-3]
        with open('test.mx', 'w') as f:
            f.write(src_text)

        print(t + ':', end='')
        for i in range(len(t), max_len):
            print(end = ' ')
        print("verdicted: ", verdict, end=' ')
        if os.system('%s < ./test.mx > test.s' % execute_cmd):
            if verdict == 1:
                print(color_red + "Wrong!" + color_none);
                continue_fail += 1
                exit(1)
        else:
            if verdict == 0:
                print(color_red + "Wrong!" + color_none);
                continue_fail += 1
                exit(1)
        passed += 1
        continue_fail = 0

        print(color_green + "Accepted" + color_none)

    print("total {}, passed {}, ratio {}".format(total, passed, passed / total))
    if calculate_score:
        score = np.sort(np.array(score))
        print(np.mean(score[1:-1]) * 10)

if __name__ == '__main__':
    main()
