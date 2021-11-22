

# Fleet management using confluent and Atlas
The Android application targets to utilize and demonstrate the power and features of MongoDB atlas and Confluent Cloud.
The android application uses features such as time series collection, RealmDB, sync, triggers and push notifications from MongoDB atlas also uses KSQL and MongoDbAtlasSinkConnector from Confluent cloud to transform and load the data to MongoDB Atlas.
We have utilised time series collection hosted on mongodb managed atlas cluster as a sink for confluent connector to store the stream data for stimulation of moving vehicles. The stream can be generated using python script in [data_generator](data_generator) folder. This Demo is w.r.t. static location for city of Bengaluru and can be updated as per your use case. 

## Setup
Prerequisites for building the run and build the apk.

    1. Android Studio.
    2. Mongodb Atlas cluster with mongodb version 5.0 or higher.
    3. Configure Realm.
    4. Configure Confluent cloud.
    5. Firebase Account. (for Alerts and Push notifications).
    6. GCP cloud credentials for maps service and firebase service.


### 1. Install android studio:
Download and Install android studio from [here](https://developer.android.com/studio). Clone the repository and open the IoT/AndroidApp in android studio. Sync all the gradle dependencies. Download this repo and open with android studio.

### 2. Configure MongoDB Atlas:

Follow below steps to setup atlas cluster and collections:
* Set up an [Atlas](https://www.mongodb.com/atlas) cluster or login into your cluster if you have it already. Please make sure you are running on **5.0** or higher version of Mongo on your cluster([setup instructions](https://docs.atlas.mongodb.com/tutorial/deploy-free-tier-cluster/)).
* Create a database named vehicle in your cluster.
* Create collection **TrackingGeospatial** which will hold the data of current location of tracked users also the details of users such as city, name etc. The data in this collection would be the latest data that we will sync with the mobile application.
* **tracking-historic** (Time series collection) which will hold Live / Stimulated data. Time series capabilities are available on 5.0 and higher. The timestamp field name should be set as **Timestamp**.

### 3. Configure Realm:
We need following preconfigured in realm application to run the android application. Create a [realm application](https://docs.mongodb.com/realm/manage-apps/create/create-with-realm-ui/) from realm tab of your Atlas UI and navigate to schema. Create a [schema](https://docs.mongodb.com/realm/sdk/android/#define-an-object-schema) for the collection TrackingGeospatial.
* ##### Realm schema for TrackingGeospatial.
  TrackingGeospatial schema:
  Create a realm application with following schema. Note: The "partition_key" can be set as per requirement depending upon use case please refer [here](https://docs.mongodb.com/realm/sync/partitions/). Verify the data model is generated for the schema by navigating to SDK on side pane of Realm UI.

      {
          "title": "TrackingGeoSpatial",
          "bsonType": "object",
          "required": ["_id"],
          "properties": {
              "_id": {
                  "bsonType": "string"
              },
              "_modifiedTS": {
                  "bsonType": "date"
              },
              "location": {
                  "bsonType": "object",
                  "properties": {
                      "coordinates": {
                          "bsonType": "array",
                          "items": {
                              "bsonType": "double"
                          }
                      },
                      "type": {
                          "bsonType": "string"
                      }
                  }
              },
              "partition_key": {
                  "bsonType": "string"
              },
              "city": {
                  "bsonType": "string"
              },
              "owner": {
                  "bsonType": "string"
              }
          }
      }



* ##### Webhooks :
  Create [webhooks](https://docs.mongodb.com/realm/services/) (Navigate to 3rd party services, Click on add service, click on </>HTTP) to access the time series collection data and the tracking collection for displaying all vehicles.

  Function : GetTimeline : Returns all coordinates for requested vehicle for 1 hour.

      // This function is the webhook's request handler.
       exports = function(payload, response) {
              const body = payload.query.reg_num;
              const doc = context.services.get("mongodb-atlas").db("vehicle").collection("tracking-historic").find({
              "Timestamp" : { "$lt": new Date(), "$gte": new Date(new Date().setDate(new Date().getDate()-1))},
              "reg_num": body
          });
          return  doc;
          }


      Copy the webhook URLs to the variable URL of MyApplication class delarations.


* ##### Triggers for database collection update:
  Create a [trigger](https://docs.mongodb.com/realm/triggers/database-triggers/) function to listen to the database change event(Select operation type insert, update). Function is configured to send push notifications to the application on change event on TrackingGeospatial collection.

          exports = function(changeEvent) {
            const { updateDescription, fullDocument } = changeEvent;
          
            const doc = context.services.get("mongodb-atlas").db("vehicle").collection("TrackingGeospatial").aggregate([
            {"$geoNear": {"near": { "type": 'Point', "coordinates": [12.97182, 77.59499] },"distanceField": 'dist',"maxDistance": 5000}}
            ]);
            context.services.get("gcm").send({
              "to": "/topics/GeofenceTrigger",
              "notification":{
              "title":"Alert!!",
              "body":String(doc)
            }
            });
            return doc;
          };



* ##### Realm App id :
  Copy the app id to appid variable in MyApplication class.

* ##### GCP map token:
  Create google API_KEY (On GCP console, Navigate to APIs and Services, Click on credentials and create credentials ) for accessing maps service and paste it in AndroidManifest.xml file.

* ##### Firebase Account for push notifications:
  Create a [Firebase](https://console.firebase.google.com/?pli=1) account add the api and api_key to the push notification settings.


Start the sync by navigating to sync on side pane from realm UI. Follow the [documentation](https://docs.mongodb.com/realm/sync/get-started/) for more details

### 4. Confluent Configuration
Official [documentation](https://docs.confluent.io/cloud/current/overview.html) can be used to create the cluster, topic, connectors and run ksql queries or Follow the instruction in [here](https://github.com/AskMeiPaaS/iiot-hybrid-with-mongodb-confluent) to create a topic and MongoDBAtlasSink connector.

##### Note:
Create the atlas cluster and confluent cluster in same region. The sample payload to time series collection is shown below.

* Create a topic named iot.data.

* Create 2 MongoDBAtlasSink connectors with below configurations (Note: If using confluent UI, copy the value from below configurations)

i. Create stream to modify the input data and pass to the topic. Navigate to confluent cloud, click on ksql and editor. Copy paste the below sql commands to create the streams. The topic iot.data should be present before we run the below commands.


    create stream stream01
    (
        "reg_num" varchar,
        "owner" varchar,
        "city" varchar,
        "lon" double,
        "lat" double,
        "partition_key" varchar
    ) WITH (KAFKA_TOPIC='iot.data',
        VALUE_FORMAT='JSON'
    );


    create stream finalStream as select "city", "owner", "reg_num" as "_id", struct("type":='Point', "coordinates":=array["lat", "lon"]) as "location","partition_key" from  stream01 emit changes;



ii. Time series connector configuration.


            {
                "name": "MongoDbAtlasSinkConnector_0",
                "config": {
                    "connector.class": "MongoDbAtlasSink",
                    "name": "MongoDbAtlasSinkConnector_0",
                    "input.data.format": "JSON",
                    "topics": "iot.data",
                    "connection.host": "iiotapp.2wqno.mongodb.net",
                    "connection.user": "venkatesh",
                    "database": "vehicle",
                    "collection": "tracking-historic",
                    "max.num.retries": "1",
                    "timeseries.timefield": "Timestamp",
                    "timeseries.timefield.auto.convert": "true",
                    "timeseries.timefield.auto.convert.date.format": "yyyy-MM-dd'T'HH:mm:ss'Z'",
                    "tasks.max": "1"
                }
            }



iii. Geospatial connector configuration.


    {
        "name": "MongoDbAtlasSinkConnector_1",
        "config": {
            "connector.class": "MongoDbAtlasSink",
            "name": "MongoDbAtlasSinkConnector_1",
            "input.data.format": "JSON",
            "topics": "pksqlc-o2znjFINALSTREAM",
            "connection.host": "iiotapp.2wqno.mongodb.net",
            "connection.user": "venkatesh",
            "database": "vehicle",
            "collection": "TrackingGeospatial",
            "write.strategy": "UpdateOneTimestampsStrategy",
            "tasks.max": "1"
        }
    }


#### Time series data format:
The Timestamp field should be of string format.

    {      
      "city": "Bangalore",
      "lat":12.9737,
      "lon":77.6248,
      "owner": "Peter",
      "reg_num":"KA1111",
      "partition_key":"security",
      "Timestamp":"2021-10-13T12:25:45Z"
    } 

#### Geospatial data format:
The data in geospatial collection will be loaded from confluent connector for geospatial collection. The input data in transformed into geospatial format using ksql queries shown in next step.

      {
         "partition_key": "security",
         "Timestamp": "2021-09-16T18:08:49.520Z"
         "city": "banglore",
         "location": {
            "coordinates": [12.9716, 77.5946],
            "type": "Point"
         },
         "owner": "Jane Doe",
         "reg_num": "KA01A1111"
      }

"For this collection a 2d index need to be created on location field."


Once the setup is done run the data generator code from [data_generator](data_generator). The Application requires initial sync for anonymous login - Please reopen the application if it fails to open the very first time.


## Caution:

The following use case uses MongoDB Atlas resources and Confluent Cloud that may be billable. To run the application we need a new Confluent Cloud environment, Kafka cluster, topics, API keys, as well as resources that have hourly charges like connectors. Also MongoDB Atlas with mongo version 5.0 may be chargeable under dedicated resources. To avoid unexpected charges, carefully evaluate the cost of resources before you start. After you are done running the application carefully destroy/pause all chargeable resources to avoid accruing hourly charges for services and verify that they have been deleted/paused.


## Reference:

1. [Kafka producer script](https://github.com/confluentinc/confluent-kafka-python)
2. [Confluent cloud setup](https://github.com/AskMeiPaaS/iiot-hybrid-with-mongodb-confluent)
3. [Realm http API setup](https://docs.mongodb.com/realm/services/http/)
4. [Push notification setup](https://docs.mongodb.com/realm/services/push-notifications/)
5. [MongoDB Trigger](https://docs.mongodb.com/realm/triggers/database-triggers/)
6. [Time series collection](https://docs.mongodb.com/manual/core/timeseries-collections/)
7. [Geospatial queries](https://docs.mongodb.com/manual/geospatial-queries/)
