# 背景
aosp每年更新一个大版本，有些版本代码变化非常大。本人有幸经历几家手机公司，一直奋斗在修改framework的前线。
有些公司代码解耦做得比较好，有些公司代码解耦做得稍微差一点。如果代码全部都加到原生framework仓，基本上到每年的android大版本升级会很劳民伤财，一边做新项目一边升级老项目，辛苦程度可想而知。
前段时间上海疫情居家办公，享受到了不用通勤多获得几小时的时间。便用了几天的时候去了解各大手机厂的framework解耦方案。综合几家的优点并避免抄袭的嫌疑，得出了如下的方案（因为种种原因当前公司没有使用这个方案，所以也不担心代码泄露的问题）。

# 简介
```bash
jos/frameworks
├── base
│   ├── core
│   │   └── java
│   │       ├── com
│   │       │   ├── android
│   │       │   │   └── server
│   │       │   └── journeyOS
│   │       │       └── server
│   │       │           └── godeye
│   │       └── system
│   │           └── ext
│   │               ├── hook
│   │               ├── reflect
│   │               └── utils
│   ├── data
│   │   └── etc
│   └── services
│       └── core
│           └── java
│               └── com
│                   └── android
│                       └── server
│                           └── display
└── ext
    ├── core
    │   └── java
    │       └── com
    │           └── android
    │               └── server
    └── services
        └── core
            └── java
                └── com
                    ├── android
                    │   └── server
                    │       └── display
                    └── journeyOS
                        └── server
                            └── godeye
```
如上为frameworks的基本目录结构。
jos/frameworks/base下的目录结构跟frameworks/base完全一致，jos/frameworks/ext下的目录结构跟frameworks/base基本一致。
jos/frameworks/base为接口类，jos/frameworks/ext为实现类。
- framework.jar
    jos/frameworks/base/core下文件编译到原生framework.jar，并且全部为interface。
- services.jar
    jos/frameworks/base/services/core下文件编译到原生services.jar，并且全部为interface。
- jos-framework.jar
    jos/frameworks/ext/core下文件编译到jos-framework.jar，并且全部为impl。
- jos-services.jar
    jos/frameworks/ext/services/core下文件编译到jos-services.jar，并且全部为impl。

# 定义
文件jos/frameworks/base/core/java/com/android/server/HookSystemConfig.java内容如下：
```bash
package com.android.server;

import java.io.File;

import system.ext.hook.Inject;

public interface HookSystemConfig {

    static HookSystemConfig get() {
        return (HookSystemConfig) Inject.getInstance().getInject(HookSystemConfig.class);
    }

    void init(SystemConfig systemConfig);

    default void readPermissionsFromXml(File file) {
    }

    default boolean isExtFile(File file) {
        return false;
    }

    default boolean supportBackgroundService(String packageName) {
        return false;
    }
}
```
根据前面我们聊到的，这个接口文件就是编译到framework.jar中。原生代码通过如下调用：
```bash
HookSystemConfig.get().supportBackgroundService(packageName);
```
就可以调到其实现类，我们这边暂不说明为什么可以调过去，先来看看具体的实现类。

