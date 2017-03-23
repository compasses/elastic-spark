
## 大数据&分布式计算：第一周
互联网技术的繁荣，助推了大数据处理技术的演进。目前很多公司都会说说大数据、数据挖掘、机器学习等。特别最近几年AI领域又开始风气浪涌，所以有时间还是要研究一下。平时有时间就会跟踪看看最近领域的发展和最新动向。自己也想持续的研究这一领域，毕竟自己研究生期间做过图像检索、语义理解方向的研究。开始看看是不是有新的突破。

当然现在开始再研究理论已经不合适了，这些是学校里的研究生和博士生所要研究的，目前自己更关注大数据处理的技术、框架等等。这一周就折腾了Hadoop和Spark，还有语言Scala。还有Flink，貌似用的也很多。主要集中学习Spark，也发现ElasticSearch可以和Hadoop、Spark集成起来，后面会看看怎么应用到自己的项目当中。

### Scala
Scala语言，基于JVM，是函数式编程语言。非常的灵活多变，可以用最少的代码实现功能。就像它所倡导的**Less Code, Less Mistake**，Scala在大数据处理领域会独放异彩。而语言也是不断进化的，好的特性就会相互借鉴。

学习Scala的首要原因也是Spark用Scala实现的，为后面深入研究做好准备吧。安装一个Scala的插件在IntelliJ上，开发很方便。可以方便的建立基于sbt的项目。

### Spark


### HDFS
Hadoop算是比较成熟的大数据处理设施了，安装单节点在本地Mac上，可根据[官网](https://hadoop.apache.org/docs/stable/hadoop-project-dist/hadoop-common/SingleCluster.html)即可。
Hadoop的生态已经非常完备，在工业实战方面，已经经受了很多考验。它主要的基础设施包括：HDFS文件系统、MapReduce计算框架、YARN调度管理。Hadoop目前主要使用了HDFS。

HDFS作为分布式的存储系统，被广泛使用。Spark可以基于HDFS来进行计算。HDFS是个抽象层，背后是个分布式的存储系统，用户感觉不到后面的多个服务器存在。容错性也是由这种多个机器分摊了，扩展性也得到了满足。
HDFS有NameNode和DataNode之分，NameNode保存了元数据，就是那些复杂的目录结构，DataNode存储数据。


安装配置非常简单：

1.  vi etc/hadoop/core-site.xml 
    ```
    <configuration>
        <property>
            <name>fs.defaultFS</name>
            <value>hdfs://localhost:9000</value>
        </property>
    </configuration>
    ```
2.  vi etc/hadoop/hdfs-site.xml 
    ```
    <configuration>
        <property>
            <name>dfs.replication</name>
            <value>1</value>
        </property>
    
    <property>
          <name>dfs.namenode.name.dir</name>
          <value>/Users/jet/hadoop-2.7.3/hdfs/namenode</value>
     </property>
     <property>
          <name>dfs.datanode.data.dir</name>
          <value>/Users/jet/hadoop-2.7.3/hdfs/datanode</value>
     </property>
    </configuration>
    ```
3. bin/start-dfs.sh
支持文件操作命令，例如：```bin/hadoop dfs -ls /```，查看根目录的内容。