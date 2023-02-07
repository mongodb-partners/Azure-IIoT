# Overview
  
This python script is used to generate and push messages to confluent Kafka topic with connector configured to consume MongoDB time series data 
Please refer for details on [Confluent Python Client for Apache Kafka](https://github.com/confluentinc/confluent-kafka-python).

### Setup.
1. Clone GitHub this repo repository.

2. Create a local file (for example, at $HOME/.confluent/librdkafka.config) with configuration parameters to connect to your Kafka cluster. Starting with one of the templates below, customize the file with connection information to your cluster. Substitute your values for {{ BROKER_ENDPOINT }}, {{CLUSTER_API_KEY }}, and {{ CLUSTER_API_SECRET }} (see Configure Confluent Cloud Clients for instructions on how to manually find these values, or use the ccloud-stack Utility for Confluent Cloud to automatically create them).
    
    Template configuration file for Confluent Cloud.




       bootstrap.servers={{ BROKER_ENDPOINT }}
       security.protocol=SASL_SSL
       sasl.mechanisms=PLAIN
       sasl.username={{ CLUSTER_API_KEY }}
       sasl.password={{ CLUSTER_API_SECRET }}
   

3. Install requirements using pip3:

   
      pip3 install -r requirements.txt
   

4. Pass the <topic configured> for  configured topic on confluent cloud and run the Producer by running:


    python3 generate_stimulation_data.py   -f $HOME/.confluent/librdkafka.config -t <topic configured>

### Source Documentation

You can find the documentation and instructions for running this Python example at [https://docs.confluent.io/platform/current/tutorials/examples/clients/docs/python.html](https://docs.confluent.io/platform/current/tutorials/examples/clients/docs/python.html?utm_source=github&utm_medium=demo&utm_campaign=ch.examples_type.community_content.clients-ccloud)

### Schema samples
{"timestamp":{"$date":"2021-08-09T01:24:43.000Z"},"reg_num":"KA111","lat":41.2860336,"lon":19.8905149, "owner": "Peter","city": "Bengalore"}


