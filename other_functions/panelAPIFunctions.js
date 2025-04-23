const fs = require('fs');
const path = require('path');
const https = require('https');
const { pannelToken, serverID, serverNo } = require('../config.json');
const { sign } = require('crypto');
const { pterosocket } = require('pterosocket')

const origin = "https://panel.createcivilization.com";

const socket = new pterosocket(origin, pannelToken, serverNo);

socket.on("auth_sucess", ()=>{
    console.log("Connected to websocket successfully")
})

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
    socket.writeCommand(commandString);
    return await awaitForMessage("Server")
}

async function awaitForMessage(expectedString) {
    let toReturn = formatSystemMessage(await findMessage(expectedString));

    if (toReturn.includes("see below for error")) {
        const extra = formatSystemMessage(await findMessage("[HERE]"));
        toReturn += "\n" + extra;
    }

    return toReturn;
}

function findMessage(expectedString, timeout = 0) {
    return new Promise((resolve, reject) => {
        if (timeout >= 20) return resolve("No return");

        const listener = (output) => {
            if (output.includes(expectedString)) {
                resolve(output);
            } else {
                findMessage(expectedString, timeout + 1)
                    .then(resolve)
                    .catch(reject);
            }
            socket.off("console_output", listener)
        };

        socket.on("console_output", listener);

        setTimeout(() => {
            socket.off("console_output", listener);
            resolve("No return (timed out)");
        }, 1000);
    });
}

function formatSystemMessage(string) {
    return string.replace(/^.*?\MinecraftServer\]:\s*/, "");
}


module.exports = {restartMinecraftServer, sendCommandToServer, formatSystemMessage}