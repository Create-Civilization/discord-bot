const fs = require('fs');
const path = require('path');
const https = require('https');
const { craftyToken, serverID, serverIP, serverPort } = require('../config.json');

async function restartMinecraftServer(){
    const fetch = (await import('node-fetch')).default;
    const apiUrl = `https://${serverIP}:${serverPort}/api/v2/servers/${serverID}/action/restart_server`;

    const agent = new https.Agent({
        rejectUnauthorized: false 
    });

    // Send the API request to Crafty to restart the server
    try {
        const response = await fetch(apiUrl, {
            method: 'POST',
            headers: {
                'Authorization': `Bearer ${craftyToken}`,
                'Content-Type': 'application/json'
            },
            agent 
        });

        if (!response.ok) {
            throw new Error(`Error: ${response.status}`);
        }

        return await response.json();

    } 
    catch (error) {
            console.error('Error restarting the server in the funcs', error);
    }
}


async function sendCommandToServer(commandString) {
    const fetch = (await import('node-fetch')).default;
    const apiUrl = `https://${serverIP}:${serverPort}/api/v2/servers/${serverID}/stdin`;
    
    const agent = new https.Agent({
        rejectUnauthorized: false 
    });

    try {
        const response = await fetch(apiUrl, {
            method: 'POST',
            headers: {
                'Content-Type': 'text/plain', 
                'Authorization': `Bearer ${craftyToken}` 
            },
            body: commandString,
            agent
        });

        if (!response.ok) {
            throw new Error(`Error: ${response.status}`);
        }

        const result = await response.json(); 
        return result;

    } catch (error) {
        console.error('Error sending command to server:', error);
        throw error;
    }
}


module.exports = {restartMinecraftServer, sendCommandToServer}