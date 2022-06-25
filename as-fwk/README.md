# as framework
此as工程可以快速的导入aosp framework(java部分)，比这个https://www.jianshu.com/p/2ba5d6bd461e方案还快，并且“联想”也很方便。

在scripts/config.gradle文件中修改asop所在的目录以及你扩展fwk所在的目录。
```bash
aospDir = "/home/solo/code/aosp"
extDir = "$aospDir/jos"
```

如果要用as编译此工程，需要在settings.gradle注释
```bash
include ':framework'
include ':services'
```