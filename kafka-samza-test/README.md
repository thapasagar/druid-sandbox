

Below is the documentation for [kafka-samza-test](https://github.com/mark1900/druid-sandbox/tree/master/kafka-samza-test).

The kafka-samza-test project uses the following technologies:

* CentOS 7
* ZooKeeper 3.4.6
* Kafka 2.10-0.8.2.1
* Samza 0.9.1


The concept is to take a Kafka Topic Message process it and publish to another Kafka Topic.  From here we can utilize the Druid's Kafka Eight Extension to consume data directly from the Kafka Topic.
* https://github.com/druid-io/druid/tree/master/extensions/kafka-eight
* http://druid.io/docs/latest/ingestion/realtime-ingestion.html


Note:

* Remember to update the application's configuration.
    * Default hostnames in Maven pom.xml  (Might be possible to edit /etc/hosts as well.  Remember it cannot point to 127.0.0.1.)

<pre><code>
        &lt;zookeeper.hostname&gt;zookeeper-hostname&lt;/zookeeper.hostname&gt;
        &lt;kafka.hostname&gt;kafka-hostname&lt;/kafka.hostname&gt;
        &lt;hadoop.hostname&gt;hadoop-hostname&lt;/hadoop.hostname&gt;
</code></pre>



# Standard Deployment

<pre><code>

cd ~/

mkdir kafka-storm-test

cd kafka-storm-test

wget http://www.us.apache.org/dist/zookeeper/zookeeper-3.4.6/zookeeper-3.4.6.tar.gz
wget http://www.us.apache.org/dist/kafka/0.8.2.1/kafka_2.10-0.8.2.1.tgz
wget http://www.us.apache.org/dist/hadoop/common/hadoop-2.7.1/hadoop-2.7.1.tar.gz

tar -xzf zookeeper-3.4.6.tar.gz
tar -xzf kafka_2.10-0.8.2.1.tgz
tar -xzf hadoop-2.7.1.tar.gz

./hadoop-2.7.1/bin/hadoop namenode -format
./kafka_2.10-0.8.2.1/bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic kafka_samza_test_phase_01
./kafka_2.10-0.8.2.1/bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic kafka_samza_test_phase_02

</code></pre>


## Build Test Appliation

* kafka-samza-test-0.0.1-dist.tar.gz

<pre><code>

    mvn clean package

</code></pre>


## Configuration

* Assume that yarn.package.path is "hdfs:", never "file:".
    * https://samza.apache.org/learn/tutorials/0.9/run-in-multi-node-yarn.html (Skip steps 4,5,6.)
    * https://samza.apache.org/learn/tutorials/0.9/deploy-samza-job-from-hdfs.html

* Configure Samza

<pre><code>

hadoop-server:~/tmp/kafka-samza-test-0.0.1-dist.tar.gz

cd ~/tmp
rm -rf kafka-samza-test && mkdir kafka-samza-test && tar -xvf kafka-samza-test-0.0.1-dist.tar.gz -C kafka-samza-test
mv ~/.samza ~/.samza-$(date +"%Y.%m.%d.%S.%N")
mkdir -p ~/.samza/conf && cp kafka-samza-test/config/standard/deploy/* ~/.samza/conf

</code></pre>


## Deploy Test Application

<pre><code>

 # Start Hadoop....
 # http://hadoop-server:8088

cd ~/tmp

 # kafka-samza-test/bin/kill-yarn-job.sh application_1440008845052_0008

 # http://samza.apache.org/learn/tutorials/0.9/deploy-samza-job-from-hdfs.html
 # ./hadoop-2.7.1/bin/hadoop fs -mkdir -p /kafka-samza-test/
 # ./hadoop-2.7.1/bin/hadoop fs -put -f ~/tmp/kafka-samza-test-0.0.1-dist.tar.gz /kafka-samza-test/kafka-samza-test-0.0.1-dist.tar.gz

kafka-samza-test/bin/run-job.sh --config-factory=org.apache.samza.config.factories.PropertiesConfigFactory --config-path=file://$PWD/kafka-samza-test/config/standard/processing-stream-task.properties

</code></pre>


## Use Test Application

<pre><code>

 # Input Sample JSON to Kafka Topic
./kafka_2.10-0.8.2.1/bin/kafka-console-producer.sh --broker-list localhost:9092 --topic kafka.kafka_samza_test_phase_01
# {"timestamp": "2015-08-21T17:08:45-0400", "key1":"value1", "key2", "value2"}

 # View Sample JSON Output
./kafka_2.10-0.8.2.1/bin/kafka-console-consumer.sh --zookeeper localhost:2181 --from-beginning --topic kafka.kafka_samza_test_phase_02

</code></pre>



# Ambari Deployment


## Install and Configure

* https://cwiki.apache.org/confluence/display/AMBARI/Install+Ambari+2.1.0+from+Public+Repositories
* https://issues.apache.org/jira/browse/AMBARI-12793


<pre><code>
cd ~/

mkdir kafka-storm-test

cd kafka-storm-test

/usr/hdp/2.3.2.0-2621/hadoop/bin/hadoop namenode -format
/usr/hdp/2.3.2.0-2621/kafka/bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic kafka_samza_test_phase_01
/usr/hdp/2.3.2.0-2621/kafka/bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic kafka_samza_test_phase_02

</code></pre>


## Build Test Appliation

* kafka-samza-test-0.0.1-dist.tar.gz


## Configuration

* Assume that yarn.package.path is "hdfs:", never "file:".
    * https://samza.apache.org/learn/tutorials/0.9/run-in-multi-node-yarn.html (Skip steps 4,5,6.)
    * https://samza.apache.org/learn/tutorials/0.9/deploy-samza-job-from-hdfs.html

* Configure Samza

<pre><code>

hadoop-server:~/tmp/kafka-samza-test-0.0.1-dist.tar.gz

cd ~/tmp
rm -rf kafka-samza-test && mkdir kafka-samza-test && tar -xvf kafka-samza-test-0.0.1-dist.tar.gz -C kafka-samza-test
mv ~/.samza ~/.samza-$(date +"%Y.%m.%d.%S.%N")
mkdir -p ~/.samza/conf && cp kafka-samza-test/config/standard/deploy/* ~/.samza/conf

</code></pre>


## Deploy Test Application

<pre><code>

 # Start Hadoop Services using Ambari
 # http://hadoop-server:8080
 # http://hadoop-server:8088

cd ~/tmp

 # kafka-samza-test/bin/kill-yarn-job.sh application_1440008845052_0008
 # /usr/hdp/2.3.2.0-2621/hadoop/bin/yarn application -kill application_1440008845052_0011

 # http://samza.apache.org/learn/tutorials/0.9/deploy-samza-job-from-hdfs.html
 # /usr/hdp/2.3.2.0-2621/hadoop/bin/hadoop fs -mkdir -p /kafka-samza-test/
 # /usr/hdp/2.3.2.0-2621/hadoop/bin/hadoop fs -put -f ~/tmp/kafka-samza-test-0.0.1-dist.tar.gz /kafka-samza-test/kafka-samza-test-0.0.1-dist.tar.gz

kafka-samza-test/bin/run-job.sh --config-factory=org.apache.samza.config.factories.PropertiesConfigFactory --config-path=file://$PWD/kafka-samza-test/config/ambari/processing-stream-task.properties

</code></pre>


## Use Test Application

<pre><code>

cd /usr/hdp/2.3.2.0-2621/kafka

 # Input Sample JSON to Kafka Topic
bin/kafka-console-producer.sh --broker-list \`hostname\`:6667 --topic kafka_samza_test_phase_01
# {"timestamp": "2015-08-21T17:08:45-0400", "key1":"value1", "key2":"value2"}

 # View Sample JSON Output
bin/kafka-console-consumer.sh --zookeeper \`hostname\`:2181 --from-beginning --topic kafka_samza_test_phase_02

</code></pre>
