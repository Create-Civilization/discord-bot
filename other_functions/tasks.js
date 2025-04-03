const { Client, Collection, GatewayIntentBits, ActivityType } = require('discord.js');
const https = require('https');
const configJson = require('../config.json');
const { getAllTickets, getTicketsOlderThan, deleteTicketByTicketID } = require('./ticketDatabaseFuncs.js');
const {embedMaker, getMinecraftNameByUUID} = require('./helperFunctions.js')
const exp = require('constants');
const { getAllWhitelistData, setUserUsername } = require('./whitelistDatabaseFuncs.js');
const { Console } = require('console');



//I will be so real I wrote this and idk why I did it like this. I am sorry but I am not going to fix it. I am just not touching this.
// BOT STATUS (I HATE COMMENTS)
const setBotStatus = async (client, onlinePlayers, maxPlayers) => {
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
const fetchServerStats = async () => {
    const fetch = (await import('node-fetch')).default;
    const apiUrl = 'https://api.mcsrvstat.us/3/play.createcivilization.com'; // New API endpoint

    try {
        const response = await fetch(apiUrl, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json'
            }
        });

        if (!response.ok) {
            throw new Error(`Error: ${response.status}`);
        }

        const result = await response.json();
        return result; // Return full server stats from the MCSrvStat API
    } catch (error) {
        console.error('Failed to fetch server stats:', error.message);
        return null; // Return null in case of an error
    }
};
//READABLITY MY 

const updateStatusTask = async (client) => {
    try {
        const stats = await fetchServerStats();

        if (!stats) {
            console.log('No server stats returned.');
            return false;
        }

        if (stats.debug.online === false) {
            console.log('Server Is Offline');
            return false; 
        }

        const onlinePlayers = stats.players ? stats.players.online : 0; 
        const maxPlayers = stats.players ? stats.players.max : 0; 

        const returnVar = await setBotStatus(client, onlinePlayers, maxPlayers);
        return returnVar;
    } catch (error) {
        console.error('Error in updateStatusTask:', error);
        return false; 
    }
};


const checkStaleTickets = async (client) => {
    try {
        const expiredTickets = await getTicketsOlderThan(configJson.ticketExpiryTimeSeconds);

        if (expiredTickets.length > 0) {
            for (let i = 0; i < expiredTickets.length; i++) {
                try {
                    const ticket = expiredTickets[i];
                    const threadOwner = await client.users.fetch(ticket.authorID);
                    const guild = client.guilds.cache.get(configJson.guildID);
                    const helpChannelOBJ = client.channels.cache.get(configJson.helpTicketChannelID);
                    const fetchedMessage = await helpChannelOBJ.messages.fetch(ticket.threadChannelID);
                    const thread = await client.channels.fetch(ticket.threadChannelID);

                    await deleteTicketByTicketID(ticket.id);


                    let newEmbed = embedMaker({
                        colorHex: 0xD70040,
                        title: `Ticket Closed`,
                        description: `Ticket has been closed by <@${client.user.id}>. Reason: Ticket Expired After \`${(configJson.ticketExpiryTimeSeconds /60/60/24)}\` days. If this ticket is still relevant, please make a new one.`,
                        footer: {
                            text: `${client.user.username || 'Bot'}`,  
                            iconURL: client.user.avatarURL({ dynamic: true }) || undefined  
                        }
                    });
                    await fetchedMessage.edit({ embeds: [newEmbed] });

                    newEmbed = embedMaker({
                        colorHex: 0xD70040,
                        title: `Ticket Closed Due To Expiry`,
                        description: `Ticket has been closed by <@${client.user.id}>. Reason: Ticket Expired After \`${(configJson.ticketExpiryTimeSeconds /60/60/24)}\` days. If this ticket is still relevant, please make a new one.`,
                        footer: {
                            text: `${guild.name} | ${guild.id}`,
                            iconURL: guild.iconURL({ dynamic: true }) || undefined
                        },
                        author: {
                            name: `${client.user.username || 'Bot'}`,
                            iconURL: client.user.avatarURL({ dynamic: true }) || undefined
                        }
                    });
                    await threadOwner.send({ embeds: [newEmbed] });

                    await thread.setLocked(true);
                    await thread.setArchived(true);

                } catch (error) {
                    console.error('Error in processing ticket:', error);
                }
            }
        } else {
            console.log("No expired tickets found.");
        }
    } catch (error) {
        console.error('Error in checkStaleTickets:', error);
    }
};


const checkForIgnChanges = async (client) => {
    try {
        const whitelist = await getAllWhitelistData();
        const BATCH_SIZE = 5;
        const DELAY_BETWEEN_BATCHES = 1000;
        
        for (let i = 0; i < whitelist.length; i += BATCH_SIZE) {
            const batch = whitelist.slice(i, i + BATCH_SIZE);
            
            const promises = batch.map(async (element) => {
                try {
                    const username = await getMinecraftNameByUUID(element.playerUUID);
                    
                    if (username.name !== element.username) {
                        console.log(`Username changed for ${element.username} (${element.playerUUID}) -> ${username.name}`);
                        await setUserUsername('playerUUID', element.playerUUID, username.name);
                    } else {
                        console.log(`No changes found for ${element.username}`);
                    }
                } catch (error) {
                    console.error(`Error checking username for ${element.playerUUID}:`, error.message);
                }
            });
            
            await Promise.all(promises);
            if (i + BATCH_SIZE < whitelist.length) {
                await new Promise(resolve => setTimeout(resolve, DELAY_BETWEEN_BATCHES));
            }
        }
    } catch (error) {
        console.error('Error in checkForIgnChanges:', error);
    }
};







module.exports = {setBotStatus, fetchServerStats, updateStatusTask, checkStaleTickets, checkForIgnChanges};