#!/bin/bash

# Testing script for my compiler

COMPILER_JAR="./FinalYearProject.jar"

# Inputs:
# $1 - test number
# $2 - src code filename
# $3 - command line args
# $4 - expected output
compile_and_test_stdout () {
	testnumber=$1
	filename=$2
	stripped="${filename%%.*}"
	args=$3
	expectedpat=$4

	java -jar $COMPILER_JAR $filename -o "${stripped}.s"
	as "${stripped}.s" -o "${stripped}.o"
	ld -dynamic-linker "/lib/ld-linux.so.2" -o $stripped "${stripped}.o" -lc

	out="$(./$stripped $args)"

	if [[ $out =~ $expectedpat ]]; then
		echo "PASSED $stripped test number: $testnumber"
	else
		echo "FAILED $stripped test number: $testnumber"
		echo "Expected - $expected"
		echo "Actual   - $out"
	fi
}

compile_and_test_stdout 1 "addtest.rop" "" "13"
compile_and_test_stdout 1 "arrayreassign.rop" "Hello" "HelloWorld!"
compile_and_test_stdout 1 "arrayreassign2.rop" "" "Hello!"
compile_and_test_stdout 1 "arrayreassign3.rop" "" "1,2,3,4,5"
compile_and_test_stdout 1 "arraytest.rop" "" "12345678910"
compile_and_test_stdout 1 "binaryadd.rop" "101 010" "7"
compile_and_test_stdout 1 "bubblesort.rop" "" "120,240,360,480,600,720"
compile_and_test_stdout 1 "castingtest.rop" "" "65,B,67,D"
compile_and_test_stdout 1 "comparetest.rop" "" "True,False,True,False,True,False,True,False,True,False,True,False,True,False"
compile_and_test_stdout 1 "comparetest2.rop" "" "True,False,True,False,True,False,True,False,True,False,True,False,True,False"
compile_and_test_stdout 1 "concat.rop" "hello world" "helloworld"
compile_and_test_stdout 1 "echotest.rop" "hello world !" "helloworld!"
compile_and_test_stdout 1 "factorial.rop" "" "120"
compile_and_test_stdout 1 "fibonacci.rop" "" "34"
compile_and_test_stdout 1 "fizzbuzz.rop" "" "0:fb 1: 2: 3:f 4: 5:b 6:f 7: 8: 9:f 10:b 11: 12:f 13: 14: 15:fb 16: 17: 18:f 19: 20:b 21:f 22: 23: 24:f 25:b 26: 27:f 28: 29: 30:fb "
compile_and_test_stdout 1 "helloworld.rop" "" "Hello World!"
compile_and_test_stdout 1 "iftest.rop" "" "Y"
compile_and_test_stdout 1 "iftest2.rop" "" "N"
compile_and_test_stdout 1 "logictest.rop" "" "True,False,False,False,True,True,True,False,False,True"
compile_and_test_stdout 1 "logictest2.rop" "" "True,False,False,False,True,True,True,False,False,True"
compile_and_test_stdout 1 "max.rop" "" "8,9"
compile_and_test_stdout 1 "multiplytest.rop" "" "40"
compile_and_test_stdout 1 "negativetest.rop" "" "-1"
compile_and_test_stdout 1 "negcomparetest.rop" "" "True,False,True,False,True,False,True,False,True,False,True,False,True,False"
compile_and_test_stdout 1 "palindrome.rop" "tacocat" "True"
compile_and_test_stdout 2 "palindrome.rop" "tacoca" "False"
compile_and_test_stdout 1 "power.rop" "" "9"
compile_and_test_stdout 1 "printarray.rop" "" "120,240,360,480,600"
compile_and_test_stdout 1 "printarrptr.rop" "" "(0x[0-9a-fA-F]+){2}"
compile_and_test_stdout 1 "product.rop" "" "560,3360"
compile_and_test_stdout 1 "quotient.rop" "" "6,1"
compile_and_test_stdout 1 "reverse.rop" "hello" "olleh"
compile_and_test_stdout 1 "stringequal.rop" "hello hello" "True"
compile_and_test_stdout 2 "stringequal.rop" "hello yello" "False"
compile_and_test_stdout 1 "stringlength.rop" "" "5,6"
compile_and_test_stdout 1 "stringtoint.rop" "34" "34"
compile_and_test_stdout 1 "subtest.rop" "" "3"
compile_and_test_stdout 1 "sum.rop" "" "15,21"
compile_and_test_stdout 1 "whiletest.rop" "" "0123456789"