# 实现
文件jos/frameworks/ext/core/java/com/android/server/HookSystemConfigImpl.java
```bash
package com.android.server;

import android.text.TextUtils;
import android.util.ArraySet;
import android.util.Xml;

import com.android.internal.util.XmlUtils;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import system.ext.utils.JosLog;

public class HookSystemConfigImpl implements HookSystemConfig {
    private static final String TAG = HookSystemConfigImpl.class.getSimpleName();
    private static final boolean DEBUG = true;
    static HookSystemConfigImpl sInstance;

    private SystemConfig mSystemConfig;

    private final ArraySet<String> mBackgroundServices = new ArraySet<>();

    public static HookSystemConfigImpl getInstance() {
        synchronized (HookSystemConfigImpl.class) {
            if (sInstance == null) {
                sInstance = new HookSystemConfigImpl();
            }
            return sInstance;
        }
    }

    public HookSystemConfigImpl() {
    }

    @Override
    public void init(SystemConfig systemConfig) {
        mSystemConfig = systemConfig;
    }

    @Override
    public void readPermissionsFromXml(File file) {
        FileInputStream stream;
        synchronized (file) {
            try {
                stream = new FileInputStream(file);
            } catch (FileNotFoundException e) {
                JosLog.e(TAG, "jos-feature.xml not found; Skipping.");
                return;
            }
            boolean success = false;
            try {
                XmlPullParser parser = Xml.newPullParser();
                parser.setInput(stream, null);
                int type;
                success = true;
                while ((type = parser.next()) != XmlPullParser.START_TAG
                        && type != XmlPullParser.END_DOCUMENT) {
                    ;
                }
                if (type != XmlPullParser.START_TAG) {
                    throw new IllegalStateException("no start tag found");
                }

                int outerDepth = parser.getDepth();
                while ((type = parser.next()) != XmlPullParser.END_DOCUMENT
                        && (type != XmlPullParser.END_TAG || parser.getDepth() > outerDepth)) {
                    if (type == XmlPullParser.END_TAG
                            || type == XmlPullParser.TEXT) {
                        continue;
                    }

                    String name = parser.getName();
                    switch (name) {
                        case "background-services":
                            String packageName = parser.getAttributeValue(null, "package");
                            mBackgroundServices.add(packageName);
                            break;
                        default:
                            JosLog.w(TAG, "Tag " + name + " is unknown in "
                                    + file + " at " + parser.getPositionDescription());
                            XmlUtils.skipCurrentTag(parser);
                            break;
                    }
                }
            } catch (IllegalStateException e) {
                JosLog.w(TAG, "Failed parsing " + e.toString());
            } catch (NullPointerException e) {
                JosLog.w(TAG, "Failed parsing " + e.toString());
            } catch (NumberFormatException e) {
                JosLog.w(TAG, "Failed parsing " + e.toString());
            } catch (XmlPullParserException e) {
                JosLog.w(TAG, "Failed parsing " + e.toString());
            } catch (IOException e) {
                JosLog.w(TAG, "Failed parsing " + e.toString());
            } catch (IndexOutOfBoundsException e) {
                JosLog.w(TAG, "Failed parsing " + e.toString());
            } finally {
                try {
                    stream.close();
                } catch (IOException e) {
                }
            }
        }
    }

    @Override
    public boolean isExtFile(File file) {
        boolean extFile = false;
        if (file != null) {
            extFile = file.getPath().endsWith("etc/permissions/jos-feature.xml");
        }

        return extFile;
    }

    @Override
    public boolean supportBackgroundService(String packageName) {
        if (DEBUG) {
            JosLog.d(TAG, "query packageName = [" + packageName + "]");
        }
        boolean support = false;
        if (TextUtils.isEmpty(packageName)) {
            JosLog.w(TAG, "query packageName was null...");
            return support;
        }
        for (String backgroundService : mBackgroundServices) {
            if (DEBUG) {
                JosLog.d(TAG, "background service = [" + backgroundService + "]");
            }

            if (packageName.equals(backgroundService)) {
                support = true;
                break;
            }
        }

        return support;
    }

}

```
根据前面我们聊到的，这个接口文件就是编译到jos-framework.jar中。从这个依然看不出来代码有何特别之处，为何调用HookSystemConfig就可以调到HookSystemConfigImpl的接口了呢？


# 原生调用
为了方便介绍整体的方案，我这里还是多啰嗦介绍原生代码如何调用：
```bash
HookSystemConfig.get().supportBackgroundService(packageName);
```
就可以调到其实现类，我们现在开始来分析其原理。

