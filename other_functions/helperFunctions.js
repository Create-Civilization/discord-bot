const { EmbedBuilder } = require('discord.js');
const { getUserByDiscordID } = require('./whitelistDatabaseFuncs');
const configJson = require('../config.json')
const allowedRoleIds = configJson.allowedRoleIds;

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

async function requireAllowedId(roleCache, toRun) {
    if(!configJson.adminRolesIDS.some(roleId => roleCache.has(roleId))){
        return interaction.reply({
            content: 'You do not have permission to run this',
            ephemeral: true
        })
    }
    await toRun()
}

async function requireWhitelistEntry(userId, toRun) {
    const dbObject = await getUserByDiscordID(userId)

    if(!dbObject){
        return interaction.editReply({
            content: "This user has no whitelist entries",
            ephemeral: true
        })
    }
    toRun(dbObject)
}

async function log(logData, client) {
    const Logchannel = await client.channels.cache.get(configJson.logChannelID);

    try {
        await Logchannel.send(logData)
    } catch(err) {
        console.log(`There was an error logging: ${logData}`)
        console.log(err)
    }

}

async function createLogEmbed(goodOrBad, title, description, client) {
    const guild = await client.guilds.fetch(configJson.guildID);
    const embed = embedMaker(
        {
            colorHex: goodOrBad ? 0x32CD32 : 0xFF3024,
            title: title,
            description: description,
            footer: {
                text: `${guild.name} | ${guild.id}`,
                iconURL: guild.iconURL({dynamic: true}) || undefined
            },
            author: {
                name: client.user.username,
                iconURL: client.user.avatarURL({dynamic: true}) || undefined
            },
        }
    );
    return embed;
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


module.exports = {
    requireAllowedId,
    embedMaker,
    isMcUsernameReal,
    getMinecraftNameByUUID,
    getMinecraftNameByDiscordID,
    log,
    createLogEmbed,
    requireWhitelistEntry
};
