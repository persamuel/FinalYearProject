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

compile_and_test_stdout 1 "addtest.rope" "" "13"
compile_and_test_stdout 1 "arrayreassign.rope" "Hello" "HelloWorld!"
compile_and_test_stdout 1 "arrayreassign2.rope" "" "Hello!"
compile_and_test_stdout 1 "arrayreassign3.rope" "" "1,2,3,4,5"
compile_and_test_stdout 1 "arraytest.rope" "" "12345678910"
compile_and_test_stdout 1 "binaryadd.rope" "101 010" "7"
compile_and_test_stdout 1 "bubblesort.rope" "" "120,240,360,480,600,720"
compile_and_test_stdout 1 "castingtest.rope" "" "65,B,67,D"
compile_and_test_stdout 1 "comparetest.rope" "" "True,False,True,False,True,False,True,False,True,False,True,False,True,False"
compile_and_test_stdout 1 "comparetest2.rope" "" "True,False,True,False,True,False,True,False,True,False,True,False,True,False"
compile_and_test_stdout 1 "concat.rope" "hello world" "helloworld"
compile_and_test_stdout 1 "echotest.rope" "hello world !" "helloworld!"
compile_and_test_stdout 1 "factorial.rope" "" "120"
compile_and_test_stdout 1 "fibonacci.rope" "" "34"
compile_and_test_stdout 1 "fizzbuzz.rope" "" "0:fb 1: 2: 3:f 4: 5:b 6:f 7: 8: 9:f 10:b 11: 12:f 13: 14: 15:fb 16: 17: 18:f 19: 20:b 21:f 22: 23: 24:f 25:b 26: 27:f 28: 29: 30:fb "
compile_and_test_stdout 1 "helloworld.rope" "" "Hello World!"
compile_and_test_stdout 1 "iftest.rope" "" "Y"
compile_and_test_stdout 1 "iftest2.rope" "" "N"
compile_and_test_stdout 1 "logictest.rope" "" "True,False,False,False,True,True,True,False,False,True"
compile_and_test_stdout 1 "logictest2.rope" "" "True,False,False,False,True,True,True,False,False,True"
compile_and_test_stdout 1 "max.rope" "" "8,9"
compile_and_test_stdout 1 "multiplytest.rope" "" "40"
compile_and_test_stdout 1 "negativetest.rope" "" "-1"
compile_and_test_stdout 1 "negcomparetest.rope" "" "True,False,True,False,True,False,True,False,True,False,True,False,True,False"
compile_and_test_stdout 1 "palindrome.rope" "tacocat" "True"
compile_and_test_stdout 2 "palindrome.rope" "tacoca" "False"
compile_and_test_stdout 1 "power.rope" "" "9"
compile_and_test_stdout 1 "printarray.rope" "" "120,240,360,480,600"
compile_and_test_stdout 1 "printarrptr.rope" "" "(0x[0-9a-fA-F]+){2}"
compile_and_test_stdout 1 "product.rope" "" "560,3360"
compile_and_test_stdout 1 "quotient.rope" "" "6,1"
compile_and_test_stdout 1 "reverse.rope" "hello" "olleh"
compile_and_test_stdout 1 "stringequal.rope" "hello hello" "True"
compile_and_test_stdout 2 "stringequal.rope" "hello yello" "False"
compile_and_test_stdout 1 "stringlength.rope" "" "5,6"
compile_and_test_stdout 1 "stringtoint.rope" "34" "34"
compile_and_test_stdout 1 "subtest.rope" "" "3"
compile_and_test_stdout 1 "sum.rope" "" "15,21"
compile_and_test_stdout 1 "whiletest.rope" "" "0123456789"