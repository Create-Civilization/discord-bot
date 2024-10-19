import {Events} from 'discord.js';
import {getUserByDiscordID, deleteEntryByUserID} from './other_functions/whitelistDatabaseFuncs.js';
import {getMinecraftNameByUUID} from './other_functions/helperFunctions.js';
import { sendCommandToServer } from './other_functions/craftyAPIfuncs.js'

export default {
    name: Events.GuildMemberRemove,
    once: false,
    async execute(member) {
        const databaseEntry = await getUserByDiscordID(member.id);
        if (!databaseEntry){
      
        }else{
          const mcUUID = databaseEntry.playerUUID
          const discordID = databaseEntry.discordID
          //Unwhitelist command
          const mcNameCorrect = await getMinecraftNameByUUID(mcUUID)
          await sendCommandToServer(`whitelist remove ${mcNameCorrect.name}`)
          await deleteEntryByUserID(discordID);
        }
    }
}