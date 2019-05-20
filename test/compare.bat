@echo off
:start
del result.txt
del test.txt
python gen_hw2.py
java -cp wlt;specs-homework-2-1.2-raw-jar-with-dependencies.jar; model.Main <test.txt >wlt\result.txt
copy wlt\result.txt result.txt

java -cp 1;specs-homework-2-1.2-raw-jar-with-dependencies.jar; Main <test.txt >1\result.txt
echo finished 1
java -cp 2;specs-homework-2-1.2-raw-jar-with-dependencies.jar; Main <test.txt >2\result.txt
echo finished 2
java -cp 3;specs-homework-2-1.2-raw-jar-with-dependencies.jar; Main <test.txt >3\result.txt
echo finished 3
java -cp 4;specs-homework-2-1.2-raw-jar-with-dependencies.jar; Main <test.txt >4\result.txt
echo finished 4
java -cp 6;specs-homework-2-1.2-raw-jar-with-dependencies.jar; Main <test.txt >6\result.txt
echo finished 6
java -cp 7;specs-homework-2-1.2-raw-jar-with-dependencies.jar; Main <test.txt >7\result.txt
echo finished 7
java -cp 8;specs-homework-2-1.2-raw-jar-with-dependencies.jar; graph.Main <test.txt >8\result.txt
echo finished 8

for /d %%i in (*) do ( 
	echo %%i 
	fc result.txt %%i\result.txt >nul&&echo same||(echo different!!!!!!!!!&&goto :end)
)
goto :start
:end