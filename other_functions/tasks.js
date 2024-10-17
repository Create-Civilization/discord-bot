import { Client, Collection, GatewayIntentBits, ActivityType } from 'discord.js';
import fetch from 'node-fetch';
import https from 'https';
import configJson from '../config.json' with { type: 'json' };



//I will be so real I wrote this and idk why I did it like this. I am sorry but I am not going to fix it. I am just not touching this.
// BOT STATUS (I HATE COMMENTS)
export const setBotStatus = async (client, onlinePlayers, maxPlayers) => {
    try {
        if (maxPlayers == 0) {
            await client.user.setPresence({
                activities: [{ name: `Server Offline`, type: ActivityType.Custom }],
                status: 'dnd',
            });
            console.log(`Bot status updated! Server Offline`);
            return false;

        } else {
            await client.user.setPresence({
                activities: [{ name: `${onlinePlayers}/${maxPlayers} players`, type: ActivityType.Watching }],
                status: 'online',
            });
            console.log(`Bot status updated! ${onlinePlayers}/${maxPlayers} players online`);
            return true;
        }
    } catch (error) {
        console.error('Error updating bot status:', error);
        return false;
    }
};

// GET EM STATS
export const fetchServerStats = async () => {
    const apiToken = configJson.craftyToken; 
    const serverId = configJson.serverID; 
    const apiUrl = `https://${configJson.serverIP}:${configJson.serverPort}/api/v2/servers/${serverId}/stats`; 

    try {
        const response = await fetch(apiUrl, {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${apiToken}`,
                'Content-Type': 'application/json'
            },
            agent: new https.Agent({ rejectUnauthorized: false }) // YUCKY SSL
        });

        if (!response.ok) {
            throw new Error(`Error: ${response.status}`);
        }

        const result = await response.json();
        return result.data; 
    } catch (error) {
        console.error('Error fetching server stats:', error);
        return null; 
    }
};
//READABLITY MY ASS

// logs
export const fetchServerLogs = async (useFile = false, addColors = false, rawOutput = false, outputAsHTML = false) => {
    const apiToken = configJson.craftyToken;
    const serverId = configJson.serverID; 
    const apiUrl = `https://${configJson.serverIP}:${configJson.serverPort}/api/v2/servers/${serverId}/logs`;

    const params = new URLSearchParams({
        file: useFile,
        colors: addColors,
        raw: rawOutput,
        html: outputAsHTML,
    });

    try {
        const response = await fetch(`${apiUrl}?${params}`, {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${apiToken}`,
                'Content-Type': 'application/json',
            },
            agent: new https.Agent({ rejectUnauthorized: false }), // YUCKY SSL
        });

        if (!response.ok) {
            throw new Error(`Error fetching logs: ${response.status}`);
        }

        const result = await response.json();
        return result.data;  // Return the logs data directly
    } catch (error) {
        console.error('Error fetching server logs:', error);
        return null; 
    }
};


//Read da name
export const restartServer = async (client) => {
    const apiToken = configJson.craftyToken; 
    const serverId = configJson.serverID; 
    const apiUrl = `https://${configJson.serverIP}:${configJson.serverPort}/api/v2/servers/${serverId}/action/restart_server`; 

    const agent = new https.Agent({ rejectUnauthorized: false }); 

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
            throw new Error(`Error restarting server: ${response.status}`);
        }

        const result = await response.json();


        if (result.status === 'ok') {
            client.channels.cache.get("1272992315069169704").send(':tada: VS CRASH DETECTED :tada: | :sparkles:  RESTARTING SERVER :sparkles:');
            console.log('Server restarted successfully!');

        } else {
            console.error('Failed to restart the server:', result);
        }
    } catch (error) {
        console.error('Error restarting server:', error);
    }
};


export const checkCrashTask = async (client) => {
    try {
        const serverLogs = await fetchServerLogs(true, true, false, false);

        if (Array.isArray(serverLogs)) {
            const warningMessage = '[Burger Factory/]: Too many game frames in the game frame queue. Is the physics stage broken?';
            
            
            const warnings = serverLogs.filter(log => log.includes(warningMessage));
            if (warnings.length > 0) {
                console.log('Warning found in server logs');
                await restartServer(client); 
                return true; 
            } else {
                console.log('No warnings found in server logs');
            }
        } else {
            console.error('Server logs is not an array or is undefined:', serverLogs);
        }
    } catch (error) {
        console.error('Error in periodicTasks:', error);
    }

    return false; 
};

export const updateStatusTask = async (client) => {
    try {
        const stats = await fetchServerStats(); 
        if (stats) {
            const onlinePlayers = stats.online;
            const maxPlayers = stats.max;
            await setBotStatus(client, onlinePlayers, maxPlayers);
        }
    } catch(error) {
        console.error('Error in updateStatusTask:', error);
    }
};







