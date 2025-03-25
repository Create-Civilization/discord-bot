const fs = require('fs');
const path = require('path');
const https = require('https');
const { pannelToken, serverID, serverIP, serverPort } = require('../config.json');
const { sign } = require('crypto');

async function restartMinecraftServer(){
    const fetch = (await import('node-fetch')).default;
    const apiUrl = `https://panel.createcivilization.com/api/client/servers/${serverID}/power`;

    const agent = new https.Agent({
        rejectUnauthorized: false 
    });

    try {
        const response = await fetch(apiUrl, {
            method: 'POST',
            headers: {
                'Authorization': `Bearer ${pannelToken}`,
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                signal: 'restart'
            }),
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

    const apiUrl = `https://panel.createcivilization.com/api/client/servers/${serverID}/command`;

    const agent = new https.Agent({
        rejectUnauthorized: false,
    });

    try {
        const response = await fetch(apiUrl, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${pannelToken}`,
                'Accept': 'Application/vnd.pterodactyl.v1+json',
            },
            body: JSON.stringify({
                command: commandString,
            }),
            agent,
        });

        if (!response.ok) {
            const errorDetails = await response.text();
            throw new Error(`Error: ${response.status}, Details: ${errorDetails || 'No details provided'}`);
        }

        const resultText = await response.text();
        return resultText ? JSON.parse(resultText) : { message: 'Command sent successfully, no response returned.' };

    } catch (error) {
        console.error('Error sending command to server:', error.message);
        throw error;
    }
}



module.exports = {restartMinecraftServer, sendCommandToServer}