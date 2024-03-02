# InfinityGuizhanLib (Standalone Edition)

[![Maven Central](https://img.shields.io/maven-central/v/net.guizhanss/GuizhanLib.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22net.guizhanss%22%20AND%20a:%22GuizhanLib%22)

A library that helps developing Slimefun addons and translate Slimefun addons to Simplified Chinese.  
一个帮助粘液科技附属开发，并可以汉化粘液附属的库。

## What's the difference with the original version?

Actually, there is <b>almost no difference</b>. However, the implementation of the <b>[SlimefunAddon](https://github.com/Slimefun/Slimefun4/blob/master/src/main/java/io/github/thebusybiscuit/slimefun4/api/SlimefunAddon.java)</b> class is separate from
the <b>[AbstractAddon](https://github.com/ARVIN3108/InfinityGuizhanLib-Standalone/blob/master/GuizhanLib-slimefun/src/main/java/net/guizhanss/guizhanlib/slimefun/addon/AbstractAddon.java)</b> class, and of course <b>there is [InfinityLib](https://github.com/Mooy1/InfinityLib) class as well</b>. This will be very useful for plugins that want to use <b>[InfinityLib](https://github.com/Mooy1/InfinityLib) and [GuizhanLib](https://github.com/ybw0014/GuizhanLib) simultaneously without having to call the [Slimefun](https://github.com/Slimefun/Slimefun4) API class</b>. This way, we can place Slimefun in a [soft-depend](https://docs.papermc.io/paper/dev/plugin-yml#softdepend) position in the Bukkit configuration file [plugin.yml](https://docs.papermc.io/paper/dev/plugin-yml).

## Usage | 如何使用

Add `GuizhanLib-api` (which includes all sub modules) or respective modules as dependency (Maven for example):  
将`GuizhanLib-api`（包含所有包）或者指定的包添加为依赖项（以 Maven 为例）：

```
    <dependency>
        <groupId>net.guizhanss</groupId>
        <artifactId>GuizhanLib-api</artifactId>
        <version>REPLACE WITH VERSION</version>
        <scope>compile</scope>
    </dependency>
```

You will need to relocate the library classes if you use it for addon development.  
在`build`中，你需要将GuizhanLib迁移到你的包中，避免与其他插件中使用的GuizhanLib冲突
（如果已存在`maven-shade-plugin`的配置，只需要添加relocation即可:

```
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-shade-plugin</artifactId>
                <version>3.3.0</version>

                <configuration>
                    <!-- Add the following field to remove all unused classes and reduce the size of generated jar file. Not required, but recommended  -->
                    <!-- 你可以添加下面这一行，去除所有库中未使用的类，来减少生成jar的大小，非必须，但建议开启 -->
                    <minimizeJar>true</minimizeJar>
                    <relocations>
                        <!-- IMPORTANT: add the following relocation -->
                        <!-- 重要: 你需要将以下relocation(迁移)部分添加到你的pom.xml中 -->
                        <relocation>
                            <pattern>net.guizhanss.guizhanlib</pattern>
                            <shadedPattern>(YOUR PACKAGE NAME HERE).guizhanlib</shadedPattern>
                        </relocation>
                        <relocation>
                            <pattern>id.arvin3108.standalone</pattern>
                            <shadedPattern>(YOUR PACKAGE NAME HERE).standalone</shadedPattern>
                        </relocation>
                    </relocations>

                    <filters>
                        <filter>
                            <artifact>*:*</artifact>
                            <excludes>
                                <exclude>META-INF/*</exclude>
                            </excludes>
                        </filter>
                    </filters>
                </configuration>

                <executions>
                    <execution>
                        <phase>package</phase>
                        <goals>
                            <goal>shade</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
        </plugins>
```

## Changelog | 更新日志

The changelog is only available in Chinese.

[Changelog](/CHANGELOG.md)
