import logging
import pymongo
import azure.functions as func
import datetime

def main(req: func.HttpRequest) -> func.HttpResponse:
    logging.info('Python HTTP trigger function processed a request.')
    # Link to Atlas Cluster 
    client = pymongo.MongoClient('<mongodb-connection-string>')
    db = client["IOT"]
    json_data = req.get_json()[0]
    json_data['ts'] = datetime.datetime.utcnow()
    json_data['device_id'] = "d001"
    db["Sensors_TS"].insert_one(json_data)

    return func.HttpResponse(f"This HTTP triggered function executed successfully.")