# 原理
从前面的调用方式以及接口类里的代码我们不难发现，其实做核心的代码是在get()函数里。
```bash
public interface HookSystemConfig {

    static HookSystemConfig get() {
        return (HookSystemConfig) Inject.getInstance().getInject(HookSystemConfig.class);
    }

}
```
调用了Inject类的getInject()方法。这里把Inject类的代码列出来，很简单，其实就是通过反射的方式把interface和Impl关联起来，并保存到map里提供下次使用。
而且Inject是单例，即使impl不是单例，通过map拿到的还是原来的impl实例。当然笔者在这之前也多预留了接口，如果impl写成单例，也可以在调用getInject选择使用单例还是非单例。
```bash
public class Inject {
    private static final String TAG = Inject.class.getSimpleName();
    private static final boolean DEBUG = true;
    private static final ArrayMap<Class<?>, Object> INJECT_MAPS = new ArrayMap<>();
    private static Inject sInstance;

    public Inject() {
    }

    public static Inject getInstance() {
        synchronized (Inject.class) {
            if (sInstance == null) {
                sInstance = new Inject();
            }
            return sInstance;
        }
    }

    private static Object create(String clsName, ClassLoader classLoader, boolean isSingleton) {
        try {
            Class<?> clazz;
            if (classLoader != null) {
                clazz = Class.forName(clsName, false, classLoader);
            } else {
                clazz = Class.forName(clsName);
            }
            if (clazz != null) {
                if (isSingleton) {
                    return MethodUtils.invokeStaticMethod(clazz, "getInstance");
                } else {
                    return MethodUtils.invokeConstructor(clazz);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Object getInject(Class tClass) {
        return getInject(tClass, null, false);
    }

    public Object getInject(Class tClass, String dClass) {
        return getInject(tClass, dClass, false);
    }

    public Object getInject(Class tClass, String dClass, boolean isSingleton) {
        if (DEBUG) {
            JosLog.d(TAG, "interface class = [" + tClass + "], is singleton = [" + isSingleton + "]");
        }

        if (!INJECT_MAPS.containsKey(tClass)) {
            String clsName = dClass;
            if (TextUtils.isEmpty(clsName)) {
                clsName = tClass.getCanonicalName() + "Impl";
            }
            if (DEBUG) {
                JosLog.d(TAG, "impl class = [" + tClass + "]");
            }

            Object inject = create(clsName, tClass.getClassLoader(), isSingleton);
            if (DEBUG) {
                JosLog.d(TAG, "constructor impl class = [" + tClass + "]");
            }

            if (inject != null && tClass.isAssignableFrom(inject.getClass())) {
                INJECT_MAPS.put(tClass, inject);
            } else {
                INJECT_MAPS.put(tClass, null);
            }
        }
        return INJECT_MAPS.get(tClass);
    }
}
```

# 注意
因为是反射的关系，而且在Inject.getInstance().getInject()函数里并没有传任何跟需要反射的impl类相关的代码。而且在代码里写死了：
```bash
String clsName = tClass.getCanonicalName() + "Impl";
```
也就是说这里有一个默认的重要规则：
1. Impl类跟interface要同包名
2. Impl类的命名规范是在interface类后加Impl，也就是说如果interface类叫XXX，那么Impl是XXXImpl。
3. 如果Impl类写成单例，单例的方法必须写成getInstance()

>
> 可以通过分析函数public Object getInject(Class tClass, String dClass, boolean isSingleton)可知，如果传实现类的class进来（等同于实现类的getCanonicalName）就可忽略1、2点的限制。
> 如：
```bash
public interface HookSystemConfig {

    static HookSystemConfig get() {
        return (HookSystemConfig) Inject.getInstance().getInject(HookSystemConfig.class, "com.journeyOS.server.ExtSystemConfig");
    }

}
```
> 笔者为了统一以及规范性，还是建议按照以上规则。

举例：
```bash
package com.android.server;

public interface HookSystemConfig {
}
```

```bash
package com.android.server;

public class HookSystemConfigImpl implements HookSystemConfig {
}
```


# 编译到rom
```bash
# 在对应target里的device.mk
# 如 build/target/product/sdk_phone_x86_64.mk
$(call inherit-product-if-exists, jos/frameworks/frameworks_ext.mk)
```

android 12之后 SKIP_BOOT_JARS_CHECK = true 不生效，在文件的最后 build/soong/scripts/check_boot_jars/package_allowed_list.txt加如下：
```bash
###################################################
# Packages in the journeyOS namespace across all bootclasspath jars.
system\.ext.*
system\.ext\..*
journeyOS\.os.*
journeyOS\.os\..*
com\.journeyOS.*
com\.journeyOS\..*
```

# 原生fwk bp
在frameworks/base/Android.bp里filegroup为framework-non-updatable-sources引用
```bash
//ext
":jos_framework_sources_aidl",
":jos_framework_sources_java",
```

在frameworks/base/services/core/Android.bp里filegroup为services.core.unboosted引用
```bash
//jos
":jos_service_sources_aidl",
":jos_service_sources_java",
```
