import { Client, Collection, GatewayIntentBits, EmbedBuilder, Partials, Embed, Events} from 'discord.js';
import configJson from './config.json' with { type: 'json' };
import fs, { accessSync } from 'fs';
import { checkCrashTask, updateStatusTask } from './other_functions/tasks.js';
import {initDatabase, getTicketByAuthor, insertTicket} from './other_functions/ticketDatabaseFuncs.js'
import {getUserByUUID, getUserByDiscordID, addUserToWhitelist, deleteEntryByUserID, initWhiteListDatabase} from './other_functions/whitelistDatabaseFuncs.js';
import { embedMaker, isMcUsernameReal, getMinecraftNameByUUID} from './other_functions/helperFunctions.js';
import { sendCommandToServer } from './other_functions/craftyAPIfuncs.js'
import whitelist from './commands/server_commands/whitelist.js';
const ticketsdb = initDatabase();
const whitelistdb = initWhiteListDatabase();



const client = new Client({ 
  intents: [
    GatewayIntentBits.Guilds, 
    GatewayIntentBits.GuildMembers, 
    GatewayIntentBits.GuildMessages,
    GatewayIntentBits.DirectMessages, // To handle DMs
  ],
  partials: [Partials.Channel] // To access uncached DM channels
});

client.commands = new Collection();

const token = configJson.token;

client.on('ready', () => {
  console.log(`Logged in as ${client.user.tag}!`);

  setInterval(async () => {
      await updateStatusTask(client);
  }, 5000);

  setInterval(async () => {
    await checkCrashTask(client);
}, 5 * 60 * 1000);
});



let modules = ["fun_commands", "moderation_commands", "server_commands"];

modules.forEach(async (module) => {
  fs.readdir(`./commands/${module}`, async (err, files) => {
    if (err) {
      throw new Error("Missing Folder Of Commands! Example: Commands/<Folder>/<Command>.js");
    }

    for (const file of files) {
      if (!file.endsWith(".js")) continue;
      
      try {
        const { default: command } = await import(`./commands/${module}/${file}`);
        console.log(`${command.name} loaded`);
        if (command.name) client.commands.set(command.name, command);
      } catch (err) {
        console.error(`Failed to load command ${file}:`, err);
      }
    }
  });
});


//Handle Tickets
client.on('messageCreate',  async (message) => {
  if (message.author.id === client.user.id) return;
  if(message.channel.type == 1){
    if(configJson.helpTicketChannelID){
      const helpTicketChannel = client.channels.cache.get(configJson.helpTicketChannelID)

      try {
        const activeThread = await getTicketByAuthor(message.author.id);
        if (!activeThread){

          const sentMessage = await helpTicketChannel.send({embeds: [embedMaker({
            colorHex: 0x32CD32, 
            title: `${message.author.globalName}'s Help Ticket`, 
            description: "waiting for thread", 
            footer: {
              text:`${message.author.globalName} | ${message.author.id}`,
              iconURL: message.author.displayAvatarURL(),
            },
          })]})

          const theThread = await helpTicketChannel.threads.create({
            name: `${message.author.username}'s Help Ticket`,
            startMessage: sentMessage.id,
            reason: 'Help Ticket',
          })
          insertTicket(message.author.id, theThread.id, sentMessage.id)


          sentMessage.edit({embeds: [embedMaker({
            colorHex: 0x32CD32, 
            title: `${message.author.globalName}'s Help Ticket`, 
            description: `<#${theThread.id}>`, 
            footer: {
              text: `${message.author.globalName} | ${message.author.id}`,
              iconURL: message.author.displayAvatarURL()
            },
          })]})


          const threadChannel = client.channels.cache.get(theThread.id)
          threadChannel.send({embeds: [embedMaker({
            colorHex: 0xbfbfbf, 
            title: `A New Ticket Has Been Made`, 
            description: `To respond to this ticket use \`/reply\` every other message will be ignored. To close the ticket do \`/close\``
          })]})

          message.react('✅')

          threadChannel.send({embeds: [embedMaker({
            colorHex: 0x32CD32, 
            title: `Message Recived`, 
            description: message.content,
            footer: {
              text: message.author.globalName
            }
          })]})


        } else if(activeThread) {

          const thread = await client.channels.fetch(activeThread.threadChannelID);

          thread.send({embeds: [embedMaker({
            colorHex: 0x32CD32, 
            title: `Message Recived`, 
            description: message.content,
            footer: {
              text: message.author.globalName
            }
          })]})

          message.react('✅')
        }

      } catch(err) {
        console.log(err)
      }
    }else{
      message.channel.send("No Help Channel Setup. Let a server Admin know.")
    }
  }
}) 

