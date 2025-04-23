const { Client, Collection, GatewayIntentBits, EmbedBuilder, Partials, Embed } = require('discord.js');
const { deleteEntryByUserID, getUserByDiscordID, getUserByMinecraftUsername } = require('./whitelistDatabaseFuncs');

function sleep(ms) {
    return new Promise(resolve => setTimeout(resolve, ms));
  }

function embedMaker({ colorHex, title, description, footer = {}, author = {} }) {
    const newEmbed = new EmbedBuilder()
        //Required
        .setColor(colorHex)
        .setTitle(title)
        .setDescription(description);

    //Optional
    if (footer.text || footer.iconURL) {
        newEmbed.setFooter({ text: footer.text || undefined, iconURL: footer.iconURL || undefined });
    }

    if (author.name || author.iconURL || author.clickableURL) {
        newEmbed.setAuthor({ 
            name: author.name || undefined, 
            iconURL: author.iconURL || undefined, 
            url: author.clickableURL || undefined 
        });
    }

    return newEmbed;
}



async function isMcUsernameReal(username){
    const fetch = (await import('node-fetch')).default;
    try {
        const response = await fetch(`https://api.mojang.com/users/profiles/minecraft/${username}`);

        //Invalid Username
        if (response.status === 204){
            return 204;


        }
        // Valid username
        else if(response.status === 200){
            const data = await response.json();
            return data;
        } 
        //Other
        else{
            return response.status
        }
    } catch (err){
        console.log('Error while calling Mojan API: ', err);
        return 0;
    }
}

async function getMinecraftNameByUUID(mcUUID){
    const fetch = (await import('node-fetch')).default;
    try {
        const response = await fetch(`https://sessionserver.mojang.com/session/minecraft/profile/${mcUUID}`);

        //Invalid Username
        if (response.status === 204){
            return 204;


        }
        // Valid username
        else if(response.status === 200){
            const data = await response.json();
            return data;
        } 
        //Other
        else{
            return response.status
        }
    } catch (err){
        console.log('Error while calling Mojan API: ', err);
        return 0;
    }
}

async function getMinecraftNameByDiscordID(id) {
    const user = await getUserByDiscordID(id);
    return user.username;
}

async function isBanned(id) {
    const user = await getUserByDiscordID(id);
    return user.bannedAt > 0;
}

module.exports = { embedMaker, isMcUsernameReal, getMinecraftNameByUUID, getMinecraftNameByDiscordID, isBanned};
