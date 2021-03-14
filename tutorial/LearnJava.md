# Some Useful Java Properties

## Inheritance, Overriding, Polymorphism 

Inheritance and virtual method in C++ are like

```c++
class Shape { // base class
public:
    virtual std::string name() { // virtual method
        return "Shape";
    }
};
class Rectangle: public Shape { // derived class
public:
    int x, y;
    virtual std::string name() override { // override base virtual method
        return "Rectangle";
    }
};

int main() {
    Rectangle b;
    Shape *a = &b;
    std::cout << a->name() << std::endl; // "Rectangle"
}
```

We can rewrite this code in Java:

```java
// Shape.java
public class Shape { // base class
    public String name() {
        return "Shape";
    }
}
// Rectangle.java
public class Rectangle extends Shape { // derived class
    int x, y;
    @Override	// override base method
    public String name() {
        return "Rectangle";
    }
}
// Main.java
public class Main {
    public static void main(String[] args) {
        Rectangle b = new Rectangle(); // NOTICE THIS
        Shape a = b;
        System.out.println(a.name()); // "Rectangle"
    }
}
```

You should notice these things:

- **No ';'** after class definition in Java style.

- Every public class in Java should have the same name with its file.

- Every Java project should have a **`Main`** class, in which a specified `main` method is compulsory. (Every thing is CLASS in Java.)

- (**IMPORTANT**) In Java, if you want to declare an instance of **array** or some **class**, `new` key word is needed, which is different from C++ `new`. You don't need to worry about memory leak because Java has Garbage Collector, and there is **NO pointer**: it's all references.

- The key word of inheritance is `extends` in Java. One more thing: in Java, a derived class cannot have more than one parent class.

- To achieve polymorphism in Java, you don't need to specify that a method is virtual (In other words, all methods are virtual by default). If the derived class has a same method, it will override the base one. The `@Override` key word here is similar to `override` in C++: it reminds the compiler that this is a overriding method. If method name dismatches, compiler will throw errors.

  (What if you DO NOT want a method to be virtual? Add `final` key word to make the method a final one.)

### `super`

`super` key word can call parent methods in derived class:

```java
public class Rectangle extends Shape {
    int x, y;
    @Override
    public String name() {
        return super.name() + ": Rectangle"; // "Shape: Rectangle"
    }
}
```

### `this`

`this` key word: referring to the class instance itself. It's useful when parameters have the same name with class members (it's legal in Java):

```java
// Rectangle.java
public class Rectangle extends Shape {
    int x, y;
    Rectangle(int x, int y) {
        this.x = x; // use `this` to distinguish the member "x" with the parameter "x"
        this.y = y;
    }
}
```



## Abstract Class & Method

Same context, in C++:

```c++
class Shape {
public:
    virtual std::string name() { return "Shape"; }
    virtual int area() = 0; // PURE virtual method
};
```

If a class has at least one **pure virtual method**, then it is an abstract class. It **cannot be instantiated**.

In Java, declaring an **abstract class** is more straightforward:

```java
abstract public class Shape { // ABSTRACT class!
    public String name() { return "Shape"; }
    abstract public int area(); // abstract method (== pure virtual method in C++)
}
```

- An **abstract method** can be defined only if it's in an abstract class. Actually it is just a reminder since the class is already abstract, i.e. you can just write like:

  ```java
  public int area() {} // everything OK
  ```

- An abstract class **cannot be instantiated**, just like C++.

How to inherit an abstract class? Quite the same, nothing special:

```java
public class Rectangle extends Shape {
    int x, y;
    @Override
    public String name() { return "Rectangle"; }

    @Override
    public int area() {
        return x * y;
    }
}
```

## Interface, Implementation

Java provide another way to describe "abstract": **interface**.

An interface is used for grouping related methods. It is quite like an abstract class, but its design is oriented to methods, and is "more abstract":

- **Cannot be instantiated**.
- No constructor.
- Every member variable can only be `public static final` one
- Every method can only be `public abstract` one.
- Can only **declare** methods without bodies. (NOTE: after jdk1.8, method bodies in interface are legal as well.)

For instance, we can define an interface like:

```java
// EverydayWork.java
public interface EverydayWork { // INTERFACE!
    String eat(); // implicitly: "public abstract String eat();"
    String sleep(); // same
}
```

How to implement this interface? Use `implements` key word (quite like `extends`):

```java
// AliceEverydayWork.java
public AliceEverydayWork implements EverydayWork { // IMPLEMENTS!
    public String eat() { // implementation of super eat() method
        return "Eat everything.";
    }
    public String sleep() {
        return "sleep for 25 hrs.";
    }
}
```

Hint: interface is useful in designing `ASTVisitor`, since it is a group of abstract methods.