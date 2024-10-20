const { Events } = require('discord.js');
const { checkCrashTask, updateStatusTask } = require('../other_functions/tasks.js');

module.exports = {
    name: Events.ClientReady,
    once: false,
    async execute(client) {
        
        setInterval(async () => {
            client.isServerAlive = await updateStatusTask(client)
            console.log(client.isServerAlive)
        }, 5000);
      
        setInterval(async () => {
          await checkCrashTask(client);
      }, 5 * 60 * 1000);
    }
}