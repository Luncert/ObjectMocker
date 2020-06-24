```java
class TestA {
  private String field1;
  private String field2;
}

class TestB {
  private int field1;
}
```
这种情况怎么处理

## V2 Features

### Virtual Context -> Child Context

V1中的Virtual Context机制没有正确地做到context间隔离。V2的Child Context具有以下特点：
* 在Child Context中添加/删除ignore不会修改Parent Context的配置
* 在Child Context中添加fieldGenerator不会修改Parent Context
* Child Context的ignore和fieldGenerator对Parent Context不可见，相反Parent Context的对Child Context的是可见的
* 如果Parent Context有某个字段的fieldGenerator，那么从Child Context获取到的fieldGenerator实际上是Parent Context对应fieldGenerator的proxy，对proxy的修改不会影响到client