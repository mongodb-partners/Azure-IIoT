const express = require('express')
const app = express()
const Client = require('azure-iot-device').Client;
const Message = require('azure-iot-device').Message;
const Protocol = require('azure-iot-device-mqtt').Mqtt;

const bodyParser = require('body-parser');
const DATA = require('./psudo_data.json');

const PORT = 8080

app.use(express.static(__dirname + '/public'));
app.use(bodyParser.urlencoded({ extended: true })); 

function createMessages() {
    high = 2003
    low = 0
    data_position = Math.floor(Math.random() * (high - low) + low)
    console.log("Picking data from pos:" + data_position)
    return DATA[data_position]
}

function sendMessages(payload, client_iot) {vha
    // Create message
    var message = new Message(payload);
    console.log(message.toString())
    console.log('Sending message: ' + message.toString());
    // Send data to IoT Hub
    client_iot.sendEvent(message, function (err) {
        if (err) {
            console.log("ERROR!!")
            console.error('Error :' + err);
        } else {
            console.log('Message sent to Azure IoT Hub');
        }
    });  
}

app.get('/', (req, res) => {
    res.render('connectedPage')
})

app.post('/connect', (req, res) => {
    console.log("Request Hostname" ,req.body.host)
    connectionData = req.body
    const connectionString = connectionData.connectionString 

    // Initialize the IoT client
    var client_iot = Client.fromConnectionString(connectionString, Protocol);
    // Keep sending data 
    var total_calls = 3
    var counter = 0
    var time_out = 5000
    
    // Send random data in intervals
    var dataTimer = setInterval(() => {
        message = createMessages()
        message = JSON.stringify(message)
        sendMessages(message, client_iot)

        if (++counter === total_calls) {
            clearInterval(dataTimer);
        }     
    }, time_out);

    setTimeout(() => {
        res.redirect('/')        
    }, time_out * total_calls + 1);
})

app.listen(PORT, () => {
    console.log(`Example app listening on PORT ${PORT}`)
})