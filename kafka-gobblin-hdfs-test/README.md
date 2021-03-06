

Below is the documentation for [kafka-gobblin-hdfs-test](https://github.com/mark1900/druid-sandbox/tree/master/kafka-gobblin-hdfs-test).

The kafka-gobblin-hdfs-test project uses the following technologies:

* CentOS 7
* Ambari 2.1.0
    * ZooKeeper 3.4.6
    * Kafka 0.8.2
    * Hadoop (HDFS, MapReduce2, YARN) 2.7.1
    * Pig 0.15.0
    * Tez 0.7.0
    * Hive 1.2.1


Goal

To utilize Gobblin to create a Hadoop MapReduce Job that will:

1. Extract Kafka Topic Messages
2. Transform the JSON Message payload into the AVRO format.
3. Push data into HDFS


Notes:

* Remember to update the application's configuration.
    * Default hostnames in Maven pom.xml  (Might be possible to edit /etc/hosts as well.  Remember it cannot point to 127.0.0.1.)

<pre><code>
        &lt;kafka.hostname&gt;kafka-hostname&lt;/kafka.hostname&gt;
        &lt;hadoop.hostname&gt;hadoop-hostname&lt;/hadoop.hostname&gt;
</code></pre>


See also:
* https://github.com/linkedin/gobblin/wiki/Kafka-HDFS-Ingestion
* https://github.com/linkedin/gobblin/wiki/Gobblin%20Deployment#Hadoop-MapReduce-Deployment


# Ambari Deployment


## Install and Configure Environment

* https://cwiki.apache.org/confluence/display/AMBARI/Install+Ambari+2.1.0+from+Public+Repositories
* https://issues.apache.org/jira/browse/AMBARI-12793


<pre><code>
cd ~/

mkdir ~/tmp

cd kafka-storm-test

/usr/hdp/current/hadoop-client/bin/hadoop namenode -format
/usr/hdp/current/kafka-broker/bin/kafka-topics.sh --create --zookeeper \`hostname\`:2181 --replication-factor 1 --partitions 1 --topic kafka-gobblin-hdfs-test

</code></pre>


## Build Test Appliation

* Build Gobblin from source
    * https://github.com/linkedin/gobblin.git
    * Set GOBBLIN_HOME environment variable (See kafka-gobblin-hdfs-test/pom.xml).

* Build this Maven project kafka-gobblin-hdfs-test
    * mvn clean package
    * kafka-gobblin-hdfs-test-0.0.0-dist.tar.gz


## Configuration Gobblin


<pre><code>

 vim ~/.bashrc

 # Add the following entries
export HADOOP_BIN_DIR=/usr/hdp/current/hadoop-client/bin

</code></pre>


<pre><code>

mkdir ~/tmp && cd ~/tmp

 # Copy required files

cd ~/tmp/

 # Extract kafka-gobblin-hdfs-test-0.0.0.jar to directory kafka-gobblin-hdfs-test-0.0.0
unzip kafka-gobblin-hdfs-test-0.0.0.jar -d kafka-gobblin-hdfs-test-0.0.0

 # Extract gobblin-dist.tar.gz to directory gobblin-dist
tar -zxvf gobblin-dist.tar.gz

 # Update the Gobblin config file (fs.uri=hdfs://hadoop-hostname:8020)
 # vim gobblin-dist/conf/gobblin-mapreduce.properties
sed -i -- 's/localhost:8020/hadoop-hostname:8020/g' gobblin-dist/conf/gobblin-mapreduce.properties

 # Check the Job config file
vim kafka-gobblin-hdfs-test-0.0.0/kafka-gobblin-hdfs-test.pull

 # Fix Windows encoding
yum install -y dos2unix
find . -type f -name "*.sh" -exec dos2unix {} \;

cd gobblin-dist
chmod +x bin/gobblin-mapreduce.sh

</code></pre>




## Deploy Test Application

<pre><code>

 # Start Hadoop Services using Ambari
 # http://server1:8080
 # http://server1:8088

cd ~/tmp

 # Configure HDFS Output Directories
 sudo usermod -a -G hdfs,hadoop $USER
 /usr/hdp/current/hadoop-client/bin/hadoop fs -mkdir -p /gobblin/work

cd gobblin-dist
./bin/gobblin-mapreduce.sh --jars ../kafka-gobblin-hdfs-test-0.0.0.jar,lib/guava-15.0.jar,lib/* --conf ../kafka-gobblin-hdfs-test-0.0.0/kafka-gobblin-hdfs-test.pull --workdir hdfs://\`hostname\`:8020/gobblin/work


 # Kill running application
 # /usr/hdp/current/hadoop-yarn-client/bin/yarn application -kill application_1440008845052_0011

 # View working data
 # /usr/hdp/current/hadoop-client/bin/hadoop fs -ls /gobblin/work/
 # Erase all Gobblin locks (Set in kafka-gobblin-hdfs-test.pull)
 # /usr/hdp/current/hadoop-client/bin/hadoop fs -rm -r -skipTrash /gobblin/work/locks


</code></pre>


## Use Test Application


Create Kafka Message


* Option 1:

  Run Java Application test.KafkaMessageProducer


* Option 2:


<pre><code>

/usr/hdp/current/kafka-broker/bin/kafka-console-producer.sh --broker-list \`hostname\`:6667 --topic kafka-gobblin-hdfs-test
{"timestamp": "2015-08-21T17:08:45-0400", "type": "SYSTEM","level": 5,"message": "RAM usage above 90 per cent utilization"}

</code></pre>


View Gobblin Ingestion

<pre><code>

 # View working data
 /usr/hdp/current/hadoop-client/bin/hadoop fs -ls /gobblin/work/

</code></pre>
