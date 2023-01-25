import logging
import pymongo
import pickle
import pandas as pd
import azure.functions as func
import datetime
from sklearn import tree

def tranform(data):
    try:
        del data['device_id']
        df_inputdata = pd.DataFrame([data])
        # Convert all columns as float
        df_inputdata[['Air temperature','Process temperature','Rotational speed','Torque','Tool wear']] = df_inputdata[['Air temperature','Process temperature','Rotational speed','Torque','Tool wear']].astype(float)
        df_inputdata = df_inputdata[['Type', 'Air temperature', 'Process temperature', 'Rotational speed','Torque', 'Tool wear']]
        return df_inputdata 
    except Exception as e:
        print("EXCEPTION", e)

def find_latest(client):
    db = client['IOT']
    collection = db['Sensors_TS']
    # Find the latest document inserted
    result = collection.find({"device_id" : "d001"}, projection={'_id': 0, 'ts': 0}, sort=[('_id', -1)], limit=1)
    return next(result) 

def add_latest_failure(client, prediction, device_id):
    db = client['IOT']
    collection = db['Failure']
    # Different failure categories
    failures = ['No Failure', 'Power Failure', 'Tool Wear Failure','Overstrain Failure', 'Random Failures','Heat Dissipation Failure']
    # Below document will be inserted in failure collection.
    failure_doc = {
        "device_id" : device_id,
        "failure" : failures[prediction],
        "ts": datetime.datetime.utcnow(),
        "partition_key" : "sensor"
    }
    # Insert into MongoDB 
    doc = collection.insert_one(failure_doc)
    return doc


def main(req: func.HttpRequest) -> func.HttpResponse:
    try:
        logging.info('Python HTTP trigger function processed a request.')
        print("HTTP Trigger")

        client = pymongo.MongoClient("<mongodb connection string>") # Update with your connection string 
        db = client['<database>']
        coll = db['<collection for storing model>']

        # Find data from model dump
        model_out = coll.find_one({"tag": "DecisionTree"})
        model_bin = pickle.loads(model_out['model_ckpt'])

        data = find_latest(client)
        device_id = data['device_id']
        print(data)
        df = tranform(data)
        prediction = model_bin.predict(df)
        print("pred {}".format(prediction))
        # If prediction encounters a failure add to failure collection, otherwise skip insertion. 
        if prediction[0] > 0:
            add_latest_failure(client, prediction[0], device_id)
            return func.HttpResponse("Inserted latest failure")
        else:
            return func.HttpResponse("No failure")
    except Exception as e:
        return func.HttpResponse(f"{e}") 
