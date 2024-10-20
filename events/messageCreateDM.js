const { Events } = require('discord.js');
const configJson = require('../config.json');
const { getTicketByAuthor, insertTicket } = require('../other_functions/ticketDatabaseFuncs.js');
const { embedMaker } = require('../other_functions/helperFunctions.js');



module.exports = {
    name: Events.MessageCreate,
    once: false,
    async execute(client, message) {
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
                  title: `Message Received`, 
                  description: message.content,
                  footer: {
                    text: message.author.globalName
                  }
                })]})
      
      
              } else if(activeThread) {
      
                const thread = await client.channels.fetch(activeThread.threadChannelID);
      
                thread.send({embeds: [embedMaker({
                  colorHex: 0x32CD32, 
                  title: `Message Received`, 
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
    }

}