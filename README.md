[TOC]

# 总 

本博文是2019年北航面向对象（OO）课程第三单元作业（规格化设计）的总结。三次作业的要求大致如下：

- 第一次作业：实现一个路径管理系统，可以通过各类输入指令来进行数据的增删查改等交互。
- 第二次作业：实现一个无向图系统，可以进行基于无向图的一些查询操作。
- 第三次作业：实现一个简单地铁系统，可以进行一些基本的查询操作

源代码及项目要求均已发布到 [github](<https://github.com/LutingWang/Railway_System> "Luting") ，读者可以下载检查。以下将对这一单元作业进行简单总结。

# JML规格化设计

Java建模语言（JML）是一种行为接口规范语言，可用于指定`Java`模块的行为。它结合了`契约方法设计`和`Larch系列接口规范语言`的基于模型的规范方法，以及`细化演算`的一些理论。

## 理论基础

JML是一种行为界面规范语言（BISL）。在这种被称为面向模型的编程风格中，我们需要指定方法或抽象数据类型的接口及其行为。

`方法`或`类`的`接口`是从程序的其他部分使用它所需的信息。对于`JML`，这是调用方法或使用属性或类所需的Java语法和类型信息。方法接口包括诸如方法的名称、修饰符、参数数量、返回类型、可能抛出的异常等等；属性接口包括名称和类型及其修饰符；类接口包括名称、修饰符、包、超类，以及它声明和继承的属性和方法的接口。JML使用Java的语法指定所有这些接口信息。

`方法`或`类`的`行为`描述了一组能够执行的状态转换：定义调用方法的一组状态，允许方法可以赋值的一组属性，以及调用状态和返回状态之间的关系。方法的状态由逻辑断言描述，称为方法的`前置条件`。这些状态与正常返回可能导致的状态之间允许的关系由另一个称为方法`正常后置条件`的逻辑断言描述。类似地，这些前置状态与抛出异常可能导致的状态之间的关系由方法的`异常后置条件`描述。

## 工具链

本单元中我们使用到的工具主要有`OpenJML`和`JMLUnitNG`

- `OpenJML` 可以对`JML`注释进行静态或动态检查，验证程序是否符合规格
- `JMLUnitNG` 可以自动生成测试样例

# 规格验证

## 验证代码

由于项目代码过于庞大，无法使用工具对其进行验证。因此我将代码的部分内容抽离，在新的子项目中进行测试。子项目的文件树如下

```
D:.
│  jmlunitng.jar
│
├─model
│      Path.java
│      PathContainer.java
│
└─unit
```

其中，`unit`是生成的`.class`文件路径。`java`源文件的内容如下

```java
// .\model\Path.java
package model;

public class Path {
    final private /*@ spec_public @*/int[] nodes;
    
    public Path() {
        this(1, 2, 3, 4, 5);
    }
    
    public Path(int... nodes) {
        this.nodes = nodes;
    }
    
    public int[] getNodes() {
        return nodes;
    }
    
    public int size() {
        return nodes.length;
    }
    
    @Override
    public String toString() {
        String result = "[";
        for (int i = 0; i < size(); i++) {
            result += nodes[i];
            result += ", ";
        }
        result = result.substring(0, result.length() - 2) + "]";
        return result;
    }
}
```

```java
// .\model\PathContainer.java
package model;

public class PathContainer {
    private  /*@ spec_public @*/ Path[] pList;
    private  /*@ spec_public @*/ int[] pidList;
    private int size = 0;

    public PathContainer() {
        pList = new Path[10];
        pidList = new int[10];
        addPath(new Path(1, 2, 3));
        addPath(new Path());
        addPath(new Path(4, 1, 2, 3));
    }

    public /*@pure@*/ int size() {
        return size;
    }
    
    /*@
      @ ensures path == null ==> \result == 0;
      @ ensures path != null ==> \result == size();
      @*/
    public int addPath(Path path) {
        pList[size] = path;
        pidList[size] = size + 1;
        size++;
        return size;
    }
    
    /*@
      @ ensures \result == pathId <= size() && pathId >= 1;
      @*/
    public boolean containsPathId(int pathId) {
        for (int i = 0; i < size; i++) {
            if (pidList[i] == pathId) {
                return true;
            }
        }
        return false;
    }
}
```

## 代码静态检查

有关`openjml`批处理脚本的内容可以查看[OpenJML入门](https://www.cnblogs.com/lutingwang/p/openjml_basic.html)，这里直接使用命令进行代码静态检查。

```
D:\>openjml -esc -prove model\*.java
model\Path.java:26: 警告: The prover cannot establish an assertion (PossiblyNegativeIndex) in method toString
            result += nodes[i];
                           ^
model\Path.java:26: 警告: The prover cannot establish an assertion (PossiblyTooLargeIndex) in method toString
            result += nodes[i];
                           ^
model\Path.java:29: 警告: The prover cannot establish an assertion (ExceptionalPostcondition: C:\Users\ThinkPad\Documents\Java\openjml-0.8.42-20190401\openjml.jar(specs/java/lang/Object.jml):194: 注: ) in method toString
        result = result.substring(0, result.length() - 2) + "]";
                                 ^
C:\Users\ThinkPad\Documents\Java\openjml-0.8.42-20190401\openjml.jar(specs/java/lang/Object.jml):194: 警告: Associated declaration: model\Path.java:29: 注:
    /*-RAC@   public normal_behavior // FIXME - do we want this to be normal_behavior?
                     ^
model\PathContainer.java:24: 警告: The prover cannot establish an assertion (PossiblyNegativeIndex) in method addPath
        pList[size] = path;
             ^
model\PathContainer.java:24: 警告: The prover cannot establish an assertion (PossiblyTooLargeIndex) in method addPath
        pList[size] = path;
             ^
model\PathContainer.java:24: 警告: The prover cannot establish an assertion (PossiblyBadArrayAssignment) in method addPath
        pList[size] = path;
                    ^
model\PathContainer.java:25: 警告: The prover cannot establish an assertion (PossiblyTooLargeIndex) in method addPath
        pidList[size] = size + 1;
               ^
model\PathContainer.java:39: 警告: The prover cannot establish an assertion (Postcondition: model\PathContainer.java:31: 注: ) in method containsPathId
        return false;
        ^
model\PathContainer.java:31: 警告: Associated declaration: model\PathContainer.java:39: 注:
      @ ensures \result == (pathId <= size() && pathId >= 1);
        ^
model\PathContainer.java:35: 警告: The prover cannot establish an assertion (PossiblyNegativeIndex) in method containsPathId
            if (pidList[i] == pathId) {
                       ^
model\PathContainer.java:35: 警告: The prover cannot establish an assertion (PossiblyTooLargeIndex) in method containsPathId
            if (pidList[i] == pathId) {
                       ^
12 个警告
```

可以看到，静态检查的警告主要是针对数组下标越界这一问题。但查看源码发现这个问题实际上并不会发生，因为`for`循环中已经限制了下标大小，因此输出信息也只是警告而已。下面我们来更改一下源码，将`PathContainer`的第 $22$ 行改为

```java
@ ensures path == null => \result == 0;
```

这时再次运行会发现

```
model\PathContainer.java:21: 错误: 非法的表达式开始
      @ ensures path == null => \result == 0;
                              ^
model\PathContainer.java:21: 错误: 意外的类型
      @ ensures path == null => \result == 0;
                     ^
  需要: 变量
  找到:    值
model\PathContainer.java:21: 错误: Assignments are not allowed where pure expressions are expected
      @ ensures path == null => \result == 0;
                             ^
3 个错误
```

原本的警告变成了错误，说明静态检查检查出了错误。事实上，静态检查不仅可以检查语法，还可以检查算术溢出等，这里不再赘述。

## 自动生成测试样例

### 生成结果

生成测试样例的脚本如下

```batch
::jmlunitng.bat
java -jar jmlunitng.jar -d unit model\*.java
copy model\*.java unit\model\
javac -cp jmlunitng.jar;unit\; unit\model\*.java
call openjml -rac -d unit model\*.java
java -cp jmlunitng.jar;unit\; model.Path_JML_Test
java -cp jmlunitng.jar;unit\; model.PathContainer_JML_Test
cmd.exe
```

输出如下

```
[TestNG] Running:
  Command line suite

Passed: racEnabled()
Passed: constructor Path()
Failed: constructor Path(null)
Passed: <<[1, 2, 3, 4, 5]>>.getNodes()
Passed: <<[1, 2, 3, 4, 5]>>.size()
Passed: <<[1, 2, 3, 4, 5]>>.toString()

===============================================
Command line suite
Total tests run: 6, Failures: 1, Skips: 0
===============================================

[TestNG] Running:
  Command line suite

Passed: racEnabled()
Passed: constructor PathContainer()
Passed: <<model.PathContainer@68fb2c38>>.addPath(null)
Passed: <<model.PathContainer@567d299b>>.addPath([1, 2, 3, 4, 5])
Failed: <<model.PathContainer@6c629d6e>>.containsPathId(-2147483648)
Failed: <<model.PathContainer@5ecddf8f>>.containsPathId(0)
Passed: <<model.PathContainer@3f102e87>>.containsPathId(2147483647)
Passed: <<model.PathContainer@27abe2cd>>.size()

===============================================
Command line suite
Total tests run: 8, Failures: 2, Skips: 0
===============================================
```

可以看出，`JMLUnitNG`检查的方式是将边界数据带入，而且不管方法有没有规格描述。

### 错误分析

#### 空指针异常

对于引用类型参数，工具会自动传入`null`以测试鲁棒性。在`Path::new`中，我没有考虑到传入参数为`null`的情况。这时如果调用`size`方法就会引发空指针异常。

#### JML描述错误

下方`PathContainer`的两个错误是由于`JML`规格写错造成的。我以为以下两条语句是等价的，实则不然。

```java
/*@ ensures \result == pathId <= size && pathId >= 1; @*/
/*@ ensures \result == (pathId <= size && pathId >= 1); @*/
```

将规格描述修改后，自动生成测试样例的检测也就通过了。

# 作业设计

## 第九次作业

### 架构
![](https://img2018.cnblogs.com/blog/1615581/201905/1615581-20190522203325079-718789101.png)

![hw1](https://img2018.cnblogs.com/blog/1615581/201905/1615581-20190522203215184-643568964.png)

核心架构中，只涉及两个接口和其实现类，符合面向对象的里氏替换原则。

### 代码实现

由于是本单元的第一次作业，代码实现也比较简单，严格按照规格编程即可。主要难点在于`distinct_node_count`方法的复杂度如何降低，这里我使用了函数式编程。因此本次作业中没有出现Bug。

## 第十次作业

### 架构

![hw2](https://img2018.cnblogs.com/blog/1615581/201905/1615581-20190522203251413-119651452.png)

可以看出，第二次作业比第一次复杂许多。这主要是因为增加了一组接口`Graph`以及自定义数据结构`datastructure.Graph`。

本次作业的架构是上次作业的直接拓展，并没有更改上次作业的逻辑。因为`Graph`接口本身继承自`PathContainer`接口，所以实现类`MyGraph`也继承自`MyPathContainer`，以提高代码复用性。此外，为了将业务逻辑和底层实现分离，我将图结构抽象出来作为数据结构类，为`MyGraph`提供支持。这样一来，`MyGraph`的工作量就大大降低了，便于维护和测试。

### 代码实现

由于`MyGraph`继承了`MyPathContainer`，因此很多查询指令不需要重复实现。但对于图变更指令，由于其会影响到`MyGraph`类中的属性，因此我对其进行了重载。下述的两个Bug都是因为重载时没有考虑到所有情况而产生的。

#### addPath

最初，我的`addPath`方法如下

```java
@Override
public int addPath(Path path) {
    int result = super.addPath(path);
    if (result == 0) {
        return result;
    }
    // operations
    return result;
}
```

在这个函数中我只考虑到了`path`不合法的情况，却没有考虑到如果`path`事先存在，则`super.addPath`不会执行这个问题。而`operations`部分的代码，只要看到`path`合法就一定会执行，因此造成了`super`和`this`不匹配的问题。

为了解决这个问题，我在函数开头加入了判断

```java
@Override
public int addPath(Path path) {
    try {
        return super.getPathId(path);
    } catch (PathNotFoundException e) { /* nothing */ }
    int result = super.addPath(path);
    // ...
    return result;
}
```

这样就可以排除`path`事先存在这一影响因素了。

#### removePath

在进行了对于`MyPathContainer`的删除操作后，`MyGraph`需要对其内部元素`graph`同样执行删除操作。一开始我的代码如下

```java
int firstNode = 0;
int secondNode = path.getNode(0);
for (int i = 1; i < path.size(); i++) {
    firstNode = secondNode;
    secondNode = path.getNode(i);
    graph.removeEdge(firstNode, secondNode);
    graph.removeIfIsolated(firstNode);
}
graph.removeIfIsolated(secondNode);
```

乍看上去，似乎没什么问题。我们遍历了路径上的每条边和每个顶点，依次将其删除。然而问题出现在最后一行。

如果一条路径的最后两个顶点相同，而且是这个图中唯一的一对顶点。那么在循环时，首先会经过这个点一次。这时，由于最后一条边已经被删除了，所以这个顶点是孤立点，也被删除。这时跳出循环的话，会在执行一次删除操作，引发空指针异常。为了解决这个问题，我特判了这种情况

```java
if (firstNode != secondNode) { graph.removeIfIsolated(secondNode); }
```

## 第十一次作业

### 架构

![hw3](https://img2018.cnblogs.com/blog/1615581/201905/1615581-20190522203339118-1567926685.png)

本次作业中我对代码进行了重构。按照接口的继承关系，我本来应该让实现类`MyRailwaySystem`继承`MyGraph`的。但是由于本次作业的数据基本限制和上次作业相比有很大变化，因此我重构了`datastructure.Graph`类，使其能处理泛型数据。这样一来，原本的`MyGraph`类也需要被重构。但是因为`MyGraph`本就不需要在代码中出现，因此我将其功能全部转移到了`MyRailwaySystem`中进行了重构。

### 代码实现

本次作业中没有出现Bug。原本担心的`TLE`也并没有出现，可能是因为静态数组的效率之高弥补了没有`cache`的缺陷。

# 心得体会

很早就听说程序的正确性可以用形式化证明来保证，但是以前一直没有使用过相关工具。总的来说本单元作业让我体会到了规格化设计的神奇之处。`OpenJML`这样的工具虽然还不完善，但已经可以帮我们检查一些隐蔽的错误了。而在规格描述方面，这样的语言也提供了标准的描述方法，避免了自然语言的二义性。可以说是很长见识了。

但是在本单元我认为还存在一些问题。首先，我原先以为本单元作业是有关设计模式的，没想到是用规格描述来代替指导书……其次，规格设计应该是抽象的，提纲挈领的描述函数调用接口，这样才能方便人们进行验证。但事实是规格往往比代码的内容要多，这一方面造成了规格设计和程序设计双方交流所需的时间，另一方面完全没有给形式化验证带来任何便利。因为工具并不能完全自动的验证程序正确性，在关键地方还是需要人来进行逻辑推理。这时如果人面对着比代码还要多的规格描述，我觉得人可能会选择直接检查代码。

因此我认为，本单元作业确实教会了我关于规格化设计的基础知识，但我在未来的工作中不会选择规格设计。

# 参考

[Java Modelling Language](http://www.eecs.ucf.edu/~leavens/JML/index.shtml)