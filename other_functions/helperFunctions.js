import { Client, Collection, GatewayIntentBits, EmbedBuilder, Partials, Embed} from 'discord.js';
import fetch from 'node-fetch';
import {deleteEntryByUserID, } from './whitelistDatabaseFuncs.js'

function sleep(ms) {
    return new Promise(resolve => setTimeout(resolve, ms));
  }

function embedMaker({ colorHex, title, description, footer = {}, author = {} }) {
    console.log(colorHex);
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

//check whitelistDB and update names



export { embedMaker, isMcUsernameReal, getMinecraftNameByUUID};
