# Java

`abstract`: abstract class

`super`: 调用父类的构造函数

`interface`: 接口，需要实例化，使用`implements`

# Compiler

### AST

`ASTVisitor`用接口`interface`写

在`SemanticChecker`中实例化

AST简化了ParserTree的节点（去除括号等）

注意`Type`类和`TypeNode`类的区别：

- `TypeNode`：仅保存一个AST的Node节点信息（typeName等），在ASTBuild阶段得到
- `Type`：完整的一个类型信息，在SemanticCheck阶段得到

`isAssignable`：是否为左值，目前在ASTBuild阶段得到