// Command handling for slash commands (interaction)
client.on('interactionCreate', async interaction => {
  if(interaction.isChatInputCommand()){
    let command = client.commands.get(interaction.commandName);
    if (!command){return;}
    try{
      await command.run(client,interaction);
      return;
    } catch(err) {
      console.error(`Error using ${interaction.commandName}`);
			console.error(err);
    }
  }
  if (interaction.isButton()){
    const button_id = interaction.customId;
    console.log('we got here at least')
    fs.readdir('./commands/button_commands', async (err,files) =>{
      if (err) {
        throw new Error("Missing Folder Of Commands! Example: Commands/<Folder>/<Command>.js");
      }
      for (const file of files) {
        if (!file.endsWith(".js")) continue;
        
        try {
          const { default: buttonCommand } = await import(`./commands/button_commands/${file}`);          
          if (buttonCommand.customId === button_id) {
            await buttonCommand.run(client, interaction);
            console.log(`${buttonCommand.customId} button interaction executed.`);
            return;
          }
        } catch (err) {
          console.error(`Failed to load button command ${file}:`, err);
        }
      }
      
      console.log("No matching button command found.");
    });
  }
});

//Handle whitelistModal
client.on(Events.InteractionCreate, async interaction => {
  if (!interaction.isModalSubmit()) return;

  if (interaction.customId === 'whitelistModal') {
      // Get the data entered by the user
      const inGameName = interaction.fields.getTextInputValue('inGameName');
      const reason = interaction.fields.getTextInputValue('reason');
      const mojangAPI = await isMcUsernameReal(inGameName)
      const guild = interaction.guild;
      const whitelistRole = guild.roles.cache.get(configJson.whitelistedRoleID)
      
      await interaction.deferReply({ephemeral: true})

      if(mojangAPI == 404) {
        return interaction.editReply({
          content: 'No account found with that usernmame. Please double check it',
          ephemeral: true,
        })
      } else if (mojangAPI && typeof mojangAPI === 'object') {
        //Valid Username
        try{
          await addUserToWhitelist(mojangAPI.id, interaction.user.id, mojangAPI.name)
          await sendCommandToServer(`whitelist add ${mojangAPI.name}`)
          const member = await guild.members.fetch(interaction.user.id);
          await member.roles.add(whitelistRole);
        } catch(err){
          console.log(`There was an error running addUserToWhitelist ${err}`)
          return interaction.editReply({
            content: `A fatal error occured if this happens multiple times make a support ticket`,
            ephemeral: true
          })
        }

        return interaction.editReply({
          content: `Added ${mojangAPI.name} to the whitelist. If you added the wrong username or want to be removed to /remove_whitelist`,
          ephemeral: true,
        })
     }
     else {
      console.log(JSON.stringify(mojangAPI))
      return interaction.editReply({
          content: 'Unknown Error Occured. Try again later',
          ephemeral: true,
      });
  }
  }
});


//Remove member from whitelist
client.on('guildMemberRemove', async (member) => {
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

})


//Error catching
client.on('error', (error) => {
  console.error('Client error:', error);
});

process.on('unhandledRejection', (error) => {
  console.error('Unhandled promise rejection:', error);
});








// Log in to Discord using the token from the config
client.login(token);
