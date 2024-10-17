import fetch from 'node-fetch';
import configJson from '../config.json' with { type: 'json' };
import https from 'https';

const serverId = configJson.serverID; 
const apiToken = configJson.craftyToken; 

async function restartMinecraftServer(){
    const apiUrl = `https://${configJson.serverIP}:${configJson.serverPort}/api/v2/servers/${serverId}/action/restart_server`;

    const agent = new https.Agent({
        rejectUnauthorized: false 
    });

    // Send the API request to Crafty to restart the server
    try {
        const response = await fetch(apiUrl, {
            method: 'POST',
            headers: {
                'Authorization': `Bearer ${apiToken}`,
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
    const apiUrl = `https://${configJson.serverIP}:${configJson.serverPort}/api/v2/servers/${serverId}/stdin`;
    
    const agent = new https.Agent({
        rejectUnauthorized: false 
    });

    try {
        const response = await fetch(apiUrl, {
            method: 'POST',
            headers: {
                'Content-Type': 'text/plain', 
                'Authorization': `Bearer ${apiToken}` 
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


export {restartMinecraftServer, sendCommandToServer}