# MongoDB <> Azure IIOT Solution

MongoDB Atlas and Azure IIOT solution for building a smart factory architecture.


## Intro

Imagine a manufacturing facility that has sensors installed in their CNC machines measuring parameters such as temperature, torque, rotational speed and tool wear via vibration. These sensors are sending data to a sensor gateway which is connected to Azure IoT Edge and sending sensor data through MQTT protocol. This data is then transmitted to Azure IoT Hub where the IoT Edge is registered as an end device.

Once we have the data in the IoT Hub, we can utilize Azure Stream Analytics to filter the data so that only relevant information flows into the MongoDB Atlas Cluster. The connection between Stream Analytics and MongoDB is done via an Azure Function.

This filtered sensor data inside MongoDB is used for following purposes:
To provide data for machine learning model that will predict the root cause of machine failure based on sensor data
To act as a data store for prediction results that are utilized by Azure Synapse Analytics and Power BI for analytical queries
To store the trained machine learning model as a JSON document in a collection

The overall architecture is shown below


## Architecture
![image](https://user-images.githubusercontent.com/114057324/214538847-f2665d63-1617-4a99-ba8c-b0974403f0e7.png)

## Steps to replicate 

#### 1. Failure detection ML model 
- A ML model can be trained to detect if a failure occurs and predict its type. This can help in reducing the time to diagnose the machine and finding its root cause.
- Refer this [notebook](https://github.com/mongodb-partners/Azure-IIoT/blob/main/ml-model/fraud-detection.ipynb) to train your ML model.
- Once the model is trained, you can use `pickle` module to convert the model as binary and store it in MongoDB. 
```
from bson import Binary
import pickle

model_out = {"tag":"DecisionTree", "model_ckpt":Binary(pickle.dumps(decision))}
model_coll = db['models']
res = model_coll.insert_one(model_out)

print(res.acknowledged)
```
- This model can then be used (as shown [here](https://github.com/mongodb-partners/Azure-IIoT/blob/main/azure-functions/fault_prediction.py#L61)) to make predictions using a simple Azure function.

#### 2. Azure IoT 

- Setup IoT Hub in your Azure account, following this [link](https://learn.microsoft.com/en-us/azure/iot-hub/iot-hub-create-through-portal#create-an-iot-hub).

- Register a new [device](https://learn.microsoft.com/en-us/azure/iot-hub/iot-hub-create-through-portal#register-a-new-device-in-the-iot-hub)

- Once the device is registered successfully, copy its primary connection string.


#### 3. Web Service data simulator

- Clone the github repo in your local, and go to web-app folder.

- Install dependencies using `npm install`.

- Start the service using `node app.js`, and input the primary connection string copied in previous step to establish connection.

![image](https://user-images.githubusercontent.com/114057324/214539115-1b2a2eb7-4092-495e-89f9-23151924b536.png)

#### 4. Stream Analytics
- Once the data reaches IoT Hub, it can then be served to Stream Analytics for filtering and pushing to MongoDB Atlas.

- Follow this [step](https://learn.microsoft.com/en-us/azure/stream-analytics/stream-analytics-quick-create-portal#create-a-stream-analytics-job) for setting up a Stream Analytics job.

- Add IoT Hub as [input](https://learn.microsoft.com/en-us/azure/stream-analytics/stream-analytics-quick-create-portal#configure-job-input) for the job. 

- For output select Azure functions. (See Step 4 below for setting up azure function)

#### 5. Azure functions

- This setup requires 2 Azure functions;
  - To push data to MongoDB Atlas from Stream Analytics. Function available [here](https://github.com/mongodb-partners/Azure-IIoT/blob/main/azure-functions/telemetry_data.py).
  - Running Machine Learning model to get failure inferences. Function available [here](https://github.com/mongodb-partners/Azure-IIoT/blob/main/azure-functions/fault_prediction.py).          

     Follow [this](https://learn.microsoft.com/en-us/azure/azure-functions/create-first-function-cli-python) guide to set-up both the functions.



#### 6. Realm - Device Sync Mobile Application 
- Android application helps in keeping the user notified about any failures that may happen in the factory.

Follow this [guide](https://www.mongodb.com/developer/products/realm/introduction-realm-sdk-android/) to setup the [mobile application](https://github.com/mongodb-partners/Azure-IIoT/blob/main/android-app/). 

#### 7. Charts
- The real time data can be analysed using various charts available under MongoDB Charts.

- Refer this [link](https://www.mongodb.com/docs/charts/welcome-experience/) for setting up one for yourself and you can build awesome charts like this.

![image](https://user-images.githubusercontent.com/114057324/214540397-b56dd2fa-f295-4ec7-8702-d45bf056223a.png)


## Conclusion
This gives a working template to setup an end-to-end flow for smart factory, to analyse its telemetric data using MongoDB Atlas and Azure IoT Services.

For any further information, please contact partners@mongodb.com
